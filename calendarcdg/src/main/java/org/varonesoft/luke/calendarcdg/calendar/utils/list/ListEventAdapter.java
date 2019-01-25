package org.varonesoft.luke.calendarcdg.calendar.utils.list;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;

import org.varonesoft.luke.calendarcdg.calendar.utils.Utils;

import java.util.List;

/**
 * Event List Adapter
 *
 * Created by luke on 30/09/17.
 */

public class ListEventAdapter extends ArrayAdapter<ListItem> implements ListAdapter {

    private Utils util;

    public ListEventAdapter(Context context, int textViewResourceId, List<ListItem> objects, Utils util) {
        super(context, textViewResourceId, objects);

        this.util = util;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        return this.getItem(position).getLayout(getContext(), parent, util);
    }

    /* (non-Javadoc)
     * @see android.widget.BaseAdapter#areAllItemsEnabled()
     */
    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    /* (non-Javadoc)
     * @see android.widget.BaseAdapter#isEnabled(int)
     */
    @Override
    public boolean isEnabled(int position) {
        return this.getItem(position).getType() != ListItem.ItemType.SEPARATOR;
    }
}