package gr.aueb.wmnc.wifidirecttransfer.chat.client;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import java.io.IOException;
import java.io.ObjectOutputStream;

import gr.aueb.wmnc.wifidirecttransfer.chat.MemberData;
import gr.aueb.wmnc.wifidirecttransfer.chat.Message;
import gr.aueb.wmnc.wifidirecttransfer.chat.MessageAdapter;

public class ClientActionListener
{
    private ObjectOutputStream out;
    private ImageButton send;
    private EditText chat;
    private ListView messages;
    private MemberData memberData;
    private MessageAdapter adapter;
    private Message message;

    public ClientActionListener(ObjectOutputStream out, ImageButton send, EditText chat, ListView messages, MemberData memberData, MessageAdapter adapter)
    {
        this.out = out;
        this.send = send;
        this.chat = chat;
        this.messages = messages;
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
