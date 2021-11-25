package com.example.helloworld1;

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
import java.util.Objects;


public class friendRequestFragment extends Fragment {



    public friendRequestFragment() {
        // Required empty public constructor
    }




    private RecyclerView friendSearch_recycler;
    private EditText friendSearch_UserName;
    private FirebaseDatabase friendSearch_firebaseDatabase;
    private DatabaseReference mySearchRef, friendReqRef, friendListRef;
    private String nameUser, positionKey;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friend_request,container,false);

        //Recyclerview Part
        friendSearch_recycler = view.findViewById(R.id.friendSearch_recycler);
        friendSearch_recycler.hasFixedSize();
        friendSearch_recycler.setLayoutManager(new LinearLayoutManager(getContext()));

        //Cast Components
        friendSearch_UserName = view.findViewById(R.id.friendSearch_edittext);
        Button friendSearch_btn = view.findViewById(R.id.friendSearch_btn);
        friendSearch_firebaseDatabase =  FirebaseDatabase.getInstance();

        //Intalize databaseRef
        mySearchRef = friendSearch_firebaseDatabase.getReference("users");

        friendSearch_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = friendSearch_UserName.getText().toString().trim();
                retrieveUsersSearched(userName);
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    private void retrieveUsersSearched(String userName){
        Toast.makeText(getContext(),"Searching...",Toast.LENGTH_SHORT).show();

        Query firebaseSearchQuery = mySearchRef.orderByChild("firstName").startAt(userName).endAt(userName + "\uf8ff");

        FirebaseRecyclerAdapter<uploadUserDataClass, searchFriendViewHolder> firebaseRecyclerAdapter = new
                FirebaseRecyclerAdapter<uploadUserDataClass, searchFriendViewHolder>
                        (uploadUserDataClass.class,R.layout.carview_friendsearchlist, searchFriendViewHolder.class,firebaseSearchQuery) {
                    @Override
                    protected void populateViewHolder(final searchFriendViewHolder ViewHolder, uploadUserDataClass model, int position) {
                        positionKey = getRef(position).getKey();

                        friendReqRef = friendSearch_firebaseDatabase.getReference("friend_request")
                                .child(positionKey).child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
                        String UId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                        if(!UId.equals(positionKey)){
                            ViewHolder.UserName(model.getFirstName()+""+model.getLastName());
                            ViewHolder.setProfilePic(model.getProfilePicUrl());

                            friendListRef = friendSearch_firebaseDatabase.getReference("friend List")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(positionKey);
                            friendListRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(snapshot.exists()){
                                        ViewHolder.setButtonText("invisible");
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });


                        }else{
                            ViewHolder.itemView.setVisibility(View.GONE);
                            ViewHolder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0,0));
                        }
                        Button addButton = ViewHolder.myView.findViewById(R.id.friendSearch_addbtn);

                        friendReqRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists()){
                                    addButton.setText("Request Sent");
                                    addButton.setEnabled(false);

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        addButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {



                        mySearchRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                uploadUserDataClass userData = snapshot.getValue(uploadUserDataClass.class);

                                assert userData != null;
                                nameUser = userData.getFirstName() + " "+userData.getLastName();
                                Calendar calendar = Calendar.getInstance();
                                int day = calendar.get(Calendar.DAY_OF_MONTH);
                                int month = calendar.get(Calendar.MONTH);
                                int year = calendar.get(Calendar.YEAR);

                                String dataUser = day + "/" + month +"/" + year;

                                retrieveFriendSearchClass sendDetails = new retrieveFriendSearchClass(nameUser,dataUser);

                                friendReqRef.setValue(sendDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            addButton.setText("Request Sent");
                                            addButton.setEnabled(false);

                                        }
                                    }
                                });

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                            }
                        });
                    }
                };
        friendSearch_recycler.setAdapter(firebaseRecyclerAdapter);

    }
    public static class searchFriendViewHolder extends RecyclerView.ViewHolder{

        View myView;

        public searchFriendViewHolder(@NonNull View itemView){

            super(itemView);
            myView = itemView;
        }
        public void UserName(String userName){
            TextView searchFriend = myView.findViewById(R.id.friendSearch_textView);
            searchFriend.setText(userName);
        }
        public void setButtonText(String type){
            Button searchFriendBtn = myView.findViewById(R.id.friendSearch_btn);
            if(type.equals("invisible")){
                searchFriendBtn.setVisibility(View.INVISIBLE);
            }
        }
        public void setProfilePic(String imageURI){
            ImageView profilePic = myView.findViewById(R.id.friendSearch_profileImg);
            Picasso.get().load(imageURI).fit().centerCrop().into(profilePic);
        }
    }
}