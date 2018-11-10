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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.srv_twry.studentcompanion.Fragments.CodingCalendarListFragment;
import com.example.srv_twry.studentcompanion.Fragments.ContestDetailFragment;
import com.example.srv_twry.studentcompanion.POJOs.Contest;

import static com.example.srv_twry.studentcompanion.R.menu.main_menu;
import static com.example.srv_twry.studentcompanion.R.id.action_filter;

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
    private CodingCalendarListFragment mCodingCalenderListFragment;
    @Nullable @BindView(R.id.tv_click_contest_message)
    TextView clickContestMessage;

    private LinearLayout mHiddenSpinner;
    private Spinner mCategorySpinner;
    private String Category = "";
    private Boolean mIsSpinnerFirstCall = Boolean.FALSE;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coding_calendar_list);
        setTitle(getResources().getString(R.string.coding_calendar));
        mCodingCalenderListFragment = new CodingCalendarListFragment();
        ButterKnife.bind(this);
        mHiddenSpinner = (LinearLayout) findViewById(R.id.hiddenSpinner);
        mCategorySpinner = (Spinner) findViewById(R.id.categorySpinner);
        ArrayAdapter<String> categorys=new ArrayAdapter<String>(CodingCalendarListActivity.this,android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.category_arrays));
        categorys.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mCategorySpinner.setAdapter(categorys);
        mCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Category = mCategorySpinner.getItemAtPosition(i).toString();
                if(mIsSpinnerFirstCall) {
                    if (Category != "") {
                        Log.d(INTENT_EXTRA_TAG, Category);
                       oncreateFragment();
                    }
                }
                mIsSpinnerFirstCall =Boolean.TRUE;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
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
    public void oncreateFragment(){
        CodingCalendarListFragment codingCalendarListFragment = CodingCalendarListFragment.newInstance();
        FragmentManager fragmentManager1 = getSupportFragmentManager();
        fragmentManager1.beginTransaction().replace(R.id.frame_layout_coding_calendar_list, codingCalendarListFragment).addToBackStack(null).commit();
    }
    public String getCategory () {
        return  Category;
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
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        if(item.getItemId() == action_filter)
        {
            mHiddenSpinner.setVisibility(View.VISIBLE);
        }
        return super.onOptionsItemSelected(item);
    }

}
