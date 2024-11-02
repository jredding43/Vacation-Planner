package com.jack.myapp.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapp.R;
import com.jack.myapp.notifications.NotificationHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private Button buttonToVacation;
    private Button buttonToVacationList;
    private Button buttonGenerateReport;
    private FirebaseAuth mAuth;
    private TextView textViewUserEmail;
    private Switch alarmSwitch;
    private SharedPreferences sharedPreferences;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NotificationHelper.createNotificationChannel(this);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize the TextView for displaying user email
        textViewUserEmail = findViewById(R.id.textViewUserEmail);

        // Get the current user
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // Display the current user's email
            String userEmail = currentUser.getEmail();
            textViewUserEmail.setText("Logged in as: " + userEmail);
        } else {
            // If no user is logged in, show a message and redirect to login
            textViewUserEmail.setText("No user is logged in");
            Toast.makeText(this, "No user is logged in. Redirecting to login page.", Toast.LENGTH_SHORT).show();

        }

        NotificationHelper.createNotificationChannel(this);

        // Shared Preferences for saving alarm setting
        sharedPreferences = getSharedPreferences("AlarmPreferences", MODE_PRIVATE);

        // Initialize alarm switch
        alarmSwitch = findViewById(R.id.alarmSwitch);
        boolean isAlarmEnabled = sharedPreferences.getBoolean("isAlarmEnabled", true); // Default is enabled
        alarmSwitch.setChecked(isAlarmEnabled);

        alarmSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("isAlarmEnabled", isChecked);
            editor.apply();
            Toast.makeText(MainActivity.this, "Alarms " + (isChecked ? "Enabled" : "Disabled"), Toast.LENGTH_SHORT).show();
        });


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
