package com.example.cmps297nmedicationreminder.ui.calendar;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.example.cmps297nmedicationreminder.R;
import com.example.cmps297nmedicationreminder.logic.DailyMedicationItem;
import com.example.cmps297nmedicationreminder.logic.Helper;
import com.example.cmps297nmedicationreminder.logic.LocalStorage;
import com.example.cmps297nmedicationreminder.logic.MedicationItem;
import com.example.cmps297nmedicationreminder.logic.OnceMedicationItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

public class CalendarFragment extends Fragment {

    private MedicationCalendarAdapter adapter;
    private CalendarView calendarView;
    ArrayList<MedicationItem> allMedications = new ArrayList<MedicationItem>();
    ArrayList<MedicationItem> medications_today = new ArrayList<MedicationItem>();
    int calenderYear;
    int calendarMonth;
    int calenderDay;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        final View root = inflater.inflate(R.layout.fragment_calendar, container, false);




        calendarView = root.findViewById(R.id.calendar_view);


        adapter = new MedicationCalendarAdapter(medications_today,getContext());
        final ListView listView = root.findViewById(R.id.list_of_medications_by_day);
        listView.setAdapter(adapter);


        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                medications_today.clear();
                adapter.clear();

                getAllMedicationForTheSelectedDate(year,month,dayOfMonth);

                calenderYear = year;
                calendarMonth = month;
                calenderDay = dayOfMonth;
                adapter.notifyDataSetChanged();

            }
        });




        // control the checkbox actions
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                MedicationItem medicationItem = medications_today.get(position);
                CheckBox checkBox = v.findViewById(R.id.medication_checkbox);

                if(checkBox.isChecked()){
                    if(medicationItem instanceof OnceMedicationItem){
                        OnceMedicationItem onceMedicationItem = (OnceMedicationItem) medicationItem;
                        onceMedicationItem.undoTackingMedication();
                        onceMedicationItem.checkIfSatisfied(calenderYear,calendarMonth,calenderDay);
                        LocalStorage.updateMedicationItem(onceMedicationItem);

                    }else if (medicationItem instanceof  DailyMedicationItem){
                        DailyMedicationItem dailyMedicationItem = (DailyMedicationItem) medicationItem;
                        dailyMedicationItem.undoTackingMedication(calenderYear,calendarMonth,calenderDay);
                        dailyMedicationItem.checkIfSatisfied(calenderYear,calendarMonth,calenderDay);
                        LocalStorage.updateMedicationItem(dailyMedicationItem);
                    }
                    checkBox.setChecked(false);

                }else{
                    if(medicationItem instanceof OnceMedicationItem){
                        OnceMedicationItem onceMedicationItem = (OnceMedicationItem) medicationItem;
                        onceMedicationItem.takeMedication();
                        onceMedicationItem.checkIfSatisfied(calenderYear,calendarMonth,calenderDay);
                        LocalStorage.updateMedicationItem(onceMedicationItem);

                    }else if (medicationItem instanceof  DailyMedicationItem){
                        DailyMedicationItem dailyMedicationItem = (DailyMedicationItem) medicationItem;
                        dailyMedicationItem.takeMedication(calenderYear,calendarMonth,calenderDay);
                        dailyMedicationItem.checkIfSatisfied(calenderYear,calendarMonth,calenderDay);
                        LocalStorage.updateMedicationItem(dailyMedicationItem);
                    }
                    checkBox.setChecked(true);
                }

            }
        });

        return root;


    }

    @Override
    public void onStart() {
        super.onStart();

        long dateInMilliseconds = calendarView.getDate(); // Gets the selected date in milliseconds since January 1, 1970 00:00:00
        Date date = new Date(dateInMilliseconds);
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calenderYear = calendar.get(Calendar.YEAR);
        calendarMonth = calendar.get(Calendar.MONTH);
        calenderDay = calendar.get(Calendar.DAY_OF_MONTH);

        System.out.println(LocalStorage.MEDICATION_ITEMS);
        allMedications.addAll(LocalStorage.MEDICATION_ITEMS);
        getAllMedicationForTheSelectedDate(calenderYear,calendarMonth,calenderDay);

    }

    // adds medications for the current selected date
    private void getAllMedicationForTheSelectedDate(int year, int month, int dayOfMonth){
        Calendar calendar = Calendar.getInstance();
        calendar.set(year,month,dayOfMonth,0,0);
        Date selectedDate = calendar.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
        Collections.sort(allMedications);

        System.out.println(allMedications);




        for (int i = 0 ; i < allMedications.size(); i++){
            MedicationItem medicationItem = allMedications.get(i);
            medicationItem.checkIfSatisfied(year,month,dayOfMonth);
            if(medicationItem instanceof OnceMedicationItem){
                OnceMedicationItem onceMedicationItem = (OnceMedicationItem) medicationItem;
                if(dateFormat.format(onceMedicationItem.date).equals(dateFormat.format(selectedDate))){
                    onceMedicationItem.checkIfSatisfied(year,month,dayOfMonth);
                    medications_today.add(onceMedicationItem);

                }
            }else if(medicationItem instanceof DailyMedicationItem) {
                DailyMedicationItem dailyMedicationItem = (DailyMedicationItem) medicationItem;


                if(dailyMedicationItem.continuousTreatment && (Helper.getDateDiff(selectedDate,dailyMedicationItem.startDate)< 24)){
                    medications_today.add(dailyMedicationItem);
                }else if (!dailyMedicationItem.continuousTreatment && (Helper.getDateDiff(selectedDate,dailyMedicationItem.startDate)< 24)
                        && (Helper.getDateDiff(selectedDate,dailyMedicationItem.endDate) >= 0)){
                    medications_today.add(dailyMedicationItem);
                }

            }


        }
    }


}