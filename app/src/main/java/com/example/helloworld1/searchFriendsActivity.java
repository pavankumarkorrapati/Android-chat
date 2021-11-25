package com.example.helloworld1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;

public class searchFriendsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_friends);

        friendRequestFragment frag = new friendRequestFragment();
        FragmentManager f_man = getSupportFragmentManager();
        f_man.beginTransaction().replace(R.id.searchFriend_frameLayout, frag).commit();
    }
}