package com.ssrij.fingerprintunlockmac;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class MainFragment extends Fragment {

    public MainFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        if (preferences.getBoolean("persistentNotif", true)) {
            showUnlockNotification(getActivity());
        }
        startActivity(new Intent(getActivity(), ScanActivity.class));
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    public void showUnlockNotification(Context context) {

        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                new Intent(context, ScanActivity.class), PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_fingerprint_white_48dp)
                        .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),
                                R.drawable.ic_fingerprint_black_48dp))
                        .setContentTitle("Unlock Mac")
                        .setContentText("Press this notification to open unlock UI")
                        .setPriority(-2)
                        .setContentIntent(contentIntent)
                        .setOngoing(true);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(999, mBuilder.build());
    }
}
