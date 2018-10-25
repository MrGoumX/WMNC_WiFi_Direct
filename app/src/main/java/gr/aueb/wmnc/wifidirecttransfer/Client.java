package gr.aueb.wmnc.wifidirecttransfer;

import android.os.AsyncTask;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

public class Client extends AsyncTask<String, Void, Void> {

    private Socket socket;
    private String ip;

    @Override
    protected Void doInBackground(String... strings) {
        ip = strings[0];
        try{
            socket = new Socket();
            socket.connect(new InetSocketAddress(ip, 4200), 500);
        }
        catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }
}
