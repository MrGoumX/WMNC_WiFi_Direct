package gr.aueb.wmnc.wifidirecttransfer;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class DrawerMain extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, onConnectionInfo {

    private WifiManager wifiManager;
    private String what;
    private phonesIps phonesIps;
    protected Menu menu;
    private static final int PERMISSION_ACCESS_COARSE_LOCATION = 0; // It is necessary for device scanning
                                                                    // after Android version 7 and on

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment, new InfoFrag()).commit();

        wifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
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
            }
            else{
                wifiManager.setWifiEnabled(true);
                Toast.makeText(getApplicationContext(), "WiFi: Enabled", Toast.LENGTH_SHORT).show();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        InfoFrag infoFrag = new InfoFrag();
        SettingsFrag settingsFrag = new SettingsFrag();
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment, infoFrag).commit();
        } else if (id == R.id.nav_trans) {

        } else if (id == R.id.nav_chat) {

        } else if (id == R.id.nav_settings) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment, settingsFrag).commit();
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

    @Override
    public void onConnectionInfo(String what, phonesIps phonesIps) {
        System.out.println(menu.getItem(R.id.con_status));
        this.what = what;
        this.phonesIps = phonesIps;
        if(this.what == null && this.phonesIps == null){
            Toast.makeText(getApplicationContext(), "Error: No connection established", Toast.LENGTH_SHORT).show();
        }
        else{
            menu.findItem(R.id.con_status).setVisible(true);
            menu.findItem(R.id.con_status).setTitle(this.what);
        }
    }
}
