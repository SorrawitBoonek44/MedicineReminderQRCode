package com.example.medicinereminderqrcode.medicine;

import android.annotation.SuppressLint;

import java.util.ArrayList;
import java.util.Comparator;


public class MedicineReminderList {

    private final ArrayList<MedicineReminder> medicineReminderArrayList;

    public MedicineReminderList() {
        medicineReminderArrayList = new ArrayList<>();
    }

    public ArrayList<MedicineReminder> getMedicineReminderArrayList() {
        return new ArrayList<>(medicineReminderArrayList);
    }

    public ArrayList<MedicineReminder> getMedicineReminderArrayListSortByTime() {
        sortBytime();
        return getMedicineReminderArrayList();
    }

    public ArrayList<MedicineReminder> getMedicineReminderArrayListSortByNotOutOfTime(){
        ArrayList<MedicineReminder> notOutOfTimeList = new ArrayList<>();
        ArrayList<MedicineReminder> medicineReminders = getMedicineReminderArrayListSortByTime();
        for(MedicineReminder medicineReminder:medicineReminders){
            if(medicineReminder.checkIsCurrentTime()){
                notOutOfTimeList.add(medicineReminder);
            }
        }
        notOutOfTimeList.addAll(getNotOutOfTimeList());
        for (MedicineReminder medicineReminder: notOutOfTimeList){
            medicineReminders.remove(medicineReminder);
        }
        if(medicineReminders.size() == 0){
            return notOutOfTimeList;
        }
        notOutOfTimeList.addAll(medicineReminders);
        return notOutOfTimeList;
    }

    public void add(MedicineReminder medicineReminder){
        medicineReminderArrayList.add(medicineReminder);
    }

    @SuppressLint("SimpleDateFormat")
    public void sortBytime(){
        medicineReminderArrayList.sort(Comparator.comparing(MedicineReminder::getTime));
    }

    public ArrayList<MedicineReminder> getNotOutOfTimeList(){
        sortBytime();
        ArrayList<MedicineReminder> notOutOfTimeList = new ArrayList<>(medicineReminderArrayList);
        notOutOfTimeList.removeIf(MedicineReminder::checkOutOfTime);
        return notOutOfTimeList;
    }



    public int size(){
        return medicineReminderArrayList.size();
    }

}
