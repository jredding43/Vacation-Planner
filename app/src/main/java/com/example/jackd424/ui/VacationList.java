package com.example.jackd424.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jackd424.R;
import com.example.jackd424.database.Repository;
import com.example.jackd424.viewmodel.VacationViewModel;

public class VacationList extends AppCompatActivity {

    private VacationViewModel vacationViewModel;
    private Button buttonReturnToMain;
    private RecyclerView recyclerView;
    private VacationAdapter adapter;
    private Repository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vacation_list);

        // Initialize the repository and adapter
        repository = new Repository(getApplication());
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        adapter = new VacationAdapter(this, repository, this);
        recyclerView.setAdapter(adapter);

        // Initialize ViewModel
        vacationViewModel = new ViewModelProvider(this).get(VacationViewModel.class);

        // Observe vacations and update the adapter
        vacationViewModel.getAllVacations().observe(this, vacations -> {
            vacationViewModel.getAllExcursions().observe(this, excursions -> {
                adapter.setVacations(vacations, excursions); // Pass both vacations and excursions to the adapter
            });
        });

        // Search functionality
        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        SearchView searchView = findViewById(R.id.searchVacation);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.filter(query);  // Apply the search filter
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.filter(newText);  // Apply the search filter as the user types
                return false;
            }
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
