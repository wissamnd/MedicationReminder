package com.example.cmps297nmedicationreminder;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.cmps297nmedicationreminder.logic.DailyMedicationItem;
import com.example.cmps297nmedicationreminder.logic.LocalStorage;
import com.example.cmps297nmedicationreminder.logic.MedicationItem;
import com.example.cmps297nmedicationreminder.logic.MedicationType;
import com.example.cmps297nmedicationreminder.logic.OnceMedicationItem;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class AddMedicationActivity extends AppCompatActivity {
    FirebaseDatabase rootNode;
    DatabaseReference reference;
    Spinner spinnerType, spinnerFrequency;
    LinearLayout medicationsLinearLayout;
    TextInputEditText nameTextEdit, strengthTextEdit;
    String typeOfInstructionsSelected ="";
    final String[] typesOfMedications = {"Pill"};
    final String[] frequencyOfMedication = {"Once","Daily"};
    final String[] instructionsTypes = {"Before Eating","While Eating","After Eating","Not Specified"};
    ArrayList<View> views = new ArrayList<View>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_medication);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.add_medication);

        rootNode = FirebaseDatabase.getInstance();

        nameTextEdit = findViewById(R.id.medication_name_add);
        strengthTextEdit = findViewById(R.id.medication_strength_add);

        ArrayAdapter<String> adapterType = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, typesOfMedications);
        spinnerType = findViewById(R.id.medication_type_selector);
        spinnerType.setAdapter(adapterType);

        ArrayAdapter<String> adapterFrequency = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, frequencyOfMedication);
        spinnerFrequency = findViewById(R.id.medication_frequency_selector);
        spinnerFrequency.setAdapter(adapterFrequency);

        medicationsLinearLayout = findViewById(R.id.medication_linear_layout);

        // add required views programmatically depending on the type of medication selected from the checkbox
        spinnerFrequency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                clearAllNewlyAddedViews(); // clear all previous view before adding the new ones
                if (frequencyOfMedication[position].equals("Once")){
                    addViewsForOnceMedication();
                }else{
                    addViewsForDailyMedications();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });




    }

    public void clearAllNewlyAddedViews(){
        for (int i=0 ; i< views.size(); i++){
            medicationsLinearLayout.removeView(views.get(i));
        }
        views.clear();
    }

    public void addViewsForDailyMedications(){
        final Calendar calendar = Calendar.getInstance();

        final int currentYear = calendar.get(Calendar.YEAR);
        final int currentMonth = calendar.get(Calendar.MONTH);
        final int currentDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        // Number of Times a day
        addHeader("How Many Times a Day?");
        final TextInputEditText  numberOfTimesADay = new TextInputEditText(this);
        numberOfTimesADay.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        numberOfTimesADay.setInputType(InputType.TYPE_CLASS_NUMBER);
        medicationsLinearLayout.addView(numberOfTimesADay);
        views.add(numberOfTimesADay);

        // Create a linear Layout that contains views that are needed depending on the number of times a day the medication should be taken
        final LinearLayout medicationTimesLinearLayout = new LinearLayout(this);
        medicationTimesLinearLayout.setOrientation(LinearLayout.VERTICAL);
        medicationTimesLinearLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        medicationsLinearLayout.addView(medicationTimesLinearLayout);
        views.add(medicationTimesLinearLayout);

        final ArrayList<Integer> hoursList = new ArrayList<>();
        final ArrayList<Integer> minutesList = new ArrayList<>();
        final ArrayList<Integer> numberOfPillsList = new ArrayList<>();

        numberOfTimesADay.addTextChangedListener(new TextWatcher() {
            int numberOfViewAdded = 0;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                medicationTimesLinearLayout.removeAllViews();
                hoursList.clear();
                minutesList.clear();
                numberOfPillsList.clear();

                if (s != null && s.length() >0){
                    for (int i = 0; i < Integer.parseInt(s.toString()); i++){
                        hoursList.add(0);
                        minutesList.add(0);
                        numberOfPillsList.add(0);
                        final int index = i;
                        TextView headerTextView = new TextView(AddMedicationActivity.this);
                        headerTextView.setText("Select Time for Dose "+(i+1));
                        headerTextView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        headerTextView.setPadding(0,32,0,32);
                        headerTextView.setTypeface(headerTextView.getTypeface(), Typeface.BOLD);
                        medicationTimesLinearLayout.addView(headerTextView);

                        final Button timeSelector = new Button(AddMedicationActivity.this);
                        timeSelector.setText("NOT SELECTED");
                        timeSelector.setBackgroundColor(getResources().getColor(R.color.gray_color));

                        timeSelector.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                TimePickerDialog dpd = new TimePickerDialog(AddMedicationActivity.this,
                                        new TimePickerDialog.OnTimeSetListener() {
                                            @Override
                                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                                Calendar c = Calendar.getInstance();
                                                c.set(Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH, hourOfDay,minute);
                                                String time = new SimpleDateFormat("h:mm a").format(c.getTime());
                                                timeSelector.setText(time);
                                                hoursList.set(index,hourOfDay);
                                                minutesList.set(index,minute);
                                            }

                                        }, 8,0,false);
                                dpd.show();
                            }
                        });

                        medicationTimesLinearLayout.addView(timeSelector);

                        TextView numberOfPillsHeader = new TextView(AddMedicationActivity.this);
                        numberOfPillsHeader.setText("Number of Pills For "+(i+1));
                        numberOfPillsHeader.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        numberOfPillsHeader.setPadding(0,32,0,32);
                        numberOfPillsHeader.setTypeface(numberOfPillsHeader.getTypeface(), Typeface.BOLD);
                        medicationTimesLinearLayout.addView(numberOfPillsHeader);

                        final TextInputEditText  numberOfPillsADay = new TextInputEditText(AddMedicationActivity.this);
                        numberOfPillsADay.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        numberOfPillsADay.setInputType(InputType.TYPE_CLASS_NUMBER);
                        medicationTimesLinearLayout.addView(numberOfPillsADay);


                        numberOfPillsADay.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {

                            }

                            @Override
                            public void afterTextChanged(Editable s) {
                                if(s != null && s.length() > 0){
                                    numberOfPillsList.set(index, Integer.parseInt(s.toString()));
                                }

                            }
                        });
                    }
                    numberOfViewAdded = Integer.parseInt(s.toString());
                }


            }

            @Override
            public void afterTextChanged(Editable s) { }
        });


        addHeader("Start Date");

        final Date[] startDate = new Date[1];

        final Button startDataSelector = new Button(this);
        startDataSelector.setText("NOT SELECTED");
        startDataSelector.setBackgroundColor(getResources().getColor(R.color.gray_color));

        startDataSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dpd = new DatePickerDialog(AddMedicationActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int day) {
                                Calendar c = Calendar.getInstance();
                                c.set(year, month, day);
                                String date = new SimpleDateFormat("MM/dd/yyyy").format(c.getTime());
                                startDataSelector.setText(date);
                                startDate[0] = c.getTime();

                            }
                        }, currentYear, currentMonth, currentDayOfMonth);
                dpd.getDatePicker().setMinDate(System.currentTimeMillis());

                dpd.show();
            }
        });

        medicationsLinearLayout.addView(startDataSelector);
        views.add(startDataSelector);



        final Button endDataSelector = new Button(this);
        endDataSelector.setText("NOT SELECTED");
        endDataSelector.setBackgroundColor(getResources().getColor(R.color.gray_color));
        final CheckBox checkBox = new CheckBox(this);
        checkBox.setText("Continuous Treatment");

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    endDataSelector.setEnabled(false);
                }else {
                    endDataSelector.setEnabled(true);
                }
            }
        });

        medicationsLinearLayout.addView(checkBox);
        views.add(checkBox);

        addHeader("End Date");

        final Date[] endDate = new Date[1];

        calendar.add(Calendar.DATE,1);

        endDataSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dpd = new DatePickerDialog(AddMedicationActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int day) {
                                Calendar c = Calendar.getInstance();
                                c.set(year, month, day);
                                String date = new SimpleDateFormat("MM/dd/yyyy").format(c.getTime());
                                endDataSelector.setText(date);
                                endDate[0] = c.getTime();

                            }
                        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));


                dpd.getDatePicker().setMinDate(calendar.getTimeInMillis());

                dpd.show();
            }
        });

        medicationsLinearLayout.addView(endDataSelector);
        views.add(endDataSelector);




        addHeader("When should this be taken?");

        Spinner instructionsSpinner = new Spinner(this);
        instructionsSpinner.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));
        ArrayAdapter instructionsAdapter = new ArrayAdapter(this,R.layout.support_simple_spinner_dropdown_item,instructionsTypes);
        instructionsSpinner.setAdapter(instructionsAdapter);
        medicationsLinearLayout.addView(instructionsSpinner);
        views.add(instructionsSpinner);

        instructionsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                typeOfInstructionsSelected = instructionsTypes[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        addHeader("Additional Instructions");
        final EditText additionalInstructionsText = new EditText(this);
        additionalInstructionsText.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));
        additionalInstructionsText.setMinLines(3);
        additionalInstructionsText.setMaxLines(6);
        additionalInstructionsText.setPadding(0,16,0,32);


        medicationsLinearLayout.addView(additionalInstructionsText);
        views.add(additionalInstructionsText);


        Button saveButton = new Button(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0,32,0,32);
        saveButton.setLayoutParams(params);
        saveButton.setBackgroundColor(getResources().getColor(R.color.gray_color));
        TypedValue outValue = new TypedValue();
        getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            saveButton.setForeground(getDrawable(outValue.resourceId));
        }

        saveButton.setText("Save");
        medicationsLinearLayout.addView(saveButton);
        views.add(saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String medicationName = nameTextEdit.getText().toString();
                String medicationStrengthText = strengthTextEdit.getText().toString();
                String numberOfMedicationsText = numberOfTimesADay.getText().toString();
                boolean startDateSelected = (startDataSelector.getText().toString()).equals("NOT SELECTED")?false:true;
                boolean endDateSelected = (endDataSelector.getText().toString()).equals("NOT SELECTED")?false:true;




                if(medicationName.length() >0 && medicationStrengthText.length() >0 && numberOfMedicationsText.length()>0){

                    int numberOfMedications = Integer.parseInt(numberOfMedicationsText);

                    boolean hoursSelected = true;
                    boolean numberOfPillsSelected = true;

                    for (int i = 0; i < hoursList.size(); i++){
                        if(hoursList.get(i) == 0){
                            hoursSelected = false;
                        }
                        if(numberOfPillsList.get(i) == 0){
                            numberOfPillsSelected = false;
                        }
                    }

                    if(hoursSelected && numberOfPillsSelected){
                        if(checkBox.isChecked() && startDateSelected){
                            for (int i = 0 ; i < numberOfMedications; i++){
                                DailyMedicationItem dailyMedicationItem = new DailyMedicationItem(medicationName,
                                        MedicationType.PILL,Integer.parseInt(medicationStrengthText), numberOfPillsList.get(i),
                                        MedicationItem.typeOfInstruction(typeOfInstructionsSelected),startDate[0],hoursList.get(i),minutesList.get(i));

                                if(additionalInstructionsText.getText().toString().length() >0){
                                    dailyMedicationItem.additionalInstructions = additionalInstructionsText.getText().toString();
                                }
                                LocalStorage.addMedicationItem(dailyMedicationItem);

                                Intent intent = new Intent();
                                setResult(RESULT_OK,intent);
                                finish();

                            }

                        }else if (!checkBox.isChecked() && endDateSelected){
                            for (int i = 0 ; i < numberOfMedications; i++){
                                DailyMedicationItem dailyMedicationItem = new DailyMedicationItem(medicationName,
                                        MedicationType.PILL,Integer.parseInt(medicationStrengthText), numberOfPillsList.get(i),
                                        MedicationItem.typeOfInstruction(typeOfInstructionsSelected),startDate[0],endDate[0],hoursList.get(i),minutesList.get(i));
                                LocalStorage.addMedicationItem(dailyMedicationItem);
                                if(additionalInstructionsText.getText().toString().length() >0){
                                    dailyMedicationItem.additionalInstructions = additionalInstructionsText.getText().toString();
                                }
                                Intent intent = new Intent();
                                setResult(RESULT_OK,intent);
                                finish();

                            }
                        }

                    }else {
                        // display an error: Time and number of pills not selected
                    }

                }else {
                    // display an error : Name not selected
                    if (medicationName.length() == 0){
                        nameTextEdit.setError("insert name");
                    }

                    if (medicationStrengthText.length() ==0){
                        strengthTextEdit.setError("insert strength");
                    }
                    if (numberOfMedicationsText.length() == 0 ){
                        numberOfTimesADay.setError("insert number");
                    }

                }


            }
        });






    }

    public void addViewsForOnceMedication(){


        Calendar calendar = Calendar.getInstance();

        final int currentYear = calendar.get(Calendar.YEAR);
        final int currentMonth = calendar.get(Calendar.MONTH);
        final int currentDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        final int[] tyear = {currentYear};
        final int[] tmonth = {currentMonth};
        final int[] tdate = {currentDayOfMonth};
        final int[] thour = {8};
        final int[] tminute = {0};


        addHeader("Select Date");

        final Button dataSelector = new Button(this);
        dataSelector.setText("NOT SELECTED");
        dataSelector.setBackgroundColor(getResources().getColor(R.color.gray_color));

        dataSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dpd = new DatePickerDialog(AddMedicationActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int day) {
                                Calendar c = Calendar.getInstance();
                                c.set(year, month, day);
                                String date = new SimpleDateFormat("MM/dd/yyyy").format(c.getTime());
                                dataSelector.setText(date);
                                tyear[0] = year;
                                tmonth[0] = month;
                                tdate[0] = day;

                            }
                        }, currentYear, currentMonth, currentDayOfMonth);
                dpd.getDatePicker().setMinDate(System.currentTimeMillis());


                dpd.show();
            }
        });

        medicationsLinearLayout.addView(dataSelector);
        views.add(dataSelector);

        addHeader("Select Time");


        final Button timeSelector = new Button(this);
        timeSelector.setText("NOT SELECTED");
        timeSelector.setBackgroundColor(getResources().getColor(R.color.gray_color));

        timeSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog dpd = new TimePickerDialog(AddMedicationActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                Calendar c = Calendar.getInstance();
                                c.set(Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH, hourOfDay,minute);
                                String time = new SimpleDateFormat("h:mm a").format(c.getTime());
                                timeSelector.setText(time);
                                thour[0] = hourOfDay;
                                tminute[0] =minute;
                            }

                        }, 8,0,false);
                dpd.show();
            }
        });

        medicationsLinearLayout.addView(timeSelector);
        views.add(timeSelector);

        addHeader("Number of pills");

        TextInputLayout textInputLayout = new TextInputLayout(new ContextThemeWrapper(this, R.style.Widget_MaterialComponents_TextInputLayout_OutlinedBox));
        textInputLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));


        final TextInputEditText  numberOfPills = new TextInputEditText(this);

        numberOfPills.setInputType(InputType.TYPE_CLASS_NUMBER);

        textInputLayout.addView(numberOfPills, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        medicationsLinearLayout.addView(textInputLayout);
        views.add(textInputLayout);

        addHeader("When should this be taken?");

        Spinner instructionsSpinner = new Spinner(this);
        instructionsSpinner.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));
        ArrayAdapter instructionsAdapter = new ArrayAdapter(this,R.layout.support_simple_spinner_dropdown_item,instructionsTypes);
        instructionsSpinner.setAdapter(instructionsAdapter);
        medicationsLinearLayout.addView(instructionsSpinner);
        views.add(instructionsSpinner);

        instructionsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                typeOfInstructionsSelected = instructionsTypes[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        addHeader("Additional Instructions");
        final EditText additionalInstructionsText = new EditText(this);
        additionalInstructionsText.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));
        additionalInstructionsText.setMinLines(3);
        additionalInstructionsText.setMaxLines(6);
        additionalInstructionsText.setPadding(0,16,0,32);


        medicationsLinearLayout.addView(additionalInstructionsText);
        views.add(additionalInstructionsText);

        Button saveButton = new Button(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0,32,0,32);
        saveButton.setLayoutParams(params);
        saveButton.setBackgroundColor(getResources().getColor(R.color.gray_color));
        TypedValue outValue = new TypedValue();
        getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            saveButton.setForeground(getDrawable(outValue.resourceId));
        }
        saveButton.setText("Save");


        medicationsLinearLayout.addView(saveButton);
        views.add(saveButton);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(tyear[0],tmonth[0],tdate[0],thour[0],tminute[0]);

                String name = nameTextEdit.getText().toString();
                String strengthString = strengthTextEdit.getText().toString();
                String additionalInstructions = additionalInstructionsText.getText().toString();
                String numberOfPillsString = numberOfPills.getText().toString();
                if(name.length()>0 && strengthString.length()>0 && numberOfPillsString.length() >0 && !dataSelector.getText().toString().equals("NOT SELECTED") && !timeSelector.getText().toString().equals("NOT SELECTED")){


                    OnceMedicationItem onceMedicationItem = new OnceMedicationItem(name,MedicationType.PILL,Integer.parseInt(strengthString),
                            Integer.parseInt(numberOfPillsString),MedicationItem.typeOfInstruction(typeOfInstructionsSelected),calendar.getTime());
                    if(additionalInstructions.length() >0){
                        onceMedicationItem.additionalInstructions = additionalInstructions;
                    }
                    LocalStorage.addMedicationItem(onceMedicationItem);
                    Intent intent = new Intent();
                    setResult(RESULT_OK,intent);
                    finish();
                }else {
                    if (name.length() == 0){
                        nameTextEdit.setError("insert name");
                    }

                    if (strengthString.length() == 0){
                        strengthTextEdit.setError("insert strength");
                    }
                }


            }
        });

    }

    public void addHeader(String text){
        TextView headerTextView = new TextView(this);
        headerTextView.setText(text);
        headerTextView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        headerTextView.setPadding(0,32,0,32);
        headerTextView.setTypeface(headerTextView.getTypeface(), Typeface.BOLD);
        medicationsLinearLayout.addView(headerTextView);
        views.add(headerTextView);
    }
    @Override
    public void onBackPressed() {
        finish();
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