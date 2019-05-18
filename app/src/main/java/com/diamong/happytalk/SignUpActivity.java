package com.diamong.happytalk;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.diamong.happytalk.model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class SignUpActivity extends AppCompatActivity {
    private static final int PICK_ALBUM = 1000;
    private EditText email, password, name;
    private Button buttonSignUp;
    private FirebaseRemoteConfig mFirebaseRemoteConfig;
    private CircleImageView profile;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        String splash_background = mFirebaseRemoteConfig.getString("splash_background");
        getWindow().setStatusBarColor(Color.parseColor(splash_background));


        profile = findViewById(R.id.signupactivity_iv_profile);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                startActivityForResult(intent, PICK_ALBUM);
            }
        });
        email = findViewById(R.id.signupactivity_edittext_email);
        password = findViewById(R.id.signupactivity_edittext_password);
        name = findViewById(R.id.signupactivity_edittext_name);
        buttonSignUp = findViewById(R.id.signupactivity_button_signup);
        buttonSignUp.setBackgroundColor(Color.parseColor(splash_background));


        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (email.getText().toString().isEmpty() || password.getText().toString().isEmpty() || name.getText().toString().isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "빈칸을 입력하세요", Toast.LENGTH_SHORT).show();
                } else {
                    FirebaseAuth.getInstance()
                            .createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                            .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull final Task<AuthResult> task) {


                                    final String uid = task.getResult().getUser().getUid();

                                    //유저 프로필 이미지 업로드 후 Url 얻어오는 코드
                                    final StorageReference filepath = FirebaseStorage.getInstance().getReference().child("userimages")
                                            .child(uid);

                                    if (imageUri==null) imageUri=Uri.parse("android.resource://"+getPackageName()+"/"+R.drawable.noprofile);
                                    

                                    filepath.putFile(imageUri)
                                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                    taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                        @Override
                                                        public void onSuccess(Uri uri) {
                                                            final String imageURL = uri.toString();
                                                            UserModel userModel = new UserModel();
                                                            userModel.userName = name.getText().toString();
                                                            userModel.profileImageUrl = imageURL;
                                                            FirebaseDatabase.getInstance().getReference().child("users").child(uid).setValue(userModel);
                                                        }
                                                    });
                                                }
                                                
                                            });
                                    /*filepath.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                                            //String imageURL=storageReference.getDownloadUrl().toString();
                                            Task<Uri> imageURL=task.getResult().getStorage().getDownloadUrl();

                                            UserModel userModel = new UserModel();
                                            userModel.userName = name.getText().toString();
                                            userModel.profileImageUrl = imageURL.toString();
                                            FirebaseDatabase.getInstance().getReference().child("users").child(uid).setValue(userModel);
                                        }
                                    });*/

                                    /*FirebaseStorage.getInstance().getReference().child("userimages").child(uid).putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("userimages").child(uid);

                                            Task<Uri> imageUrl = task.getResult().getStorage().getDownloadUrl();
                                            while (!imageUrl.isComplete()) ;

                                            UserModel userModel = new UserModel();
                                            userModel.userName = name.getText().toString();
                                            userModel.profileImageUrl = imageUrl.toString();
                                            FirebaseDatabase.getInstance().getReference().child("users").child(uid).setValue(userModel);
                                        }
                                    });*/


                                }
                            });
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_ALBUM && resultCode == RESULT_OK) {

            Glide.with(SignUpActivity.this)
                    .load(data.getData())
                    .centerCrop()
                    .into(profile);
            //profile.setImageURI(data.getData());
            imageUri = data.getData();
        }
    }
}
