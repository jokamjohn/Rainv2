package me.johnkagga.rainv2.weather;

/**
 * Created by John Kagga on 6/17/2015.
 */
public class Forecast {
    private Current mCurrent;
    private  Hour [] mHours;
    private Day [] mDays;

    public Current getCurrent() {
        return mCurrent;
    }

    public void setCurrent(Current current) {
        mCurrent = current;
    }

    public Hour[] getHours() {
        return mHours;
    }

    public void setHours(Hour[] hours) {
        mHours = hours;
    }

    public Day[] getDays() {
        return mDays;
    }

    public void setDays(Day[] days) {
        mDays = days;
    }
}
