package com.likandr.minispy;

import android.annotation.TargetApi;
import android.app.usage.UsageStats;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.os.Build;
import android.os.Handler;

import com.jaredrummler.android.processes.AndroidProcesses;
import com.jaredrummler.android.processes.models.AndroidAppProcess;
import com.likandr.minispy.utils.SharedPreferencesUtils;

import java.util.ArrayList;
import java.util.List;

import static com.likandr.minispy.utils.USUtils.getUsageStatsList;

public class DetectLaunchAppRunnable implements Runnable {

    private static final String TAG = DetectLaunchAppRunnable.class.getSimpleName();
    private static int DELAY = 1000;

    private Handler mHandler;
    private Context mContext;

    private List<BigData> mSavedList = new ArrayList<>();
    private Long timeStart = 0L;

    DetectLaunchAppRunnable(Context context) {
        this.mContext = context;
        this.mHandler = new Handler();
    }

    @Override public void run() {
        mainFunc();
        mHandler.postDelayed(this, DELAY);
    }

    private void mainFunc() {
        timeStart = System.currentTimeMillis();

        mSavedList = android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N ?
                doResultList24() : doResultList();

        if(!mSavedList.isEmpty()) {
            SharedPreferencesUtils.setProcessList(mSavedList);
        }
    }

    //region doResult
    private List<BigData> doResultList() {
        List<BigData> result = new ArrayList<>();
        List<String> currentDataList = getCurrentList();

        if (currentDataList.isEmpty()) {
            return result;
        }

        for (String currentAppForeground : currentDataList) {
            if (!currentAppForeground.equals("")) {
                if (mSavedList.isEmpty()) {
                    SharedPreferencesUtils.clearList();
                    result.add(new BigData(currentAppForeground, getTime()));
                } else {
                    result = mSavedList;
                    boolean hasEquals = false;
                    for (int i = 0; i < mSavedList.size(); i++) {
                        if (mSavedList.get(i).getName().equals(currentAppForeground)) {
                            result.get(i).setTotal(mSavedList.get(i).getTotal() + getTime());
                            hasEquals = true;
                            break;
                        }
                    }
                    if (!hasEquals) {
                        result.add(new BigData(currentAppForeground, getTime()));
                    }
                }
            }
        }
        return result;
    }

    private List<BigData> doResultList24() {
        List<BigData> result = new ArrayList<>();
        List<BigData> currentDataList = getCurrentList24();

        if (currentDataList.isEmpty()) {
            return result;
        }

        if (mSavedList.isEmpty()) {
            SharedPreferencesUtils.clearList();
            result = currentDataList;
        } else {
            result = mSavedList;
            boolean hasEquals;
            for (BigData currentDataItem : currentDataList) {
                hasEquals = false;
                for (int i = 0; i < mSavedList.size(); i++) {
                    if (mSavedList.get(i).getName().equals(currentDataItem.getName())) {
                        result.get(i).setTotal(currentDataItem.getTotal());
                        hasEquals = true;
                        break;
                    }
                }
                if (!hasEquals) {
                    result.add(currentDataItem);
                }
            }
        }
        return result;
    }
    //endregion

    //region getCurrentData
    private List<String> getCurrentList() {
        List<String> result = new ArrayList<>();

        List<AndroidAppProcess> processes = AndroidProcesses.getRunningForegroundApps(mContext);
        for (AndroidAppProcess process : processes) {
            try {
                if (process.foreground) {
                    PackageInfo packageInfo = process.getPackageInfo(mContext, 0);
                    if (checkForNeeded(packageInfo.packageName))
                        result.add(packageInfo.packageName);
                        //Log.i(TAG, "-----" + packageInfo.packageName);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    @TargetApi(21)
    private List<BigData> getCurrentList24() {
        List<BigData> result = new ArrayList<>();

        List<UsageStats> processes = getUsageStatsList(mContext);
        for (UsageStats process : processes) {
            result.add(
                    new BigData(process.getPackageName(), (int) process.getTotalTimeInForeground()));
        }
        return result;
    }
    //endregion

    private boolean checkForNeeded(String str1) {
        List<String> asd = new ArrayList<>();
        //asd.add("com.android");
        //asd.add("com.google");

        for (String str2 : asd)
            if (str1.contains(str2))
                return false;

        return true;
    }

    private int getTime() {
        Long currentTime = System.currentTimeMillis();
        return (int) (currentTime - timeStart + 1000);
    }
}
