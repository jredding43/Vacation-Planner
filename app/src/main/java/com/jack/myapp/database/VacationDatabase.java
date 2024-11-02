package com.jack.myapp.database;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.jack.myapp.dao.ExcursionDAO;
import com.jack.myapp.dao.VacationDAO;
import com.jack.myapp.data.DateConverter;
import com.jack.myapp.entities.Excursion;
import com.jack.myapp.entities.Vacation;

@TypeConverters(DateConverter.class)
@Database(entities = {Vacation.class, Excursion.class}, version = DatabaseConfig.DATABASE_VERSION, exportSchema = false)
public abstract class VacationDatabase extends RoomDatabase {
    private static volatile VacationDatabase instance;

    public abstract VacationDAO vacationDAO();
    public abstract ExcursionDAO excursionDAO();

    public static VacationDatabase getInstance(final Context context) {
        if (instance == null) {
            synchronized (VacationDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(context.getApplicationContext(),
                                    VacationDatabase.class, "vacation_database")
                            .fallbackToDestructiveMigration() // This will drop the old database
                            .build();
                }
            }
        }
        return instance;
    }
}
