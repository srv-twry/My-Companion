package com.example.srv_twry.studentcompanion.BroadcastReceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.srv_twry.studentcompanion.Services.SetReminderAfterRebootService;

import timber.log.Timber;

/**
 * Created by srv_twry on 22/6/17.
 * The broadcast manager that gets triggered whenever the device reboots so that it can
 * start the service that can set the reminders again.
 */

public class DeviceRebootBroadcastReceiver extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            //start the service here.
            Intent startServiceIntent = new Intent(context, SetReminderAfterRebootService.class);
            context.startService(startServiceIntent);
            Timber.v("[Reboot receiver] STARTED SERVICE TO RESCHEDULE THE ALARMS");
        }

    }
}
