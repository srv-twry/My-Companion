package com.example.srv_twry.studentcompanion.Adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.srv_twry.studentcompanion.Database.DatabaseContract;
import com.example.srv_twry.studentcompanion.POJOs.FlashCard;
import com.example.srv_twry.studentcompanion.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by srv_twry on 25/6/17.
 */

public class FlashCardsRecyclerViewCursorAdapter extends RecyclerView.Adapter<FlashCardsRecyclerViewCursorAdapter.ViewHolder> {

    private Cursor mCursor;
    private final Context mContext;
    private final FlashCardsRecyclerViewCursorAdapterOnClickListener flashCardsRecyclerViewCursorAdapterOnClickListener;

    public FlashCardsRecyclerViewCursorAdapter(Context context, FlashCardsRecyclerViewCursorAdapterOnClickListener flashCardsRecyclerViewCursorAdapterOnClickListener){
        mContext = context;
        this.flashCardsRecyclerViewCursorAdapterOnClickListener=flashCardsRecyclerViewCursorAdapterOnClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.flash_card_list_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        int idIndex = mCursor.getColumnIndex(DatabaseContract.FlashCardsEntry._ID);
        int questionIndex = mCursor.getColumnIndex(DatabaseContract.FlashCardsEntry.FLASH_CARD_QUESTION);

        mCursor.moveToPosition(position);

        int id = mCursor.getInt(idIndex);
        holder.itemView.setTag(id);     //setting this tag will be helpful in swipe to delete operation.
        String question = mCursor.getString(questionIndex);

        holder.flashCardQuestions.setText(question);
    }

    @Override
    public int getItemCount() {
        if (mCursor == null){
            return 0;
        }
        return mCursor.getCount();
    }

    /**
     * When data changes and a re-query occurs, this function swaps the old Cursor
     * with a newly updated Cursor (Cursor c) that is passed in.
     */
    public Cursor swapCursor(Cursor c) {
        // check if this cursor is the same as the previous cursor (mCursor)
        if (mCursor == c) {
            return null; // bc nothing has changed
        }
        Cursor temp = mCursor;
        this.mCursor = c; // new cursor value assigned

        //check if this is a valid cursor, then update the cursor
        if (c != null) {
            this.notifyDataSetChanged();
        }
        return temp;
    }

    //Interface to pass the flash card clicked to show the details
    public interface FlashCardsRecyclerViewCursorAdapterOnClickListener{
        void onFlashCardClicked(FlashCard flashCard);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @BindView(R.id.tv_flash_cards_question) TextView flashCardQuestions;

        public ViewHolder(View itemView){
            super(itemView);
            ButterKnife.bind(this,itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            mCursor.moveToPosition(position);

            int idIndex = mCursor.getColumnIndex(DatabaseContract.FlashCardsEntry._ID);
            int topicNameIndex = mCursor.getColumnIndex(DatabaseContract.FlashCardsEntry.FLASH_CARD_TOPIC_NAME);
            int questionIndex = mCursor.getColumnIndex(DatabaseContract.FlashCardsEntry.FLASH_CARD_QUESTION);
            int answerIndex = mCursor.getColumnIndex(DatabaseContract.FlashCardsEntry.FLASH_CARD_ANSWER);

            int id = mCursor.getInt(idIndex);
            String topicName = mCursor.getString(topicNameIndex);
            String question = mCursor.getString(questionIndex);
            String answer = mCursor.getString(answerIndex);

            flashCardsRecyclerViewCursorAdapterOnClickListener.onFlashCardClicked(new FlashCard(id, topicName,question,answer));
        }
    }
}
