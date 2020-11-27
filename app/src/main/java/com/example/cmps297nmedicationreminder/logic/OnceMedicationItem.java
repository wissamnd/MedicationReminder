package com.example.cmps297nmedicationreminder.logic;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

public class OnceMedicationItem extends MedicationItem implements Comparable<MedicationItem>, Serializable {
    public Date date;

    public OnceMedicationItem(String name, MedicationType type, int strength, int numberOfPills, Instruction instruction, Date date) {
        super(name, type, strength, numberOfPills,instruction);
        this.date = date;
        this.numberOfPills = numberOfPills;
    }

    public void takeMedication(){
        history.add(date);
    }

    public void undoTackingMedication() {history.remove(date);}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if(o instanceof  OnceMedicationItem){
            OnceMedicationItem that = (OnceMedicationItem) o;
            return this.name.equals(that.name);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return(date.toString()+numberOfPills+name+strength).hashCode();
    }

    @Override
    public int compareTo(MedicationItem o) {
        if(o instanceof  OnceMedicationItem){
            OnceMedicationItem other = (OnceMedicationItem) o;
            return date.compareTo(other.date);
        }else if(o instanceof DailyMedicationItem){
            DailyMedicationItem other = (DailyMedicationItem) o;
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            Integer hour = calendar.get(Calendar.HOUR_OF_DAY);
            return hour.compareTo(other.hour);
        }
        return name.compareTo(o.name);

    }
}
