package com.likandr.minispy;

import android.annotation.TargetApi;
import android.app.usage.UsageStats;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.os.Build;
import android.os.Handler;

import com.jaredrummler.android.processes.AndroidProcesses;
import com.jaredrummler.android.processes.models.AndroidAppProcess;

import java.util.ArrayList;
import java.util.List;

public class DetectLaunchAppRunnable implements Runnable {
    private static int DELAY = 1000;

    private Handler mHandler;
    private Context mContext;

    private List<String> oldList = new ArrayList<>();

    DetectLaunchAppRunnable(Context context) {
        this.mContext = context;
        this.mHandler = new Handler();
    }

    @Override public void run() {
        mainFunc();
        mHandler.postDelayed(this, DELAY);
    }

    private void mainFunc() {
        List<String> newList =
                android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ?
                getTargetList21() : getTargetList();

        List<String> newApp = new ArrayList<>();
        if (oldList.size() == 0) {
            oldList = newList;
        } else {
            newApp = getDiff(oldList, newList);
            oldList = newList;
        }

        if(!newApp.isEmpty()) {
            SharedPreferencesUtils.storeProcessList(newApp);
        }
    }

    private List<String> getTargetList() {
        List<String> result = new ArrayList<>();
        List<AndroidAppProcess> processes = AndroidProcesses.getRunningAppProcesses();
        for (AndroidAppProcess process : processes) {
            try {
                PackageInfo packageInfo = process.getPackageInfo(mContext, 0);
                result.add(packageInfo.packageName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    @TargetApi(21)
    private List<String> getTargetList21() {
        List<String> result = new ArrayList<>();
        List<UsageStats> processes = USUtils.getUsageStatsList(mContext);
        for (UsageStats process : processes) {
            result.add(process.getPackageName());
        }
        return result;
    }

    private List<String> getDiff(List<String> oldList, List<String> newList) {
        List<String> result = new ArrayList<>();
        if (oldList != null && newList != null && oldList.size() > 0 && newList.size() > 0) {
            for (String item1 : newList) {
                boolean isBingo = false;
                for (String item2 : oldList) {
                    if (item2.equals(item1)) {
                        isBingo = true;
                        break;
                    }
                }
                if (!isBingo) {
                    result.add(item1);
                }
            }
        }
        return result;
    }
}
