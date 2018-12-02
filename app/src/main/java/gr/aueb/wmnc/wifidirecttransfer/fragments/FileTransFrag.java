package gr.aueb.wmnc.wifidirecttransfer.fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.net.URISyntaxException;

import gr.aueb.wmnc.wifidirecttransfer.filetrans.PathUtil;
import gr.aueb.wmnc.wifidirecttransfer.R;
import gr.aueb.wmnc.wifidirecttransfer.connections.phonesIps;
import gr.aueb.wmnc.wifidirecttransfer.filetrans.Send;
import gr.aueb.wmnc.wifidirecttransfer.filetrans.Sense;
import gr.aueb.wmnc.wifidirecttransfer.ui.UIUpdater;
import gr.aueb.wmnc.wifidirecttransfer.wifidirect.WiFiDirectReceiver;

public class FileTransFrag extends Fragment {

    private Button choose, sendf;
    private TextView filename;
    private Activity mActivity;
    private Uri uri;
    private phonesIps phonesIps;
    private String path;
    private Menu menu;

    private static final int READ_REQUEST_CODE = 42;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.filetrans_layout, container, false);
        setHasOptionsMenu(true);

        mActivity = getActivity();

        choose = (Button) view.findViewById(R.id.choose);
        sendf = (Button) view.findViewById(R.id.sendf);
        filename = (TextView) view.findViewById(R.id.filename);

        //phonesIps = WiFiDirectReceiver.getInstance().getPhoneIps();

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
            phonesIps = WiFiDirectReceiver.getInstance().getPhoneIps();
        }
        Sense fileSense = new Sense();
        fileSense.execute();
        choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search();
            }
        });

        sendf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initiateMission();
            }
        });
    }

    public void initiateMission(){
        Send sendFile = new Send();
        if(uri != null){
            if(WiFiDirectReceiver.connected){
                if(WiFiDirectReceiver.isHost){
                    sendFile.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, path, phonesIps.getClientIp());
                }
                else{
                   sendFile.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, path, phonesIps.getServerIp());
                }
            }
        }
        else{
            Toast.makeText(mActivity.getApplication(), "No file chosen", Toast.LENGTH_SHORT).show();
        }
    }

    public void search(){
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        startActivityForResult(intent, READ_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            uri = null;
            if(data != null){
                uri = data.getData();
                filename.setText((new File(uri.toString())).getName());
                try {
                    path = PathUtil.getPath(mActivity.getApplicationContext(), uri);
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
