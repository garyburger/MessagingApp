package com.example.finalproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.w3c.dom.Text;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private Button updateProfile;
    private Button main;
    private EditText username;
    private EditText status;
    private EditText updateEmail;
    private EditText updatePhone;
    private CircleImageView profilePicture;
    private EditText updatePassword;
    private String profilePicUrl;

    private String userID;
    private FirebaseAuth auth;
    private DatabaseReference dbr;
    private StorageReference imagesRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        auth = FirebaseAuth.getInstance();
        userID = auth.getCurrentUser().getUid();
        dbr = FirebaseDatabase.getInstance().getReference();
        imagesRef = FirebaseStorage.getInstance().getReference().child("Profile Images");

        updateProfile = (Button) findViewById(R.id.updateSettings);
        username = (EditText) findViewById(R.id.settings_username);
        status = (EditText) findViewById(R.id.settings_status);
        profilePicture = (CircleImageView) findViewById(R.id.set_profile_image);
        main = (Button) findViewById(R.id.MainMenu);
        updateEmail = (EditText) findViewById(R.id.settings_updateEmail);
        updatePhone = (EditText) findViewById(R.id.settings_updatePhone);
        updatePassword = (EditText) findViewById(R.id.settings_updatePassword);

        profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
            }
        });


                dbr.child("Users").child(userID)
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("name"))) {
                                    username.setText(dataSnapshot.child("name").getValue().toString());
                                    status.setText(dataSnapshot.child("status").getValue().toString());

                                    updateEmail.setText(auth.getCurrentUser().getEmail());
                                    if(dataSnapshot.child("image").getValue() != null) {
                                        profilePicUrl = dataSnapshot.child("image").getValue().toString();
                                        Picasso.get().load(profilePicUrl).into(profilePicture);
                                    }
                                } else if (!(dataSnapshot.exists())) {
                                    Toast.makeText(SettingsActivity.this, "Please enter your username", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

        main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent  = new Intent(SettingsActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        updateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String usernameStr = username.getText().toString();
                final String statusStr = status.getText().toString();
                final String emailStr = updateEmail.getText().toString();
                final String phoneStr = updatePhone.getText().toString();
                final String passStr = updatePassword.getText().toString();


                if(TextUtils.isEmpty(usernameStr)){
                    Toast toast = Toast.makeText(SettingsActivity.this, "Please enter a username", Toast.LENGTH_SHORT);
                    toast.show();
                } else{
                    HashMap<String, String> profile = new HashMap<>();
                    profile.put("uid", userID);
                    profile.put("name", usernameStr);
                    profile.put("status", statusStr);
                    profile.put("image", profilePicUrl);

                    dbr.child("Users").child(userID).setValue(profile)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast toast = Toast.makeText(SettingsActivity.this, "Profile updated", Toast.LENGTH_SHORT);
                                        toast.show();
                                        username.setText(usernameStr);
                                        if(!(TextUtils.isEmpty(statusStr)))
                                            status.setText(statusStr);
                                        if(!(TextUtils.isEmpty(emailStr)))
                                            auth.getCurrentUser().updateEmail(emailStr);
                                        if(!(TextUtils.isEmpty(passStr))) {
                                            auth.getCurrentUser().updatePassword(passStr);
                                            updatePassword.setText("");
                                        }
                                    } else {
                                        Toast toast = Toast.makeText(SettingsActivity.this, task.getException().toString(), Toast.LENGTH_SHORT);
                                        toast.show();
                                    }
                                }
                            });
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==1 && resultCode==RESULT_OK && data != null){
            Uri imgUri = data.getData();
            CropImage.activity().setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1).start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if(resultCode == RESULT_OK){
                final Uri resultUri = result.getUri();
                StorageReference filePath = imagesRef.child(userID + ".jpg");
                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()) {
                            Toast.makeText(SettingsActivity.this, "Upload successful", Toast.LENGTH_SHORT).show();

                            final String downloadUrl = resultUri.toString();
                            dbr.child("Users").child(userID).child("image").setValue(downloadUrl);
                        }
                    }
                });
            }
        }
    }
}
