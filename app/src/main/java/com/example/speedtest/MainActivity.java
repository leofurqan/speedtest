package com.example.speedtest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.switchmaterial.SwitchMaterial;

public class MainActivity extends AppCompatActivity {

    DatabaseHelper databaseHelper;
    ImageButton btnGo;
    TextView txtServer;
    Button btnChange;
    ImageView imgHistory;
    SwitchMaterial switchNetwork;
    DNSData dns;
    ActivityResultLauncher<Intent> launchDNS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databaseHelper = DatabaseHelper.getInstance(this);
        btnGo = findViewById(R.id.btn_go);
        txtServer = findViewById(R.id.txt_server);
        btnChange = findViewById(R.id.btn_change);
        imgHistory = findViewById(R.id.img_history);
        switchNetwork = findViewById(R.id.switch_network);
        dns = databaseHelper.getDNSByID(1);
        dns.setNetwork("4G");

        if (dns.getId() != 0) {
            txtServer.setText(dns.getIp());
        } else {
            txtServer.setText("Add or Select any DNS");
        }

        launchDNS = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                Intent data = result.getData();

                if (data != null) {
                    dns = databaseHelper.getDNSByID(data.getIntExtra("dns_id", 0));
                    if (switchNetwork.isChecked()) {
                        dns.setNetwork("5G");
                    } else {
                        dns.setNetwork("4G");
                    }
                    txtServer.setText(dns.getIp());
                }
            }
        });

        btnChange.setOnClickListener(v -> {
            Intent intent = new Intent(this, DNSActivity.class);
            launchDNS.launch(intent);
        });

        btnGo.setOnClickListener(v -> {
            if (dns.getId() != 0) {
                Intent intent = new Intent(this, ConnectionActivity.class);
                intent.putExtra("dns", dns);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Select or Add DNS to check network speed", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, DNSActivity.class);
                launchDNS.launch(intent);
            }
        });

        imgHistory.setOnClickListener(v -> {
            Intent intent = new Intent(this, Hisotry.class);
            startActivity(intent);
        });

        switchNetwork.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                dns.setNetwork("5G");
            } else {
                dns.setNetwork("4G");
            }
        });
    }
}