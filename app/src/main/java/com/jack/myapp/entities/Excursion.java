package com.jack.myapp.entities;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "excursions")
public class Excursion implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    private int excursionID;

    @ColumnInfo(name = "vacationId")
    private int vacationId;

    private String name;
    private String description;
    private Date date;

    public Excursion(String name, String description, int vacationId, Date date) {
        this.name = name;
        this.description = description;
        this.vacationId = vacationId;
        this.date = date;
    }

    protected Excursion(Parcel in) {
        excursionID = in.readInt();
        vacationId = in.readInt();
        name = in.readString();
        description = in.readString();
        long tmpDate = in.readLong();
        date = tmpDate != -1 ? new Date(tmpDate) : null;
    }

    // Getters and Setters
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getVacationId() {
        return vacationId;
    }

    public void setVacationId(int vacationId) {
        this.vacationId = vacationId;
    }

    public int getExcursionID() {
        return excursionID;
    }

    public void setExcursionID(int excursionID) {
        this.excursionID = excursionID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public static final Creator<Excursion> CREATOR = new Creator<Excursion>() {
        @Override
        public Excursion createFromParcel(Parcel in) {
            return new Excursion(in);
        }

        @Override
        public Excursion[] newArray(int size) {
            return new Excursion[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(excursionID);  // Write excursionID to the parcel
        dest.writeInt(vacationId);   // Write vacationId to the parcel
        dest.writeString(name);      // Write name to the parcel
        dest.writeString(description); // Write description to the parcel
        dest.writeLong(date != null ? date.getTime() : -1); // Write date as long if not null
    }
}
