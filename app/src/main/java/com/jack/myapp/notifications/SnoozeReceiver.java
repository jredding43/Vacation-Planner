package com.jack.myapp.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class SnoozeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        long snoozeTime = System.currentTimeMillis() + (10 * 60 * 1000);

        String title = intent.getStringExtra("title");
        String message = intent.getStringExtra("message");
        NotificationHelper.scheduleNotification(context, title, message, snoozeTime);

        // Notify user of the snooze
        Toast.makeText(context, "Snoozed for 10 minutes", Toast.LENGTH_SHORT).show();
    }
}
