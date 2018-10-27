package gr.aueb.wmnc.wifidirecttransfer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class WiFiDirectBR extends BroadcastReceiver {

    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private MainActivity mActivity;
    private List<WifiP2pDevice> peers = new ArrayList<WifiP2pDevice>();
    private String[] deviceNames;
    private WifiP2pDevice[] devices;
    private int state;

    public WiFiDirectBR(WifiP2pManager mManager, WifiP2pManager.Channel mChannel, MainActivity mainActivity){
        this.mManager = mManager;
        this.mChannel = mChannel;
        this.mActivity = mainActivity;
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
                System.out.println("Here");
                mManager.requestPeers(mChannel, mActivity.peerList);
            }

        }
        else if(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)){
            if(mManager == null){
                return;
            }
            NetworkInfo info = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
            if(info.isConnected()){
                mManager.requestConnectionInfo(mChannel, mActivity.connectionInfoListener);
            }
            else{
                Toast.makeText(context, "Disconnected", Toast.LENGTH_SHORT).show();
            }
        }
        else if(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)){

        }
    }

    public int getState(){
        return state;
    }

}