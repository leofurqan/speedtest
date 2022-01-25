package com.example.speedtest;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class DNSActivity extends AppCompatActivity implements OnClickListener {

    DatabaseHelper databaseHelper;
    RecyclerView rvDns;
    DNSAdapter dnsAdapter;
    ArrayList<DNSData> dnsData;
    FloatingActionButton fabAdd;
    Intent resultIntent = new Intent();
    DNSData dns;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dnsactivity);

        databaseHelper = DatabaseHelper.getInstance(this);
        rvDns = findViewById(R.id.rv_dns);
        fabAdd = findViewById(R.id.fab_add);
        dns = new DNSData();
        dnsData = databaseHelper.getAllDNS();
        dnsAdapter = new DNSAdapter(this, dnsData);
        dnsAdapter.setOnItemClickListener(this);
        rvDns.setLayoutManager(new LinearLayoutManager(this));
        rvDns.setAdapter(dnsAdapter);

        fabAdd.setOnClickListener(v -> {
            Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.dialog_add_dns);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            TextView txtTitle = dialog.findViewById(R.id.txt_title);
            EditText etIp = dialog.findViewById(R.id.et_ip);
            Button btnAdd = dialog.findViewById(R.id.btn_add);

            txtTitle.setText("Add DNS");

            btnAdd.setOnClickListener(v1 -> {
                if (TextUtils.isEmpty(etIp.getText().toString())) {
                    etIp.setError("IP is Required");
                } else if (!Patterns.IP_ADDRESS.matcher(etIp.getText().toString()).matches()) {
                    etIp.setError("IP is Invalid");
                } else {
                    dns.setIp(etIp.getText().toString());
                    databaseHelper.addDNS(dns);
                    refreshData();
                    dialog.dismiss();
                }
            });

            dialog.show();
        });
    }

    private void refreshData() {
        dnsData = new ArrayList<>();
        dnsData = databaseHelper.getAllDNS();

        dnsAdapter = new DNSAdapter(this, dnsData);
        dnsAdapter.setOnItemClickListener(this);
        rvDns.setAdapter(dnsAdapter);
        rvDns.invalidate();
    }

    @Override
    public void onItemClick(int position, int id) {
        resultIntent.putExtra("dns_id", id);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }
}