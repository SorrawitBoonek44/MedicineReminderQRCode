package com.example.medicinereminderqrcode.medicine;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class MedicineReminder {
    String medicineName;
    double portion;
    String time;

    public MedicineReminder() {
    }

    public MedicineReminder(String medicineName, double portion, String time) {
        this.medicineName = medicineName;
        this.portion = portion;
        this.time = time;
    }

    public String getMedicineName() {
        return medicineName;
    }

    public double getPortion() {
        return portion;
    }

    public String getTime() {
        return time;
    }

    public long getTimeInMillis(){
        return getCalender().getTimeInMillis();
    }

    public String getTime(String format){
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleFormatter = new SimpleDateFormat(format);
        return simpleFormatter.format(getCalender().getTime());
    }

    private Calendar getCalender(){
        String[] time = getTime().split(":");
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time[0]));
        calendar.set(Calendar.MINUTE, Integer.parseInt(time[1]));
        calendar.set(Calendar.SECOND,0);
        return calendar;
    }


    public Boolean checkOutOfTime(){
        Calendar calendar = GregorianCalendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleFormatter = new SimpleDateFormat("HH:mm");
        String cTime = simpleFormatter.format(calendar.getTime());
        //Log.i("xxx", "checkOutOfTime: "+cTime);
        return getTime("HH:mm").compareTo(cTime) <= 0;
    }

    @SuppressLint("SimpleDateFormat")
    public Boolean checkIsCurrentTime(){
        return getTime().compareTo(new SimpleDateFormat("HH:mm").format(Calendar.getInstance().getTime())) == 0;
    }

    @NonNull
    @SuppressLint("DefaultLocale")
    @Override
    public String toString() {
        if(portion % 1 == 0){
            return String.format("take \"%s\" %.0f portion",getMedicineName(),getPortion());
        }
        return String.format("take \"%s\" %.1f portion",getMedicineName(),getPortion());
    }
}
