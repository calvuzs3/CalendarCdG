package org.varonesoft.luke.calendarcdg;

/**
 * Created by luke on 20/09/17.
 *
 * Wrapper class for Log
 *
 */

public final class Log {

    // Default TAG
    private final static String TAG = "CalendarCdG LOG";

    // Debug
    private final static boolean DEBUG = BuildConfig.DEBUG;

    // Wrap the debug an info to the warn to avoid art annoying messages
    private final static boolean DEBUG_TO_WARN = false;

    /* Verbose */
    public static void v(String tag, String msg){
        if (DEBUG)
            if (DEBUG_TO_WARN)
                android.util.Log.w(tag, msg);
            else
                android.util.Log.v(tag, msg);
    }

    /* Debug */
    public static void d( String msg) {
        if (DEBUG)
            d( TAG, msg);
    }

    /* Debug Overloading */
    public static void d( String tag, String msg) {
        if (DEBUG)
            if (DEBUG_TO_WARN)
                android.util.Log.w(tag, msg);
            else
                android.util.Log.d(tag, msg);
    }
//
//    /* Info */
//    public static void i(String tag, String msg){
//        if (DEBUG)
//            if (DEBUG_TO_WARN)
//                android.util.Log.w(tag, msg);
//            else
//            android.util.Log.i(tag, msg);
//    }
//
//    /* Warnings go to warnings */
//    public static void w(String tag, String msg) {
//        if (DEBUG)
//            android.util.Log.w(tag, msg);
//    }

    /* Errors must go to errors */
    public static void e(String tag, String msg) {
        if (DEBUG)
            android.util.Log.e(tag, msg);
    }
}