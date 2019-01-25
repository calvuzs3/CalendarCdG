package org.varonesoft.luke.calendarcdg.calendarcdg;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.varonesoft.luke.calendarcdg.R;
import org.varonesoft.luke.calendarcdg.calendarcdg.models.Day;
import org.varonesoft.luke.calendarcdg.calendarcdg.models.HalfTeam;
import org.varonesoft.luke.calendarcdg.calendarcdg.models.Shift;

import java.util.ArrayList;

import static org.varonesoft.luke.calendarcdg.Costants.CalendarCdg.CAL_SUNDAY;

/**
 * Adapter for DaysList
 * <p>
 * Created by luke on 24/09/17.
 */

@SuppressWarnings("deprecation")
public class CalendarCdGListAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<Day> mDaysList;
    private LayoutInflater mInflater;


    public CalendarCdGListAdapter(Context context, ArrayList<Day> daysList) {

        mContext = context;
        mDaysList = daysList;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    /**
     * Sets the array reference for data to display
     *
     * @param daysList new list reference
     */
    public void swapDays(ArrayList<Day> daysList) {
        mDaysList = daysList;
    }

    @Override
    public int getCount() {
        return mDaysList.size();
    }

    @Override
    public Object getItem(int position) {
        return mDaysList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        Resources r = mContext.getResources();

        View v = convertView;
        if (v == null) {
            v = mInflater.inflate(R.layout.fragment_calendarcdg_dayslist_row, null);
        }

        Day day = (Day) getItem(position);

        if (day != null) {
            TextView tday = v.findViewById(R.id.tday);
            TextView twday = v.findViewById(R.id.twday);
            TextView t1 = v.findViewById(R.id.tt1);
            TextView t2 = v.findViewById(R.id.tt2);
            TextView t3 = v.findViewById(R.id.tt3);
            TextView tR = v.findViewById(R.id.ttR);
            TextView tE = v.findViewById(R.id.ttE);

            assert tday != null;
            assert twday != null;
            assert t1 != null;
            assert t2 != null;
            assert t3 != null;
            assert tR != null;
            assert tE != null;

            tday.setText(r.getString(R.string.str_scheme_num, day.getDayOfMonth()));
            twday.setText(r.getString(R.string.str_scheme, day.getDayOfWeekAsString()));
            t1.setText(r.getString(R.string.str_scheme, day.getTeamsAsString(0)));
            t2.setText(r.getString(R.string.str_scheme, day.getTeamsAsString(1)));
            t3.setText(r.getString(R.string.str_scheme, day.getTeamsAsString(2)));
            tR.setText(r.getString(R.string.str_scheme, day.getOffWorkHalfTeamsAsString()));
            tE.setText(day.hasEvents() ? "*" : "  ");

            // RED color for Sunday
            if (day.getDayOfWeek() == CAL_SUNDAY) {
                tday.setTextColor(mContext.getResources().getColor(R.color.colorTextRed));
                twday.setTextColor(mContext.getResources().getColor(R.color.colorTextRed));
            } else {
                tday.setTextColor(mContext.getResources().getColor(R.color.colorTextBlack));
                twday.setTextColor(mContext.getResources().getColor(R.color.colorTextBlack));
            }

            // Get the user halteam
            HalfTeam uh = CalendarCdG.getInstance(mContext).getUserHalfTeam();

            // and position
            int userposition = -1;
            if (uh != null) {
                userposition = day.getInWichTeamIsHalfTeam(uh);
            }

            if (userposition >= 0) {
                switch (userposition) {
                    case 0:
                        t1.setBackgroundColor(mContext.getResources().getColor(R.color.colorBackgroundBlue));
                        t2.setBackgroundColor(0);
                        t3.setBackgroundColor(0);
                        break;
                    case 1:
                        t2.setBackgroundColor(mContext.getResources().getColor(R.color.colorBackgroundBlue));
                        t1.setBackgroundColor(0);
                        t3.setBackgroundColor(0);
                        break;
                    case 2:
                        t3.setBackgroundColor(mContext.getResources().getColor(R.color.colorBackgroundBlue));
                        t1.setBackgroundColor(0);
                        t2.setBackgroundColor(0);
                        break;
                }
            } else {
                t1.setBackgroundColor(0);
                t2.setBackgroundColor(0);
                t3.setBackgroundColor(0);
            }


            // Recupero i turni
            ArrayList<Shift> shifts = day.getShifts();
            // Verifico la fermata impianti
            int counter = 0;
            for (Shift s : shifts) {
                if (s.isStop()) {
                    switch (counter) {
                        case 0:
                            t1.setBackgroundColor(mContext.getResources().getColor(R.color.colorBackgroundRed));
                            break;
                        case 1:
                            t2.setBackgroundColor(mContext.getResources().getColor(R.color.colorBackgroundRed));
                            break;
                        case 2:
                            t3.setBackgroundColor(mContext.getResources().getColor(R.color.colorBackgroundRed));
                            break;
                    }
                }
                counter++;
            }

            // Set a background color for today
            if (!day.getIsToday()) v.setBackgroundColor(0);
            else
                v.setBackgroundColor(mContext.getResources().getColor(R.color.colorBackgroundYellow));
        }

        return v;
    }
}