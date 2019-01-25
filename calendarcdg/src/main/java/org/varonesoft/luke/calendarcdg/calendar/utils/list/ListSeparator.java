package org.varonesoft.luke.calendarcdg.calendar.utils.list;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.varonesoft.luke.calendarcdg.R;
import org.varonesoft.luke.calendarcdg.calendar.utils.Utils;

/**
 * Represents a separator in the ListView
 *
 * @author brianreber
 *         <p>
 *         Created by luke on 30/09/17.
 */
public class ListSeparator extends ListItem {

    /**
     * Creates a new Separator for the ListView with the given title
     *
     * @param title string
     */
    public ListSeparator(String title) {
        this.title = title;
    }

    /* (non-Javadoc)
     * @see org.reber.agenda.ListItem#getType()
     */
    @Override
    public ItemType getType() {
        return ItemType.SEPARATOR;
    }

    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(@NonNull ListItem another) {
        return 0;
    }

    /* (non-Javadoc)
     * @see org.reber.agenda.ListItem#getLayout(android.content.Context, android.view.ViewGroup)
     */
    @Override
    public LinearLayout getLayout(Context ctx, ViewGroup parent, Utils util) {
        LinearLayout v = (LinearLayout) LayoutInflater.from(ctx).inflate(R.layout.eventslist_separator, parent, false);

        TextView label = v.findViewById(R.id.list_separator_label);
        label.setText(getTitle());

        return v;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return title;
    }

}
