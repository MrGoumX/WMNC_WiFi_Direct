package gr.aueb.wmnc.wifidirecttransfer;

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

import gr.aueb.wmnc.wifidirecttransfer.wifidirect.WiFiDirectReceiver;

public class InfoFrag extends Fragment {

    private Menu menu;
    private MenuInflater menuInflater;
    private String type;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.info_frag, container, false);
        setHasOptionsMenu(true);
        type = getArguments().getString("connected");
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        this.menu = menu;
        if(WiFiDirectReceiver.connected){
            addItemsToUI();
        }
        else{
            removeItemFromUI();
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(WiFiDirectReceiver.connected){
            if(id == R.id.cancel){
                ((DrawerMain)getActivity()).getSettingsFrag().cancelConnection();
                removeItemFromUI();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void addItemsToUI(){
        menu.findItem(R.id.cancel).setVisible(true);
        menu.findItem(R.id.cancel).setEnabled(true);
        menu.findItem(R.id.con_status).setVisible(true);
        menu.findItem(R.id.con_status).setTitle(type);
    }

    public void removeItemFromUI(){
        menu.findItem(R.id.cancel).setVisible(false);
        menu.findItem(R.id.cancel).setEnabled(false);
        menu.findItem(R.id.con_status).setVisible(false);
        menu.findItem(R.id.con_status).setTitle("");
    }
}
