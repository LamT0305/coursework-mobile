package com.example.myapplication.ClassInstance;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.myapplication.R;

import java.util.List;

public class CustomArrayAdapter extends ArrayAdapter<ClassSample> {
    private Context context;
    private List<ClassSample> objectList;

    public CustomArrayAdapter(Context context, List<ClassSample> objectList) {
        super(context, 0, objectList);
        this.context = context;
        this.objectList = objectList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return createViewFromResource(position, convertView, parent);
    }


    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return createViewFromResource(position, convertView, parent);
    }

    private View createViewFromResource(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.spinner_layout, parent, false);
        }

        ClassSample item = getItem(position);
        TextView nameTextView = view.findViewById(R.id.nameTextView);
        TextView typeTextView = view.findViewById(R.id.typeTextView);
        TextView dayTextView = view.findViewById(R.id.dayTextView);

        if (item != null) {
            nameTextView.setText(item.getTeacherName());
            typeTextView.setText(item.getClassType());
            dayTextView.setText(item.getDayOfWeek());
        }
        return view;
    }
}
