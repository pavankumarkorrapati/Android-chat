package com.example.helloworld1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ColorMatrix;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class registerUserActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    private EditText registerActivity_firstName, registerActivity_lastName, registerActivity_DOB,
            registerActivity_emailID, registerActivity_password;
    private TextView registerActivity_registered;
    private ImageView registerActivity_profilePic;
    private Button registerActivity_registerBtn;

    private FirebaseAuth registerActivity_firebaseAuth;
    private FirebaseDatabase registerActivity_firebaseDatabase;
    private FirebaseStorage registerActivity_firebaseStorage;
    private String profilepicstatus;
    private DatabaseReference myUserRef;
    private StorageReference myUserStorageRef;
    final static int userGalleryDialogue_bundle = 1;
    private Uri userImageUri;
    private byte[] imageArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        getSupportActionBar().hide();

        CastComponents();


        profilepicstatus = "nothing";


        registerActivity_DOB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int day = 1;
                int month = 1;
                int year = 1990;
                DatePickerDialog datePickerDialog = new DatePickerDialog(registerUserActivity.this,
                        registerUserActivity.this, year, month, day);

                datePickerDialog.show();
            }
        });

        registerActivity_registerBtn.setOnClickListener(new  View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    String firstNam = registerActivity_firstName.getText().toString().trim().toLowerCase();
                    String lastNam = registerActivity_lastName.getText().toString().trim().toLowerCase();
                    String pass = registerActivity_password.getText().toString();

                    if (CheckFirstName(firstNam) && CheckLastName(lastNam) && CheckPassword(pass) ) {
                        String emailid = registerActivity_emailID.getText().toString().trim();
                        SignupUserMethod(emailid, pass);
                    } else {
                        Toast.makeText(registerUserActivity.this, "Please give correct credentials in every field", Toast.LENGTH_LONG).show();

                    }
                }
        });
        registerActivity_profilePic.setOnClickListener((v) -> {
            Intent userPicDialogue = new Intent();
            userPicDialogue.setAction(Intent.ACTION_GET_CONTENT);
            userPicDialogue.setType("image/*");
            startActivityForResult(userPicDialogue, userGalleryDialogue_bundle);
        });

    }


    private void SignupUserMethod(String emailid, String pass) {
        if (!profilepicstatus.equals("nothing")) {
            registerActivity_firebaseAuth.createUserWithEmailAndPassword(emailid, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(registerUserActivity.this, "User registered Successfully", Toast.LENGTH_SHORT).show();
                        SignInMethod();
                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(registerUserActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            Toast.makeText(this,"Setup a Profile picture to Continue",Toast.LENGTH_SHORT).show();
        }
    }

    private void SignInMethod() {
        String emailid = registerActivity_emailID.getText().toString().trim();
        String pass = registerActivity_password.getText().toString().trim();
        registerActivity_firebaseAuth.signInWithEmailAndPassword(emailid, pass).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {

                uploadUserProfileImage(imageArray);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(registerUserActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void UploadUserData() {
            String firstNam = registerActivity_firstName.getText().toString().trim().toLowerCase();
            String lastNam = registerActivity_lastName.getText().toString().trim().toLowerCase();
            String DOB = registerActivity_DOB.getText().toString().trim();
            String ProfilePicURL = profilepicstatus;
        myUserRef = registerActivity_firebaseDatabase.getReference("users")
                .child(registerActivity_firebaseAuth.getCurrentUser().getUid());

            uploadUserDataClass userData = new uploadUserDataClass(firstNam, lastNam, DOB, ProfilePicURL);


            myUserRef.setValue(userData).addOnCompleteListener((task) -> {
                if(task.isSuccessful()){
                    Toast.makeText(registerUserActivity.this,"user Data Uploaded Successfully",Toast.LENGTH_SHORT).show();
                    registerActivity_firebaseAuth.signOut();
                    startActivity(new Intent(registerUserActivity.this,MainActivity.class));
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(registerUserActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }


    private void CastComponents() {
        registerActivity_firstName = (EditText) findViewById(R.id.registerActivity_firstName);
        registerActivity_lastName = (EditText) findViewById(R.id.registerActivity_lastName);
        registerActivity_DOB = (EditText) findViewById(R.id.registerActivity_DOB);
        registerActivity_emailID = (EditText) findViewById(R.id.registerActivity_emailID);
        registerActivity_password = (EditText) findViewById(R.id.registerActivity_password);
        registerActivity_profilePic = (ImageView) findViewById(R.id.registerActivity_profilepic);
        registerActivity_registered = (TextView) findViewById(R.id.registerActivity_registered);
        registerActivity_registerBtn = (Button) findViewById(R.id.registerActivity_registerBtn);

        registerActivity_firebaseAuth = FirebaseAuth.getInstance();
        registerActivity_firebaseDatabase = FirebaseDatabase.getInstance();
        registerActivity_firebaseStorage = FirebaseStorage.getInstance();
    }


    private boolean CheckFirstName(String firstName) {
        int length = firstName.length();
        if (length < 2) {
            return false;
        } else if(length > 2) {
            return true;
        }else{
            return true;
        }
    }

    private boolean CheckLastName(String lastName) {
        int length = lastName.length();
        if (length < 2) {
            return false;
        } else if(length > 2) {
            return true;
        }else{
            return true;
        }

    }

    private boolean CheckPassword(String password) {
        int length = password.length();
        if (length < 8) {
            return false;
        } else if (length >= 8){
            return true;
        }else{
            return true;
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        month = month + 1;
        registerActivity_DOB.setText(dayOfMonth + "/" + month + "/" + year);
    }

    //load image part and upload

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == userGalleryDialogue_bundle && resultCode == RESULT_OK && data != null) {
            userImageUri = data.getData();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), userImageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            changeOrientationUserImage(bitmap);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void changeOrientationUserImage(Bitmap userImageBitmap) {
        InputStream inputStream;
        try {
            inputStream = getContentResolver().openInputStream(userImageUri);
            ExifInterface exifInterface = new ExifInterface(inputStream);
            int image_orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            Bitmap rotatedImage = rotatedImage(userImageBitmap, image_orientation);
            compressUserImage(rotatedImage);
            registerActivity_profilePic.setImageBitmap(rotatedImage);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Bitmap rotatedImage(Bitmap userImageBitmap, int orientation) {
        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_NORMAL:
                return userImageBitmap;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.setRotate(180);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                matrix.setRotate(90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(-90);
                break;
            default:
                return userImageBitmap;

        }
        try {
            Bitmap rotated_bitmap = Bitmap.createBitmap(userImageBitmap, 0, 0, userImageBitmap.getWidth(), userImageBitmap.getHeight(), matrix, true);
            return rotated_bitmap;
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }
        private void compressUserImage(Bitmap userImageBitmap){
            ByteArrayOutputStream imageBaos = new ByteArrayOutputStream();
            userImageBitmap.compress(Bitmap.CompressFormat.JPEG,10,imageBaos);
            imageArray = imageBaos.toByteArray();
            profilepicstatus ="loaded";
        }

        private void uploadUserProfileImage(byte[] userImageArray){
            myUserStorageRef = registerActivity_firebaseStorage.getReference("registered users").child("profile pic")
                    .child(registerActivity_firebaseAuth.getCurrentUser().getUid() + ".jpg");

         UploadTask uploadTask = myUserStorageRef.putBytes(userImageArray);

         uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
             @Override
             public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                 Toast.makeText(registerUserActivity.this,"Profile Picture Updated Successfully",Toast.LENGTH_SHORT).show();
                 Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                 while (!urlTask.isSuccessful());
                 Uri downloadUri = urlTask.getResult();
                 profilepicstatus = downloadUri.toString();
                 UploadUserData();
             }
         }).addOnFailureListener(new OnFailureListener() {
             @Override
             public void onFailure(@NonNull Exception e) {
                 Toast.makeText(registerUserActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
             }
         });
        }



        }





