package com.example.dentalclinic;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.exchange.Constanta;
import com.google.exchange.Doctor;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class DocActivity extends AppCompatActivity {
    private static final String TAG = "My_log";
    private String jsonDoc, jsonService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doc);

        Gson gson = new Gson();

        Bundle extras = getIntent().getExtras();

        assert extras != null;
        jsonDoc = extras.getString("jsonDoc");
        jsonService = extras.getString("jsonService");
        Log.d(TAG, "Doc jsonService " + jsonService);

        Type itemDoc = new TypeToken<List<Doctor>>() { }.getType();
        ArrayList<Doctor> doctors = gson.fromJson(jsonDoc, itemDoc);

        if (!doctors.isEmpty()) {
            ArrayList<String> fullNameDoc = new ArrayList<>();
            for (Doctor doctor : doctors) {
                String name = doctor.getName();
                String patronymic = doctor.getPatronymic();
                String surname = doctor.getSurname();
                String fullName = name + " " + patronymic + " " + surname;
                fullNameDoc.add(fullName);
            }
            jsonDoc = gson.toJson(fullNameDoc);
            Log.d(TAG, "Doc jsonDoc " + jsonDoc);
        }
        RecyclerView rvList = findViewById(R.id.rvList);

        // определяем слушателя нажатия элемента в списке
        DocAdapter.OnDocClickListener doсClickListener = (doctor, position) -> {
            Intent appointmentActivity = new Intent(DocActivity.this, AppointmentActivity.class);
            appointmentActivity.putExtra("jsonDoc", jsonDoc);
            appointmentActivity.putExtra("jsonService", jsonService);
            startActivity(appointmentActivity);
        };

        DocAdapter adapter = new DocAdapter(this, doctors, doсClickListener);
        rvList.setAdapter(adapter);


        Log.d(TAG, "Doc onCreate finish");
    }
}
