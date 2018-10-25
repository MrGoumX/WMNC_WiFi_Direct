package gr.aueb.wmnc.wifidirecttransfer;

import android.os.AsyncTask;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends AsyncTask<String, Void, Void> {

    private ServerSocket serverSocket;
    private Socket socket;

    @Override
    protected Void doInBackground(String... strings) {
        try{
            serverSocket = new ServerSocket(4200);
            socket = serverSocket.accept();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
