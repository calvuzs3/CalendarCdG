package org.varonesoft.luke.calendarcdg;

import android.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import org.varonesoft.luke.calendarcdg.calendarcdg.CalendarCdG;
import org.varonesoft.luke.calendarcdg.calendarcdg.CalendarCdGListAdapter;


/**
 * Fragment for the list of days
 */
public class CalendarCdgDaysListFragment extends ListFragment {

    private static final String TAG = "CalendarCdGDaysListFragment";

    private OnCalendarCdGDaysListFragmentInteractionListener mListener = null;

    private CalendarCdG mCalendarCdG = null;
    private CalendarCdGListAdapter mAdapter = null;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public CalendarCdgDaysListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
        }
    }

    //
//    public static CalendarCdgDaysListFragment newInstance(int columnCount) {
//        CalendarCdgDaysListFragment fragment = new CalendarCdgDaysListFragment();
//        Bundle args = new Bundle();
//        args.putInt(ARG_COLUMN_COUNT, columnCount);
//        fragment.setArguments(args);
//        return fragment;
//    }

    @Override
    public void onStart() {
        super.onStart();

        // Read Preferences
        mCalendarCdG.setSharedPreferences(getActivity());
        if (mCalendarCdG.isRefresh()) {
            mCalendarCdG.setRefresh(false);
            mAdapter.swapDays(mCalendarCdG.getMonth().getDaysList());
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "D/onActivityCreated");

        mCalendarCdG = CalendarCdG.getInstance(getActivity().getApplicationContext());
        mAdapter = new CalendarCdGListAdapter(getActivity(), mCalendarCdG.getMonth().getDaysList());

        setListAdapter(mAdapter);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnCalendarCdGDaysListFragmentInteractionListener) {
            mListener = (OnCalendarCdGDaysListFragmentInteractionListener) context;
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

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        // Callback
        Log.d(TAG, "mListener.onCalendarCdGDaysListFragmentInteraction");
        if (mListener != null)
            mListener.onCalendarCdGDaysListFragmentInteraction(id);
    }

    public void notifyCalendarCdGUpdates() {
        updateList();
        Log.d(TAG, "notifyCalendarCdGUpdates");
    }

    /* Updates the ListView with the days */
    protected void updateList() {
        if ((mAdapter != null) && (mCalendarCdG != null)) {
            mAdapter.swapDays(mCalendarCdG.getMonth().getDaysList());
            mAdapter.notifyDataSetChanged();
        }
        Log.d(TAG, "D/updateList");
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    interface OnCalendarCdGDaysListFragmentInteractionListener {
        // Call activity
        void onCalendarCdGDaysListFragmentInteraction(long id);
    }
}
