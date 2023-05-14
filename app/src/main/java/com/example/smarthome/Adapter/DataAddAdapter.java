package com.example.smarthome.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.smarthome.Model.DataAdd;
import com.example.smarthome.R;

import java.util.List;

public class DataAddAdapter extends ArrayAdapter<DataAdd> {
    private TextView txt_snp_add;
    private ImageView img_snp_add;
    Activity context;int resource;List<DataAdd> objects;
    public DataAddAdapter(Activity context, int resource,List<DataAdd> objects) {
        super(context, resource, objects);
        this.context=context;
        this.resource=resource;
        this.objects=objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater=this.context.getLayoutInflater();
        View row=layoutInflater.inflate(this.resource,null);
        txt_snp_add=(TextView) row.findViewById(R.id.txt_snp_add);
        img_snp_add=(ImageView) row.findViewById(R.id.img_snp_add);
        final DataAdd dataAdd=this.objects.get(position);
        txt_snp_add.setText(dataAdd.getText());
        img_snp_add.setImageResource(dataAdd.getImg());
        return row;
    }
}
