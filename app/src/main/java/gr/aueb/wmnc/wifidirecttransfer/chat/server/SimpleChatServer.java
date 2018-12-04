package gr.aueb.wmnc.wifidirecttransfer.chat.server;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.io.IOException;
import java.net.Socket;
import java.util.Random;

import gr.aueb.wmnc.wifidirecttransfer.R;
import gr.aueb.wmnc.wifidirecttransfer.chat.MemberData;
import gr.aueb.wmnc.wifidirecttransfer.chat.Message;
import gr.aueb.wmnc.wifidirecttransfer.chat.MessageAdapter;
import gr.aueb.wmnc.wifidirecttransfer.chat.client.Color;
import gr.aueb.wmnc.wifidirecttransfer.chat.SendMessage;

public class SimpleChatServer extends AsyncTask<Object, Void, Void>{

    private ServerSocket serverSocket;
    private Socket socket;
    private View view;
    private ObjectInputStream in;
    private ObjectOutputStream out ;
    private MessageAdapter adapter;
    private Message d;
    private Activity mActivity;
    private String name;
    public static boolean chatOnline = false;

    @Override
    protected Void doInBackground(Object... objects) {
        chatOnline = true;
        adapter = (MessageAdapter) objects[0];
        mActivity = (Activity) objects[1];
        name = (String) objects[2];
        view = mActivity.getCurrentFocus();
        final EditText chat = (EditText) view.findViewById(R.id.chat_box);
        final ImageButton send = (ImageButton) view.findViewById(R.id.send);
        final MemberData memberData = new MemberData(name, Color.generateColor(new Random()));
        try{
            serverSocket = new ServerSocket(4203);
            while(true){
                socket = serverSocket.accept();
                try{
                    out = new ObjectOutputStream(socket.getOutputStream());
                    in = new ObjectInputStream(socket.getInputStream());
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
                }
                catch (IOException | ClassNotFoundException e){
                    e.printStackTrace();
                }
            }

        }
        catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

}