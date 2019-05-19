package com.diamong.happytalk;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.diamong.happytalk.fragment.PeopleFragment;

public class BodyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_body);

        getFragmentManager().beginTransaction().replace(R.id.bodyactivity_framelayout,new PeopleFragment()).commit();
    }
}
