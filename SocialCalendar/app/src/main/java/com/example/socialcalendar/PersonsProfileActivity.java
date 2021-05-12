package com.example.socialcalendar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class PersonsProfileActivity extends AppCompatActivity {
    private TextView userName, userProfName, userBio;
    private CircleImageView userProfileImage;
    private Button SendFriendRequestButton, DeclineFriendRequestButton;

    private DatabaseReference FriendRequestRef, UsersRef, FriendsRef;
    private FirebaseAuth mAuth;

    private String senderUserID, receiverUserID, current_state, saveCurrentDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_persons_profile);

        mAuth = FirebaseAuth.getInstance();
        senderUserID = mAuth.getCurrentUser().getUid();

        receiverUserID = getIntent().getExtras().get("visit_user_id").toString();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        FriendRequestRef = FirebaseDatabase.getInstance().getReference().child("FriendRequest");
        FriendsRef = FirebaseDatabase.getInstance().getReference().child("Friends");


        InitializeFields();

        UsersRef.child(receiverUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String myProfileImage = dataSnapshot.child("profileimage").getValue().toString();
                    String myUserName = dataSnapshot.child("username").getValue().toString();
                    String myProfileName = dataSnapshot.child("fullname").getValue().toString();
                    String myProfileStatus = dataSnapshot.child("status").getValue().toString();

                    Picasso.get().load(myProfileImage).placeholder(R.drawable.profile).into(userProfileImage);
                    userName.setText("@" + myUserName);
                    userProfName.setText(myProfileName);
                    userBio.setText(myProfileStatus);

                    MaintenanceOfButtons();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        DeclineFriendRequestButton.setVisibility(View.INVISIBLE);
        DeclineFriendRequestButton.setEnabled(false);

        if(!senderUserID.equals(receiverUserID)){
            SendFriendRequestButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SendFriendRequestButton.setEnabled(false);

                    if(current_state.equals("not_friends")){
                        SendFriendRequestToAPerson();
                    }
                    if(current_state.equals("request_sent")){
                        CancelFriendRequest();
                    }
                    if(current_state.equals("request_received")){
                        AcceptFriendRequest();
                    }
                    if(current_state.equals("friends")){
                        UnfriendExistingFriend();
                    }
                }
            });
        }
        else{
            DeclineFriendRequestButton.setVisibility(View.INVISIBLE);
            SendFriendRequestButton.setVisibility(View.INVISIBLE);
        }


    }

    private void UnfriendExistingFriend() {
        FriendsRef.child(senderUserID).child(receiverUserID)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                        if(task.isSuccessful()){
                            FriendsRef.child(receiverUserID).child(senderUserID)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull @NotNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                SendFriendRequestButton.setEnabled(true);
                                                current_state = "not_friends";
                                                SendFriendRequestButton.setText("Send Friend Request");
                                                SendFriendRequestButton.setBackgroundColor(Color.parseColor("#59BD2E"));

                                                DeclineFriendRequestButton.setVisibility(View.INVISIBLE);
                                                DeclineFriendRequestButton.setEnabled(false);

                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void AcceptFriendRequest() {
        Calendar callForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMMM dd, yyyy");
        saveCurrentDate = currentDate.format(callForDate.getTime());

        FriendsRef.child(senderUserID).child(receiverUserID).child("date").setValue(saveCurrentDate)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                        if(task.isSuccessful()){
                            FriendsRef.child(receiverUserID).child(senderUserID).child("date").setValue(saveCurrentDate)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull @NotNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                FriendRequestRef.child(senderUserID).child(receiverUserID)
                                                        .removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull @NotNull Task<Void> task) {
                                                                if(task.isSuccessful()){
                                                                    FriendRequestRef.child(receiverUserID).child(senderUserID)
                                                                            .removeValue()
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull @NotNull Task<Void> task) {
                                                                                    if(task.isSuccessful()){
                                                                                        SendFriendRequestButton.setEnabled(true);
                                                                                        current_state = "friends";
                                                                                        SendFriendRequestButton.setText("Unfriend");
                                                                                        SendFriendRequestButton.setBackgroundColor(Color.parseColor("#BD2E2E"));

                                                                                        DeclineFriendRequestButton.setVisibility(View.INVISIBLE);
                                                                                        DeclineFriendRequestButton.setEnabled(false);

                                                                                    }
                                                                                }
                                                                            });
                                                                }
                                                            }
                                                        });
                                            }
                                        }
                                    });
                        }
                    }
                });

    }

    private void CancelFriendRequest() {
        FriendRequestRef.child(senderUserID).child(receiverUserID)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                        if(task.isSuccessful()){
                            FriendRequestRef.child(receiverUserID).child(senderUserID)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull @NotNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                SendFriendRequestButton.setEnabled(true);
                                                current_state = "not_friends";
                                                SendFriendRequestButton.setText("Send Friend Request");
                                                SendFriendRequestButton.setBackgroundColor(Color.parseColor("#59BD2E"));

                                                DeclineFriendRequestButton.setVisibility(View.INVISIBLE);
                                                DeclineFriendRequestButton.setEnabled(false);

                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void MaintenanceOfButtons() {
        FriendRequestRef.child(senderUserID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild(receiverUserID)){
                            String request_type = dataSnapshot.child(receiverUserID).child("request_type").getValue().toString();

                            if(request_type.equals("sent")){
                                current_state = "request_sent";
                                SendFriendRequestButton.setText("Cancel Friend Request");
                                SendFriendRequestButton.setBackgroundColor(Color.parseColor("#BD2E2E"));

                                DeclineFriendRequestButton.setVisibility(View.INVISIBLE);
                                DeclineFriendRequestButton.setEnabled(false);
                            }
                            else if(request_type.equals("received")){
                                current_state = "request_received";
                                SendFriendRequestButton.setText("Accept Friend Request");

                                DeclineFriendRequestButton.setVisibility(View.VISIBLE);
                                DeclineFriendRequestButton.setEnabled(true);

                                DeclineFriendRequestButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        CancelFriendRequest();
                                    }
                                });
                            }
                        }
                        else{
                            FriendsRef.child(senderUserID)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull @NotNull DataSnapshot dataSnapshot) {
                                            if(dataSnapshot.hasChild(receiverUserID)){
                                                current_state = "friends";
                                                SendFriendRequestButton.setText("Unfriend");
                                                SendFriendRequestButton.setBackgroundColor(Color.parseColor("#BD2E2E"));

                                                DeclineFriendRequestButton.setVisibility(View.INVISIBLE);
                                                DeclineFriendRequestButton.setEnabled(false);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull @NotNull DatabaseError databaseError) {

                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError databaseError) {

                    }
                });
    }

    private void SendFriendRequestToAPerson() {
        FriendRequestRef.child(senderUserID).child(receiverUserID)
                .child("request_type").setValue("sent")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                        if(task.isSuccessful()){
                            FriendRequestRef.child(receiverUserID).child(senderUserID)
                                    .child("request_type").setValue("received")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull @NotNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                SendFriendRequestButton.setEnabled(true);
                                                current_state = "request_sent";
                                                SendFriendRequestButton.setText("Cancel Friend Request");
                                                SendFriendRequestButton.setBackgroundColor(Color.parseColor("#BD2E2E"));

                                                DeclineFriendRequestButton.setVisibility(View.INVISIBLE);
                                                DeclineFriendRequestButton.setEnabled(false);

                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void InitializeFields() {
        userName = (TextView) findViewById(R.id.persons_username);
        userProfName = (TextView) findViewById(R.id.person_full_name);
        userBio = (TextView) findViewById(R.id.persons_profile_bio);
        userProfileImage = (CircleImageView) findViewById(R.id.persons_profile_pic);
        SendFriendRequestButton = (Button) findViewById(R.id.send_friend_request_button);
        DeclineFriendRequestButton = (Button) findViewById(R.id.decline_friend_request_button);

        current_state = "not_friends";
    }
}