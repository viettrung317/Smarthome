package com.example.smarthome.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.smarthome.Model.Home;
import com.example.smarthome.R;

import java.util.List;

public class SelectHomeAdapter extends ArrayAdapter<Home> {
    public SelectHomeAdapter(@NonNull Context context, int resource, @NonNull List<Home> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView= LayoutInflater.from(parent.getContext()).inflate(R.layout.select_home_layout,parent,false);
        TextView txtHomeNameSllayout=(TextView) convertView.findViewById(R.id.txtHomeNameSllayout);
        ImageView imgSlHome=(ImageView) convertView.findViewById(R.id.imgSlHome);
        Home home=this.getItem(position);
        if(home!=null){
            txtHomeNameSllayout.setText(home.getNameHome());
        }
        return convertView;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView=LayoutInflater.from(this.getContext()).inflate(R.layout.select_home_layout2,parent,false);
        TextView txtHomeNameSl=(TextView) convertView.findViewById(R.id.txtHomeNameSl);
        Home home=this.getItem(position);
        if(home!=null){
            txtHomeNameSl.setText(home.getNameHome());
        }
        return convertView;
    }
}
