package org.varonesoft.luke.calendarcdg.calendar.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;

import static org.varonesoft.luke.calendarcdg.Library.getSharedPreferences;

/**
 * Manage Permissions Utilities coupled with calendars
 * <p>
 * Created by luke on 07/01/19.
 */

public class PermissionsUtils {
    /*
    * Check if version is marshmallow and above.
    * Used in deciding to ask runtime permission
    */
    public static boolean shouldAskPermission() {
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M);
    }

    private static boolean shouldAskPermission(Context context, String permission) {
        if (shouldAskPermission()) {
            int permissionResult = ActivityCompat.checkSelfPermission(context, permission);
            return permissionResult != PackageManager.PERMISSION_GRANTED;
        }
        return false;
    }

    public static void checkPermission(Context context, String permission, PermissionAskListener listener, Activity activity) {
/*
        * If permission is not granted
        * */
        if (shouldAskPermission(context, permission)) {
/*
            * If permission denied previously
            * */
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                listener.onPermissionPreviouslyDenied();
            } else {
                /*
                * Permission denied or first time requested
                * */
                if (isFirstTimeAskingPermission(context, permission)) {
                    firstTimeAskingPermission(context, permission, false);
                    listener.onNeedPermission();
                } else {
                    /*
                    * Handle the feature without permission or ask user to manually allow permission
                    * */
                    listener.onPermissionDisabled();
                }
            }
        } else {
            listener.onPermissionGranted();
        }
    }

    public static void firstTimeAskingPermission(Context context, String permission, boolean isFirstTime) {
        SharedPreferences preferences = getSharedPreferences(context);
        preferences.edit().putBoolean(permission, isFirstTime).apply();
    }

    public static boolean isFirstTimeAskingPermission(Context context, String permission) {
        SharedPreferences preferences = getSharedPreferences(context);
        return preferences.getBoolean(permission, true);
    }

    /*
    * Callback on various cases on checking permission
    *
    * 1.  Below M, runtime permission not needed. In that case onPermissionGranted() would be called.
    *     If permission is already granted, onPermissionGranted() would be called.
    *
    * 2.  Above M, if the permission is being asked first time onNeedPermission() would be called.
    *
    * 3.  Above M, if the permission is previously asked but not granted, onPermissionPreviouslyDenied()
    *     would be called.
    *
    * 4.  Above M, if the permission is disabled by device policy or the user checked "Never ask again"
    *     check box on previous request permission, onPermissionDisabled() would be called.
    * */
    public interface PermissionAskListener {
        /*
                * Callback to ask permission
                * */
        void onNeedPermission();

        /*
                * Callback on permission denied
                * */
        void onPermissionPreviouslyDenied();

        /*
                * Callback on permission "Never show again" checked and denied
                * */
        void onPermissionDisabled();

        /*
                * Callback on permission granted
                * */
        void onPermissionGranted();
    }
}
