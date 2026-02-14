package com.mowtiie.faithful.util;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.mowtiie.faithful.R;
import com.mowtiie.faithful.ui.activities.MainActivity;

public class NotificationUtil {

    public static final String CHANNEL_THOUGHT_ID = "QUICK_THOUGHT";
    public static final String CHANNEL_THOUGHT_NAME = "Quick Thought";
    public static final int CHANNEL_THOUGHT_IMPORTANCE = NotificationManager.IMPORTANCE_LOW;

    public static void createChannels(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel deadlineChannel = new NotificationChannel(CHANNEL_THOUGHT_ID, CHANNEL_THOUGHT_NAME, CHANNEL_THOUGHT_IMPORTANCE);
            notificationManager.createNotificationChannel(deadlineChannel);
        }
    }

    public static void create(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS)
                        != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("OPEN_DIALOG", true);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_THOUGHT_ID)
                .setSmallIcon(R.drawable.ic_thought)
                .setContentTitle("What's your thought?")
                .setContentText("Tap to quickly write it down, so you won't forget about it.")
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setOngoing(true)
                .setContentIntent(pendingIntent)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(1, builder.build());
    }
}