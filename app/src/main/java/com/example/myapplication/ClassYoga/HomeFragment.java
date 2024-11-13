package com.example.myapplication.ClassYoga;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.CustomAdapter;
import com.example.myapplication.DatabaseHelper;
import com.example.myapplication.R;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    EditText searchInput;
    TextView searchBtn;
    ImageButton addClassBtn;
    RecyclerView recyclerView;
    CustomAdapter adapter;
    DatabaseHelper DB;

    ArrayList<String> classId, dayOfWeek, description, capacity, price, teacher, classTime, classType, duration;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        searchInput = rootView.findViewById(R.id.searchInput);
        searchBtn = rootView.findViewById(R.id.searchBtn);
        addClassBtn = rootView.findViewById(R.id.addBtn);

        teacher = new ArrayList<>();
        classTime = new ArrayList<>();
        classType = new ArrayList<>();
        duration = new ArrayList<>();
        dayOfWeek = new ArrayList<>();
        description = new ArrayList<>();
        capacity = new ArrayList<>();
        price = new ArrayList<>();
        classId = new ArrayList<>();

        recyclerView = rootView.findViewById(R.id.recyclerView);
        DB = new DatabaseHelper(getContext());
        adapter = new CustomAdapter(getContext(), this, dayOfWeek, description, capacity, price, teacher, classType, classTime, duration, classId);

        // Set up custom adapter
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Display class data into RecyclerView
        displayClass();

        // Search button click listener
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String input = searchInput.getText().toString();
                clearClassDataLists();
                displaySearchResult(input);
                adapter.notifyDataSetChanged();
            }
        });

        addClassBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateClassFragment createClassFragment = new CreateClassFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, createClassFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("YogaClassFragment", "Fragment resumed, refreshing data.");

        // Clear lists and reload data
        clearClassDataLists();
        displayClass();

        // Notify adapter about data changes
        adapter.notifyDataSetChanged();
        Log.d("YogaClassFragment", "Data refreshed in onResume.");
    }

    private void displayClass() {
        Cursor cs = DB.getAllClasses();
        if (cs.getCount() == 0) {
            Toast.makeText(getContext(), "No class available!", Toast.LENGTH_SHORT).show();
        } else {
            while (cs.moveToNext()) {
                dayOfWeek.add(cs.getString(1));
                capacity.add(cs.getString(3));
                price.add(cs.getString(5));
                teacher.add(cs.getString(7));
                classType.add(cs.getString(6));
                classTime.add(cs.getString(2));
                duration.add(cs.getString(4));
                description.add(cs.getString(8));
                classId.add(cs.getString(0));
            }
        }
        if (!cs.isClosed()) {
            cs.close();
        }
    }

    private void clearClassDataLists() {
        dayOfWeek.clear();
        capacity.clear();
        price.clear();
        teacher.clear();
        classType.clear();
        classTime.clear();
        duration.clear();
        description.clear();
        classId.clear();
    }

    private void displaySearchResult(String searchTerm) {
        Cursor cs;
        if (searchTerm == null || searchTerm.isEmpty()) {
            cs = DB.getAllClasses();
        } else {
            cs = DB.getClassesByName(searchTerm);
        }

        if (cs.getCount() == 0) {
            Toast.makeText(getContext(), "No classes found!", Toast.LENGTH_SHORT).show();
        } else {
            while (cs.moveToNext()) {
                dayOfWeek.add(cs.getString(1));
                capacity.add(cs.getString(3));
                price.add(cs.getString(5));
                teacher.add(cs.getString(7));
                classType.add(cs.getString(6));
                classTime.add(cs.getString(2));
                duration.add(cs.getString(4));
                description.add(cs.getString(8));
                classId.add(cs.getString(0));
            }
        }
        if (!cs.isClosed()) {
            cs.close();
        }
    }
}
