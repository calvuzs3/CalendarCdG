package org.varonesoft.luke.calendarcdg.calendar.utils;

import android.support.annotation.NonNull;

/**
 * A class that represents a calendar used in the Calendar widget
 *
 * @author brianreber
 *
 * Created by luke on 29/09/17.
 *
 */
public class AndroidCalendar implements Comparable<AndroidCalendar>  {
    private String id;
    private String color;
    private String name;

    /**
     * Creates a new Calendar with the given parameters
     *
     * @param id
     * The id of the calendar
     * @param color
     * The color of the calendar
     * @param name
     * The name of the calendar
     */
    AndroidCalendar(String id, String color, String name) {
        this.id = id;
        this.color = color;
        this.name = name;
    }

    /**
     * @return the id of this calendar
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the color
     */
    public String getColor() {
        return color;
    }

    /**
     * @param color the color to set
     */
    public void setColor(String color) {
        this.color = color;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return name;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((color == null) ? 0 : color.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AndroidCalendar other = (AndroidCalendar) obj;
        if (color == null) {
            if (other.color != null)
                return false;
        } else if (!color.equals(other.color))
            return false;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (name == null) {
            return other.name == null;
        } else return name.equals(other.name);
    }

    @Override
    public int compareTo(@NonNull AndroidCalendar androidCalendar) {
        return name.compareTo(androidCalendar.name);
    }
}
