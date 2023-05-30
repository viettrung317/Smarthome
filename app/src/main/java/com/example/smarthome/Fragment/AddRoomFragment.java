package com.example.smarthome.Fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.*;
import java.util.Objects;

public class AddRoomFragment extends BottomSheetDialogFragment {
    private static final int REQUESTCODE_CAMERA = 777;
    private static final int REQUESTCODE_FOLDER = 999;
    private User user;
    private Home mHome;
    private List<Home> homeList=new ArrayList<>();
    private List<Room> roomList=new ArrayList<>();
    private int position=0;
    private EditText txtAddRoomName;
    private TextView txtSaveAddRoom,txtCancelAddRoom,txtAddImgRoom,txtSelectImgRoom;
    private ImageView imgAddRoom;
    private ProgressBar progressBar4;
    //
    private final FirebaseAuth mAuth=FirebaseAuth.getInstance();
    private final FirebaseDatabase dt=FirebaseDatabase.getInstance();
    private final DatabaseReference ref=dt.getReference("User");
    private final FirebaseUser fbuser=mAuth.getCurrentUser();
    private final String userUid;
    private List<Device> deviceList=new ArrayList<>();
    private List<Sensor> sensorList=new ArrayList<>();

    {
        assert fbuser != null;
        userUid = fbuser.getUid();
    }
    private final FirebaseStorage storage=FirebaseStorage.getInstance();
    private Bitmap bitmapImageRoom=null;
    private String roomName;
    private Room room;
    private int sizeListRoom=0;
    BottomSheetBehavior behavior;
    public static AddRoomFragment newInstace(Home home, User user){
        AddRoomFragment addRoomFragment=new AddRoomFragment();
        Bundle bundle=new Bundle();
        bundle.putSerializable("home",home);
        bundle.putSerializable("user",user);
        addRoomFragment.setArguments(bundle);
        return addRoomFragment;

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle=getArguments();
        if(bundle!=null){
            user=(User) bundle.get("user");
            mHome= (Home) bundle.get("home");
            if(mHome.getRoomList()!=null){
                roomList=mHome.getRoomList();
            }
            homeList=user.getHomeList();
            for(Home home:homeList){
                if(home==mHome){
                    position=homeList.indexOf(home);
                    break;
                }
            }


        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUESTCODE_CAMERA && resultCode == Activity.RESULT_OK && data != null) ;
        {
            try {
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                imgAddRoom.setImageBitmap(bitmap);
                bitmapImageRoom = bitmap;

            } catch (Exception e) {
                Log.e("erro", "" + e);
            }
        }
        if (requestCode == REQUESTCODE_FOLDER && resultCode == Activity.RESULT_OK && data != null) {
            Uri uri = data.getData();
            try {
                InputStream inputStream = requireActivity().getContentResolver().openInputStream(uri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                bitmapImageRoom = bitmap;
                imgAddRoom.setImageURI(data.getData());

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        View view= LayoutInflater.from(getContext()).inflate(R.layout.addroomlayout,null);
        bottomSheetDialog.setContentView(view);
        txtAddRoomName=(EditText) view.findViewById(R.id.txtAddRoomName);
        txtAddImgRoom=(TextView) view.findViewById(R.id.txtAddImgRoom);
        txtSelectImgRoom=(TextView) view.findViewById(R.id.txtSelectImgRoom);
        txtSaveAddRoom=(TextView) view.findViewById(R.id.txtSaveAddRoom);
        txtCancelAddRoom=(TextView) view.findViewById(R.id.txtCancelAddRoom);
        imgAddRoom=(ImageView) view.findViewById(R.id.imgAddRoom) ;
        progressBar4=(ProgressBar) view.findViewById(R.id.progressBar4);
        addEvents(bottomSheetDialog);
        behavior=BottomSheetBehavior.from((View) view.getParent());
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        return bottomSheetDialog;
    }

    private void addEvents(BottomSheetDialog bottomSheetDialog) {
        final StorageReference storageRef = storage.getReferenceFromUrl("gs://smarthome-49ec9.appspot.com");
        txtCancelAddRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
            }
        });
        txtAddRoomName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text=txtAddRoomName.getText().toString();
                if(!text.equals("")){
                    txtSaveAddRoom.setTextColor(Color.rgb(86,41,166));
                }else{
                    txtSaveAddRoom.setTextColor(Color.rgb(80,79,79));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        txtAddImgRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(camera, REQUESTCODE_CAMERA);
            }
        });
        txtSelectImgRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, REQUESTCODE_FOLDER);
            }
        });
        txtSaveAddRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                roomName = txtAddRoomName.getText().toString();
                if (!roomName.equals("")) {
                    progressBar4.setVisibility(View.VISIBLE);
                    Calendar calendar = Calendar.getInstance();
                    StorageReference mountainsRef = storageRef.child("imageRoom" + calendar.getTimeInMillis() + ".png");
                    // Get the data from an ImageView as bytes
                    imgAddRoom.setDrawingCacheEnabled(true);
                    imgAddRoom.buildDrawingCache();
                    Bitmap bitmap = ((BitmapDrawable) imgAddRoom.getDrawable()).getBitmap();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                    byte[] data = baos.toByteArray();

                    UploadTask uploadTask = mountainsRef.putBytes(data);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                            progressBar4.setVisibility(View.GONE);
                            Toast.makeText(getActivity(), "False !!!", Toast.LENGTH_LONG).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                            // ...
                            mountainsRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    room = new Room();
                                    room.setRoomName(roomName);
                                    room.setImgRoom(uri.toString());
                                    room.setListDevice(deviceList);
                                    room.setSensorList(sensorList);
                                    if(roomList.size()!=0){
                                        sizeListRoom=roomList.size();
                                    }
                                    ref.child(userUid).child("homeList").child(String.valueOf(position)).child("roomList").child(String.valueOf(sizeListRoom)).setValue(room).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                bottomSheetDialog.dismiss();
                                            } else {
                                                Toast.makeText(getActivity(), "Có sự cố khi thêm phòng !", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                                }
                            });

                        }
                    });
                }
            }
        });
    }
}
