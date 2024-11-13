package com.example.myapplication.ClassInstance;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
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


public class EditClassInstanceFragment extends Fragment {

    String class_instance_id, class_yoga_id;
    Spinner classYoga;
    TextView date, type;
    EditText teacher, description;
    DatabaseHelper db;
    List<ClassSample> classSamples;
    CustomArrayAdapter customArrayAdapter;
    Button updateClassInstance, deleteClassInstance;
    ClassSample selectedClassSample;
    String dayOfWeek;
    ImageButton pickdate;

    public EditClassInstanceFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        if (getArguments() != null){
            class_instance_id = getArguments().getString("class_instance_id");
            class_yoga_id = getArguments().getString("class_yoga_id");

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_edit_class_instance, container, false);

        classYoga = rootView.findViewById(R.id.inputClassYoga);
        date = rootView.findViewById(R.id.inputDate);
        type = rootView.findViewById(R.id.inputType);
        teacher = rootView.findViewById(R.id.inputTeacher);
        description = rootView.findViewById(R.id.inputDescription);
        updateClassInstance = rootView.findViewById(R.id.updateClInstanceBtn);
        deleteClassInstance = rootView.findViewById(R.id.deleteClInstanceBtn);
        pickdate = rootView.findViewById(R.id.pickDate);

        db = new DatabaseHelper(getContext());

        classSamples = db.getClassSample();

        // Check if classSamples is null or empty
        if (classSamples == null || classSamples.isEmpty()) {
            Toast.makeText(getContext(), "No classes available", Toast.LENGTH_SHORT).show();
            classSamples = new ArrayList<>();
        }

        customArrayAdapter = new CustomArrayAdapter(getContext(), classSamples);
        classYoga.setAdapter(customArrayAdapter);
        classYoga.setEnabled(false);

        pickdate.setOnClickListener(val -> showDatePickerDialog());
        getSetData();



        // update
        updateClassInstance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validateField(teacher)){
                    return;
                }
                String _teacher = teacher.getText().toString();
                String _date = date.getText().toString();
                String comment = description.getText().toString();

                boolean handleUpdate = db.updateClassInstance(Integer.parseInt(class_instance_id), _date, _teacher, comment);
                if (handleUpdate){
                    Toast.makeText(getContext(), "Updated successfully!", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(getContext(), "Update failed!", Toast.LENGTH_SHORT).show();
                }


            }
        });
        deleteClassInstance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean handleDelete = db.deleteClassInstance(Integer.parseInt(class_instance_id));
                if (handleDelete){
                    ClassInstanceFragment classInstanceFragment = new ClassInstanceFragment();
                    Toast.makeText(getContext(), "Deleted successfully!", Toast.LENGTH_SHORT).show();
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, classInstanceFragment)
                            .commit();

                }else {
                    Toast.makeText(getContext(), "Delete failed!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return rootView;
    }

    @SuppressLint("Range")
    private void getSetData(){
        if (getArguments() != null){
            String _teacher = getArguments().getString("teacher");
            String _date = getArguments().getString("date");
            String comment = getArguments().getString("comment");


            ClassSample classSample = db.getClassSampleById(class_yoga_id);
            int position = customArrayAdapter.getPosition(classSample);

            if (position >= 0){
                classYoga.setSelection(position);
            }
            if (class_yoga_id != null && !class_yoga_id.isEmpty()) {
                // Query class type from Classes table
                Cursor classCursor = db.getClassById(Integer.parseInt(class_yoga_id));
                if (classCursor != null && classCursor.moveToFirst()) {
                    @SuppressLint("Range")
                    String classType = classCursor.getString(classCursor.getColumnIndex(DatabaseHelper.COLUMN_TYPE));
                    dayOfWeek = classCursor.getString(classCursor.getColumnIndex(DatabaseHelper.COLUMN_DAY_OF_WEEK));
                    type.setText(classType);
                    classCursor.close();
                } else {
                    type.setText("Unknown"); // Default value if class data is not found
                }
            } else {
                type.setText("Invalid ID"); // Handle case where classId is null or empty
            }
            date.setText(_date);
            description.setText(comment);
            teacher.setText(_teacher);
        }
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
    private boolean validateField(EditText field) {
        if (field.getText().toString().trim().isEmpty()) {
            field.setError("This field is required!");
            return false;
        }
        return true;
    }


}