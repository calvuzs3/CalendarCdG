package org.varonesoft.luke.calendarcdg;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import org.varonesoft.luke.calendarcdg.calendar.CalendarsActivity;
import org.varonesoft.luke.calendarcdg.calendarcdg.CalendarCdG;
import org.varonesoft.luke.calendarcdg.dialogs.DialogAbout;
import org.varonesoft.luke.calendarcdg.preferences.SettingsActivity;

import static org.varonesoft.luke.calendarcdg.CalendarCdGPreferences.KEY_SHOW_CALENDARS;
import static org.varonesoft.luke.calendarcdg.CalendarCdGPreferences.KEY_SHOW_STOPS;

public class CalendarCdgActivity extends AppCompatActivity implements
        CalendarCdgDaysListFragment.OnCalendarCdGDaysListFragmentInteractionListener,
        CalendarCdgEventsListFragment.OnCalendarCdGEventsListFragmentInteractionListener,
        NavigationView.OnNavigationItemSelectedListener {

    /* Whether or not the activity is in two-pane mode     */
    private boolean mTwoPane = false;

    /* Private members     */
    private CalendarCdgDaysListFragment calendarCdGDaysListFragment = null;
    private CalendarCdgEventsListFragment calendarCdgEventsListFragment = null;
    private CalendarCdG calendarCdG = null;

    /* Methods     */
    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PreferenceManager.setDefaultValues(this, R.xml.pref_general, false);

        // Init the variables
        calendarCdG = CalendarCdG.getInstance(getApplicationContext());
        calendarCdG.setShowCalendars(
                CalendarCdGPreferences.getSharedPreference(getApplicationContext(), KEY_SHOW_CALENDARS, false));
        calendarCdG.setShowStops(
                CalendarCdGPreferences.getSharedPreference(getApplicationContext(), KEY_SHOW_STOPS, false));
        // workaround
        calendarCdG.refresh(getApplicationContext());

        // Layout
        setContentView(R.layout.activity_calendarcdg);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Drawer
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Check TwoPaneMode
        FragmentManager fm = getFragmentManager();
        calendarCdGDaysListFragment = (CalendarCdgDaysListFragment) fm.findFragmentById(R.id.fragment_calendarcdg_dayslist);

        try {
            calendarCdgEventsListFragment = (CalendarCdgEventsListFragment) fm.findFragmentById(R.id.fragment_calendarcdg_eventslist);
        } catch (ClassCastException e) {
            calendarCdgEventsListFragment = null;
        }

        // Controlla se il Fragment non e' nullo e se si trova in Layout
        // altrimenti crasha poiche' dopo aver ruotato esiste ed e' ancora
        // indicizzato , pertanto il controllo e' inevitabile'
        mTwoPane = (calendarCdgEventsListFragment != null) && (calendarCdgEventsListFragment.isInLayout());


        ImageButton btnLeft = findViewById(R.id.image_button_left);
        btnLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendarCdG.moveBackward(getApplicationContext());
                notifyUpdates();

            }
        });
        ImageButton btnRight = findViewById(R.id.image_button_right);
        assert btnRight != null;
        btnRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendarCdG.moveForward(getApplicationContext());
                notifyUpdates();

            }
        });

        notifyUpdates();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent i;
        switch (item.getItemId()) {

            case R.id.menu_calendarcdg_settings:
                i = new Intent(CalendarCdgActivity.this, SettingsActivity.class);
                startActivity(i);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Toolbar tb = findViewById(R.id.toolbar);
        tb.inflateMenu(R.menu.menu_calendarcdg);
        tb.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return onOptionsItemSelected(item);
            }
        });
        return true;
    }

    /**
     * Notify the calendar to move
     * notify the fragment to update
     */
    private void notifyUpdates() {
        TextView v = findViewById(R.id.textMese);
        v.setText(calendarCdG.getCursorDate().toString("MMM yyyy"));
        if (calendarCdGDaysListFragment != null) {
            calendarCdGDaysListFragment.notifyCalendarCdGUpdates();
        }
        if (mTwoPane && (calendarCdgEventsListFragment != null)) {
            calendarCdgEventsListFragment.notifyCalendarCdGUpdates();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /* (non-Javadoc)
    * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
    */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 10000 && resultCode != RESULT_CANCELED) {
            if (calendarCdGDaysListFragment != null) {
                calendarCdGDaysListFragment.notifyCalendarCdGUpdates();
            }
        }
    }

    /**
     * Interaction with DaysList
     * <p>
     * In future develp:
     * thi function chain to event on calendar
     * it create events or modifies them
     *
     * @param id event_id
     */
    @Override
    public void onCalendarCdGDaysListFragmentInteraction(long id) {
        // nothing now
    }

    /**
     * Interaction with Events list
     */
    @Override
    public void onCalendarCdGEventsListFragmentInteraction() {
        // nothing now
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        Intent i = null;
        int id = item.getItemId();

        if (id == R.id.nav_calendars) {
            i = new Intent(CalendarCdgActivity.this, CalendarsActivity.class);

        } else if (id == R.id.nav_settings) {
            i = new Intent(CalendarCdgActivity.this, SettingsActivity.class);

        } else if (id == R.id.nav_about) {
            DialogAbout.showDialog(getFragmentManager());

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        if (i != null) startActivity(i);
        return true;
    }
}
