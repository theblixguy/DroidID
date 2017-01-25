package com.ssrij.fingerprintunlockmac;

import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.github.ajalt.reprint.core.Reprint;

import org.apache.commons.lang3.RandomStringUtils;

public class MainActivity extends AppCompatActivity {

    String authCode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setElevation(0);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        final SharedPreferences.Editor editor = preferences.edit();

        if (preferences.getBoolean("isSignedUp", false)) {
            setFragment(new MainFragment());
        } else if (!Reprint.isHardwarePresent()) {
            setFragment(new UnsupportedDeviceFragment());
        } else {
            authCode = generateUniqueCode();
            setFragment(new SetupFragment().newInstance(authCode));

            Firebase myFirebaseRef = new Firebase("https://path_to_firebase_instance" + authCode + "/signedUp");
            myFirebaseRef.setValue(false);
            myFirebaseRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.i("App", dataSnapshot.getValue().toString());
                    if (dataSnapshot.getValue().equals(true)) {
                        editor.putBoolean("isSignedUp", true);
                        editor.putString("authCode", authCode);
                        editor.apply();
                        setFragment(new MainFragment());
                    }
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });
        }

        if (!Reprint.hasFingerprintRegistered()) {
            showFingerprintUnregisteredDialog();
        }

    }

    public String generateUniqueCode() {
        return RandomStringUtils.randomAlphanumeric(6).toUpperCase();
    }

    public void showSensorUnavailableDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Uh oh");
        builder.setMessage("Your device does not support a fingerprint scanner");
        builder.setPositiveButton("Close app", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                finish();
            }
        });
        builder.show();
    }

    public void showFingerprintUnregisteredDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Uh oh");
        builder.setMessage("You haven't registered any fingerprints on this device. Please go to " +
                "fingerprint settings and set one up.");
        builder.setPositiveButton("Open settings", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                startActivity(new Intent(Settings.ACTION_SECURITY_SETTINGS));
            }
        });
        builder.show();
    }

    public void showDownloadClientDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Download Mac app");
        builder.setMessage("You can download the Mac app by visiting: " +
                "suyashsrijan.com/droidid");
        builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.setNegativeButton("Open link", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, "http://www.suyashsrijan.com/droidid");
                startActivity(Intent.createChooser(intent, "Open link"));
            }
        });
        builder.show();
    }

    protected void setFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction =
                fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
        fragmentTransaction.replace(android.R.id.content, fragment);
        fragmentTransaction.commit();
    }

    public void unlinkDevice (View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Unlink DroidID");
        builder.setMessage("Are you sure you want to unlink this device? You will need to do the same " +
                "on the Mac app by clicking the fingerprint icon on the status bar and selecting 'Unlink this Mac'.");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                final SharedPreferences.Editor editor = preferences.edit();
                editor.clear();
                editor.apply();
                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.cancel(999);
                finish();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_about:
                startActivity(new Intent(this, AboutActivity.class));
                return true;
            case R.id.menu_download_mac_client:
                showDownloadClientDialog();
                return true;
            case R.id.menu_help:
                startActivity(new Intent(this, HelpActivity.class));
                return true;
            case R.id.menu_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
