package com.likandr.minispy;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class LogService extends Service {

    final String TAG = LogService.class.getSimpleName();

    Thread mThread;
    private Runnable mDetectLaunchAppRunnable;

    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        someTask();
        return super.onStartCommand(intent, flags, startId);
    }

    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");

        mDetectLaunchAppRunnable = null;
        mThread.interrupt();
    }

    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind");
        return null;
    }

    private void someTask() {
        mDetectLaunchAppRunnable = new DetectLaunchAppRunnable(getApplicationContext());
        mThread = new Thread(mDetectLaunchAppRunnable);
        mThread.start();
    }
}
