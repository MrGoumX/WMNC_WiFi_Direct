package gr.aueb.wmnc.wifidirecttransfer.filetrans;

import android.os.AsyncTask;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Sense extends AsyncTask<Object, Void, Void> {
    private ServerSocket serverSocket;
    private Socket socket;

    @Override
    protected Void doInBackground(Object... objects) {
        try{
            serverSocket = new ServerSocket(4201);
            socket = serverSocket.accept();
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            while(true){
                String temp = (String) in.readObject();
                if(temp == null){
                    continue;
                }
                else if(temp.equals("SEND_FILE")){
                    Receive receive = new Receive();
                    receive.execute();
                }
                else if(temp.equals("EXIT")){
                    break;
                }
            }
            out.close();
            in.close();
            socket.close();
            serverSocket.close();
        }
        catch (IOException e){
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
