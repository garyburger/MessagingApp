package com.example.finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfile extends AppCompatActivity {

    private String usernameString;
    private String userIdStr;
    private String currentUser;
    private String currentUserId;
    private CircleImageView profile;
    private TextView username;
    private TextView status;
    private Button sendMsg;
    private Button getLocation;
    private DatabaseReference dbr;
    private FirebaseAuth auth;
    private String lat;
    private String longi;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        auth = FirebaseAuth.getInstance();
        dbr = FirebaseDatabase.getInstance().getReference().child("Users");
        //Other user
        userIdStr = getIntent().getExtras().get("otherUserId").toString();
        //Logged in user
        currentUserId = auth.getCurrentUser().getUid();



        profile = (CircleImageView) findViewById(R.id.users_profileImage_userProfileActivity);
        username = (TextView) findViewById(R.id.users_username_userProfile);
        status = (TextView) findViewById(R.id.users_status_userProfile);
        sendMsg = (Button) findViewById(R.id.chat_with_user_button_userProfile);
        getLocation = (Button) findViewById(R.id.user_location_button);


        dbr.child(userIdStr).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usernameString = dataSnapshot.child("name").getValue().toString();
                username.setText(usernameString);
                status.setText(dataSnapshot.child("status").getValue().toString());

                if(dataSnapshot.exists() && (dataSnapshot.child("latitude").getValue() != null)) {
                    lat = dataSnapshot.child("latitude").getValue().toString();
                    longi = dataSnapshot.child("longitude").getValue().toString();
                }
                if(dataSnapshot.exists() && !(dataSnapshot.hasChild("image")))
                    Picasso.get().load(R.drawable.emptyprofilepic);
                else
                    Picasso.get().load(dataSnapshot.child("image").getValue().toString()).into(profile);
                }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        sendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!currentUserId.equals(userIdStr)) {
                    Toast.makeText(UserProfile.this, "currentUser = " + currentUserId + "\n otherUser = " + userIdStr, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(UserProfile.this, PrivateChatActivity.class);
                    //Other user info
                    intent.putExtra("usernameStr", usernameString);
                    intent.putExtra("userId", userIdStr);
                    //Logged in user info
                    intent.putExtra("currentUser", currentUserId);

                    startActivity(intent);
                } else
                    Toast.makeText(UserProfile.this, "Can't chat with yourself, loser!", Toast.LENGTH_SHORT).show();
            }
        });

        getLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserProfile.this, OtherUserLocation.class);
                intent.putExtra("username", usernameString);
                intent.putExtra("userLat", lat);
                intent.putExtra("userLong", longi);
                startActivity(intent);
            }
        });


    }
}
