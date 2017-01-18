package com.ssrij.fingerprintunlockmac;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

public class StartupBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            if (preferences.getBoolean("isSignedUp", false)) {
                if (preferences.getBoolean("persistentNotif", false)) {
                    showUnlockNotification(context);
                }
            }
        }
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
