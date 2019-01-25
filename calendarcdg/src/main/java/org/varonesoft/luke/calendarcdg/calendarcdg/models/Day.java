package org.varonesoft.luke.calendarcdg.calendarcdg.models;

import org.joda.time.LocalDate;
import org.varonesoft.luke.calendarcdg.Log;
import org.varonesoft.luke.calendarcdg.calendar.utils.list.Event;
import org.varonesoft.luke.calendarcdg.calendarcdg.CalendarCdG;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Day
 * <p>
 * Created by luke on 21/09/17.
 */

public class Day implements Cloneable {

    // TAG
    public final static String TAG = Day.class.getSimpleName();

    // LOG
    private final static boolean LOG_ = false;
    private final static boolean LOG_SHIFTS = false;
    private final static boolean LOG_STOPS = false;

    // Data
    private LocalDate localDate;

    // Il giorno e' oggi
    private boolean isToday = false;

    // Squadre ovvero i turni del giorno
    private ArrayList<Shift> shifts = null;

    // le squadre a riposo
    private ArrayList<HalfTeam> offWorkHalfTeams = null;

    // events
    private ArrayList<Event> events = null;


    // Costruttore
    public Day(LocalDate localDate) {
        if (LOG_) Log.v(TAG, "New Day {" + localDate.toString() + "}");

        this.localDate = new LocalDate(localDate.getYear(), localDate.getMonthOfYear(), localDate.getDayOfMonth());
        events = null;
        shifts = null;
        LocalDate ld = LocalDate.now();
        setIsToday(ld.getYear() == this.localDate.getYear() &&
                ld.getMonthOfYear() == this.localDate.getMonthOfYear() &&
                ld.getDayOfMonth() == this.localDate.getDayOfMonth());

        if (LOG_) Log.d(TAG, localDate.toString() + " - " + this.localDate.toString());
//        offWorkHalfTeams = new ArrayList<>();
//        try {
//            for( HalfTeam t:CalendarCdG.HALFTEAM_ALL) this.addOffWorkHalfTeam(t.clone());
//        } catch (CloneNotSupportedException e) {
//            Log.e(TAG, e.getMessage());
//        }
        try {
            offWorkHalfTeams = (ArrayList<HalfTeam>) CalendarCdG.HALFTEAM_ALL.clone();
        } catch (Exception e) {
            Log.e(TAG, "HALFTEAM.clone() : " + e.getMessage());
        }
    }

    public int getInWichTeamIsHalfTeam(HalfTeam h) {
        int result = -1;
        if ((shifts == null) || (shifts.isEmpty())) return result;

        for (Shift t : shifts) {
            result++;
            for (HalfTeam ht : t.getHalfTeams()) {
                if (ht.compareTo(h)) {
                    return result;
                }
            }
        }
        return -1;
    }

    public ArrayList<Shift> getShifts() {
        return this.shifts;
    }

    /**
     * Settiamo i turni creando un clone di ogni turno passato
     * poiche i turni passati sono lo schema base
     *
     * @param shifts i turni da copiare
     */
    private void setShifts(ArrayList<Shift> shifts) {
        this.shifts = new ArrayList<>();
        for (Shift s : shifts) {
            try {
                addShift(s.clone());
            } catch (CloneNotSupportedException e) {
                Log.e(TAG, "setShifts: " + e.getMessage());
            }
        }
    }

    public void addShift(Shift s) {
        if (shifts == null) shifts = new ArrayList<>();
        shifts.add(s);
        if (LOG_SHIFTS) Log.d(TAG, "addShift: " + s.toString());

        // remove the HalfTeam from offworkteam
        Iterator itr;

        for (HalfTeam ss : s.getHalfTeams()) {
            itr = offWorkHalfTeams.iterator();
            while (itr.hasNext()) {
                /*
                    Iterator's next method returns an Object so we need to cast it into
                    appropriate class before using it.
                */
                HalfTeam sElement = (HalfTeam) itr.next();
                if (sElement.compareTo(ss)) {
                    /*
                      Remove an element using remove() method of Iterator
                      Remove method removes an element from underlying collection and
                      it may throw a UnsupportedOperationException if the remove
                      operation is not supported.
                    */
                    itr.remove();
                    break;
                }
            }
        }
    }


    private void setOffWorkHalfTeams(ArrayList<HalfTeam> offWorkHalfTeams) {
        this.offWorkHalfTeams = new ArrayList<>();
        for (HalfTeam t : offWorkHalfTeams) {
            try {
                addOffWorkHalfTeam(t.clone());
            } catch (CloneNotSupportedException e) {
                Log.e(TAG, "setOffWorkHalfTeam: " + e.getMessage());
            }
        }
    }

    private void addOffWorkHalfTeam(HalfTeam halfTeam) {
        this.offWorkHalfTeams.add(halfTeam);
    }

    public int getDayOfMonth() {
        return localDate.getDayOfMonth();
    }

    public int getDayOfWeek() {
        return localDate.getDayOfWeek();
    }

    public String getDayOfWeekAsString() {
        return localDate.toString("EEEE");
    }

    public String getOffWorkHalfTeamsAsString() {

        StringBuilder result = new StringBuilder();
        for (HalfTeam halfTeam : offWorkHalfTeams) {
            result.append(halfTeam.getName());
        }
        return result.toString();
    }

    public String getTeamsAsString(int pos) {
        return shifts.get(pos).getTeamsAsString();
    }

    public boolean getIsToday() {
        return this.isToday;
    }

    public void setIsToday(boolean b) {
        this.isToday = b;
    }

    public void setLocalDate(LocalDate localDate) {
        this.localDate = new LocalDate(localDate.getYear(), localDate.getMonthOfYear(), localDate.getDayOfMonth());
    }

    public void addEvent(Event event) {
        if (events == null) events = new ArrayList<>();
        events.add(event);
    }

    public boolean hasEvents() {
        return !((events == null) || events.isEmpty());
    }

    /**
     * Dato il turno setto la fermata impianti
     * con sfondo rosso per intenderci
     *
     * @param shift il turno del giorno (1,2 o 3)
     */
    public void setStop(int shift) {
        //Controllo lo shift passato
        if ((shifts != null) && (shifts.size() >= shift)) {
            shifts.get(shift - 1).setStop(true);
            if (LOG_STOPS) Log.v(TAG, "setStop: " + shift);
        }
    }

    public String toString() {
        return "Day{" + this.localDate.toString() + "}";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (obj.getClass() != getClass()) return false;
        Day d = (Day) obj;
        return d.localDate == localDate;
    }

    @Override
    public int hashCode() {
        int prime = 13;
        int result = 1;
        result = prime * result + localDate.hashCode();
        return result;
    }

    @Override
    public Day clone() throws CloneNotSupportedException {
        Day day = (Day) super.clone();
        day.setIsToday(false);
        day.setOffWorkHalfTeams(this.offWorkHalfTeams);
        day.setShifts(this.shifts);
        return day;
    }

}
