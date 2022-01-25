package com.example.speedtest;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;

import fr.bmartel.speedtest.SpeedTestReport;
import fr.bmartel.speedtest.SpeedTestSocket;
import fr.bmartel.speedtest.inter.ISpeedTestListener;
import fr.bmartel.speedtest.model.SpeedTestError;

public class ConnectionActivity extends AppCompatActivity {
    DNSData dns;

    DatabaseHelper databaseHelper;

    long timeofping;
    TextView txtPing, txtLatency, txtLoss, txtNetwork;
    String latency = "";
    String lost = "";

    static int position = 0;
    static int lastPosition = 0;

    ImageView barImage, speedImage;
    TextView downloadSpeed, uploadSpeed, txtServer;

    HistoryData history;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection);

        databaseHelper = DatabaseHelper.getInstance(this);
        dns = (DNSData) getIntent().getSerializableExtra("dns");

        txtPing = findViewById(R.id.txt_ping);
        txtLatency = findViewById(R.id.txt_jitter);
        txtLoss = findViewById(R.id.txt_loss);
        txtServer = findViewById(R.id.txt_server);
        txtNetwork = findViewById(R.id.network);

        barImage = (ImageView) findViewById(R.id.barImageView);
        speedImage = findViewById(R.id.imageView);
        downloadSpeed = (TextView) findViewById(R.id.download);
        uploadSpeed = (TextView) findViewById(R.id.uplaod);

        txtNetwork.setText(dns.getNetwork());
        txtServer.setText(dns.getIp());

        history = new HistoryData();

        if (dns.getNetwork().equals("4G")) {
            speedImage.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.main, null));
        } else {
            speedImage.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.main2, null));
        }

        history.setIp(dns.getIp());

        new Thread(ping).start();
    }

    public class SpeedTestTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {

            SpeedTestSocket speedTestSocket = new SpeedTestSocket();

            // add a listener to wait for speedtest completion and progress
            speedTestSocket.addSpeedTestListener(new ISpeedTestListener() {

                @Override
                public void onCompletion(SpeedTestReport report) {
                    position = 0;
                    lastPosition = 0;

                    RotateAnimation rotateAnimation;
                    rotateAnimation = new RotateAnimation(lastPosition, position, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                    rotateAnimation.setInterpolator(new LinearInterpolator());
                    rotateAnimation.setDuration(2000);
                    barImage.startAnimation(rotateAnimation);

                    runOnUiThread(() -> checkUploadSpeed());
                }

                @Override
                public void onError(SpeedTestError speedTestError, String errorMessage) {
                    runOnUiThread(() -> {
                        Toast.makeText(ConnectionActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    });
                }

                @Override
                public void onProgress(float percent, SpeedTestReport report) {
                    // called to notify download/upload progress
                    Log.v("speedtest", "[PROGRESS] progress : " + percent + "%");
                    Log.v("speedtest", "[PROGRESS] rate in octet/s : " + report.getTransferRateOctet());
                    Log.v("speedtest", "[PROGRESS] rate in bit/s   : " + report.getTransferRateBit());
                    java.math.BigDecimal bigDecimal = new java.math.BigDecimal("" + report.getTransferRateBit());
                    float finalDownload = (bigDecimal.longValue() / 1000000);
                    position = getPositionByRate(finalDownload);

                    runOnUiThread(() -> {
                        RotateAnimation rotateAnimation;
                        rotateAnimation = new RotateAnimation(lastPosition, position, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                        rotateAnimation.setInterpolator(new LinearInterpolator());
                        rotateAnimation.setDuration(2000);
                        barImage.startAnimation(rotateAnimation);
                        downloadSpeed.setText("Download Speed: " + formatFileSize(bigDecimal.doubleValue()));
                        history.setDownload(formatFileSize(bigDecimal.doubleValue()));
                    });

                    lastPosition = position;
                }
            });

            String url = "http://" + dns.getIp();
            if (dns.getNetwork().equals("4G")) {
                url += "1GB.zip";
            } else {
                url += "10GB.zip";
            }

            speedTestSocket.startFixedDownload(url, 10000);

            return null;
        }
    }

    private void checkUploadSpeed() {
        new Handler().postDelayed(() -> new UploadSpeedTestTask().execute(), 2500);
    }

    public class UploadSpeedTestTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {

            SpeedTestSocket speedTestSocket = new SpeedTestSocket();

            // add a listener to wait for speedtest completion and progress
            speedTestSocket.addSpeedTestListener(new ISpeedTestListener() {

                @Override
                public void onCompletion(SpeedTestReport report) {
                    position = 0;
                    lastPosition = 0;

                    RotateAnimation rotateAnimation;
                    rotateAnimation = new RotateAnimation(lastPosition, position, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                    rotateAnimation.setInterpolator(new LinearInterpolator());
                    rotateAnimation.setDuration(2000);
                    barImage.startAnimation(rotateAnimation);

                    databaseHelper.addHistory(history);
                }

                @Override
                public void onError(SpeedTestError speedTestError, String errorMessage) {
                    // called when a download/upload error occur
                }

                @Override
                public void onProgress(float percent, SpeedTestReport report) {
                    // called to notify download/upload progress
                    Log.v("speedtest", "[PROGRESS] progress : " + percent + "%");
                    Log.v("speedtest", "[PROGRESS] rate in octet/s : " + report.getTransferRateOctet());
                    Log.v("speedtest", "[PROGRESS] rate in bit/s   : " + report.getTransferRateBit());
                    java.math.BigDecimal bigDecimal = new java.math.BigDecimal("" + report.getTransferRateBit());
                    float finalDownload = (bigDecimal.longValue() / 1000000);
                    position = getPositionByRate(finalDownload);

                    runOnUiThread(() -> {
                        RotateAnimation rotateAnimation;
                        rotateAnimation = new RotateAnimation(lastPosition, position, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                        rotateAnimation.setInterpolator(new LinearInterpolator());
                        rotateAnimation.setDuration(1000);
                        barImage.startAnimation(rotateAnimation);
                        uploadSpeed.setText("Upload Speed: " + formatFileSize(bigDecimal.doubleValue()));
                        history.setUpload(formatFileSize(bigDecimal.doubleValue()));
                    });

                    lastPosition = position;
                }
            });

            speedTestSocket.startFixedUpload("http://" + dns.getIp() + "/", dns.getNetwork().equals("4G") ? 100000000 : 1000000000, 10000);

            return null;
        }
    }

    void setPingResult(long pingtime) {
        this.timeofping = pingtime;
        history.setPing(pingtime + "");
        txtPing.setText("Ping: " + pingtime + "ms");
    }

    void setLatencyResult(String latency) {
        int jitter = (int) Double.parseDouble(latency);
        history.setJitter(jitter + "");
        txtLatency.setText("Jitter: " + jitter + "ms");
    }

    void setLostResult(String lost) {
        txtLoss.setText("Loss: " + lost);
        history.setLoss(lost + "");

        new SpeedTestTask().execute();
    }

    public Runnable ping = () -> {
        Runtime runtime = Runtime.getRuntime();
        try {
            long a = System.currentTimeMillis() % 1000;
            Process ipProcess = runtime.exec("/system/bin/ping -c 2 " + dns.getIp());

            BufferedReader in = new BufferedReader(new InputStreamReader(ipProcess.getInputStream()));
            String inputLine;
            String latencyResult = null;
            while ((inputLine = in.readLine()) != null) {
                latencyResult = inputLine;

                if (inputLine.contains("packet loss")) {
                    int i = inputLine.indexOf("received");
                    int j = inputLine.indexOf("%");
                    lost = inputLine.substring(i + 10, j + 1);
                }
                Log.d("latencty", inputLine);
            }
            String[] keyValue = latencyResult.split("=");
            String[] value = keyValue[1].split("/");
            latency = value[1];

            ipProcess.waitFor();
            timeofping = System.currentTimeMillis() % 1000 - a;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        runOnUiThread(new Runnable() {
            public void run() {
                setPingResult(timeofping);
                setLatencyResult(latency);
                setLostResult(lost);
            }
        });
    };

    public String formatFileSize(double size) {
        Log.d("speed", size + "");
        String hrSize;

        double bytes = size;

        if (dns.getNetwork().equals("5G")) {
            bytes /= 8.0;
        }

        double k = bytes / 1024.0;
        double m = k / 1024.0;
        double g = m / 1024.0;
        double t = g / 1024.0;

        DecimalFormat dec = new DecimalFormat("0.00");

        if (t > 1) {
            hrSize = dec.format(t).concat(" ");
        } else if (g > 1) {
            hrSize = dns.getNetwork().equals("4G") ? dec.format(g).concat(" Gb/s") : dec.format(g).concat(" Gb/s");
        } else if (m > 1) {
            hrSize = dns.getNetwork().equals("4G") ? dec.format(m).concat(" Mb/s") : dec.format(m).concat(" MB/s");
        } else if (k > 1) {
            hrSize = dns.getNetwork().equals("4G") ? dec.format(k).concat(" Kb/s") : dec.format(k).concat(" KB/s");
        } else {
            hrSize = dec.format(bytes);
        }

        return hrSize;
    }

    public int getPositionByRate(float rate) {
        if (dns.getNetwork().equals("4G")) {
            if (rate <= 1) {
                return (int) (rate * 30);

            } else if (rate <= 10) {
                return (int) (rate * 6) + 30;

            } else if (rate <= 30) {
                return (int) ((rate - 10) * 3) + 90;

            } else if (rate <= 50) {
                return (int) ((rate - 30) * 1.5) + 150;

            } else if (rate <= 100) {
                return (int) ((rate - 50) * 1.2) + 180;
            }
        } else {
            if (rate <= 10) {
                return 15;
            } else if (rate <= 20) {
                return 30;
            } else if (rate <= 30) {
                return 45;
            } else if (rate <= 40) {
                return 60;
            } else if (rate <= 50) {
                return 75;
            } else if (rate <= 60) {
                return 90;
            } else if (rate <= 70) {
                return 105;
            } else if (rate <= 80) {
                return 120;
            } else if (rate <= 90) {
                return 135;
            } else if (rate <= 100) {
                return 150;
            } else if (rate <= 110) {
                return 165;
            } else if (rate <= 120) {
                return 180;
            } else if (rate <= 130) {
                return 195;
            } else if (rate <= 140) {
                return 210;
            }
        }

        return 0;
    }
}