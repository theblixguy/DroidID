package com.ssrij.fingerprintunlockmac;

import android.app.Application;

import com.firebase.client.Firebase;
import com.github.ajalt.reprint.core.Reprint;
import com.parse.Parse;
import com.parse.ParseInstallation;

public class DroidIDApplication extends Application {

        @Override
        public void onCreate() {
            super.onCreate();
            Firebase.setAndroidContext(this);
            Reprint.initialize(this);
            Parse.initialize(this, "APP_KEY", "APP_SECRET");
            ParseInstallation.getCurrentInstallation().saveInBackground();
        }
}
