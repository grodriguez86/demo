package com.challenge.upgrade.demo.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

@Entity
@Table(name = "availabilities")
public class DayAvailability implements Serializable {

    private static final long serialVersionUID = 5229707468017041496L;

    public static final String ID_PATTERN = "yyyy-MM-dd";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(unique = true)
    private String day;

    @Column
    private int occupancy;

    @Version
    private int version;

    public DayAvailability() {
    }

    public DayAvailability(String date) {
        this.day = date.toString();
        this.occupancy = 0;
    }

    public long getId() {
        return id;
    }

    public int getOccupancy() {
        return occupancy;
    }

    public void setOccupancy(int occupancy) {
        this.occupancy = occupancy;
    }

    public String getDay() {
        return day;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

}