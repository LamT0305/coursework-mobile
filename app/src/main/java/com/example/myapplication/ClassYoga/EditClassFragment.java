package com.example.myapplication.ClassYoga;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.myapplication.DatabaseHelper;
import com.example.myapplication.R;

import java.util.Calendar;

public class EditClassFragment extends Fragment {

    // Define parameters to store fragment arguments
    private String id;
    private Spinner dayOfWeek;
    private EditText description, classType, price, duration, capacity, teacherName;
    private TextView timeCourse;
    private DatabaseHelper DB;
    private Button updateBtn, deleteBtn;

    public EditClassFragment() {
        // Required empty public constructor
    }

    // Factory method to create a new instance of this fragment using the provided parameters
    public static EditClassFragment newInstance(String param1, String param2) {
        EditClassFragment fragment = new EditClassFragment();
        Bundle args = new Bundle();
        args.putString("param1", param1);
        args.putString("param2", param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            id = getArguments().getString("position");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_class, container, false);

        // Initialize views
        dayOfWeek = view.findViewById(R.id.inputDay);
        description = view.findViewById(R.id.inputDescription);
        classType = view.findViewById(R.id.inputType);
        price = view.findViewById(R.id.inputPrice);
        duration = view.findViewById(R.id.inputDuration);
        capacity = view.findViewById(R.id.inputCapacity);
        teacherName = view.findViewById(R.id.inputTeacher);
        timeCourse = view.findViewById(R.id.inputTime);
        updateBtn = view.findViewById(R.id.updateClassBtn);
        deleteBtn = view.findViewById(R.id.deleteClassBtn);

        DB = new DatabaseHelper(getContext());

        textOverFlow();
        showDropdownList(dayOfWeek);
        timeCourse.setOnClickListener(v -> showTimePickerDialog(timeCourse));
        getAndSetIntentData();

        // Update button click listener
        updateBtn.setOnClickListener(v -> {
            String selectedDay = dayOfWeek.getSelectedItem().toString();
            String timeOfCourse = timeCourse.getText().toString();
            int _capacity = Integer.parseInt(capacity.getText().toString());
            int _duration = Integer.parseInt(duration.getText().toString());
            double _price = Double.parseDouble(price.getText().toString());
            String _classType = classType.getText().toString();
            String teacher = teacherName.getText().toString();
            String _description = description.getText().toString();

            boolean checkUpdateClass = DB.updateClass(Integer.parseInt(id), selectedDay, timeOfCourse, _capacity, _duration, _price, _classType, teacher, _description);
            if (checkUpdateClass) {
                Toast.makeText(getContext(), "Updated successfully!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Failed to update! " + id, Toast.LENGTH_SHORT).show();
            }
        });

        // Delete button click listener
        deleteBtn.setOnClickListener(v -> {
            int position = Integer.parseInt(id);
            boolean checkDeleteData = DB.deleteClass(position);
            if (checkDeleteData) {
                Toast.makeText(getContext(), "Deleted successfully", Toast.LENGTH_SHORT).show();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new HomeFragment())  // Replace with HomeFragment
                        .commit();
            } else {
                Toast.makeText(getContext(), "Failed to delete", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void showTimePickerDialog(TextView timeEditText) {
        // Get the current time
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        // Initialize the TimePickerDialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                (TimePicker view, int selectedHour, int selectedMinute) -> {
                    // Format the selected time and set it to EditText
                    String time = String.format("%02d:%02d", selectedHour, selectedMinute);

                    timeEditText.setText(time);
                }, hour, minute, true); // Set true for 24-hour format, false for AM/PM format

        // Show the TimePickerDialog
        timePickerDialog.show();
    }

    private void getAndSetIntentData() {
        // Retrieve data passed to the fragment through arguments
        if (getArguments() != null) {
            String _selectedDay = getArguments().getString("dayOfWeek");
            String _timeCourse = getArguments().getString("timeCourse");
            String _capacity = getArguments().getString("capacity");
            String _duration = getArguments().getString("duration");
            String _price = getArguments().getString("price");
            String _classType = getArguments().getString("classType");
            String _teacher = getArguments().getString("teacher");
            String _description = getArguments().getString("description");

            setSelectedDay(dayOfWeek, _selectedDay);
            timeCourse.setText(_timeCourse);
            capacity.setText(_capacity);
            duration.setText(_duration);
            price.setText(_price);
            classType.setText(_classType);
            teacherName.setText(_teacher);
            description.setText(_description);
        } else {
            Toast.makeText(getContext(), "Data error!", Toast.LENGTH_SHORT).show();
        }
    }

    private void showDropdownList(Spinner spinner) {
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.days_of_week, android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        // Set a listener to handle selections
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Handle selection if needed
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle case when nothing is selected
            }
        });
    }

    private void setSelectedDay(Spinner spinner, String value) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.days_of_week, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);

        int position = adapter.getPosition(value);
        if (position >= 0) {
            spinner.setSelection(position);
        }
    }
    private void textOverFlow(){
        description.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                description.setSingleLine();
                description.setHorizontallyScrolling(true);
            }
        });
    }
}
