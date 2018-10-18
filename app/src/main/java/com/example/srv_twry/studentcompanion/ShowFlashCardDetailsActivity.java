package com.example.srv_twry.studentcompanion;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.srv_twry.studentcompanion.POJOs.FlashCard;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ShowFlashCardDetailsActivity extends AppCompatActivity {

    private FlashCard mFlashCard;
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
        getMenuInflater().inflate(R.menu.flash_card_details_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case (R.id.flashcard_delete):
                FlashCard.getFlashCardFromID(this,mFlashCard.getContentID()).deleteFromDB(this,true);
                return true;
            case R.id.flashcard_edit:


            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
