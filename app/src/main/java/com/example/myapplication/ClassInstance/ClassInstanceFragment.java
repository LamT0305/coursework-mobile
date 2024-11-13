package com.example.myapplication.ClassInstance;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.myapplication.DatabaseHelper;
import com.example.myapplication.R;

import java.util.ArrayList;

public class ClassInstanceFragment extends Fragment {

    RecyclerView recyclerView;
    ImageButton addClass;
    Adapter adapter;
    DatabaseHelper db;
    ArrayList<String> classInstance_id, classYoga_id, teacher, date, comment, type;

    public ClassInstanceFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_class_instance, container, false);

        // Initialize lists
        classInstance_id = new ArrayList<>();
        classYoga_id = new ArrayList<>();
        teacher = new ArrayList<>();
        date = new ArrayList<>();
        comment = new ArrayList<>();
        type = new ArrayList<>();

        // Initialize database helper
        db = new DatabaseHelper(getContext());

        // Set up RecyclerView and Adapter
        recyclerView = rootView.findViewById(R.id.recyclerViewInstance);
        addClass = rootView.findViewById(R.id.addInstanceBtn);
        adapter = new Adapter(getContext(), this, classInstance_id, classYoga_id, teacher, date, comment, type);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Handle Add button click
        addClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateInstanceFragment createInstanceFragment = new CreateInstanceFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, createInstanceFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        // Fetch and display class instances
        displayClassInstance();

        return rootView;
    }

    private void displayClassInstance() {
        // Query all class instances
        Cursor cs = db.getClassInstances();

        // Check if the cursor has data
        if (cs != null && cs.getCount() != 0) {
            while (cs.moveToNext()) {
                // Get class instance details
                @SuppressLint("Range") String instanceId = cs.getString(cs.getColumnIndex(DatabaseHelper.COLUMN_CLASS_INSTANCE_ID));
                @SuppressLint("Range") String classId = cs.getString(cs.getColumnIndex(DatabaseHelper.COLUMN_CLASS_YOGA_ID));
                @SuppressLint("Range") String teacherName = cs.getString(cs.getColumnIndex(DatabaseHelper.COLUMN_SCHEDULE_TEACHER));
                @SuppressLint("Range") String classDate = cs.getString(cs.getColumnIndex(DatabaseHelper.COLUMN_DATE));
                @SuppressLint("Range") String classComment = cs.getString(cs.getColumnIndex(DatabaseHelper.COLUMN_COMMENTS));

                // Add to arrays for adapter
                classInstance_id.add(instanceId);
                classYoga_id.add(classId);
                teacher.add(teacherName);
                date.add(classDate);
                comment.add(classComment);


                // Query class type from Classes table
                if (classId != null && !classId.isEmpty()) {
                    // Query class type from Classes table
                    Cursor classCursor = db.getClassById(Integer.parseInt(classId));
                    if (classCursor != null && classCursor.moveToFirst()) {
                        @SuppressLint("Range")
                        String classType = classCursor.getString(classCursor.getColumnIndex(DatabaseHelper.COLUMN_TYPE));
                        type.add(classType);
                        classCursor.close();
                    } else {
                        type.add("Unknown"); // Default value if class data is not found
                    }
                } else {
                    type.add("Invalid ID"); // Handle case where classId is null or empty
                }
            }
        }

        // Close the cursor
        if (cs != null) {
            cs.close();
        }

        // Notify the adapter about data change
        adapter.notifyDataSetChanged();
    }
}
