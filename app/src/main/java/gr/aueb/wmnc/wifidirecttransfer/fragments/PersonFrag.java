package gr.aueb.wmnc.wifidirecttransfer.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import gr.aueb.wmnc.wifidirecttransfer.R;

public class PersonFrag extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.person_frag, container, false);
        TextView name = (TextView)view.findViewById(R.id.name);
        TextView am = (TextView)view.findViewById(R.id.am);
        TextView email = (TextView)view.findViewById(R.id.email);
        TextView github = (TextView)view.findViewById(R.id.github_link);

        String person = getArguments().getString("person");
        if(person.equals("person1")){
            name.setText(getString(R.string.person1_name));
            am.setText(getString(R.string.person1_am));
            email.setText(Html.fromHtml(getString(R.string.person1_email)));
            github.setText(Html.fromHtml(getString(R.string.person1_github)));
        }
        else if(person.equals("person2")){
            name.setText(getString(R.string.person2_name));
            am.setText(getString(R.string.person2_am));
            email.setText(Html.fromHtml(getString(R.string.person2_email)));
            github.setText(Html.fromHtml(getString(R.string.person2_github)));
        }
        else if(person.equals("person3")){
            name.setText(getString(R.string.person3_name));
            am.setText(getString(R.string.person3_am));
            email.setText(Html.fromHtml(getString(R.string.person3_email)));
            github.setText(Html.fromHtml(getString(R.string.person3_github)));
        }
        email.setMovementMethod(LinkMovementMethod.getInstance());
        github.setMovementMethod(LinkMovementMethod.getInstance());

        return view;
    }

}
