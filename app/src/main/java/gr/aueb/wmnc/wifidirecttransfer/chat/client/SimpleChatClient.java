package gr.aueb.wmnc.wifidirecttransfer.chat.client;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import java.net.Socket;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Random;

import gr.aueb.wmnc.wifidirecttransfer.R;
import gr.aueb.wmnc.wifidirecttransfer.chat.MemberData;
import gr.aueb.wmnc.wifidirecttransfer.chat.Message;
import gr.aueb.wmnc.wifidirecttransfer.chat.MessageAdapter;

public class SimpleChatClient extends AsyncTask<Void, Void, Void>
{
    private static Socket csocket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private ClientActionListener listener;
    private ImageButton send;
    private EditText chat;
    private View view;
    private String name;
    private String ip;

    @Override
    protected Void doInBackground(Void... voids) {
        try{
            csocket = new Socket(ip, 4203);
            System.out.println("CONNECTED");
            out = new ObjectOutputStream(csocket.getOutputStream());
            in = new ObjectInputStream(csocket.getInputStream());
            out.writeObject(name);
            out.flush();
            System.out.println("here");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        final MessageAdapter adapter = new MessageAdapter(view.getContext());
        final MemberData memberData = new MemberData(name, generateColor(new Random()));

        final ServerController inputHandler = new ServerController(in, adapter);
        inputHandler.start();
        //inputHandler.execute();
        //listener = new ClientActionListener(out);
        send = (ImageButton) view.findViewById(R.id.send);
        chat = (EditText) view.findViewById(R.id.chat_box);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String temp = chat.getText().toString();
                final Message message = new Message(temp, memberData, false);
                listener = new ClientActionListener(out);
                listener.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, message);
                //listener.execute(message);
                //listener.send(message);
                /*listener.start();
                try {
                    listener.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }*/
                chat.getText().clear();
                message.setOur(true);
                adapter.add(message);
            }
        });
        try {
            inputHandler.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
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

}