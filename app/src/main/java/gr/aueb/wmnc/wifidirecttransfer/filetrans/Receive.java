package gr.aueb.wmnc.wifidirecttransfer.filetrans;

import android.os.AsyncTask;
import android.os.Environment;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class Receive extends AsyncTask<Object, Void, Void> {
    private static final int size = 1024*1024;

    private ServerSocket server = null;
    private BufferedOutputStream bos;
    private FileOutputStream fos;
    private String storePath;
    private int port = 4200;

    @Override
    protected Void doInBackground(Object... params) {
        try {
            startServer(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        storePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
        try {
            startServer(port);
            openFile(storePath, receiveName());
            try {
                readData();
            } catch (IOException e) {
                System.err.println("Error reading data.\n\n");
                e.printStackTrace();
            }
        }
        catch(IOException e){
            e.printStackTrace();
        }
        try {
            closeServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void openFile(String path, String name){

        try{
            fos = new FileOutputStream(path + File.separator + name);
            System.out.println("File: "+ path + " is opened.");
        }catch(FileNotFoundException e){
            System.err.println("Error opening file.\n\n");
            e.printStackTrace();
        }
    }

    private void startServer(int port) throws IOException{
        server = new ServerSocket(port);
        System.out.println("Server socket is created");
    }

    private void readData() throws IOException{

        int current;
        byte buffer[] = new byte[size];
        Socket sock = server.accept();
        System.out.println("Connect to server.");
        InputStream is = sock.getInputStream();
        bos = new BufferedOutputStream(fos);
        while((current = is.read(buffer))>0){
            bos.write(buffer,0,current);
        }
        bos.flush();
        System.out.println("Transfer Complete.");

        if(fos!=null)fos.close();
        if(bos!=null)bos.close();
        if(sock!=null)sock.close();
    }

    private String receiveName()throws IOException {
        Socket sock = server.accept();
        BufferedReader receiveName = new BufferedReader(new InputStreamReader(sock.getInputStream()));
        String name = receiveName.readLine();
        receiveName.close();
        return name;
    }

    private void closeServer() throws IOException{
        server.close();
    }
}
