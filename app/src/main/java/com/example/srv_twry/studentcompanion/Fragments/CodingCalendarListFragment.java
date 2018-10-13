package com.example.srv_twry.studentcompanion.Fragments;

import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.srv_twry.studentcompanion.Adapters.ContestRecyclerViewAdapter;
import com.example.srv_twry.studentcompanion.Database.DatabaseContract;
import com.example.srv_twry.studentcompanion.Network.FetchContestsRetrofit;
import com.example.srv_twry.studentcompanion.POJOs.Contest;
import com.example.srv_twry.studentcompanion.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass which contains the list of Active and upcoming contests.
 * Activities that contain this fragment must implement the
 * {@link CodingCalendarListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CodingCalendarListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CodingCalendarListFragment extends Fragment implements ContestRecyclerViewAdapter.ContestRecyclerViewOnClickListener, FetchContestsRetrofit.onLoadingFinishedListener, LoaderManager.LoaderCallbacks<Cursor> {

    private static final String RECYCLERVIEW_POSITION = "recyclerView position";
    private Parcelable recyclerViewState;

    private OnFragmentInteractionListener mListener;
    private ArrayList<Contest> contestArrayList = new ArrayList<>();
    @BindView(R.id.pb_loading_contests)
    ProgressBar loadingContestsProgressBar;
    @BindView(R.id.rv_contest_list)
    RecyclerView contestRecyclerView;
    private GridLayoutManager gridLayoutManager;

    private static final int CONTEST_LOADER_ID = 100;

    public CodingCalendarListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * No parameters are required for this fragment.
     * @return A new instance of fragment CodingCalendarListFragment.
     */
    public static CodingCalendarListFragment newInstance() {
        return new CodingCalendarListFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_coding_calendar_list, container, false);
        ButterKnife.bind(this,view);

        if (savedInstanceState != null){
            recyclerViewState = savedInstanceState.getParcelable(RECYCLERVIEW_POSITION);
        }
        // To get the contests either from server or database.
        startLoadingData();


        ContestRecyclerViewAdapter contestRecyclerViewAdapter = new ContestRecyclerViewAdapter(contestArrayList,this);
        gridLayoutManager = new GridLayoutManager(getActivity().getBaseContext(),getResources().getInteger(R.integer.number_columns_grid_view_contest_list));
        contestRecyclerView.setAdapter(contestRecyclerViewAdapter);
        contestRecyclerView.setLayoutManager(gridLayoutManager);
        return view;
    }

    private void startLoadingData() {

        //If the device is online then get the updated data from the server otherwise use the cached data from the database.
        if (isOnline()){
            FetchContestsRetrofit fetchContestsRetrofit = new FetchContestsRetrofit(getContext(),this);
            fetchContestsRetrofit.fetchContest();
        }else{
            getDataFromDatabase();
        }
    }

    // The method which uses loaders to get the Data from the database to the recycler view.
    private void getDataFromDatabase() {
        if (getActivity() != null){
            getActivity().getSupportLoaderManager().initLoader(CONTEST_LOADER_ID,null,this);
        }
    }

    // To pass the activity with the Contest clicked.
    private void passContestToActivity(Contest clickedContest) {
        if (mListener != null) {
            mListener.onListFragmentInteraction(clickedContest);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    //This is used to save the position of the recycler view after rotation.
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(RECYCLERVIEW_POSITION,contestRecyclerView.getLayoutManager().onSaveInstanceState());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        recyclerViewState = contestRecyclerView.getLayoutManager().onSaveInstanceState();
    }

    @Override
    public void onContestListItemClicked(Contest clickedContest) {
        //This so that if the user comes back to the list fragment he comes to the right position.
        recyclerViewState = contestRecyclerView.getLayoutManager().onSaveInstanceState();
        passContestToActivity(clickedContest);
    }

    @Override
    public void onLoadingFinished() {
        // Start the loader to fetch the updated data.
        getDataFromDatabase();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnFragmentInteractionListener {
        void onListFragmentInteraction(Contest clickedContest);
    }
    private boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }

    /*
    * Loader methods
    * */

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<Cursor>(getContext()) {

            Cursor mCursor;

            @Override
            protected void onStartLoading() {
                if (mCursor !=null){
                    deliverResult(mCursor);
                }else{
                    forceLoad();
                }
            }

            public void deliverResult(Cursor c) {
                mCursor=c;
                super.deliverResult(mCursor);
            }

            @Override
            public Cursor loadInBackground() {
                try{
                    return getActivity().getContentResolver().query(DatabaseContract.ContestEntry.CONTENT_URI_CONTESTS,null,null,null,null);
                } catch (Exception e) {
                    Timber.e("Failed to asynchronously load data.");
                    e.printStackTrace();
                    return null;
                }
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(data !=null){
            contestArrayList = convertCursorToArrayList(data);
            ContestRecyclerViewAdapter contestRecyclerViewAdapter = new ContestRecyclerViewAdapter(contestArrayList,this);
            contestRecyclerView.setAdapter(contestRecyclerViewAdapter);
            contestRecyclerView.invalidate();
            loadingContestsProgressBar.setVisibility(View.GONE);
            contestRecyclerView.setVisibility(View.VISIBLE);

            //scrolling the recyclerview to the last visited position.
            if (recyclerViewState != null){
                gridLayoutManager.onRestoreInstanceState(recyclerViewState);
            }
        }
    }

    // To convert the cursor to an array list
    private ArrayList<Contest> convertCursorToArrayList(Cursor data) {
        ArrayList<Contest> returnArrayList = new ArrayList<>();

        int titleColumnIndex = data.getColumnIndex(DatabaseContract.ContestEntry.CONTEST_COLUMN_TITLE);
        int descriptionColumnIndex = data.getColumnIndex(DatabaseContract.ContestEntry.CONTEST_COLUMN_DESCRIPTION);
        int urlColumnIndex = data.getColumnIndex(DatabaseContract.ContestEntry.CONTEST_COLUMN_URL);
        int startTimeColumnIndex = data.getColumnIndex(DatabaseContract.ContestEntry.CONTEST_COLUMN_START_TIME);
        int endTimeColumnIndex = data.getColumnIndex(DatabaseContract.ContestEntry.CONTEST_COLUMN_END_TIME);

        for(int i=0; i< data.getCount();i++){
            data.moveToPosition(i);
            String title = data.getString(titleColumnIndex);
            String description= data.getString(descriptionColumnIndex);
            String url = data.getString(urlColumnIndex);
            String start = data.getString(startTimeColumnIndex);
            String end = data.getString(endTimeColumnIndex);

            Date startTime = getDateFromString(start);
            Date endTime = getDateFromString(end);

            returnArrayList.add(new Contest(title,description,url,startTime,endTime));
        }
        return returnArrayList;
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //set the blank data to the recyclerview.
        contestArrayList = new ArrayList<>();
    }

    //A helper method to convert the time in String to Java Date Class
    private Date getDateFromString(String string){

        Date result;
        try {
            TimeZone tz = TimeZone.getTimeZone("UTC");
            //TimeZone tz = TimeZone.getTimeZone("Asia/Calcutta");
            //Calendar cal = Calendar.getInstance(tz);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            //sdf.setCalendar(cal);
            //cal.setTime(sdf.parse(string));
            //result = cal.getTime();
            sdf.setTimeZone(tz);
            result = sdf.parse(string);
            Timber.v(string);
            Timber.v(result.toString());
        }catch (ParseException e){
            e.printStackTrace();
            return null;
        }
        return result;
    }

}
