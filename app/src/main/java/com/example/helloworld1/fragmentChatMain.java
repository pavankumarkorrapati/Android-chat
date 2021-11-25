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
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


public class fragmentChatMain extends Fragment {




    public fragmentChatMain() {
        // Required empty public constructor
    }

   private RecyclerView chatMain_recycler;
    private DatabaseReference chatMain_chats;
    private DatabaseReference chatMain_profilePicUrl;

    private FirebaseDatabase chatMain_firebaseDatabase;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_main,container, false);

        chatMain_recycler = view.findViewById(R.id.chatMain_recycler);
        chatMain_recycler.hasFixedSize();
        chatMain_recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        chatMain_firebaseDatabase = FirebaseDatabase.getInstance();

        String myuserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        chatMain_chats = chatMain_firebaseDatabase.getReference("last message")
                .child(myuserId);

        chatMain_profilePicUrl = chatMain_firebaseDatabase.getReference("users");

        chatMain_chats.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    RetrieveLastMessages();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    private void RetrieveLastMessages() {
        FirebaseRecyclerAdapter<retrieveChatMessage, ChatMainUserNodes> firebaseRecyclerAdapter = new
                FirebaseRecyclerAdapter<retrieveChatMessage, ChatMainUserNodes>
                        (retrieveChatMessage.class,R.layout.cardview_chatmain, ChatMainUserNodes.class,chatMain_chats) {
                    @Override
                    protected void populateViewHolder(ChatMainUserNodes holder, retrieveChatMessage model, int position) {

                      holder.SetMyUName(model.getFrom());
                      holder.SetMsg(model.getMessage());

                      String positionKey = getRef(position).getKey();

                      chatMain_profilePicUrl.child(positionKey).child("profilePicUrl").addValueEventListener(new ValueEventListener() {
                          @Override
                          public void onDataChange(@NonNull DataSnapshot snapshot) {
                              String myprofileUrl = snapshot.getValue().toString();
                              if(!myprofileUrl.equals("nothing")){
                                  holder.SetPUrl(myprofileUrl);
                              }
                          }

                          @Override
                          public void onCancelled(@NonNull DatabaseError error) {

                          }
                      });

                      holder.myNView.setOnClickListener(new View.OnClickListener() {
                          @Override
                          public void onClick(View v) {
                              String posKey = getRef(position).getKey();
                              startActivity(new Intent(getActivity(), chatMessageActivity.class).putExtra("userid", posKey));
                          }
                      });

                    }
                };
          chatMain_recycler.setAdapter(firebaseRecyclerAdapter);
    }

    public static class ChatMainUserNodes extends RecyclerView.ViewHolder {

        View myNView;

        public ChatMainUserNodes(@NonNull View itemView) {
            super(itemView);
            myNView = itemView;
        }
        public void SetMyUName(String uName){
            TextView uNameT = myNView.findViewById(R.id.chatMain_userName);
            uNameT.setText(uName);
        }
        public void SetMsg(String Msg){
            TextView uMsg = myNView.findViewById(R.id.chatMain_userMessage);
            uMsg.setText(Msg);
        }
        public void SetPUrl (String PicUrl){
            ImageView i_view = myNView.findViewById(R.id.chatMain_userImage);
            Picasso.get().load(PicUrl).fit().centerInside().into(i_view);
        }
    }
}