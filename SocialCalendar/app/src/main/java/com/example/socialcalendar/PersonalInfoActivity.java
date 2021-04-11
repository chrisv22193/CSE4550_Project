package com.example.socialcalendar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class PersonalInfoActivity extends AppCompatActivity {

    private EditText userPhoneNumber, userDOB;
    private Button saveChangesSettingsButton;

    private FirebaseAuth mAuth;
    private DatabaseReference settingsUserRef;

    private String currentUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        settingsUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);

        userPhoneNumber = (EditText) findViewById(R.id.personal_info_settings_phone_number);
        userDOB = (EditText) findViewById(R.id.personal_info_settings_users_dob);
        saveChangesSettingsButton = (Button) findViewById(R.id.update_account_settings_button);

        settingsUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String myUserPhoneNumber = dataSnapshot.child("phonenumber").getValue().toString();
                    String myUserDOB = dataSnapshot.child("dob").getValue().toString();

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

    private void ValidateAccountInfo() {
        String phonenumber = userPhoneNumber.getText().toString();
        String birthdate = userDOB.getText().toString();

        if (TextUtils.isEmpty(phonenumber)){
            Toast.makeText(PersonalInfoActivity.this, "Please write your phone number", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(birthdate)){
            Toast.makeText(PersonalInfoActivity.this, "Please write your birthdate", Toast.LENGTH_SHORT).show();
        }
        else{
            UpdateAccountInfo(birthdate, phonenumber);
        }

    }

    private void UpdateAccountInfo(String birthdate, String phonenumber) {
        HashMap userMap = new HashMap();
        userMap.put("phonenumber", phonenumber);
        userMap.put("dob", birthdate);
        settingsUserRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if(task.isSuccessful()){
                    SendUserToMainActivity();
                    Toast.makeText(PersonalInfoActivity.this, "Account information were saved successfully", Toast.LENGTH_SHORT);
                }
                else{
                    Toast.makeText(PersonalInfoActivity.this, "Error occurred while saving account information", Toast.LENGTH_SHORT);
                }
            }
        });

    }

    private void SendUserToMainActivity() {
        Intent settingsIntent = new Intent(PersonalInfoActivity.this, MainActivity.class);
        settingsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(settingsIntent);
    }
}