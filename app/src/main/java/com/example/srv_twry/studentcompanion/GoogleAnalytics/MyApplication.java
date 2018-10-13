package com.example.srv_twry.studentcompanion.GoogleAnalytics;

import android.app.Application;

import com.example.srv_twry.studentcompanion.BuildConfig;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.StandardExceptionParser;
import com.google.android.gms.analytics.Tracker;

import timber.log.Timber;

/**
 * Created by srv_twry on 28/6/17.
 * This class will be used to return the tracker and also contains some methods for the analytics.
 */

public class MyApplication extends Application {

    private static MyApplication mInstance;

        @Override
        public void onCreate() {
            super.onCreate();
            mInstance = this;

            /**
             * Timber Initialization
             */
            if (BuildConfig.DEBUG) {
                Timber.plant(new Timber.DebugTree());
            }

            AnalyticsTrackers.initialize(this);
            AnalyticsTrackers.getInstance().get(AnalyticsTrackers.Target.APP);
        }

        public static synchronized MyApplication getInstance() {
            return mInstance;
        }

        public synchronized Tracker getGoogleAnalyticsTracker() {
            AnalyticsTrackers analyticsTrackers = AnalyticsTrackers.getInstance();
            return analyticsTrackers.get(AnalyticsTrackers.Target.APP);
        }

        /***
         * Tracking screen view
         *
         * @param screenName screen name to be displayed on GA dashboard
         */
        public void trackScreenView(String screenName) {
            Tracker t = getGoogleAnalyticsTracker();

            // Set screen name.
            t.setScreenName(screenName);

            // Send a screen view.
            t.send(new HitBuilders.ScreenViewBuilder().build());

            GoogleAnalytics.getInstance(this).dispatchLocalHits();
        }

        /***
         * Tracking exception
         *
         * @param e exception to be tracked
         */
        public void trackException(Exception e) {
            if (e != null) {
                Tracker t = getGoogleAnalyticsTracker();

                t.send(new HitBuilders.ExceptionBuilder()
                        .setDescription(
                                new StandardExceptionParser(this, null)
                                        .getDescription(Thread.currentThread().getName(), e))
                        .setFatal(false)
                        .build()
                );
            }
        }

        /***
         * Tracking event
         *
         * @param category event category
         * @param action   action of the event
         * @param label    label
         */
        public void trackEvent(String category, String action, String label) {
            Tracker t = getGoogleAnalyticsTracker();

            // Build and send an Event.
            t.send(new HitBuilders.EventBuilder().setCategory(category).setAction(action).setLabel(label).build());
        }


}
