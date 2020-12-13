package com.example.cmps297nmedicationreminder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.cmps297nmedicationreminder.logic.DailyMedicationItem;
import com.example.cmps297nmedicationreminder.logic.Helper;
import com.example.cmps297nmedicationreminder.logic.LocalStorage;
import com.example.cmps297nmedicationreminder.logic.MedicationItem;
import com.example.cmps297nmedicationreminder.logic.OnceMedicationItem;
import com.example.cmps297nmedicationreminder.ui.medication.MedicationsFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class MedicationDetailsActivity extends AppCompatActivity {

    //TextViews and Button initialization

    TextView medicationType, medicationInstructions, medicationStrength,medicationRemindersText, medicationInstructionHeader;
    Button editButton, deleteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medication_details);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final MedicationItem medicationItem = (MedicationItem) getIntent().getSerializableExtra(MedicationsFragment.EXTRA_MEDICATION_ITEM);
        getSupportActionBar().setTitle(medicationItem.name);

        //Loading Views Content

        medicationType = findViewById(R.id.medication_item_detail_type);
        medicationType.setText(medicationItem.getMedicationType());

        medicationStrength = findViewById(R.id.medication_item_detail_strength);
        medicationStrength.setText(medicationItem.strength +" mg");

        medicationRemindersText = findViewById(R.id.reminders_text);



        //Once Medication View

        if(medicationItem instanceof OnceMedicationItem){
            OnceMedicationItem onceMedicationItem = (OnceMedicationItem) medicationItem;
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM d, yy h:mm a");
            String text = "Once\n";
            text+= "Take "+ onceMedicationItem.numberOfPills +" Pill(s)";
            text+= " on "+ dateFormat.format(onceMedicationItem.date);


            medicationRemindersText.setText(text);


            //Daily Medication View

        }else if (medicationItem instanceof  DailyMedicationItem){
            DailyMedicationItem  dailyMedicationItem= (DailyMedicationItem) medicationItem;
            String text = "Every Day\n";
            for (int i = 0; i < LocalStorage.MEDICATION_ITEMS.size();i++){
                MedicationItem m = LocalStorage.MEDICATION_ITEMS.get(i);
                if(m instanceof DailyMedicationItem){
                    DailyMedicationItem d = (DailyMedicationItem) m;
                    if(d.equals(dailyMedicationItem)){
                        SimpleDateFormat dateFormat = new SimpleDateFormat("h:mm a");
                        text+= "Take "+ m.numberOfPills +" Pill(s)";
                        text+= " at "+ dateFormat.format(Helper.getDate(d.hour,d.minutes));
                        text+= "\n";
                    }
                }

            }
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM d, yy");
            text+= "Starting From "+ dateFormat.format(dailyMedicationItem.startDate);
            if(dailyMedicationItem.endDate != null){
                text+= "\nEnding By "+ dateFormat.format(dailyMedicationItem.endDate);
            }
            medicationRemindersText.setText(text);
        }

        //Medication Instruction logic

        medicationInstructionHeader = findViewById(R.id.instructions_header);
        medicationInstructions = findViewById(R.id.instructions_text);
        if(medicationItem.getInstruction().length() >0){
            String text = "Take "+medicationItem.getInstruction();
            if(medicationItem.additionalInstructions.length() > 0){
                text += "\n"+medicationItem.additionalInstructions;
            }
            medicationInstructions.setText(text);

        }else {
            medicationInstructionHeader.setVisibility(View.GONE);
            medicationInstructions.setVisibility(View.GONE);
        }

        //Delete Button View
        deleteButton = findViewById(R.id.delete_medication_button);

        //Delete Button Event Listener
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ArrayList<MedicationItem> temp = new ArrayList<>();
                temp.addAll(LocalStorage.MEDICATION_ITEMS);
                for (int i = 0 ; i < temp.size(); i++){
                    MedicationItem m = temp.get(i);
                    if (m.equals(medicationItem)){
                        LocalStorage.removeMedicationItem(medicationItem);
                    }
                }

                Intent replyIntent  = new Intent();
                setResult(RESULT_OK,replyIntent);


                finish();



            }
        });

    }



    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}