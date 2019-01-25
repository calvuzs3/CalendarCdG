package org.varonesoft.luke.calendarcdg.calendarcdg.models;

/**
 * Created by luke on 28/12/18.
 */

public final class Stop {
    public int year;
    public int month;
    public int day;
    public int shift;
    public int endyear;
    public int endmonth;
    public int endday;
    private int endshift;

    public Stop(int year, int month, int day, int shift, int endyear, int endmonth, int endday, int endshift) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.shift = shift;
        this.endyear = endyear;
        this.endmonth = endmonth;
        this.endday = endday;
        this.endshift = endshift;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("Stop{");
        sb.append(day +"/"+ month +"/"+ year + "("+shift+")");
        sb.append(" - ");
        sb.append(endday +"/"+ endmonth +"/"+ endyear + "("+endshift+")");
        sb.append("}");
        return sb.toString();
    }
}
