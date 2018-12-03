package gr.aueb.wmnc.wifidirecttransfer.chat.client;

import android.os.AsyncTask;
import java.io.ObjectInputStream;

import gr.aueb.wmnc.wifidirecttransfer.chat.Message;
import gr.aueb.wmnc.wifidirecttransfer.chat.MessageAdapter;

class ServerController extends AsyncTask<Void, Void, Void>
{
    private ObjectInputStream in;
    private MessageAdapter adapter;

    public ServerController(ObjectInputStream in, MessageAdapter adapter)
    {
        this.in = in;
        this.adapter = adapter;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try
        {
            Message data;
            while ((data = (Message) in.readObject()) != null)
            {
                synchronized (adapter)
                {
                    adapter.add(data);
                }
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        } finally
        {
            System.out.println("The client will now exit");
        }
        return null;
    }
}