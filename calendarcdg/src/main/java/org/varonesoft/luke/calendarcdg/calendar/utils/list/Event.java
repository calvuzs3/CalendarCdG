package org.varonesoft.luke.calendarcdg.calendar.utils.list;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.varonesoft.luke.calendarcdg.R;
import org.varonesoft.luke.calendarcdg.calendar.utils.Utils;

import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Event list item
 * A representation of an event in a user's calendar.
 *
 * @author brianreber
 */
public class Event extends ListItem {

    private final int id;
    private GregorianCalendar start;
    private GregorianCalendar end;
    private boolean allDay;
    private String color;
    private String location;

    /**
     * Creates an event with the given values.
     *
     * @param title    The title of the event
     * @param start    The start <code>Date</code> of the event
     * @param end      The end <code>Date</code> of the event
     * @param allDay   Whether this is an all-day event
     * @param color    The color of the calendar it belongs to
     * @param location The location of the event
     */
    public Event(String title, GregorianCalendar start, GregorianCalendar end, boolean allDay, String color, String location, int id) {
        this.title = title;
        this.start = start;
        this.end = end;
        this.allDay = allDay;
        this.color = color;
        this.location = location;
        this.id = id;
    }

    /**
     * Gets the location of this event
     *
     * @return location
     */
    public String getLocation() {
        return location;
    }

    /**
     * Gets the color of this event
     *
     * @return color
     */
    public String getColor() {
        return color;
    }

    /**
     * Sets the color of this event
     *
     * @param color color
     */
    public void setColor(String color) {
        this.color = color;
    }

    /**
     * Gets the starting date
     *
     * @return gregorian calendar start
     */
    public GregorianCalendar getStart() {
        return start;
    }

    /**
     * Gets the ending date
     *
     * @return gregorian calendar end
     */
    public GregorianCalendar getEnd() {
        return end;
    }

    /**
     * Gets whether this is an all day event
     *
     * @return isAllDay?
     */
    public boolean isAllDay() {
        return allDay;
    }

    /**
     * Compares the start time of this event to the given event.
     *
     * @param another The event to compare to
     */
    @Override
    public int compareTo(@NonNull ListItem another) {
        if (another.getType() == ItemType.SEPARATOR) {
            return 0;
        }

        return getStart().compareTo(((Event) another).getStart());
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Event [title=" + title + ", start=" + start.get(Calendar.MONTH) + "/" + start.get(Calendar.DAY_OF_MONTH) +
                "/" + start.get(Calendar.YEAR) + " " + start.get(Calendar.HOUR_OF_DAY) + ":" + start.get(Calendar.MINUTE) + ", end=" +
                +end.get(Calendar.MONTH) + "/" + end.get(Calendar.DAY_OF_MONTH) + "/" + end.get(Calendar.YEAR) + " " +
                end.get(Calendar.HOUR_OF_DAY) + ":" + end.get(Calendar.MINUTE) + ", allDay=" + allDay + ", color=" + color + ", location=" + location + "]";
    }

    /* (non-Javadoc)
     * @see org.reber.agenda.ListItem#getType()
     */
    @Override
    public ItemType getType() {
        return ItemType.EVENT;
    }

    /* (non-Javadoc)
     * @see org.reber.agenda.ListItem#getLayout(android.content.Context, android.view.ViewGroup)
     */
    @Override
    public LinearLayout getLayout(Context ctx, ViewGroup parent, Utils util) {
        LinearLayout v = (LinearLayout) LayoutInflater.from(ctx).inflate(R.layout.eventslist_rows, parent, false);
        String text = getTitle();
        String date = util.getFormattedTimeString(this);
        String location = getLocation();

        TextView label = v.findViewById(R.id.label);
        TextView labelDate = v.findViewById(R.id.labelDate);
        ImageView iv = v.findViewById(R.id.icon);
        TextView labelLocation = v.findViewById(R.id.labelLocation);

        if (location == null || location.equals("")) {
            label.setText(text);
            labelDate.setText(date);
            labelLocation.setHeight(0);
        } else {
            label.setText(text);
            labelDate.setText(date);
            labelLocation.setText(location);
        }

        int[] colors = new int[15 * 80];
        Arrays.fill(colors, Color.parseColor(getColor()));

        Bitmap bm = Bitmap.createBitmap(colors, 15, 80, Bitmap.Config.RGB_565);

        iv.setImageBitmap(bm);

        return v;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (allDay ? 1231 : 1237);
        result = prime * result + ((color == null) ? 0 : color.hashCode());
        result = prime * result + ((end == null) ? 0 : end.hashCode());
        result = prime * result
                + ((location == null) ? 0 : location.hashCode());
        result = prime * result + ((start == null) ? 0 : start.hashCode());
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Event other = (Event) obj;
        if (allDay != other.allDay) {
            return false;
        }
        if (color == null) {
            if (other.color != null) {
                return false;
            }
        } else if (!color.equals(other.color)) {
            return false;
        }
        if (end == null) {
            if (other.end != null) {
                return false;
            }
        } else if (!end.equals(other.end)) {
            return false;
        }
        if (location == null) {
            if (other.location != null) {
                return false;
            }
        } else if (!location.equals(other.location)) {
            return false;
        }
        if (start == null) {
            if (other.start != null) {
                return false;
            }
        } else if (!start.equals(other.start)) {
            return false;
        }
        return super.equals(other);
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }
}
