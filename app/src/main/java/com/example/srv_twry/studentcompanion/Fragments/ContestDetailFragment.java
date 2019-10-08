package com.example.srv_twry.studentcompanion.Fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.srv_twry.studentcompanion.CodingCalendarContestDetailActivity;
import com.example.srv_twry.studentcompanion.POJOs.Contest;
import com.example.srv_twry.studentcompanion.R;
import com.example.srv_twry.studentcompanion.Utilities.DatabaseUtilites;
import com.example.srv_twry.studentcompanion.Utilities.SubscribedContestUtilities;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ContestDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ContestDetailFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String CONTEST_TAG = "contest";
    private static final String IS_REMINDED = "isReminded";

    private Contest mContest;
    private Boolean isSetForReminder;
    private int subscribedDatabaseId;
    private SharedPreferences sharedPreferences;

    public ContestDetailFragment() {
        // Required empty public constructor
    }

    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbarLayout;
    @BindView(R.id.contest_cover)
    ImageView coverImage;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tv_contest_details_title)
    TextView contestDetailTitleView;
    @BindView(R.id.tv_contest_details_start_time)
    TextView contestDetailStartTimeText;
    @BindView(R.id.tv_contest_detail_duration)
    TextView contestDetailDurationText;
    @BindView(R.id.tv_contest_detail_description)
    TextView contestDetailDescriptionText;
    @BindView(R.id.button_contest_detail_register)
    Button contestDetailRegistrationButton;
    @BindView(R.id.contest_detail_share_fab)
    FloatingActionButton shareFloatingActionButton;
    @BindView(R.id.contest_detail_set_reminder)
    FloatingActionButton setReminderFloatingActionButton;

    //For a new instance of the Fragment.
    public static ContestDetailFragment newInstance(Contest contest) {
        ContestDetailFragment fragment = new ContestDetailFragment();
        Bundle args = new Bundle();
        args.putParcelable(CONTEST_TAG,contest);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mContest = getArguments().getParcelable(CONTEST_TAG);
        }
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contest_detail, container, false);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        isSetForReminder = sharedPreferences.getBoolean(mContest.getTitle(),false);
        subscribedDatabaseId = sharedPreferences.getInt(mContest.getTitle()+" subsDbId",-1);

        if (savedInstanceState != null){
            isSetForReminder = savedInstanceState.getBoolean(IS_REMINDED);
        }

        // Initialize and set the data.
        ButterKnife.bind(this,view);
        boolean isTablet;
        if (getActivity().getResources().getBoolean(R.bool.is_tablet)){
            isTablet = true;
            toolbar.setVisibility(View.GONE);
        }else{
            isTablet = false;
        }

        if (!isTablet){
            CodingCalendarContestDetailActivity codingCalendarContestDetailActivity = (CodingCalendarContestDetailActivity) getActivity();
            codingCalendarContestDetailActivity.setSupportActionBar(toolbar);
        }


        //Only for phones
        if (!isTablet){
            CodingCalendarContestDetailActivity codingCalendarContestDetailActivity = (CodingCalendarContestDetailActivity) getActivity();
            toolbar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().finish();
                }
            });
            ActionBar actionBar = codingCalendarContestDetailActivity.getSupportActionBar();
            if (actionBar!=null) {
                actionBar.setHomeButtonEnabled(true);
                actionBar.setDisplayShowHomeEnabled(true);
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
        }


        collapsingToolbarLayout.setTitle("");

        //setting the fab buttons
        shareFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                String sharingString = "Hey "+ mContest.getTitle()+ " is scheduled at "+ mContest.getStartTime().toString() + "\n"+
                        "Lets compete together :) ";
                shareIntent.putExtra(Intent.EXTRA_TEXT,sharingString);
                shareIntent.setType("text/plain");
                startActivity(Intent.createChooser(shareIntent, getResources().getText(R.string.share_contest)));
            }
        });

        //depending on the subscription decide the setReminder fab background
        if (isSetForReminder){
            setReminderFloatingActionButton.setImageResource(R.drawable.ic_remove_reminder);
        }else{
            setReminderFloatingActionButton.setImageResource(R.drawable.ic_set_reminder);
        }

        setReminderFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSetForReminder) {
                    removeContestReminder();
                } else {
                    if (mContest.getStartTime().getTime() > System.currentTimeMillis()){
                        setContestReminder();
                        if (subscribedDatabaseId >0){
                            isSetForReminder = true;
                            setReminderFloatingActionButton.setImageResource(R.drawable.ic_remove_reminder);
                        }else{
                            Toast toast = Toast.makeText(getContext(), R.string.cannot_add_reminder,Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }else{
                        Toast toast = Toast.makeText(getContext(), R.string.contest_is_already_running,Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }
            }
        });

        //Setting the data to the views.
        setDataToViews();

        return view;
    }

    // A helper method to set the reminder for the contest
    private void setContestReminder() {
        //Set the reminder using alarm manager
        SubscribedContestUtilities.setReminderUsingAlarmManager(getContext(),mContest.getTitle(),mContest.getUrl(),mContest.getStartTime().getTime());

        //Adding the contest into the subscribed table in order to re-start alarm in case of reboot.
        subscribedDatabaseId= SubscribedContestUtilities.saveContestIntoSubscribedDatabase(getContext(),mContest);
    }

    // A helper method to remove the reminder set for the contest
    private void removeContestReminder() {
        // cancel the reminder using alarm manager
        SubscribedContestUtilities.removeReminderUsingAlarmManager(getContext(),mContest.getTitle(),mContest.getUrl());

        //Remove the contest from the Database
        int returnedInt = SubscribedContestUtilities.removeContestFromSubscribedDatabase(getContext(),subscribedDatabaseId);
        if (returnedInt >0){
            isSetForReminder = false;
            subscribedDatabaseId = -1;
            setReminderFloatingActionButton.setImageResource(R.drawable.ic_set_reminder);
        }else{
            Toast toast = Toast.makeText(getContext(), R.string.cannot_remove_reminder,Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    // A helper method to set the data into the views.
    private void setDataToViews() {
        if (mContest == null){
            return;
        }
        contestDetailTitleView.setText(mContest.getTitle());
        //contestDetailTitleView.setPaintFlags(contestDetailTitleView.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);
        coverImage.setImageResource(DatabaseUtilites.getCoverImage(mContest.getUrl()));
        SpannableString contestDetailsStartTime = DatabaseUtilites.getStartTimeTextDetailsFragment(mContest.getStartTime());
        contestDetailStartTimeText.setText(contestDetailsStartTime);
        String duration = getString(R.string.duration_approximately)+" "+getContestDuration(mContest.getStartTime(),mContest.getEndTime())+" "+getString(R.string.hours);
        contestDetailDurationText.setText(duration);
        if (mContest.getDescription().equals("")){
            contestDetailDescriptionText.setText(R.string.contest_description_coding_contest_for_programming_enthusiasts);
        }else{
            contestDetailDescriptionText.setText(getString(R.string.contest_description)+ mContest.getDescription());
        }
        contestDetailRegistrationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(mContest.getUrl()));
                startActivity(intent);
            }
        });
    }

    private long getContestDuration(Date start, Date end){
        long startTime = start.getTime();
        long endTime = end.getTime();

        long difference = endTime- startTime;
        difference = TimeUnit.MILLISECONDS.toHours(difference);        // returning the difference in hours.
        return difference;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(IS_REMINDED,isSetForReminder);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        //saving the data for persistent use.
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(mContest.getTitle(),isSetForReminder);
        editor.putInt(mContest.getTitle()+" subsDbId",subscribedDatabaseId);
        editor.apply();
    }
}
