package com.example.jackd424.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.example.jackd424.notifications.NotificationHelper;

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // Check shared preferences to see if alarms are enabled
        SharedPreferences sharedPreferences = context.getSharedPreferences("AlarmPreferences", Context.MODE_PRIVATE);
        boolean isAlarmEnabled = sharedPreferences.getBoolean("isAlarmEnabled", true); // Default is enabled

        if (!isAlarmEnabled) {
            return; // If alarms are disabled, do nothing
        }

        // Handle the notification logic here
        String message = intent.getStringExtra("message");
        if (message != null) {
            NotificationHelper.showNotification(context, "Notification", message);
        } else {
            Toast.makeText(context, "No message received for notification.", Toast.LENGTH_SHORT).show();
        }
    }
}
