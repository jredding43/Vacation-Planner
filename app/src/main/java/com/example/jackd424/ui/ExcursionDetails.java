package com.example.jackd424.ui;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.jackd424.R;
import com.example.jackd424.entities.Excursion;
import com.example.jackd424.viewmodel.VacationViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class ExcursionDetails extends AppCompatActivity {

    private HashMap<String, String> excursionDescriptions = new HashMap<>();
    private EditText editTextExcursion;
    private Button buttonAddExcursion, buttonDeleteExcursion, buttonConfirm;
    private ListView listViewSelectedExcursions;
    private ArrayAdapter<String> selectedExcursionsAdapter;
    private ArrayList<Excursion> selectedExcursions = new ArrayList<>();
    private ArrayList<String> selectedExcursionNames = new ArrayList<>();

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
        editTextExcursion = findViewById(R.id.autoCompleteExcursion);
        buttonAddExcursion = findViewById(R.id.buttonAddExcursion);
        buttonDeleteExcursion = findViewById(R.id.buttonDeleteExcursion);
        buttonConfirm = findViewById(R.id.buttonConfirm);
        listViewSelectedExcursions = findViewById(R.id.listViewSelectedExcursions);

        // Initialize ViewModel
        vacationViewModel = new ViewModelProvider(this).get(VacationViewModel.class);

        // Populate excursion descriptions
        populateExcursionDescriptions();

        // Get vacation start date, end date, and vacation ID from the intent
        vacationStartDate = (Calendar) getIntent().getSerializableExtra("vacation_start_date");
        vacationEndDate = (Calendar) getIntent().getSerializableExtra("vacation_end_date");
        vacationId = getIntent().getIntExtra("vacation_id", -1);

        // Get existing excursions if passed from the previous screen
        ArrayList<Excursion> existingExcursions = getIntent().getParcelableArrayListExtra("selected_excursions");
        if (existingExcursions != null) {
            selectedExcursions.addAll(existingExcursions);
            for (Excursion excursion : existingExcursions) {
                String formattedDate = "No date available";
                if (excursion.getDate() != null) {
                    formattedDate = new SimpleDateFormat("MM-dd-yyyy", Locale.getDefault()).format(excursion.getDate());
                }
                selectedExcursionNames.add(excursion.getName() + " - " + formattedDate);
            }
        }

        // Set up ListView adapter for selected excursions
        selectedExcursionsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, selectedExcursionNames);
        listViewSelectedExcursions.setAdapter(selectedExcursionsAdapter);

        // Add excursion button click listener
        buttonAddExcursion.setOnClickListener(v -> {
            String selectedExcursionName = editTextExcursion.getText().toString().trim();
            if (selectedExcursionName.isEmpty()) {
                Toast.makeText(this, "Please enter an excursion", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!selectedExcursionNames.contains(selectedExcursionName)) {
                String description = excursionDescriptions.get(selectedExcursionName);
                Excursion newExcursion = new Excursion(selectedExcursionName, description, vacationId, null);
                selectedExcursions.add(newExcursion);
                selectedExcursionNames.add(newExcursion.getName() + " - No date available");  // Set default message for date
                showDatePickerDialog(newExcursion);  // Allow the user to pick a date for the excursion
                selectedExcursionsAdapter.notifyDataSetChanged();  // Update the ListView
                editTextExcursion.setText(""); // Clear the EditText after adding the excursion
            } else {
                Toast.makeText(this, "Excursion already added", Toast.LENGTH_SHORT).show();
            }
        });

        // Delete excursion button click listener
        buttonDeleteExcursion.setOnClickListener(v -> {
            if (!selectedExcursions.isEmpty()) {
                int lastPosition = selectedExcursions.size() - 1;
                if (lastPosition >= 0) {
                    selectedExcursions.remove(lastPosition);
                    selectedExcursionNames.remove(lastPosition);
                    selectedExcursionsAdapter.notifyDataSetChanged();
                    Toast.makeText(this, "Deleted last excursion!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Unable to delete the excursion. Index out of bounds!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "No excursions to delete!", Toast.LENGTH_SHORT).show();
            }
        });

        // Confirm button click listener to send selected excursions to ExcursionActivity
        buttonConfirm.setOnClickListener(v -> {
            Intent intent = new Intent(ExcursionDetails.this, ExcursionActivity.class);
            if (selectedExcursions != null && !selectedExcursions.isEmpty()) {
                // Pass selected excursions
                intent.putParcelableArrayListExtra("selected_excursions", selectedExcursions);
            } else {
                // Pass an empty list if no excursions are selected
                intent.putParcelableArrayListExtra("selected_excursions", new ArrayList<>());
                Toast.makeText(this, "No excursions to pass.", Toast.LENGTH_SHORT).show();
            }
            intent.putExtra("vacation_id", vacationId);
            startActivity(intent);
            finish();
        });

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

            if (isDateWithinVacation(selectedDate)) {
                excursion.setDate(selectedDate.getTime());  // Set date in the Excursion object

                int index = selectedExcursions.indexOf(excursion);
                if (index >= 0 && index < selectedExcursionNames.size()) {
                    String formattedDate = new SimpleDateFormat("MM-dd-yyyy", Locale.getDefault()).format(excursion.getDate());
                    selectedExcursionNames.set(index, excursion.getName() + " - " + formattedDate);  // Update with selected date
                    selectedExcursionsAdapter.notifyDataSetChanged();  // Refresh the ListView
                }
            } else {
                Toast.makeText(this, "Excursion date must be within the vacation period!", Toast.LENGTH_LONG).show();
            }
        }, year, month, day);

        datePickerDialog.show();
    }

    private boolean isDateWithinVacation(Calendar selectedDate) {
        return !selectedDate.before(vacationStartDate) && !selectedDate.after(vacationEndDate);
    }
}
