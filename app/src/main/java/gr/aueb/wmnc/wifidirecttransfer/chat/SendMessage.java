package gr.aueb.wmnc.wifidirecttransfer.chat;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.ObjectOutputStream;

import gr.aueb.wmnc.wifidirecttransfer.chat.Message;

public class SendMessage extends AsyncTask<Object, Void, Message> {

    @Override
    protected Message doInBackground(Object... objects) {
        System.out.println("EXECUTED");
        ObjectOutputStream out = (ObjectOutputStream) objects[0];
        try {
            out.writeObject(objects[1]);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return (Message) objects[1];
    }

}
