package org.varonesoft.luke.calendarcdg.calendar;

import android.app.ListFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ListView;

import org.varonesoft.luke.calendarcdg.calendar.utils.AndroidCalendar;
import org.varonesoft.luke.calendarcdg.calendar.utils.Utils;
import org.varonesoft.luke.calendarcdg.calendar.utils.list.Event;
import org.varonesoft.luke.calendarcdg.calendar.utils.list.ListEventAdapter;
import org.varonesoft.luke.calendarcdg.calendar.utils.list.ListItem;
import org.varonesoft.luke.calendarcdg.calendar.utils.list.ListSeparator;

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
 * Activities containing this fragment MUST implement the {@link OnEventListFragmentInteractionListener}
 * interface.
 */
public class EventFragment extends ListFragment {

    // argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    private static final String USE_24_HR = "use24hr";
    private static final String NUM_DAYS = "num_days";
    private static final String APP_PREFS = "pref_app";
    private static final String ENABLE_CLICK_EVENT = "pref_enable_click_event";
    private static final String WIDGET_PREFS = "pref_widget";
    public static int NUM_DAYS_IN_LIST = 7;
    // Listener
    private OnEventListFragmentInteractionListener mListener;
    // Members
    private Utils util;
    private SharedPreferences pref;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public EventFragment() {
    }

    @SuppressWarnings("unused")
    public static EventFragment newInstance(int columnCount) {
        EventFragment fragment = new EventFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        util = new Utils(getActivity(), pref.getBoolean(USE_24_HR, false));

        notifyUtilUpdated();
    }

    /**
     * Updates the ListView with the events within the next NUM_DAYS_IN_LIST days
     */
    protected void updateList() {
        try {
            int triggeredWidgetId = -1;
            if (triggeredWidgetId == -1) {
                //				util.setSelectedCalendars(util.getSelectedCalendarFromPref(Constants.AgendaList.APP_PREFS));
                util.setSelectedCalendars(util.getSelectedCalendarFromPref(null));
            } else {
                // If we have a valid widget id (this activity was started by clicking on the widget),
                // set the calendars for this run to the calendars from that widget's preferences
                util.setSelectedCalendars(util.getSelectedCalendarFromPref(WIDGET_PREFS + triggeredWidgetId));
            }
        } catch (NoSuchElementException e) {
            util.setSelectedCalendars(new HashSet<AndroidCalendar>());
        }

        Collection<Event> events = util.getCalendarData(NUM_DAYS_IN_LIST, true);

        setListAdapter(new ListEventAdapter(getActivity(), android.R.layout.simple_list_item_1, getListWithDateRows(events), util));

        if (util.getSelectedCalendars().isEmpty()) {
            setEmptyText("Nessun Calendario selezionato");
        } else if (events.isEmpty()) {
            setEmptyText("Nessun dato");
        }
    }

    /**
     * Adds Date strings in the list so that they can act as separators in the
     * ListView.
     *
     * @param events events
     * @return A list of ListItems containting event data with separator
     * inserted
     */
    private List<ListItem> getListWithDateRows(Collection<Event> events) {
        LinkedList<ListItem> eventList = new LinkedList<ListItem>(events);
        List<String> dates = new ArrayList<>();

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
    public void onListItemClick(ListView l, View v, int position, long id) {
        SharedPreferences pref = getActivity().getSharedPreferences(APP_PREFS, 0);
        if (pref.getBoolean(ENABLE_CLICK_EVENT, true)) {
            Object obj = l.getItemAtPosition(position);
            Event ev = null;
            if (obj instanceof Event) {
                ev = (Event) obj;
            }
            /*try {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(CalendarContract.Events.CONTENT_URI + "/" + ev.getId()));
                // Who knows why you need to put the start and end times in the intent,
                // but for some reason you need to for the com.android.calendar app...
                intent.putExtra("beginTime", ev.getStart().getTimeInMillis());
                intent.putExtra("endTime", ev.getEnd().getTimeInMillis());

                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(getActivity(), "Unable to open event", Toast.LENGTH_SHORT).show();
            }*/
            mListener.onEventListFragmentInteraction(ev.getId());
        }
    }

    /**
     * The util variable might have been updated. We will update our state based on the state
     * of the preferences and then refresh the list
     */
    public void notifyUtilUpdated() {
        util.setUse24Hour(pref.getBoolean(USE_24_HR, true));
        try {
            util.setSelectedCalendars(util.getSelectedCalendarFromPref(null));
        } catch (NoSuchElementException e) {
            util.setSelectedCalendars(new HashSet<AndroidCalendar>());
        }

        try {
            NUM_DAYS_IN_LIST = Integer.parseInt(pref.getString(NUM_DAYS, "7"));
        } catch (NumberFormatException e) {
            NUM_DAYS_IN_LIST = 7;
        }

        updateList();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnEventListFragmentInteractionListener) {
            mListener = (OnEventListFragmentInteractionListener) context;
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
    interface OnEventListFragmentInteractionListener {
        void onEventListFragmentInteraction(int id);
    }
}
