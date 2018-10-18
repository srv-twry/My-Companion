package com.example.srv_twry.studentcompanion;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.srv_twry.studentcompanion.Database.DatabaseContract;

import butterknife.BindView;
import butterknife.ButterKnife;

/*
* The activity to add a flash card associated with the topicName
* */
public class AddFlashCardActivity extends AppCompatActivity {

    @BindView(R.id.editText_flash_card_question)
    EditText questionEditText;

    @BindView(R.id.editText_flash_card_answer)
    EditText answerEditText;

    @BindView(R.id.action_button)
    Button button;


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
        ButterKnife.bind(this);

        String action = getIntent().getExtras().getString(AddFlashCardActivity.INTENT_ACTION);

        if(action != null){

            switch(action){
                case AddFlashCardActivity.INTENT_ACTION_SAVE:
                    prepareSaveLayout();
                    break;
                case AddFlashCardActivity.INTENT_ACTION_EDIT:
                    prepareEditLayout();
                    break;
            }
        }else{
           prepareSaveLayout();
        }


    }

    /**
     * onClickAddTask is called when the "ADD" button is clicked.
     * It retrieves user input and inserts that new task data into the underlying database.
     */

    private void onClickAddTask() {

        new AsyncTask(){
            @Override
            protected Object doInBackground(Object[] objects) {

                //We don't want empty topics in the database hence this check
                String inputQuestion = questionEditText.getText().toString();
                String inputAnswer = answerEditText.getText().toString();
                if (inputQuestion.length() == 0 || inputAnswer.length() == 0) {
                    AddFlashCardActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getBaseContext(), R.string.both_the_fields_are_mandatory,Toast.LENGTH_SHORT).show();
                        }
                    });

                    return null;
                }

                ContentValues contentValues = new ContentValues();

                contentValues.put(DatabaseContract.FlashCardsEntry.FLASH_CARD_TOPIC_NAME, topicName);
                contentValues.put(DatabaseContract.FlashCardsEntry.FLASH_CARD_QUESTION, inputQuestion);
                contentValues.put(DatabaseContract.FlashCardsEntry.FLASH_CARD_ANSWER,inputAnswer);

                getContentResolver().insert(DatabaseContract.FlashCardsEntry.CONTENT_URI_FLASH_CARDS, contentValues);

                return null;
            }
        }.execute();

        //Returning to the FlashCardsHomeActivity
        finish();

    }

    private void prepareEditLayout(){

        long id = getIntent().getExtras().getLong(AddFlashCardActivity.FLASH_CARD_ID);
        Cursor cursor = null;
        try {
             cursor = getContentResolver().query(DatabaseContract.FlashCardsEntry.CONTENT_URI_FLASH_CARDS, null,
                    DatabaseContract.FlashCardsEntry._ID + " =? ", new String[]{Long.toString(id)}, null);
            Log.v("cursor count" , cursor.getCount()+"");
        }catch(Exception ex){
            Log.v("cursor count" , cursor.getCount()+"");
        }

        cursor.moveToFirst();
        String question = cursor.getString(cursor.getColumnIndex(DatabaseContract.FlashCardsEntry.FLASH_CARD_QUESTION));
        String answer = cursor.getString(cursor.getColumnIndex(DatabaseContract.FlashCardsEntry.FLASH_CARD_ANSWER));
        questionEditText.setText(question);
        answerEditText.setText(answer);

        button.setText("Update");

    }

    private void prepareSaveLayout(){
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickAddTask();
            }
        });
    }
}
