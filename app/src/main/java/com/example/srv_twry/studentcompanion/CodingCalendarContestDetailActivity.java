package com.example.srv_twry.studentcompanion;

import android.os.Build;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.example.srv_twry.studentcompanion.Fragments.ContestDetailFragment;
import com.example.srv_twry.studentcompanion.POJOs.Contest;

import static com.example.srv_twry.studentcompanion.CodingCalendarListActivity.INTENT_EXTRA_TAG;

/* The activity which contains the fragment showing the details of the Contest in phones
*  For tablets it would be blank.
*/
public class CodingCalendarContestDetailActivity extends AppCompatActivity {

    private Contest mContest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }

        if (Build.VERSION.SDK_INT < 16)//before Jelly Bean Versions
        {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        else // Jelly Bean and up
        {
            View decorView = getWindow().getDecorView();
            // Hide the status bar.
            int ui = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(ui);

        }

        setContentView(R.layout.activity_coding_calendar_contest_detail);
        mContest = getIntent().getParcelableExtra(INTENT_EXTRA_TAG);

        if (savedInstanceState ==null){
            ContestDetailFragment contestDetailFragment = ContestDetailFragment.newInstance(mContest);
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().add(R.id.frame_layout_contest_detail,contestDetailFragment).commit();
        }

    }
}
