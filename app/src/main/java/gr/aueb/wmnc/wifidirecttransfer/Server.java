package gr.aueb.wmnc.wifidirecttransfer;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends AsyncTask<Void, Void, phonesIps> {

    public postConnectionIps bind = null;
    private ServerSocket serverSocket;
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private String serverIp, clientIp;
    private phonesIps ips;

    @Override
    protected phonesIps doInBackground(Void... voids) {
        try{
            ServerSocket server = new ServerSocket(4200);
            Socket socket = server.accept();
            serverIp = socket.getInetAddress().toString();
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            clientIp = (String)in.readObject();
            clientIp = clientIp.substring(1);
            out.writeObject(serverIp);
            out.flush();
        }
        catch (IOException|ClassNotFoundException e){
            e.printStackTrace();
        }
        try{
            if(out != null && in != null && socket != null && serverSocket != null){
                out.close();
                in.close();
                socket.close();
                serverSocket.close();
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
        ips = new phonesIps(serverIp, clientIp);
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
