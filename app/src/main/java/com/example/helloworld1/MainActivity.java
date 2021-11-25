package com.example.helloworld1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class MainActivity extends AppCompatActivity {

    private EditText loginActivity_emailID,loginActivity_password;
    private Button loginActivity_loginBtn;
    private TextView loginActivity_newUser;
    private FirebaseAuth loginActivity_firebaseAuth;
    private GoogleSignInClient googleSignInClient;
    private static final int RC_SIGN_IN =99;
    private SignInButton googleSignInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        getSupportActionBar().hide();

        CastComponents();

        if(loginActivity_firebaseAuth.getCurrentUser() != null){
            startActivity(new Intent(MainActivity.this,chatMainActivity.class));
            finish();
        }

        loginActivity_newUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,registerUserActivity.class));
            }
        });
        loginActivity_loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailID = loginActivity_emailID.getText().toString().trim();
                String password = loginActivity_password.getText().toString();
                if (emailID.equals("") && password.equals("")) {
                    Toast.makeText(MainActivity.this,"Email or password cannot be empty",Toast.LENGTH_SHORT).show();
                }else {
                  loginUserMethod(emailID, password);
                }
            }
        });

    }

    private void signInToGoogle() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                //...
            }

        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(),null);
        loginActivity_firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(MainActivity.this,"Signed In Successfully",Toast.LENGTH_SHORT).show();
                            FirebaseUser user = loginActivity_firebaseAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                           Toast.makeText(MainActivity.this,"Failed to Login with Google",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        if (acct != null) {
            String personName = acct.getDisplayName();
            String personGivenName = acct.getGivenName();
            String personFamilyName = acct.getFamilyName();
            String personEmail = acct.getEmail();
            String personId = acct.getId();
            Uri personPhoto = acct.getPhotoUrl();

            String[] name = personName.split("\\s+");
            String firstName = name[0];
            String lastName = name[1];

            Intent changeIntent = new Intent(MainActivity.this,registerUserActivity.class);
            changeIntent.putExtra("FirstName",firstName);
            changeIntent.putExtra("LastName",lastName);
            changeIntent.putExtra("PhotoUri",personPhoto.toString());
            changeIntent.putExtra("typeLogin","google");
            startActivity(changeIntent);
            finish();

        }

    }


    private void loginUserMethod(String userID , String password){
        loginActivity_firebaseAuth.signInWithEmailAndPassword(userID, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
             if(task.isSuccessful()){
                 Toast.makeText(MainActivity.this,"Logged In Successfully",Toast.LENGTH_SHORT).show();
                 startActivity(new Intent(MainActivity.this,chatMainActivity.class));
                 finish();
             }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
              Toast.makeText(MainActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });

  }


    private void CastComponents(){
        loginActivity_emailID = (EditText) findViewById(R.id.loginActivity_emailID);
        loginActivity_password = (EditText) findViewById(R.id.loginActivity_password);
        loginActivity_loginBtn = (Button) findViewById(R.id.loginActivity_loginBtn);
        loginActivity_newUser = (TextView) findViewById(R.id.loginActivity_registerUserText);
        loginActivity_firebaseAuth = FirebaseAuth.getInstance();



    }
}