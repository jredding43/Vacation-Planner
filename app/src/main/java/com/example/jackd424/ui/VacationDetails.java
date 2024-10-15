package com.example.jackd424.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.jackd424.R;
import com.example.jackd424.entities.Excursion;
import com.example.jackd424.entities.Vacation;
import com.example.jackd424.notifications.NotificationHelper;
import com.example.jackd424.viewmodel.VacationViewModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class VacationDetails extends AppCompatActivity {

    private TextView textViewVacationName, textViewToFlight, textViewFromFlight, textViewHotel;
    private TextView textViewDepartDate, textViewReturnDate, textViewAdults, textViewKids;
    private VacationViewModel vacationViewModel;
    private Vacation vacation;
    private Button buttonEdit, buttonDelete, buttonShare, buttonSave, buttonViewExcursionDetails;
    private int vacationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vacation_details);

        // Initialize Views
        textViewVacationName = findViewById(R.id.textViewVacationName);
        // textViewToFlight = findViewById(R.id.textViewToFlight);
        // textViewFromFlight = findViewById(R.id.textViewFromFlight);
        textViewDepartDate = findViewById(R.id.textViewDepartDate);
        textViewReturnDate = findViewById(R.id.textViewReturnDate);
        // textViewAdults = findViewById(R.id.textViewAdults);
        // textViewKids = findViewById(R.id.textViewKids);
        textViewHotel = findViewById(R.id.textViewHotel);
        buttonViewExcursionDetails = findViewById(R.id.buttonViewExcursionDetails);

        // Initialize ViewModel
        vacationViewModel = new ViewModelProvider(this).get(VacationViewModel.class);

        // Get the vacation data from the intent
        Intent intent = getIntent();
        vacation = (Vacation) intent.getSerializableExtra("vacation");
        vacationId = intent.getIntExtra("vacation_id", -1);

        if (vacation != null) {
            populateFields(); // Populate UI with vacation data
        }

        // Initialize buttons
        buttonEdit = findViewById(R.id.buttonEdit);
        buttonDelete = findViewById(R.id.buttonDelete);
        buttonShare = findViewById(R.id.buttonShare);
        buttonSave = findViewById(R.id.buttonSave);

        // Set OnClickListeners
        setOnClickListeners();
    }

    private void setOnClickListeners() {
        buttonEdit.setOnClickListener(v -> {
            Intent intentEdit = new Intent(VacationDetails.this, VacationActivity.class);
            intentEdit.putExtra("vacation_id", vacationId);
            startActivity(intentEdit);
            finish(); // Optional: Close this activity if you want to remove it from the back stack
        });

        buttonDelete.setOnClickListener(v -> deleteVacation(vacationId));

        buttonShare.setOnClickListener(v -> shareVacationDetails());

        buttonSave.setOnClickListener(v -> {
            saveVacationDetails();  // Save the vacation details and show toast
        });

        buttonViewExcursionDetails.setOnClickListener(v -> {
            // Create Calendar objects for vacation start and end dates
            Calendar vacationStartDate = Calendar.getInstance();
            Calendar vacationEndDate = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());

            try {
                // Parse the vacation's start and end dates
                vacationStartDate.setTime(sdf.parse(vacation.getDepartDate()));
                vacationEndDate.setTime(sdf.parse(vacation.getReturnDate()));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            // Fetch excursions associated with the vacation
            vacationViewModel.getExcursionsByVacationId(vacationId).observe(this, excursions -> {
                // Check if there are any associated excursions to pass along
                if (excursions != null && !excursions.isEmpty()) {
                    // Create an intent and pass the vacation details and excursions to ExcursionDetails
                    Intent intentExcursions = new Intent(VacationDetails.this, ExcursionDetails.class);
                    intentExcursions.putExtra("vacation_id", vacationId);
                    intentExcursions.putExtra("vacation_start_date", vacationStartDate); // Pass start date
                    intentExcursions.putExtra("vacation_end_date", vacationEndDate);   // Pass end date
                    intentExcursions.putParcelableArrayListExtra("selected_excursions", new ArrayList<>(excursions));  // Pass associated excursions

                    startActivity(intentExcursions);
                } else {
                    // If no excursions are found, pass only the vacation details
                    Intent intentExcursions = new Intent(VacationDetails.this, ExcursionDetails.class);
                    intentExcursions.putExtra("vacation_id", vacationId);
                    intentExcursions.putExtra("vacation_start_date", vacationStartDate); // Pass start date
                    intentExcursions.putExtra("vacation_end_date", vacationEndDate);   // Pass end date
                    startActivity(intentExcursions);
                }
            });
        });

    }

    private void deleteVacation(int vacationId) {
        if (vacationId != -1) {
            vacationViewModel.deleteVacation(vacationId);
            Toast.makeText(this, "Vacation deleted!", Toast.LENGTH_SHORT).show();

            // Navigate back to MainActivity after deletion
            Intent intent = new Intent(VacationDetails.this, MainActivity.class);
            startActivity(intent);
            finish(); // Close the VacationDetails activity
        } else {
            Toast.makeText(this, "Vacation ID is invalid!", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("SetTextI18n")
    private void populateFields() {
        textViewVacationName.setText(vacation.getVacationName());
        // textViewToFlight.setText("To Flight: " + vacation.getToFlight());
        // textViewFromFlight.setText("From Flight: " + vacation.getFromFlight());
        textViewDepartDate.setText("Departure Date: " + vacation.getDepartDate());
        textViewReturnDate.setText("Return Date: " + vacation.getReturnDate());
        // textViewAdults.setText("Number of Adults: " + vacation.getAdults());
        // textViewKids.setText("Number of Kids: " + vacation.getKids());
        textViewHotel.setText("Hotel: " + vacation.getHotel());
    }

    private void shareVacationDetails() {
        String vacationDetails = "Vacation Name: " + vacation.getVacationName() + "\n" +
                "Hotel: " + vacation.getHotel() + "\n" +
                // "To Flight: " + vacation.getToFlight() + "\n" +
                // "From Flight: " + vacation.getFromFlight() + "\n" +
                "Departure Date: " + vacation.getDepartDate() + "\n" +
                "Return Date: " + vacation.getReturnDate() + "\n";
        // "Adults: " + vacation.getAdults() + "\n" +
        // "Kids: " + vacation.getKids();

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, vacationDetails);
        sendIntent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(sendIntent, "Share Vacation Details via");
        startActivity(shareIntent);
    }

    private void saveVacationDetails() {
        if (vacation != null) {
            String updatedVacationName = textViewVacationName.getText().toString();
            String updatedDepartDate = textViewDepartDate.getText().toString().replace("Departure Date: ", "");
            String updatedReturnDate = textViewReturnDate.getText().toString().replace("Return Date: ", "");
            String updatedHotel = textViewHotel.getText().toString().replace("Hotel: ", "");

            // Update vacation object with new details
            vacation.setVacationName(updatedVacationName);
            vacation.setDepartDate(updatedDepartDate);
            vacation.setReturnDate(updatedReturnDate);
            vacation.setHotel(updatedHotel);

            // Save to the database
            vacationViewModel.update(vacation);
            Toast.makeText(this, "Vacation details saved!", Toast.LENGTH_SHORT).show();

            // Schedule an alarm for the vacation start date
            scheduleVacationStartAlarm(vacation);

            // Navigate back to the MainActivity
            Intent intent = new Intent(VacationDetails.this, MainActivity.class);
            startActivity(intent);
            finish();  // Close the current activity
        }
    }

    // Method to schedule the alarm for vacation start date
    private void scheduleVacationStartAlarm(Vacation vacation) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
        Calendar startDate = Calendar.getInstance();

        try {
            startDate.setTime(sdf.parse(vacation.getDepartDate()));
        } catch (ParseException e) {
            e.printStackTrace();
            return;  // Exit if the date cannot be parsed
        }

        long timeInMillis = startDate.getTimeInMillis();
        String startMessage = "Your vacation " + vacation.getVacationName() + " is starting today!";

        // Schedule the alarm using NotificationHelper or AlarmManager
        NotificationHelper.scheduleNotification(this, "Vacation Start Alert", startMessage, timeInMillis);
    }


}
