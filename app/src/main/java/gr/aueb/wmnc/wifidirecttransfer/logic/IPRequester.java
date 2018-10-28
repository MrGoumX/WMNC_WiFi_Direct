package gr.aueb.wmnc.wifidirecttransfer.logic;

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

import gr.aueb.wmnc.wifidirecttransfer.phonesIps;
import gr.aueb.wmnc.wifidirecttransfer.postConnectionIps;

public class IPRequester extends AsyncTask<String, Void, phonesIps> {

    public postConnectionIps bind = null;
    private Socket socket;
    private String server, client;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private phonesIps ips;

    @Override
    protected phonesIps doInBackground(String... strings) {

        if (strings.length == 0)
            return null;
        try{
            System.out.println(strings[0].substring(1));
            // create the socket and initialize the streams
            socket = new Socket(strings[0].substring(1), 4200);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            // send the GroupOwner ip to initialize the protocol
            out.writeObject(server);
            out.flush();
            // receive our ip address
            client = (String)in.readObject();
            client = client.substring(1);
        }
        catch (IOException|ClassNotFoundException e){
            e.printStackTrace();
        }finally
        {
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
        }

        ips = new phonesIps(socket.getInetAddress().toString(), client);
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
