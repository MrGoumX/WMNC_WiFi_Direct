package gr.aueb.wmnc.wifidirecttransfer;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import gr.aueb.wmnc.wifidirecttransfer.connections.phonesIps;
import gr.aueb.wmnc.wifidirecttransfer.fragments.ChatFrag;
import gr.aueb.wmnc.wifidirecttransfer.fragments.FileTransFrag;
import gr.aueb.wmnc.wifidirecttransfer.fragments.InfoFrag;
import gr.aueb.wmnc.wifidirecttransfer.fragments.PersonFrag;
import gr.aueb.wmnc.wifidirecttransfer.fragments.ServiceFrag;
import gr.aueb.wmnc.wifidirecttransfer.fragments.SettingsFrag;
import gr.aueb.wmnc.wifidirecttransfer.ui.UIUpdater;
import gr.aueb.wmnc.wifidirecttransfer.wifidirect.WiFiDirectReceiver;

public class DrawerMain extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private WifiManager wifiManager;
    private String type;
    private gr.aueb.wmnc.wifidirecttransfer.connections.phonesIps phonesIps;
    private InfoFrag infoFrag;
    private SettingsFrag settingsFrag;
    private ServiceFrag serviceFrag;
    private ChatFrag chatFrag;
    private FileTransFrag fileTransFrag;
    private WiFiDirectReceiver wiFiDirectReceiver;
    protected Menu menu;
    private static final int PERMISSION_ACCESS_COARSE_LOCATION = 0; // It is necessary for device scanning
                                                                    // after Android version 7 and on
    private static final int PERMSSION_WRITE_EXTERNAL_STORAGE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer_main);

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        PERMISSION_ACCESS_COARSE_LOCATION);
            }
        }

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        PERMSSION_WRITE_EXTERNAL_STORAGE);
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

        wiFiDirectReceiver = WiFiDirectReceiver.getInstance();
        wiFiDirectReceiver.initialize(this);


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
                UIUpdater.updateUI(menu, WiFiDirectReceiver.type);
            }
            else{
                wifiManager.setWifiEnabled(true);
                Toast.makeText(getApplicationContext(), "WiFi: Enabled", Toast.LENGTH_SHORT).show();
            }
        }
        else if(id == R.id.cancel){
            if(WiFiDirectReceiver.connected){
                settingsFrag.cancelConnection();
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
            if(WiFiDirectReceiver.connected){
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment, fileTransFrag).commit();
            }
            else{
                Toast.makeText(getApplicationContext(), "Not connected yet", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.nav_chat) {
            if(WiFiDirectReceiver.connected){
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
        UIUpdater.updateUI(menu, WiFiDirectReceiver.type);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public WiFiDirectReceiver getWiFiDirectReceiver() {
        return wiFiDirectReceiver;
    }

    public void setPhonesIps(phonesIps phonesIps){
        this.phonesIps = phonesIps;
    }
}
