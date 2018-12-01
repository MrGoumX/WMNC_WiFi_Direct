package gr.aueb.wmnc.wifidirecttransfer;

import android.os.AsyncTask;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ListenServer extends AsyncTask<Void, Void, Void> {

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            ServerSocket server = new ServerSocket(4200);
            Socket socket = server.accept();
            System.out.println("Accepted");
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            out.writeDouble(2.0);
            out.flush();
        } catch (IOException e) {

        }
        return null;
    }
}
