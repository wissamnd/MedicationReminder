package com.example.cmps297nmedicationreminder.ui.medication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
    private MedicationItemAdapter medicationItemAdapter;
    public static final String EXTRA_MEDICATION_ITEM = "MEDICATION_ITEM";
    public static final Integer DELETE_REQUEST_CODE = 114;
    public static final Integer ADD_REQUEST_CODE = 111;
    private RecyclerView recyclerView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        final View root = inflater.inflate(R.layout.fragment_medications, container, false);

        retrieveMedications(); // retrieves medications from database


        FloatingActionButton fab = root.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddMedicationActivity.class);
                startActivityForResult(intent, ADD_REQUEST_CODE);
            }
        });

        // Create recycler view.
        recyclerView = root.findViewById(R.id.recyclable_view);
        // Create an adapter and supply the data to be displayed.
        medicationItemAdapter = new MedicationItemAdapter(getContext(), medicationsList);
        // Connect the adapter with the recycler view.
        recyclerView.setAdapter(medicationItemAdapter);
        // Give the recycler view a default layout manager.
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        medicationItemAdapter.setOnItemClickListener(new MedicationItemAdapter.ClickListener()  {

            @Override
            public void onItemClick(View v, int position) {
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
                retrieveMedications();
                recyclerView.getAdapter().notifyDataSetChanged();
            }
        }else if (requestCode == ADD_REQUEST_CODE){
            if (resultCode == Activity.RESULT_OK){
                medicationsList.clear();
                retrieveMedications();
                recyclerView.getAdapter().notifyDataSetChanged();

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