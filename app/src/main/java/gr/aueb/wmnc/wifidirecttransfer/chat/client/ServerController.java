package gr.aueb.wmnc.wifidirecttransfer.chat.client;

import android.os.AsyncTask;
import java.io.ObjectInputStream;
import java.net.Socket;

import gr.aueb.wmnc.wifidirecttransfer.chat.Message;
import gr.aueb.wmnc.wifidirecttransfer.chat.MessageAdapter;

public class ServerController extends AsyncTask<Void, Void, Void>
{
    private Socket socket;
    private ObjectInputStream in;
    private MessageAdapter adapter;

    public ServerController(Socket socket, MessageAdapter adapter)
    {
        this.socket = socket;
        this.adapter = adapter;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try
        {
            /*Message data;
            while ((data = (Message) in.readObject()) != null)
            {
                adapter.add(data);
                System.out.println(data.getMessage());
            }*/
            while(true){
                in = new ObjectInputStream(socket.getInputStream());
                Message d = (Message) in.readObject();
                if(d != null){
                    adapter.add(d);
                    System.out.println(d.getMessage());
                }
                else{
                    continue;
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