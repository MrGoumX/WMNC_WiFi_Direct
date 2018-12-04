package gr.aueb.wmnc.wifidirecttransfer.chat.server;

import android.os.AsyncTask;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Objects;

import gr.aueb.wmnc.wifidirecttransfer.chat.Message;
import gr.aueb.wmnc.wifidirecttransfer.chat.MessageAdapter;

class ConnectionController extends AsyncTask<Void, Void, Void>
{
    private String clientName;
    private Socket clientSocket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private MessageAdapter adapter;


    public ConnectionController(Socket connectionSocket, MessageAdapter adapter)
    {
        this.clientSocket = connectionSocket;
        this.adapter = adapter;
    }


    @Override
    protected Void doInBackground(Void... voids) {
        try {
            out = new ObjectOutputStream(clientSocket.getOutputStream());
            in = new ObjectInputStream(clientSocket.getInputStream());
            clientName = (String) in.readObject();
            Message d;
            while((d = (Message) in.readObject()) != null){
                synchronized (adapter){
                    adapter.add(d);
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }


    public void disconnect()
    {
        try
        {
            out.close();
            in.close();
            clientSocket.close();
        } catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    public String getClientName()
    {
        return clientName;
    }

    public Socket getClientSocket()
    {
        return clientSocket;
    }

    public void send(String msg) throws IOException
    {
        out.writeObject(msg);
        out.flush();
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConnectionController that = (ConnectionController) o;
        return Objects.equals(clientName, that.clientName);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(clientName);
    }


}