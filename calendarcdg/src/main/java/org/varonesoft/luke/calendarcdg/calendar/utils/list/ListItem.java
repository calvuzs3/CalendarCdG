package org.varonesoft.luke.calendarcdg.calendar.utils.list;

import android.content.Context;
import android.view.ViewGroup;

import org.varonesoft.luke.calendarcdg.calendar.utils.Utils;

/**
 * An abstraction of a ListItem in our Agenda ListView.  Can
 * specify what type of ListItem it is by overriding the getType()
 * method.
 *
 * @author brianreber
 *
 * Created by luke on 30/09/17.
 *
 */
public abstract class ListItem implements Comparable<ListItem> {


    /**
     * The title of this ListItem
     */
    protected String title;

    /**
     * Gets the title of this ListItem
     *
     * @return title value
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title of this ListItem
     *
     * @param title string
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Gets the type of this ListItem
     *
     * @return A value from the enum ItemType
     */
    public abstract ItemType getType();

    /**
     * Gets the layout this ListItem will use to display in the UI
     *
     * @param ctx    The context in which to open the layout
     * @param parent The parent ViewGroup to the layout to be returned
     * @return The LinearLayout to be used in the ListView
     */
    public abstract ViewGroup getLayout(Context ctx, ViewGroup parent, Utils util);

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((title == null) ? 0 : title.hashCode());
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
        ListItem other = (ListItem) obj;
        if (title == null) {
            if (other.title != null) {
                return false;
            }
        } else if (!title.equals(other.title)) {
            return false;
        }
        return true;
    }

    /**
     * A list of the different types of ListItems that can be in
     * the Agenda ListView
     */
    public enum ItemType {
        EVENT, SEPARATOR
    }
}
