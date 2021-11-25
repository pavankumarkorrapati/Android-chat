package com.example.helloworld1;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class chatMainActivity extends AppCompatActivity {

    private BottomNavigationView.OnNavigationItemSelectedListener monNavigationItemSelectedListener
            = (item) -> {
        switch (item.getItemId()) {
            case R.id.navigation_chat:
                fragmentChatMain chatMessage = new fragmentChatMain();
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.chatMessage_frameLayout, chatMessage).commit();

                return true;
            case R.id.navigation_friendList:
                fragmentFriendList friendList = new fragmentFriendList();
                FragmentManager fragmentManager1 = getSupportFragmentManager();
                fragmentManager1.beginTransaction().replace(R.id.chatMessage_frameLayout, friendList).commit();

                return true;

            case R.id.navigation_friendRequest:

                friendAddRemove frndAdd = new friendAddRemove();
                FragmentManager fragmentManager2 = getSupportFragmentManager();
                fragmentManager2.beginTransaction().replace(R.id.chatMessage_frameLayout,frndAdd).commit();

                return true;
        }
        return false;

    };
    private BottomNavigationView navView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_main);
        navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(monNavigationItemSelectedListener);

        fragmentChatMain chatMessage = new fragmentChatMain();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.chatMessage_frameLayout, chatMessage).commit();

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
         getMenuInflater().inflate(R.menu.app_bar_menu,menu);
         return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if(id==R.id.chatMenu_editprofile){
         startActivity(new Intent(chatMainActivity.this,registerUserActivity.class));
        }

        if(id==R.id.chatMenu_findfriends){
           startActivity(new Intent(chatMainActivity.this, searchFriendsActivity.class));
           return true;
        }

        if(id==R.id.chatMenu_logout){
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(this,"Logged out successfully", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(chatMainActivity.this,MainActivity.class));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}