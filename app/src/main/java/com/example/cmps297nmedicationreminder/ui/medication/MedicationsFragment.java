package com.example.cmps297nmedicationreminder.ui.medication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.cmps297nmedicationreminder.AddMedicationActivity;
import com.example.cmps297nmedicationreminder.MedicationDetailsActivity;
import com.example.cmps297nmedicationreminder.R;
import com.example.cmps297nmedicationreminder.logic.DailyMedicationItem;
import com.example.cmps297nmedicationreminder.logic.LocalStorage;
import com.example.cmps297nmedicationreminder.logic.MedicationItem;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MedicationsFragment extends Fragment {


    private ArrayList<MedicationItem> medicationsList = new ArrayList<>();

    public static final String EXTRA_MEDICATION_ITEM = "MEDICATION_ITEM";
    public static final Integer DELETE_REQUEST_CODE = 114;
    public static final Integer ADD_REQUEST_CODE = 111;

    public MedicationItemAdapter medicationItemAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        final View root = inflater.inflate(R.layout.fragment_medicatiions, container, false);
        retrieveMedications(); // retrieves medications from database
        ListView listView = root.findViewById(R.id.list_view_medications);
        medicationItemAdapter = new MedicationItemAdapter(medicationsList,getContext());
        listView.setAdapter(medicationItemAdapter);

        FloatingActionButton fab = root.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddMedicationActivity.class);
                startActivityForResult(intent, ADD_REQUEST_CODE);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), MedicationDetailsActivity.class);
                intent.putExtra(EXTRA_MEDICATION_ITEM,medicationsList.get(position));
                startActivityForResult(intent,DELETE_REQUEST_CODE);

            }
        });


        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == DELETE_REQUEST_CODE){
            if (resultCode == Activity.RESULT_OK){
                medicationsList.clear();
                medicationItemAdapter.clear();
                retrieveMedications();
            }
        }else if (requestCode == ADD_REQUEST_CODE){
            if (resultCode == Activity.RESULT_OK){
                medicationsList.clear();
                medicationItemAdapter.clear();
                retrieveMedications();
            }
        }

    }

    public void retrieveMedications(){
        for(int i = 0; i < LocalStorage.MEDICATION_ITEMS.size(); i++){
            MedicationItem medicationItem = LocalStorage.MEDICATION_ITEMS.get(i);
            if(medicationItem instanceof DailyMedicationItem) {
                DailyMedicationItem dailyMedicationItem = (DailyMedicationItem) medicationItem;
                if(!medicationsList.contains(dailyMedicationItem)){
                    medicationsList.add(medicationItem);
                }
            }else {
                medicationsList.add(medicationItem);
            }

        }
    }
}