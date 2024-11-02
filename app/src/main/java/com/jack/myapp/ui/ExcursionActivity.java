package com.jack.myapp.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapp.R;
import com.jack.myapp.entities.Excursion;
import com.jack.myapp.notifications.NotificationHelper;
import com.jack.myapp.viewmodel.VacationViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class ExcursionActivity extends AppCompatActivity {

    private TextView textViewExcursionName, textViewExcursionDate;
    private VacationViewModel vacationViewModel;
    private ArrayList<Excursion> excursions; // List of selected excursions
    private Button buttonEdit, buttonDelete, buttonSave;
    private int vacationId;

    @SuppressLint({"MissingInflatedId", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_excursion); // Set the layout for this activity

        // Initialize Views
        textViewExcursionName = findViewById(R.id.textViewExcursionName);
        textViewExcursionDate = findViewById(R.id.textViewExcursionDate);

        // Initialize VacationViewModel
        vacationViewModel = new ViewModelProvider(this).get(VacationViewModel.class);

        // Get the excursion data from the intent
        excursions = getIntent().getParcelableArrayListExtra("selected_excursions");
        vacationId = getIntent().getIntExtra("vacation_id", -1);

        // Get the LinearLayout container to dynamically add TextViews
        LinearLayout excursionContainer = findViewById(R.id.excursionContainer);

        // Check if there are excursions to display
        if (excursions != null && !excursions.isEmpty()) {
            for (Excursion excursion : excursions) {
                TextView excursionText = new TextView(this);

                // Check if the excursion date is null
                String formattedDate;
                if (excursion.getDate() != null) {
                    formattedDate = new SimpleDateFormat("MM-dd-yyyy", Locale.getDefault()).format(excursion.getDate());
                } else {
                    formattedDate = "No date available";  // Handle null date case
                }

                // Set the text with excursion details
                excursionText.setText(
                        "Name: " + excursion.getName() + "\n" +
                                "Date: " + formattedDate
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
        // Navigate to ExcursionDetails to edit the excursion
        buttonEdit.setOnClickListener(v -> {
            Intent intentEdit = new Intent(ExcursionActivity.this, ExcursionDetails.class);

            // Pass the vacationId back to ExcursionDetails
            intentEdit.putExtra("vacation_id", vacationId);

            // You can also pass the selected excursions if needed
            if (excursions != null && !excursions.isEmpty()) {
                intentEdit.putParcelableArrayListExtra("selected_excursions", excursions);
            }

            startActivity(intentEdit);
            finish();
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
        int vacationId = getIntent().getIntExtra("vacation_id", -1);

        if (vacationId == -1) {
            Toast.makeText(this, "Invalid vacation ID.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Clear all existing excursions for the vacation (overwrite logic)
        vacationViewModel.clearExcursionsForVacation(vacationId);

        if (excursions == null || excursions.isEmpty()) {
            // If no excursions are selected, just clear and notify the user
            Toast.makeText(this, "No excursions selected, cleared any existing excursions.", Toast.LENGTH_SHORT).show();
        } else {
            // Iterate through all selected excursions and save each one
            for (Excursion excursion : excursions) {
                excursion.setVacationId(vacationId);  // Ensure the correct vacation ID is set
                vacationViewModel.insertExcursion(excursion);
                // Schedule an alert for each excursion
                scheduleExcursionAlert(excursion);
            }
            Toast.makeText(this, "All excursions saved and updated!", Toast.LENGTH_SHORT).show();
        }

        // Redirect back to MainActivity after saving all excursions
        Intent intent = new Intent(ExcursionActivity.this, MainActivity.class);
        startActivity(intent);
        finish();  // Close the current activity
    }


    // Schedule alerts for the excursions
    private void scheduleExcursionAlert(Excursion excursion) {
        if (excursion.getDate() != null) {
            long timeInMillis = excursion.getDate().getTime(); // Get the time in milliseconds
            String message = "Excursion " + excursion.getName() + " is happening today!";
            NotificationHelper.scheduleNotification(this, "Excursion Alert", message, timeInMillis);
        }
    }

}
