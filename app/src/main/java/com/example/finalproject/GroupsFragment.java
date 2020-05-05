package com.example.finalproject;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


/**
 * A simple {@link Fragment} subclass.
 */
public class GroupsFragment extends Fragment {

    private View group;

    private ListView list;
    private ArrayAdapter<String> arrayA;
    private ArrayList<String> arrayL = new ArrayList<>();

    private DatabaseReference dbr;

    public GroupsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        group = inflater.inflate(R.layout.fragment_groups, container, false);

        dbr = FirebaseDatabase.getInstance().getReference().child("Groups");

        list = (ListView) group.findViewById(R.id.list_of_groups);
        arrayA = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, arrayL);
        list.setAdapter(arrayA);

        dbr.addValueEventListener(new ValueEventListener() {
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Set<String> set = new HashSet<>();
                Iterator iter = dataSnapshot.getChildren().iterator();
                while(iter.hasNext()){
                    set.add(((DataSnapshot)iter.next()).getKey());
                }
                update(arrayA, arrayL, set);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String grpName = adapterView.getItemAtPosition(i).toString();
                Intent intent = new Intent(getContext(),GroupChatActivity.class);
                intent.putExtra("nameofgroup", grpName);
                startActivity(intent);
            }
        });

        return group;
    }

    private void update(ArrayAdapter<String> arrayAdapter, ArrayList<String> arrayList, Set<String> set){
        arrayL.clear();
        arrayL.addAll(set);
        arrayA.notifyDataSetChanged();
    }

}
