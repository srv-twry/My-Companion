package com.example.srv_twry.studentcompanion;

import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.example.srv_twry.studentcompanion.Adapters.FeaturesRecyclerViewAdapter;
import com.example.srv_twry.studentcompanion.POJOs.Feature;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, FeaturesRecyclerViewAdapter.FeaturesOnClickListener{

    private ArrayList<Feature> featureArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("My Home");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                String sharingString = "Hey Checkout this cool app \n"+ getResources().getString(R.string.app_name) +
                        " :) \n"+ "This is a perfect companion for any student. Get it at the google playstore here:- \n"+
                        getResources().getString(R.string.app_playstore_link); //TODO: To put the playstore link after pushing the app on playstore here
                shareIntent.putExtra(Intent.EXTRA_TEXT,sharingString);
                shareIntent.setType("text/plain");
                startActivity(Intent.createChooser(shareIntent, getResources().getText(R.string.share_this_app)));

            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        featureArrayList = fillWithData();
        RecyclerView featuresRecyclerView= (RecyclerView) findViewById(R.id.rv_features);
        FeaturesRecyclerViewAdapter featuresRecyclerViewAdapter = new FeaturesRecyclerViewAdapter(featureArrayList,this);
        int numberOfColumns = getResources().getInteger(R.integer.number_columns_grid_view_features);
        GridLayoutManager featuresGridLayoutManager = new GridLayoutManager(HomeActivity.this,numberOfColumns);
        featuresRecyclerView.setAdapter(featuresRecyclerViewAdapter);
        featuresRecyclerView.setLayoutManager(featuresGridLayoutManager);

    }

    // Method to create features arrayList
    private ArrayList<Feature> fillWithData() {
        ArrayList<Feature> featureArrayList = new ArrayList<>();

        featureArrayList.add(new Feature(getResources().getString(R.string.coding_calendar),R.drawable.coding_calendar_logo));
        featureArrayList.add(new Feature(getResources().getString(R.string.flash_cards),R.drawable.flash_cards_logo));
        featureArrayList.add(new Feature(getResources().getString(R.string.pdf_creator),R.drawable.pdf_creator_logo));
        return featureArrayList;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_coding_calendar) {
            startCodingCalendar();
        } else if (id == R.id.nav_flash_cards) {
            startFlashCards();
        }else if(id == R.id.nav_pdf_creator){
            startPdfCreator();
        }else if (id == R.id.nav_share){
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            String sharingString = "Hey Checkout this cool app \n"+ getResources().getString(R.string.app_name) +
                    " :) \n"+ "This is a perfect companion for any student. Get it at the google playstore here:- \n"+
                    getResources().getString(R.string.app_playstore_link); //TODO: To put the playstore link after pushing the app on playstore here
            shareIntent.putExtra(Intent.EXTRA_TEXT,sharingString);
            shareIntent.setType("text/plain");
            startActivity(Intent.createChooser(shareIntent, getResources().getText(R.string.share_this_app)));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //Open the necessary activity responding to the clicks
    @Override
    public void onFeatureClicked(int position) {
        if (featureArrayList.get(position).getTitle().equals(getResources().getString(R.string.coding_calendar))){
            startCodingCalendar();
        }else if (featureArrayList.get(position).getTitle().equals(getResources().getString(R.string.flash_cards))){
            startFlashCards();
        }else if (featureArrayList.get(position).getTitle().equals(getResources().getString(R.string.pdf_creator))){
            startPdfCreator();
        }

    }

    //Start pdf creator
    private void startPdfCreator() {
        Intent intent = new Intent(HomeActivity.this,PDFCreatorHomeActivity.class);
        startActivity(intent);
    }

    //Start flashCardsHome
    private void startFlashCards() {
        Intent intent = new Intent(HomeActivity.this,FlashCardsHomeActivity.class);
        startActivity(intent);
    }

    // Start the coding calendar list activity
    private void startCodingCalendar() {
        Intent intent = new Intent(HomeActivity.this,CodingCalendarListActivity.class);
        startActivity(intent);
    }
}
