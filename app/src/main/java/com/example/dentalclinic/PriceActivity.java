package com.example.dentalclinic;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.exchange.Constanta;
import com.google.exchange.Doctor;
import com.google.exchange.Service;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class PriceActivity extends AppCompatActivity {
    private static final String TAG = "myLogs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_price);
        Log.d(TAG, "Price onCreate start");

        Gson gson = new Gson();

        Bundle extras = getIntent().getExtras();
        Log.d(TAG, "extras");
        assert extras != null;
        String jsonPrice = extras.getString("json");

        Type itemPrice = new TypeToken<List<Service>>() {}.getType();
        ArrayList<Service> prices = gson.fromJson(jsonPrice, itemPrice);
        assert prices != null;
        Log.d(TAG, "prices " + prices.size());

        // начальная инициализация списка
        RecyclerView recV = findViewById(R.id.recV);

        // создаем адаптер
        PriceAdapter adapter = new PriceAdapter(this, prices);
        Log.d(TAG, "Price onCreate adapter");
        // устанавливаем для списка адаптер
        recV.setAdapter(adapter);

        Log.d(TAG, "Price onCreate finish");
    }


}
