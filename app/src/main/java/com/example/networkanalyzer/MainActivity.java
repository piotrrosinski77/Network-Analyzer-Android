package com.example.networkanalyzer;

import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
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

import com.example.networkanalyzer.databinding.ActivityMainBinding;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    public String ping;

    @RequiresApi(api = Build.VERSION_CODES.M)

    @Override
    protected void onCreate(Bundle savedInstanceState) { //On startup.
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); //Fullscreen mode.
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        try {
            getWifiSpeed();//Just testing, remember to delete it later!
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (isOnline()) {
            Toast.makeText(MainActivity.this.getApplicationContext(), //Is online or is not :)?
                    "Connection found.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(MainActivity.this.getApplicationContext(),
                    "No connection found. Connect your device and try again later.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
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

    public boolean isOnline() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    public void runTest(View v) {
        v.setEnabled(false);
        Button b = (Button) v; //casting
        b.setText("Testing your network connection, please wait...");
        Toast.makeText(MainActivity.this.getApplicationContext(),
                "Your internet speed is... working on that function right now :)", Toast.LENGTH_LONG).show();
    }

    public void getSpecificInfo(View info)
    {
        TextView textView = findViewById(R.id.ip);
        textView.setText("Your Device IP Address: " + getIpAddress());

        TextView textView2 = findViewById(R.id.mask);
        textView2.setText("Your subnet mask is " + getSubnetMask());

        TextView textView3 = findViewById(R.id.ping);
        ping = ping("google.com");
        textView3.setText(ping);
    }

    public void getWifiSpeed() throws java.io.IOException{
        URL downloadFileUrl100MB = new URL("http://cachefly.cachefly.net/100mb.test");
            Log.d("Start", "Used URL: http://cachefly.cachefly.net/100mb.test");//Basic info in the beginning.
            long startTime = System.currentTimeMillis(); //Starting the stopwatch (system's clock), maybe not the best method, time will show.
            HttpURLConnection connection = (HttpURLConnection) downloadFileUrl100MB.openConnection();
            connection.setRequestProperty("accept", "application/json");
            long usedTime = System.currentTimeMillis() - startTime; //Eventually we get the time used to download the file (expected to be on the end of the method).
            Log.d("Start time", "start time"+startTime);
            Log.d("Time", "used time"+usedTime );//PS work
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
                output.append(buffer, 0, i);
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
        try
        {
            Thread.sleep(ms);
        }
        catch(InterruptedException ex)
        {
            Thread.currentThread().interrupt();
        }
    }
}


