package com.example.cmps297nmedicationreminder.ui.medication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.cmps297nmedicationreminder.logic.MedicationItem;
import com.example.cmps297nmedicationreminder.R;

import java.util.ArrayList;

public class MedicationItemAdapter extends ArrayAdapter<MedicationItem> {

    private Context context;
    private ArrayList<MedicationItem> medicationItems;
    private TextView medicationName, medicationInstructions;

    public MedicationItemAdapter(ArrayList<MedicationItem> medicationItems, Context context) {
        super(context, R.layout.medication_item_calendar, medicationItems);
        this.context = context;
        this.medicationItems = medicationItems;
    }

    @Override
    public int getCount() {
        return medicationItems.size();
    }

    @Nullable
    @Override
    public MedicationItem getItem(int position) {
        return medicationItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.medication_item, parent, false);

        MedicationItem medicationItem = medicationItems.get(position);

        medicationName = convertView.findViewById(R.id.medication_item_name);
        medicationName.setText(medicationItem.name);
        medicationInstructions = convertView.findViewById(R.id.medication_item_instructions);
        String instructionStatement = medicationItem.strength +"mg";
        String foodInstruction = medicationItem.getInstruction();
        if(foodInstruction.length() >0){
            instructionStatement+= ", "+foodInstruction;
        }
        medicationInstructions.setText(instructionStatement);


        return convertView;
    }
}
