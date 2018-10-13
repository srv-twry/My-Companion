package com.example.srv_twry.studentcompanion;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.TextView;

import com.example.srv_twry.studentcompanion.Adapters.FlashCardsRecyclerViewCursorAdapter;
import com.example.srv_twry.studentcompanion.Database.DatabaseContract;
import com.example.srv_twry.studentcompanion.POJOs.FlashCard;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;
/*
*  This activity shows the flash cards associated with a certain flash card topic.
* */

public class ShowFlashCardsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> , FlashCardsRecyclerViewCursorAdapter.FlashCardsRecyclerViewCursorAdapterOnClickListener{

    private static final int FLASH_CARDS_LOADER_ID = 400;
    public static final String INTENT_EXTRA_TOPIC_NAME = "intentExtraTopicName";
    public static final String INTENT_EXTRA_FLASH_CARD = "intentExtraFlashCard";

    @BindView(R.id.rv_flash_cards)
    RecyclerView flashCardsRecyclerView;
    @BindView(R.id.fab_add_flash_cards)
    FloatingActionButton floatingActionButton;
    @BindView(R.id.message_show_flash_cards)
    TextView messageShowFlashCards;
    private FlashCardsRecyclerViewCursorAdapter flashCardsRecyclerViewCursorAdapter;
    private String topicName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_flash_cards);

        //getting the topic name
        topicName= getIntent().getStringExtra(INTENT_EXTRA_TOPIC_NAME);
        setTitle(topicName);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ButterKnife.bind(ShowFlashCardsActivity.this);

        flashCardsRecyclerView.setLayoutManager(new LinearLayoutManager(ShowFlashCardsActivity.this));
        flashCardsRecyclerViewCursorAdapter = new FlashCardsRecyclerViewCursorAdapter(ShowFlashCardsActivity.this,this);
        flashCardsRecyclerView.setAdapter(flashCardsRecyclerViewCursorAdapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }



            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                final int id = (int)viewHolder.itemView.getTag();

                FlashCard.getFlashCardFromID(ShowFlashCardsActivity.this,id).deleteFromDB(ShowFlashCardsActivity.this, false);


                /*//Show the alert dialog
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(ShowFlashCardsActivity.this);
                alertDialog.setTitle(R.string.confirm_delete);
                alertDialog.setMessage(R.string.are_you_sure_you_want_to_delete_this_card);
                alertDialog.setIcon(R.drawable.ic_delete_black);

                alertDialog.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int which) {
                    String idString = Integer.toString(id);
                    Uri deleteFlashCardIndividual = DatabaseContract.FlashCardsEntry.CONTENT_URI_FLASH_CARDS.buildUpon().appendPath(idString).build();
                    int itemsDeleted = getContentResolver().delete(deleteFlashCardIndividual,null,null);

                    if (itemsDeleted >0 ){
                        Toast.makeText(ShowFlashCardsActivity.this, R.string.card_deleted_successfully,Toast.LENGTH_SHORT).show();
                        getSupportLoaderManager().restartLoader(FLASH_CARDS_LOADER_ID, null, ShowFlashCardsActivity.this);
                    }else{
                        Toast.makeText(ShowFlashCardsActivity.this, R.string.cannot_delete_card, Toast.LENGTH_SHORT).show();
                    }
                    }
                });

                alertDialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                        getSupportLoaderManager().restartLoader(FLASH_CARDS_LOADER_ID, null, ShowFlashCardsActivity.this);
                    }
                });

                alertDialog.show();*/

            }
        }).attachToRecyclerView(flashCardsRecyclerView);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShowFlashCardsActivity.this,AddFlashCardActivity.class);
                intent.putExtra(INTENT_EXTRA_TOPIC_NAME,topicName);
                startActivity(intent);
            }
        });

        //start the loader
        getSupportLoaderManager().initLoader(FLASH_CARDS_LOADER_ID, null, this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        messageShowFlashCards.setVisibility(View.GONE);
        getSupportLoaderManager().restartLoader(FLASH_CARDS_LOADER_ID, null, this);
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
                    //only load data with the topic name
                    String selection = DatabaseContract.FlashCardsEntry.FLASH_CARD_TOPIC_NAME + " = ?";
                    String [] selectionArgs = {topicName};
                    return getContentResolver().query(DatabaseContract.FlashCardsEntry.CONTENT_URI_FLASH_CARDS,
                            null,selection,selectionArgs,null);
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
        if (data.getCount() == 0){
            messageShowFlashCards.setVisibility(View.VISIBLE);
        }
        flashCardsRecyclerViewCursorAdapter.swapCursor(data);
        Timber.v("[Flash card] loading completed for topic "+ topicName);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        flashCardsRecyclerViewCursorAdapter.swapCursor(null);
    }

    @Override
    public void onFlashCardClicked(FlashCard flashCard) {
        Intent intent = new Intent(ShowFlashCardsActivity.this,ShowFlashCardDetailsActivity.class);
        intent.putExtra(INTENT_EXTRA_FLASH_CARD,flashCard);
        startActivity(intent);
    }
}
