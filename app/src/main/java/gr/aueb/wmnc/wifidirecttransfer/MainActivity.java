package gr.aueb.wmnc.wifidirecttransfer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private IntentFilter intentFilter;
    private WifiP2pManager.Channel mChannel;
    private WifiP2pManager mManager;
    private WifiManager wifiManager;
    private BroadcastReceiver mReceiver;
    private List<WifiP2pDevice> peers;
    private String[] deviceNames;
    private WifiP2pDevice[] devices;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.devices);

        intentFilter = new IntentFilter();

        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        mReceiver = new WiFiDirectBR(mManager, mChannel, this);

        peers = new ArrayList<>();

        restOfAction();
    }

    public void restOfAction(){
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final WifiP2pDevice device = devices[position];
                WifiP2pConfig config = new WifiP2pConfig();
                config.deviceAddress = device.deviceAddress;

                mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(getApplicationContext(), "Connected to " + device.deviceName, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int reason) {
                        Toast.makeText(getApplicationContext(), "Connection falied", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.refresh:
                mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(getApplicationContext(), "Discovery Started", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int reason) {
                        Toast.makeText(getApplicationContext(), "Discovery Failed", Toast.LENGTH_SHORT).show();
                    }
                });

                break;

            case R.id.wifi:

                if(wifiManager.isWifiEnabled()){
                    wifiManager.setWifiEnabled(false);
                    Toast.makeText(getApplicationContext(), "WiFi: Disabled", Toast.LENGTH_SHORT).show();
                }
                else{
                    wifiManager.setWifiEnabled(true);
                    Toast.makeText(getApplicationContext(), "WiFi: Enabled", Toast.LENGTH_SHORT).show();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /*@Override
    public void onReceive(Context context, Intent intent){
        String action = intent.getAction();

    }*/

    @Override
    protected void onResume(){
        super.onResume();
        registerReceiver(mReceiver, intentFilter);
    }

    @Override
    public void onPause(){
        super.onPause();
        unregisterReceiver(mReceiver);
    }

    WifiP2pManager.PeerListListener peerList = new WifiP2pManager.PeerListListener(){

        @Override
        public void onPeersAvailable(WifiP2pDeviceList peersL) {
            if(!peersL.getDeviceList().equals(peers)){
                peers.clear();
                peers.addAll(peersL.getDeviceList());
                deviceNames = new String[peersL.getDeviceList().size()];
                devices = new WifiP2pDevice[peersL.getDeviceList().size()];
                int k = 0;
                for(WifiP2pDevice i : peersL.getDeviceList()){
                    deviceNames[k] = i.deviceName;
                    devices[k] = i;
                    k++;
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, deviceNames);
                listView.setAdapter(adapter);
            }
            if(peers.size() == 0){
                Toast.makeText(getApplicationContext(), "No devices found", Toast.LENGTH_SHORT).show();
            }
        }
    };

    WifiP2pManager.ConnectionInfoListener connectionInfoListener = new WifiP2pManager.ConnectionInfoListener() {
        @Override
        public void onConnectionInfoAvailable(WifiP2pInfo info) {
            final InetAddress owner = info.groupOwnerAddress;
            MenuItem con = (MenuItem)findViewById(R.id.con_status);
            if(info.groupFormed && info.isGroupOwner) {
                con.setTitle("Host");
            }
            else {
                con.setTitle("Client");
            }
        }
    };

}
