package com.example.srv_twry.studentcompanion;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.example.srv_twry.studentcompanion.Fragments.CodingCalendarListFragment;
import com.example.srv_twry.studentcompanion.Fragments.ContestDetailFragment;
import com.example.srv_twry.studentcompanion.POJOs.Contest;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

/*
* This activity contains the coding contest lists for phones and The two pane layout for the tablets.
* */
public class CodingCalendarListActivity extends AppCompatActivity implements CodingCalendarListFragment.OnFragmentInteractionListener {

    public static final String INTENT_EXTRA_TAG = "Contest";

    // The authority for the sync adapter's content provider
    private static final String AUTHORITY = "com.example.srv_twry.studentcompanion";
    // An account type, in the form of a domain name
    private static final String ACCOUNT_TYPE = "example.com";
    // The account name
    private static final String ACCOUNT = "dummyaccount";

    // Sync interval constants
    private static final long SECONDS_PER_MINUTE = 60L;
    private static final long SYNC_INTERVAL_IN_MINUTES = 60L;
    private static final long SYNC_INTERVAL =
            SYNC_INTERVAL_IN_MINUTES *
                    SECONDS_PER_MINUTE;

    // Instance fields
    private Account mAccount;
    @Nullable @BindView(R.id.tv_click_contest_message)
    TextView clickContestMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coding_calendar_list);
        setTitle(getResources().getString(R.string.coding_calendar));

        ButterKnife.bind(this);

        //Only create a fragment when their isn't one.
        if (savedInstanceState == null) {
            CodingCalendarListFragment codingCalendarListFragment = CodingCalendarListFragment.newInstance();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().add(R.id.frame_layout_coding_calendar_list, codingCalendarListFragment).commit();
        }

        //Setting the syncAccount
        mAccount = CreateSyncAccount(CodingCalendarListActivity.this);
        //Setting the periodic sync per hour.
        ContentResolver.addPeriodicSync(mAccount, AUTHORITY, Bundle.EMPTY, SYNC_INTERVAL);


    }

    // In case of phones it will start the ContestDetailActivity while in case of tablets it will contact ContestDetailFragment for details.
    @Override
    public void onListFragmentInteraction(Contest clickedContest) {
        //For phones
        if (!getResources().getBoolean(R.bool.is_tablet)) {
            Intent intent = new Intent(CodingCalendarListActivity.this, CodingCalendarContestDetailActivity.class);
            intent.putExtra(INTENT_EXTRA_TAG, clickedContest);
            startActivity(intent);
        } else {
            //For tablets
            clickContestMessage.setVisibility(View.GONE);
            ContestDetailFragment contestDetailFragment = ContestDetailFragment.newInstance(clickedContest);
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.frame_layout_contest_detail, contestDetailFragment).commit();
        }

    }

    // Creating the sync account for the Coding calendar activity
    private Account CreateSyncAccount(Context context) {

        // Create the account type and default account
        Account newAccount = new Account(
                ACCOUNT, ACCOUNT_TYPE);
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(
                        ACCOUNT_SERVICE);

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
        if (accountManager.addAccountExplicitly(newAccount, null, null)) {
            Timber.v("Created the account");

            // Requesting the first sync after creating the account.
            ContentResolver.requestSync(newAccount, AUTHORITY, Bundle.EMPTY);
        }

        return newAccount;
    }

}
