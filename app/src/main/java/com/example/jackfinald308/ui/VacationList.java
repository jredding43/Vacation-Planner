package com.example.jackfinald308.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jackfinald308.R;
import com.example.jackfinald308.database.Repository;
import com.example.jackfinald308.viewmodel.VacationViewModel;

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

        // Initialize the repository and adapter
        Repository repository = new Repository(getApplication());
        final VacationAdapter adapter = new VacationAdapter(this, repository, this);
        recyclerView.setAdapter(adapter);

        // Initialize ViewModel
        vacationViewModel = new ViewModelProvider(this).get(VacationViewModel.class);

        // Observe vacations and update the adapter
        vacationViewModel.getAllVacations().observe(this, vacations -> {
            vacationViewModel.getAllExcursions().observe(this, excursions -> {
                adapter.setVacations(vacations, excursions); // Pass both vacations and excursions to the adapter
            });
        });

        // Set up the Return to Main button
        buttonReturnToMain = findViewById(R.id.buttonReturnToMain);
        buttonReturnToMain.setOnClickListener(v -> {
            Intent intent = new Intent(VacationList.this, MainActivity.class);
            startActivity(intent);
            finish();  // Close the current activity
        });
    }
}
