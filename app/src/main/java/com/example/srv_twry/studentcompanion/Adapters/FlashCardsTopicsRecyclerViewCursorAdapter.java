package com.example.srv_twry.studentcompanion.Adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.GradientDrawable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.srv_twry.studentcompanion.Database.DatabaseContract;
import com.example.srv_twry.studentcompanion.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by srv_twry on 24/6/17.
 * The cursor adapter for the Flash card topics recycler view.
 */

public class FlashCardsTopicsRecyclerViewCursorAdapter extends RecyclerView.Adapter<FlashCardsTopicsRecyclerViewCursorAdapter.ViewHolder>{

    private Cursor mCursor;
    private final Context mContext;
    private final FlashCardsTopicRecyclerViewOnClickListener flashCardsTopicRecyclerViewOnClickListener;

    public FlashCardsTopicsRecyclerViewCursorAdapter(Context context ,FlashCardsTopicRecyclerViewOnClickListener flashCardsTopicRecyclerViewOnClickListener){
        mContext = context;
        this.flashCardsTopicRecyclerViewOnClickListener = flashCardsTopicRecyclerViewOnClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.flash_card_topic_list_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        int idIndex = mCursor.getColumnIndex(DatabaseContract.FlashCardsTopicsEntry._ID);
        int nameIndex = mCursor.getColumnIndex(DatabaseContract.FlashCardsTopicsEntry.FLASH_CARDS_TOPIC_NAME);
        int priorityIndex = mCursor.getColumnIndex(DatabaseContract.FlashCardsTopicsEntry.FLASH_CARDS_TOPIC_PRIORITY);

        mCursor.moveToPosition(position);

        int id = mCursor.getInt(idIndex);
        String name = mCursor.getString(nameIndex);
        int priority = mCursor.getInt(priorityIndex);

        holder.itemView.setTag(id);
        holder.topicName.setText(name);

        //getting the color.
        int priorityColor = getPriorityColor(priority);
        GradientDrawable priorityCircle = (GradientDrawable) holder.priorityTextView.getBackground();
        String priorityText;
        switch (priority){
            case 1:
                priorityText = mContext.getResources().getString(R.string.high);
                break;
            case 2:
                priorityText = mContext.getResources().getString(R.string.medium);
                break;
            case 3:
                priorityText = mContext.getResources().getString(R.string.low);
                break;
            default:
                priorityText = mContext.getResources().getString(R.string.high);
                break;
        }
        holder.priorityTextView.setText(priorityText);
        priorityCircle.setColor(priorityColor);

    }

    @Override
    public int getItemCount() {
        if (mCursor == null){
            return 0;
        }
        return mCursor.getCount();
    }

    /*
    Helper method for selecting the correct priority circle color.
    P1 = red, P2 = orange, P3 = yellow
    */
    private int getPriorityColor(int priority) {
        int priorityColor = 0;

        switch(priority) {
            case 1: priorityColor = ContextCompat.getColor(mContext, R.color.materialRed);
                break;
            case 2: priorityColor = ContextCompat.getColor(mContext, R.color.materialOrange);
                break;
            case 3: priorityColor = ContextCompat.getColor(mContext, R.color.materialYellow);
                break;
            default: break;
        }
        return priorityColor;
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

    //Interface to implement the onClickListener
    public interface FlashCardsTopicRecyclerViewOnClickListener{
        void onFlashCardTopicClicked(String topicName);
    }

    // Inner class for creating ViewHolders
    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @BindView(R.id.tv_flash_cards_topics_item_name) TextView topicName;
        @BindView(R.id.priorityTextView) TextView priorityTextView;

        /**
         * Constructor for the TaskViewHolders.
         *
         * @param itemView The view inflated in onCreateViewHolder
         */
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            mCursor.moveToPosition(position);
            int nameIndex = mCursor.getColumnIndex(DatabaseContract.FlashCardsTopicsEntry.FLASH_CARDS_TOPIC_NAME);
            String topicName = mCursor.getString(nameIndex);
            flashCardsTopicRecyclerViewOnClickListener.onFlashCardTopicClicked(topicName);
        }
    }
}
