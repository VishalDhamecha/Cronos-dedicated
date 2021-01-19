package com.company.mysapcpsdkprojectoffline.util;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.SystemClock;
import android.util.Log;

import static android.content.Context.ALARM_SERVICE;

public class NotificationScheduler {
    @SuppressLint("NewApi")
    public static void setReminder(Context context, Class<?> cls, long delay) {
        try {

            ComponentName receiver = new ComponentName(context, cls);
            PackageManager pm = context.getPackageManager();

            pm.setComponentEnabledSetting(receiver,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP);

            Intent intent = new Intent(context, cls);

            long futureInMillis = System.currentTimeMillis() + delay;
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
            am.setExact(AlarmManager.RTC_WAKEUP, futureInMillis, pendingIntent);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
