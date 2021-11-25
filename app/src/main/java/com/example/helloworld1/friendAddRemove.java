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
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Calendar;


public class friendAddRemove extends Fragment {
   private RecyclerView friendAddRemove_recycler;

   private FirebaseDatabase friendAddRemove_firebaseDatabase;
   private DatabaseReference friendAddRemove_dataRef, friendAddRemove_profilePic;
   private String positionKey, myName;


    public friendAddRemove() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_friend_add_remove, container, false);

        //Cast all Components
        friendAddRemove_recycler = view.findViewById(R.id.friendAddRemove_recycler);
        friendAddRemove_recycler.hasFixedSize();
        friendAddRemove_recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        friendAddRemove_firebaseDatabase = FirebaseDatabase.getInstance();

        String myUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        //create database references
        friendAddRemove_dataRef = friendAddRemove_firebaseDatabase.getReference("friend request")
                .child(myUserId);
        friendAddRemove_profilePic = friendAddRemove_firebaseDatabase.getReference("users");

        friendAddRemove_profilePic.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        uploadUserDataClass getName = snapshot.getValue(uploadUserDataClass.class);
                        myName = getName.getFirstName() + " " + getName.getLastName();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        friendAddRemove_dataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                 if(snapshot.exists()){
                     RetreiveFriendRequest();
                 }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(),error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });



        return view;
    }

    private void RetreiveFriendRequest() {

        FirebaseRecyclerAdapter<retrieveFriendSearchClass, FriendAddRemoveNodeViewHolder> firebaseRecyclerAdapter = new
                FirebaseRecyclerAdapter<retrieveFriendSearchClass, FriendAddRemoveNodeViewHolder>
                        (retrieveFriendSearchClass.class, R.layout.cardview_addremove, FriendAddRemoveNodeViewHolder.class, friendAddRemove_dataRef) {
                    @Override
                    protected void populateViewHolder(final FriendAddRemoveNodeViewHolder viewHolder, final retrieveFriendSearchClass model, int position) {
                      viewHolder.UserName(model.getUserName());
                      viewHolder.SentDate(model.getSentDate());
                      positionKey = getRef(position).getKey();

                      //check profile pic
                        friendAddRemove_profilePic.child(positionKey).child("profilePicUrl").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String userPhoto = snapshot.getValue().toString();
                                if(!userPhoto.equals("null"))
                                    viewHolder.ProfilePic((userPhoto));
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                         Button acceptButton = viewHolder.myView.findViewById(R.id.friendAddRemove_acceptBtn);
                         Button rejectButton = viewHolder.myView.findViewById(R.id.friendAddRemove_rejectBtn);
                         acceptButton.setOnClickListener(new View.OnClickListener() {
                             @Override
                             public void onClick(View v) {

                                 //set friendlist database ref
                                 DatabaseReference myFriendList = friendAddRemove_firebaseDatabase.getReference("friend list")
                                         .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(positionKey);

                                 //set friends database ref
                                 DatabaseReference myFriendListFrnd = friendAddRemove_firebaseDatabase.getReference("friend list")
                                         .child(positionKey)
                                         .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                 Calendar cal  = Calendar.getInstance();

                                 int year = cal.get(Calendar.YEAR);
                                 int month = cal.get(Calendar.MONTH);
                                 month = month +1;
                                 int day = cal.get(Calendar.DAY_OF_MONTH);

                                 String date = day + "/" + month + "/"  + year;

                                 String RequestersName = model.getUserName();

                                 retrieveFriendSearchClass setFriendDBS = new retrieveFriendSearchClass(RequestersName, date);

                                 myFriendList.setValue(setFriendDBS).addOnCompleteListener(new OnCompleteListener<Void>() {
                                     @Override
                                     public void onComplete(@NonNull Task<Void> task) {
                                          if(task.isSuccessful()){
                                              Toast.makeText(getContext(),"Friend Added Successfully", Toast.LENGTH_SHORT).show();
                                          }else {
                                              Toast.makeText(getContext(),"Error Adding Friend",Toast.LENGTH_SHORT).show();
                                          }
                                     }
                                 });

                                 retrieveFriendSearchClass setMyName =  new retrieveFriendSearchClass(myName, date);

                                 myFriendList.setValue(setMyName).addOnCompleteListener(new OnCompleteListener<Void>() {
                                     @Override
                                     public void onComplete(@NonNull Task<Void> task) {
                                         if(task.isSuccessful()){
                                             friendAddRemove_dataRef.child(positionKey).removeValue();
                                         }
                                     }
                                 });

                             }
                         });
                            rejectButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    friendAddRemove_dataRef.child(positionKey).removeValue();
                                }
                            });

                    }
                };
                                friendAddRemove_recycler.setAdapter(firebaseRecyclerAdapter);
    }
   public static class FriendAddRemoveNodeViewHolder extends RecyclerView.ViewHolder{
        View myView;

       public FriendAddRemoveNodeViewHolder(@NonNull View itemView) {
           super(itemView);
           myView = itemView;
       }
       public void UserName(String userName){
           TextView friendAddRemove_Name = myView.findViewById(R.id.friendAddRemove_userName);
           friendAddRemove_Name.setText(userName);
       }
       public void SentDate(String date){
           TextView friendAddRemove_date = myView.findViewById(R.id.friendAddRemove_sentOn);
           friendAddRemove_date.setText("Sent on: "+date);
       }
       public void ProfilePic(String imageUrl){
           ImageView friendAddRemove_profilePic = myView.findViewById(R.id.friendAddRemove_profileImg);
           Picasso.get().load(imageUrl).fit().centerCrop().into(friendAddRemove_profilePic);
       }

   }

}