package org.varonesoft.luke.calendarcdg.calendar.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;
import android.text.format.DateUtils;

import org.varonesoft.luke.calendarcdg.Log;
import org.varonesoft.luke.calendarcdg.calendar.utils.list.Event;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.PriorityQueue;
import java.util.Set;

/**
 * Utilities
 *
 * Created by luke on 29/09/17.
 */

public class Utils {

    private static final String TAG = "Utils";
    private static final boolean LOG_ = false;

    private static final String CAL_PREFS = "pref_calendars";
    //    public static SimpleDateFormat monthFormat = new SimpleDateFormat("MMM");
    private final Set<AndroidCalendar> calendars;
    private final Context context;
    //    private boolean availableCalendars;
    private Set<AndroidCalendar> selectedCalendars;
    private boolean use24Hour;

    /**
     * Creates a new CalendarUtilities instance with the given context
     *
     * @param context Context
     */
    public Utils(Context context, boolean use24Hour) {
        this.context = context;
        this.use24Hour = use24Hour;
        calendars = new HashSet<>();
        selectedCalendars = new HashSet<>();
//        availableCalendars = false;
    }

    /**
     * Oftentimes all-day events have messed up starting times, which can cause
     * them to be displayed before events that happen in the previous day.
     * <p>
     * The times are usually off by the Timezone offset, so we should be able
     * to just subtract the offset from the given event time, and it should be
     * all fixed.
     *
     * @param eventStart start
     * @param eventEnd   date to modify
     */
    private static void fixAllDayEvent(GregorianCalendar eventStart, GregorianCalendar eventEnd) {
        long milliseconds;
        milliseconds = eventStart.getTimeInMillis();
        milliseconds -= eventStart.getTimeZone().getRawOffset();
        eventStart.setTimeInMillis(milliseconds);

        milliseconds = eventEnd.getTimeInMillis();
        milliseconds -= eventEnd.getTimeZone().getRawOffset();
        eventEnd.setTimeInMillis(milliseconds - 1000);
    }

    /**
     * The color data the calendar stores is an int. We want it in hex so that
     * we can compare it to the data Google has posted in their API. So we convert
     * it to hex and do some math to getHalfTeams it into the form Google shares.
     *
     * @param color The string the calendar database stores
     * @return The hex string as Google lists in their API
     * http://code.google.com/apis/calendar/data/2.0/reference.html#gCalcolor
     */
    private static String getColorHex(String color) {
        if (color == null) {
            return "";
        }

        try {
            int hex = Integer.parseInt(color);
            hex &= 0xFFFFFFFF;

            return String.format("#%08x", hex);
        } catch (NumberFormatException e) {
            return "";
        }
    }

    /**
     * Formats the given Event's date as: <br />
     * {Month} {Day}
     *
     * @param e The Event to format the date of
     * @return The Month and Day for the given event
     */
    private static String getFormattedDateString(Event e, String formatString) {
        Date date = e.getStart().getTime();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat(formatString);
        return formatter.format(date);
    }

    /**
     * Figures out whether the given Event is today or tomorrow.  If neither,
     * returns the formatted date string.
     *
     * @param event The event to check
     * @return Today, Tomorrow, or the date
     */
    public static String getDateString(Event event, String formatString) {

        if (formatString == null) {
            return getFormattedDateString(event, "MMM d");
        } else return getFormattedDateString(event, formatString);
    }

    /**
     * @param use24Hour the use24Hour to set
     */
    public void setUse24Hour(boolean use24Hour) {
        this.use24Hour = use24Hour;
    }

