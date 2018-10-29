package gr.aueb.wmnc.wifidirecttransfer;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class ServiceFrag extends Fragment {

    private Activity activity;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.service_frag, container, false);

        setHasOptionsMenu(true);

        //Button button = (Button)view.findViewById(R.id.create_service);

        //button.setOnClickListener(new View.OnClickListener() {
        /*    @Override
            public void onClick(View view) {
                startService();
            }
        });*/

        return view;
    }

    private void startService()
    {

    }
}
