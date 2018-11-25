package gr.aueb.wmnc.wifidirecttransfer.chat.server;

import android.os.AsyncTask;

import java.net.ServerSocket;
import java.io.IOException;

public class SimpleChatServer extends AsyncTask<Void, Void, Void> {

    private ServerSocket ssocket;
    private final int port = 5678;
    private ConnectionSet connections;

    @Override
    protected Void doInBackground(Void... voids) {
        connections = new ConnectionSet();
        ssocket = null;

        try {
            ssocket = new ServerSocket(port);
            System.out.println("server started.\nWaiting for connections...");
            while (true) {
                ConnectionController temp = new ConnectionController(ssocket.accept(), connections);
                temp.execute();
            }
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
        }
        finally {
            try {
                System.out.println("Closing the server socket...");
                ssocket.close();
            }
            catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
        return null;
    }
}
