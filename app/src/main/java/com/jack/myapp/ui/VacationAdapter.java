package com.jack.myapp.ui;

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

import com.example.myapp.R;
import com.jack.myapp.database.Repository;
import com.jack.myapp.entities.Excursion;
import com.jack.myapp.entities.Vacation;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class VacationAdapter extends RecyclerView.Adapter<VacationAdapter.VacationViewHolder> {

    private List<Vacation> vacationList;
    private List<Excursion> excursionList;
    private Context context;
    private Repository repository;
    private LifecycleOwner lifecycleOwner;

    public VacationAdapter(Context context, Repository repository, LifecycleOwner lifecycleOwner) {
        this.context = context;
        this.repository = repository;
        this.lifecycleOwner = lifecycleOwner;
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
        String vacationDates = "From: " + vacation.getDepartDate() + " To: " + vacation.getReturnDate();
        holder.vacationDatesTextView.setText(vacationDates);

        // Set excursions for this vacation
        String excursions = getExcursionDetailsForVacation(vacation.getVacationID());
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

        // Vacation edit button (edit vacation details)
        holder.editVacationButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, VacationActivity.class);
            intent.putExtra("VACATION_ID", vacation.getVacationID());  // Pass the vacation ID
            context.startActivity(intent);
        });



        holder.editExcursionButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, ExcursionDetails.class);

            // Pass vacation details
            intent.putExtra("vacation_id", vacation.getVacationID());  // Pass the vacation ID
            intent.putExtra("vacation_name", vacation.getVacationName());  // Pass vacation name
            intent.putExtra("vacation_depart_date", vacation.getDepartDate());  // Pass departure date
            intent.putExtra("vacation_return_date", vacation.getReturnDate());  // Pass return date

            // Create a list to store excursions for this vacation
            ArrayList<Excursion> excursionsForVacation = new ArrayList<>();
            for (Excursion excursion : excursionList) {
                if (excursion.getVacationId() == vacation.getVacationID()) {
                    excursionsForVacation.add(excursion);  // Add excursions related to this vacation
                }
            }

            // Pass the excursions list to the next activity
            intent.putParcelableArrayListExtra("selected_excursions", excursionsForVacation);  // Pass the excursions list
            context.startActivity(intent);
        });



        // Share vacation details via text or email
        holder.btnShareVacation.setOnClickListener(v -> {
            String vacationName = vacation.getVacationName();
            String vacationDates2 = "From: " + vacation.getDepartDate() + " To: " + vacation.getReturnDate();
            String excursions2 = getExcursionDetailsForVacation(vacation.getVacationID());

            String shareMessage = "Vacation: " + vacationName + "\n"
                    + "Dates: " + vacationDates2 + "\n"
                    + "Excursions: " + (excursions2.isEmpty() ? "No excursions" : excursions2);

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
            context.startActivity(Intent.createChooser(shareIntent, "Share Vacation via"));
        });
    }

    private String getExcursionDetailsForVacation(int vacationId) {
        StringBuilder excursionDetails = new StringBuilder();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()); // Adjust format as needed

        for (Excursion excursion : excursionList) {
            if (excursion.getVacationId() == vacationId) {
                // Format the excursion date to display only the date part
                String formattedDate = dateFormat.format(excursion.getDate());

                // Append excursion name and formatted date
                excursionDetails.append(excursion.getName())
                        .append(" - ")
                        .append(formattedDate)
                        .append("\n");
            }
        }
        if (excursionDetails.length() > 0) {
            excursionDetails.setLength(excursionDetails.length() - 1);  // Remove trailing newline
        }
        return excursionDetails.toString();
    }

    @Override
    public int getItemCount() {
        return vacationList == null ? 0 : vacationList.size();
    }

    public void setVacations(List<Vacation> vacations, List<Excursion> excursions) {
        this.vacationList = vacations;
        this.excursionList = excursions;
        notifyDataSetChanged();
    }


    public static class VacationViewHolder extends RecyclerView.ViewHolder {
        TextView vacationNameTextView, vacationDatesTextView, excursionListTextView;
        ImageButton editVacationButton, editExcursionButton, deleteButton, btnShareVacation;

        public VacationViewHolder(@NonNull View itemView) {
            super(itemView);
            vacationNameTextView = itemView.findViewById(R.id.text_view_vacation_name);
            vacationDatesTextView = itemView.findViewById(R.id.text_view_vacation_dates);
            excursionListTextView = itemView.findViewById(R.id.text_view_excursion_list);
            editVacationButton = itemView.findViewById(R.id.btn_edit_vacation);
            editExcursionButton = itemView.findViewById(R.id.btn_edit_excursion);
            deleteButton = itemView.findViewById(R.id.btn_delete_vacation);
            btnShareVacation = itemView.findViewById(R.id.btn_share_vacation); // New share button
        }
    }
}
