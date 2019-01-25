package org.varonesoft.luke.calendarcdg.calendarcdg.models;

import java.util.Objects;

/**
 * HalfTeam
 *
 * Created by luke on 22/09/17.
 */

public class HalfTeam implements Cloneable {

    // La squadra di appartenenza ( o semisquadra)
    private String name;

    // COSTRUTTORE
    public HalfTeam(char c) {
        this(String.valueOf(c));
    }

    public HalfTeam(String c) {
        this.name = c;
    }

    public String getName() {
        return name;
    }

    public String toString() {
        return "HalfTeam{" + this.name + "}";
    }

    public boolean compareTo(HalfTeam h) {
        return h != null && h.getName().equals(this.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (obj.getClass() != getClass()) return false;
        HalfTeam h = (HalfTeam) obj;
        return Objects.equals(this.getName(), h.getName());
    }

    @Override
    public HalfTeam clone() throws CloneNotSupportedException {
        return (HalfTeam) super.clone();
    }
}
