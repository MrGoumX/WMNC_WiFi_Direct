package gr.aueb.wmnc.wifidirecttransfer.chat.client;

import android.os.AsyncTask;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import java.io.ObjectInputStream;

import gr.aueb.wmnc.wifidirecttransfer.chat.MemberData;
import gr.aueb.wmnc.wifidirecttransfer.chat.Message;
import gr.aueb.wmnc.wifidirecttransfer.chat.MessageAdapter;

class ServerController extends AsyncTask<Void, Void, Void>
{
    private ObjectInputStream in;
    private ImageButton send;
    private EditText chat;
    private ListView messages;
    private MemberData memberData;
    private MessageAdapter adapter;

    public ServerController(ObjectInputStream in, ImageButton send, EditText chat, ListView messages, MemberData memberData, MessageAdapter adapter)
    {
        this.in = in;
        this.send = send;
        this.chat = chat;
        this.messages = messages;
        this.memberData = memberData;
        this.adapter = adapter;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try
        {
            Message data;
            while ((data = (Message) in.readObject()) != null)
            {
                synchronized (messages.getAdapter())
                {
                    adapter.add(data);
                }
            }
        } catch (Exception e)
        {
            // For debug.
            //System.out.println("Input thread exception.");
            e.printStackTrace();
        } finally
        {
            System.out.println("The client will now exit");
        }
        return null;
    }
}