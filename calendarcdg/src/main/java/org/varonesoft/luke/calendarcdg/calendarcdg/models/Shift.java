package org.varonesoft.luke.calendarcdg.calendarcdg.models;

import java.util.HashSet;
import java.util.Set;

/**
 * The full Shift
 *
 * Created by luke on 22/09/17.
 */

public class Shift implements Cloneable {

    // TipoTurno, referenzia un tipoturno
    private ShiftType       shiftType;

    // Fermo macchine
    private boolean         stop;

    // Il turno e' composto dalle seguenti semisquadre
    private Set<HalfTeam> halfTeams;


    // COSTRUTTORE
    public Shift(ShiftType shiftType) {
        this.shiftType =    shiftType;
        this.stop =         false;
        this.halfTeams = new HashSet<>();
    }

    public boolean isStop() {
        return stop;
    }

    public void setStop(boolean stop) {
        this.stop = stop;
    }

    public void addTeam(HalfTeam halfTeam) {
        halfTeams.add(halfTeam);
    }

    public Set<HalfTeam> getHalfTeams() {
        return halfTeams;
    }

    public String getTeamsAsString() {
        StringBuilder sb = new StringBuilder();
        for (HalfTeam t : halfTeams) {
            sb.append(t.getName());
        }
        return sb.toString();
    }

    public String toString() {
        return "Shift{" + shiftType.getName() + (isStop() ? "-stop" : "" + "}");
    }

    @Override
    public Shift clone() throws CloneNotSupportedException {
        Shift shift = (Shift) super.clone();
        shift.setStop(this.stop);
        for (HalfTeam t : halfTeams) {
            shift.addTeam(t.clone());
        }
        return shift;
    }
}
