package com.example.dentalclinic;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.exchange.Doctor;
import java.util.ArrayList;

class DocAdapter extends RecyclerView.Adapter<DocAdapter.DocViewHolder> {
    private static final String TAG = "My_log";

    interface  OnDocClickListener{
        void onDocClick(Doctor doctor, int position);
    }

    private final OnDocClickListener onClickListener;

    private final LayoutInflater inflater;
    private final ArrayList<Doctor> doctors;

    DocAdapter(Context context, ArrayList<Doctor> doctors, OnDocClickListener onDocClickListener) {
        this.inflater = LayoutInflater.from(context);
        this.doctors = doctors;
        this.onClickListener = onDocClickListener;
    }

    @NonNull
    @Override
    public DocAdapter.DocViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = inflater.inflate(R.layout.item_doc2, parent, false);
        return new DocViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DocAdapter.DocViewHolder holder, int position) {
        Doctor doctor = doctors.get(position);
        String name = doctor.getName();
        String surName = doctor.getSurname();
        String patronymic = doctor.getPatronymic();
        String fullName = name + "\n" + patronymic + "\n" + surName;

        holder.itemView.setOnClickListener(v -> {
            onClickListener.onDocClick(doctor, position);

        });

        holder.tvFullName.setText(fullName);
        holder.tvPhone.setText(doctor.getPhone());
        holder.tvPosition.setText(doctor.getPosition());
        String category = "Категория: " + doctor.getCategory();
        holder.tvCategory.setText(category);
        String workingHours = "Работаем по " + doctor.getWorkingHours();
        holder.tvWorkingHours.setText(workingHours);
    }

    @Override
    public int getItemCount() {
        return doctors.size();
    }

    static class DocViewHolder extends RecyclerView.ViewHolder {
        private TextView tvFullName;
        private TextView tvPosition;
        private TextView tvCategory;
        private TextView tvWorkingHours;
        private TextView tvPhone;

        DocViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFullName = itemView.findViewById(R.id.tvFullName);
            tvPhone = itemView.findViewById(R.id.tvPhone);
            tvPosition = itemView.findViewById(R.id.tvPosition);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvWorkingHours = itemView.findViewById(R.id.tvWorkingHours);
        }
    }
}
