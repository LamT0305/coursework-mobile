package com.example.myapplication.ClassInstance;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

import java.util.ArrayList;

public class Adapter extends RecyclerView.Adapter<Adapter.MyViewHolder> {
    private Context context;
    private ClassInstanceFragment fragment;
    private ArrayList<String> classInstance_id, classYoga_id, teacher, date, comment, type;

    public Adapter(Context context, ClassInstanceFragment fragment, ArrayList<String> classInstance, ArrayList<String> classYoga, ArrayList<String> teacher, ArrayList<String> date, ArrayList<String> comment, ArrayList<String> type) {
        this.context = context;
        this.fragment = fragment;
        this.classInstance_id = classInstance;
        this.classYoga_id = classYoga;
        this.teacher = teacher;
        this.date = date;
        this.comment = comment;
        this.type = type;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_view_class_instance, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.type.setText(type.get(position) != null ? type.get(position) : "Unknown Type");
        holder.teacher.setText(teacher.get(position) != null ? teacher.get(position) : "Unknown Teacher");
        holder.date.setText(date.get(position) != null ? date.get(position) : "No Date");
        holder.classInstance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currentPosition = holder.getAdapterPosition();
                if (currentPosition != RecyclerView.NO_POSITION){
                    EditClassInstanceFragment editClassInstanceFragment = new EditClassInstanceFragment();

                    Bundle bundle = new Bundle();
                    bundle.putString("class_instance_id", String.valueOf(classInstance_id.get(currentPosition)));
                    bundle.putString("class_yoga_id", String.valueOf(classYoga_id.get(currentPosition)));
                    bundle.putString("teacher", String.valueOf(teacher.get(currentPosition)));
                    bundle.putString("date", String.valueOf(date.get(currentPosition)));
                    bundle.putString("comment", String.valueOf(comment.get(currentPosition)));
                    editClassInstanceFragment.setArguments(bundle);

                    fragment.getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, editClassInstanceFragment)
                            .addToBackStack(null)
                            .commit();
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return classInstance_id.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView teacher, type, date;
        CardView classInstance;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            teacher = itemView.findViewById(R.id.teachertxt);
            type = itemView.findViewById(R.id.typetxt);
            date = itemView.findViewById(R.id.datetxt);
            classInstance = itemView.findViewById(R.id.class_instance_card);
        }
    }
}
