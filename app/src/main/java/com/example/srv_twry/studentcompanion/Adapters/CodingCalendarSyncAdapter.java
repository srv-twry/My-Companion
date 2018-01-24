package com.example.srv_twry.studentcompanion.Adapters;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;

import com.example.srv_twry.studentcompanion.Network.FetchContestsRetrofit;

/**
 * Created by srv_twry on 20/6/17.
 * The sync adapter for the coding calendar.
 */

public class CodingCalendarSyncAdapter extends AbstractThreadedSyncAdapter implements FetchContestsRetrofit.onLoadingFinishedListener{

    private final String TAG = CodingCalendarSyncAdapter.class.getSimpleName();

    private Context mContext;

    public CodingCalendarSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mContext=context;
        Log.v(TAG,"Initialised Coding calendar Sync adapter");
    }

    private CodingCalendarSyncAdapter(
            Context context,
            boolean autoInitialize,
            boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        FetchContestsRetrofit fetchContestsVolley = new FetchContestsRetrofit(mContext,this);
        fetchContestsVolley.fetchContest();
        // To see this log message kindly change the logcat view to no filters.
        Log.v(TAG,"SYNCING STARTED BUDDY, TAKE A CLOSER LOOK . THIS MSG IS BIG TO MAKE SURE I CAN NOTICE IT AMONG THE LARGE LOGS.");
    }

    /*The interface method in the FetchContestsRetrofit class which sets the recycler view with the new data
        Here it will not do anything useful other than logging.
    */
    @Override
    public void onLoadingFinished() {
        Log.v(TAG,"synced the data");
    }
}
