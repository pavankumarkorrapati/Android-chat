package com.example.helloworld1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class chatMessageActivity extends AppCompatActivity {

    private RecyclerView userChat_recycler;
    private EditText userChat_msgBox;
    private ImageButton userChat_sendBtn;
    private Calendar calendar;
    private String positionKeyFriend, positionKeyMsg, friendUserName;

    private DatabaseReference userChat_chatMsgRef, userChat_userNameRef;
    private FirebaseDatabase userChat_firebaseDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_message);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        positionKeyFriend = getIntent().getStringExtra("userid");

        userChat_recycler = findViewById(R.id.chatMessage_recyclerView);
        userChat_recycler.hasFixedSize();
        userChat_recycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        calendar = Calendar.getInstance();
        userChat_firebaseDatabase = FirebaseDatabase.getInstance();
        userChat_msgBox = findViewById(R.id.chatMessage_msgBox);
        userChat_sendBtn = findViewById(R.id.chatMessage_sendBtn);

        userChat_chatMsgRef = userChat_firebaseDatabase.getReference("chats")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                 .child(positionKeyFriend);

        userChat_userNameRef = userChat_firebaseDatabase.getReference("users");

        userChat_userNameRef.child(positionKeyFriend)
                .child("firstName").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    friendUserName = snapshot.getValue().toString();
                    setTitle(friendUserName);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(chatMessageActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();

            }
        });
        userChat_sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userChatMsg = userChat_msgBox.getText().toString();
                if(userChatMsg.equals("")){
                    Toast.makeText(chatMessageActivity.this,"Message is empty",Toast.LENGTH_SHORT).show();
                }else{
                    userChat_userNameRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child("first").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){
                                String myUserName;
                                myUserName = snapshot.getValue().toString();

                                DateFormat dateFormat = new SimpleDateFormat("HH:mm");
                                String time = dateFormat.format(calendar.getTime());

                                String message = userChat_msgBox.getText().toString();
                                SendUser(message, time, myUserName);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        });
           userChat_chatMsgRef.addValueEventListener(new ValueEventListener() {
               @Override
               public void onDataChange(@NonNull DataSnapshot snapshot) {
                   if(snapshot.exists()){
                       RetrieveChatMessage();
                   }
               }

               @Override
               public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(chatMessageActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
               }
           });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
        
    }
    private void RetrieveChatMessage(){
        final FirebaseRecyclerAdapter<sendUserChatReceiveClass, UserChatViewHolder> firebaseRecyclerAdapter = new
                FirebaseRecyclerAdapter<sendUserChatReceiveClass, UserChatViewHolder>
                        (sendUserChatReceiveClass.class, R.layout.sentchat_cardview, UserChatViewHolder.class, userChat_chatMsgRef) {
                    @Override
                    protected void populateViewHolder(UserChatViewHolder holder, sendUserChatReceiveClass model, int position) {
                         positionKeyMsg = model.getUserID();
                         if(positionKeyMsg.equals(positionKeyFriend)){
                             holder.ChangeOrientationLeft();
                             holder.LastMessage(model.getMessage());
                             holder.UserTime(model.getTime());
                         }else{
                             holder.ChangeOrientationRight();
                             holder.LastMessage(model.getMessage());
                             holder.UserTime(model.getTime());
                         }
                    }
                };
        userChat_recycler.setAdapter(firebaseRecyclerAdapter);
    }

    private void SendUser(String message, String time, String myUserName) {
        userChat_msgBox.setText("");

        DatabaseReference myLastMsgRef = userChat_firebaseDatabase.getReference("last message")
                .child(positionKeyFriend).child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        DatabaseReference myOWnMsgRef = userChat_firebaseDatabase.getReference("last message")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(positionKeyFriend);

        DatabaseReference friendReceieveMsgRef = userChat_firebaseDatabase.getReference("chats")
                .child(positionKeyFriend).child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        DatabaseReference myReceiveMsgRef = userChat_firebaseDatabase.getReference("chats")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(positionKeyFriend);

        retrieveChatMessage lastMsg = new retrieveChatMessage(myUserName, message, time);

        myLastMsgRef.setValue(lastMsg).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(chatMessageActivity.this,"Message could not be delivered",Toast.LENGTH_SHORT).show();
            }
        });
        retrieveChatMessage myOwnMsg = new retrieveChatMessage(friendUserName, message, time);

        myOWnMsgRef.setValue(myOwnMsg).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(chatMessageActivity.this, "Message could not be delivered", Toast.LENGTH_SHORT).show();
            }
        });

        sendUserChatReceiveClass friendMsg = new sendUserChatReceiveClass(FirebaseAuth
                .getInstance().getCurrentUser().getUid(), message, time);
        friendReceieveMsgRef.push().setValue(friendMsg).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
             Toast.makeText(chatMessageActivity.this,e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        sendUserChatReceiveClass myClassMsg = new sendUserChatReceiveClass(FirebaseAuth
        .getInstance().getCurrentUser().getUid(), message, time);
        myReceiveMsgRef.push().setValue(myClassMsg).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(chatMessageActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }


    public static class UserChatViewHolder extends RecyclerView.ViewHolder{
        View myView;

        public UserChatViewHolder(@NonNull View itemView){
            super(itemView);
            myView = itemView;
        }
        public void LastMessage(String Message){
            TextView sentChat_message = myView.findViewById(R.id.sentChat_message);
            sentChat_message.setText(Message);
        }
        public void UserTime(String Time){
            TextView sentChat_time = myView.findViewById(R.id.sentChat_Time);
            sentChat_time.setText(Time);
        }
        public void ChangeOrientationLeft(){
            LinearLayout cardView = myView.findViewById(R.id.sentChat_layout);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT
            , FrameLayout.LayoutParams.WRAP_CONTENT);
            params.setMarginEnd(100);
            cardView.setLayoutParams(params);
            cardView.setBackground(ContextCompat.getDrawable(myView.getContext(), R.drawable.layout_bg_white));
        }
        public void ChangeOrientationRight(){
            LinearLayout cardView = myView.findViewById(R.id.sentChat_layout);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT
                    , FrameLayout.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.END;
            params.setMarginStart(100);
            cardView.setLayoutParams(params);
            cardView.setBackground(ContextCompat.getDrawable(myView.getContext(), R.drawable.layout_bg_green));
        }
    }

}