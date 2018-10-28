package gr.aueb.wmnc.wifidirecttransfer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import gr.aueb.wmnc.wifidirecttransfer.logic.IPGiver;
import gr.aueb.wmnc.wifidirecttransfer.logic.IPRequester;
import gr.aueb.wmnc.wifidirecttransfer.wifidirect.WiFiDirectReceiver;

import static android.os.Looper.getMainLooper;

public class SettingsFrag extends Fragment implements postConnectionIps{

    public onConnectionInfo info;
    private IntentFilter intentFilter;
    private WifiP2pManager.Channel mChannel;
    private WifiP2pManager mManager;
    private WifiManager wifiManager;
    private BroadcastReceiver mReceiver;
    private List<WifiP2pDevice> peers;
    private String[] deviceNames;
    private WifiP2pDevice[] devices;
    private ListView listView;
    private View view;
    private Menu menu;
    private Activity parent;
    private phonesIps phonesIps;
    private SettingsFrag temp;
    private String what;
    private boolean connected = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.settings_frag, container, false);

        listView = (ListView)view.findViewById(R.id.device_list);

        setHasOptionsMenu(true);

        restOfAction();

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        info = (onConnectionInfo) context;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.findItem(R.id.refresh).setVisible(true);
        menu.findItem(R.id.refresh).setEnabled(true);
        this.menu = menu;
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if(id == R.id.refresh){
            discover();
        }
        else if(id == R.id.cancel){
            if(connected){
                mManager.removeGroup(mChannel, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(parent.getApplicationContext(), "Disconnect Successful", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int i) {
                        Toast.makeText(parent.getApplicationContext(), "Disconnect Failed", Toast.LENGTH_SHORT).show();
                    }
                });
                menu.findItem(R.id.con_status).setTitle("");
                menu.findItem(R.id.con_status).setVisible(false);
                menu.findItem(R.id.cancel).setVisible(false);
                menu.findItem(R.id.cancel).setEnabled(false);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void restOfAction() {
        temp = this;

        intentFilter = new IntentFilter();

        parent = this.getActivity();

        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        mManager = (WifiP2pManager) parent.getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(parent, getMainLooper(), null);
        //mChannel = mManager.initialize(parent.getApplicationContext(), getMainLooper(), null);
        wifiManager = (WifiManager) parent.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        mReceiver = new WiFiDirectReceiver(mManager, mChannel, parent, this);

        peers = new ArrayList<>();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, View view, int position, long id) {
                final WifiP2pDevice device = devices[position];
                WifiP2pConfig config = new WifiP2pConfig();
                config.deviceAddress = device.deviceAddress;

                mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(parent.getContext(), "Connected to " + device.deviceName, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int reason) {
                        Toast.makeText(parent.getContext(), "Connection falied", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        discover();
    }

    @Override
    public void onResume() {
        super.onResume();
        mReceiver = new WiFiDirectReceiver(mManager, mChannel, parent, this);
        parent.registerReceiver(mReceiver, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        parent.unregisterReceiver(mReceiver);
    }

    private void discover(){
        mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {

                Toast.makeText(parent.getApplicationContext(), "Discovery Started", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFailure(int reason) {
                Toast.makeText(parent.getApplicationContext(), "Discovery Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private WifiP2pManager.ConnectionInfoListener connectionInfoListener = new WifiP2pManager.ConnectionInfoListener() {
        @SuppressLint("RestrictedApi")
        @Override
        public void onConnectionInfoAvailable(WifiP2pInfo info) {
            final InetAddress owner = info.groupOwnerAddress;
            MenuItem item = menu.findItem(R.id.con_status);
            if(!connected){
                if(info.groupFormed && info.isGroupOwner) {
                    item.setTitle("Host");
                    temp.what = "Host";
                    IPGiver server = new IPGiver();
                    server.bind = temp;
                    server.execute();
                }
                else {
                    item.setTitle("Guest");
                    temp.what = "Guest";
                    IPRequester client = new IPRequester();
                    client.bind = temp;
                    client.execute(owner.toString());
                }
                connected = true;
                MenuItem cancel = menu.findItem(R.id.cancel);
                cancel.setVisible(true);
                cancel.setEnabled(true);
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
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(parent.getApplicationContext(), R.layout.simple_list_item_1, deviceNames);
                listView.setAdapter(adapter);
            }
            if(peers.size() == 0){
                Toast.makeText(parent.getApplicationContext(), "No devices found", Toast.LENGTH_SHORT).show();
                return;
            }
        }
    };

    // temporal solution, a better way is to transfer the listener to the WifiDirectReceiver class
    public WifiP2pManager.PeerListListener getPeerListListener() { return peerListListener; }
    public WifiP2pManager.ConnectionInfoListener getConnectionInfoListener() { return  connectionInfoListener; }


    public void passInfo(String what, phonesIps phonesIps){
        info.onConnectionInfo(what, phonesIps);
    }

    @Override
    public void getIps(phonesIps phonesIps) {
        this.phonesIps = phonesIps;
        if(this.phonesIps == null){
            Toast.makeText(parent.getApplicationContext(), "Error: No connection established", Toast.LENGTH_SHORT).show();
        }
        else{
            passInfo(this.what, this.phonesIps);
        }
    }
}
