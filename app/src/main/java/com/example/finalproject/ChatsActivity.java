package com.example.finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatsActivity extends AppCompatActivity {

    private RecyclerView view;
    private DatabaseReference dbr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_finding);

        dbr = FirebaseDatabase.getInstance().getReference().child("Users");

        view = (RecyclerView) findViewById(R.id.find_friends_recycler);
        view.setLayoutManager(new LinearLayoutManager(this));

        getSupportActionBar().setTitle("Chat with a user!");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onStart(){
        super.onStart();

        FirebaseRecyclerOptions<User> opt = new FirebaseRecyclerOptions.Builder<User>()
                .setQuery(dbr, User.class)
                .build();
        FirebaseRecyclerAdapter<User, FindViewHolder> adapt =
                new FirebaseRecyclerAdapter<User, FindViewHolder>(opt) {
                    @Override
                    protected void onBindViewHolder(@NonNull FindViewHolder holder, final int position, @NonNull User model) {
                        holder.user.setText(model.getName());
                        holder.status.setText(model.getStatus());
                        if(model.getImage().isEmpty())
                            Picasso.get().load(R.drawable.emptyprofilepic);
                        else
                            Picasso.get().load(model.getImage()).into(holder.profile);
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String otherUserId = getRef(position).getKey();
                                Intent intent = new Intent(ChatsActivity.this, UserProfile.class);
                                intent.putExtra("otherUserId", otherUserId);
                                startActivity(intent);
                            }
                        });
                    }
                    @NonNull
                    @Override
                    public FindViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        FindViewHolder holderOfView = new FindViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.users_layout, parent, false));
                        return holderOfView;
                    }
                };
        view.setAdapter(adapt);
        adapt.startListening();
    }

    private static class FindViewHolder extends RecyclerView.ViewHolder{

        CircleImageView profile;
        TextView user;
        TextView status;

        private FindViewHolder(@NonNull View itemView) {
            super(itemView);

            user = itemView.findViewById(R.id.recycler_username);
            profile = itemView.findViewById(R.id.recycler_profile_imgs);
            status = itemView.findViewById(R.id.recycler_status);
        }
    }
}
