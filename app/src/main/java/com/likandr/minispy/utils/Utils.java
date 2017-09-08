package com.likandr.minispy.utils;

import android.os.SystemClock;

import com.likandr.minispy.BigData;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class Utils {

    private static final String TAG = Utils.class.getSimpleName();

    public static List<String> formatListBigData(List<BigData> list) {
        List<String> result = new ArrayList<>();
        for(BigData item : list) {
            result.add(formatString(item.getTotal(), item.getName()));
        }
        return result;
    }

    public static String formatString(int par1, String par2) {
        return String.format(Locale.getDefault(),
                "Time %s, proc: %s",
                formatMilliSeconds(par1),
                par2);
    }

    private static String formatMilliSeconds(int milliSeconds) {
        return  String.format(Locale.getDefault(), "%02d",
                TimeUnit.MILLISECONDS.toSeconds(milliSeconds));

        /*return String.format(Locale.getDefault(), "%02d-%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toDays(milliSeconds),
                TimeUnit.MILLISECONDS.toHours(milliSeconds) % TimeUnit.DAYS.toHours(1),
                TimeUnit.MILLISECONDS.toMinutes(milliSeconds) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(milliSeconds) % TimeUnit.MINUTES.toSeconds(1));
        */
    }
}
