package com.example.helloworld1;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Calendar;


public class fragmentFriendList extends Fragment {


    public fragmentFriendList() {

    }


    private  RecyclerView friendList_recycler;
    private DatabaseReference friendList_List;
    private DatabaseReference friendList_profileUrl;
    private FirebaseDatabase friendList_firebaseDatabase;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friend_list,container,false);

        //cast components
        friendList_recycler = view.findViewById(R.id.friendList_recycler);
        friendList_recycler.hasFixedSize();
        friendList_recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        friendList_firebaseDatabase = FirebaseDatabase.getInstance();

        String myUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        friendList_List = friendList_firebaseDatabase.getReference("friend list")
                .child(myUserId);

        friendList_profileUrl = friendList_firebaseDatabase.getReference("users");

        friendList_List.keepSynced(true);

        friendList_List.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
              if (snapshot.exists()) {
                  RetreieveFriendListNodes();
              }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        // Inflate the layout for this fragment
        return view;
    }

    private void RetreieveFriendListNodes() {
        FirebaseRecyclerAdapter<retrieveFriendSearchClass, FriendListUserNodeHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<retrieveFriendSearchClass, FriendListUserNodeHolder>
                        (retrieveFriendSearchClass.class, R.layout.friendlist_cardview, FriendListUserNodeHolder.class, friendList_List) {
                    @Override
                    protected void populateViewHolder(FriendListUserNodeHolder holder, retrieveFriendSearchClass model, int position) {

                        holder.SetUserName(model.getUserName());
                        holder.SetSinceDate(model.getSentDate());

                        String positionKey = getRef(position).getKey();

                        friendList_profileUrl.child(positionKey).child("profilePicUrl").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String myProfilePicUrl = snapshot.getValue().toString();

                                if(!snapshot.equals("nothing")){
                                   holder.SetPicUrl(myProfilePicUrl);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(getActivity(),error.getMessage(),Toast.LENGTH_SHORT).show();

                            }
                        });

                        holder.myView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String posKey = getRef(position).getKey();
                                startActivity(new Intent(getActivity(), chatMessageActivity.class).putExtra("userid", posKey));
                            }
                        });

                    }
                };
                      friendList_recycler.setAdapter(firebaseRecyclerAdapter);
    }

    public static class FriendListUserNodeHolder extends RecyclerView.ViewHolder{
        View myView;

        public FriendListUserNodeHolder(@NonNull View itemView) {
            super(itemView);
            myView = itemView;
        }

        public void SetUserName(String userName){
            TextView myUserName = myView.findViewById(R.id.friendlist_username);
            myUserName.setText(userName);
        }
        public void SetSinceDate(String sincedt){
            TextView myS_Date = myView.findViewById(R.id.friendlist_sinceDate);
            myS_Date.setText("Friends since: " + sincedt);
        }
        public void SetPicUrl(String picUrl){
            ImageView myI_View = myView.findViewById(R.id.friendlist_image);
            Picasso.get().load(picUrl).fit().centerInside().into(myI_View);
        }
    }

}