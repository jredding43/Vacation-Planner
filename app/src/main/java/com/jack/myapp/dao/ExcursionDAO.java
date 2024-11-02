package com.jack.myapp.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.jack.myapp.entities.Excursion;

import java.util.List;

@Dao
public interface ExcursionDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(Excursion excursion);

    @Update
    void update(Excursion excursion);

    @Delete
    void delete(Excursion excursion);

    @Query("SELECT * FROM excursions ORDER BY excursionID ASC")
    LiveData<List<Excursion>> getAllExcursion(); // Assuming your table name is 'excursions'

    @Query("SELECT * FROM excursions WHERE vacationID = :vacationId")
    LiveData<List<Excursion>> getExcursionsByVacationId(int vacationId);

    @Query("SELECT * FROM excursions")
    LiveData<List<Excursion>> getAllExcursions();

    // Fixing the table name here as well
    @Query("SELECT * FROM excursions WHERE vacationID = :vacationId")
    List<Excursion> getExcursionsByVacationIdSync(int vacationId);  // Synchronous method

    @Query("DELETE FROM excursions WHERE vacationId = :vacationId")
    void deleteExcursionsByVacationId(int vacationId);

}
