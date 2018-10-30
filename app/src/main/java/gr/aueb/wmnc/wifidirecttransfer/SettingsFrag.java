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
import java.util.Set;

import gr.aueb.wmnc.wifidirecttransfer.logic.IPGiver;
import gr.aueb.wmnc.wifidirecttransfer.logic.IPRequester;
import gr.aueb.wmnc.wifidirecttransfer.wifidirect.WiFiDirectReceiver;

import static android.os.Looper.getMainLooper;

public class SettingsFrag extends Fragment{

    private WiFiDirectReceiver wiFiDirectReceiver;
    private ListView listView;
    private View view;
    private Menu menu;
    private phonesIps phonesIps;
    private String type;
    private Activity activity;
    private boolean connected = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.settings_frag, container, false);

        listView = (ListView)view.findViewById(R.id.device_list);

        wiFiDirectReceiver = WiFiDirectReceiver.getInstance();
        wiFiDirectReceiver.initialize(this.getActivity(), this);

        this.activity = getActivity();

        setHasOptionsMenu(true);

        restOfAction();

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.findItem(R.id.refresh).setVisible(true);
        menu.findItem(R.id.refresh).setEnabled(true);
        if(WiFiDirectReceiver.connected){
            addItemsToUI();
        }
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
            cancelConnection();
        }
        return super.onOptionsItemSelected(item);
    }

    private void restOfAction() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, View view, int position, long id) {
                wiFiDirectReceiver.select(position);
                if(WiFiDirectReceiver.connected){
                    phonesIps = wiFiDirectReceiver.getPhoneIps();
                }
            }
        });

        discover();
    }

    @Override
    public void onResume() {
        super.onResume();
        wiFiDirectReceiver.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        wiFiDirectReceiver.onPause();
    }

    private void discover(){
        wiFiDirectReceiver.discover(listView);
        /*ArrayAdapter<String> adapter = wiFiDirectReceiver.getAdapter();
        listView.setAdapter(adapter);*/
    }

    /*public void passInfo(String what, phonesIps phonesIps){
        info.onConnectionInfo(what, phonesIps);
    }*/

    public void addItemsToUI(){
        menu.findItem(R.id.con_status).setTitle(wiFiDirectReceiver.getType());
        menu.findItem(R.id.con_status).setVisible(true);
        menu.findItem(R.id.cancel).setVisible(true);
        menu.findItem(R.id.cancel).setEnabled(true);
    }

    public void removeItemsFromUI(){
        menu.findItem(R.id.con_status).setTitle("");
        menu.findItem(R.id.con_status).setVisible(false);
        menu.findItem(R.id.cancel).setVisible(false);
        menu.findItem(R.id.cancel).setEnabled(false);
    }

    public void cancelConnection(){
        wiFiDirectReceiver.disconnect();
    }

    public phonesIps getPhonesIps(){
        return wiFiDirectReceiver.getPhoneIps();
    }

    public String getType(){
        return wiFiDirectReceiver.getType();
    }
}
