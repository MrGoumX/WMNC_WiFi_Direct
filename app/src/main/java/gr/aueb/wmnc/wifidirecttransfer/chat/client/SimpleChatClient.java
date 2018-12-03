package gr.aueb.wmnc.wifidirecttransfer.chat.client;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.View;

import java.net.Socket;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Random;

import gr.aueb.wmnc.wifidirecttransfer.chat.MemberData;
import gr.aueb.wmnc.wifidirecttransfer.chat.MessageAdapter;

public class SimpleChatClient extends Service
{
    private Socket csocket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private ClientActionListener listener;
    private View view;
    private String name;
    private String ip;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    public void initiate(){
        try{
            csocket = new Socket(ip, 4203);
            out = new ObjectOutputStream(csocket.getOutputStream());
            in = new ObjectInputStream(csocket.getInputStream());
            out.writeObject(name);
            out.flush();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        MessageAdapter adapter = new MessageAdapter(getApplicationContext());
        MemberData memberData = new MemberData(name, generateColor(new Random()));

        ServerController inputHandler = new ServerController(in, adapter);
        inputHandler.execute();
        listener = new ClientActionListener(out, view, memberData, adapter);
    }

    public void setName(String name){
        this.name = name;
    }

    public void connectToIp(String ip){
        this.ip = ip;
    }

    public void setView(View view){
        this.view = view;
    }

    private static String generateColor(Random r) {
        final char [] hex = { '0', '1', '2', '3', '4', '5', '6', '7',
                '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
        char [] s = new char[7];
        int     n = r.nextInt(0x1000000);

        s[0] = '#';
        for (int i=1;i<7;i++) {
            s[i] = hex[n & 0xf];
            n >>= 4;
        }
        return new String(s);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}