package gr.aueb.wmnc.wifidirecttransfer.chat.server;

import android.os.AsyncTask;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Objects;

class ConnectionController extends AsyncTask<Void, Void, Void>
{
    private String clientName;
    private Socket clientSocket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private ConnectionSet connectionSet;


    public ConnectionController(Socket connectionSocket, ConnectionSet connections)
    {
        clientSocket = connectionSocket;
        connectionSet = connections;
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

    @Override
    protected Void doInBackground(Void... voids) {
        boolean accepted = false;
        try
        {
            out = new ObjectOutputStream(clientSocket.getOutputStream());
            in = new ObjectInputStream(clientSocket.getInputStream());

            clientName = (String) in.readObject();

            accepted = connectionSet.add(this);
            if (accepted)
                System.out.println(clientName + " connected.\nNumber of total connections: " + connectionSet.size());
            else
                out.writeObject(new String("Name already exists!\nThe connection will now terminate."));

            String data;

            while (accepted && (data = (String) in.readObject()) != null)
            {
                if (data.trim().equals("/close"))
                    break;
                connectionSet.broadcast(clientName + ": " + data);
            }


        } catch (Exception e)
        {
            System.out.println(e);
        } finally

        {
            if (accepted)
            {
                connectionSet.remove(this);

            }
            disconnect();
        }
        return null;
    }
}