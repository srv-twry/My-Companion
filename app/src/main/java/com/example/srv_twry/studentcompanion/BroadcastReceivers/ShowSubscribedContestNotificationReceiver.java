package com.example.srv_twry.studentcompanion.BroadcastReceivers;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.srv_twry.studentcompanion.Utilities.DatabaseUtilites;
import com.example.srv_twry.studentcompanion.Utilities.SubscribedContestUtilities;

import timber.log.Timber;

import static android.support.v4.app.NotificationCompat.PRIORITY_MAX;

/**
 * Created by srv_twry on 21/6/17.
 * The broadcast receiver that will be triggered when the contest is about to start.
 */

public class ShowSubscribedContestNotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Timber.v("[Show notF receiver] RECEIVED BROADCAST");

        //Use Notification builder to show notifications.
        String contestTitle = intent.getStringExtra(SubscribedContestUtilities.CONTEST_TITLE);
        String contestUrl = intent.getStringExtra(SubscribedContestUtilities.CONTEST_URL);

        int imageResourceForContest = DatabaseUtilites.getCoverImage(contestUrl);

        //Setting up the pending Intent
        Intent startBrowser = new Intent(Intent.ACTION_VIEW, Uri.parse(contestUrl));
        PendingIntent pendingIntent = PendingIntent.getActivity(context,0,startBrowser,0);

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        //setting the notification builder
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(imageResourceForContest)
                        .setContentTitle(contestTitle)
                        .setContentText("Click to participate")
                        .setPriority(PRIORITY_MAX)
                        .setSound(alarmSound)
                        .setVibrate(new long[]{500L, 500L, 500L, 500L})
                        .setAutoCancel(true);

        mBuilder.setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(10,mBuilder.build());

        // Delete the contest from the database here.
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        int databaseId = sharedPreferences.getInt(contestTitle +" subsDbId",-1);
        Timber.v("[Show Notifications] " + databaseId);
        int deleted= SubscribedContestUtilities.removeContestFromSubscribedDatabase(context,databaseId);
        if (deleted >0){
            //boolean isSetForReminder = false;
            //databaseId = -1;
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove(contestTitle);
            editor.remove(contestTitle +" subsDbId");
            Timber.v("[Show Notifications] DELETED SHARED PREFERENCES");
            //editor.putBoolean(contestTitle,isSetForReminder);
            //editor.putInt(contestTitle+" subsDbId",databaseId);
            editor.apply();
        }
    }
}
