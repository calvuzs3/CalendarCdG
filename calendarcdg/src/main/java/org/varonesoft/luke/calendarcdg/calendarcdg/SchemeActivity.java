package org.varonesoft.luke.calendarcdg.calendarcdg;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import org.joda.time.LocalDate;
import org.varonesoft.luke.calendarcdg.CalendarCdGPreferences;
import org.varonesoft.luke.calendarcdg.Costants;
import org.varonesoft.luke.calendarcdg.R;

import static org.varonesoft.luke.calendarcdg.CalendarCdGPreferences.KEY_SCHEME_START_DAY;
import static org.varonesoft.luke.calendarcdg.CalendarCdGPreferences.KEY_SCHEME_START_MONTH;
import static org.varonesoft.luke.calendarcdg.CalendarCdGPreferences.KEY_SCHEME_START_YEAR;
import static org.varonesoft.luke.calendarcdg.CalendarCdGPreferences.getSharedPreference;
import static org.varonesoft.luke.calendarcdg.CalendarCdGPreferences.setSharedPreference;

public class SchemeActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    // View
    TextView mDateView;
    DatePickerDialog mDatePickerDialog;
    // Context
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scheme);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Self reference
        mContext = this;
        mDateView = findViewById(R.id.tv_date_scheme);

        // Let's get the date'
        int year = CalendarCdGPreferences.getSharedPreference(mContext, KEY_SCHEME_START_YEAR, Costants.CalendarCdg.SCHEME_START_YEAR);
        int month = getSharedPreference(mContext, KEY_SCHEME_START_MONTH, Costants.CalendarCdg.SCHEME_START_MONTH);
        int day = getSharedPreference(mContext, KEY_SCHEME_START_DAY, Costants.CalendarCdg.SCHEME_START_DAY);
        LocalDate mLocalDate = new LocalDate(year, month, day);
        mDateView.setText(mLocalDate.toString("dd MMM yyyy"));

        // Fab
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDatePickerDialog.show();
            }
        });


        // Sets the picker
        mDatePickerDialog = new DatePickerDialog(mContext, SchemeActivity.this, year, month - 1, day);

    }

    @Override
    protected void onStop() {
        // destroy the self reference
        mContext = null;
        super.onStop();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

        LocalDate ld = new LocalDate(mDatePickerDialog.getDatePicker().getYear(),
                mDatePickerDialog.getDatePicker().getMonth() + 1,
                mDatePickerDialog.getDatePicker().getDayOfMonth());
        Snackbar.make(view, "Data attuale: " + ld.toString("dd MMM yyyy"), Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
        // Sets the new data as the beginning of the scheme
        setSharedPreference(mContext, KEY_SCHEME_START_DAY, ld.getDayOfMonth());
        setSharedPreference(mContext, KEY_SCHEME_START_MONTH, ld.getMonthOfYear());
        setSharedPreference(mContext, KEY_SCHEME_START_YEAR, ld.getYear());
        // Refresh calendarCdG
        CalendarCdG.getInstance(mContext).refresh(mContext);
        // Let's stop
        finish();
    }
}
