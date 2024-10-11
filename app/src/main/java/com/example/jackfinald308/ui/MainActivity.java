package com.example.jackfinald308.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.jackfinald308.R;

public class MainActivity extends AppCompatActivity {

    private Button buttonToVacation;
    private Button buttonToVacationList;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
            Intent intent = new Intent(MainActivity.this, VacationList.class);
            startActivity(intent);
        });
    }
}
