package com.example.smarthome.Fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smarthome.Account.LoginActivity;
import com.example.smarthome.Account.Validator;
import com.example.smarthome.Activity.EditAvatarMainActivity;
import com.example.smarthome.MainActivity;
import com.example.smarthome.Model.Home;
import com.example.smarthome.Model.User;
import com.example.smarthome.R;
import com.example.smarthome.ViewModel.UserViewModel;
import com.example.smarthome.databinding.FragmentHomeBinding;
import com.example.smarthome.databinding.FragmentProfileBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ProfileFragment extends Fragment {
    private FragmentProfileBinding binding;
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private final DatabaseReference ref = database.getReference("User");
    private final FirebaseUser fbuser = mAuth.getCurrentUser();
    private final String userUid;

    {
        assert fbuser != null;
        userUid = fbuser.getUid();
    }

    private User user = new User();
    private MainActivity mainActivity;
    private UserViewModel mViewModel; // Khai báo UserViewModel
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewModelProvider viewModelProvider=new ViewModelProvider(requireActivity());
        mViewModel = viewModelProvider.get(UserViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        if(getActivity()!=null) {
            mainActivity = (MainActivity) getActivity();
            getData();
        }
        addEvents();
        return binding.getRoot();
    }

    private void addEvents() {
        binding.btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                startActivity(new Intent(getActivity(), LoginActivity.class));

            }
        });
        binding.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser userFb=FirebaseAuth.getInstance().getCurrentUser();
                userFb.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getActivity(), "Đã xóa tài khoản !", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getActivity(), LoginActivity.class));
                            ref.child(userUid).removeValue(new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                }
                            });
                        }
                    }
                });
                startActivity(new Intent(getActivity(),LoginActivity.class));

            }
        });
        binding.btnResetPassWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openResetPassWord();
            }

            private void openResetPassWord() {
                final Dialog dialog1=new Dialog(getContext());
                dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog1.setContentView(R.layout.currentpasswordlayout);
                Window window= dialog1.getWindow();
                if(window==null){
                    return;
                }
                window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
                window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                WindowManager.LayoutParams windowAttributes=window.getAttributes();
                windowAttributes.gravity= Gravity.CENTER;
                window.setAttributes(windowAttributes);

                if(Gravity.BOTTOM== Gravity.CENTER){
                    dialog1.setCancelable(true);
                }
                else{
                    dialog1.setCancelable(false);
                }
                EditText txtCurrentPassWord=dialog1.findViewById(R.id.txtCurrentPassWord);
                TextView txtErrorCurrent=dialog1.findViewById(R.id.txtErrorCurrent);
                ProgressBar progressBar7=dialog1.findViewById(R.id.progressBar7);
                Button btnCancelResetPass=dialog1.findViewById(R.id.btnCancelResetPass);
                Button btnOpenNewPass=dialog1.findViewById(R.id.btnOpenNewPass);
                btnCancelResetPass.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog1.dismiss();
                    }
                });
                btnOpenNewPass.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String currentpass = txtCurrentPassWord.getText().toString();
                        if (TextUtils.isEmpty(currentpass)) {
                            txtErrorCurrent.setVisibility(View.VISIBLE);
                            txtErrorCurrent.setText("Vui lòng nhập mật khẩu !");
                            txtCurrentPassWord.requestFocus();
                        } else {
                            // Tạo một đối tượng AuthCredential từ email và password hiện tại của người dùng
                            AuthCredential credential = EmailAuthProvider.getCredential(fbuser.getEmail(), currentpass);
                            // Yêu cầu người dùng xác thực mật khẩu hiện tại
                            fbuser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        txtErrorCurrent.setVisibility(View.GONE);
                                        progressBar7.setVisibility(View.VISIBLE);
                                        dialog1.dismiss();
                                        openResetNewPassDialog();
                                    }
                                    else{
                                        txtErrorCurrent.setVisibility(View.VISIBLE);
                                        txtErrorCurrent.setText("Mật khẩu không chính xác !");
                                        txtCurrentPassWord.setText("");
                                        txtCurrentPassWord.requestFocus();
                                    }
                                }
                            });
                        }
                    }

                    private void openResetNewPassDialog() {
                        final Dialog dialog2=new Dialog(getContext());
                        dialog2.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog2.setContentView(R.layout.newpasswordlayout);
                        Window window= dialog2.getWindow();
                        if(window==null){
                            return;
                        }
                        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
                        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        WindowManager.LayoutParams windowAttributes=window.getAttributes();
                        windowAttributes.gravity= Gravity.CENTER;
                        window.setAttributes(windowAttributes);

                        if(Gravity.BOTTOM== Gravity.CENTER){
                            dialog2.setCancelable(true);
                        }
                        else{
                            dialog2.setCancelable(false);
                        }
                        EditText txtNewPassword=dialog2.findViewById(R.id.txtNewPassword);
                        EditText txtNewPasswordRe=dialog2.findViewById(R.id.txtNewPasswordRe);
                        TextView txtErrorNewPass=dialog2.findViewById(R.id.txtErrorNewPass);
                        ProgressBar progressBar8=dialog2.findViewById(R.id.progressBar8);
                        Button btnCancelResetNewPass=dialog2.findViewById(R.id.btnCancelResetNewPass);
                        Button btnResetNewPassWord=dialog2.findViewById(R.id.btnResetNewPassWord);
                        btnCancelResetNewPass.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog2.dismiss();
                            }
                        });
                        btnResetNewPassWord.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String Newpass = txtNewPassword.getText().toString();
                                String RePass = txtNewPasswordRe.getText().toString();
                                Validator validator=new Validator();
                                if (TextUtils.isEmpty(Newpass)) {
                                    txtErrorNewPass.setVisibility(View.VISIBLE);
                                    txtErrorNewPass.setText("Vui lòng nhập mật khẩu mới !");
                                    txtNewPassword.requestFocus();
                                } else if (TextUtils.isEmpty(RePass)) {
                                    txtErrorNewPass.setVisibility(View.VISIBLE);
                                    txtErrorNewPass.setText("Vui lòng xác nhận mật khẩu mới !");
                                    txtNewPasswordRe.requestFocus();
                                } else {
                                    if(!validator.validatePass(Newpass)){
                                        txtErrorNewPass.setVisibility(View.VISIBLE);
                                        txtErrorNewPass.setText("Password must be at least 8 characters, with uppercase and special characters ! ");
                                        txtNewPassword.setText("");
                                        txtNewPasswordRe.setText("");
                                        txtNewPassword.requestFocus();
                                    }
                                    else {
                                        if (!Newpass.equals(RePass)) {
                                            txtErrorNewPass.setVisibility(View.VISIBLE);
                                            txtErrorNewPass.setText("Mật khẩu xác thực không trùng khớp !");
                                            txtNewPassword.setText("");
                                            txtNewPasswordRe.setText("");
                                            txtNewPassword.requestFocus();
                                        } else {
                                            txtErrorNewPass.setVisibility(View.GONE);
                                            progressBar8.setVisibility(View.VISIBLE);
                                            // Xác thực thành công, tiến hành cập nhật mật khẩu mới
                                            fbuser.updatePassword(Newpass)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                Log.d("Error", "Cập nhật mật khẩu thành công!");
                                                                dialog2.dismiss();
                                                                mAuth.signOut();
                                                                startActivity(new Intent(getActivity(), LoginActivity.class));

                                                            } else {
                                                                Log.d("Error", "Cập nhật mật khẩu thất bại!");
                                                            }
                                                        }
                                                    });

                                        }
                                    }
                                }
                            }
                        });
                        dialog2.show();
                    }
                });
                dialog1.show();
            }
        });
        binding.ibtnEditSdt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog(Gravity.CENTER);
            }

        });
        binding.ibtnEditNgaySinh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chonNgay();
            }

            private void chonNgay() {
                Calendar calendar=Calendar.getInstance();
                int ngay=calendar.get(Calendar.DATE);
                int thang=calendar.get(Calendar.MONTH);
                int nam=calendar.get(Calendar.YEAR);
                DatePickerDialog datePickerDialog=new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        calendar.set(i,i1,i2);
                        String ngay=String.valueOf(i2);
                        if(i2<10){
                            ngay="0"+ngay;
                        }
                        String thang=String.valueOf(i1+1);
                        if(i1<10){
                            thang="0"+thang;
                        }
                        String nam=String.valueOf(i);
                        String nSinh=ngay+"/"+thang+"/"+nam;
                        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd/MM/yyyy");
                        ref.child(userUid).child("ngaySinh").setValue(nSinh).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                binding.txtNgaySinh.setText(simpleDateFormat.format(calendar.getTime()));
                            }
                        });
                    }
                },nam,thang,ngay);
                datePickerDialog.show();
            }
        });
        binding.ibtnEditGT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chonGT(Gravity.CENTER);
            }

            private void chonGT(int gravity) {
                final Dialog dialog=new Dialog(getContext());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_edit_gt);
                Window window= dialog.getWindow();
                if(window==null){
                    return;
                }
                window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
                window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                WindowManager.LayoutParams windowAttributes=window.getAttributes();
                windowAttributes.gravity=gravity;
                window.setAttributes(windowAttributes);

                if(Gravity.BOTTOM==gravity){
                    dialog.setCancelable(true);
                }
                else{
                    dialog.setCancelable(false);
                }
                RadioGroup radioGt=dialog.findViewById(R.id.radioGt);
                RadioButton radioNam=dialog.findViewById(R.id.radioNam);
                RadioButton radioNu=dialog.findViewById(R.id.radioNu);
                Button btnCancelgt=dialog.findViewById(R.id.btnCancelgt);
                Button btnUpdategt=dialog.findViewById(R.id.btnUpdategt);
                btnCancelgt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                btnUpdategt.setOnClickListener(new View.OnClickListener() {
                    String gt="";
                    @Override
                    public void onClick(View view) {
                        if(radioNam.isChecked()){
                            gt="Nam";
                        }
                        else if(radioNu.isChecked()){
                            gt="Nữ";
                        }
                        else{
                            Toast.makeText(getActivity(), "Bạn chưa chọn giới tính!", Toast.LENGTH_SHORT).show();
                        }
                        binding.txtGioiTinh.setText(gt);
                        ref.child(userUid).child("gt").setValue(gt).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                dialog.dismiss();
                            }
                        });
                    }
                });
                dialog.show();
            }
        });
        binding.ibtnEditAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), EditAvatarMainActivity.class));
            }
        });
    }
    private void openDialog(int gravity) {
        final Dialog dialog=new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_edit_sdt);
        Window window= dialog.getWindow();
        if(window==null){
            return;
        }
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams windowAttributes=window.getAttributes();
        windowAttributes.gravity=gravity;
        window.setAttributes(windowAttributes);

        if(Gravity.BOTTOM==gravity){
            dialog.setCancelable(true);
        }
        else{
            dialog.setCancelable(false);
        }
        EditText txtEditSdt=dialog.findViewById(R.id.txtEditSdt);
        Button btnCancelSdt=dialog.findViewById(R.id.btnCancelAvatar);
        Button btnUpdateSdt=dialog.findViewById(R.id.btnUpdateAvatar);
        btnCancelSdt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        btnUpdateSdt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String sdt=txtEditSdt.getText().toString();
                if(TextUtils.isEmpty(sdt)){
                    txtEditSdt.setError("Vui lòng nhập số điện thoại !");
                    txtEditSdt.requestFocus();
                }
                else{
                    binding.txtSdt.setText(sdt);
                    ref.child(userUid).child("sdt").setValue(sdt).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            dialog.dismiss();
                        }
                    });

                }
            }
        });
        dialog.show();
    }

    private void getData() {
        mViewModel.getMyData().observe(getViewLifecycleOwner(), new Observer<User>() {
            @Override
            public void onChanged(User userData) {
                // Xử lý cập nhật dữ liệu mới nhất từ server vào Fragment
                user=userData;
                if (user != null) {
                    if (user.getAvatar()!=null) {
                        Picasso.with(getContext()).load(user.getAvatar()).into(binding.imgAvatarUser);
                    }
                    if (user.getUserName()!=null) {
                        binding.txtUserName.setText(user.getUserName());
                    }
                    if (user.getEmail()!=null) {
                        binding.txtAddressEmail.setText(user.getEmail());
                    }
                    if (user.getGt()!=null) {
                        binding.txtGioiTinh.setText(user.getGt());
                    }
                    if (user.getNgaySinh()!=null) {
                        binding.txtNgaySinh.setText(user.getNgaySinh());
                    }
                    if (user.getSdt()!=null) {
                        binding.txtSdt.setText(user.getSdt());
                    }
                }
            }
        });
        mViewModel.LoadUser();
//        userData=mainActivity.getUser();
//        userData.observe(getViewLifecycleOwner(), new Observer<User>() {
//            @Override
//            public void onChanged(User user) {
//                // Xử lý cập nhật dữ liệu mới nhất từ server vào Fragment
//                user=userData.getValue();
//
//            }
//        });

    }
}