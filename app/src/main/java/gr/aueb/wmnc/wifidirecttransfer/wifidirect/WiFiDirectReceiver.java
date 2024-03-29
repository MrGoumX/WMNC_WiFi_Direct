package gr.aueb.wmnc.wifidirecttransfer.wifidirect;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gr.aueb.wmnc.wifidirecttransfer.DrawerMain;
import gr.aueb.wmnc.wifidirecttransfer.R;
import gr.aueb.wmnc.wifidirecttransfer.logic.IPGiver;
import gr.aueb.wmnc.wifidirecttransfer.logic.IPRequester;
import gr.aueb.wmnc.wifidirecttransfer.connections.phonesIps;
import gr.aueb.wmnc.wifidirecttransfer.connections.postConnectionIps;

import static android.os.Looper.getMainLooper;

public class WiFiDirectReceiver extends BroadcastReceiver implements postConnectionIps{

    private IntentFilter intentFilter, intentFilter2, intentFilter3;
    private WifiP2pManager.Channel mChannel;
    private WifiP2pManager mManager;
    private WifiManager wifiManager;
    private Activity mActivity;
    private Menu menu;
    private int state;
    private List<WifiP2pDevice> peers;
    private List<WifiP2pDevice> services;
    private List<String> serviceDeviceNamesL;
    private Map<String, String> availableServices;
    private String[] deviceNames;
    private String[] serviceDeviceNames;
    private WifiP2pDevice[] devices;
    private WifiP2pDevice[] serviceDevices;
    private phonesIps phoneIps;
    private WiFiDirectReceiver thisClass = this;
    private ListView listView, listView2;
    private boolean isOwner = false;
    private static boolean isInitialized = false;
    private InetAddress owner;
    private WifiP2pDnsSdServiceInfo serviceInfo;

    private static WiFiDirectReceiver instance = null;
    public static boolean connected = false;
    public static boolean hasService = false;
    public static boolean isHost = false;
    public static String type = "";

    public static WiFiDirectReceiver getInstance()
    {
        if (instance == null)
            instance = new WiFiDirectReceiver();
        return instance;
    }

    private WiFiDirectReceiver()
    {

    }

