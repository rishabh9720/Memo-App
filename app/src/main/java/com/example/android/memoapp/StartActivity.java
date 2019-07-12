package com.example.android.memoapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.android.memoapp.user_sign.LoginActivity;
import com.example.android.memoapp.user_sign.RegisterActivity;
import com.google.firebase.auth.FirebaseAuth;

public class StartActivity extends AppCompatActivity {
    Button btn_reg, btn_log;
    private FirebaseAuth fauth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        fauth = FirebaseAuth.getInstance();
        Updateui();
        btn_log = findViewById(R.id.start_log_button);
        btn_reg = findViewById(R.id.start_res_button);
        btn_log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        btn_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });


    }

    private void register() {
        Intent intent = new Intent(StartActivity.this, RegisterActivity.class);
        startActivity(intent);
    }

    private void login() {
        Intent intent = new Intent(StartActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    private void Updateui() {
        if (fauth.getCurrentUser() != null) {
            Intent intent = new Intent(StartActivity.this, StartActivity.class);

            startActivity(intent);
            finish();

        } else {
        }


    }
}
