package gr.aueb.wmnc.wifidirecttransfer.chat.client;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import java.net.Socket;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.util.Random;

import gr.aueb.wmnc.wifidirecttransfer.chat.MemberData;
import gr.aueb.wmnc.wifidirecttransfer.chat.MessageAdapter;

public class SimpleChatClient extends AsyncTask<Object, Void, Void>
{
    private Socket csocket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private ClientActionListener listener;
    private ImageButton send;
    private EditText chat;
    private ListView messages;
    private Activity mActivity;

    @Override
    protected Void doInBackground(Object... params) {
        try{
            csocket = new Socket((String) params[0], 5678);
            out = new ObjectOutputStream(csocket.getOutputStream());
            in = new ObjectInputStream(csocket.getInputStream());
            send = (ImageButton) params[2];
            chat = (EditText) params[3];
            messages = (ListView) params[4];
            mActivity = (Activity) params[5];
            MessageAdapter adapter = new MessageAdapter(mActivity);
            MemberData memberData = new MemberData((String) params[1], generateColor(new Random()));
            ServerController inputHandler = new ServerController(in, send, chat, messages, memberData, adapter);
            inputHandler.execute();
            listener = new ClientActionListener(out, send, chat, messages, memberData, adapter);
            String name = (String) params[1];
            out.writeObject(name);
            out.flush();
        }
        catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

    private static String generateColor(Random r) {
        final char [] hex = { '0', '1', '2', '3', '4', '5', '6', '7',
                '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
        char [] s = new char[7];
        int     n = r.nextInt(0x1000000);

        s[0] = '#';
        for (int i=1;i<7;i++) {
            s[i] = hex[n & 0xf];
            n >>= 4;
        }
        return new String(s);
    }
}