    public void initialize(Activity activity)
    {
        if(!isInitialized){
            this.mActivity = activity;
            intentFilter = new IntentFilter();
            intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
            intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
            intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
            intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

            intentFilter2 = new IntentFilter();
            intentFilter2.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
            intentFilter2.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
            intentFilter2.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

            intentFilter3 = new IntentFilter();
            intentFilter3.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
            intentFilter3.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

            mManager = (WifiP2pManager) mActivity.getSystemService(Context.WIFI_P2P_SERVICE);
            mChannel = mManager.initialize(mActivity, getMainLooper(), null);
            wifiManager = (WifiManager) mActivity.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            peers = new ArrayList<>();
            services = new ArrayList<>();
            availableServices = new HashMap<String, String>();
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
            }
            else{
                connected = false;
                type = "";
            }
        }
        else if(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)){

        }
    }

    private WifiP2pManager.ConnectionInfoListener connectionInfoListener = new WifiP2pManager.ConnectionInfoListener() {
        @SuppressLint("RestrictedApi")
        @Override
        public void onConnectionInfoAvailable(WifiP2pInfo info) {
        owner = info.groupOwnerAddress;
        isOwner = info.isGroupOwner;
        if(!connected){
            if(info.isGroupOwner) {
                type = "Host";
                isHost = true;
                if(!hasService){
                    IPGiver listenServer = new IPGiver();
                    listenServer.bind = thisClass;
                    listenServer.execute();
                }
            }
            else {
                type = "Guest";
                IPRequester requester = new IPRequester();
                requester.bind = thisClass;
                requester.execute(info.groupOwnerAddress.toString());
            }
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
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(mActivity.getApplicationContext(), R.layout.simple_list_item_1, deviceNames);
            }
            if(peers.size() == 0){
                Toast.makeText(mActivity.getApplicationContext(), "No devices found", Toast.LENGTH_SHORT).show();
                return;
            }
        }
    };

    private WifiP2pManager.DnsSdTxtRecordListener txtRecordListener = new WifiP2pManager.DnsSdTxtRecordListener() {
        @Override
        public void onDnsSdTxtRecordAvailable(String s, Map<String, String> map, WifiP2pDevice wifiP2pDevice) {
            availableServices.put(wifiP2pDevice.deviceAddress, map.get("name"));
        }
    };

    private WifiP2pManager.DnsSdServiceResponseListener serviceResponseListener = new WifiP2pManager.DnsSdServiceResponseListener() {
        @Override
        public void onDnsSdServiceAvailable(String s, String s1, WifiP2pDevice wifiP2pDevice) {
            wifiP2pDevice.deviceName = availableServices.get(wifiP2pDevice.deviceAddress);
            services.clear();
            if(!services.contains(wifiP2pDevice)){
                services.add(wifiP2pDevice);
                serviceDeviceNames = new String[services.size()];
                serviceDevices = new WifiP2pDevice[services.size()];
                int k = 0;
                for(WifiP2pDevice i : services){
                    serviceDeviceNames[k] = i.deviceName;
                    serviceDevices[k] = i;
                    k++;
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(mActivity.getApplicationContext(), R.layout.simple_list_item_1, serviceDeviceNames);
                listView2.setAdapter(adapter);
            }
            if(services.size() == 0){
                Toast.makeText(mActivity.getApplicationContext(), "No services found", Toast.LENGTH_SHORT).show();
            }
        }
    };

    public void startService(){
        String currentName = android.os.Build.MODEL;
        Map record = new HashMap();
        record.put("listenport", "4200");
        record.put("name", "WMNC " + currentName);
        record.put("available", "visible");
        mManager.createGroup(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onFailure(int reason) {

            }
        });
        serviceInfo = WifiP2pDnsSdServiceInfo.newInstance("WMNC" + currentName, "_presence._tcp", record);
        mManager.addLocalService(mChannel, serviceInfo, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(mActivity.getApplicationContext(), "Created Service Successfully", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int i) {
                Toast.makeText(mActivity.getApplicationContext(), "Service Creation Failed", Toast.LENGTH_SHORT).show();
            }
        });
        IPGiver listenSever = new IPGiver();
        listenSever.bind = this;
        listenSever.execute();
        hasService = true;
    }

    public void destroyService(){
        mManager.clearLocalServices(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(int i) {

            }
        });
        mManager.removeLocalService(mChannel, serviceInfo, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(mActivity.getApplicationContext(), "Service Destroyed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int i) {
                Toast.makeText(mActivity.getApplicationContext(), "Service Destruction Failed", Toast.LENGTH_SHORT).show();
            }
        });
        hasService = false;
        connected = false;
    }

    public void onResume(){
        mActivity.registerReceiver(this, intentFilter);
    }

    public void onResumeService(){
        mActivity.registerReceiver(this, intentFilter2);
    }

    public void onResumeFragments(){
        mActivity.registerReceiver(this, intentFilter3);
    }

    public void onPause(){
        mActivity.unregisterReceiver(this);
    }

    public WifiP2pManager getManager(){
        return mManager;
    }

    public void discover(final ListView listView, final ListView listView2){
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
        WifiP2pDnsSdServiceRequest serviceRequest = WifiP2pDnsSdServiceRequest.newInstance();
        mManager.addServiceRequest(mChannel, serviceRequest, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(int i) {

            }
        });
        mManager.setDnsSdResponseListeners(mChannel, serviceResponseListener, txtRecordListener);
        mManager.discoverServices(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(mActivity.getApplicationContext(), "Service Discovery Started", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int i) {
                Toast.makeText(mActivity.getApplicationContext(), "Service Discovery Failed", Toast.LENGTH_SHORT).show();
            }
        });
        this.listView = listView;
        this.listView2 = listView2;
    }

    public void disconnect(){
        if(connected && isInitialized){
            mManager.removeGroup(mChannel, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {
                    Toast.makeText(mActivity.getApplicationContext(), "Disconnect Successful", Toast.LENGTH_SHORT).show();
                    connected = false;
                    type = "";
                    ((DrawerMain)mActivity).setPhonesIps(null);
                }

                @Override
                public void onFailure(int i) {
                    Toast.makeText(mActivity.getApplicationContext(), "Disconnect Failed", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void select(int pos){
        final WifiP2pDevice device = devices[pos];
        final WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = device.deviceAddress;
        config.wps.setup = WpsInfo.PBC;
        config.groupOwnerIntent = 15;
        mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(mActivity.getApplicationContext(), "Connected to " + device.deviceName, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int reason) {
                Toast.makeText(mActivity.getApplicationContext(), "Connection failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void selectService(int pos){
        final WifiP2pDevice device = serviceDevices[pos];
        final String serviceName = serviceDeviceNames[pos];
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = device.deviceAddress;
        config.wps.setup = WpsInfo.PBC;
        config.groupOwnerIntent = 15;
        mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(mActivity.getApplicationContext(), "Connected to Service " + serviceName, Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFailure(int i) {
                Toast.makeText(mActivity.getApplicationContext(), "Connection to Service Failed", Toast.LENGTH_SHORT).show();
            }
        });
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
        else{
            ((DrawerMain)mActivity).setPhonesIps(phonesIps);
        }
    }

    public boolean isOwner() {
        if(connected){
            return isOwner;
        }
        return Boolean.parseBoolean(null);
    }

    public void destroy(){
        if(connected){
            disconnect();
        }
        if(hasService){
            destroyService();
        }
    }

    public void setMenu(Menu menu){
        this.menu = menu;
    }

}