    /**
     * Gets a set of all available calendars on this device
     *
     * @return A set of all available calendars on this device
     */
    public Set<AndroidCalendar> getAvailableCalendars() {
        ContentResolver contentResolver = context.getContentResolver();

        Uri calendarsURI = CalendarContract.Calendars.CONTENT_URI;

        /* Check permissions. We ask for them in the CalendarsActivity choosing page */
        if (ActivityCompat.checkSelfPermission(context,
                Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {

            // Here we dont have permissions
            if (LOG_) Log.d(TAG, "PERMISSIONS DENIED");
//            availableCalendars = false;
            return calendars;
        }

        Cursor cursor = contentResolver.query(calendarsURI, new String[]{
                        CalendarContract.Calendars._ID,
                        CalendarContract.Calendars.CALENDAR_COLOR,
                        CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
                        CalendarContract.Calendars.NAME,
                        CalendarContract.Calendars.OWNER_ACCOUNT,
                        CalendarContract.Calendars.ACCOUNT_NAME},
                null, null, null);

        /* Get all the enabled calendars in the database */
        while (cursor != null && cursor.moveToNext()) {
            AndroidCalendar current = new AndroidCalendar(cursor.getString(0),
                    getColorHex(cursor.getString(1)), cursor.getString(2));
            calendars.add(current);
            // Log
            StringBuilder msg = new StringBuilder("Calendario{" + current.toString() + "[");
            msg.append(cursor.getString(0)).append("-").append(cursor.getString(1)).append("\n");
            msg.append(cursor.getString(2)).append("\n").append(cursor.getString(3)).append("\n");
            msg.append(cursor.getString(4)).append("\n").append(cursor.getString(5)).append("]}\n");
            if (LOG_) Log.d(TAG, msg.toString());
        }

        // Close cursor
        if (cursor != null) cursor.close();

        if (LOG_) Log.d(TAG, "PERMISSIONS GRANTED");
//        availableCalendars = true;
        return calendars;
    }

    /**
     * Retrieves the selected calendars from the SharedPreferences with the given key
     *
     * @param prefName The key the calendars are filed under
     * @return The Calendar instances associated with the calendars saved in preferences
     * @throws NoSuchElementException If there isn't a preference of the given name
     */
    public Set<AndroidCalendar> getSelectedCalendarFromPref(String prefName) {
        Set<AndroidCalendar> calsFromPref = new HashSet<>();
        SharedPreferences pref = null;
        Set<String> string = null;

        if (prefName != null) {
            pref = context.getSharedPreferences(prefName, 0);
        } else {
            if (context != null)
                pref = PreferenceManager.getDefaultSharedPreferences(context);
            else if (LOG_) Log.d(TAG, "getSelectedCalendarFromPref: context null");
        }

        if (pref != null)
            string = pref.getStringSet(CAL_PREFS, null);

        // to avoid a null object reference set an if
        //if (availableCalendars)
        // cant set the if statement: it is false until getAvailableCalendars()
        for (AndroidCalendar c : getAvailableCalendars()) {
            if (string == null || string.contains(c.getId())) {
                calsFromPref.add(c);
            }
        }

        if (LOG_) Log.v(TAG, "getSelectedCalendarsFromPref" + calsFromPref.toString());
        return calsFromPref;
    }

    /**
     * Save the list of selected calendars in a SharedPreference with the
     * given key
     *
     * @param prefName The key to file the preference under
     */
    public void saveSelectedCalendarsPref(String prefName) {
        Set<String> selectedCalsInfo = new HashSet<>();
        for (AndroidCalendar c : selectedCalendars) {
            selectedCalsInfo.add(c.getId());
        }

        // Save the package name in the preferences
        SharedPreferences pref;

        if (prefName != null) {
            pref = context.getSharedPreferences(prefName, 0);
        } else {
            pref = PreferenceManager.getDefaultSharedPreferences(context);
        }

        SharedPreferences.Editor edit = pref.edit();
        edit.putStringSet(CAL_PREFS, selectedCalsInfo);
        edit.apply();
        if (LOG_) Log.v(TAG, "saveSelectedCalendarsPref" + selectedCalsInfo.toString());
    }

    /**
     * Gets the selected calendars for this instance
     *
     * @return The set of calendars the user has selected
     */
    public Set<AndroidCalendar> getSelectedCalendars() {
        return selectedCalendars;
    }

    /**
     * Set the selected calendars for this instance
     *
     * @param selected The set of calendars the user has selected
     */
    public void setSelectedCalendars(Set<AndroidCalendar> selected) {
        selectedCalendars = selected;
    }

    /**
     * Gets the event data (the current time through numDays past now)
     * from the user's enabled calendars.
     *
     * @param numDays The number of days after the current time to getHalfTeams the events from
     * @return A Collection of Events that are on the user's calendar
     */
    public Collection<Event> getCalendarData(int numDays, boolean showCurrentEvent) {
        // Using a PriorityQueue for the events because it sorts them as you addTeam them
        PriorityQueue<Event> events = new PriorityQueue<>();

        // For each calendar, getHalfTeams the events after the current time
        for (AndroidCalendar cal : selectedCalendars) {
            events.addAll(getEventDataFromCalendar(cal, numDays, showCurrentEvent));
        }

        ArrayList<Event> sorted = new ArrayList<>(events);
        Collections.sort(sorted);

        return sorted;
    }

    public Collection<Event> getCalendarData(int year, int month) {
        // Data inizio mese
        GregorianCalendar gc = new GregorianCalendar(year, month - 1, 1, 0, 1);
        // Using a PriorityQueue for the events because it sorts them as you addTeam them
        PriorityQueue<Event> events = new PriorityQueue<>();
        // For each calendar, getHalfTeams the events after the current time
        for (AndroidCalendar cal : selectedCalendars) {
            events.addAll(getEventDataFromCalendar(cal, gc.getTimeInMillis()));
        }
        ArrayList<Event> sorted = new ArrayList<>(events);
        Collections.sort(sorted);
        return sorted;
    }

    /**
     * Actually getHalfTeams the events from the given calendar
     *
     * @param cal     The calendar to getHalfTeams events from
     * @param numDays The number of days after the current time to getHalfTeams the events from
     * @return A collection of events from the given calendar
     */
    private PriorityQueue<Event> getEventDataFromCalendar(AndroidCalendar cal, int numDays, boolean showCurrentEvent) {
        GregorianCalendar todayDate = new GregorianCalendar();
        todayDate.setTimeInMillis(System.currentTimeMillis());
        PriorityQueue<Event> events = new PriorityQueue<>();

        Uri.Builder builder = CalendarContract.Instances.CONTENT_URI.buildUpon();
        GregorianCalendar today = new GregorianCalendar();
        long now = todayDate.getTimeInMillis();
        long temp = today.getTimeInMillis() - (today.get(GregorianCalendar.HOUR_OF_DAY) * DateUtils.HOUR_IN_MILLIS);

        ContentUris.appendId(builder, now - 5 * DateUtils.MINUTE_IN_MILLIS);
        ContentUris.appendId(builder, temp + numDays * DateUtils.DAY_IN_MILLIS);

        Cursor cursor = null;

        try {
            cursor = context.getContentResolver().query(builder.build(),
                    new String[]{CalendarContract.Instances.TITLE, CalendarContract.Instances.BEGIN,
                            CalendarContract.Instances.END, CalendarContract.Instances.ALL_DAY,
                            CalendarContract.Instances.EVENT_LOCATION, CalendarContract.Instances.EVENT_ID},
                    CalendarContract.Events.CALENDAR_ID + "=" + cal.getId(), null,
                    CalendarContract.Instances.START_DAY + " ASC, " + CalendarContract.Instances.START_MINUTE + " ASC");
        } catch (SQLiteException e) {
            if (LOG_) Log.d(TAG, e.getMessage());
        }


        while (cursor != null && cursor.moveToNext()) {
            GregorianCalendar begin = new GregorianCalendar();
            begin.setTimeInMillis(cursor.getLong(1));
            GregorianCalendar end = new GregorianCalendar();
            end.setTimeInMillis(cursor.getLong(2));
            boolean allDay = !"0".equals(cursor.getString(3));

            // All day events have times that are off by the Timezone offset, so we
            // need to fix that to getHalfTeams it to display correctly in the list and on the widget
            if (allDay) {
                fixAllDayEvent(begin, end);
            }

            if ((begin.after(todayDate) && !showCurrentEvent) || (showCurrentEvent && end.after(todayDate))) {
                events.add(new Event(cursor.getString(0), begin, end, allDay, cal.getColor(),
                        (cursor.getString(4) == null ? "" : cursor.getString(4)), cursor.getInt(5)));
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        return events;
    }

    private PriorityQueue<Event> getEventDataFromCalendar(AndroidCalendar cal, long date) {

        PriorityQueue<Event> events = new PriorityQueue<>();
        Uri.Builder builder = CalendarContract.Instances.CONTENT_URI.buildUpon();

        GregorianCalendar startDate = new GregorianCalendar();
        startDate.setTimeInMillis(date);
        startDate.set(GregorianCalendar.HOUR_OF_DAY, 0);
        startDate.set(GregorianCalendar.MINUTE, 1);

        ContentUris.appendId(builder, startDate.getTimeInMillis());

        GregorianCalendar endDate = (GregorianCalendar) startDate.clone();
        endDate.add(GregorianCalendar.MONTH, 1);
        endDate.add(GregorianCalendar.MINUTE, -2);

        ContentUris.appendId(builder, endDate.getTimeInMillis());

        Cursor cursor = null;

        try {
            cursor = context.getContentResolver().query(
                    builder.build(),
                    new String[]{CalendarContract.Instances.TITLE, CalendarContract.Instances.BEGIN,
                            CalendarContract.Instances.END, CalendarContract.Instances.ALL_DAY,
                            CalendarContract.Instances.EVENT_LOCATION, CalendarContract.Instances.EVENT_ID},
                    CalendarContract.Events.CALENDAR_ID + "=" + cal.getId(),
                    null,
                    CalendarContract.Instances.START_DAY + " ASC, " + CalendarContract.Instances.START_MINUTE + " ASC");
        } catch (SQLiteException e) {
            if (LOG_) Log.d(TAG, e.getMessage());
        }


        while (cursor != null && cursor.moveToNext()) {
            GregorianCalendar begin = new GregorianCalendar();
            begin.setTimeInMillis(cursor.getLong(1));
            GregorianCalendar end = new GregorianCalendar();
            end.setTimeInMillis(cursor.getLong(2));
            boolean allDay = !"0".equals(cursor.getString(3));

            // All day events have times that are off by the Timezone offset, so we
            // need to fix that to getHalfTeams it to display correctly in the list and on the widget
            if (allDay) {
                fixAllDayEvent(begin, end);
            }

            if ((begin.after(startDate)) || (end.after(startDate))) {
                events.add(new Event(cursor.getString(0), begin, end, allDay, cal.getColor(),
                        (cursor.getString(4) == null ? "" : cursor.getString(4)), cursor.getInt(5)));
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        return events;
    }

    /**
     * Gets the time of the start and end of the given event, formatted in this manner:
     * "All Day" if the event is an all day event
     * "HH:MM AM/PM - HH:MM AM/PM"
     *
     * @param event The event to getHalfTeams the time for
     * @return A string with the given event's start and end time formatted as listed above
     */
    @SuppressLint("SimpleDateFormat")
    public String getFormattedTimeString(Event event) {
        GregorianCalendar todayDate = new GregorianCalendar();
        todayDate.setTimeInMillis(System.currentTimeMillis());

        Date startDate = event.getStart().getTime();
        Date endDate = event.getEnd().getTime();

        SimpleDateFormat formatter;

        if (event.isAllDay()) {
            return "allDay";
        } else if (use24Hour) {
            formatter = new SimpleDateFormat("H:mm");
        } else {
            formatter = new SimpleDateFormat("h:mm a");
        }

        String startString = formatter.format(startDate);
        String endString = formatter.format(endDate);

        if (event.getStart().before(todayDate) && event.getEnd().after(todayDate)) {
            return "Adesso" + " - " + endString;
        } else {
            return startString + " - " + endString;
        }
    }

}
