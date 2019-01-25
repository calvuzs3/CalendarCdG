package org.varonesoft.luke.calendarcdg;

import android.app.Application;

/**
 * Ensure some default preferences for all activities
 * <p>
 * Created by luke on 04/01/19.
 */

public class CalendarCdGApplication extends Application {

    private final static String TAG = CalendarCdGApplication.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();

        // Sets default preferences
        CalendarCdGPreferences.setDefaultValues(this);

        // Version number for upcoming changes
        CalendarCdGPreferences.setSharedPreference(this,
                CalendarCdGPreferences.KEY_VERSION_CODENAME,
                Library.getVersionCode(this));

        Log.d(TAG, "onCreate");
    }
}
