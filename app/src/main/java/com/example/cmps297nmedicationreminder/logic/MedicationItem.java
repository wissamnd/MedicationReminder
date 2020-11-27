package com.example.cmps297nmedicationreminder.logic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MedicationItem implements Comparable<MedicationItem>,Serializable {
    public String name;
    public MedicationType type;
    public int strength;
    public int numberOfPills;
    public Instruction instruction = Instruction.NOT_SPECIFIED;
    public String additionalInstructions = "";
    public ArrayList<Date> history = new ArrayList<>();
    public boolean isSatisfied = false;


    public MedicationItem(String name, MedicationType type, int strength, int numberOfPills, Instruction instruction) {
        this.name = name;
        this.type = type;
        this.strength = strength;
        this.numberOfPills = numberOfPills;
        this.instruction = instruction;
    }

    public static Instruction typeOfInstruction(String text){
        if (text.equals("Before Eating")){
            return Instruction.BEFORE_EATING;
        }else if(text.equals("While Eating")){
            return Instruction.WHILE_EATING;
        }else if(text.equals("After Eating")){
            return Instruction.AFTER_EATING;
        }else {
            return Instruction.NOT_SPECIFIED;
        }

    }

    public String getMedicationType(){
        switch (type){
            case PILL:
                return "Pill";
        }
        return "";
    }
    public void checkIfSatisfied(int year, int month, int dateOfTheMonth){
        isSatisfied = false;
        for (int i = 0; i < history.size(); i++) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(history.get(i));
            int yearOther = calendar.get(Calendar.YEAR);
            int monthOther = calendar.get(Calendar.MONTH);
            int dayOfTheMonthOther = calendar.get(Calendar.DAY_OF_MONTH);
            if (year == yearOther && month == monthOther && dateOfTheMonth == dayOfTheMonthOther) {
                isSatisfied = true;
                break;
            }
        }

    }

    public String getInstruction() {
        switch (instruction){
            case AFTER_EATING:
                return "After Eating";
            case WHILE_EATING:
                return "While Eating";
            case BEFORE_EATING:
                return "Before Eating";

            case NOT_SPECIFIED:
                return "";
        }
        return "";

    }


    @Override
    public int compareTo(MedicationItem o) {

        return name.compareTo(name);
    }
}

