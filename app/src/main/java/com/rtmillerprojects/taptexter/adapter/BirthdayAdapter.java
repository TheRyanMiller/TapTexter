package com.rtmillerprojects.taptexter.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * Created by Ryan on 5/15/2016.
 */
public class BirthdayAdapter extends BaseAdapter {
    public int getCount(){
        return 1;
    }
    public Object getItem(int arg0){
        String res = "ryan";
        return res;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }

    public Object getView(){
        String res = "ryan";
        return res;
    }
}
