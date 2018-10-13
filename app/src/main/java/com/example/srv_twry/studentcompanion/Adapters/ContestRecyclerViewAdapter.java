package com.example.srv_twry.studentcompanion.Adapters;

import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.srv_twry.studentcompanion.POJOs.Contest;
import com.example.srv_twry.studentcompanion.R;
import com.example.srv_twry.studentcompanion.Utilities.DatabaseUtilites;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * Created by srv_twry on 18/6/17.
 * The recycler view adapter for the contest list recyclerview
 */

public class ContestRecyclerViewAdapter extends RecyclerView.Adapter<ContestRecyclerViewAdapter.ViewHolder> {

    private final ArrayList<Contest> contestArrayList;
    private final ContestRecyclerViewOnClickListener contestRecyclerViewOnClickListener;

    public ContestRecyclerViewAdapter(ArrayList<Contest> contestArrayList , ContestRecyclerViewOnClickListener contestRecyclerViewOnClickListener){
        this.contestArrayList = contestArrayList;
        this.contestRecyclerViewOnClickListener = contestRecyclerViewOnClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.contest_list_view_holder_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return contestArrayList.size();
    }

    public interface ContestRecyclerViewOnClickListener{
        void onContestListItemClicked(Contest clickedContest);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @BindView(R.id.contest_view_holder_platform_image) ImageView contestPlatform;
        @BindView(R.id.tv_contest_platform_name) TextView contestTitle;
        @BindView(R.id.tv_contest_start_time) TextView contestStartTime;
        final boolean isLandscape;

        public ViewHolder(View view){
            super(view);
            ButterKnife.bind(this,view);
            view.setOnClickListener(this);
            isLandscape = view.getResources().getBoolean(R.bool.is_landscape);
        }

        public void bind(int position) {
            setImageViewUsingUrl(contestArrayList.get(position).getUrl());
            String contestTitleString = contestArrayList.get(position).getTitle();
            if (contestTitleString.length() >=34 && !isLandscape){
                String temp = contestTitleString.substring(0,31) + "...";
                contestTitle.setText(temp);
            }else{
                contestTitle.setText(contestTitleString);
            }
            SpannableString contestStartTimeString = DatabaseUtilites.getStartTimeTextContestList(contestArrayList.get(position).getStartTime());
            contestStartTime.setText(contestStartTimeString);
        }

        private void setImageViewUsingUrl(String url) {
            URL urlPlatform;
            try{
                urlPlatform = new URL(url);
                //get the platform of the contest.
                String platformString = urlPlatform.getHost();
                Timber.v("ContestRecyclerViewAdap " + platformString);

                if (platformString.equals("www.topcoder.com")){
                    contestPlatform.setImageResource(R.mipmap.topcoder_logo);
                }else if (platformString.equals("www.codechef.com")){
                    contestPlatform.setImageResource(R.mipmap.codechef_logo);
                }else if(platformString.equals("www.hackerrank.com")){
                    contestPlatform.setImageResource(R.mipmap.hackerrank_logo);
                }else if(platformString.equals("www.hackerearth.com")){
                    contestPlatform.setImageResource(R.mipmap.hackerearth_logo);
                }else if(platformString.equals("codeforces.com")){
                    contestPlatform.setImageResource(R.mipmap.codeforces_logo);
                }else{
                    contestPlatform.setImageResource(R.mipmap.ic_code);
                }

            }catch (MalformedURLException e){
                e.printStackTrace();
                contestPlatform.setVisibility(View.GONE);
            }
        }

        @Override
        public void onClick(View v) {
            Contest clickedContest = contestArrayList.get(getAdapterPosition());
            contestRecyclerViewOnClickListener.onContestListItemClicked(clickedContest);
        }
    }
}
