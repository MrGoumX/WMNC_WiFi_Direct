package gr.aueb.wmnc.wifidirecttransfer;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

public class InfoFrag extends Fragment {

    private Menu menu;
    private MenuInflater menuInflater;
    private String type;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.info_frag, container, false);
        setHasOptionsMenu(true);
        //type = getArguments().getString("connected");
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        /*if(!type.equals("")){
            menu.findItem(R.id.con_status).setVisible(true);
            menu.findItem(R.id.con_status).setTitle(type);
        }*/
        super.onCreateOptionsMenu(menu, inflater);
    }

}
