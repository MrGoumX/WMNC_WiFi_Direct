package gr.aueb.wmnc.wifidirecttransfer;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

public class Client extends AsyncTask<String, Void, phonesIps> {

    public postConnectionIps bind = null;
    private Socket socket;
    private String ip, server, client;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private phonesIps ips;

    @Override
    protected phonesIps doInBackground(String... strings) {
        ip = strings[0].substring(1);
        try{
            System.out.println(ip);
            socket = new Socket(ip, 4200);
            server = socket.getInetAddress().toString();
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            out.writeObject(server);
            out.flush();
            client = (String)in.readObject();
            client = client.substring(1);
        }
        catch (IOException|ClassNotFoundException e){
            e.printStackTrace();
        }
        try{
            if(out != null && in != null &&socket != null) {
                out.close();
                in.close();
                socket.close();
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
        ips = new phonesIps(server, client);
        return ips;
    }

    @Override
    protected void onPostExecute(phonesIps phonesIps) {
        if (bind == null){
            Log.e("postConnectionIps", "No fragment bound to this task");
        }
        else{
            bind.getIps(phonesIps);
        }
    }
}
