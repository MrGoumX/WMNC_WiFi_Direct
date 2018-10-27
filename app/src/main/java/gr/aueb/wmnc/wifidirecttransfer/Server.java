package gr.aueb.wmnc.wifidirecttransfer;

import android.os.AsyncTask;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends AsyncTask<Void, Void, String> {

    private ServerSocket serverSocket;
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private String ip;

    @Override
    protected String doInBackground(Void... voids) {
        try{
            ServerSocket server = new ServerSocket(4200);
            Socket socket = server.accept();
            ip = socket.getInetAddress().toString();
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            String ip2 = (String)in.readObject();
            out.writeObject(ip);
            out.flush();
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
