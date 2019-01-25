package org.varonesoft.luke.calendarcdg.calendar;

import android.Manifest;
import android.app.Fragment;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.varonesoft.luke.calendarcdg.Log;
import org.varonesoft.luke.calendarcdg.R;
import org.varonesoft.luke.calendarcdg.calendar.utils.AndroidCalendar;
import org.varonesoft.luke.calendarcdg.calendar.utils.PermissionsUtils;
import org.varonesoft.luke.calendarcdg.calendar.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import static org.varonesoft.luke.calendarcdg.Costants.CalendarCdg.MY_PERMISSIONS_REQUEST_READ_CALENDAR;

/**
 * A placeholder fragment containing a simple view.
 */
public class CalendarsFragment extends Fragment {

    private static final String TAG = "CalendarsFragment";
    Utils mUtils = null;
    ArrayList<AndroidCalendar> mAvailableCalendars = new ArrayList<>();
    List<AndroidCalendar> mSelectedCalendars;
    ArrayAdapter<AndroidCalendar> mAdapter;
    ListView mListViewCalendars;

    /* Empty Constructor */
    public CalendarsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_calendars_calendarslist_list, container, false);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.v(TAG, "V/onPause()");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.v(TAG, "V/onResume()");
        setCalendars();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CALENDAR: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                // return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
        Log.d(TAG,"onRequestPermissionsResult");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Log.v(TAG, "V/onActivityCreated()");

        PermissionsUtils.checkPermission(
                getActivity().getApplicationContext(),
                Manifest.permission.READ_CALENDAR,
                new PermissionsUtils.PermissionAskListener() {
                    @Override
                    public void onNeedPermission() {
                        ActivityCompat.requestPermissions(
                                getActivity(),
                                new String[]{Manifest.permission.READ_CALENDAR},
                                MY_PERMISSIONS_REQUEST_READ_CALENDAR
                        );
                    }

                    @Override
                    public void onPermissionPreviouslyDenied() {
                        //show a dialog explaining permission and then request permission
                        ActivityCompat.requestPermissions(
                                getActivity(),
                                new String[]{Manifest.permission.READ_CALENDAR},
                                MY_PERMISSIONS_REQUEST_READ_CALENDAR
                        );
                    }

                    @Override
                    public void onPermissionDisabled() {
                        Snackbar.make(getActivity().findViewById(R.id.fragment),
                                R.string.str_permissions_denied, Snackbar.LENGTH_LONG);
                        Toast.makeText(getActivity().getApplicationContext(), R.string.str_permissions_denied,
                                Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onPermissionGranted() {
                        // Eureka
                        Log.d(TAG,"onPermissionGranted");
                    }
                },
                getActivity()
        );
    }

    private void setCalendars() {
        Log.d(TAG, "setCalendars");

//        if (mUtils == null)
            mUtils = new Utils(getActivity(), true);

        try {
            mUtils.setSelectedCalendars(mUtils.getSelectedCalendarFromPref(null));
        } catch (NoSuchElementException e) {
            Log.d(TAG, "setCalendars: " + e.getMessage());
            mUtils.setSelectedCalendars(new HashSet<AndroidCalendar>());
        }
        mSelectedCalendars = new ArrayList<>(mUtils.getSelectedCalendars());

        mAvailableCalendars = new ArrayList<>(mUtils.getAvailableCalendars());
        Collections.sort(mAvailableCalendars);

        mAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_multiple_choice, mAvailableCalendars);

        mListViewCalendars = getActivity().findViewById(R.id.list_calendars);

        mListViewCalendars.setAdapter(mAdapter);

        for (AndroidCalendar cal : mAvailableCalendars) {
            mListViewCalendars.setItemChecked(mAvailableCalendars.indexOf(cal), mSelectedCalendars.contains(cal));
        }

        mListViewCalendars.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                Log.v(TAG, "onItemClick[id=" + id + "]");

                Set<AndroidCalendar> selectedCalendars = mUtils.getSelectedCalendars();
                if (selectedCalendars.contains(mAvailableCalendars.get(position)))
                    //Remove
                    selectedCalendars.remove(mAvailableCalendars.get(position));
                else
                    // Add
                    selectedCalendars.add(mAvailableCalendars.get(position));

                mUtils.setSelectedCalendars(selectedCalendars);
                mUtils.saveSelectedCalendarsPref(null);

                // TODO informare l'activity del cambio pref, cosi' aggiorna l'altro fragment se c'e'

                mSelectedCalendars = new ArrayList<>(mUtils.getSelectedCalendars());
            }
        });
    }
}
