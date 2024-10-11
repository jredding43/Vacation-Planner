package com.example.jackfinald308.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.jackfinald308.R;
import com.example.jackfinald308.entities.Excursion;
import com.example.jackfinald308.viewmodel.VacationViewModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ExcursionActivity extends AppCompatActivity {

    private TextView textViewExcursionName, textViewExcursionDate, textViewExcursionDescription;
    private VacationViewModel vacationViewModel;
    private ArrayList<Excursion> excursions; // List of selected excursions
    private Button buttonEdit, buttonDelete, buttonSave;

    @SuppressLint({"MissingInflatedId", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_excursion); // Set the layout for this activity

        // Initialize Views
        textViewExcursionName = findViewById(R.id.textViewExcursionName);
        textViewExcursionDate = findViewById(R.id.textViewExcursionDate);
        textViewExcursionDescription = findViewById(R.id.textViewExcursionDescription);

        // Initialize VacationViewModel
        vacationViewModel = new ViewModelProvider(this).get(VacationViewModel.class);

        // Get the excursion data from the intent
        excursions = getIntent().getParcelableArrayListExtra("selected_excursions");

        // Get the LinearLayout container to dynamically add TextViews
        LinearLayout excursionContainer = findViewById(R.id.excursionContainer);

        // Check if there are excursions to display
        if (excursions != null && !excursions.isEmpty()) {
            for (Excursion excursion : excursions) {
                TextView excursionText = new TextView(this);
                excursionText.setText(
                        "Name: " + excursion.getName() + "\n" +
                                "Date: " + new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(excursion.getDate()) + "\n" +
                                "Description: " + excursion.getDescription()
                );
                excursionText.setPadding(0, 16, 0, 16); // Add some padding for each TextView
                excursionContainer.addView(excursionText);
            }
        } else {
            Toast.makeText(this, "No excursions found.", Toast.LENGTH_SHORT).show();
        }

        // Initialize buttons
        buttonEdit = findViewById(R.id.buttonEdit);
        buttonDelete = findViewById(R.id.buttonDelete);
        buttonSave = findViewById(R.id.buttonSave);

        // Set OnClickListeners
        setOnClickListeners();
    }

    private void setOnClickListeners() {
        // Navigate to ExcursionEditActivity to edit the excursion
        buttonEdit.setOnClickListener(v -> {
            Intent intentEdit = new Intent(ExcursionActivity.this, ExcursionDetails.class); // Adjusted to use correct activity
            startActivity(intentEdit);
            finish(); // Close this activity if you want to remove it from the back stack
        });

        // Handle excursion deletion
        buttonDelete.setOnClickListener(v -> deleteExcursions());

        // Save all selected excursions and go back to MainActivity
        buttonSave.setOnClickListener(v -> saveExcursions());
    }

    // Delete all selected excursions logic
    private void deleteExcursions() {
        if (excursions != null && !excursions.isEmpty()) {
            for (Excursion excursion : excursions) {
                vacationViewModel.deleteExcursion(excursion);
            }
            Toast.makeText(this, "All excursions deleted!", Toast.LENGTH_SHORT).show();
            // After deletion, return to MainActivity
            Intent intent = new Intent(ExcursionActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // Close the current activity after deletion
        } else {
            Toast.makeText(this, "No excursions to delete!", Toast.LENGTH_SHORT).show();
        }
    }

    // Save all excursions logic
    private void saveExcursions() {
        if (excursions == null || excursions.isEmpty()) {
            Toast.makeText(this, "No excursions to save", Toast.LENGTH_SHORT).show();
            return;
        }

        // Iterate through all selected excursions and save each one
        for (Excursion excursion : excursions) {
            String excursionName = excursion.getName();
            String excursionDescription = excursion.getDescription();
            String excursionDateString = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(excursion.getDate());

            // Convert the date string into a Date object
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
            Date excursionDate;
            try {
                excursionDate = dateFormat.parse(excursionDateString);
            } catch (ParseException e) {
                Toast.makeText(this, "Invalid date format for " + excursionName, Toast.LENGTH_SHORT).show();
                continue; // Skip this excursion if the date format is invalid
            }

            // Update excursion object with new values
            excursion.setDate(excursionDate);
            excursion.setName(excursionName);
            excursion.setDescription(excursionDescription);

            // Save to the database via ViewModel for each excursion
            vacationViewModel.insertExcursion(excursion);
        }

        Toast.makeText(this, "All excursions saved!", Toast.LENGTH_SHORT).show();

        // Redirect back to MainActivity after saving all excursions
        Intent intent = new Intent(ExcursionActivity.this, MainActivity.class);
        startActivity(intent);
        finish();  // Close the current activity
    }
}
