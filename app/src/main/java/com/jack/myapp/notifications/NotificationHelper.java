package com.jack.myapp.notifications;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.jack.myapp.R;

import android.os.Build;

import androidx.core.app.NotificationCompat;

public class NotificationHelper {

    private static final String CHANNEL_ID = "vacation_alerts";

    // Create Notification Channel
    public static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Vacation Alerts";
            String description = "Channel for vacation and excursion alerts";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }
    }

    // Show notification with Snooze and Dismiss actions
    public static void showNotification(Context context, String title, String message) {
        createNotificationChannel(context);

        // Adding actions for snooze and dismiss
        PendingIntent snoozeIntent = getSnoozePendingIntent(context, title, message);
        PendingIntent dismissIntent = getDismissPendingIntent(context);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .addAction(R.drawable.ic_snooze, "Snooze", snoozeIntent)
                .addAction(R.drawable.ic_dismiss, "Dismiss", dismissIntent)
                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }

    // Schedule a notification for a specific time
    @SuppressLint("ScheduleExactAlarm")
    public static void scheduleNotification(Context context, String title, String message, long timeInMillis) {
        Intent intent = new Intent(context, NotificationReceiver.class);
        intent.putExtra("message", message);
        intent.putExtra("title", title);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, (int) System.currentTimeMillis(), intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent);
        }
    }

    // Get PendingIntent for Snooze action
    private static PendingIntent getSnoozePendingIntent(Context context, String title, String message) {
        Intent snoozeIntent = new Intent(context, SnoozeReceiver.class);
        snoozeIntent.putExtra("title", title);
        snoozeIntent.putExtra("message", message);
        return PendingIntent.getBroadcast(context, (int) System.currentTimeMillis(), snoozeIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
    }

    // Get PendingIntent for Dismiss action
    private static PendingIntent getDismissPendingIntent(Context context) {
        Intent dismissIntent = new Intent(context, DismissReceiver.class);
        return PendingIntent.getBroadcast(context, (int) System.currentTimeMillis(), dismissIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
    }
}
