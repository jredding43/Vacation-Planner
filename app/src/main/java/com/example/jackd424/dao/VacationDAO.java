package com.example.jackd424.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.jackd424.entities.Vacation;

import java.util.List;

@Dao
public interface VacationDAO {


    @Update
    void update(Vacation vacation);

    @Delete
    void delete(Vacation vacation);

    @Query("DELETE FROM vacations WHERE vacationId = :vacationId")
    void deleteById(int vacationId);

    @Query("SELECT * FROM vacations")
    LiveData<List<Vacation>> getAllVacation();

    @Query("SELECT * FROM vacations WHERE excursionID = :excursionID")
    LiveData<List<Vacation>> getAssociatedVacation(int excursionID);

    @Query("SELECT * FROM vacations WHERE vacationId = :vacationId")
    LiveData<Vacation> getVacationById(int vacationId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Vacation vacation);

    @Query("SELECT * FROM vacations WHERE vacationId = :vacationId LIMIT 1")
    Vacation getVacationByIdSync(int vacationId);


}
