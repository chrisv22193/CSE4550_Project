package com.example.socialcalendar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileSettingsActivity extends AppCompatActivity {

    private EditText userName, userProfName, userStatus, userPhoneNumber, userDOB;
    private Button saveChangesSettingsButton;
    private CircleImageView userProfImage;
    private ImageView userBackgroundImage;
    private TextView Logout;

    private FirebaseAuth mAuth;
    private DatabaseReference settingsUserRef;
    private StorageReference UserProfileImageRef;
    private StorageReference UserProfileBackgroundImageRef;

    private String currentUserID;
    final static int Gallery_Pick = 1;
    final static int Gallery_Pick_2 = 1;
    private Uri ImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_settings);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        settingsUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);
        UserProfileImageRef = FirebaseStorage.getInstance().getReference().child("Profile Images");
        UserProfileBackgroundImageRef = FirebaseStorage.getInstance().getReference().child("Profile Background Images");

        userName = (EditText) findViewById(R.id.settings_username);
        userProfName = (EditText) findViewById(R.id.settings_full_name);
        userStatus = (EditText) findViewById(R.id.settings_status);
        userPhoneNumber = (EditText) findViewById(R.id.settings_phone_number);
        userDOB = (EditText) findViewById(R.id.settings_users_dob);
        userProfImage = (CircleImageView) findViewById(R.id.settings_profile_image);
        userBackgroundImage = (ImageView) findViewById(R.id.settings_profile_background);
        saveChangesSettingsButton = (Button) findViewById(R.id.update_account_settings_button);
        Logout = findViewById(R.id.log_out_link);

        userProfImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryImageIntent = new Intent();
                galleryImageIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryImageIntent.setType("image/*");
                startActivityForResult(galleryImageIntent, Gallery_Pick);
            }
        });

        userBackgroundImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryBackgroundIntent = new Intent();
                galleryBackgroundIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryBackgroundIntent.setType("image/*");
                startActivityForResult(galleryBackgroundIntent, Gallery_Pick_2);
            }
        });

        Logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                startActivity(new Intent(ProfileSettingsActivity.this, LoginActivity.class));
            }
        });

        settingsUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String myProfileImage = dataSnapshot.child("profileimage").getValue().toString();
                    String myProfileBackground = dataSnapshot.child("profilebackgroundimage").getValue().toString();
                    String myUserName = dataSnapshot.child("username").getValue().toString();
                    String myProfileName = dataSnapshot.child("fullname").getValue().toString();
                    String myProfileStatus = dataSnapshot.child("status").getValue().toString();
                    String myUserPhoneNumber = dataSnapshot.child("phonenumber").getValue().toString();
                    String myUserDOB = dataSnapshot.child("dob").getValue().toString();

                    Picasso.get().load(myProfileImage).placeholder(R.drawable.profile).into(userProfImage);
                    Picasso.get().load(myProfileBackground).placeholder(R.drawable.background).into(userBackgroundImage);
                    userName.setText(myUserName);
                    userProfName.setText(myProfileName);
                    userStatus.setText(myProfileStatus);
                    userPhoneNumber.setText(myUserPhoneNumber);
                    userDOB.setText(myUserDOB);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        saveChangesSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidateAccountInfo();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Gallery_Pick && resultCode == RESULT_OK && data != null){
            ImageUri = data.getData();

            CropImage.activity(ImageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult Imageresult = CropImage.getActivityResult(data);


            if (resultCode == RESULT_OK){
                Uri resultUriImage = Imageresult.getUri();

                final StorageReference filepathImage = UserProfileImageRef.child(currentUserID + ".jpg");
                filepathImage.putFile(resultUriImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        filepathImage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                final String downloadImageUrl = uri.toString();
                                settingsUserRef.child("profileimage").setValue(downloadImageUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Toast.makeText(ProfileSettingsActivity.this,"Image stored", Toast.LENGTH_SHORT).show();
                                        }
                                        else{
                                            String message = task.getException().getMessage();
                                            Toast.makeText(ProfileSettingsActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        });
                    }
                });
            }
            else{
                Toast.makeText(ProfileSettingsActivity.this, "Error occurred: Image could not be cropped. Try again", Toast.LENGTH_SHORT);
            }
        }

        if (requestCode == Gallery_Pick_2 && requestCode == RESULT_OK && data != null){
            Uri BackgroundUri = data.getData();

            CropImage.activity(BackgroundUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult Backgroundresult = CropImage.getActivityResult(data);


            if (resultCode == RESULT_OK){
                Uri resultUriBackground = Backgroundresult.getUri();

                final StorageReference filePathBackground = UserProfileBackgroundImageRef.child(currentUserID + ".jpg");

                filePathBackground.putFile(resultUriBackground).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        filePathBackground.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                final String downloadBackgroundUrl = uri.toString();
                                settingsUserRef.child("profilebackgroundimage").setValue(downloadBackgroundUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Toast.makeText(ProfileSettingsActivity.this,"Image stored", Toast.LENGTH_SHORT).show();
                                        }
                                        else{
                                            String message = task.getException().getMessage();
                                            Toast.makeText(ProfileSettingsActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        });
                    }
                });
            }
            else{
                Toast.makeText(ProfileSettingsActivity.this, "Error occurred: Image could not be cropped. Try again", Toast.LENGTH_SHORT);
            }
        }
    }

    private void ValidateAccountInfo() {
        String username = userName.getText().toString();
        String profilename = userProfName.getText().toString();
        String status = userStatus.getText().toString();
        String phonenumber = userPhoneNumber.getText().toString();
        String birthdate = userDOB.getText().toString();

        if (TextUtils.isEmpty(username)) {
            Toast.makeText(ProfileSettingsActivity.this, "Please write your username", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(profilename)){
            Toast.makeText(ProfileSettingsActivity.this, "Please write your full name", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(status)){
            Toast.makeText(ProfileSettingsActivity.this, "Please write your status", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(phonenumber)){
            Toast.makeText(ProfileSettingsActivity.this, "Please write your phone number", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(birthdate)){
            Toast.makeText(ProfileSettingsActivity.this, "Please write your birthdate", Toast.LENGTH_SHORT).show();
        }
        else{
            UpdateAccountInfo(username, profilename, status, birthdate, phonenumber);
        }

    }

    private void UpdateAccountInfo(String username, String profilename, String status, String birthdate, String phonenumber) {
        HashMap userMap = new HashMap();
        userMap.put("username", username);
        userMap.put("fullname", profilename);
        userMap.put("phonenumber", phonenumber);
        userMap.put("dob", birthdate);
        userMap.put("status", status);
        settingsUserRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if(task.isSuccessful()){
                    SendUserToMainActivity();
                    Toast.makeText(ProfileSettingsActivity.this, "Account information were saved successfully", Toast.LENGTH_SHORT);
                }
                else{
                    Toast.makeText(ProfileSettingsActivity.this, "Error occurred while saving account information", Toast.LENGTH_SHORT);
                }
            }
        });

    }

    private void SendUserToMainActivity() {
        Intent settingsIntent = new Intent(ProfileSettingsActivity.this, MainActivity.class);
        settingsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(settingsIntent);
    }
}