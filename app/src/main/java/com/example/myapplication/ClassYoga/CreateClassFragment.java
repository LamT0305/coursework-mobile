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

public class CreateClassFragment extends Fragment {
    Spinner dayOfWeek;
    EditText description, classType, price, duration, capacity, teacherName;
    TextView timeCourse;
    Button addClassBtn;
    DatabaseHelper DB;
    private boolean isPickedTime;

    public CreateClassFragment() {
        // Required empty public constructor
    }

    public static CreateClassFragment newInstance(String param1, String param2) {
        CreateClassFragment fragment = new CreateClassFragment();
        Bundle args = new Bundle();
        args.putString("param1", param1);
        args.putString("param2", param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_create_class, container, false);

        // Initialize UI elements
        dayOfWeek = rootView.findViewById(R.id.inputDay);
        description = rootView.findViewById(R.id.inputDescription);
        classType = rootView.findViewById(R.id.inputType);
        price = rootView.findViewById(R.id.inputPrice);
        duration = rootView.findViewById(R.id.inputDuration);
        capacity = rootView.findViewById(R.id.inputCapacity);
        timeCourse = rootView.findViewById(R.id.inputTime);
        teacherName = rootView.findViewById(R.id.inputTeacher);
        addClassBtn = rootView.findViewById(R.id.createClassBtn);

        isPickedTime = false;
        DB = new DatabaseHelper(getActivity());  // Use getActivity() to reference the activity context


        textOverFlow();
        // Initialize the dropdown spinner and other listeners
        showDropdownList(dayOfWeek);

        // Handle the "Add Class" button click
        addClassBtn.setOnClickListener(v -> {
            if (!validateField(description) || !validateField(capacity) || !validateField(duration) || !validateField(price) || !validateField(classType)) {
                return;
            }
            if (!isPickedTime){
                Toast.makeText(getActivity(), "Please pick a time!", Toast.LENGTH_SHORT).show();
                return;
            }
            String selectedDay = dayOfWeek.getSelectedItem().toString();
            String timeOfCourse = timeCourse.getText().toString();
            int _capacity = Integer.parseInt(capacity.getText().toString());
            int _duration = Integer.parseInt(duration.getText().toString());
            double _price = Double.parseDouble(price.getText().toString());
            String _classType = classType.getText().toString();
            String teacher = teacherName.getText().toString();
            String _description = description.getText().toString();
            boolean checkAddClass = DB.addClass(selectedDay, timeOfCourse, _capacity, _duration, _price, _classType, teacher, _description);
            if (checkAddClass) {
                getActivity().setResult(getActivity().RESULT_OK);  // Indicate success to trigger refresh in YogaClass activity
                // Replace current fragment with HomeFragment
                HomeFragment homeFragment = new HomeFragment();  // Create a new instance of HomeFragment
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, homeFragment)  // Replace the container with HomeFragment
                        .commit();
                Toast.makeText(getActivity(), "Added class successfully!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "Failed to add class!", Toast.LENGTH_SHORT).show();
            }
        });

        // Time picker click listener
        timeCourse.setOnClickListener(v -> showTimePickerDialog(timeCourse));

        return rootView;
    }

    private void showTimePickerDialog(TextView timeEditText) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
                (TimePicker view, int selectedHour, int selectedMinute) -> {
                    String time = String.format("%02d:%02d", selectedHour, selectedMinute);
                    timeEditText.setText(time);
                    isPickedTime = true;
                }, hour, minute, true); // Use 24-hour format

        timePickerDialog.show();
    }

    private void showDropdownList(Spinner spinner) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.days_of_week, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                parent.getItemAtPosition(position);  // Can use the selected day
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing if nothing is selected
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

    private void textOverFlow(){
        description.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                description.setSingleLine();
                description.setHorizontallyScrolling(true);
            }
        });
    }
}
