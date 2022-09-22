package com.example.network88;


import android.net.ConnectivityManager;
import android.net.DhcpInfo;

import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.network88.databinding.ActivityMainBinding;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;

import fr.bmartel.speedtest.SpeedTestReport;
import fr.bmartel.speedtest.SpeedTestSocket;
import fr.bmartel.speedtest.inter.ISpeedTestListener;
import fr.bmartel.speedtest.model.SpeedTestError;

public class MainActivity extends AppCompatActivity {
    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    public String ping;
    public String ToastMbsDownload;
    public String ToastMbsUpload;

    @RequiresApi(api = Build.VERSION_CODES.M)

    @Override
    protected void onCreate(Bundle savedInstanceState) { //On startup.
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); //Fullscreen mode.
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (isOnline()) {
            Toast.makeText(MainActivity.this.getApplicationContext(),
                    "Connection found.", Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(MainActivity.this.getApplicationContext(),
                    "No connection found. Connect your device and try again later.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public class SpeedTestTaskDownload extends AsyncTask<Void, Void, String> {

        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(Void... params) {

            SpeedTestSocket speedTestSocket = new SpeedTestSocket();

            // add a listener to wait for speedtest completion and progress
            speedTestSocket.addSpeedTestListener(new ISpeedTestListener() {

                @Override
                public void onCompletion(SpeedTestReport report) {
                    // called when download/upload is finished
                    BigDecimal bit = report.getTransferRateBit();

                    double Mbit = Double.parseDouble(String.valueOf(bit)) / 1000000;
                    double MbitFinalCompleted = BigDecimal.valueOf(Mbit).setScale(2, RoundingMode.HALF_UP).doubleValue();

                    Log.v("downloadCompleted", "completed rate in Mbit/s: " + MbitFinalCompleted);
                }

                @Override
                public void onError(SpeedTestError speedTestError, String errorMessage) {
                    // called when a download/upload error occur
                }

                @Override
                public void onProgress(float percent, SpeedTestReport report) {
                    // called to notify download/upload progress
                    BigDecimal bit = report.getTransferRateBit();

                    double Mbit = Double.parseDouble(String.valueOf(bit)) / 1000000;
                    double MbitFinalProgress = BigDecimal.valueOf(Mbit).setScale(2, RoundingMode.HALF_UP).doubleValue();

                    Log.v("progressDownload", "progress: " + percent + "%");
                    Log.v("currentSpeedDownload", "rate in Mbit/s: " + MbitFinalProgress);
                    ToastMbsDownload = String.valueOf(MbitFinalProgress);
                }
            });

            speedTestSocket.startFixedDownload("http://ipv4.ikoula.testdebit.info/50M.iso", 5000);

            return null;
        }
    }
    public class SpeedTestTaskUpload extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {

            SpeedTestSocket speedTestSocket = new SpeedTestSocket();

            // add a listener to wait for speedtest completion and progress
            speedTestSocket.addSpeedTestListener(new ISpeedTestListener() {

                @Override
                public void onCompletion(SpeedTestReport report) {
                    // called when download/upload is finished
                    BigDecimal bit = report.getTransferRateBit();

                    double Mbit = Double.parseDouble(String.valueOf(bit)) / 1000000;
                    double MbitFinalCompleted = BigDecimal.valueOf(Mbit).setScale(2, RoundingMode.HALF_UP).doubleValue();

                    Log.v("uploadCompleted", "completed rate in Mbit/s: " + MbitFinalCompleted);
                }

                @Override
                public void onError(SpeedTestError speedTestError, String errorMessage) {
                    // called when a download/upload error occur
                }

                @Override
                public void onProgress(float percent, SpeedTestReport report) {
                    // called to notify download/upload progress
                    BigDecimal bit = report.getTransferRateBit();

                    double Mbit = Double.parseDouble(String.valueOf(bit)) / 1000000;
                    double MbitFinalProgress = BigDecimal.valueOf(Mbit).setScale(2, RoundingMode.HALF_UP).doubleValue();

                    Log.v("progressUpload", "progress: " + percent + "%");
                    Log.v("currentSpeedUpload", "rate in Mbit/s: " + MbitFinalProgress);
                    ToastMbsUpload = String.valueOf(MbitFinalProgress);
                }
            });

            speedTestSocket.startFixedUpload("http://ipv4.ikoula.testdebit.info/", 10000000, 5000);

            return null;
        }
    }

    public boolean isOnline() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    public void runTest(View v) {
        Button runTestButton = (Button) v; //casting
        runTestButton.setText("Press to run your test again");

        getDownloadSpeed();
        getUploadspeed();

        wait(3500);

        getSpecificInfo();
        displayNetworkParameters();

        /*Toast.makeText(MainActivity.this.getApplicationContext(),
                ("Your download speed is " + ToastMbsDownload + "Mbs, your upload speed is " + ToastMbsUpload + "Mbs"), Toast.LENGTH_LONG).show();*/
    }

    public void getSpecificInfo() {
        TextView textViewIP = findViewById(R.id.ip);
        textViewIP.setText("IP Address:\n" + getIpAddress());

        TextView textViewMask = findViewById(R.id.mask);
        textViewMask.setText("Subnet mask:\n" + getSubnetMask());

        TextView textViewPing = findViewById(R.id.ping);
        ping = ping("google.com");
        textViewPing.setText("Pinging google.com\n= " + ping);
    }

    public void  displayNetworkParameters() {
        TextView textViewDownload = findViewById(R.id.textView_Download);
        textViewDownload.setText("Download: \n\n" + "   " + ToastMbsDownload);

        TextView textViewUpload = findViewById(R.id.textView_Upload);
        textViewUpload.setText("Upload: \n\n" + "   " + ToastMbsUpload);
    }

    public void getDownloadSpeed() {

        new SpeedTestTaskDownload().execute();
    }

    public void getUploadspeed(){

        new SpeedTestTaskUpload().execute();
    }

    public String getIpAddress() {
        WifiManager wifiMgr = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        String ipAddress = Formatter.formatIpAddress(wifiMgr.getConnectionInfo().getIpAddress());
        return ipAddress;
    }

    public String getSubnetMask() {
        WifiManager wifiMgr = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        DhcpInfo dhcp = wifiMgr.getDhcpInfo();
        String mask = intToIP(dhcp.netmask);
        return mask;
    }

    public String ping(String url) {
        String str = "";
        try {
            Process process = Runtime.getRuntime().exec(
                    "/system/bin/ping -c 1 " + url); //A library in Android, where PING command can be found.
            new InputStreamReader(process.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    process.getInputStream()));
            int i;
            char[] buffer = new char[4096];
            StringBuffer output = new StringBuffer();
            while ((i = reader.read(buffer)) > 0)
                output.append(buffer, 95, 146);
            reader.close();
            str = output.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return str;
    }

    private static String intToIP(int ipAddress) {
        return String.format("%d.%d.%d.%d", (ipAddress & 0xff), (ipAddress >> 8 & 0xff), (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff));
    } //Bitwise AND operation, Your subnet mask is ip address 192.168.232.2 gateway..., 0xff = 11111111...

    public static void wait(int ms) //One may use it to delay next actions
    {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }
}