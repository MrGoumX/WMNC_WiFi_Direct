package gr.aueb.wmnc.wifidirecttransfer;

import android.os.AsyncTask;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

public class Client extends AsyncTask<String, Void, String> {

    private Socket socket;
    private String ip, server;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    @Override
    protected String doInBackground(String... strings) {
        ip = strings[0].substring(1);
        try{
            System.out.println(ip);
            socket = new Socket(ip, 4200);
            server = socket.getInetAddress().toString();
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            out.writeObject(server);
            out.flush();
            String ip2 = (String)in.readObject();
            System.out.println(ip2);
        }
        catch (IOException|ClassNotFoundException e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onPostExecute(String ret){
        ret = ip;
    }
}
