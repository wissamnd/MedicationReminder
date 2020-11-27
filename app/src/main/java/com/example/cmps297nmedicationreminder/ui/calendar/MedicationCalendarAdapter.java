package com.example.cmps297nmedicationreminder.ui.calendar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.cmps297nmedicationreminder.logic.DailyMedicationItem;
import com.example.cmps297nmedicationreminder.logic.Helper;
import com.example.cmps297nmedicationreminder.logic.MedicationItem;
import com.example.cmps297nmedicationreminder.logic.OnceMedicationItem;
import com.example.cmps297nmedicationreminder.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class MedicationCalendarAdapter extends ArrayAdapter<MedicationItem> {

    private Context context;
    private ArrayList<MedicationItem> medicationItems;
    private TextView medicationTime, medicationName, medicationInstructions;
    private CheckBox medicationCheckbox;

    public MedicationCalendarAdapter(ArrayList<MedicationItem> medicationItems, Context context) {
        super(context, R.layout.medication_item_calendar, medicationItems);
        this.context = context;
        this.medicationItems = medicationItems;
    }
    @Override
    public int getCount() {
        return medicationItems.size();
    }

    @Override
    public MedicationItem getItem(int position) {
        return medicationItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.medication_item_calendar, parent, false);
        medicationTime = convertView.findViewById(R.id.medication_time);
        MedicationItem medicationItem = medicationItems.get(position);


        SimpleDateFormat dateFormat = new SimpleDateFormat("h:mm a");
        medicationName = convertView.findViewById(R.id.medication_name);
        medicationName.setText(medicationItem.name);

        if(medicationItem instanceof OnceMedicationItem){
            OnceMedicationItem onceMedicationItem = (OnceMedicationItem) medicationItem;

            medicationTime.setText(dateFormat.format(onceMedicationItem.date));




        }else if (medicationItem instanceof DailyMedicationItem){
            final DailyMedicationItem dailyMedicationItem = (DailyMedicationItem) medicationItem;

            medicationTime.setText(dateFormat.format(Helper.getDate(dailyMedicationItem.hour,dailyMedicationItem.minutes)));




        }

        medicationCheckbox = convertView.findViewById(R.id.medication_checkbox);
        medicationCheckbox.setChecked(medicationItem.isSatisfied);

        medicationInstructions = convertView.findViewById(R.id.medication_instructions);
        String instructionStatement = medicationItem.strength +"mg, Take " + medicationItem.numberOfPills +" pill";
        String foodInstruction = medicationItem.getInstruction();
        if(foodInstruction.length() >0){
            instructionStatement+= ", "+foodInstruction;
        }
        medicationInstructions.setText(instructionStatement);






        return convertView;
    }
}
