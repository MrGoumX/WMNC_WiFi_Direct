package gr.aueb.wmnc.wifidirecttransfer.fragments;

import android.app.Activity;
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
import android.widget.ListView;

import gr.aueb.wmnc.wifidirecttransfer.DrawerMain;
import gr.aueb.wmnc.wifidirecttransfer.R;
import gr.aueb.wmnc.wifidirecttransfer.ui.UIUpdater;
import gr.aueb.wmnc.wifidirecttransfer.connections.phonesIps;
import gr.aueb.wmnc.wifidirecttransfer.wifidirect.WiFiDirectReceiver;

public class SettingsFrag extends Fragment{

    private WiFiDirectReceiver wiFiDirectReceiver;
    private ListView listView, listView2;
    private View view;
    private Menu menu;
    private gr.aueb.wmnc.wifidirecttransfer.connections.phonesIps phonesIps;
    private String type;
    private Activity activity;
    private boolean connected = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.settings_frag, container, false);

        listView = (ListView)view.findViewById(R.id.device_list);
        listView2 = (ListView)view.findViewById(R.id.service_list);

        /*wiFiDirectReceiver = WiFiDirectReceiver.getInstance();
        wiFiDirectReceiver.initialize(this.getActivity());*/

        wiFiDirectReceiver = ((DrawerMain)getActivity()).getWiFiDirectReceiver();

        this.activity = getActivity();

        setHasOptionsMenu(true);

        restOfAction();

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.findItem(R.id.refresh).setVisible(true);
        menu.findItem(R.id.refresh).setEnabled(true);
        this.menu = menu;
        wiFiDirectReceiver.setMenu(menu);
        UIUpdater.updateUI(menu, WiFiDirectReceiver.type);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if(id == R.id.refresh){
            discover();
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
        listView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                wiFiDirectReceiver.selectService(position);
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
        wiFiDirectReceiver.discover(listView, listView2);
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
