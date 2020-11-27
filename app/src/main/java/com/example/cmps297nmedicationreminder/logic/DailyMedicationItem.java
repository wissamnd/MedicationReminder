package com.example.cmps297nmedicationreminder.logic;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

public class DailyMedicationItem extends MedicationItem implements Comparable<MedicationItem>, Serializable {
    public Date startDate;
    public boolean continuousTreatment;
    public Date endDate;
    public int hour;
    public int minutes;



    public DailyMedicationItem(String name, MedicationType type, int strength, int numberOfPills, Instruction instruction, Date startDate, Date endDate, int hour, int minutes) {
        super(name, type, strength,numberOfPills,instruction);
        this.startDate = startDate;
        this.endDate = endDate;
        this.hour = hour;
        this.minutes = minutes;
        this.continuousTreatment = false;
    }
    public DailyMedicationItem(String name, MedicationType type, int strength,int numberOfPills,Instruction instruction,Date startDate, int hour, int minutes) {
        super(name, type, strength, numberOfPills, instruction);
        this.startDate = startDate;
        this.hour = hour;
        this.minutes = minutes;
        this.continuousTreatment = true;
    }

    public void takeMedication(int year, int month, int dayOfMonth){

        history.add(Helper.getDate(year,month,dayOfMonth,this.hour,this.minutes));
    }

    public void undoTackingMedication(int year, int month, int dayOfMonth) {
        for(int i= 0 ; i < history.size(); i++){
            Date date = history.get(i);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            if(calendar.get(Calendar.YEAR) == year && calendar.get(Calendar.MONTH) == month && calendar.get(Calendar.DAY_OF_MONTH)==dayOfMonth){
                history.remove(date);
            }


        }
        history.remove(Helper.getDate(year,month,dayOfMonth,this.hour,this.minutes));
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        if(o instanceof DailyMedicationItem){
            DailyMedicationItem that = (DailyMedicationItem) o;
            return this.name.equals(that.name);
        }else {
            return false;
        }

    }

    @Override
    public String toString() {
        return "DailyMedicationItem{" +
                "name='" + name + '\'' +
                ", type=" + type +
                ", strength=" + strength +
                ", numberOfPills=" + numberOfPills +
                ", instruction=" + instruction +
                ", additionalInstructions='" + additionalInstructions + '\'' +
                ", history=" + history +
                ", isSatisfied=" + isSatisfied +
                '}';
    }

    @Override
    public int hashCode() {
        return (startDate.toString()+continuousTreatment+endDate.toString()+name+strength).hashCode();
    }

    @Override
    public int compareTo(MedicationItem o) {
        if (o instanceof DailyMedicationItem){
            DailyMedicationItem other = (DailyMedicationItem) o;
            return startDate.compareTo(other.startDate);
        }else if(o instanceof OnceMedicationItem){
            OnceMedicationItem other = (OnceMedicationItem) o;
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(other.date);
            Integer hour = calendar.get(Calendar.HOUR_OF_DAY);
            Integer myHour = new Integer(this.hour);
            return myHour.compareTo(hour);
        }
        return name.compareTo(o.name);

    }
}
