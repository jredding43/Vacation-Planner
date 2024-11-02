package com.jack.myapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapp.R;
import com.jack.myapp.database.Repository;
import com.jack.myapp.entities.Vacation;
import com.jack.myapp.viewmodel.VacationViewModel;

import java.util.List;
import java.util.stream.Collectors;

public class VacationList extends AppCompatActivity {

    private VacationViewModel vacationViewModel;
    private Button buttonReturnToMain;
    private EditText searchVacationInput;
    private Button buttonSearchVacation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vacation_list);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        // Initialize the repository and adapter
        Repository repository = new Repository(getApplication());
        final VacationAdapter adapter = new VacationAdapter(this, repository, this); // Pass LifecycleOwner (this)
        recyclerView.setAdapter(adapter);

        // Initialize ViewModel
        vacationViewModel = new ViewModelProvider(this).get(VacationViewModel.class);

        // Observe vacations and excursions
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

        // Set up the search functionality
        searchVacationInput = findViewById(R.id.search_vacation);
        buttonSearchVacation = findViewById(R.id.button_search_vacation);

        buttonSearchVacation.setOnClickListener(v -> {
            String query = searchVacationInput.getText().toString().trim();
            if (!TextUtils.isEmpty(query)) {
                filterVacationsByName(query, adapter);
            } else {
                // If the query is empty, display the full list
                vacationViewModel.getAllVacations().observe(this, vacations -> {
                    vacationViewModel.getAllExcursions().observe(this, excursions -> {
                        adapter.setVacations(vacations, excursions);
                    });
                });
            }
        });
    }

    // Filter the vacation list based on the query and update the adapter
    private void filterVacationsByName(String query, VacationAdapter adapter) {
        vacationViewModel.getAllVacations().observe(this, vacations -> {
            List<Vacation> filteredVacations = vacations.stream()
                    .filter(vacation -> vacation.getVacationName().toLowerCase().contains(query.toLowerCase()))
                    .collect(Collectors.toList());

            vacationViewModel.getAllExcursions().observe(this, excursions -> {
                adapter.setVacations(filteredVacations, excursions);
            });
        });
    }
}
