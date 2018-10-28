package gr.aueb.wmnc.wifidirecttransfer.logic;

import android.os.AsyncTask;
import android.util.Log;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import gr.aueb.wmnc.wifidirecttransfer.phonesIps;
import gr.aueb.wmnc.wifidirecttransfer.postConnectionIps;

public class IPGiver extends AsyncTask<Void, Void, phonesIps> {

    public postConnectionIps bind = null;
    private ServerSocket server;
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private String clientIp, myIp;
    private phonesIps ips;

    @Override
    protected phonesIps doInBackground(Void... voids) {
        try{
            // create the socket and initialize the streams
            server = new ServerSocket(4200);
            socket = server.accept();
            clientIp = socket.getInetAddress().toString();
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            myIp = (String)in.readObject();
            // send his ip back to him
            out.writeObject(clientIp);
            out.flush();
        }
        catch (IOException|ClassNotFoundException e)
        {
            e.printStackTrace();
        }finally
        {
            try{
                if(out != null && in != null && socket != null && server != null){
                    out.close();
                    in.close();
                    socket.close();
                    server.close();
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }

        ips = new phonesIps(clientIp, myIp);
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