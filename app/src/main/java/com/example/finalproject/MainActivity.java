package com.example.finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private FragmentsAccess tabsAccessorAdapter;

    private FirebaseUser currentUser;
    private FirebaseAuth auth;
    private DatabaseReference dbr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        dbr = FirebaseDatabase.getInstance().getReference();


        viewPager = (ViewPager) findViewById(R.id.main_tabs_pager);
        tabsAccessorAdapter = new FragmentsAccess(getSupportFragmentManager());
        viewPager.setAdapter(tabsAccessorAdapter);

    }

    @Override
    protected void onStart() {
        super.onStart();

        if(currentUser == null){
            //Redirect user to login screen
            Intent login = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(login);
        } else {
            String user = currentUser.getUid();
            dbr.child("Users").child(user).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(!(dataSnapshot.child("name").exists())){
                        Toast toast = Toast.makeText(MainActivity.this, "Please enter a username", Toast.LENGTH_SHORT);
                        toast.show();
                        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == R.id.logout){
            auth.signOut();
            Toast toast = Toast.makeText(MainActivity.this, "Logged out...", Toast.LENGTH_SHORT);
            toast.show();
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        }
        else if(item.getItemId() == R.id.googleMaps){
            Intent intent = new Intent(MainActivity.this, MapsActivity.class);
            intent.putExtra("userId", currentUser.getUid());
            startActivity(intent);
        }
        else if (item.getItemId() == R.id.chat_with_a_person){
            Intent intent = new Intent(getApplicationContext(), ChatsActivity.class);
            startActivity(intent);
        }
        else if (item.getItemId() == R.id.settings){
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        }
        else if(item.getItemId() == R.id.group){
            AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
            alert.setTitle("Group name?");

            final EditText name = new EditText(getApplicationContext());
            alert.setView(name);
            alert.setPositiveButton("Create Group", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    String grpName = name.getText().toString();
                    if(TextUtils.isEmpty(grpName)){
                        Toast toast = Toast.makeText(getApplicationContext(), "Please enter group name", Toast.LENGTH_SHORT);
                        toast.show();
                    } else {
                        try {
                            dbr.child("Groups").child(grpName).setValue("")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (!task.isSuccessful()) {
                                                Toast.makeText(getApplicationContext(), task.getException().toString(), Toast.LENGTH_SHORT);
                                            }
                                        }
                                    });
                        } catch(com.google.firebase.database.DatabaseException dbe){
                            Toast.makeText(getApplicationContext(), dbe.toString(), Toast.LENGTH_LONG).show();
                        }
                    }
                }
            });
            alert.show();
        }
        return true;
    }
}
