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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AddDeviceFragment extends BottomSheetDialogFragment {
    private static final int REQUESTCODE_CAMERA = 777;
    private static final int REQUESTCODE_FOLDER = 999;
    private Room mRoom;
    private Device device;
    private List<Home> homeList=new ArrayList<>();
    private List<Room> roomList=new ArrayList<>();
    private List<Device> deviceList=new ArrayList<>();
    private int position=0;
    private EditText txtAddDeviceName;
    private TextView txtSaveAddDevice,txtCancelAddDevice,txtAddImgDevice,txtSelectImgDevice;
    private ImageView imgAddDevice;
    private ProgressBar progressBar40;
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
    private final FirebaseStorage storage=FirebaseStorage.getInstance();
    private Bitmap bitmapImageDevice=null;
    private String deviceName;
    private int sizeListDevice=0;
    BottomSheetBehavior behavior;
    public static AddDeviceFragment newInstace(Room room,int positionadd){
        AddDeviceFragment addDeviceFragment=new AddDeviceFragment();
        Bundle bundle=new Bundle();
        bundle.putSerializable("room",room);
        bundle.putInt("positionadd",positionadd);
        addDeviceFragment.setArguments(bundle);
        return addDeviceFragment;

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle=getArguments();
        if(bundle!=null){
            mRoom= (Room) bundle.get("room");
            if(mRoom.getListDevice()!=null){
                deviceList=mRoom.getListDevice();
            }
            position= (int) bundle.get("positionadd");


        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUESTCODE_CAMERA && resultCode == Activity.RESULT_OK && data != null) ;
        {
            try {
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                imgAddDevice.setImageBitmap(bitmap);
                bitmapImageDevice = bitmap;

            } catch (Exception e) {
                Log.e("erro", "" + e);
            }
        }
        if (requestCode == REQUESTCODE_FOLDER && resultCode == Activity.RESULT_OK && data != null) {
            Uri uri = data.getData();
            try {
                InputStream inputStream = requireActivity().getContentResolver().openInputStream(uri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                bitmapImageDevice = bitmap;
                imgAddDevice.setImageURI(data.getData());

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
        View view= LayoutInflater.from(getContext()).inflate(R.layout.adddevicelayout,null);
        bottomSheetDialog.setContentView(view);
        txtAddDeviceName=(EditText) view.findViewById(R.id.txtAddDeviceName);
        txtAddImgDevice=(TextView) view.findViewById(R.id.txtAddImgDevice);
        txtSelectImgDevice=(TextView) view.findViewById(R.id.txtSelectImgDevice);
        txtSaveAddDevice=(TextView) view.findViewById(R.id.txtSaveAddDevice);
        txtCancelAddDevice=(TextView) view.findViewById(R.id.txtCancelAddDevice);
        imgAddDevice=(ImageView) view.findViewById(R.id.imgAddDevice) ;
        progressBar40=(ProgressBar) view.findViewById(R.id.progressBar40);
        addEvents(bottomSheetDialog);
        behavior=BottomSheetBehavior.from((View) view.getParent());
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        return bottomSheetDialog;
    }

    private void addEvents(BottomSheetDialog bottomSheetDialog) {
        final StorageReference storageRef = storage.getReferenceFromUrl("gs://smarthome-49ec9.appspot.com");
        txtCancelAddDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
            }
        });
        txtAddDeviceName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text=txtAddDeviceName.getText().toString();
                if(!text.equals("")){
                    txtSaveAddDevice.setTextColor(Color.rgb(86,41,166));
                }else{
                    txtSaveAddDevice.setTextColor(Color.rgb(80,79,79));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        txtAddImgDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(camera, REQUESTCODE_CAMERA);
            }
        });
        txtSelectImgDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, REQUESTCODE_FOLDER);
            }
        });
        txtSaveAddDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deviceName = txtAddDeviceName.getText().toString();
                if (!deviceName.equals("")) {
                    progressBar40.setVisibility(View.VISIBLE);
                    Calendar calendar = Calendar.getInstance();
                    StorageReference mountainsRef = storageRef.child("imageDevice" + calendar.getTimeInMillis() + ".png");
                    // Get the data from an ImageView as bytes
                    imgAddDevice.setDrawingCacheEnabled(true);
                    imgAddDevice.buildDrawingCache();
                    Bitmap bitmap = ((BitmapDrawable) imgAddDevice.getDrawable()).getBitmap();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                    byte[] data = baos.toByteArray();

                    UploadTask uploadTask = mountainsRef.putBytes(data);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                            progressBar40.setVisibility(View.GONE);
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
                                    device = new Device();
                                    device.setDeviceName(deviceName);
                                    device.setImgDevice(uri.toString());
                                    device.setStatus(false);
                                    if(deviceList.size()!=0) {
                                        sizeListDevice= mRoom.getListDevice().size();
                                    }
                                    ref.child(userUid).child("homeList").child("0").child("roomList").child(String.valueOf(position)).child("listDevice").child(String.valueOf(sizeListDevice)).setValue(device).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                bottomSheetDialog.dismiss();
                                            } else {
                                                Toast.makeText(getActivity(), "Có sự cố khi thêm thiết bị !", Toast.LENGTH_LONG).show();
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
