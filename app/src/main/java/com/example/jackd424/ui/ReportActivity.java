package com.example.jackd424.ui;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import com.example.jackd424.R;
import com.example.jackd424.entities.Vacation;
import com.example.jackd424.viewmodel.VacationViewModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReportActivity extends AppCompatActivity {

    private Button buttonStartDate, buttonEndDate, buttonGenerateReport, buttonReturnToMain;
    private TextView dateTimeStamp;
    private TableLayout tableLayoutReport;

    private Calendar startDateCalendar, endDateCalendar;
    private VacationViewModel vacationViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        // Initialize views
        buttonStartDate = findViewById(R.id.buttonStartDate);
        buttonEndDate = findViewById(R.id.buttonEndDate);
        buttonGenerateReport = findViewById(R.id.buttonGenerateReport);
        buttonReturnToMain = findViewById(R.id.buttonReturnToMain);
        dateTimeStamp = findViewById(R.id.dateTimeStamp);
        tableLayoutReport = findViewById(R.id.tableLayoutReport);

        vacationViewModel = new ViewModelProvider(this).get(VacationViewModel.class);

        // Handle Date Picker for Start Date
        buttonStartDate.setOnClickListener(v -> showDatePickerDialog(true));

        // Handle Date Picker for End Date
        buttonEndDate.setOnClickListener(v -> showDatePickerDialog(false));

        // Handle Report Generation
        buttonGenerateReport.setOnClickListener(v -> {
            if (startDateCalendar == null || endDateCalendar == null) {
                Toast.makeText(this, "Please select both start and end dates", Toast.LENGTH_SHORT).show();
            } else {
                generateReport();
            }
        });

        // Handle Return to MainActivity
        buttonReturnToMain.setOnClickListener(v -> {
            Intent intent = new Intent(ReportActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void showDatePickerDialog(boolean isStartDate) {
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, monthOfYear, dayOfMonth) -> {
                    Calendar selectedDate = Calendar.getInstance();
                    selectedDate.set(year, monthOfYear, dayOfMonth);

                    if (isStartDate) {
                        startDateCalendar = selectedDate;
                        buttonStartDate.setText(new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(startDateCalendar.getTime()));
                    } else {
                        endDateCalendar = selectedDate;
                        buttonEndDate.setText(new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(endDateCalendar.getTime()));
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void generateReport() {
        // Clear the previous report
        tableLayoutReport.removeAllViews();

        String reportTitle = "Vacation Report";
        String dateTime = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.getDefault()).format(new Date());

        // Display Report Title and Timestamp
        dateTimeStamp.setText("Generated on: " + dateTime);

        // Add header row
        addHeaderRow(reportTitle);

        // Retrieve vacations from the ViewModel
        LiveData<List<Vacation>> vacationListLiveData = vacationViewModel.getAllVacations();

        vacationListLiveData.observe(this, vacations -> {
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());

            // Loop through each vacation
            for (Vacation vacation : vacations) {
                try {
                    // Parse the vacation start and end dates
                    Date vacationStartDate = sdf.parse(vacation.getDepartDate());
                    Date vacationEndDate = sdf.parse(vacation.getReturnDate());

                    // Ensure both start and end dates are non-null
                    if (vacationStartDate != null && vacationEndDate != null) {

                        // Include vacations where the start or end date is within the selected range (inclusive)
                        if (!vacationStartDate.before(startDateCalendar.getTime()) && !vacationEndDate.after(endDateCalendar.getTime())) {
                            addReportRow(new String[]{
                                    vacation.getVacationName(),
                                    vacation.getDepartDate(),
                                    vacation.getReturnDate(),
                            });
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }



    // Adds header row to the table layout
    private void addHeaderRow(String reportTitle) {
        TableRow headerRow = new TableRow(this);

        TextView vacationTitle = new TextView(this);
        vacationTitle.setText("Vacation Title");
        vacationTitle.setPadding(8, 8, 8, 8);
        headerRow.addView(vacationTitle);

        TextView startDate = new TextView(this);
        startDate.setText("Start Date");
        startDate.setPadding(8, 8, 8, 8);
        headerRow.addView(startDate);

        TextView endDate = new TextView(this);
        endDate.setText("End Date");
        endDate.setPadding(8, 8, 8, 8);
        headerRow.addView(endDate);

        TextView hotel = new TextView(this);
        hotel.setText("Location");
        hotel.setPadding(8, 8, 8, 8);
        headerRow.addView(hotel);

        tableLayoutReport.addView(headerRow);
    }

    // Adds a row to the table layout for a vacation
    private void addReportRow(String[] rowValues) {
        TableRow row = new TableRow(this);

        for (String value : rowValues) {
            TextView textView = new TextView(this);
            textView.setText(value);
            textView.setPadding(8, 8, 8, 8);
            row.addView(textView);
        }

        tableLayoutReport.addView(row);
    }
}
