package com.likandr.minispy.utils;

import android.annotation.TargetApi;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManager;
import android.content.Context;

import java.text.SimpleDateFormat;

public class USUtils {

    private static final String TAG = USUtils.class.getSimpleName();

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("M-d-yyyy HH:mm:ss",
            java.util.Locale.getDefault());

    @TargetApi(21)
    public static String getUsageStats(Context context) {
        UsageStatsManager usm = getUsageStatsManager(context);
        long time = System.currentTimeMillis();
        UsageEvents usageEvents = usm.queryEvents(time - 100 * 1000, time);
        UsageEvents.Event event = new UsageEvents.Event();
        // get last event
        while (usageEvents.hasNextEvent()) {
            usageEvents.getNextEvent(event);
        }
        if (event.getEventType() == UsageEvents.Event.MOVE_TO_FOREGROUND) {
            return event.getPackageName();
        }
        return "";
    }

    @SuppressWarnings("ResourceType")
    private static UsageStatsManager getUsageStatsManager(Context context){
        UsageStatsManager usm = (UsageStatsManager) context.getSystemService("usagestats");
        return usm;
    }
}
