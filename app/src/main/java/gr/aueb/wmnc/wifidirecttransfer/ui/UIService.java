package gr.aueb.wmnc.wifidirecttransfer.ui;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.MenuInflater;

import gr.aueb.wmnc.wifidirecttransfer.DrawerMain;
import gr.aueb.wmnc.wifidirecttransfer.R;
import gr.aueb.wmnc.wifidirecttransfer.wifidirect.WiFiDirectReceiver;

public class UIService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final Handler handler = new Handler();
        final int delay = 250;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                UIUpdater.updateUI(DrawerMain.menu, WiFiDirectReceiver.type);
                handler.postDelayed(this, delay);
            }
        }, delay);
        return super.onStartCommand(intent, flags, startId);
    }

}
