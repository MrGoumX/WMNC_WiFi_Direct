package gr.aueb.wmnc.wifidirecttransfer.chat.server;

import java.io.IOException;
import java.util.HashSet;

public class ConnectionSet
{
    private HashSet<ConnectionController> connections;

    public ConnectionSet()
    {
        connections = new HashSet<ConnectionController>();
    }

    public synchronized int size()
    {
        return connections.size();
    }

    public synchronized void remove(ConnectionController con)
    {
        if (connections.contains(con))
        {
            System.out.println("Before: " + connections.size());
            connections.remove(con);
            System.out.println("After: " + connections.size());
        }
    }

    public synchronized boolean add(ConnectionController con)
    {
        return connections.add(con);
    }

    public synchronized void broadcast(String msg)
    {
        String[] data = msg.split(":");
        for (ConnectionController con : connections)
        {
            if (!con.getClientName().equals(data[0]))
            {
                try
                {
                    con.send(msg);
                } catch (IOException ioe)
                {
                    ioe.printStackTrace();
                }
            }
        }
    }
}