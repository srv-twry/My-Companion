package com.example.srv_twry.studentcompanion.POJOs;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.example.srv_twry.studentcompanion.Database.DatabaseContract;
import com.example.srv_twry.studentcompanion.R;
import com.example.srv_twry.studentcompanion.ShowFlashCardsActivity;

import timber.log.Timber;

/**
 * Created by srv_twry on 25/6/17.
 * A simple class to represent a flash card
 */

public class FlashCard implements Parcelable {

    private final int contentID;
    private final String topicName;
    private final String question;
    private final String answer;

    public FlashCard(int ID, String topicName, String question, String answer){
        this.contentID = ID;
        this.topicName= topicName;
        this.question = question;
        this.answer = answer;
    }

    public String getTopicName() {
        return topicName;
    }

    public String getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }

    public int getContentID() {
        return contentID;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.contentID);
        dest.writeString(this.topicName);
        dest.writeString(this.question);
        dest.writeString(this.answer);
    }

    private FlashCard(Parcel in) {
        contentID = in.readInt();
        this.topicName = in.readString();
        this.question = in.readString();
        this.answer = in.readString();
    }

    public void deleteFromDB(final Activity parentContext, final boolean closeParent){
        //Show the alert dialog
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(parentContext);
        alertDialog.setTitle(R.string.confirm_delete);
        alertDialog.setMessage(R.string.are_you_sure_you_want_to_delete_this_card);
        alertDialog.setIcon(R.drawable.ic_delete_black);

        alertDialog.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                String idString = Integer.toString(contentID);
                Uri deleteFlashCardIndividual = DatabaseContract.FlashCardsEntry.CONTENT_URI_FLASH_CARDS.buildUpon().appendPath(idString).build();
                int itemsDeleted = parentContext.getContentResolver().delete(deleteFlashCardIndividual,null,null);

                if (itemsDeleted >0 ){
                    Toast.makeText(parentContext, R.string.card_deleted_successfully,Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(parentContext, R.string.cannot_delete_card, Toast.LENGTH_SHORT).show();
                }
                if(closeParent){
                    parentContext.finish();
                }
            }
        });

        alertDialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.show();
    }

    public static FlashCard getFlashCardFromCursor(Cursor mCursor){
        String tag = "getFlashcardfromcursor";
        int IDIndex = mCursor.getColumnIndex(DatabaseContract.FlashCardsEntry._ID);
        int topicNameIndex = mCursor.getColumnIndex(DatabaseContract.FlashCardsEntry.FLASH_CARD_TOPIC_NAME);
        int questionIndex = mCursor.getColumnIndex(DatabaseContract.FlashCardsEntry.FLASH_CARD_QUESTION);
        int answerIndex = mCursor.getColumnIndex(DatabaseContract.FlashCardsEntry.FLASH_CARD_ANSWER);

        Timber.d(tag + " " + IDIndex + topicNameIndex + questionIndex + answerIndex );



        Timber.d(tag + " " + mCursor.getPosition() + mCursor.getCount());
        if (mCursor.getPosition()>=0 || mCursor.moveToNext()) {
            Timber.d(tag + " " + mCursor.getPosition() + mCursor.getCount());

            int contentID = mCursor.getInt(IDIndex);
            String topicName = mCursor.getString(topicNameIndex);
            String question = mCursor.getString(questionIndex);
            String answer = mCursor.getString(answerIndex);
            return new FlashCard(contentID,topicName,question,answer);
        }

        return null;

    }

    public static FlashCard getFlashCardFromID(Context parentContext, int contentID){

        //TODO: Check for invalid input
        String[] projection = {
                DatabaseContract.FlashCardsEntry._ID,
                DatabaseContract.FlashCardsEntry.FLASH_CARD_TOPIC_NAME,
                DatabaseContract.FlashCardsEntry.FLASH_CARD_QUESTION,
                DatabaseContract.FlashCardsEntry.FLASH_CARD_ANSWER
        };
        String selectionClause = DatabaseContract.FlashCardsEntry._ID + " =?";
        String[] selectionArgs = {"" + contentID};

        Cursor mCursor = parentContext.getContentResolver().query(DatabaseContract.FlashCardsEntry.CONTENT_URI_FLASH_CARDS,
                projection,selectionClause,selectionArgs,DatabaseContract.FlashCardsEntry._ID);

        return (getFlashCardFromCursor(mCursor));
    }

    public static final Parcelable.Creator<FlashCard> CREATOR = new Parcelable.Creator<FlashCard>() {
        @Override
        public FlashCard createFromParcel(Parcel source) {
            return new FlashCard(source);
        }

        @Override
        public FlashCard[] newArray(int size) {
            return new FlashCard[size];
        }
    };
}
