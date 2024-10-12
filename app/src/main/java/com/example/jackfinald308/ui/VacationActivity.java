package com.example.jackfinald308.ui;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.jackfinald308.R;
import com.example.jackfinald308.entities.Vacation;
import com.example.jackfinald308.viewmodel.VacationViewModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class VacationActivity extends AppCompatActivity {

    private Spinner spinnerToFlight, spinnerFromFlight;
    private EditText editTextDepartDate, editTextReturnDate, editTextAdults, editTextKids;
    private EditText editTextVacationName; // Vacation name input
    private Button buttonAddUpdateVacation;
    private VacationViewModel vacationViewModel;
    private Spinner spinnerHotel;
    private int vacationId = -1;

    private String[] airports = {
            "Select Destination",
            "Hartsfield-Jackson Atlanta International Airport (ATL)",
            "Beijing Capital International Airport (PEK)",
            "Los Angeles International Airport (LAX)",
            "Dubai International Airport (DXB)",
            "Tokyo Haneda Airport (HND)",
            "Chicago O'Hare International Airport (ORD)",
            "London Heathrow Airport (LHR)",
            "Hong Kong International Airport (HKG)",
            "Paris Charles de Gaulle Airport (CDG)",
            "Amsterdam Schiphol Airport (AMS)",
            "Dallas/Fort Worth International Airport (DFW)",
            "John F. Kennedy International Airport (JFK)",
            "San Francisco International Airport (SFO)",
            "Denver International Airport (DEN)",
            "Shanghai Pudong International Airport (PVG)",
            "Istanbul Airport (IST)",
            "Madrid-Barajas Adolfo Suárez Airport (MAD)",
            "Singapore Changi Airport (SIN)",
            "Kuala Lumpur International Airport (KUL)",
            "Las Vegas McCarran International Airport (LAS)",
            "Newark Liberty International Airport (EWR)",
            "Seattle-Tacoma International Airport (SEA)",
            "Guangzhou Baiyun International Airport (CAN)",
            "Miami International Airport (MIA)",
            "Orlando International Airport (MCO)"
    };

    private String[] hotels = {
            "Select Hotel",
            "Luxury Hotel",
            "Boutique Hotel",
            "Resort Hotel",
            "Business Hotel",
            "Budget Hotel",
            "Hostel",
            "Family-Friendly Hotel",
            "All-Inclusive Hotel",
            "Pet-Friendly Hotel"
    };

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vacation);

        // Initialize Views
        editTextVacationName = findViewById(R.id.editTextVacationName); // Initialize Vacation Name input
        spinnerToFlight = findViewById(R.id.spinnerToFlight);
        spinnerFromFlight = findViewById(R.id.spinnerFromFlight);
        editTextDepartDate = findViewById(R.id.editTextDepartDate);
        editTextReturnDate = findViewById(R.id.editTextReturnDate);
        editTextAdults = findViewById(R.id.editTextAdults);
        editTextKids = findViewById(R.id.editTextKids);
        spinnerHotel = findViewById(R.id.spinnerHotel);
        buttonAddUpdateVacation = findViewById(R.id.buttonAddUpdateChanges);
        vacationViewModel = new VacationViewModel(getApplication());

        // Set up spinners with airport and hotel data
        setupSpinner(spinnerToFlight, airports);
        setupSpinner(spinnerFromFlight, airports);
        setupSpinner(spinnerHotel, hotels);

        // Set OnClickListeners for date fields
        editTextDepartDate.setOnClickListener(v -> showDatePicker(editTextDepartDate));
        editTextReturnDate.setOnClickListener(v -> showDatePicker(editTextReturnDate));

        // Handle add/update vacation
        buttonAddUpdateVacation.setOnClickListener(v -> {
            if (vacationId == -1) {
                addVacation();
            } else {
                updateVacation(vacationId);
            }
        });

        // Check if this is an edit operation
        Intent intent = getIntent();
        if (intent.hasExtra("VACATION_ID")) {
            // We're editing an existing vacation, so set vacationId and pre-fill the fields
            vacationId = intent.getIntExtra("VACATION_ID", -1);
            vacationViewModel.getVacationById(vacationId).observe(this, vacation -> {
                if (vacation != null) {
                    populateFields(vacation);
                }
            });
        }
    }

    private void updateVacation(int vacationId) {
        if (!validateInput()) return;

        // Create a vacation object with the updated information
        Vacation vacation = createVacationFromInput();
        vacation.setVacationID(vacationId);  // Set the ID to update the existing vacation

        // Update the vacation in the ViewModel
        vacationViewModel.update(vacation);

        // Schedule vacation start and end alerts
        scheduleVacationAlerts(vacation);

        Toast.makeText(this, "Vacation updated!", Toast.LENGTH_SHORT).show();
        navigateToVacationDetails(vacation, vacationId);  // Pass the updated vacation to details
    }

    private Vacation createVacationFromInput() {
        String vacationName = editTextVacationName.getText().toString();
        String hotel = spinnerHotel.getSelectedItem().toString();
        String departDate = editTextDepartDate.getText().toString();
        String returnDate = editTextReturnDate.getText().toString();
        String fromFlight = spinnerFromFlight.getSelectedItem().toString();
        String toFlight = spinnerToFlight.getSelectedItem().toString();
        int adults = !TextUtils.isEmpty(editTextAdults.getText()) ? Integer.parseInt(editTextAdults.getText().toString()) : 0;
        int kids = !TextUtils.isEmpty(editTextKids.getText()) ? Integer.parseInt(editTextKids.getText().toString()) : 0;

        return new Vacation(vacationName, hotel, 0, fromFlight, toFlight, departDate, returnDate, adults, kids, "");
    }

    private boolean validateInput() {
        String vacationName = editTextVacationName.getText().toString();

        // Validate required fields (vacation name and dates)
        if (vacationName.isEmpty()) {
            Toast.makeText(this, "Please enter a vacation name", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Validate date formats
        String departDate = editTextDepartDate.getText().toString();
        String returnDate = editTextReturnDate.getText().toString();
        if (!isDateValid(departDate) || !isDateValid(returnDate)) {
            Toast.makeText(this, "Please enter valid dates", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Validate that the return date is after the depart date
        try {
            Date depart = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).parse(departDate);
            Date returnD = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).parse(returnDate);
            if (returnD.before(depart)) {
                Toast.makeText(this, "Return date must be after departure date", Toast.LENGTH_SHORT).show();
                return false;
            }
        } catch (ParseException e) {
            Toast.makeText(this, "Date parsing error", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void setupSpinner(Spinner spinner, String[] data) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void showDatePicker(EditText dateField) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(VacationActivity.this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String date = (selectedMonth + 1) + "/" + selectedDay + "/" + selectedYear;
                    dateField.setText(date);
                }, year, month, day);
        datePickerDialog.show();
    }

    private boolean isDateValid(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
        sdf.setLenient(false);
        try {
            Date parsedDate = sdf.parse(date);
            return parsedDate != null;
        } catch (ParseException e) {
            return false;
        }
    }

    private void addVacation() {
        // Gather data from UI
        String vacationName = editTextVacationName.getText().toString();
        String toFlight = spinnerToFlight.getSelectedItem().toString();
        String fromFlight = spinnerFromFlight.getSelectedItem().toString();
        String hotel = spinnerHotel.getSelectedItem().toString();

        // Validate required fields
        if (vacationName.isEmpty() || "Select Destination".equals(toFlight) || "Select Destination".equals(fromFlight)) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get the depart and return dates
        String departDate = editTextDepartDate.getText().toString();
        String returnDate = editTextReturnDate.getText().toString();

        // Validate date formats
        if (!isDateValid(departDate) || !isDateValid(returnDate)) {
            Toast.makeText(this, "Please enter valid dates", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Parse the dates into Date objects
            Date depart = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).parse(departDate);
            Date returnD = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).parse(returnDate);

            // Validate that the return date is after the depart date
            if (returnD.before(depart)) {
                Toast.makeText(this, "Return date must be after departure date", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (ParseException e) {
            Toast.makeText(this, "Date parsing error", Toast.LENGTH_SHORT).show();
            return;
        }

        // Parse the number of adults and kids
        int adults = Integer.parseInt(editTextAdults.getText().toString());
        int kids = Integer.parseInt(editTextKids.getText().toString());

        // Create vacation object
        Vacation vacation = new Vacation(vacationName, hotel, 0, toFlight, fromFlight, departDate, returnDate, adults, kids, "");

        // Insert vacation in a background thread
        vacationViewModel.insert(vacation, new VacationViewModel.InsertCallback() {
            @Override
            public void onInsertSuccess(int vacationId) {
                Toast.makeText(VacationActivity.this, "Vacation added!", Toast.LENGTH_SHORT).show();

                // Clear input fields
                clearInputFields();

                // Pass to next activity
                Intent intent = new Intent(VacationActivity.this, VacationDetails.class);
                intent.putExtra("vacation", vacation);
                intent.putExtra("vacation_id", vacationId);
                startActivity(intent);
            }

            @Override
            public void onInsertFailure() {
                Toast.makeText(VacationActivity.this, "Error adding vacation.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void clearInputFields() {
        editTextVacationName.setText("");
        editTextDepartDate.setText("");
        editTextReturnDate.setText("");
        editTextAdults.setText("");
        editTextKids.setText("");
    }

    private void populateFields(Vacation vacation) {
        editTextVacationName.setText(vacation.getVacationName());
        spinnerToFlight.setSelection(getSpinnerPositionForFlight(vacation.getToFlight()));
        spinnerFromFlight.setSelection(getSpinnerPositionForFlight(vacation.getFromFlight()));
        editTextDepartDate.setText(vacation.getDepartDate());
        editTextReturnDate.setText(vacation.getReturnDate());
        editTextAdults.setText(String.valueOf(vacation.getAdults()));
        editTextKids.setText(String.valueOf(vacation.getKids()));
        spinnerHotel.setSelection(getSpinnerPositionForHotel(vacation.getHotel()));
    }

    private int getSpinnerPositionForFlight(String flight) {
        ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinnerToFlight.getAdapter();
        return adapter.getPosition(flight);
    }

    private int getSpinnerPositionForHotel(String hotel) {
        ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinnerHotel.getAdapter();
        return adapter.getPosition(hotel);
    }

    private void scheduleVacationAlerts(Vacation vacation) {
        // Implementation for scheduling alerts (start and end date notifications)
    }

    private void navigateToVacationDetails(Vacation vacation, int vacationId) {
        // Navigate to VacationDetails page
        Intent intent = new Intent(VacationActivity.this, VacationDetails.class);
        intent.putExtra("vacation", vacation);  // Pass the updated vacation object
        intent.putExtra("vacation_id", vacationId);  // Pass the vacation ID
        startActivity(intent);
    }
}
