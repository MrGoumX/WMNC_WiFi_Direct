package gr.aueb.wmnc.wifidirecttransfer.chat.client;

import android.os.AsyncTask;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import java.io.IOException;
import java.io.ObjectOutputStream;

import gr.aueb.wmnc.wifidirecttransfer.R;
import gr.aueb.wmnc.wifidirecttransfer.chat.MemberData;
import gr.aueb.wmnc.wifidirecttransfer.chat.Message;
import gr.aueb.wmnc.wifidirecttransfer.chat.MessageAdapter;

public class ClientActionListener extends AsyncTask<Object, Void, Void>
{
    private ObjectOutputStream out;
    private Message message;

    public ClientActionListener(ObjectOutputStream out)
    {
        this.out = out;
    }

    @Override
    protected Void doInBackground(Object... objects) {
        try {
            out.writeObject(objects[0]);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /*public void run(){
        try {
            out.writeObject(message);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void send(Message message){
        this.message = message;
    }*/
}
