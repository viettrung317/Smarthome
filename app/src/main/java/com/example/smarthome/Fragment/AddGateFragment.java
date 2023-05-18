package com.example.smarthome.Fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.smarthome.Model.Device;
import com.example.smarthome.Model.Home;
import com.example.smarthome.Model.Room;
import com.example.smarthome.Model.Sensor;
import com.example.smarthome.Model.User;
import com.example.smarthome.R;
import com.example.smarthome.encodeanddecode.DeCode;
import com.example.smarthome.encodeanddecode.EnCode;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.List;

public class AddGateFragment extends BottomSheetDialogFragment {
    private Home mHome;
    private EditText txtAddPassGate,txtAddPassGateRes;
    private TextView txtSaveAddGate,txtCancelAddGate;
    private ProgressBar progressBar42;
    //
    private final FirebaseAuth mAuth=FirebaseAuth.getInstance();
    private final FirebaseDatabase dt=FirebaseDatabase.getInstance();
    private final DatabaseReference ref=dt.getReference("User");
    private final FirebaseUser fbuser=mAuth.getCurrentUser();
    private final String userUid;

    {
        assert fbuser != null;
        userUid = fbuser.getUid();
    }
    private String passGate;
    BottomSheetBehavior behavior;
    public static AddGateFragment newInstace(Home home){
        AddGateFragment addGateFragment=new AddGateFragment();
        Bundle bundle=new Bundle();
        bundle.putSerializable("home",home);
        addGateFragment.setArguments(bundle);
        return addGateFragment;

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle=getArguments();
        if(bundle!=null){
            mHome= (Home) bundle.get("home");


        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        View view= LayoutInflater.from(getContext()).inflate(R.layout.addgatelayout,null);
        bottomSheetDialog.setContentView(view);
        txtAddPassGate=(EditText) view.findViewById(R.id.txtAddPassGate);
        txtAddPassGateRes=(EditText) view.findViewById(R.id.txtAddPassGateRes);
        txtSaveAddGate=(TextView) view.findViewById(R.id.txtSaveAddGate);
        txtCancelAddGate=(TextView) view.findViewById(R.id.txtCancelAddGate);
        progressBar42=(ProgressBar) view.findViewById(R.id.progressBar42);
        addEvents(bottomSheetDialog);
        behavior=BottomSheetBehavior.from((View) view.getParent());
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        return bottomSheetDialog;
    }

    private void addEvents(BottomSheetDialog bottomSheetDialog) {
        txtCancelAddGate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
            }
        });
        txtAddPassGate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String pass=txtAddPassGate.getText().toString();
                String rePass=txtAddPassGateRes.getText().toString();
                if(pass.length()>=6) {
                    txtAddPassGateRes.requestFocus();
                }
                if(!rePass.equals("")){
                    txtAddPassGateRes.setText("");
                    txtSaveAddGate.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        txtAddPassGateRes.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String pass=txtAddPassGate.getText().toString();
                String rePass=txtAddPassGateRes.getText().toString();
                if(rePass.equals(pass) && !rePass.isEmpty()){
                    txtSaveAddGate.setTextColor(Color.rgb(86,41,166));
                }else{
                    txtSaveAddGate.setTextColor(Color.rgb(80,79,79));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        txtSaveAddGate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pass=txtAddPassGate.getText().toString();
                String rePass = txtAddPassGateRes.getText().toString();
                if(rePass.equals(pass) && !rePass.isEmpty()){
                    progressBar42.setVisibility(View.VISIBLE);
                    try {
                        passGate= rePass;
                        mHome.setStatusGate(false);
                        mHome.setPassGateHome(passGate);
                        ref.child(userUid).child("homeList").child("0").updateChildren(mHome.toMap()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    bottomSheetDialog.dismiss();
                                } else {
                                    Toast.makeText(getActivity(), "Có sự cố khi thêm phòng !", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        });
    }
}
