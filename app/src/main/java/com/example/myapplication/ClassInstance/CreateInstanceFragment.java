package com.example.myapplication.ClassInstance;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.DatabaseHelper;
import com.example.myapplication.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


public class CreateInstanceFragment extends Fragment {

    Spinner classYoga;
    TextView date, type;
    EditText teacher, description;
    DatabaseHelper db;
    List<ClassSample> classSamples;
    CustomArrayAdapter customArrayAdapter;
    Button createClassInstance;
    ClassSample selectedClassSample;
    String dayOfWeek;
    ImageButton pickdate;

    public CreateInstanceFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_create_instance, container, false);

        classYoga = rootView.findViewById(R.id.inputClassYoga);
        date = rootView.findViewById(R.id.inputDate);
        type = rootView.findViewById(R.id.inputType);
        teacher = rootView.findViewById(R.id.inputTeacher);
        description = rootView.findViewById(R.id.inputDescription);
        createClassInstance = rootView.findViewById(R.id.createClassBtn);
        pickdate = rootView.findViewById(R.id.pickDate);

        db = new DatabaseHelper(getContext());

        classSamples = db.getClassSample();

        // Check if classSamples is null or empty
        if (classSamples == null || classSamples.isEmpty()) {
            Toast.makeText(getContext(), "No classes available", Toast.LENGTH_SHORT).show();
            classSamples = new ArrayList<>();
        }

        // Initialize the custom adapter with classSamples
        customArrayAdapter = new CustomArrayAdapter(getContext(), classSamples);
        classYoga.setAdapter(customArrayAdapter);

        //setup dropdown list of class yoga.
        showDropdownClass(classYoga);


        // handle date picker

        pickdate.setOnClickListener(val -> showDatePickerDialog());

        // handle add class instance
        createClassInstance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedClassSample != null){
                    if (!validateField(teacher)){
                        return;
                    }
                    if (date.getText().toString().trim().isEmpty()){
                        date.setError("This field is required!");
                    }

                    int class_id = selectedClassSample.getId();
                    String _teacher = teacher.getText().toString();
                    String _date = date.getText().toString();
                    String comment = description.getText().toString();



                    boolean checkAddClass = db.addClassInstance(class_id, _date, _teacher, comment);
                    if (checkAddClass){
                        ClassInstanceFragment classInstanceFragment = new ClassInstanceFragment();
                        getActivity().getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, classInstanceFragment)
                                .commit();
                        Toast.makeText(getContext(), "Added successfully!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


        return rootView;
    }

    private void showDatePickerDialog() {
        // Get the current date
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Initialize DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getContext(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        calendar.set(year, month, dayOfMonth);
                        // Display the selected date in TextView
                        String _dayOfWeek = new SimpleDateFormat("EEEE", Locale.getDefault()).format(calendar.getTime());
                        String selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;

                        if (!dayOfWeek.equals(_dayOfWeek)){
                            new AlertDialog.Builder(getContext())
                                    .setTitle("Invalid Date")
                                    .setMessage("Please pick a date that falls on a " + dayOfWeek)
                                    .setPositiveButton("OK", (dialog, which) -> {
                                        // Show the date picker again if user needs to re-select
                                        showDatePickerDialog();
                                    })
                                    .show();
                        }else {
                            date.setText(selectedDate);
                        }
                    }
                },
                year, month, day);

        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void showDropdownClass(Spinner spinner){
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedClassSample = (ClassSample) adapterView.getItemAtPosition(i);
                String day = selectedClassSample.getDayOfWeek();
                String _type = selectedClassSample.getClassType();
                String teacherName = selectedClassSample.getTeacherName();

                dayOfWeek = day;
                type.setText(_type);
                teacher.setText(teacherName);
                // Add any further handling for selected item
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Handle case when nothing is selected, if needed
            }
        });
    }

    private boolean validateField(EditText field) {
        if (field.getText().toString().trim().isEmpty()) {
            field.setError("This field is required!");
            return false;
        }
        return true;
    }
}