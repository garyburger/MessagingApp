package com.example.finalproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.icu.text.Edits;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

public class GroupChatActivity extends AppCompatActivity {

    private String userId;
    private String username;
    private String date;
    private String time;
    private String grpName;

    private FirebaseAuth auth;
    private DatabaseReference userDbr;
    private DatabaseReference groupDbr;
    private DatabaseReference grpMsgKeyDbr;

    private ScrollView scrollView;
    private EditText inputField;
    private Button sendMsg;
    private TextView msgs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        auth = FirebaseAuth.getInstance();
        userId = auth.getCurrentUser().getUid();
        grpName = getIntent().getExtras().get("nameofgroup").toString();

        getSupportActionBar().setTitle(grpName);
        scrollView = (ScrollView)findViewById(R.id.scrollView);
        inputField = (EditText)findViewById(R.id.group_chat_input_field);
        sendMsg = (Button)findViewById(R.id.send_group_text_button);
        msgs = (TextView)findViewById(R.id.group_chat_text);


        userDbr = FirebaseDatabase.getInstance().getReference().child("Users");

        groupDbr = FirebaseDatabase.getInstance().getReference().child("Groups").child(grpName);


        userDbr.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    username = dataSnapshot.child("name").getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        sendMsg.setOnClickListener(new View.OnClickListener(){
          @Override
          public void onClick(View view){
              String msgKey = groupDbr.push().getKey();
              if(!(TextUtils.isEmpty(inputField.getText().toString()))){
                  date = DateFormat.getDateInstance().format(new Date());
                  SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
                  time = timeFormat.format(Calendar.getInstance().getTime());

                  HashMap<String, Object> grpMsgKey = new HashMap<>();
                  groupDbr.updateChildren(grpMsgKey);
                  grpMsgKeyDbr = groupDbr.child(msgKey);

                  HashMap<String, Object> msgMap = new HashMap<>();
                  msgMap.put("name", username);
                  msgMap.put("date", date);
                  msgMap.put("time", time);
                  msgMap.put("message", inputField.getText().toString());
                  grpMsgKeyDbr.updateChildren(msgMap);
                  inputField.setText("");
              }
          }
        });
    }

    @Override
    public void onStart(){
        super.onStart();

        groupDbr.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.exists()){
                    Iterator itr = dataSnapshot.getChildren().iterator();

                    while(itr.hasNext()){
                        String date = ((DataSnapshot)itr.next()).getValue().toString();
                        String msg = ((DataSnapshot)itr.next()).getValue().toString();
                        String name = ((DataSnapshot)itr.next()).getValue().toString();
                        String time = ((DataSnapshot)itr.next()).getValue().toString();

                        msgs.append(name + "\n" + msg + "\n" + date + "\n" + time + "\n\n");
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
