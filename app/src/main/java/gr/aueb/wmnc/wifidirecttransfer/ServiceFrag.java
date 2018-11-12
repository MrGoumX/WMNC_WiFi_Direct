package gr.aueb.wmnc.wifidirecttransfer;

import android.app.Activity;
import android.net.wifi.p2p.WifiP2pConfig;
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
import android.widget.Button;

import gr.aueb.wmnc.wifidirecttransfer.wifidirect.WiFiDirectReceiver;

public class ServiceFrag extends Fragment {

    private Activity activity;
    private Menu menu;
    private WiFiDirectReceiver wiFiDirectReceiver;
    private Button serviceButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.service_frag, container, false);

        setHasOptionsMenu(true);

        wiFiDirectReceiver = WiFiDirectReceiver.getInstance();
        wiFiDirectReceiver.initialize(this.getActivity());

        serviceButton = (Button) view.findViewById(R.id.create_service);

        if(WiFiDirectReceiver.hasService){
            serviceButton.setText("Disable Service");
        }

        serviceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startService();
            }
        });
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        this.menu = menu;
        UIUpdater.updateUI(menu, WiFiDirectReceiver.type);
    }

    private void startService()
    {
        if(!WiFiDirectReceiver.hasService){
            wiFiDirectReceiver.startService();
            serviceButton.setText("Disable Service");
        }
        else{
            wiFiDirectReceiver.destroyService();
            serviceButton.setText("Enable Service");
        }
    }
}
