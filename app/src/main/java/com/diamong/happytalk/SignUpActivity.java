package com.diamong.happytalk;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.diamong.happytalk.model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

public class SignUpActivity extends AppCompatActivity {
    private EditText email, password, name;
    private Button buttonSignUp;
    private FirebaseRemoteConfig mFirebaseRemoteConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        String splash_background = mFirebaseRemoteConfig.getString("splash_background");
        getWindow().setStatusBarColor(Color.parseColor(splash_background));


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
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    UserModel userModel = new UserModel();
                                    userModel.userName = name.getText().toString();

                                    String uid = task.getResult().getUser().getUid();

                                    FirebaseDatabase.getInstance().getReference().child("users").child(uid).setValue(userModel);

                                }
                            });
                }
            }
        });
    }
}
