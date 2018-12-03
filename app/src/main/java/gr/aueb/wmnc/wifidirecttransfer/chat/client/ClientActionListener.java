package gr.aueb.wmnc.wifidirecttransfer.chat.client;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import java.io.IOException;
import java.io.ObjectOutputStream;

import gr.aueb.wmnc.wifidirecttransfer.R;
import gr.aueb.wmnc.wifidirecttransfer.chat.MemberData;
import gr.aueb.wmnc.wifidirecttransfer.chat.Message;
import gr.aueb.wmnc.wifidirecttransfer.chat.MessageAdapter;

public class ClientActionListener
{
    private ObjectOutputStream out;
    private ImageButton send;
    private EditText chat;
    private View view;
    //private ListView messages;
    private MemberData memberData;
    private MessageAdapter adapter;
    private Message message;

    public ClientActionListener(ObjectOutputStream out, View view, MemberData memberData, MessageAdapter adapter)
    {
        this.out = out;
        this.view = view;
        this.send = (ImageButton) view.findViewById(R.id.sendf);
        this.chat = (EditText) view.findViewById(R.id.chat_box);
        //this.messages = (ListView) view.findViewById(R.id.chat_view);
        this.memberData = memberData;
        this.adapter = adapter;
        loop();
    }

    public void loop(){
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String temp = chat.getText().toString();
                try{
                    message = new Message(temp, memberData, false);
                    out.writeObject(message);
                    out.flush();
                    chat.getText().clear();
                }
                catch (IOException e){
                    e.printStackTrace();
                }
                message.setOur(true);
                adapter.add(message);
            }
        });
    }
}
