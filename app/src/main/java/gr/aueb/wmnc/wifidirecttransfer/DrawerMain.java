package gr.aueb.wmnc.wifidirecttransfer;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.design.widget.NavigationView;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import gr.aueb.wmnc.wifidirecttransfer.chat.client.Color;
import gr.aueb.wmnc.wifidirecttransfer.chat.server.SimpleChatServer;
import gr.aueb.wmnc.wifidirecttransfer.connections.phonesIps;
import gr.aueb.wmnc.wifidirecttransfer.filetrans.FileNotification;
import gr.aueb.wmnc.wifidirecttransfer.fragments.ChatFrag;
import gr.aueb.wmnc.wifidirecttransfer.fragments.FileTransFrag;
import gr.aueb.wmnc.wifidirecttransfer.fragments.InfoFrag;
import gr.aueb.wmnc.wifidirecttransfer.fragments.PersonFrag;
import gr.aueb.wmnc.wifidirecttransfer.fragments.ServiceFrag;
import gr.aueb.wmnc.wifidirecttransfer.fragments.SettingsFrag;
import gr.aueb.wmnc.wifidirecttransfer.ui.UIService;
import gr.aueb.wmnc.wifidirecttransfer.ui.UIUpdater;
import gr.aueb.wmnc.wifidirecttransfer.wifidirect.WiFiDirectReceiver;

public class DrawerMain extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private WifiManager wifiManager;
    private gr.aueb.wmnc.wifidirecttransfer.connections.phonesIps phonesIps;
    private InfoFrag infoFrag;
    private SettingsFrag settingsFrag;
    private ServiceFrag serviceFrag;
    private ChatFrag chatFrag;
    private FileTransFrag fileTransFrag;
    private UIService uiService;
    private WiFiDirectReceiver wiFiDirectReceiver;
    private MenuItem wifi;
    public static Menu menu;

    private static final int ACCEPTED_PERMISSIONS = 0; // Necessary for Android wifi scanning & for writing files to external storage

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer_main);

        String[] perms = new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(perms,
                        ACCEPTED_PERMISSIONS);
            }
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        infoFrag = new InfoFrag();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment, infoFrag).commit();

        wifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        settingsFrag = new SettingsFrag();
        serviceFrag = new ServiceFrag();
        chatFrag = new ChatFrag();
        fileTransFrag = new FileTransFrag();
        uiService = new UIService();

        wiFiDirectReceiver = WiFiDirectReceiver.getInstance();
        wiFiDirectReceiver.initialize(this);

        FileNotification fileNotification = new FileNotification(this);

        startService(new Intent(this, UIService.class));
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.drawer_main, menu);
        this.menu = menu;
        wifi = menu.findItem(R.id.wifi);

        if(wifiManager.isWifiEnabled()){
            wifi.setIcon(R.drawable.ic_wifi_white_24dp);
        }
        else{
            wifi.setIcon(R.drawable.wifi);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.wifi) {
            if(wifiManager.isWifiEnabled()){
                wifiManager.setWifiEnabled(false);
                Toast.makeText(getApplicationContext(), "WiFi: Disabled", Toast.LENGTH_SHORT).show();
                wifi.setIcon(R.drawable.wifi);
            }
            else{
                wifiManager.setWifiEnabled(true);
                Toast.makeText(getApplicationContext(), "WiFi: Enabled", Toast.LENGTH_SHORT).show();
                wifi.setIcon(R.drawable.ic_wifi_white_24dp);
            }
        }
        else if(id == R.id.cancel){
            try{
                wiFiDirectReceiver.destroy();
                unregisterReceiver(wiFiDirectReceiver);
                finish();
                startActivity(getIntent());
            }catch (Exception e){
                Toast.makeText(getApplicationContext(), "Error: Refresh failed." + e, Toast.LENGTH_SHORT).show();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_home) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment, infoFrag).commit();
        } else if (id == R.id.nav_trans) {
            if(WiFiDirectReceiver.connected && WiFiDirectReceiver.getInstance().getPhoneIps() != null){
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment, fileTransFrag).commit();
            }
            else{
                Toast.makeText(getApplicationContext(), "Not connected yet", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.nav_chat) {
            if(WiFiDirectReceiver.connected && WiFiDirectReceiver.getInstance().getPhoneIps() != null){
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment, chatFrag).commit();
            }
            else{
                Toast.makeText(getApplicationContext(), "Not connected yet", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.nav_settings) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment, settingsFrag).commit();
        } else if(id == R.id.nav_services){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment, serviceFrag).commit();
        } else if (id == R.id.nav_person1) {
            PersonFrag personFrag = new PersonFrag();
            Bundle person = new Bundle();
            person.putString("person", "person1");
            personFrag.setArguments(person);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment, personFrag).commit();
        } else if (id == R.id.nav_person2) {
            PersonFrag personFrag = new PersonFrag();
            Bundle person = new Bundle();
            person.putString("person", "person2");
            personFrag.setArguments(person);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment, personFrag).commit();
        } else if(id == R.id.nav_person3){
            PersonFrag personFrag = new PersonFrag();
            Bundle person = new Bundle();
            person.putString("person", "person3");
            personFrag.setArguments(person);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment, personFrag).commit();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public WiFiDirectReceiver getWiFiDirectReceiver() {
        return wiFiDirectReceiver;
    }

    public InfoFrag getInfoFrag() {
        return infoFrag;
    }

    public void setPhonesIps(phonesIps phonesIps){
        this.phonesIps = phonesIps;
    }
}
