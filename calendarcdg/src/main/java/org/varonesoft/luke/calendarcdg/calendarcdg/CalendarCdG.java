package org.varonesoft.luke.calendarcdg.calendarcdg;

import android.content.Context;

import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.varonesoft.luke.calendarcdg.CalendarCdGPreferences;
import org.varonesoft.luke.calendarcdg.Log;
import org.varonesoft.luke.calendarcdg.R;
import org.varonesoft.luke.calendarcdg.calendarcdg.models.Day;
import org.varonesoft.luke.calendarcdg.calendarcdg.models.HalfTeam;
import org.varonesoft.luke.calendarcdg.calendarcdg.models.Month;
import org.varonesoft.luke.calendarcdg.calendarcdg.models.Shift;
import org.varonesoft.luke.calendarcdg.calendarcdg.models.ShiftType;

import java.util.ArrayList;

import static org.varonesoft.luke.calendarcdg.CalendarCdGPreferences.KEY_SCHEME_START_DAY;
import static org.varonesoft.luke.calendarcdg.CalendarCdGPreferences.KEY_SCHEME_START_MONTH;
import static org.varonesoft.luke.calendarcdg.CalendarCdGPreferences.KEY_SCHEME_START_YEAR;
import static org.varonesoft.luke.calendarcdg.CalendarCdGPreferences.KEY_SHOW_CALENDARS;
import static org.varonesoft.luke.calendarcdg.CalendarCdGPreferences.KEY_SHOW_STOPS;
import static org.varonesoft.luke.calendarcdg.CalendarCdGPreferences.KEY_USER_HALFTEAM;
import static org.varonesoft.luke.calendarcdg.CalendarCdGPreferences.VALUE_SHOW_CALENDARS;
import static org.varonesoft.luke.calendarcdg.CalendarCdGPreferences.VALUE_SHOW_STOPS;
import static org.varonesoft.luke.calendarcdg.Costants.CalendarCdg;
import static org.varonesoft.luke.calendarcdg.Costants.CalendarCdg.CAL_NUMERO_MESI;
import static org.varonesoft.luke.calendarcdg.Costants.CalendarCdg.SCHEME;
import static org.varonesoft.luke.calendarcdg.Costants.CalendarCdg.SCHEME_START_DAY;
import static org.varonesoft.luke.calendarcdg.Costants.CalendarCdg.SCHEME_START_MONTH;
import static org.varonesoft.luke.calendarcdg.Costants.CalendarCdg.SCHEME_START_YEAR;

/**
 * Created by luke on 17/09/17.
 * <p>
 * Ricreo il calendario sfruttando le nuove date di joda-time
 * Quindi dato un qualunque schema ed una data di partenza
 * posso creare lo chema per qualsiasi anno
 * superando la difficolta-, o meglio la limitazione dell-anno
 * hard-coded
 */
public class CalendarCdG {

    public final static ArrayList<HalfTeam> HALFTEAM_ALL;
    private static final boolean LOG_ = true;
    private static String TAG = "CalendarCdG";
    // Istance Reference
    private static CalendarCdG ourInstance = null;

    static {
        HALFTEAM_ALL = new ArrayList<>(CalendarCdg.NUMERO_SEMISQUADRE);
        HALFTEAM_ALL.add(new HalfTeam('A'));
        HALFTEAM_ALL.add(new HalfTeam('B'));
        HALFTEAM_ALL.add(new HalfTeam('C'));
        HALFTEAM_ALL.add(new HalfTeam('D'));
        HALFTEAM_ALL.add(new HalfTeam('E'));
        HALFTEAM_ALL.add(new HalfTeam('F'));
        HALFTEAM_ALL.add(new HalfTeam('G'));
        HALFTEAM_ALL.add(new HalfTeam('H'));
        HALFTEAM_ALL.add(new HalfTeam('I'));
    }

    // Members
    private ArrayList<Day> mSchemeDaysList;
    private ArrayList<Month> mMonths;
    private ArrayList<ShiftType> mShiftTypes;
    private LocalDate mSchemeDate;
    private LocalDate mCursorDate;
    private HalfTeam userHalfTeam = null;

