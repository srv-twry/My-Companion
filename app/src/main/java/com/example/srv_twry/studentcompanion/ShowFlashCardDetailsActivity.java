package com.example.srv_twry.studentcompanion;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.srv_twry.studentcompanion.Database.DatabaseContract;
import com.example.srv_twry.studentcompanion.POJOs.FlashCard;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ShowFlashCardDetailsActivity extends AppCompatActivity {

    private FlashCard mFlashCard;
    private boolean isEditMode;

    @BindView(R.id.edittext_flash_card_detail_question)
    EditText detailQuestionEdit;
    @BindView(R.id.edittext_flash_card_detail_answer)
    EditText detailAnswerEdit;
    @BindView(R.id.tv_flash_card_detail_question)
    TextView detailQuestion;
    @BindView(R.id.tv_flash_card_detail_answer)
    TextView detailAnswer;
    @BindView(R.id.fab_show_flash_card_details)
    FloatingActionButton shareThisCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_flash_card_details);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mFlashCard = getIntent().getParcelableExtra(ShowFlashCardsActivity.INTENT_EXTRA_FLASH_CARD);
        setTitle(mFlashCard.getTopicName());

        ButterKnife.bind(this);

        detailQuestion.setText(mFlashCard.getQuestion());
        detailAnswer.setText(mFlashCard.getAnswer());
        detailQuestionEdit.setText(mFlashCard.getQuestion());
        detailAnswerEdit.setText(mFlashCard.getAnswer());

        shareThisCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                String sharingString = getString(R.string.hey_checkout_this_one)+mFlashCard.getQuestion()+getString(R.string.line_break_answer)+mFlashCard.getAnswer();
                shareIntent.putExtra(Intent.EXTRA_TEXT,sharingString);
                shareIntent.setType("text/plain");
                startActivity(Intent.createChooser(shareIntent, getResources().getText(R.string.share_flash_card)));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        if (isEditMode) {
            inflater.inflate(R.menu.menu_flash_card_edit, menu);
        } else {
            inflater.inflate(R.menu.menu_flash_card, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (item.getItemId() == R.id.action_edit_card) {
            toggleEditMode();
            return true;
        } else if (item.getItemId() == R.id.action_save) {
            updateFlashCard();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void toggleEditMode() {
        isEditMode = !isEditMode;
        detailQuestion.setVisibility(isEditMode ? View.GONE : View.VISIBLE);
        detailAnswer.setVisibility(isEditMode ? View.GONE : View.VISIBLE);
        detailQuestionEdit.setVisibility(isEditMode ? View.VISIBLE : View.GONE);
        detailAnswerEdit.setVisibility(isEditMode ? View.VISIBLE : View.GONE);
        invalidateOptionsMenu();
    }

    /**
     * updateFlashCard is called when the "save" action menu button is clicked.
     * It retrieves user input and updates the task data in the underlying database.
     */
    public void updateFlashCard() {
        //We don't want empty topics in the database hence this check
        String inputQuestion = detailQuestionEdit.getText().toString();
        String inputAnswer = detailAnswerEdit.getText().toString();
        if (inputQuestion.length() == 0 || inputAnswer.length() == 0) {
            Toast.makeText(getBaseContext(), R.string.both_the_fields_are_mandatory,Toast.LENGTH_SHORT).show();
            return;
        }

        ContentValues contentValues = new ContentValues();

        contentValues.put(DatabaseContract.FlashCardsEntry.FLASH_CARD_TOPIC_NAME, mFlashCard.getTopicName());
        contentValues.put(DatabaseContract.FlashCardsEntry.FLASH_CARD_QUESTION, inputQuestion);
        contentValues.put(DatabaseContract.FlashCardsEntry.FLASH_CARD_ANSWER,inputAnswer);

        String cardId = String.valueOf(mFlashCard.getContentID());
        Uri updateFlashCardIndividual = DatabaseContract.FlashCardsEntry.CONTENT_URI_FLASH_CARDS.buildUpon().appendPath(cardId).build();
        String where = DatabaseContract.FlashCardsEntry._ID + "= ?";
        String[] selectionArgs = {cardId};
        int rowsUpdated = getContentResolver().update(
                updateFlashCardIndividual,
                contentValues,
                where,
                selectionArgs);

        if(rowsUpdated != 0) {
            Toast.makeText(getBaseContext(), R.string.updated_the_flash_card_successfully,Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getBaseContext(), R.string.unable_to_update_the_card,Toast.LENGTH_SHORT).show();
        }

        //Returning to the FlashCardsHomeActivity
        finish();

    }
}
