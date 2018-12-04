package gr.aueb.wmnc.wifidirecttransfer.chat.client;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

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
import gr.aueb.wmnc.wifidirecttransfer.connections.transportMessage;
import gr.aueb.wmnc.wifidirecttransfer.fragments.ChatFrag;

public class SimpleChatClient extends AsyncTask<Object, Void, Void>
{
    private static Socket csocket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private ClientActionListener listener;
    private ImageButton send;
    private EditText chat;
    private ListView cv;
    private View view;
    private String name;
    private String ip;
    private MessageAdapter adapter;
    private SimpleChatClient thisClass;
    private Message d;
    private Activity act;

    @Override
    protected Void doInBackground(Object... objects) {
        try {
            csocket = new Socket(ip, 4203);
            out = new ObjectOutputStream(csocket.getOutputStream());
            in = new ObjectInputStream(csocket.getInputStream());
            send = (ImageButton) view.findViewById(R.id.send);
            chat = (EditText) view.findViewById(R.id.chat_box);
            adapter = (MessageAdapter) objects[1];
            act = (Activity) objects[2];
            final MemberData memberData = new MemberData((String) objects[0], Color.generateColor(new Random()));
            send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String temp = chat.getText().toString();
                    final Message message = new Message(temp, memberData, false);
                    SendMessage sM = new SendMessage();
                    sM.execute(out, message);
                    synchronized (adapter){
                        message.setOur(true);
                        act.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.add(message);
                                adapter.notifyDataSetChanged();
                            }
                        });
                        chat.getText().clear();
                    }
                }
            });
            while((d = (Message) in.readObject()) != null){
                synchronized (adapter){
                    System.out.println(d.getMessage());
                    act.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.add(d);
                            adapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        } catch (IOException | ClassNotFoundException e) {
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


}