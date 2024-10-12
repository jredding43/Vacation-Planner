package com.example.jackd424.entities;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.List;

@Entity(tableName = "vacations")
public class Vacation implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int vacationID;

    private String hotel;
    private int excursionID;
    private String toFlight;
    private String fromFlight;
    private String departDate;
    private String returnDate;
    private int adults;
    private int kids;
    private String vacationName;
    private String excursionName;

    @Ignore
    private List<Excursion> excursions;

    public Vacation(String vacationName, String hotel, int excursionID, String toFlight, String fromFlight, String departDate, String returnDate, int adults, int kids, String excursionName) {
        this.vacationName = vacationName;
        this.hotel = hotel;
        this.excursionID = excursionID;
        this.toFlight = toFlight != null ? toFlight : ""; // Default value for toFlight
        this.fromFlight = fromFlight != null ? fromFlight : ""; //Default value for fromFlight
        this.departDate = departDate;
        this.returnDate = returnDate;
        this.adults = adults > 0 ? adults : 0; // Default value for adults
        this.kids = kids >= 0 ? kids : 0; // Default value for kids
        this.excursionName = excursionName;
    }

    public String getExcursionName() {
        return excursionName;
    }

    public void setExcursionName(String excursionName) {
        this.excursionName = excursionName;
    }

    public String getHotel() {
        return hotel;
    }

    public void setHotel(String hotel) {
        this.hotel = hotel;
    }

    public String getVacationName() {
        return vacationName;
    }

    public void setVacationName(String vacationName) {
        this.vacationName = vacationName;
    }

    public int getVacationID() {
        return vacationID;
    }

    public void setVacationID(int vacationID) {
        this.vacationID = vacationID;
    }

    public int getExcursionID() {
        return excursionID;
    }

    public void setExcursionID(int excursionID) {
        this.excursionID = excursionID;
    }

    public String getToFlight() {
        return toFlight;
    }

    public void setToFlight(String toFlight) {
        this.toFlight = toFlight;
    }

    public String getFromFlight() {
        return fromFlight;
    }

    public void setFromFlight(String fromFlight) {
        this.fromFlight = fromFlight;
    }

    public String getDepartDate() {
        return departDate;
    }

    public void setDepartDate(String departDate) {
        this.departDate = departDate;
    }

    public String getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(String returnDate) {
        this.returnDate = returnDate;
    }

    public int getAdults() {
        return adults;
    }

    public void setAdults(int adults) {
        this.adults = adults;
    }

    public int getKids() {
        return kids;
    }

    public void setKids(int kids) {
        this.kids = kids;
    }

    public List<Excursion> getExcursions() {
        return excursions;
    }

    public void setExcursions(List<Excursion> excursions) {
        this.excursions = excursions;
    }
}
