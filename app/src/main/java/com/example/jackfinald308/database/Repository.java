package com.example.jackfinald308.database;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.example.jackfinald308.dao.ExcursionDAO;
import com.example.jackfinald308.dao.VacationDAO;
import com.example.jackfinald308.entities.Excursion;
import com.example.jackfinald308.entities.Vacation;
import com.example.jackfinald308.viewmodel.VacationViewModel;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Repository {
    private final VacationDAO mVacationDAO;
    private final ExcursionDAO mExcursionDAO;
    private static final int NUMBER_OF_THREADS = 4;
    private static final ExecutorService databaseExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public Repository(Application application) {
        VacationDatabase db = VacationDatabase.getInstance(application);
        mVacationDAO = db.vacationDAO();
        mExcursionDAO = db.excursionDAO();
    }

    // Observes if a vacation has associated excursions
    public LiveData<Boolean> hasAssociatedExcursions(int vacationId) {
        return Transformations.map(mExcursionDAO.getExcursionsByVacationId(vacationId), excursions ->
                excursions != null && !excursions.isEmpty());
    }

    // Fetches excursions by vacation ID
    public LiveData<List<Excursion>> getExcursionsByVacationId(int vacationId) {
        return mExcursionDAO.getExcursionsByVacationId(vacationId);
    }

    // Deletes a vacation (checks for associated excursions before deleting)
    public void deleteVacation(int vacationId, VacationDeleteCallback callback) {
        new Handler(Looper.getMainLooper()).post(() -> {
            hasAssociatedExcursions(vacationId).observeForever(hasExcursions -> {
                if (hasExcursions) {
                    new Handler(Looper.getMainLooper()).post(() -> {
                        callback.onVacationHasExcursions(vacationId);
                    });
                } else {
                    databaseExecutor.execute(() -> {
                        mVacationDAO.deleteById(vacationId);
                        new Handler(Looper.getMainLooper()).post(callback::onVacationDeleteSuccess);
                    });
                }
            });
        });
    }

    // Deletes all excursions associated with a vacation
    public void deleteExcursionsByVacationId(int vacationId) {
        databaseExecutor.execute(() -> mExcursionDAO.deleteExcursionsByVacationId(vacationId));
    }

    // Deletes a vacation by its ID
    public void deleteVacationById(int vacationId) {
        databaseExecutor.execute(() -> mVacationDAO.deleteById(vacationId));
    }

    // Inserts a new vacation
    public void insert(Vacation vacation) {
        databaseExecutor.execute(() -> mVacationDAO.insert(vacation));
    }

    // Updates a vacation
    public void update(Vacation vacation) {
        databaseExecutor.execute(() -> mVacationDAO.update(vacation));
    }

    // Retrieves all vacations
    public LiveData<List<Vacation>> getAllVacations() {
        return mVacationDAO.getAllVacation();
    }

    // Retrieves a vacation by its ID
    public LiveData<Vacation> getVacationById(int vacationId) {
        return mVacationDAO.getVacationById(vacationId);
    }

    // Inserts a new excursion
    public void insert(Excursion excursion) {
        databaseExecutor.execute(() -> mExcursionDAO.insert(excursion));
    }

    // Updates an excursion
    public void update(Excursion excursion) {
        databaseExecutor.execute(() -> mExcursionDAO.update(excursion));
    }

    // Deletes an excursion
    public void delete(Excursion excursion) {
        databaseExecutor.execute(() -> mExcursionDAO.delete(excursion));
    }

    // Retrieves all excursions
    public LiveData<List<Excursion>> getAllExcursions() {
        return mExcursionDAO.getAllExcursions();
    }

    // Populates the database with sample excursions if none exist
    public void populateExcursions(int vacationId) {
        databaseExecutor.execute(() -> {
            List<Excursion> existingExcursions = mExcursionDAO.getExcursionsByVacationIdSync(vacationId);

            if (existingExcursions != null && existingExcursions.isEmpty()) {
                Date defaultDate = new Date();  // Use the current date as default

                // Populate the database with some excursions
                mExcursionDAO.insert(new Excursion("Snorkeling Adventure", "Explore the vibrant underwater life.", vacationId, defaultDate));
                mExcursionDAO.insert(new Excursion("City Tour", "Visit famous landmarks in the city.", vacationId, defaultDate));
                mExcursionDAO.insert(new Excursion("Mountain Hiking", "Enjoy a scenic hike in the mountains.", vacationId, defaultDate));
                mExcursionDAO.insert(new Excursion("Wine Tasting", "Sample fine wines from local vineyards.", vacationId, defaultDate));
                mExcursionDAO.insert(new Excursion("Historical Museum Visit", "Learn about the history of the region.", vacationId, defaultDate));
                mExcursionDAO.insert(new Excursion("Scuba Diving", "Dive into the deep blue sea.", vacationId, defaultDate));
                mExcursionDAO.insert(new Excursion("Cooking Class", "Learn to cook local dishes.", vacationId, defaultDate));
                mExcursionDAO.insert(new Excursion("Wildlife Safari", "Experience wildlife up close.", vacationId, defaultDate));
                mExcursionDAO.insert(new Excursion("Beach Volleyball", "Have fun playing volleyball on the beach.", vacationId, defaultDate));
                mExcursionDAO.insert(new Excursion("Bungee Jumping", "Get your adrenaline pumping with bungee jumping.", vacationId, defaultDate));
            }
        });
    }

    // Insert vacation with callback
    public void insert(Vacation vacation, VacationViewModel.InsertCallback callback) {
        databaseExecutor.execute(() -> {
            long vacationId = mVacationDAO.insert(vacation);

            new Handler(Looper.getMainLooper()).post(() -> {
                if (vacationId != -1) {
                    callback.onInsertSuccess((int) vacationId);
                } else {
                    callback.onInsertFailure();
                }
            });
        });
    }

    // Delete callback interface
    public interface VacationDeleteCallback {
        void onVacationHasExcursions(int vacationId);
        void onVacationDeleteSuccess();
    }
}
