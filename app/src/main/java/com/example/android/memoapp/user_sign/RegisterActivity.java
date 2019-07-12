package com.example.android.memoapp.user_sign;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.android.memoapp.MainActivity;
import com.example.android.memoapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    Button btn_reg;
    TextInputLayout inname, inemail, inpassword;
    private FirebaseAuth fauth;
    private DatabaseReference mfirebaseDatabase;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        btn_reg = findViewById(R.id.btn_reg);
        inname = findViewById(R.id.input_reg_name);
        inemail = findViewById(R.id.input_reg_email);
        inpassword = findViewById(R.id.input_reg_pass);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        fauth = FirebaseAuth.getInstance();
        mfirebaseDatabase = FirebaseDatabase.getInstance().getReference().child("Users");


        btn_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String uname = inname.getEditText().getText().toString().trim();
                String uemail = inemail.getEditText().getText().toString().trim();
                String upass = inpassword.getEditText().getText().toString().trim();
                registerUser(uname, uemail, upass);


            }
        });
    }

    public void registerUser(final String name, String email, String password) {

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Processing please wait");
        progressDialog.show();


        fauth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    progressDialog.dismiss();

                    mfirebaseDatabase.child(fauth.getCurrentUser().getUid()).child("basic").child("name").setValue(name).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                progressDialog.dismiss();
                                Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
                                startActivity(mainIntent);
                                Toast.makeText(RegisterActivity.this, "Success", Toast.LENGTH_SHORT).show();

                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(RegisterActivity.this, "Unsuccessful", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                } else {
                    Toast.makeText(RegisterActivity.this, "Error" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId())
        {
            case R.id.home:
                finish();
                break;
        }

        return true;
    }
}
