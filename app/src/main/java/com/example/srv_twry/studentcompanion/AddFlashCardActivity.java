package com.example.srv_twry.studentcompanion;

import android.content.ContentValues;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.srv_twry.studentcompanion.Database.DatabaseContract;

/*
* The activity to add a flash card associated with the topicName
* */
public class AddFlashCardActivity extends AppCompatActivity {

    private String topicName;

    public static final String INTENT_ACTION = "action";
    public static final String INTENT_ACTION_EDIT = "edit";
    public static final String INTENT_ACTION_SAVE = "save";
    public static final String FLASH_CARD_ID = "id";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_flash_card);
        topicName = getIntent().getStringExtra(ShowFlashCardsActivity.INTENT_EXTRA_TOPIC_NAME);
        setTitle(topicName);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    /**
     * onClickAddTask is called when the "ADD" button is clicked.
     * It retrieves user input and inserts that new task data into the underlying database.
     */
    public void onClickAddTask(View view) {
        //We don't want empty topics in the database hence this check
        String inputQuestion = ((EditText) findViewById(R.id.editText_flash_card_question)).getText().toString();
        String inputAnswer = ((EditText) findViewById(R.id.editText_flash_card_answer)).getText().toString();
        if (inputQuestion.length() == 0 || inputAnswer.length() == 0) {
            Toast.makeText(getBaseContext(), R.string.both_the_fields_are_mandatory,Toast.LENGTH_SHORT).show();
            return;
        }

        ContentValues contentValues = new ContentValues();

        contentValues.put(DatabaseContract.FlashCardsEntry.FLASH_CARD_TOPIC_NAME, topicName);
        contentValues.put(DatabaseContract.FlashCardsEntry.FLASH_CARD_QUESTION, inputQuestion);
        contentValues.put(DatabaseContract.FlashCardsEntry.FLASH_CARD_ANSWER,inputAnswer);

        Uri uri = getContentResolver().insert(DatabaseContract.FlashCardsEntry.CONTENT_URI_FLASH_CARDS, contentValues);


        if(uri != null) {
            Toast.makeText(getBaseContext(), R.string.created_the_flash_card_successfully,Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getBaseContext(), R.string.unable_to_create_the_card,Toast.LENGTH_SHORT).show();
        }

        //Returning to the FlashCardsHomeActivity
        finish();

    }
}
