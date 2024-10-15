package com.example.jackd424.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.jackd424.database.Repository;
import com.example.jackd424.entities.Excursion;
import com.example.jackd424.entities.Vacation;

import java.util.List;

public class VacationViewModel extends AndroidViewModel {
    private final Repository repository;
    private final LiveData<List<Vacation>> allVacations;
    private final LiveData<List<Excursion>> allExcursions;

    public VacationViewModel(Application application) {
        super(application);
        repository = new Repository(application);
        repository.getAllVacations();
        allVacations = repository.getAllVacations();
        allExcursions = repository.getAllExcursions();
    }



    // Get a specific vacation by its ID
    public LiveData<Vacation> getVacationById(int vacationId) {
        return repository.getVacationById(vacationId);
    }

    // Method to insert an excursion
    public void insertExcursion(Excursion excursion) {
        repository.insert(excursion);
    }

    // Get all vacations
    public LiveData<List<Vacation>> getAllVacations() {
        Log.d("VacationRepository", "Fetching all vacations from the database");
        return allVacations;
    }

    // Get excursions by vacation ID
    public LiveData<List<Excursion>> getExcursionsByVacationId(int vacationId) {
        return repository.getExcursionsByVacationId(vacationId);
    }


    public void clearExcursionsForVacation(int vacationId) {
        repository.deleteExcursionsByVacationId(vacationId);
    }

    public void insert(Vacation vacation, InsertCallback callback) {
        repository.insert(vacation, callback);
    }


    // Update a vacation
    public void update(Vacation vacation) {
        repository.update(vacation);
    }

    // Delete a vacation by ID with callback
    public void deleteVacation(int vacationId) {
        repository.deleteVacation(vacationId, new Repository.VacationDeleteCallback() {
            @Override
            public void onVacationHasExcursions(int vacationId) {
            }

            @Override
            public void onVacationDeleteSuccess() {
            }
        });
    }

    // Get all excursions
    public LiveData<List<Excursion>> getAllExcursions() {
        return allExcursions; // Use the LiveData from the repository
    }

    // Insert an excursion
    public void insertExcursionForVacation(Excursion excursion, int vacationId) {
        repository.insertExcursionForVacation(excursion, vacationId);
    }


    // Update an excursion
    public void updateExcursion(Excursion excursion) {
        repository.update(excursion);
    }

    // Delete an excursion
    public void deleteExcursion(Excursion excursion) {
        repository.delete(excursion);
    }

    // Populate excursions for a specific vacation
    public void populateExcursions(int vacationId) {
        repository.populateExcursions(vacationId);
    }

    public interface InsertCallback {
        void onInsertSuccess(int vacationId);
        void onInsertFailure();
    }

    public void deleteExcursionsByVacationId(int vacationId) {
        repository.deleteExcursionsByVacationId(vacationId);
    }
}
