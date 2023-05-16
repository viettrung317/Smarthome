package com.example.smarthome.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.smarthome.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import java.util.Calendar;

public class EditAvatarMainActivity extends AppCompatActivity {
    private ImageView imgEditAvatar;
    private ImageButton ibtnCameraEdit,ibtnFolderEdit;
    private Button btnUpdateAvatar,btnCancelAvatar;
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private final DatabaseReference ref = database.getReference("User");
    private final FirebaseUser fbuser = mAuth.getCurrentUser();
    private final String userUid;
    {
        assert fbuser != null;
        userUid = fbuser.getUid();
    }
    private static final int REQUESTCODE_CAMERA = 777;
    private static final int REQUESTCODE_FOLDER = 999;
    Bitmap bitmapImages=null;
    FirebaseStorage storage=FirebaseStorage.getInstance();
    private int check=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_avatar_main);
        addControl();
        addEvent();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUESTCODE_CAMERA && resultCode == Activity.RESULT_OK && data != null) ;
        {
            try {
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                imgEditAvatar.setImageBitmap(bitmap);
                bitmapImages = bitmap;


            } catch (Exception e) {
                Log.e("erro", "" + e);
            }
            Toast.makeText(this, "Thêm ảnh thành công !", Toast.LENGTH_SHORT).show();
        }
        if (requestCode == REQUESTCODE_FOLDER && resultCode == Activity.RESULT_OK && data != null) {
            Uri uri = data.getData();
            try {
                InputStream inputStream = this.getContentResolver().openInputStream(uri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                bitmapImages = bitmap;
                imgEditAvatar.setImageURI(data.getData());


            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            Toast.makeText(this, "Thêm ảnh thành công !", Toast.LENGTH_SHORT).show();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void addEvent() {
        final StorageReference storageRef = storage.getReferenceFromUrl("gs://smarthome-49ec9.appspot.com");
        ibtnCameraEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(camera, REQUESTCODE_CAMERA);
                check=1;
            }
        });
        ibtnFolderEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, REQUESTCODE_FOLDER);
                check=1;
            }
        });
        btnUpdateAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(check==1){
                    Calendar calendar = Calendar.getInstance();
                    StorageReference mountainsRef = storageRef.child("imageMenu" + calendar.getTimeInMillis() + ".png");
                    // Get the data from an ImageView as bytes
                    imgEditAvatar.setDrawingCacheEnabled(true);
                    imgEditAvatar.buildDrawingCache();
                    Bitmap bitmap = ((BitmapDrawable) imgEditAvatar.getDrawable()).getBitmap();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                    byte[] data = baos.toByteArray();

                    UploadTask uploadTask = mountainsRef.putBytes(data);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                            Toast.makeText(EditAvatarMainActivity.this, "False !!!", Toast.LENGTH_LONG).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                            // ...
                            mountainsRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Uri downloadUrl = uri;
                                    ref.child(userUid).child("avatar").setValue(downloadUrl.toString());
                                    Toast.makeText(EditAvatarMainActivity.this, "Success !!!", Toast.LENGTH_LONG).show();
                                    finish();
                                }
                            });
                        }
                    });
                }
                else if(check==0){
                    Toast.makeText(EditAvatarMainActivity.this, "Thêm ảnh để update !", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnCancelAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void addControl() {
        imgEditAvatar=findViewById(R.id.imgEditAvatar);
        ibtnCameraEdit=findViewById(R.id.ibtnCameraEdit);
        ibtnFolderEdit=findViewById(R.id.ibtnFolderEdit);
        btnCancelAvatar=findViewById(R.id.btnCancelAvatar);
        btnUpdateAvatar=findViewById(R.id.btnUpdateAvatar);
    }
}