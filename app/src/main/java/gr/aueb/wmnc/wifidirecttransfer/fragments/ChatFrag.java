package gr.aueb.wmnc.wifidirecttransfer.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import gr.aueb.wmnc.wifidirecttransfer.R;
import gr.aueb.wmnc.wifidirecttransfer.chat.client.SimpleChatClient;
import gr.aueb.wmnc.wifidirecttransfer.chat.server.SimpleChatServer;
import gr.aueb.wmnc.wifidirecttransfer.ui.UIUpdater;
import gr.aueb.wmnc.wifidirecttransfer.wifidirect.WiFiDirectReceiver;
import gr.aueb.wmnc.wifidirecttransfer.connections.phonesIps;

public class ChatFrag extends Fragment {

    private WiFiDirectReceiver receiver;
    private Activity mActivity;
    private SimpleChatServer chatServer;
    private String name;
    private phonesIps ips;
    private ImageButton send;
    private EditText chat;
    private ListView messages;
    private Menu menu;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chat_layout, container, false);

        setHasOptionsMenu(true);

        mActivity = getActivity();

        send = (ImageButton) view.findViewById(R.id.send);
        chat = (EditText) view.findViewById(R.id.chat_box);
        messages = (ListView) view.findViewById(R.id.chat_view);

        receiver = WiFiDirectReceiver.getInstance();

        if(WiFiDirectReceiver.connected){
            ips = receiver.getPhoneIps();
        }

        action();

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        this.menu = menu;
        UIUpdater.updateUI(menu, WiFiDirectReceiver.type);
    }

    private void action() {

        if(WiFiDirectReceiver.connected){
            if(receiver.isOwner()){
                chatServer = new SimpleChatServer();
                chatServer.execute();
            }
            SimpleChatClient client = new SimpleChatClient();

            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
            builder.setTitle("What is your name?");
            final EditText input = new EditText(mActivity);
            input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            builder.setView(input);

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    name = input.getText().toString();
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    return;
                }
            });
            builder.show();

            client.execute(ips.getServerIp(), name, send, chat, messages, mActivity);
        }

    }
}
