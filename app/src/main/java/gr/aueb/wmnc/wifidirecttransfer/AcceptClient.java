package gr.aueb.wmnc.wifidirecttransfer;

import android.os.AsyncTask;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Inet4Address;
import java.net.Socket;

public class AcceptClient extends AsyncTask<Object, Void, Void> {
    @Override
    protected Void doInBackground(Object... params) {
        try {
            String ip = ((Inet4Address) params[0]).toString();
            Socket socket = new Socket(ip.substring(1), 4200);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            System.out.println(in.readDouble());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
