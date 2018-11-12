package gr.aueb.wmnc.wifidirecttransfer;

import android.view.Menu;

import gr.aueb.wmnc.wifidirecttransfer.wifidirect.WiFiDirectReceiver;

public class UIUpdater {

    public static void updateUI(Menu menu, String type){
        if(WiFiDirectReceiver.connected){
            menu.findItem(R.id.con_status).setVisible(true);
            menu.findItem(R.id.con_status).setTitle(type);
            menu.findItem(R.id.cancel).setEnabled(true);
            menu.findItem(R.id.cancel).setVisible(true);
        }
        else{
            menu.findItem(R.id.con_status).setVisible(false);
            menu.findItem(R.id.con_status).setTitle(type);
            menu.findItem(R.id.cancel).setEnabled(false);
            menu.findItem(R.id.cancel).setVisible(false);
        }
    }
}
