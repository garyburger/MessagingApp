package com.example.finalproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class PrivateChatActivity extends AppCompatActivity {

    private String receiverUsername, receiverId;
    private String senderUsername, senderId;
    private Button sendMsg;
    private EditText inputField;
    private FirebaseAuth auth;
    private DatabaseReference dbr;
    private DatabaseReference privateDbr;
    private DatabaseReference findSender, findRecv;
    private TextView text;
    private String msgPushId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_private_chat);

        getSupportActionBar().setTitle(getIntent().getExtras().get("usernameStr").toString());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        sendMsg = (Button) findViewById(R.id.send_private_text_button);
        inputField = (EditText) findViewById(R.id.private_chat_input_field);
        text = (TextView) findViewById(R.id.private_chat_text);
        auth = FirebaseAuth.getInstance();
        senderId = auth.getCurrentUser().getUid();
        receiverId = getIntent().getExtras().get("userId").toString();
        dbr = FirebaseDatabase.getInstance().getReference();
        privateDbr = dbr.child("Messages").child(senderId).child(receiverId);

        findSender = dbr.child("Users").child(senderId);
        findSender.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                    senderUsername = dataSnapshot.child("name").getValue().toString();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        findRecv = dbr.child("Users").child(receiverId);
        findRecv.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                    receiverUsername = dataSnapshot.child("name").getValue().toString();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        sendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msgText = inputField.getText().toString();
                if(!TextUtils.isEmpty(msgText)){
                    String sender = "Messages/" + senderId + "/" + receiverId;
                    String receiver = "Messages/" + receiverId + "/" + senderId;

                    DatabaseReference privateMsgKeys = dbr.child("Messages").child(senderId).child(receiverId).push();

                    msgPushId = privateMsgKeys.getKey();

                    Map text = new HashMap();
                    text.put("message", msgText);
                    text.put("type", "text");
                    text.put("from", senderId);
                    text.put("to", receiverId);
                    text.put("fromName", senderUsername);
                    text.put("toName", receiverUsername);

                    Map msgData = new HashMap();
                    msgData.put(sender + "/" + msgPushId, text);
                    msgData.put(receiver + "/" + msgPushId, text);

                    dbr.updateChildren(msgData).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if(!task.isSuccessful()){
                                Toast.makeText(PrivateChatActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                            }
                            inputField.setText("");
                        }
                    });

                }
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();


        //((DataSnapshot)itr.next()).getValue().toString()
        privateDbr.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.exists()){
                    Iterator itr = dataSnapshot.getChildren().iterator();
                    while(itr.hasNext()){
                        String senderId = ((DataSnapshot)itr.next()).getValue().toString();
                        String senderName = (((DataSnapshot) itr.next()).getValue().toString());
                        String message = ((DataSnapshot)itr.next()).getValue().toString();
                        String receiverId = ((DataSnapshot)itr.next()).getValue().toString();
                        String receiverName = ((DataSnapshot)itr.next()).getValue().toString();
                        String type = ((DataSnapshot)itr.next()).getValue().toString();

                        text.append(senderName + "\n" + message + "\n\n");
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



}
