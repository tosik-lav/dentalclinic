package com.example.dentalclinic;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.exchange.Client;
import com.google.exchange.Constanta;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class RegistrationActivity extends AppCompatActivity {
    private static final String TAG = "My_log";
    private EditText etName, etPatronymic, etSurName, etPhone;
    private Button btnSingIn, btnRegister;

    private SharedPreferences sp;
    private PrintWriter writer;
    private Scanner scanner;
    int id_client;

    private Gson gson;
    String json = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        Log.d(TAG, "R onCreate start");

        connect();
        init();

    }

    private void init() {
        Log.d(TAG, "R init");
        sp = getSharedPreferences("registration", Context.MODE_PRIVATE);
        gson = new Gson();

        etName = findViewById(R.id.etName);
        etPatronymic = findViewById(R.id.etPatronymic);
        etSurName = findViewById(R.id.etSurName);
        etPhone = findViewById(R.id.etPhone);

        btnSingIn = findViewById(R.id.btnSingIn);
        btnRegister = findViewById(R.id.btnRegister);

        btnSingIn.setOnClickListener(this::singIn);
        btnRegister.setOnClickListener(this::registration);
    }

    private void connect() {

        new Thread(() -> {
            try {
                SocketHandler.getSocket();
                writer = new PrintWriter(SocketHandler.getSocket().getOutputStream());
                scanner = new Scanner(SocketHandler.getSocket().getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void registration(View view) {
        Log.d(TAG, "R registration");
        Client client = null;

        String name = etName.getText().toString();
        if (name.isEmpty()) {
            Toast.makeText(this, "заполните поле Имя", Toast.LENGTH_SHORT).show();
            return;
        }
        String patronymic = etPatronymic.getText().toString();
        String surName = etSurName.getText().toString();
        if (surName.isEmpty()) {
            Toast.makeText(this, "заполните поле Фамилия", Toast.LENGTH_SHORT).show();
            return;
        }
        String phone = etPhone.getText().toString();
        if (etPhone.getText().toString().length() > 10) {
            Toast.makeText(this, "заполните поле телефон", Toast.LENGTH_SHORT).show();
            return;
        }
        Log.d(TAG, "R registration et");
        if (patronymic.isEmpty()) {
            client = new Client(name, null, surName, phone);
        } else
            client = new Client(name, patronymic, surName, phone);

        Log.d(TAG, "R registration client");
        String jsonClient = gson.toJson(client);
        Log.d(TAG, "R registration jsonClient");

        Toast.makeText(this, "Register", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "R registration finish");

        new Thread(() -> {
            Log.d(TAG, "R saveClient Thread");
            writer.println(Constanta.KEY_ADD_CLIENT);
            writer.println(jsonClient);
            writer.flush();
        }).start();

        Log.d(TAG, "R saveClient finish");
    }

    private void singIn(View view) {
        Log.d(TAG, "R singIn");
        String phoneFind = etPhone.getText().toString();
        new Thread(() -> {
            writer.println(Constanta.KEY_FIND_CLIENT);
            writer.println(phoneFind);
            writer.flush();
            json = scanner.nextLine();
            Client client = gson.fromJson(json, Client.class);
            runOnUiThread(() -> {
                if (client != null) {
                    id_client = client.getId();
                    etName.setText(client.getName());
                    etPatronymic.setText(client.getPatronymic());
                    etSurName.setText(client.getSurname());
                    etPhone.setText(client.getPhone());

                    SharedPreferences.Editor editor = sp.edit();
                        editor.putInt("id_client", id_client);
                    Log.d(TAG, "R singIn id " + id_client);
                        editor.apply();
                    Intent mainActivity = new Intent(this, MainActivity.class);
                    startActivity(mainActivity);
                }
            });
        }).start();
    }
}
