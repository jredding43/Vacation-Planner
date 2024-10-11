package com.example.jackfinald308.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jackfinald308.R;
import com.example.jackfinald308.entities.Excursion;
import com.example.jackfinald308.viewmodel.VacationViewModel;

import java.util.List;

public class VacationList extends AppCompatActivity {

    private VacationViewModel vacationViewModel;
    private Button buttonReturnToMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vacation_list);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        // Adapter for the RecyclerView
        final VacationAdapter adapter = new VacationAdapter();
        recyclerView.setAdapter(adapter);

        // Initialize ViewModel
        vacationViewModel = new ViewModelProvider(this).get(VacationViewModel.class);

        // Observe vacations and excursions
        vacationViewModel.getAllVacations().observe(this, vacations -> {
            vacationViewModel.getAllExcursions().observe(this, excursions -> {
                adapter.setVacations(vacations, excursions); // Pass both vacations and excursions to the adapter
            });
        });

        // Find the button and set an OnClickListener
        buttonReturnToMain = findViewById(R.id.buttonReturnToMain);
        buttonReturnToMain.setOnClickListener(v -> {
            Intent intent = new Intent(VacationList.this, MainActivity.class);
            startActivity(intent);
            finish();  // Close the current activity
        });
    }
}
