package org.varonesoft.luke.calendarcdg.calendarcdg.models;

import java.util.Objects;

/**
 * Shift Type
 *
 * Created by luke on 04/10/17.
 */

public class ShiftType {

    // Name
    private String name;
    private String description;

    // Start
    private int startHour;
    private int startMinute;

    // Duration in Hours
    private int durationHours;
    private int durationMinutes;

    public ShiftType(String n, String d, int sH, int sM, int dH, int dM) {
        this.name = n;
        this.description = d;
        this.description = d;
        this.startHour = sH;
        this.startMinute = sM;
        this.durationHours = dH;
        this.durationMinutes = dM;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getStartHour() {
        return startHour;
    }

    public int getStartMinute() {
        return startMinute;
    }

    public int getDurationHours() {
        return durationHours;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    @Override
    public int hashCode() {

        int prime = 19;
        int result = 1;
        result = prime * result + name.hashCode();
        result = prime * result + description.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) return true;
        if (obj == null) return false;
        if (obj.getClass() != getClass()) return false;
        ShiftType st = (ShiftType) obj;
        return Objects.equals(this.getName(), st.getName()) &&
                Objects.equals(this.getDescription(), st.getDescription()) &&
                this.getStartHour() == st.getStartHour() &&
                this.getStartMinute() == st.getStartMinute() &&
                this.getDurationHours() == st.getDurationHours() &&
                this.getDurationMinutes() == st.getDurationMinutes();
    }
}
