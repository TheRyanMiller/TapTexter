package com.rtmillerprojects.taptexter.ui;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.rtmillerprojects.taptexter.R;

/**
 * Created by Ryan on 5/15/2016.
 */
public class FragmentDialog extends DialogFragment implements View.OnClickListener{

    Button add, cancel;
    Communicator cm;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        cm = (Communicator) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_dialogadd, null);
        add = (Button) view.findViewById(R.id.btn_add);
        cancel = (Button) view.findViewById(R.id.btn_cancel);
        add.setOnClickListener(this);
        cancel.setOnClickListener(this);
        setCancelable(false);
        return view;
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btn_add){
            cm.onDialogMessage("Add was clicked");
            dismiss();
        }
        if(v.getId() == R.id.btn_cancel){
            cm.onDialogMessage("Cancel was clicked");
            dismiss();
        }
        dismiss();
    }

    interface Communicator{
        void onDialogMessage(String message);
    }
}
