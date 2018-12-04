package gr.aueb.wmnc.wifidirecttransfer.chat.server;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.CollapsibleActionView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

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
import gr.aueb.wmnc.wifidirecttransfer.chat.client.SendMessage;
import gr.aueb.wmnc.wifidirecttransfer.connections.transportMessage;
import gr.aueb.wmnc.wifidirecttransfer.filetrans.Receive;
import gr.aueb.wmnc.wifidirecttransfer.fragments.ChatFrag;

public class SimpleChatServer extends AsyncTask<Object, Void, Void>{

    private ServerSocket serverSocket;
    private Socket socket;
    private View view;
    private ObjectInputStream in;
    private ObjectOutputStream out ;
    private SimpleChatServer thisClass;
    private MessageAdapter adapter;
    private Message d;
    private Activity act;

    @Override
    protected Void doInBackground(Object... objects) {
        try{
            serverSocket = new ServerSocket(4203);
            this.view = (View) objects[0];
            final EditText chat = (EditText) view.findViewById(R.id.chat_box);
            ImageButton send = (ImageButton) view.findViewById(R.id.send);
            final MemberData memberData = new MemberData((String) objects[1], Color.generateColor(new Random()));
            adapter = (MessageAdapter) objects[2];
            act = (Activity) objects[3];
            while(true){
                socket = serverSocket.accept();
                try{
                    out = new ObjectOutputStream(socket.getOutputStream());
                    in = new ObjectInputStream(socket.getInputStream());
                    //String clientName = (String) in.readObject();
                    send.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String temp = chat.getText().toString();
                            final Message message = new Message(temp, memberData, false);
                            SendMessage sM = new SendMessage();
                            sM.execute(out, message);
                            synchronized (adapter){
                                message.setOur(true);
                                /*cf.getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        adapter.add(message);
                                    }
                                });*/
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
                }
                catch (IOException | ClassNotFoundException e){
                    e.printStackTrace();
                }
                /*ConnectionController controller = new ConnectionController(socket, adapter);
                controller.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);*/
            }

        }
        catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

}