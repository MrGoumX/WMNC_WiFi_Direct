package gr.aueb.wmnc.wifidirecttransfer.wifidirect;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.widget.Toast;

import gr.aueb.wmnc.wifidirecttransfer.SettingsFrag;

public class WiFiDirectReceiver extends BroadcastReceiver {

    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private Activity mActivity;
    private SettingsFrag fragment;
    private int state;

    public WiFiDirectReceiver(WifiP2pManager mManager, WifiP2pManager.Channel mChannel, Activity mainActivity, SettingsFrag fragment){
        this.mManager = mManager;
        this.mChannel = mChannel;
        this.mActivity = mainActivity;
        this.fragment = fragment;
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
                mManager.requestPeers(mChannel, fragment.getPeerListListener());
            }

        }
        else if(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)){
            if(mManager == null){
                return;
            }
            NetworkInfo info = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
            if(info.isConnected()){
                mManager.requestConnectionInfo(mChannel, fragment.getConnectionInfoListener());
                fragment.addItemsToUI();
            }
            else{
                fragment.removeItemsFromUI();
            }
        }
        else if(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)){

        }
    }

    public int getState(){
        return state;
    }

}