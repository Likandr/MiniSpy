package com.likandr.minispy;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.os.Handler;

import com.jaredrummler.android.processes.AndroidProcesses;
import com.jaredrummler.android.processes.models.AndroidAppProcess;

import java.util.ArrayList;
import java.util.List;

public class DetectLaunchAppRunnable implements Runnable {
    public static int DELAY = 1000;

    private Handler mHandler;
    private Context mContext;

    DetectLaunchAppRunnable(Context context) {
        this.mContext = context;
        this.mHandler = new Handler();
    }

    private List<String> oldList = new ArrayList<>();

    private void asdg() {
        List<String> newList = new ArrayList<>();
        List<AndroidAppProcess> processes = AndroidProcesses.getRunningAppProcesses();
        for (AndroidAppProcess process : processes) {
            try {
                PackageInfo packageInfo = process.getPackageInfo(mContext, 0);
                newList.add(packageInfo.packageName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

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

    @Override
    public void run() {
        asdg();
        mHandler.postDelayed(this, DELAY);
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
