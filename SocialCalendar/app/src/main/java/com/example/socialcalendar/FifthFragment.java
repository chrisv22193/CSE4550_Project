package com.example.socialcalendar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ktx.Firebase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FifthFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FifthFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FifthFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FifthFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FifthFragment newInstance(String param1, String param2) {
        FifthFragment fragment = new FifthFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

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
    final static  int Gallery_Pick = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_fifth, container, false);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        settingsUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);
        UserProfileImageRef = FirebaseStorage.getInstance().getReference().child("Profile Images");
        UserProfileBackgroundImageRef = FirebaseStorage.getInstance().getReference().child("Profile Background Images");

        userName = (EditText) v.findViewById(R.id.settings_username);
        userProfName = (EditText) v.findViewById(R.id.settings_full_name);
        userStatus = (EditText) v.findViewById(R.id.settings_status);
        userPhoneNumber = (EditText) v.findViewById(R.id.settings_phone_number);
        userDOB = (EditText) v.findViewById(R.id.settings_users_dob);
        userProfImage = (CircleImageView) v.findViewById(R.id.settings_profile_image);
        userBackgroundImage = (ImageView) v.findViewById(R.id.settings_profile_background);
        saveChangesSettingsButton = (Button) v.findViewById(R.id.update_account_settings_button);
        Logout = v.findViewById(R.id.log_out_link);

        userProfImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, Gallery_Pick);
            }
        });

        userBackgroundImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, Gallery_Pick);
            }
        });

        settingsUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String myProfileImage = dataSnapshot.child("profileimage").getValue().toString();
                    String myProfileBackground = dataSnapshot.child("").getValue().toString();
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

        Logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                startActivity(new Intent(getActivity(), LoginActivity.class));
            }
        });

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Gallery_Pick && resultCode == getActivity().RESULT_OK && data != null){
            Uri ImageUri = data.getData();

            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(getActivity());
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == getActivity().RESULT_OK){
                Uri resultUri = result.getUri();

                final StorageReference filePath = UserProfileImageRef.child(currentUserID + ".jpg");

                filePath.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                final String downloadUrl = uri.toString();
                                settingsUserRef.child("profileimage").setValue(downloadUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Toast.makeText(getActivity(),"Image stored", Toast.LENGTH_SHORT).show();
                                        }
                                        else{
                                            String message = task.getException().getMessage();
                                            Toast.makeText(getActivity(), "Error: " + message, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        });
                    }
                });
            }
            else{
                Toast.makeText(getActivity(), "Error occurred: Image could not be cropped. Try again", Toast.LENGTH_SHORT);
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
            Toast.makeText(getActivity(), "Please write your username", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(profilename)){
            Toast.makeText(getActivity(), "Please write your full name", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(status)){
            Toast.makeText(getActivity(), "Please write your status", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(phonenumber)){
            Toast.makeText(getActivity(), "Please write your phone number", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(birthdate)){
            Toast.makeText(getActivity(), "Please write your birthdate", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(getActivity(), "Account information were saved successfully", Toast.LENGTH_SHORT);
                }
                else{
                    Toast.makeText(getActivity(), "Error occurred while saving account information", Toast.LENGTH_SHORT);
                }
            }
        });

    }

    private void SendUserToMainActivity() {
        Intent settingsIntent = new Intent(getActivity(), MainActivity.class);
        settingsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(settingsIntent);
    }
}