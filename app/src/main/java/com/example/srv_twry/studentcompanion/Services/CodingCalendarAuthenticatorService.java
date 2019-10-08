package com.example.srv_twry.studentcompanion.Services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import androidx.annotation.Nullable;

import com.example.srv_twry.studentcompanion.Adapters.CodingCalendarAuthenticator;

/**
 * Created by srv_twry on 20/6/17.
 * The service for the Coding calendar coding_calendar_authenticator used for the sync adapter.
 */

public class CodingCalendarAuthenticatorService extends Service {

    private CodingCalendarAuthenticator mAuthenticator;

    @Override
    public void onCreate() {
        // Create a new coding_calendar_authenticator object
        mAuthenticator = new CodingCalendarAuthenticator(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}
