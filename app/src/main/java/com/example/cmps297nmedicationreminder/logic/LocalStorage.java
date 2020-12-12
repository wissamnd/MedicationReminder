package com.example.cmps297nmedicationreminder.logic;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;




public class LocalStorage {

    public static Context context;
    public static ArrayList<MedicationItem> MEDICATION_ITEMS = new ArrayList<MedicationItem>();
    private static final String SHARED_PREFERENCES_NAME = "MEDICATION_ITEMS_PREFERENCES";
    private static final String SHARED_PREFERENCES_ONCE_SET = "MEDICATION_ITEMS_ONCE_SET";
    private static final String SHARED_PREFERENCES_DAILY_SET = "MEDICATION_ITEMS_DAILY_SET";
    public static String email;
    public static String id;
    public static String DisplayName;
    private static FirebaseDatabase rootNode;

    public static void addMedicationItem(MedicationItem item){
        MEDICATION_ITEMS.add(item);
        saveChangesToLocalStorage();
        addMedicationItemToFirebase(item);
    }

    public static void addMedicationItemToFirebase(MedicationItem item){
        rootNode = FirebaseDatabase.getInstance();
        Gson gson = new Gson();
        String json = gson.toJson(item);
        rootNode.getReference().child("users/"+id).child(item.name).setValue(json);
    }

    public static void removeMedicationItem(MedicationItem item){
        MEDICATION_ITEMS.remove(item);
        saveChangesToLocalStorage();
        removeMedicationItemFromFirebase(item);

    }

    public static void removeMedicationItemFromFirebase(MedicationItem item){
        rootNode = FirebaseDatabase.getInstance();
        rootNode.getReference().child("users/"+id).child(item.name).removeValue();

    }

    public static void updateMedicationItem(MedicationItem item){
        MEDICATION_ITEMS.remove(item);
        MEDICATION_ITEMS.add(item);
        saveChangesToLocalStorage();
        addMedicationItemToFirebase(item);


    }




    private static void saveChangesToLocalStorage(){
        SharedPreferences mPrefs = context.getSharedPreferences(SHARED_PREFERENCES_NAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();

        Set<String> setForOnceMedication = new HashSet<>();
        Set<String> setForDailyMedication = new HashSet<>();

        Gson gson = new Gson();
        for (int i = 0; i < MEDICATION_ITEMS.size(); i++) {
            MedicationItem medicationItem = MEDICATION_ITEMS.get(i);
            if (medicationItem instanceof OnceMedicationItem){
                OnceMedicationItem onceMedicationItem = (OnceMedicationItem) medicationItem;
                String json = gson.toJson(onceMedicationItem);
                setForOnceMedication.add(json);
            }else if( medicationItem instanceof DailyMedicationItem){
                DailyMedicationItem dailyMedicationItem = (DailyMedicationItem) medicationItem;
                String json = gson.toJson(dailyMedicationItem);
                setForDailyMedication.add(json);
            }

        }
        prefsEditor.putStringSet(SHARED_PREFERENCES_ONCE_SET, setForOnceMedication);
        prefsEditor.putStringSet(SHARED_PREFERENCES_DAILY_SET, setForDailyMedication);

        prefsEditor.apply();

    }



    public static void retrieveMedicationItemsFromLocalStorage(){

        if (MEDICATION_ITEMS.isEmpty()){
            SharedPreferences mPrefs = context.getSharedPreferences(SHARED_PREFERENCES_NAME,Context.MODE_PRIVATE);
            Gson gson = new Gson();
            Set<String> setForOnceMedication = mPrefs.getStringSet(SHARED_PREFERENCES_ONCE_SET, Collections.<String>emptySet());
            Set<String> setForDailyMedication = mPrefs.getStringSet(SHARED_PREFERENCES_DAILY_SET,Collections.<String>emptySet());

            if (setForOnceMedication != null){
                for (Iterator<String> it = setForOnceMedication.iterator(); it.hasNext(); ) {
                    String json = it.next();
                    OnceMedicationItem onceMedicationItem = gson.fromJson(json, OnceMedicationItem.class);
                    MEDICATION_ITEMS.add(onceMedicationItem);
                }
            }
            if (setForDailyMedication != null){
                for (Iterator<String> it = setForDailyMedication.iterator(); it.hasNext(); ) {
                    String json = it.next();
                    DailyMedicationItem dailyMedicationItem  = gson.fromJson(json, DailyMedicationItem.class);
                    MEDICATION_ITEMS.add(dailyMedicationItem);
                }
            }

        }



    }


}
