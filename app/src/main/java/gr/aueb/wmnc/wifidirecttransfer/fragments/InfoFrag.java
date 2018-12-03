package gr.aueb.wmnc.wifidirecttransfer.fragments;

import android.os.Bundle;
import android.provider.Contacts;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.style.UpdateAppearance;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import gr.aueb.wmnc.wifidirecttransfer.R;
import gr.aueb.wmnc.wifidirecttransfer.ui.UIUpdater;
import gr.aueb.wmnc.wifidirecttransfer.wifidirect.WiFiDirectReceiver;

public class InfoFrag extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.info_frag, container, false);
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //UIUpdater.updateUI(menu, WiFiDirectReceiver.type);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onResume() {
        WiFiDirectReceiver.getInstance().onResumeFragments();
        super.onResume();
    }

    @Override
    public void onPause() {
        WiFiDirectReceiver.getInstance().onPause();
        super.onPause();
    }

}
