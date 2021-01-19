package com.company.mysapcpsdkprojectoffline.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.application.isradeleon.notify.Notify;
import com.company.mysapcpsdkprojectoffline.R;
import com.company.mysapcpsdkprojectoffline.app.WelcomeActivity;

/**
 * Created by Jaison on 17/06/17.
 */

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Notify.build(context)
                .setAction(new Intent(context, WelcomeActivity.class))
                .setTitle(context.getResources().getString(R.string.app_name))
                .setContent("Data successfully synced")
                .setSmallIcon(R.drawable.ic_notification)
                .setColor(R.color.colorPrimary)
                .show();

    }
}


