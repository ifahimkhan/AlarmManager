package com.fahim.alarmmanager;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.provider.Settings;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

public class AlarmReceiver extends BroadcastReceiver {
    private static MediaPlayer mp; // Make mp static
    private final int notificationId = 123;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (context == null) {
            return; // Exit early if context is null
        }

        String action = intent != null ? intent.getAction() : null;

        if ("STOP_ALARM".equals(action)) {
            stopAlarm(context);
        } else {
            // Default action: start the alarm
            startAlarm(context, intent);
        }
    }


    private void startAlarm(Context context, Intent intent) {
        String message = intent != null ? intent.getStringExtra("EXTRA_MESSAGE") : "Alarm triggered";

        mp = MediaPlayer.create(context, Settings.System.DEFAULT_ALARM_ALERT_URI);
        mp.setLooping(true);
        mp.start();

        createNotificationChannel(context);

        // Create and display the notification
        Notification notification = createNotification(context, message);
        NotificationManager notificationManager = ContextCompat.getSystemService(context, NotificationManager.class);
        if (notificationManager != null) {
            notificationManager.notify(notificationId, notification);
        }
    }

    private void stopAlarm(Context context) {
        // Stop and release the MediaPlayer if it's initialized and playing
        if (mp != null && mp.isPlaying()) {
            mp.stop();
            mp.reset(); // Clear any lingering state
            mp.release(); // Release resources
            mp = null; // Set to null to avoid potential issues
        }

        // Cancel the notification
        NotificationManager notificationManager = ContextCompat.getSystemService(context, NotificationManager.class);
        if (notificationManager != null) {
            notificationManager.cancel(notificationId);
        }
    }

    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "alarm_channel_id",
                    "Alarm Channel",
                    NotificationManager.IMPORTANCE_HIGH
            );
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    private Notification createNotification(Context context, String message) {
        // Intent to stop the alarm
        Intent stopIntent = new Intent(context, AlarmReceiver.class);
        stopIntent.setAction("STOP_ALARM");

        PendingIntent stopPendingIntent = PendingIntent.getBroadcast(
                context,
                0,
                stopIntent,
                PendingIntent.FLAG_IMMUTABLE
        );

        // Build the notification
        return new NotificationCompat.Builder(context, "alarm_channel_id")
                .setContentTitle("Alarm Triggered")
                .setContentText(message)
                .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .addAction(android.R.drawable.ic_media_pause, "Stop", stopPendingIntent)
                .build();
    }
}