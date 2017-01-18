package com.ssrij.fingerprintunlockmac;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class SetupFragment extends Fragment {

    public SetupFragment() {
    }

    public static SetupFragment newInstance(String authCode) {
        SetupFragment sf = new SetupFragment();

        Bundle args = new Bundle();
        args.putString("authCode", authCode);
        sf.setArguments(args);

        return sf;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_setup, container, false);

        TextView textViewCode = (TextView)view.findViewById(R.id.textView2);
        textViewCode.setText(getArguments().getString("authCode"));

        return view;
    }


}
