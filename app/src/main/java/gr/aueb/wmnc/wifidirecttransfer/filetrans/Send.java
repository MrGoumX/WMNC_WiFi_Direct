package gr.aueb.wmnc.wifidirecttransfer.filetrans;

import android.os.AsyncTask;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.Socket;

public class Send extends AsyncTask<Object, Void, Void> {
    private static final int size = 1024*1024;

    private byte[] buffer = new byte[size];
    private BufferedInputStream bfis;
    private File file;
    private String ip;
    private int port = 4300;

    @Override
    protected Void doInBackground(Object... params) {
        try {
            ip = (String) params[1];
            Socket socket = new Socket(ip, 4201);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.writeObject("SEND_FILE");
            out.flush();
            out.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String path = (String) params[0];
        System.out.println(path);
        this.file = new File(path);
        this.ip = (String) params[1];
        try{
            openFile(file);
        }catch (IOException ioException){
            ioException.printStackTrace();
        }

        return null;
    }

    private void openFile(File file) throws IOException{

        //send name of file
        String name = file.getName();
        sendString(name);

        //file.
        try {
            FileInputStream fis = new FileInputStream(file);
            bfis = new BufferedInputStream(fis);
            sendFiles();
        } catch (FileNotFoundException e) {
            System.err.println("File for transfer does'nt locate.\n\n");
            e.printStackTrace();
        }
    }

    private void sendFiles() throws IOException{
        Socket sock = new Socket(ip, port);
        OutputStream os = null;

        // read file from computer.
        while (true) {
            int i = 0;
            i = bfis.read(buffer, 0, size);
            if (i == -1) {
                break;
            }
            // write and send file to client.
            os = sock.getOutputStream();
            os.write(buffer,0,i);
            os.flush();
        }
        System.out.println("Transfer Complete");

        if (bfis != null) bfis.close();
        if (os != null) os.close();
        if (sock != null) sock.close();
    }

    private void sendString(String str) throws IOException{

        Socket sock = null;
        while(true){
            try {
                sock = new Socket(ip, port);
                if(sock != null) break;
            }
            catch (IOException e){
                try{
                    Thread.sleep(1000);
                }
                catch (Exception e1){
                    e1.printStackTrace();
                }
            }
        }
        OutputStream os = sock.getOutputStream();
        Writer sendName = new PrintWriter(os);
        sendName.write(str);
        sendName.flush();
        if (sendName != null) sendName.close();
        if (os != null) os.close();
        if (sock != null) sock.close();
    }

}
