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

    public LiveData<Boolean> hasAssociatedExcursions(int vacationId) {
        return Transformations.map(mExcursionDAO.getExcursionsByVacationId(vacationId), excursions ->
                excursions != null && !excursions.isEmpty());
    }

    public LiveData<List<Excursion>> getExcursionsByVacationId(int vacationId) {
        return mExcursionDAO.getExcursionsByVacationId(vacationId);
    }

    public void deleteVacation(int vacationId) {
        LiveData<Boolean> hasExcursions = hasAssociatedExcursions(vacationId);
        if (hasExcursions.getValue() != null && hasExcursions.getValue()) {
            // Notify user that the vacation cannot be deleted
        } else {
            databaseExecutor.execute(() -> mVacationDAO.deleteById(vacationId));
        }
    }


    public void insert(Excursion excursion) {
        databaseExecutor.execute(() -> mExcursionDAO.insert(excursion));
    }

    public void update(Excursion excursion) {
        databaseExecutor.execute(() -> mExcursionDAO.update(excursion));
    }

    public void delete(Excursion excursion) {
        databaseExecutor.execute(() -> mExcursionDAO.delete(excursion));
    }

    public LiveData<List<Vacation>> getAllVacations() {
        return mVacationDAO.getAllVacation();
    }

    public void insert(Vacation vacation) {
        databaseExecutor.execute(() -> mVacationDAO.insert(vacation));
    }

    public void update(Vacation vacation) {
        databaseExecutor.execute(() -> mVacationDAO.update(vacation));
    }

    public LiveData<Vacation> getVacationById(int vacationId) {
        return mVacationDAO.getVacationById(vacationId);
    }

    public void deleteExcursionsByVacationId(int vacationId) {
        databaseExecutor.execute(() -> {
            mExcursionDAO.deleteExcursionsByVacationId(vacationId);
        });
    }

    public LiveData<List<Excursion>> getAllExcursions() {
        return mExcursionDAO.getAllExcursions();
    }

    public void populateExcursions(int vacationId) {
        databaseExecutor.execute(() -> {
            // Fetch existing excursions synchronously
            List<Excursion> existingExcursions = mExcursionDAO.getExcursionsByVacationIdSync(vacationId);

            // Check if the list is not null and is empty
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



    public void insert(Vacation vacation, VacationViewModel.InsertCallback callback) {
        databaseExecutor.execute(() -> {
            long vacationId = mVacationDAO.insert(vacation);

            // Post the result back to the UI thread
            new Handler(Looper.getMainLooper()).post(() -> {
                if (vacationId != -1) {
                    callback.onInsertSuccess((int) vacationId);
                } else {
                    callback.onInsertFailure();
                }
            });
        });
    }


}
