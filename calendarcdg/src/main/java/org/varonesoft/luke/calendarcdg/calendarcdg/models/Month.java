package org.varonesoft.luke.calendarcdg.calendarcdg.models;

import android.content.Context;

import org.joda.time.LocalDate;
import org.varonesoft.luke.calendarcdg.Log;
import org.varonesoft.luke.calendarcdg.calendar.utils.Utils;
import org.varonesoft.luke.calendarcdg.calendar.utils.list.Event;

import java.util.ArrayList;
import java.util.Collection;
import java.util.GregorianCalendar;

/**
 * Month
 * <p>
 * Created by luke on 21/09/17.
 */

public class Month {

    private final static String TAG = "Month";
    private final static boolean LOG_ = false;
    private final static boolean LOG_EVENTS = false;
    private final static boolean LOG_STOPS = false;
    /**
     * Non regge la fermata di fine anno
     * Non regge la fermata a cavallo di diversi mesi
     * L'ultimo giorno di fermata e' la ripresa lavori non viene segnato quel giorno
     * poiche' si riprende sempre la mattina..
     */
    private final static ArrayList<Stop> STOPS = new ArrayList<>();

    static {
//        // 2018
        STOPS.add(new Stop(2018, 8, 11, 3, 2018, 8, 20, 1));
        STOPS.add(new Stop(2018, 12, 21, 3, 2018, 12, 32, 1));
//        // 2019
        STOPS.add(new Stop(2019, 1, 1, 1, 2019, 1, 3, 1));
        STOPS.add(new Stop(2019, 4, 19, 3, 2019, 4, 23, 1));
        STOPS.add(new Stop(2019, 6, 20, 3, 2019, 6, 25, 1));
        STOPS.add(new Stop(2019, 8, 13, 3, 2019, 8, 21, 1));
        STOPS.add(new Stop(2019, 12, 24, 3, 2019, 12, 27, 1));
        STOPS.add(new Stop(2019, 12, 31, 3, 2019, 12, 32, 1));
    }

    // Variabili
    private LocalDate localDate;
    private boolean current;

    // Liste
    private ArrayList<Day> daysList;
    //private ArrayList<Event>    eventsList;


    /**
     * COSTRUTTORE
     *
     * @param localDate Data in formato LocalDate
     */
    public Month(LocalDate localDate) {

        this.setLocalDate(localDate);
        int maxdays = localDate.dayOfMonth().getMaximumValue();
        daysList = new ArrayList<>(maxdays);

        if (LOG_) Log.d(TAG, localDate.toString() + " (maxdays=" + maxdays + ")");
    }

    private void setLocalDate(LocalDate localDate) {
        // Create a new immutable object
        this.localDate = localDate.withDayOfMonth(1);
        if (LOG_) Log.d(TAG, "setLocalDate: " + this.localDate.toString());

        LocalDate localDateNew = LocalDate.now();
        setIsCurrent((localDateNew.getYear() == localDate.getYear()) &&
                (localDateNew.getMonthOfYear() == localDate.getMonthOfYear()));
        if (isCurrent()) setToday(localDateNew.getDayOfMonth());
    }

    private boolean isCurrent() {
        return current;
    }

    private void setIsCurrent(boolean b) {
        this.current = b;
        if (isCurrent()) {
            this.setToday(localDate.getDayOfMonth());
            if (LOG_) Log.d(TAG, "setIsCurrent: " + localDate.getDayOfMonth());
        }
    }

    public void setToday(int dayOfMonth) {
        if ((daysList != null) && (daysList.size() >= dayOfMonth)) {
            daysList.get(dayOfMonth - 1).setIsToday(true);
            if (LOG_) Log.d(TAG, "setToday: " + dayOfMonth);
        }
    }

    public ArrayList<Day> getDaysList() {
        return this.daysList;
    }

    /**
     * Settiamo i giorni
     * Fornire una lista da associare
     * Al chiamante e' delegato il compito di clonare
     * i membri della lista se necessario
     *
     * @param dayslist lista giorni
     */
    public void setDaysList(ArrayList<Day> dayslist) {
        //
        this.daysList = dayslist;
        if (LOG_) Log.d(TAG, "setDaysList: ");

        // Settiamo oggi
        if (isCurrent()) setToday(LocalDate.now().getDayOfMonth());
    }

    public void setEvents(Context context) {
        getEvents(context);
    }

    public void setStops() {
        getStops();
    }

    private void getEvents(Context c) {
        if (LOG_EVENTS) Log.v(TAG, "getEvents: start");

        Utils u = new Utils(c, true);
        u.setSelectedCalendars(u.getSelectedCalendarFromPref(null));

        Collection<Event> e = u.getCalendarData(this.localDate.getYear(), this.localDate.getMonthOfYear());
        ArrayList<Event> events = new ArrayList<>(e);

        if (!(events.isEmpty())) {
            for (Event event : events) {
                GregorianCalendar gc = event.getStart();
                try {
                    Day d = this.daysList.get(gc.get(GregorianCalendar.DAY_OF_MONTH) - 1);
                    if (d != null) {
                        d.addEvent(event);
                        if (LOG_EVENTS) Log.d(TAG, "getEvents: " + d.toString() + event.toString());
                    }
                } catch (Exception x) {
                    Log.e(TAG, x.getMessage());
                }
            }
        }
        if (LOG_EVENTS) Log.v(TAG, "getEvents: end");
    }

    /**
     * Setta le fermate previste per il mese in oggetto.
     * Recuperiamo le informazioni dagli eventi e traduciamo in turno e giorno
     */
    private void getStops() {
        if (LOG_STOPS) Log.v(TAG, "getStops: start");
        /*
        Utilizziamo una funzione fittizia che ritorna
        gli stop del mese richiesto.
        Ogni mese richiede gli stop che verranno prelevati dagli eventsList del
        calendario dedicato (oppure no, vedremo)
         */
        ArrayList<Stop> stops = getStopsForDate(localDate.getYear(), localDate.getMonthOfYear());

        for (Stop s : stops) {

            if (LOG_STOPS) Log.d(TAG, "getStops: " + s.toString());
            try {

                // Finche dura la fermata settiamo gli stop

                // Ci serve un Contatore di turni
                int shift_counter = s.shift;
                // Ci serve un contatore di giorni
                int day_counter = s.day;

                // Finche il contatore e' inferiore al giorno finale
                while (day_counter < s.endday) {

                    // Prendo il giorno
                    Day d = this.daysList.get(day_counter - 1);
                    if (d != null) {

                        //Loggo
                        if (LOG_STOPS) Log.d(TAG, "getStops: Day{" + d.toString() + "}");

                        // Finche ci sono turni li setto a fermata
                        while (shift_counter < 4) {

                            d.setStop(shift_counter);
                            shift_counter++;
                        }

                        shift_counter = 1;
                    }

                    day_counter++;
                }

            } catch (Exception x) {
                Log.e(TAG, x.getMessage());
            }
        }
        if (LOG_STOPS) Log.v(TAG, "getStops: end");
    }

    /**
     * Il mese richiede gli stop per se stesso
     * la funzione ritorna un elenco: n.giorno n.turno
     * es.
     *
     * @return array delle fermate
     */
    private ArrayList<Stop> getStopsForDate(int year, int month) {
        if (LOG_STOPS) Log.d(TAG, "getStopsForDate( " + year + ", " + month + " )");

        // Ritorna gli stop del mese
        final ArrayList<Stop> result = new ArrayList<>();

        for (Stop s : STOPS) {
            if (s.year == year) {
                if (s.month == month) {
                    result.add(s);
                }
            }
        }
        return result;
    }

}
