package com.example.srv_twry.studentcompanion.Network;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.example.srv_twry.studentcompanion.Database.DatabaseContract;
import com.example.srv_twry.studentcompanion.Network.NetworkPOJOs.FeedPOJO;
import com.example.srv_twry.studentcompanion.Network.NetworkPOJOs.ModelsPOJO;
import com.example.srv_twry.studentcompanion.R;


import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

/**
 * Created by srv_twry on 19/6/17.
 * The class which uses the volley library to fetch Contests from the Hackerrank's API
 */

public class FetchContestsRetrofit {
    private static final String HACKERRANK_API_BASE_URL = "https://www.hackerrank.com";
    private static final String CALENDAR_FEED_URL = "calendar/feed.json";
    private final Context context;
    private final onLoadingFinishedListener onLoadingFinishedListener;
    private HackerrankService api;

    public FetchContestsRetrofit(Context context, onLoadingFinishedListener onLoadingFinishedListener){
        this.context=context;
        this.onLoadingFinishedListener= onLoadingFinishedListener;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(HACKERRANK_API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        api = retrofit.create(HackerrankService.class);
    }

    public void fetchContest(){

        api.getFeed().enqueue(new Callback<ModelsPOJO>() {
            @Override
            public void onResponse(@NonNull Call<ModelsPOJO> call, @NonNull Response<ModelsPOJO> response) {

                addToDatabase(response.body().getModels());
                onLoadingFinishedListener.onLoadingFinished();
            }

            @Override
            public void onFailure(@NonNull Call<ModelsPOJO> call, @NonNull Throwable t) {

                Toast.makeText(context, R.string.check_your_internet_connection, Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Parse the JSON Response and add to database
    private void addToDatabase(List<FeedPOJO> response) {
            //deleting the previous table of contests
            Uri uri = DatabaseContract.ContestEntry.CONTENT_URI_CONTESTS;
            context.getContentResolver().delete(uri,null,null);

            //Adding a fake contest for testing notifications.
            //Change the start time of the contest according to your need here.
            /*ContentValues fakecontentValues = new ContentValues();
            fakecontentValues.put(DatabaseContract.ContestEntry.CONTEST_COLUMN_TITLE,"Fake contest");
            fakecontentValues.put(DatabaseContract.ContestEntry.CONTEST_COLUMN_DESCRIPTION,"Fake contests");
            fakecontentValues.put(DatabaseContract.ContestEntry.CONTEST_COLUMN_URL,"https://www.hackerrank.com/contests/projecteuler/");
            fakecontentValues.put(DatabaseContract.ContestEntry.CONTEST_COLUMN_START_TIME,"2017-06-26T02:45:00.000Z");
            fakecontentValues.put(DatabaseContract.ContestEntry.CONTEST_COLUMN_END_TIME,"2024-07-30T18:30:00.000Z");
            context.getContentResolver().insert(DatabaseContract.ContestEntry.CONTENT_URI_CONTESTS,fakecontentValues);
            */
            //delete the code above this line after testing or comment it.

            //The hackerrank API sorts contests in reverse order so reversing the array
            for(int i=response.size()-1; i>=0; i--){

                ContentValues contentValues = new ContentValues();
                contentValues.put(DatabaseContract.ContestEntry.CONTEST_COLUMN_TITLE, response.get(i).getTitle());
                contentValues.put(DatabaseContract.ContestEntry.CONTEST_COLUMN_DESCRIPTION,response.get(i).getDescription());
                contentValues.put(DatabaseContract.ContestEntry.CONTEST_COLUMN_URL,response.get(i).getUrl());
                contentValues.put(DatabaseContract.ContestEntry.CONTEST_COLUMN_START_TIME,response.get(i).getStart());
                contentValues.put(DatabaseContract.ContestEntry.CONTEST_COLUMN_END_TIME,response.get(i).getEnd());

                //Adding the data to the database.
                context.getContentResolver().insert(DatabaseContract.ContestEntry.CONTENT_URI_CONTESTS,contentValues);
            }
    }

    public interface HackerrankService {

        @GET(CALENDAR_FEED_URL)
        Call<ModelsPOJO> getFeed();
    }

    public interface onLoadingFinishedListener{
        void onLoadingFinished();
    }
}
