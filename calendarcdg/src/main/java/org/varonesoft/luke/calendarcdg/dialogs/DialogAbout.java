package org.varonesoft.luke.calendarcdg.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;

import org.varonesoft.luke.calendarcdg.R;


/**
 * Semplice finestrella per le info app
 *
 * Created by luke on 16/09/17.
 */

public class DialogAbout extends DialogFragment {

    public static void showDialog(final FragmentManager fm) {
        DialogAbout d = new DialogAbout();
        d.show(fm, "about");
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return new AlertDialog.Builder(getActivity()).setView(R.layout
                    .dialog_about).create();
        }
        // Try v7
        return new android.support.v7.app.AlertDialog.Builder(getActivity()).setView(R.layout
                .dialog_about).create();
    }
}