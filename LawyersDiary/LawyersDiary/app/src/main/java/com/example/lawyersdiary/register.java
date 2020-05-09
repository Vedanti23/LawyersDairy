package com.example.lawyersdiary;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class register extends AppCompatActivity implements View.OnClickListener{
    EditText name,email,password;
    ImageButton mRegisterbtn;
    ImageButton mLoginPageBack;
    FirebaseAuth mAuth;
    DatabaseReference mdatabase;
    String Name,Email,Password;
    ProgressDialog mDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registerlayout);
        name = (EditText)findViewById(R.id.txtusername);
        email = (EditText)findViewById(R.id.txtemail);
        password = (EditText)findViewById(R.id.txtpassward);
        mRegisterbtn = (ImageButton)findViewById(R.id.txtregister);
        mLoginPageBack = (ImageButton) findViewById(R.id.txtLOGIN);
        // for authentication using FirebaseAuth.
        mAuth = FirebaseAuth.getInstance();
        mRegisterbtn.setOnClickListener(this);
        mLoginPageBack.setOnClickListener(this);
        mDialog = new ProgressDialog(this);
        mdatabase = FirebaseDatabase.getInstance().getReference().child("Users");

    }

    @Override
    public void onClick(View v) {
        if (v==mRegisterbtn){
            UserRegister();
        }else if (v== mLoginPageBack){
            startActivity(new Intent(register.this,MainActivity.class));
        }
    }

    private void UserRegister() {
        Name = name.getText().toString().trim();
        Email = email.getText().toString().trim();
        Password = password.getText().toString().trim();

        if (TextUtils.isEmpty(Name)){
            Toast.makeText(register.this, "Enter Name", Toast.LENGTH_SHORT).show();
            return;
        }else if (TextUtils.isEmpty(Email)){
            Toast.makeText(register.this, "Enter Email", Toast.LENGTH_SHORT).show();
            return;
        }else if (TextUtils.isEmpty(Password)){
            Toast.makeText(register.this, "Enter Password", Toast.LENGTH_SHORT).show();
            return;
        }else if (Password.length()<6){
            Toast.makeText(register.this,"Passwor must be greater then 6 digit",Toast.LENGTH_SHORT).show();
            return;
        }
        mDialog.setMessage("Creating User please wait...");
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.show();
        mAuth.createUserWithEmailAndPassword(Email,Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    FirebaseUser user = mAuth.getCurrentUser(); //You Fi
                    sendEmailVerification();
                    mDialog.dismiss();
                    OnAuth(task.getResult().getUser());
                    mAuth.signOut();

                }else{
                    Toast.makeText(register.this,"error on creating user",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    //Email verification code using FirebaseUser object and using isSucccessful()function.
    private void sendEmailVerification() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user!=null){
            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(register.this,"Check your Email for verification",Toast.LENGTH_SHORT).show();
                        FirebaseAuth.getInstance().signOut();
                    }
                }
            });
        }
    }

    private void OnAuth(FirebaseUser user) {
        createAnewUser(user.getUid());
    }

    private void createAnewUser(String uid) {
        User user = BuildNewuser();
        mdatabase.child(uid).setValue(user);
    }


    private User BuildNewuser(){
        return new User(
                getDisplayName(),
                getUserEmail(),
                new Date().getTime()
        );
    }

    public String getDisplayName() {
        return Name;
    }

    public String getUserEmail() {
        return Email;
    }

}

