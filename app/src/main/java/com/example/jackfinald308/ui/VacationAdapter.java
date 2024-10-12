package com.example.jackfinald308.ui;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jackfinald308.R;
import com.example.jackfinald308.database.Repository;
import com.example.jackfinald308.entities.Excursion;
import com.example.jackfinald308.entities.Vacation;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class VacationAdapter extends RecyclerView.Adapter<VacationAdapter.VacationViewHolder> {

    private List<Vacation> vacationList;
    private List<Excursion> excursionList;
    private Context context;
    private Repository repository;
    private LifecycleOwner lifecycleOwner;
    private List<Vacation> vacationListFull;  // For search filtering

    public VacationAdapter(Context context, Repository repository, LifecycleOwner lifecycleOwner) {
        this.context = context;
        this.repository = repository;
        this.lifecycleOwner = lifecycleOwner;
        this.vacationList = new ArrayList<>();
        this.excursionList = new ArrayList<>();
        this.vacationListFull = new ArrayList<>();  // For search filtering
    }

    @NonNull
    @Override
    public VacationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.vacation_item, parent, false);
        return new VacationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VacationViewHolder holder, int position) {
        Vacation vacation = vacationList.get(position);

        // Set vacation name
        holder.vacationNameTextView.setText(vacation.getVacationName());

        // Set vacation dates
        String vacationDates = "From: " + vacation.getDepartDate() + " To: " + vacation.getReturnDate();
        holder.vacationDatesTextView.setText(vacationDates);

        // Set excursions (each excursion with its date on a new line)
        String excursions = getExcursionNamesForVacation(vacation.getVacationID());
        if (excursions != null && !excursions.isEmpty()) {
            holder.excursionListTextView.setText(excursions);
        } else {
            holder.excursionListTextView.setText("No excursions selected");
        }

        // Handle the Delete button click
        holder.deleteButton.setOnClickListener(v -> {
            repository.hasAssociatedExcursions(vacation.getVacationID()).observe(lifecycleOwner, hasExcursions -> {
                if (hasExcursions) {
                    new AlertDialog.Builder(context)
                            .setTitle("Cannot Delete Vacation")
                            .setMessage("This vacation has associated excursions and cannot be deleted.")
                            .setPositiveButton(android.R.string.ok, null)
                            .show();
                } else {
                    new AlertDialog.Builder(context)
                            .setTitle("Delete Vacation")
                            .setMessage("Are you sure you want to delete this vacation?")
                            .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                                repository.deleteVacation(vacation.getVacationID(), new Repository.VacationDeleteCallback() {
                                    @Override
                                    public void onVacationHasExcursions(int vacationId) {
                                        Toast.makeText(context, "Cannot delete vacation. It has associated excursions.", Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onVacationDeleteSuccess() {
                                        Toast.makeText(context, "Vacation deleted", Toast.LENGTH_SHORT).show();
                                        vacationList.remove(vacation);
                                        notifyDataSetChanged();
                                    }
                                });
                            })
                            .setNegativeButton(android.R.string.no, null)
                            .show();
                }
            });
        });

        holder.editExcursionButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, ExcursionDetails.class);
            intent.putExtra("vacation_id", vacation.getVacationID());  // Pass the vacation ID

            // Assuming your Vacation object contains start and end date information
            Calendar startDate = getCalendarFromString(vacation.getDepartDate());
            Calendar endDate = getCalendarFromString(vacation.getReturnDate());

            // Pass the vacation start and end dates to the ExcursionDetails activity
            intent.putExtra("vacation_start_date", startDate);  // Ensure you pass this value
            intent.putExtra("vacation_end_date", endDate);      // Ensure you pass this value

            ArrayList<Excursion> excursionsForVacation = new ArrayList<>();
            for (Excursion excursion : excursionList) {
                if (excursion.getVacationId() == vacation.getVacationID()) {
                    excursionsForVacation.add(excursion);
                }
            }

            // Pass the selected excursions as Parcelable
            intent.putParcelableArrayListExtra("selected_excursions", excursionsForVacation);
            context.startActivity(intent);
        });
    }

    private Calendar getCalendarFromString(String dateString) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
        try {
            Date date = sdf.parse(dateString);
            if (date != null) {
                calendar.setTime(date);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return calendar;
    }

    @Override
    public int getItemCount() {
        return vacationList == null ? 0 : vacationList.size();
    }

    // Set vacations and excursions and update the full list for search filtering
    public void setVacations(List<Vacation> vacations, List<Excursion> excursions) {
        this.vacationList = vacations;
        this.excursionList = excursions;
        this.vacationListFull = new ArrayList<>(vacations);  // Initialize the full list for filtering
        notifyDataSetChanged();
    }

    // Filter method for search functionality
    public void filter(String query) {
        List<Vacation> filteredList = new ArrayList<>();
        if (query == null || query.isEmpty()) {
            filteredList = vacationListFull;
        } else {
            String filterPattern = query.toLowerCase().trim();
            for (Vacation vacation : vacationListFull) {
                if (vacation.getVacationName().toLowerCase().contains(filterPattern)) {
                    filteredList.add(vacation);
                }
            }
        }
        vacationList = filteredList;
        notifyDataSetChanged();
    }

    private String getExcursionNamesForVacation(int vacationId) {
        StringBuilder excursionInfo = new StringBuilder();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()); // Format for dates

        for (Excursion excursion : excursionList) {
            if (excursion.getVacationId() == vacationId) {
                String excursionDate = excursion.getDate() != null ? sdf.format(excursion.getDate()) : "No date";
                excursionInfo.append(excursion.getName()).append(" - ").append(excursionDate).append("\n");
            }
        }

        // Remove the last newline if it exists
        if (excursionInfo.length() > 0) {
            excursionInfo.setLength(excursionInfo.length() - 1);
        }

        return excursionInfo.toString();
    }

    public static class VacationViewHolder extends RecyclerView.ViewHolder {
        TextView vacationNameTextView, vacationDatesTextView, excursionListTextView;
        ImageButton editVacationButton, editExcursionButton, deleteButton;

        public VacationViewHolder(@NonNull View itemView) {
            super(itemView);
            vacationNameTextView = itemView.findViewById(R.id.text_view_vacation_name);
            vacationDatesTextView = itemView.findViewById(R.id.text_view_vacation_dates);
            excursionListTextView = itemView.findViewById(R.id.text_view_excursion_list);
            editVacationButton = itemView.findViewById(R.id.btn_edit_vacation);
            editExcursionButton = itemView.findViewById(R.id.btn_edit_excursion);
            deleteButton = itemView.findViewById(R.id.btn_delete_vacation);
        }
    }
}
