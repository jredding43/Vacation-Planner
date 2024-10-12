package com.example.jackfinald308.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.jackfinald308.R;
import com.example.jackfinald308.notifications.NotificationHelper;

public class MainActivity extends AppCompatActivity {

    private Button buttonToVacation;
    private Button buttonToVacationList;
    private Button buttonGenerateReport;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NotificationHelper.createNotificationChannel(this);

        // Check if MainActivity was started by an alarm
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("ALARM_TITLE")) {
            String alarmTitle = intent.getStringExtra("ALARM_TITLE");
            String alarmMessage = intent.getStringExtra("ALARM_MESSAGE");

            // Show the alarm in an AlertDialog
            showAlarmDialog(alarmTitle, alarmMessage);
        }

        // Initialize button to go to VacationActivity
        buttonToVacation = findViewById(R.id.buttonToVacation);
        buttonToVacation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, VacationActivity.class);
                startActivity(intent);
            }
        });

        // Initialize button to go to VacationList
        buttonToVacationList = findViewById(R.id.buttonToVacationList);
        buttonToVacationList.setOnClickListener(v -> {
            Intent intent2 = new Intent(MainActivity.this, VacationList.class);
            startActivity(intent2);
        });

        // Initialize button to generate the report
        buttonGenerateReport = findViewById(R.id.buttonGenerateReport);
        buttonGenerateReport.setOnClickListener(v -> {
            Intent intentReport = new Intent(MainActivity.this, ReportActivity.class);
            startActivity(intentReport);
        });
    }

    private void showAlarmDialog(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }
}
