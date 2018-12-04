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
import android.widget.ListView;
import android.widget.Toast;

import java.util.Random;

import gr.aueb.wmnc.wifidirecttransfer.DrawerMain;
import gr.aueb.wmnc.wifidirecttransfer.R;
import gr.aueb.wmnc.wifidirecttransfer.chat.MemberData;
import gr.aueb.wmnc.wifidirecttransfer.chat.Message;
import gr.aueb.wmnc.wifidirecttransfer.chat.MessageAdapter;
import gr.aueb.wmnc.wifidirecttransfer.chat.client.Color;
import gr.aueb.wmnc.wifidirecttransfer.chat.client.SimpleChatClient;
import gr.aueb.wmnc.wifidirecttransfer.chat.server.SimpleChatServer;
import gr.aueb.wmnc.wifidirecttransfer.wifidirect.WiFiDirectReceiver;
import gr.aueb.wmnc.wifidirecttransfer.connections.phonesIps;

public class ChatFrag extends Fragment {

    private WiFiDirectReceiver receiver;
    private Activity mActivity;
    private SimpleChatServer chatServer;
    private MessageAdapter adapter;
    private ListView listView;
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

        if (WiFiDirectReceiver.connected) {
            ips = receiver.getPhoneIps();
        }

        listView = (ListView) view.findViewById(R.id.chat_view);
        adapter = new MessageAdapter(mActivity);
        listView.setAdapter(adapter);

        action();

        return view;
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
                    if(WiFiDirectReceiver.isHost){
                        SimpleChatServer simpleChatServer = new SimpleChatServer();
                        simpleChatServer.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, adapter, mActivity, name);
                    }
                    else{
                        /*if(SimpleChatServer.chatOnline){
                            SimpleChatClient simpleChatClient = new SimpleChatClient();
                            simpleChatClient.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, adapter, mActivity, name, ips.getServerIp());

                        }
                        else{
                            Toast.makeText(mActivity.getApplication(), "Server not alive", Toast.LENGTH_SHORT).show();
                            getFragmentManager().beginTransaction().replace(R.id.fragment, ((DrawerMain)getActivity()).getInfoFrag()).commit();
                        }*/
                        SimpleChatClient simpleChatClient = new SimpleChatClient();
                        simpleChatClient.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, adapter, mActivity, name, ips.getServerIp());
                    }

                    initiated = true;
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    getFragmentManager().beginTransaction().replace(R.id.fragment, ((DrawerMain)getActivity()).getInfoFrag()).commit();
                }
            });
            builder.show();
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
