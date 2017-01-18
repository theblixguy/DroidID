package com.ssrij.fingerprintunlockmac;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.util.TypedValue;
import android.view.MenuItem;

public class SettingsActivity extends AppCompatPreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int horizontalMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                2, getResources().getDisplayMetrics());

        int verticalMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                2, getResources().getDisplayMetrics());

        int topMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                (int) getResources().getDimension(R.dimen.activity_vertical_margin) + 30,
                getResources().getDisplayMetrics());

        getListView().setPadding(horizontalMargin, topMargin, horizontalMargin, verticalMargin);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment()).commit();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public static class SettingsFragment extends PreferenceFragment
    {
        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);

            Preference button = findPreference("unlinkDevice");
            button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Unlink DroidID");
                    builder.setMessage("Are you sure you want to unlink this device? You will need to do the same " +
                            "on the Mac app by clicking the fingerprint icon on the status bar and selecting 'Unlink this Mac'.");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                            final SharedPreferences.Editor editor = preferences.edit();
                            editor.clear();
                            editor.apply();
                            NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
                            notificationManager.cancel(999);
                            ActivityCompat.finishAffinity(getActivity());
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    builder.show();
                    return true;
                }
            });

            final CheckBoxPreference toggleNotif = (CheckBoxPreference) findPreference("persistentNotif");
            toggleNotif.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
            {
                @Override
                public boolean onPreferenceChange(final Preference preference, final Object newValue) {
                    if ((Boolean)newValue == true) {
                        PendingIntent contentIntent = PendingIntent.getActivity(getActivity().getApplicationContext(), 0,
                                new Intent(getActivity().getApplicationContext(), ScanActivity.class), PendingIntent.FLAG_CANCEL_CURRENT);

                        NotificationCompat.Builder mBuilder =
                                new NotificationCompat.Builder(getActivity().getApplicationContext())
                                        .setSmallIcon(R.drawable.ic_fingerprint_white_48dp)
                                        .setLargeIcon(BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(),
                                                R.drawable.ic_fingerprint_black_48dp))
                                        .setContentTitle("Unlock Mac")
                                        .setContentText("Press this notification to open unlock UI")
                                        .setPriority(-2)
                                        .setContentIntent(contentIntent)
                                        .setOngoing(true);

                        NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
                        notificationManager.notify(999, mBuilder.build());
                    } else {
                        NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
                        notificationManager.cancel(999);
                    }
                    return true;
                }
            });
        }
    }

    public void unlinkDevice () {

    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}