    // Users's HalfTeam
    //private static String[] hts;
    // Feature
    private boolean showCalendars;
    private boolean showStops;
    // fFlag to redraw
    private boolean refresh = false;

    /* EMPTY CONSTRUCTOR */
    private CalendarCdG() {
    }

    // Returns the reference to this object
    public static CalendarCdG getInstance(Context context) {
        if (ourInstance == null) {
            ourInstance = new CalendarCdG();
            ourInstance.init(context);
        }
        return ourInstance;
    }

    public boolean isRefresh() {
        return refresh;
    }

    public void setRefresh(boolean refresh) {
        this.refresh = refresh;
    }

    /**
     * As the name says
     */
    private void init(Context context) {
        final String fTAG = TAG + " init";
        if (LOG_) Log.v(fTAG, "start");

        // Liste
        mSchemeDaysList = new ArrayList<>();
        mMonths = new ArrayList<>();

        // La data cursore al primo del mese
        LocalDate temp = LocalDate.now();
        mCursorDate = new LocalDate(temp.getYear(), temp.getMonthOfYear(), 1);
        if (LOG_) Log.d(fTAG, "CursorData " + mCursorDate.toString());

        // La data schema
        if (context == null) {
            mSchemeDate = new LocalDate(SCHEME_START_YEAR, SCHEME_START_MONTH, SCHEME_START_DAY);
        } else {
            mSchemeDate = new LocalDate(
                    CalendarCdGPreferences.getSharedPreference(context,
                            KEY_SCHEME_START_YEAR, SCHEME_START_YEAR),
                    CalendarCdGPreferences.getSharedPreference(context,
                            KEY_SCHEME_START_MONTH, SCHEME_START_MONTH),
                    CalendarCdGPreferences.getSharedPreference(context,
                            KEY_SCHEME_START_DAY, SCHEME_START_DAY));
        }
        if (LOG_) Log.d(fTAG, "SchemeData " + mSchemeDate.toString());

        // Settiamo la squadra utente base
        setUserHalfTeam(new HalfTeam("A"));

        // 1. Istanzia i turni giornalieri
        fetchShiftTypes();

        // 2. Istanzia lo schema
        fetchSchemeList();

        // 3. Genera lo schema base
        fetchMonths(context);

        if (LOG_) Log.d(fTAG, "done");
    }

    /**
     * I tre mesi
     */
    private void fetchMonths(Context context) {
        final String fTAG = TAG + ": fetchMonths";
        LocalDate ld;
        try {
            // generates 3 months: previous-current-next
            for (int i = 0; i < CAL_NUMERO_MESI; i++) {

                // Let's start from the cursor date (today during init)
                ld = mCursorDate.plusMonths(i - 1);
                if (LOG_) Log.d(fTAG, "generating month in date: " + ld.toString()
                        + " (" + mSchemeDate.toString() + ")");

                Month m = new Month(ld);
                m.setDaysList(getShifts(ld));
                if (showCalendars) m.setEvents(context);
                if (showStops) m.setStops();

                mMonths.add(m);
                if (LOG_) Log.d(fTAG, "Month added " + m.toString());
            }
        } catch (Exception e) {
            Log.e(fTAG, e.getMessage());
        }

    }

    /**
     * I tipi di turno con orari
     * per uso futuro
     */
    private void fetchShiftTypes() {

        if (LOG_) Log.d(TAG, "fetchShiftTypes");
        this.mShiftTypes = new ArrayList<>(3);
        this.mShiftTypes.add(new ShiftType("1", "Mattino", 5, 0, 8, 0));
        this.mShiftTypes.add(new ShiftType("2", "Pomeriggio", 13, 0, 8, 0));
        this.mShiftTypes.add(new ShiftType("3", "Notte", 21, 0, 8, 0));
    }

