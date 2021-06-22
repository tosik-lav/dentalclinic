package com.example.dentalclinic;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.exchange.Constanta;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "My_log";

    private Socket socket;
    private PrintWriter writer;
    private Scanner scanner;
    int id_client;

    private SharedPreferences sp;
    String jsonDoc, jsonService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "M onCreate start");

        connect();
        init();
        //работает
        sp = getSharedPreferences("registration", Context.MODE_PRIVATE);
        //проверяем, первый ли раз открывается программа
        id_client = sp.getInt("id_client", 0);
        Log.d(TAG, "M id_client " + id_client);
        if (id_client == 0) { //если false
            Intent registrationActivity = new Intent(this, RegistrationActivity.class);
            startActivity(registrationActivity);
            finish();
        }
        Log.d(TAG, "M onCreate finish");
    }


    private void init() {
        Log.d(TAG, "M init start");


        Button btnNote = findViewById(R.id.btnNote);
        Button btnSchedule = findViewById(R.id.btnSchedule);

        btnNote.setOnClickListener(this::dataTimeTable);
        btnSchedule.setOnClickListener(this::docWorking);
        Log.d(TAG, "M init finish");

    }

    private void registration(View view) {
        Intent registrationActivity = new Intent(this, RegistrationActivity.class);
        startActivity(registrationActivity);
        finish();
    }


    private void dataTimeTable(View view) {
        Log.d(TAG, "M Timetable");
        new Thread(() -> {
            writer.println(Constanta.KEY_GET_DOC_SERVICE);
            writer.flush();
            jsonDoc = scanner.nextLine();
            Log.d(TAG, "M Timetable jsonDoc " + jsonDoc);

            jsonService = scanner.nextLine();
            Log.d(TAG, "M Timetable jsonService " + jsonService);

            Intent appointmentActivity = new Intent(this, AppointmentActivity.class);
            appointmentActivity.putExtra("jsonDoc", jsonDoc);
            appointmentActivity.putExtra("jsonService", jsonService);
            startActivity(appointmentActivity);
            finish();
        }).start();
    }

    private void docWorking(View view) {
        Log.d(TAG, "M docWorking");

        new Thread(() -> {
            writer.println(Constanta.KEY_GET_DOCTORS);
            writer.flush();
            jsonDoc = scanner.nextLine();
            jsonService = scanner.nextLine();
            Log.d(TAG, "M jsonDoc" + jsonDoc);
            Log.d(TAG, "M jsonService" + jsonService);
            Intent docActivity = new Intent(this, DocActivity.class);
            docActivity.putExtra("jsonDoc", jsonDoc);
            docActivity.putExtra("jsonService", jsonService);
            startActivity(docActivity);
        }).start();
    }

    private void connect() {

        new Thread(() -> {
            try {
                socket = new Socket("192.168.0.107", 8888);
//                socket = new Socket("192.168.0.96", 8888);
                SocketHandler.setSocket(socket);
                writer = new PrintWriter(socket.getOutputStream());
                scanner = new Scanner(socket.getInputStream());
                runOnUiThread(() -> {
                    Toast.makeText(this, "connected", Toast.LENGTH_SHORT).show();
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    // меню три точки вверхнем правом углу
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        Log.d(TAG, "menu");
        return true;
    }

    @Override // слушателей повесить на пункты меню
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.Price:
                new Thread(() -> {
                    writer.println(Constanta.KEY_PRICE);
                    writer.flush();
                    jsonDoc = scanner.nextLine();
                    Intent priceActivity = new Intent(this, PriceActivity.class);
                    priceActivity.putExtra("json", jsonDoc);
                    startActivity(priceActivity);
                }).start();
                break;
            case R.id.Exit:
                    Log.d(TAG, "M Exit");
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putInt("id_client", 0);
                    Log.d(TAG, "M putInt id_client " + id_client);
                    editor.apply();
                    Log.d(TAG, "M apply ");
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

}
