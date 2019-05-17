package com.diamong.happytalk;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

public class LogInActivity extends AppCompatActivity {
    private Button login, signUp;
    private FirebaseRemoteConfig mFirebaseRemoteConfig;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        mFirebaseRemoteConfig=FirebaseRemoteConfig.getInstance();
        String splash_background = mFirebaseRemoteConfig.getString("splash_background");
        getWindow().setStatusBarColor(Color.parseColor(splash_background));

        login=findViewById(R.id.loginactivity_button_login);
        signUp =findViewById(R.id.loginactivity_button_signup);
        login.setBackgroundColor(Color.parseColor(splash_background));
        signUp.setBackgroundColor(Color.parseColor(splash_background));

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LogInActivity.this,SignUpActivity.class));
            }
        });


    }
}
