package com.example.jackfinald308.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jackfinald308.R;
import com.example.jackfinald308.entities.Excursion;
import com.example.jackfinald308.entities.Vacation;

import java.util.ArrayList;
import java.util.List;

public class VacationAdapter extends RecyclerView.Adapter<VacationAdapter.VacationHolder> {

    private List<Vacation> vacations = new ArrayList<>();
    private List<Excursion> excursions = new ArrayList<>();

    @NonNull
    @Override
    public VacationHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.vacation_item, parent, false);
        return new VacationHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull VacationHolder holder, int position) {
        Vacation currentVacation = vacations.get(position);
        holder.textViewVacationName.setText(currentVacation.getVacationName());
        holder.textViewVacationDates.setText(currentVacation.getDepartDate() + " - " + currentVacation.getReturnDate());

        // Fetch excursions related to this vacation
        List<Excursion> vacationExcursions = getExcursionsForVacation(currentVacation.getVacationID());
        if (!vacationExcursions.isEmpty()) {
            StringBuilder excursionsText = new StringBuilder("Excursions:\n");
            for (Excursion excursion : vacationExcursions) {
                excursionsText.append("• ").append(excursion.getName()).append(" - ").append(excursion.getDate()).append("\n");
            }
            holder.textViewExcursions.setText(excursionsText.toString());
        } else {
            holder.textViewExcursions.setText("No excursions attached.");
        }
    }

    @Override
    public int getItemCount() {
        return vacations.size();
    }

    public void setVacations(List<Vacation> vacations, List<Excursion> excursions) {
        this.vacations = vacations;
        this.excursions = excursions;
        notifyDataSetChanged();
    }

    private List<Excursion> getExcursionsForVacation(int vacationId) {
        List<Excursion> relatedExcursions = new ArrayList<>();
        for (Excursion excursion : excursions) {
            if (excursion.getVacationId() == vacationId) {
                relatedExcursions.add(excursion);
            }
        }
        return relatedExcursions;
    }

    class VacationHolder extends RecyclerView.ViewHolder {
        private TextView textViewVacationName;
        private TextView textViewVacationDates;
        private TextView textViewExcursions; // TextView for displaying excursions

        public VacationHolder(View itemView) {
            super(itemView);
            textViewVacationName = itemView.findViewById(R.id.text_view_vacation_name);
            textViewVacationDates = itemView.findViewById(R.id.text_view_vacation_dates);
            textViewExcursions = itemView.findViewById(R.id.text_view_excursions); // Initialize the excursions TextView
        }
    }
}
