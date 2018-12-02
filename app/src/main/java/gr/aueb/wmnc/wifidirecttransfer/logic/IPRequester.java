package gr.aueb.wmnc.wifidirecttransfer.logic;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import gr.aueb.wmnc.wifidirecttransfer.connections.phonesIps;
import gr.aueb.wmnc.wifidirecttransfer.connections.postConnectionIps;

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
            // create the socket and initialize the streams
            String ip = strings[0].substring(1);
            socket = new Socket(ip, 4200);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            client = (String)in.readObject();
            out.writeObject(ip);
            out.flush();
            server = ip;
            System.out.println("Host");
            System.out.println("Server: " + server);
            System.out.println("Client: " + client);
            if(out != null || in != null || socket != null){
                out.close();
                in.close();
                socket.close();
            }
        }
        catch (IOException|ClassNotFoundException e){
            e.printStackTrace();
        }finally
        {
            /*try{
                if(out != null && in != null &&socket != null) {
                    out.close();
                    in.close();
                    socket.close();
                    completed = true;
                }
            }
            catch (IOException e){
                e.printStackTrace();
            }*/
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
