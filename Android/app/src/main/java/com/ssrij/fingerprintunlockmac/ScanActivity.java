package com.ssrij.fingerprintunlockmac;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.github.ajalt.reprint.core.AuthenticationFailureReason;
import com.github.ajalt.reprint.core.AuthenticationListener;
import com.github.ajalt.reprint.core.Reprint;
import com.mattprecious.swirl.SwirlView;

public class ScanActivity extends AppCompatActivity {

    String authCode;
    TextView result;
    SwirlView sw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        setContentView(R.layout.activity_scan);
        Reprint.initialize(this);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        authCode = preferences.getString("authCode", "");
        Firebase.setAndroidContext(this);
        getSupportActionBar().setTitle("Authentication");
        getSupportActionBar().setElevation(0);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            sw = (SwirlView)findViewById(R.id.sw);
            sw.setState(SwirlView.State.ON, true);
        }

        result = (TextView)findViewById(R.id.textViewStatus);

        Reprint.authenticate(new AuthenticationListener() {
            @Override
            public void onSuccess(int moduleTag) {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    sw.setState(SwirlView.State.OFF, true);
                }
                result.setText("Authenticated successfully!");
                Firebase myFirebaseRef = new Firebase("http://your_firebase_instance_url" + authCode + "/unlockMac");
                myFirebaseRef.setValue(true);
                Reprint.cancelAuthentication();
            }

            @Override
            public void onFailure(AuthenticationFailureReason failureReason, boolean fatal, CharSequence errorMessage, int moduleTag, int errorCode) {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    sw.setState(SwirlView.State.ERROR, true);

                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            sw.setState(SwirlView.State.ON);
                        }
                    }, 1000);
                }
                result.setText(errorMessage);
            }
        }, Reprint.DEFAULT_RESTART_COUNT);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}

