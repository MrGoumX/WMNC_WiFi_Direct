package gr.aueb.wmnc.wifidirecttransfer.chat.client;

import android.os.AsyncTask;
import java.io.ObjectInputStream;
import java.net.Socket;

import gr.aueb.wmnc.wifidirecttransfer.chat.Message;
import gr.aueb.wmnc.wifidirecttransfer.chat.MessageAdapter;

public class ServerController extends Thread
{
    private ObjectInputStream in;
    private MessageAdapter adapter;

    public ServerController(ObjectInputStream in, MessageAdapter adapter)
    {
        this.in = in;
        this.adapter = adapter;
    }

    public void run(){
        try
        {
            /*Message data;
            while ((data = (Message) in.readObject()) != null)
            {
                adapter.add(data);
                System.out.println(data.getMessage());
            }*/
            while(true){
                Message d = (Message) in.readObject();
                System.out.println(d.getMessage());
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
    }
}