    /**
     * It creates mSchemeDaysList, an ArrayList of Days with the given scheme
     * A separate function to recall whenever ther is a change in settings or scheme
     */
    private void fetchSchemeList() {
        final String fTAG = TAG + ":fetchSchemeList";
        if (LOG_) Log.d(fTAG, "start");

        Day g;
        Shift shift;
        HalfTeam halfTeam;

        LocalDate ld = mSchemeDate;
        if (LOG_) Log.d(fTAG, "Scheme date: " + mSchemeDate.toString());

        // Fetch
        for (int i = 0; i < CalendarCdg.NUMERO_RIPETIZIONE; i++) {

            g = new Day(ld.plusDays(i));
            try {
                for (int j = 0; j < CalendarCdg.NUMERO_TURNI_AL_GIORNO; j++) {
                    shift = new Shift(mShiftTypes.get(j));
                    for (int a = 0; a < CalendarCdg.NUMERO_SEMISQUADRE_TURNO; a++) {
                        halfTeam = new HalfTeam(SCHEME[i][j][a]);
                        shift.addTeam(halfTeam);
                    }
                    g.addShift(shift);
                }
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
                e.printStackTrace();
            }
            mSchemeDaysList.add(g);


            StringBuilder sb = new StringBuilder("Day[" + (i + 1) + "]{");
            for (Shift shift1 : g.getShifts()) {
                sb.append(shift1.toString());
                for (HalfTeam halfTeam1 : shift1.getHalfTeams()) {
                    sb.append("[").append(halfTeam1.getName()).append("]");
                }
            }
            sb.append("}");
            if (LOG_) Log.d(fTAG, sb.toString());
        }

        if (LOG_) Log.d(fTAG, "Scheme size " + mSchemeDaysList.size());
    }

    /**
     * La funzione ritorna una nuova lista di giorni con il dato schema
     *
     * @param ldreq la data richiesta se ne prende mese e anno.
     * @return array list of days
     */
    private ArrayList<Day> getShifts(LocalDate ldreq) {
        final String fTAG = TAG + ":getShifts";
        if (LOG_) Log.d(fTAG, "start");

        // Creo una nuova lista
        ArrayList<Day> result = new ArrayList<>();

        if ((mSchemeDaysList == null) || (mSchemeDaysList.isEmpty())) {
            if (LOG_) Log.d(fTAG, "mSchemeDaysList isEmpty or null");
            return result;
        }
        if (ldreq == null) {
            if (LOG_) Log.d(fTAG, "requested localDate is null");
            return result;
        }

        // Get the size of the scheme (should be constant)
        int schemaSize = mSchemeDaysList.size();
        if (LOG_) Log.d(fTAG, "DayListSize=" + schemaSize);

//        Set the day to first
        LocalDate mRequestedStartDate = new LocalDate(ldreq.getYear(), ldreq.getMonthOfYear(), 1);
        if (LOG_) Log.d(fTAG, "mRequestedStartDate=" + mRequestedStartDate.toString());

        // Calculate days between the dates
        int difference = Days.daysBetween(mSchemeDate, mRequestedStartDate).getDays();
        if (LOG_) Log.d(fTAG, "difference=" + difference);

        // Calculate the starting point in the scheme
        int startoffset = difference % schemaSize;
        if (startoffset < 0) startoffset = schemaSize + startoffset;
        if (LOG_) Log.d(fTAG, "startoffset=" + startoffset);

        // Get the number of days in the month
        int maxdays = mRequestedStartDate.dayOfMonth().getMaximumValue();
        if (LOG_) Log.d(fTAG, "maxdays=" + maxdays);

        for (int i = 0; i < maxdays; i++) {

            // Put in the list a clone from the scheme
            try {
                Day d = mSchemeDaysList.get(startoffset).clone();
                // Set the date
                d.setLocalDate(mRequestedStartDate);
                result.add(d);
            } catch (Exception e) {
                Log.e(fTAG, "(try block) " + e.getMessage());
            }

            // Step forward
            mRequestedStartDate = mRequestedStartDate.plusDays(1);
            startoffset++;
            if (startoffset == schemaSize) startoffset = 0;
        }

        return result;
    }

    public void moveForward(Context context) {
        if (LOG_) Log.d(TAG, "moveForward");

        // The forward button has been pressed
        this.mCursorDate = mCursorDate.plusMonths(1);
        // 0 si elemina, 1 sposta a 0, 2 sposta a 1, si crea un 2
        this.mMonths.remove(0);
        Month m = new Month(mCursorDate.plusMonths(1));
        m.setDaysList(getShifts(mCursorDate.plusMonths(1)));
        if (showCalendars) m.setEvents(context);
        if (showStops) m.setStops();
        this.mMonths.add(m);
    }

