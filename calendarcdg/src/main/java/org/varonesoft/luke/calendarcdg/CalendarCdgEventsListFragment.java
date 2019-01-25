package org.varonesoft.luke.calendarcdg;

import android.app.ListFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;

import org.varonesoft.luke.calendarcdg.calendar.utils.AndroidCalendar;
import org.varonesoft.luke.calendarcdg.calendar.utils.Utils;
import org.varonesoft.luke.calendarcdg.calendar.utils.list.Event;
import org.varonesoft.luke.calendarcdg.calendar.utils.list.ListItem;
import org.varonesoft.luke.calendarcdg.calendar.utils.list.ListSeparator;
import org.varonesoft.luke.calendarcdg.calendarcdg.CalendarCdG;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnCalendarCdGEventsListFragmentInteractionListener}
 * interface.
 */
public class CalendarCdgEventsListFragment extends ListFragment {

    private static final String TAG = "CalendarCdgEventsListFragment";
    // argument names
    private static final String USE_24_HR = "use24hr";

    // Listener
    private OnCalendarCdGEventsListFragmentInteractionListener mListener;
    // Members
    private CalendarCdG calendarCdG;
    private Utils util;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public CalendarCdgEventsListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        util = new Utils(getActivity(), pref.getBoolean(USE_24_HR, false));

        calendarCdG = CalendarCdG.getInstance(getActivity().getApplicationContext());

        notifyUtilUpdated();
        Log.d(TAG, "onActivityCreated");
    }

    public void notifyCalendarCdGUpdates() {
        notifyUtilUpdated();
    }

    public void notifyUtilUpdated() {
        updateList();
    }

    /**
     * Updates the ListView with the events within the next NUM_DAYS_IN_LIST days
     */
    private void updateList() {
        if (util == null) {
            util = new Utils(getActivity(), true);
            Log.d(TAG, "D/updateList: util isNull");
        }
        try {
            util.setSelectedCalendars(util.getSelectedCalendarFromPref(null));
        } catch (NoSuchElementException e) {
            util.setSelectedCalendars(new HashSet<AndroidCalendar>());
        }
        Collection<Event> events;

        if (calendarCdG == null) {
            calendarCdG = CalendarCdG.getInstance(getActivity().getApplicationContext());
            Log.d(TAG, "D/updateList: calendarCdG isNull");
        }
        if ((util != null) && (calendarCdG != null)) {
            events = util.getCalendarData(
                    calendarCdG.getCursorDate().getYear(),
                    calendarCdG.getCursorDate().getMonthOfYear());
        } else {
            events = null;
        }

        setListAdapter(new CalendarCdGListEventAdapter(getActivity(),
                android.R.layout.simple_list_item_1, getListWithDateRows(events), util));

        if (util.getSelectedCalendars().isEmpty()) {
            setEmptyText("Nessun Calendario selezionato");
        } else {
            if (events == null || events.isEmpty()) {
                setEmptyText("Nessun dato");
            }
        }
    }

    /**
     * Adds Date strings in the list so that they can act as separators in the
     * ListView.
     *
     * @param events Collectio of events
     * @return A list of ListItems containting event data with separator
     * inserted
     */
    private List<ListItem> getListWithDateRows(Collection<Event> events) {

        List<String> dates = new ArrayList<>();
        LinkedList<ListItem> eventList = new LinkedList<>();

        // Force a cast
        LinkedList<Event> a = new LinkedList<>(events);
        //
        eventList.addAll(a);

        ListIterator<ListItem> iter = eventList.listIterator();
        while (iter.hasNext()) {
            ListItem e = iter.next();
            String dateString = Utils.getDateString((Event) e, "E, MMM d");

            if (!dates.contains(dateString)) {
                iter.previous();
                iter.add(new ListSeparator(dateString));
                dates.add(dateString);
            }
        }

        return eventList;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnCalendarCdGEventsListFragmentInteractionListener) {
            mListener = (OnCalendarCdGEventsListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnCalendarCdGEventsListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    interface OnCalendarCdGEventsListFragmentInteractionListener {
        void onCalendarCdGEventsListFragmentInteraction();
    }

    private class CalendarCdGListEventAdapter extends ArrayAdapter<ListItem> implements ListAdapter {
        private Utils util;

        CalendarCdGListEventAdapter(Context context, int textViewResourceId, List<ListItem> objects, Utils util) {
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
}
