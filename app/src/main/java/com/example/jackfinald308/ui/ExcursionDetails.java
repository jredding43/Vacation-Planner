package com.example.jackfinald308.ui;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.jackfinald308.R;
import com.example.jackfinald308.entities.Excursion;
import com.example.jackfinald308.viewmodel.VacationViewModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class ExcursionDetails extends AppCompatActivity {

    private HashMap<String, String> excursionDescriptions = new HashMap<>();
    private ListView listViewExcursions;
    private Button buttonConfirm, buttonDelete;
    private ArrayList<Excursion> selectedExcursions = new ArrayList<>();
    private boolean[] checkedItems;
    private String[] excursionNames = {
            "Snorkeling Adventure", "Mountain Hiking", "City Tour",
            "Safari Experience", "Cultural Cooking Class", "Beach Day",
            "Wine Tasting Tour", "Wildlife Safari", "Hot Air Balloon Ride",
            "Scuba Diving"
    };

    // Vacation and excursion data
    private Calendar vacationStartDate;
    private Calendar vacationEndDate;
    private int vacationId;
    private VacationViewModel vacationViewModel;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_excursion_details);

        // Initialize views
        listViewExcursions = findViewById(R.id.listViewExcursions);
        buttonConfirm = findViewById(R.id.buttonConfirm);
        buttonDelete = findViewById(R.id.buttonDelete);

        // Initialize ViewModel
        vacationViewModel = new ViewModelProvider(this).get(VacationViewModel.class);

        // Populate excursion descriptions
        populateExcursionDescriptions();

        // Restore savedInstanceState or get values from Intent
        if (savedInstanceState != null) {
            vacationId = savedInstanceState.getInt("vacation_id", -1);
            vacationStartDate = (Calendar) savedInstanceState.getSerializable("vacation_start_date");
            vacationEndDate = (Calendar) savedInstanceState.getSerializable("vacation_end_date");
        } else {
            vacationStartDate = (Calendar) getIntent().getSerializableExtra("vacation_start_date");
            vacationEndDate = (Calendar) getIntent().getSerializableExtra("vacation_end_date");
            vacationId = getIntent().getIntExtra("vacation_id", -1);
        }

        // Handle missing vacation dates
        if (vacationStartDate == null || vacationEndDate == null) {
            Toast.makeText(this, "Error: Missing vacation start or end date.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Initialize list view with excursions
        ArrayList<Excursion> receivedExcursions = getIntent().getParcelableArrayListExtra("selected_excursions");
        if (receivedExcursions != null) {
            selectedExcursions.addAll(receivedExcursions);
        }

        // Initialize checkedItems
        checkedItems = new boolean[excursionNames.length];
        for (int i = 0; i < excursionNames.length; i++) {
            final String excursionName = excursionNames[i];
            if (receivedExcursions != null) {
                checkedItems[i] = receivedExcursions.stream()
                        .anyMatch(excursion -> excursion.getName().equals(excursionName));
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice, excursionNames);
        listViewExcursions.setAdapter(adapter);
        listViewExcursions.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        // Set checked states for the ListView
        for (int i = 0; i < checkedItems.length; i++) {
            listViewExcursions.setItemChecked(i, checkedItems[i]);
        }

        // Add item click listener
        listViewExcursions.setOnItemClickListener((parent, view, position, id) -> {
            String excursionName = excursionNames[position];
            if (listViewExcursions.isItemChecked(position)) {
                String description = excursionDescriptions.get(excursionName);
                Excursion newExcursion = new Excursion(excursionName, description, vacationId, null);
                if (!selectedExcursions.contains(newExcursion)) {
                    selectedExcursions.add(newExcursion);
                    showDatePickerDialog(newExcursion);
                }
            } else {
                selectedExcursions.removeIf(excursion -> excursion.getName().equals(excursionName));
            }
        });

        // Handle confirm button click
        buttonConfirm.setOnClickListener(v -> {
            // Navigate to ExcursionActivity with the selected excursions
            Intent intent = new Intent(ExcursionDetails.this, ExcursionActivity.class);

            // Pass the selected excursions (even if none are selected)
            intent.putParcelableArrayListExtra("selected_excursions", selectedExcursions);

            // Pass the vacation ID
            intent.putExtra("vacation_id", vacationId);

            // Show a toast message only if no excursions were selected
            if (selectedExcursions.isEmpty()) {
                Toast.makeText(this, "No excursions selected. Proceeding without excursions.", Toast.LENGTH_SHORT).show();
            }

            // Start the next activity
            startActivity(intent);
            finish();
        });

    }

    // Override onSaveInstanceState to save vacationId and vacation dates during configuration changes
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("vacation_id", vacationId);
        outState.putSerializable("vacation_start_date", vacationStartDate);
        outState.putSerializable("vacation_end_date", vacationEndDate);
    }

    private void populateExcursionDescriptions() {
        excursionDescriptions.put("Snorkeling Adventure", "Explore the vibrant underwater life.");
        excursionDescriptions.put("Mountain Hiking", "Enjoy a scenic hike in the mountains.");
        excursionDescriptions.put("City Tour", "Visit famous landmarks in the city.");
        excursionDescriptions.put("Safari Experience", "Experience wildlife up close.");
        excursionDescriptions.put("Cultural Cooking Class", "Learn to cook traditional dishes.");
        excursionDescriptions.put("Beach Day", "Relax on the beautiful sandy beaches.");
        excursionDescriptions.put("Wine Tasting Tour", "Sample fine wines from local vineyards.");
        excursionDescriptions.put("Wildlife Safari", "Observe exotic wildlife in their natural habitat.");
        excursionDescriptions.put("Hot Air Balloon Ride", "Soar high above the landscape in a hot air balloon.");
        excursionDescriptions.put("Scuba Diving", "Dive into the deep blue sea and explore underwater life.");
    }

    private void showDatePickerDialog(Excursion excursion) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, selectedYear, selectedMonth, selectedDay) -> {
            Calendar selectedDate = Calendar.getInstance();
            selectedDate.set(selectedYear, selectedMonth, selectedDay);

            // Check if the selected date is within the vacation period
            if (isDateWithinVacation(selectedDate)) {
                excursion.setDate(selectedDate.getTime());
                Toast.makeText(this, "Date set for " + excursion.getName(), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Excursion date must be within the vacation period!", Toast.LENGTH_LONG).show();
            }
        }, year, month, day);

        // Set minimum and maximum dates for the date picker
        datePickerDialog.getDatePicker().setMinDate(vacationStartDate.getTimeInMillis());
        datePickerDialog.getDatePicker().setMaxDate(vacationEndDate.getTimeInMillis());

        datePickerDialog.show();
    }

    private boolean isDateWithinVacation(Calendar selectedDate) {
        setTimeToMidnight(selectedDate);
        setTimeToMidnight(vacationStartDate);
        setTimeToMidnight(vacationEndDate);

        return !selectedDate.before(vacationStartDate) && !selectedDate.after(vacationEndDate);
    }

    // Helper function to set the time of a Calendar object to midnight (00:00:00)
    private void setTimeToMidnight(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }
}
