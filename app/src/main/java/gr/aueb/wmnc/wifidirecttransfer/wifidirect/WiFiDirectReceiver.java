package gr.aueb.wmnc.wifidirecttransfer.wifidirect;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import gr.aueb.wmnc.wifidirecttransfer.R;
import gr.aueb.wmnc.wifidirecttransfer.SettingsFrag;
import gr.aueb.wmnc.wifidirecttransfer.logic.IPGiver;
import gr.aueb.wmnc.wifidirecttransfer.logic.IPRequester;
import gr.aueb.wmnc.wifidirecttransfer.onConnectionInfo;
import gr.aueb.wmnc.wifidirecttransfer.phonesIps;
import gr.aueb.wmnc.wifidirecttransfer.postConnectionIps;
import gr.aueb.wmnc.wifidirecttransfer.onConnectionInfo;

import static android.os.Looper.getMainLooper;

public class WiFiDirectReceiver extends BroadcastReceiver implements postConnectionIps{

    private IntentFilter intentFilter;
    private WifiP2pManager.Channel mChannel;
    private WifiP2pManager mManager;
    private WifiManager wifiManager;
    private Activity mActivity;
    private SettingsFrag settingsFrag;
    private int state;
    private List<WifiP2pDevice> peers;
    private String[] deviceNames;
    private WifiP2pDevice[] devices;
    private String type;
    private phonesIps phoneIps;
    private ArrayAdapter<String> adapter;
    private WiFiDirectReceiver thisClass = this;
    private ListView listView;
    private static boolean isInitialized = false;

    private static WiFiDirectReceiver instance = null;
    public static boolean connected = false;

    public static WiFiDirectReceiver getInstance()
    {
        if (instance == null)
            instance = new WiFiDirectReceiver();
        return instance;
    }

    private WiFiDirectReceiver()
    {

    }

    public void initialize(Activity activity, SettingsFrag settingsFrag)
    {
        if(!isInitialized){
            this.mActivity = activity;
            this.settingsFrag = settingsFrag;
            intentFilter = new IntentFilter();
            intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
            intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
            intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
            intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
            mManager = (WifiP2pManager) mActivity.getSystemService(Context.WIFI_P2P_SERVICE);
            mChannel = mManager.initialize(mActivity, getMainLooper(), null);
            wifiManager = (WifiManager) mActivity.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            peers = new ArrayList<>();
            isInitialized = true;
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)){
            state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if(state == WifiP2pManager.WIFI_P2P_STATE_ENABLED){
                Toast.makeText(context, "WiFi Direct: Enabled", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(context, "WiFi Direct: Disabled", Toast.LENGTH_SHORT).show();
            }
        }
        else if(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)){
            if(mManager != null){
                mManager.requestPeers(mChannel, peerListListener);
            }

        }
        else if(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)){
            if(mManager == null){
                return;
            }
            NetworkInfo info = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
            if(info.isConnected()){
                mManager.requestConnectionInfo(mChannel, connectionInfoListener);
                settingsFrag.addItemsToUI();
            }
            else{
                settingsFrag.removeItemsFromUI();
            }
        }
        else if(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)){

        }
    }

    private WifiP2pManager.ConnectionInfoListener connectionInfoListener = new WifiP2pManager.ConnectionInfoListener() {
        @SuppressLint("RestrictedApi")
        @Override
        public void onConnectionInfoAvailable(WifiP2pInfo info) {
        final InetAddress owner = info.groupOwnerAddress;
        if(!connected){
            if(info.groupFormed && info.isGroupOwner) {
                type = "Host";
                IPGiver server = new IPGiver();
                server.bind = thisClass;
                server.execute();
            }
            else {
                type = "Guest";
                IPRequester client = new IPRequester();
                client.bind = thisClass;
                client.execute(owner.toString());
            }
            settingsFrag.addItemsToUI();
            connected = true;
        }
        }
    };

    private WifiP2pManager.PeerListListener peerListListener = new WifiP2pManager.PeerListListener(){
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
            adapter = new ArrayAdapter<String>(mActivity.getApplicationContext(), R.layout.simple_list_item_1, deviceNames);
            listView.setAdapter(adapter);
        }
        if(peers.size() == 0){
            Toast.makeText(mActivity.getApplicationContext(), "No devices found", Toast.LENGTH_SHORT).show();
            return;
        }
        }
    };



    public void onResume(){
        mActivity.registerReceiver(this, intentFilter);
    }

    public void onPause(){
        mActivity.unregisterReceiver(this);
    }

    public WifiP2pManager getManager(){
        return mManager;
    }

    public void discover(ListView listView){
        mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {

                Toast.makeText(mActivity.getApplicationContext(), "Discovery Started", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFailure(int reason) {
                Toast.makeText(mActivity.getApplicationContext(), "Discovery Failed", Toast.LENGTH_SHORT).show();
            }
        });
        this.listView = listView;
    }

    public void disconnect(){
        if(connected){
            mManager.removeGroup(mChannel, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {
                    Toast.makeText(mActivity.getApplicationContext(), "Disconnect Successful", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(int i) {
                    Toast.makeText(mActivity.getApplicationContext(), "Disconnect Failed", Toast.LENGTH_SHORT).show();
                }
            });
            connected = false;
        }
    }

    public void select(int pos){
        final WifiP2pDevice device = devices[pos];
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = device.deviceAddress;
        mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(mActivity.getApplicationContext(), "Connected to " + device.deviceName, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int reason) {
                Toast.makeText(mActivity.getApplicationContext(), "Connection falied", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public ArrayAdapter<String> getAdapter(){
        return adapter;
    }

    public String getType(){
        return type;
    }

    public phonesIps getPhoneIps(){
        return phoneIps;
    }

    @Override
    public void getIps(phonesIps phonesIps) {
        this.phoneIps = phonesIps;
        if(this.phoneIps == null){
            Toast.makeText(mActivity.getApplicationContext(), "Error: ", Toast.LENGTH_SHORT).show();
        }
    }
}