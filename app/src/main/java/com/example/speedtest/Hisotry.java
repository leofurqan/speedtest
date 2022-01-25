package com.example.speedtest;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class Hisotry extends AppCompatActivity {

    DatabaseHelper databaseHelper;
    RecyclerView rvHistory;
    HistoryAdapter adapter;
    ArrayList<HistoryData> historyData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hisotry);

        databaseHelper = DatabaseHelper.getInstance(this);
        historyData = databaseHelper.getAllHistory();
        adapter = new HistoryAdapter(this, historyData);
        rvHistory = findViewById(R.id.rv_history);

        rvHistory.setLayoutManager(new LinearLayoutManager(this));
        rvHistory.setAdapter(adapter);
    }
}