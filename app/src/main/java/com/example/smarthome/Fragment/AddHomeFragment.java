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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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

public class AddHomeFragment extends BottomSheetDialogFragment {

    private static final int REQUESTCODE_CAMERA = 777;
    private static final int REQUESTCODE_FOLDER = 999;
    private User user;
    private Home mHome;
    private List<Home> homeList=new ArrayList<>();
    private EditText txtAddHomeName;
    private TextView txtSaveAddHome,txtCancelAddHome,txtAddImgHome,txtSelectImgHome;
    private ImageView imgAddHome;
    private ProgressBar progressBar41;
    //
    private final FirebaseAuth mAuth=FirebaseAuth.getInstance();
    private final FirebaseDatabase dt=FirebaseDatabase.getInstance();
    private final DatabaseReference ref=dt.getReference("User");
    private final FirebaseUser fbuser=mAuth.getCurrentUser();
    private final String userUid;
    private List<Room> roomList=new ArrayList<>();

    {
        assert fbuser != null;
        userUid = fbuser.getUid();
    }
    private final FirebaseStorage storage=FirebaseStorage.getInstance();
    private Bitmap bitmapImageHome=null;
    private String homeName;
    private int sizeListHome=0;
    BottomSheetBehavior behavior;
    public static AddHomeFragment newInstace(User user){
        AddHomeFragment addHomeFragment=new AddHomeFragment();
        Bundle bundle=new Bundle();
        bundle.putSerializable("user",user);
        addHomeFragment.setArguments(bundle);
        return addHomeFragment;

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle=getArguments();
        if(bundle!=null){
            user=(User) bundle.get("user");
            if(user.getHomeList()!=null) {
                homeList = user.getHomeList();
            }


        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUESTCODE_CAMERA && resultCode == Activity.RESULT_OK && data != null) ;
        {
            try {
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                imgAddHome.setImageBitmap(bitmap);
                bitmapImageHome = bitmap;

            } catch (Exception e) {
                Log.e("erro", "" + e);
            }
        }
        if (requestCode == REQUESTCODE_FOLDER && resultCode == Activity.RESULT_OK && data != null) {
            Uri uri = data.getData();
            try {
                InputStream inputStream = requireActivity().getContentResolver().openInputStream(uri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                bitmapImageHome = bitmap;
                imgAddHome.setImageURI(data.getData());

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
        View view= LayoutInflater.from(getContext()).inflate(R.layout.addhomelayout,null);
        bottomSheetDialog.setContentView(view);
        txtAddHomeName=(EditText) view.findViewById(R.id.txtAddHomeName);
        txtAddImgHome=(TextView) view.findViewById(R.id.txtAddImgHome);
        txtSelectImgHome=(TextView) view.findViewById(R.id.txtSelectImgHome);
        txtSaveAddHome=(TextView) view.findViewById(R.id.txtSaveAddHome);
        txtCancelAddHome=(TextView) view.findViewById(R.id.txtCancelAddHome);
        imgAddHome=(ImageView) view.findViewById(R.id.imgAddHome) ;
        progressBar41=(ProgressBar) view.findViewById(R.id.progressBar41);
        addEvents(bottomSheetDialog);
        behavior=BottomSheetBehavior.from((View) view.getParent());
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        return bottomSheetDialog;
    }

    private void addEvents(BottomSheetDialog bottomSheetDialog) {
        final StorageReference storageRef = storage.getReferenceFromUrl("gs://smarthome-49ec9.appspot.com");
        txtCancelAddHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
            }
        });
        txtAddHomeName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text=txtAddHomeName.getText().toString();
                if(!text.equals("")){
                    txtSaveAddHome.setTextColor(Color.rgb(86,41,166));
                }else{
                    txtSaveAddHome.setTextColor(Color.rgb(80,79,79));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        txtAddImgHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(camera, REQUESTCODE_CAMERA);
            }
        });
        txtSelectImgHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, REQUESTCODE_FOLDER);
            }
        });
        txtSaveAddHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                homeName = txtAddHomeName.getText().toString();
                if (!homeName.equals("")) {
                    progressBar41.setVisibility(View.VISIBLE);
                    Calendar calendar = Calendar.getInstance();
                    StorageReference mountainsRef = storageRef.child("imageHome" + calendar.getTimeInMillis() + ".png");
                    // Get the data from an ImageView as bytes
                    imgAddHome.setDrawingCacheEnabled(true);
                    imgAddHome.buildDrawingCache();
                    Bitmap bitmap = ((BitmapDrawable) imgAddHome.getDrawable()).getBitmap();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                    byte[] data = baos.toByteArray();

                    UploadTask uploadTask = mountainsRef.putBytes(data);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                            progressBar41.setVisibility(View.GONE);
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
                                    mHome = new Home();
                                    mHome.setNameHome(homeName);
                                    mHome.setImageHome(uri.toString());
                                    mHome.setRoomList(roomList);
                                    if(homeList.size()!=0){
                                        sizeListHome=homeList.size();
                                    }
                                    ref.child(userUid).child("homeList").child(String.valueOf(sizeListHome)).setValue(mHome).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                bottomSheetDialog.dismiss();
                                            } else {
                                                Toast.makeText(getActivity(), "Có sự cố khi thêm nhà !", Toast.LENGTH_LONG).show();
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