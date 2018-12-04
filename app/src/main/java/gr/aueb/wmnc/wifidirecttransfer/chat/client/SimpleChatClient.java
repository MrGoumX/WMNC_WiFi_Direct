package gr.aueb.wmnc.wifidirecttransfer.chat.client;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import java.net.Socket;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.util.Random;

import gr.aueb.wmnc.wifidirecttransfer.R;
import gr.aueb.wmnc.wifidirecttransfer.chat.MemberData;
import gr.aueb.wmnc.wifidirecttransfer.chat.Message;
import gr.aueb.wmnc.wifidirecttransfer.chat.MessageAdapter;
import gr.aueb.wmnc.wifidirecttransfer.chat.SendMessage;

public class SimpleChatClient extends AsyncTask<Object, Void, Void>
{
    private static Socket csocket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private View view;
    private String name;
    private String ip;
    private MessageAdapter adapter;
    private Message d;
    private Activity mActivity;

    @Override
    protected Void doInBackground(Object... objects) {
        adapter = (MessageAdapter) objects[0];
        mActivity = (Activity) objects[1];
        name = (String) objects[2];
        ip = (String) objects[3];
        view = mActivity.getCurrentFocus();
        final ImageButton send = (ImageButton) view.findViewById(R.id.send);
        final EditText chat = (EditText) view.findViewById(R.id.chat_box);
        final MemberData memberData = new MemberData(name, Color.generateColor(new Random()));
        try {
            csocket = new Socket(ip, 4203);
            out = new ObjectOutputStream(csocket.getOutputStream());
            in = new ObjectInputStream(csocket.getInputStream());
            send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String temp = chat.getText().toString();
                    final Message message = new Message(temp, memberData, false);
                    SendMessage sM = new SendMessage();
                    sM.execute(out, message);
                    synchronized (adapter){
                        message.setOur(true);
                        mActivity.runOnUiThread(new Runnable() {
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
                    Handler handler = new Handler();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.add(d);
                                    adapter.notifyDataSetChanged();
                                }
                            });
                        }
                    });
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}