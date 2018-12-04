package gr.aueb.wmnc.wifidirecttransfer.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import gr.aueb.wmnc.wifidirecttransfer.R;
import gr.aueb.wmnc.wifidirecttransfer.chat.client.SimpleChatClient;
import gr.aueb.wmnc.wifidirecttransfer.chat.server.SimpleChatServer;
import gr.aueb.wmnc.wifidirecttransfer.wifidirect.WiFiDirectReceiver;
import gr.aueb.wmnc.wifidirecttransfer.connections.phonesIps;

public class ChatFrag extends Fragment {

    private WiFiDirectReceiver receiver;
    private Activity mActivity;
    private SimpleChatServer chatServer;
    private String name;
    private phonesIps ips;
    private Menu menu;
    private SimpleChatClient client;
    private boolean initiated = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chat_layout, container, false);

        setHasOptionsMenu(true);

        mActivity = getActivity();

        receiver = WiFiDirectReceiver.getInstance();

        if(WiFiDirectReceiver.connected){
            ips = receiver.getPhoneIps();
        }

        action();

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        if(WiFiDirectReceiver.connected){
            if(WiFiDirectReceiver.getInstance().isOwner()){
                chatServer = new SimpleChatServer();
                chatServer.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
            client = new SimpleChatClient();
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        this.menu = menu;
    }

    private void action() {
        if(!initiated){
            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
            builder.setTitle("What is your name?");
            final EditText input = new EditText(mActivity);
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    name = input.getText().toString();
                    client.connectToIp(ips.getServerIp());
                    client.setName(name);
                    client.setView(getView());
                    //client.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    client.execute();
                    initiated = true;
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
        }
        if(initiated){
            mActivity.startService(new Intent(mActivity, SimpleChatClient.class));
        }
    }

    @Override
    public void onResume() {
        WiFiDirectReceiver.getInstance().onResumeFragments();
        super.onResume();
    }

    @Override
    public void onPause() {
        WiFiDirectReceiver.getInstance().onPause();
        super.onPause();
    }
}
