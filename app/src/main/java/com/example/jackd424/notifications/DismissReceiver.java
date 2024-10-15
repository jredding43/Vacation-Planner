package com.example.jackd424.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class DismissReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        // Notify user of the dismissal
        Toast.makeText(context, "Notification dismissed", Toast.LENGTH_SHORT).show();
    }
}
