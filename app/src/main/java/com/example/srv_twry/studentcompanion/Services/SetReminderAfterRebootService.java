package com.example.srv_twry.studentcompanion.Services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import androidx.annotation.Nullable;

import com.example.srv_twry.studentcompanion.Database.DatabaseContract;
import com.example.srv_twry.studentcompanion.Utilities.SubscribedContestUtilities;

/**
 * Created by srv_twry on 22/6/17.
 * The IntentService class to re-schedule all the reminders set on the application.
 */

public class SetReminderAfterRebootService extends IntentService {

    private final Context mContext;

    public SetReminderAfterRebootService(){
        super("SetReminderAfterRebootService");
        mContext = getApplicationContext();
    }

    //Since it is already called in a background thread by the system so need for loaders.
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        // Setting the alarms for the coding calendar
        Cursor subscribedContestCursor = mContext.getContentResolver().query(DatabaseContract.SubscribedContestEntry.CONTENT_URI_SUBSCRIBED_CONTESTS,null,null,null,null);
        setAlarmsForContests(subscribedContestCursor);

    }

    private void setAlarmsForContests(Cursor subscribedContestCursor) {
        int numberOfSubscribedContests = subscribedContestCursor.getCount();

        int titleColumnIndex = subscribedContestCursor.getColumnIndex(DatabaseContract.SubscribedContestEntry.SUBSCRIBED_CONTEST_TITLE);
        int urlColumnIndex = subscribedContestCursor.getColumnIndex(DatabaseContract.SubscribedContestEntry.SUBSCRIBED_CONTEST_URL);
        int startTimeColumnIndex = subscribedContestCursor.getColumnIndex(DatabaseContract.SubscribedContestEntry.SUBSCRIBED_CONTEST_START_TIME);

        for(int i=0; i< numberOfSubscribedContests; i++){
            subscribedContestCursor.moveToPosition(i);
            long startTime = subscribedContestCursor.getLong(startTimeColumnIndex);
            String title = subscribedContestCursor.getString(titleColumnIndex);
            long currentTime = System.currentTimeMillis();

            if (startTime > currentTime){
                //set the reminder
                String url = subscribedContestCursor.getString(urlColumnIndex);
                SubscribedContestUtilities.setReminderUsingAlarmManager(mContext,title,url,startTime);

            }else{
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
                int databaseId = sharedPreferences.getInt(title+" subsDbId",-1);

                int deleted= SubscribedContestUtilities.removeContestFromSubscribedDatabase(mContext,databaseId);
                if (deleted >0){
                    boolean isSetForReminder = false;
                    databaseId = -1;
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean(title,isSetForReminder);
                    editor.putInt(title+" subsDbId",databaseId);
                    editor.apply();
                }
            }
        }
    }
}
