package com.example.finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegistrationActivity extends AppCompatActivity {

    private EditText email, password;
    private Button create;
    private FirebaseAuth auth;
    private DatabaseReference dbr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);


        email = (EditText) findViewById(R.id.register_username);
        password = (EditText) findViewById(R.id.register_password);
        create = (Button) findViewById(R.id.createAcctButton);
        auth = FirebaseAuth.getInstance();
        dbr = FirebaseDatabase.getInstance().getReference();



        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailStr = email.getText().toString();
                String passStr = password.getText().toString();

                if (TextUtils.isEmpty(emailStr) && !(TextUtils.isEmpty(passStr))){
                    Toast toast = Toast.makeText(getApplicationContext(), "Please enter a valid email", Toast.LENGTH_SHORT);
                    toast.show();
                }
                else if (!(TextUtils.isEmpty(emailStr)) && TextUtils.isEmpty(passStr)){
                    Toast toast = Toast.makeText(getApplicationContext(), "Please enter a password", Toast.LENGTH_SHORT);
                    toast.show();
                }
                else if(TextUtils.isEmpty(emailStr) && TextUtils.isEmpty(passStr)){
                    Toast toast = Toast.makeText(getApplicationContext(), "Please enter a valid email and password", Toast.LENGTH_SHORT);
                    toast.show();
                }
                else{
                    auth.createUserWithEmailAndPassword(emailStr, passStr)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){
                                        String user = auth.getCurrentUser().getUid();
                                        dbr.child("Users").child(user).setValue("");
                                        Toast toast = Toast.makeText(RegistrationActivity.this, "Account created", Toast.LENGTH_SHORT);
                                        toast.show();
                                        Intent intent = new Intent(RegistrationActivity.this, MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Toast toast = Toast.makeText(RegistrationActivity.this, task.getException().toString(), Toast.LENGTH_SHORT);
                                        toast.show();
                                    }
                                }
                            });
                }
            }
        });

    }
}
