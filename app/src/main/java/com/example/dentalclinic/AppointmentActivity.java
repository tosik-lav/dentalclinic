package com.example.dentalclinic;

import androidx.appcompat.app.AppCompatActivity;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.exchange.Constanta;
import com.google.exchange.Timetable;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.naishadhparmar.zcustomcalendar.CustomCalendar;
import org.naishadhparmar.zcustomcalendar.Property;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class AppointmentActivity extends AppCompatActivity {
    private static final String TAG = "My_log";

    private CustomCalendar customCalendar;
    private Calendar calendar = Calendar.getInstance();
    private Spinner spnDoctor, spnService;
    private String sDate, time;
    private int selectDoc, selectService;
    private int id_client;
    private String jsonDoc, jsonService;

    private PrintWriter writer;
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment);
        Log.d(TAG, "A onCreate ");

        connect();
        init();

        HashMap<Object, Property> desvHashMap = new HashMap<>();
        Property defaultProperty = new Property();
        defaultProperty.layoutResource = R.layout.default_view;
        defaultProperty.dateTextViewResource = R.id.tv_CV;
        desvHashMap.put("default", defaultProperty);

        //на текущую дату
        Property currentProperty = new Property();
        currentProperty.layoutResource = R.layout.current_view;
        currentProperty.dateTextViewResource = R.id.tv_CV;
        desvHashMap.put("current", currentProperty);

        Property oddProperty = new Property();
        oddProperty.layoutResource = R.layout.odd_view;
        oddProperty.dateTextViewResource = R.id.tv_CV;
        desvHashMap.put("odd", oddProperty);

        Property evenProperty = new Property();
        evenProperty.layoutResource = R.layout.even_view;
        evenProperty.dateTextViewResource = R.id.tv_CV;
        desvHashMap.put("even", evenProperty);

        customCalendar.setMapDescToProp(desvHashMap);

        HashMap<Integer, Object> dateHashMap = new HashMap<>();
        calendar = Calendar.getInstance();

        //вставляем значения
        dateHashMap.put(calendar.get(Calendar.DAY_OF_MONTH), currentProperty);
        //метод окрашивания дней
        dateHashMap.putAll(paintTheDays(dateHashMap));

        //вставляем даты
        customCalendar.setDate(calendar, dateHashMap);

        customCalendar.setOnDateSelectedListener((view, selectedDate, desc) -> {
            //Get string date
            sDate = selectedDate.get(Calendar.DAY_OF_MONTH)
                    + "/" + (selectedDate.get(Calendar.MONTH) + 1)
                    + "/" + selectedDate.get(Calendar.YEAR);

            timePicker();
        });
    }

    private void init() {
        SharedPreferences sp = getSharedPreferences("registration", Context.MODE_PRIVATE);
        id_client = sp.getInt("id_client", 0);

        gson = new Gson();

        Bundle extras = getIntent().getExtras();
        assert extras != null;
        String jsonDocName = extras.getString("jsonDoc");
        String jsonCategory = extras.getString("jsonService");

        Type itemDoc = new TypeToken<List<String>>() {
        }.getType();
        ArrayList<String> fullNameDoc = gson.fromJson(jsonDocName, itemDoc);

        Type itemService = new TypeToken<List<String>>() {
        }.getType();
        ArrayList<String> allCategory = gson.fromJson(jsonCategory, itemService);

        customCalendar = findViewById(R.id.custom_calendar);
        Button btnOk = findViewById(R.id.btnOk);
        spnDoctor = findViewById(R.id.spnDoctor);
        spnService = findViewById(R.id.spnService);

        assert fullNameDoc != null;
        ArrayAdapter<String> adapterDoc = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                fullNameDoc);
        spnDoctor.setAdapter(adapterDoc);

        assert allCategory != null;
        ArrayAdapter<String> adapterCategory = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                allCategory);
        spnService.setAdapter(adapterCategory);

        spnDoctor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectDoc = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spnService.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectService = position;
                Log.d(TAG, "A spnService position " + position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //сохранение записи
        btnOk.setOnClickListener(this::buttonOkSave);
    }

    private void buttonOkSave(View view) {
        if (id_client == 0) {
            Toast.makeText(this, "Register to make an appointment", Toast.LENGTH_SHORT).show();
            return;
        }
        if (sDate == null) {
            Toast.makeText(this, "Choose a date", Toast.LENGTH_SHORT).show();
            return;
        }
        if (time == null) {
            Toast.makeText(this, "Select appointment time", Toast.LENGTH_SHORT).show();
            return;

        }
        if (selectDoc == 0) {
            Toast.makeText(this, "Choose a doctor", Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectService == 0) {
            Toast.makeText(this, "Select service category", Toast.LENGTH_SHORT).show();
            return;
        }

        Timetable timetable = new Timetable(sDate, time, id_client, selectDoc, selectService);
        String json = gson.toJson(timetable);
        new Thread(() -> {
            writer.println(Constanta.KEY_TIMETABLE);
            writer.println(json);
            writer.flush();
            Intent mainActivity = new Intent(this, MainActivity.class);
            startActivity(mainActivity);
        }).start();
        finish();
    }

    //выбор времени
    private void timePicker() {

        TimePickerDialog.OnTimeSetListener callBack = (view, hourOfDay, minute) -> {

                time = hourOfDay + ":" + minute;
        };

        TimePickerDialog dialog = new TimePickerDialog(
                this,
                android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
                callBack,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
        );

        dialog.show();
    }

    //метод окрашивания дней
    private HashMap<Integer, Object> paintTheDays(HashMap<Integer, Object> hashMap) {
        int maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        int nowDay = calendar.get(Calendar.DAY_OF_MONTH);

        for (int i = 1; i <= maxDay; i++) {
            if (i != nowDay) {
                if (i % 2 == 0) {
                    hashMap.put(i, "even");
                } else
                    hashMap.put(i, "odd");
            }
        }
        return hashMap;
    }

    private void connect() {

        new Thread(() -> {
            try {
                SocketHandler.getSocket();
                writer = new PrintWriter(SocketHandler.getSocket().getOutputStream());
                Scanner scanner = new Scanner(SocketHandler.getSocket().getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