    public void moveBackward(Context context) {
        if (LOG_) Log.d(TAG, "moveBackward");

        // The forward button has been pressed
        mCursorDate = mCursorDate.minusMonths(1);
        // 2 si elemina, 1 sposta a 2, 0 sposta a 1, si crea un 0
        mMonths.remove(2);
        Month m = new Month(mCursorDate.minusMonths(1));
        m.setDaysList(getShifts(mCursorDate.minusMonths(1)));
        if (showCalendars) m.setEvents(context);
        if (showStops) m.setStops();
        mMonths.add(0, m);
    }

    /**
     * @return Mese Il mese cursored
     */
    public Month getMonth() {
        return getMonth(1);
    }

    /**
     * @param position posizione nell'array
     * @return Mese
     */
    private Month getMonth(int position) {
        return this.mMonths.get(position);
    }

    /**
     * Ritorna una data creata con la data cursore
     *
     * @return LocalDate La data Cursor
     */
    public LocalDate getCursorDate() {
        return mCursorDate;
    }

    /**
     * Ritorna la semisquadra dell'utente
     *
     * @return HalfTeam
     */
    public HalfTeam getUserHalfTeam() {
        if (userHalfTeam == null)
            setUserHalfTeam(new HalfTeam("A"));
        return userHalfTeam;
    }

    /**
     * Setta la semisquadra dell'utente
     *
     * @param userHalfTeam = la semisquadra da settare
     */
    private void setUserHalfTeam(HalfTeam userHalfTeam) {
        // Check
        if ((this.userHalfTeam != null) && this.userHalfTeam.equals(userHalfTeam)) return;
        // Set
        this.userHalfTeam = userHalfTeam;
    }

    /**
     * Read from preferences and sets the value
     *
     * @param context context
     */
    public void setSharedPreferences(Context context) {
        final String fTAG = TAG + "setSharedPreferences: ";

        boolean showCalendars = CalendarCdGPreferences.getSharedPreference(context,
                KEY_SHOW_CALENDARS, VALUE_SHOW_CALENDARS);
        boolean showStops = CalendarCdGPreferences.getSharedPreference(context,
                KEY_SHOW_STOPS, VALUE_SHOW_STOPS);

        if (this.showCalendars != showCalendars) {
            this.showCalendars = showCalendars;
            setRefresh(true);
        }
        if (this.showStops != showStops) {
            this.showStops = showStops;
            setRefresh(true);
        }
        String[] hts = context.getResources().getStringArray(R.array.pref_entries_user_halfteam);

        if (hts.length > 0) {
            // Should alway be here
            HalfTeam userHalfTeam = new HalfTeam(hts[Integer.valueOf(
                    CalendarCdGPreferences.getSharedPreference(context, KEY_USER_HALFTEAM, "1"))]);
            if (!this.userHalfTeam.equals(userHalfTeam)) {
                this.userHalfTeam = userHalfTeam;
                setRefresh(true);
            }
        } else {
            userHalfTeam = new HalfTeam("B");
            Log.e(fTAG, "UserTeam{" + userHalfTeam.toString() + "}");
            setRefresh(true);
            return;
        }
        Log.d(fTAG, "UserTeam{" + userHalfTeam.toString() + "}");

        if (isRefresh()) {
            mMonths.clear();
            fetchMonths(context);
        }
    }

    public void refresh(Context context) {
        mSchemeDaysList.clear();
        mMonths.clear();
        init(context);
        // Can't do this because on screen orientation changes
        // the fragment is adjourned when it has still to be removed
        // from reference (mCalendarCdGEventsListFragment
        // and from mTwoPane
//        if (listener != null) listener.onUpdate();
    }

    // Set preference
    public void setShowCalendars(boolean showCalendars) {
        this.showCalendars = showCalendars;
    }

    // Set preference
    public void setShowStops(boolean showStops) {
        this.showStops = showStops;
    }

}
