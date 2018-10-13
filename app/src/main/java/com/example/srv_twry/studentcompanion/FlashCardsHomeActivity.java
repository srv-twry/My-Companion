package com.example.srv_twry.studentcompanion;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.srv_twry.studentcompanion.Adapters.FlashCardsTopicsRecyclerViewCursorAdapter;
import com.example.srv_twry.studentcompanion.Database.DatabaseContract;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

import static com.example.srv_twry.studentcompanion.Database.DatabaseContract.FlashCardsTopicsEntry.FLASH_CARDS_TOPIC_PRIORITY;
import static com.example.srv_twry.studentcompanion.ShowFlashCardsActivity.INTENT_EXTRA_TOPIC_NAME;

/*
* The home activity of the flash cards module. It will contain the list of flash card categories/topics added by the user
* and ability to add more categories.
* */


public class FlashCardsHomeActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> ,
        FlashCardsTopicsRecyclerViewCursorAdapter.FlashCardsTopicRecyclerViewOnClickListener{

    private static final int TOPICS_LOADER_ID = 300;

    @BindView(R.id.fab_add_flash_cards_topics)
    FloatingActionButton addFlashCardsFab;
    @BindView(R.id.rv_flash_cards_topics)
    RecyclerView flashCardsRecyclerView;
    @BindView(R.id.pb_loading_flash_cards_topics)
    ProgressBar loadingFlashCardsTopics;
    @BindView(R.id.message_show_flash_cards_home)
    TextView messageShowFlashCardTopics;

    private FlashCardsTopicsRecyclerViewCursorAdapter flashCardsTopicsRecyclerViewCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getResources().getString(R.string.flash_cards));
        setContentView(R.layout.activity_flash_cards_home);

        // Handle up navigation
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ButterKnife.bind(this);

        //Instantiate the recycler view
        flashCardsRecyclerView.setLayoutManager(new GridLayoutManager(this,getResources().getInteger(R.integer.number_colums_grid_view_flash_topic)));
        flashCardsTopicsRecyclerViewCursorAdapter = new FlashCardsTopicsRecyclerViewCursorAdapter(FlashCardsHomeActivity.this,this);
        flashCardsRecyclerView.setAdapter(flashCardsTopicsRecyclerViewCursorAdapter);

        //setUp fab button to add topics here.
        addFlashCardsFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FlashCardsHomeActivity.this,AddFlashCardTopicActivity.class);
                startActivity(intent);
            }
        });

        //start the loader
        getSupportLoaderManager().initLoader(TOPICS_LOADER_ID, null, this);


        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                final int id = (int) viewHolder.itemView.getTag();
                final String topicName = ((TextView)viewHolder.itemView.findViewById(R.id.tv_flash_cards_topics_item_name)).getText().toString();

                //Show the alert dialog
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(FlashCardsHomeActivity.this);
                alertDialog.setTitle(R.string.confirm_delete_alert_dialog);
                alertDialog.setMessage(R.string.are_you_sure_you_want_to_delete_this);
                alertDialog.setIcon(R.drawable.ic_delete_forever_black);

                alertDialog.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int which) {
                        deleteTopicFromDatabase(id,topicName);
                    }
                });

                alertDialog.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        getSupportLoaderManager().restartLoader(TOPICS_LOADER_ID, null, FlashCardsHomeActivity.this);
                        dialog.cancel();
                    }
                });

                alertDialog.show();

            }
        }).attachToRecyclerView(flashCardsRecyclerView);

    }

    @Override
    protected void onResume() {
        super.onResume();

        // re-queries for all tasks
        messageShowFlashCardTopics.setVisibility(View.GONE);
        loadingFlashCardsTopics.setVisibility(View.VISIBLE);
        addFlashCardsFab.setVisibility(View.GONE);
        flashCardsRecyclerView.setVisibility(View.GONE);
        getSupportLoaderManager().restartLoader(TOPICS_LOADER_ID, null, this);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<Cursor>(this) {

            private Cursor mCursor = null;

            @Override
            protected void onStartLoading() {
                if (mCursor !=null){
                    deliverResult(mCursor);
                }else{
                    forceLoad();
                }
            }
            @Override
            public void deliverResult(Cursor cursor){
                mCursor = cursor;
                super.deliverResult(cursor);
            }

            @Override
            public Cursor loadInBackground() {
                try{
                    return getContentResolver().query(DatabaseContract.FlashCardsTopicsEntry.CONTENT_URI_FLASH_CARDS_TOPICS,
                            null,null,null,FLASH_CARDS_TOPIC_PRIORITY);
                }catch(Exception e){
                    Timber.e("Failed to asynchronously load data.");
                    e.printStackTrace();
                    return null;
                }
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Timber.v("Loading completed");
        if (data.getCount() == 0){
            messageShowFlashCardTopics.setVisibility(View.VISIBLE);
        }
        flashCardsTopicsRecyclerViewCursorAdapter.swapCursor(data);
        loadingFlashCardsTopics.setVisibility(View.GONE);
        addFlashCardsFab.setVisibility(View.VISIBLE);
        flashCardsRecyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        flashCardsTopicsRecyclerViewCursorAdapter.swapCursor(null);
    }

    //Method to open all the flash cards with the topicName
    @Override
    public void onFlashCardTopicClicked(String topicName) {
        Timber.v("Clicked topic with Name: "+topicName);
        Intent intent = new Intent(FlashCardsHomeActivity.this,ShowFlashCardsActivity.class);
        intent.putExtra(INTENT_EXTRA_TOPIC_NAME,topicName);
        startActivity(intent);
    }

    //Helper method to delete the selected topic and all the flash cards associated with it.
    private void deleteTopicFromDatabase(int tag, String topicName){
        String tagString = Integer.toString(tag);
        Uri deleteTopic = DatabaseContract.FlashCardsTopicsEntry.CONTENT_URI_FLASH_CARDS_TOPICS.buildUpon().appendPath(tagString).build();
        int resultOne=getContentResolver().delete(deleteTopic,null,null);

        Uri deleteTopicsCards = DatabaseContract.FlashCardsTopicsEntry.CONTENT_URI_FLASH_CARDS_TOPICS.buildUpon().appendPath(topicName).build();
        int resultTwo = getContentResolver().delete(deleteTopicsCards,null,null);

        if (resultOne >0){
            Toast.makeText(this, R.string.topic_successfully_deleted,Toast.LENGTH_SHORT).show();
            loadingFlashCardsTopics.setVisibility(View.VISIBLE);
            addFlashCardsFab.setVisibility(View.GONE);
            flashCardsRecyclerView.setVisibility(View.GONE);
            getSupportLoaderManager().restartLoader(TOPICS_LOADER_ID, null, this);
        }else{
            if (resultTwo <0){
                Timber.v(getString(R.string.error_in_deleting_cards));
            }
            Toast.makeText(this, R.string.unable_to_delete_topic,Toast.LENGTH_SHORT).show();
        }
    }
}
