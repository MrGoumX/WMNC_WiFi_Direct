package gr.aueb.wmnc.wifidirecttransfer.logic;

import android.os.AsyncTask;
import android.util.Log;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import gr.aueb.wmnc.wifidirecttransfer.connections.phonesIps;
import gr.aueb.wmnc.wifidirecttransfer.connections.postConnectionIps;

public class IPGiver extends AsyncTask<Void, Void, phonesIps> {

    public postConnectionIps bind = null;
    private ServerSocket server;
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private String serverIp, myIp;
    private phonesIps ips;

    @Override
    protected phonesIps doInBackground(Void... voids) {
        try{
            // create the socket and initialize the streams
            server = new ServerSocket(4200);
            socket = server.accept();
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            out.writeObject(socket.getInetAddress().toString().substring(1));
            out.flush();
            myIp = (String) in.readObject();
            serverIp = socket.getInetAddress().toString().substring(1);
            out.close();
            in.close();
            socket.close();
            server.close();
        }
        catch (IOException|ClassNotFoundException e)
        {
            e.printStackTrace();
        }

        ips = new phonesIps(myIp, serverIp);
        return ips;
    }

    @Override
    protected void onPostExecute(phonesIps phonesIps) {
        if(bind == null){
            Log.e("postConnectionIps", "No fragment bound to this task");
        }
        else{
            bind.getIps(phonesIps);
        }
    }
}
