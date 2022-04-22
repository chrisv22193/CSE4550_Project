package com.example.socialcalendar;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SecondFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SecondFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SecondFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SecondFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SecondFragment newInstance(String param1, String param2) {
        SecondFragment fragment = new SecondFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    private TextView userName, userProfName, userBio;
    private Button editProfileButton;
    private RecyclerView postList;
    private ImageView userProfileBackgroundImage;
    private CircleImageView userProfileImage;

    private DatabaseReference profileUserRef, CalendarPostRef;
    private FirebaseAuth mAuth;

    private String currentUserID;

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
        View v = inflater.inflate(R.layout.fragment_second, container, false);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        profileUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);
        CalendarPostRef = FirebaseDatabase.getInstance().getReference().child("CalendarPost");

        userName = (TextView) v.findViewById(R.id.my_username);
        userProfName = (TextView) v.findViewById(R.id.my_profile_full_name);
        userBio = (TextView) v.findViewById(R.id.my_profile_bio);
        userProfileImage = (CircleImageView) v.findViewById(R.id.my_profile_pic);
//        userProfileBackgroundImage = (ImageView) v.findViewById(R.id.my_profile_background);
        editProfileButton = (Button) v.findViewById(R.id.edit_profile_button);

        postList = (RecyclerView) v.findViewById(R.id.all_users_post_list);
        postList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        postList.setLayoutManager(linearLayoutManager);

        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ProfileSettingsActivity.class));
            }
        });

        profileUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String myProfileImage = dataSnapshot.child("profileimage").getValue().toString();
//                    String myProfileBackgroundImage = dataSnapshot.child("profilebackgroundimage").getValue().toString();
                    String myUserName = dataSnapshot.child("username").getValue().toString();
                    String myProfileName = dataSnapshot.child("fullname").getValue().toString();
                    String myProfileStatus = dataSnapshot.child("status").getValue().toString();

                    Picasso.get().load(myProfileImage).placeholder(R.drawable.profile).into(userProfileImage);
//                    Picasso.get().load(myProfileBackgroundImage).placeholder(R.drawable.background).into(userProfileBackgroundImage);
                    userName.setText("@" + myUserName);
                    userProfName.setText(myProfileName);
                    userBio.setText(myProfileStatus);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        DisplayAllUsersPost();

        return v;
    }

    private void DisplayAllUsersPost() {
        Query myPostList = CalendarPostRef.orderByChild("uid")
                .startAt(currentUserID).endAt(currentUserID + "\uf8ff");

        FirebaseRecyclerAdapter<Events, FirstFragment.PostViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Events, FirstFragment.PostViewHolder>
                        (
                                Events.class,
                                R.layout.all_calendar_post_layout,
                                FirstFragment.PostViewHolder.class,
                                myPostList
                        ) {
                    @Override
                    protected void populateViewHolder(FirstFragment.PostViewHolder postViewHolder, Events posts, int i) {
                        final String usersIDs = getRef(i).getKey();
                        CalendarPostRef.child(usersIDs).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull @NotNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()){
                                    final String date = dataSnapshot.child("date").getValue().toString();
                                    final String event = dataSnapshot.child("event").getValue().toString();
                                    final String profileimage = dataSnapshot.child("profileimage").getValue().toString();
                                    final String time = dataSnapshot.child("time").getValue().toString();
                                    final String username = dataSnapshot.child("username").getValue().toString();

                                    postViewHolder.setDate(date);
                                    postViewHolder.setEvent(event);
                                    postViewHolder.setProfileimage(profileimage);
                                    postViewHolder.setTime(time);
                                    postViewHolder.setUsername(username);

                                    postViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            CharSequence options[] = new CharSequence[]{
                                                    "Edit event",
                                                    "Delete event"
                                            };
                                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                            builder.setTitle("Select option");

                                            builder.setItems(options, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    if(which == 0){
                                                        // this is for the ability to edit the post
                                                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                                        builder.setTitle("Edit event:");

                                                        final EditText inputField = new EditText(getActivity());
                                                        inputField.setText(event);
                                                        builder.setView(inputField);

                                                        builder.setPositiveButton(Html.fromHtml("<font color='#004F93'>Update</font>"), new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                CalendarPostRef.child(usersIDs).child("event").setValue(inputField.getText().toString());
                                                                Toast.makeText(getActivity(), "Event updated successfully", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });

                                                        builder.setNegativeButton(Html.fromHtml("<font color='#004F93'>Cancel</font>"), new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                dialog.cancel();
                                                            }
                                                        });

                                                        Dialog dialog1 = builder.create();
                                                        dialog1.show();
                                                    }
                                                    if(which == 1){
                                                        // this is for the deletion of the post
                                                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                                        builder.setTitle("Are you sure you want to delete this event?");

                                                        builder.setPositiveButton(Html.fromHtml("<font color='#004F93'>Delete</font>"), new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                CalendarPostRef.child(usersIDs).removeValue();
                                                                Toast.makeText(getActivity(), "Event deleted", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });

                                                        builder.setNegativeButton(Html.fromHtml("<font color='#004F93'>Cancel</font>"), new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                dialog.cancel();
                                                            }
                                                        });
                                                        Dialog dialog2 = builder.create();
                                                        dialog2.show();
                                                    }
                                                }
                                            });
                                            builder.show();
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull @NotNull DatabaseError databaseError) {

                            }
                        });
                    }
                };
        firebaseRecyclerAdapter.startListening();
        postList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder{
        View mView;
        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setUsername(String username){
            TextView Username = (TextView) mView.findViewById(R.id.calendar_post_user_name);
            Username.setText(username);
        }

        public void setProfileimage(String profileimage){
            ImageView image = (CircleImageView) mView.findViewById(R.id.calendar_post_profile_image);
            Picasso.get().load(profileimage).placeholder(R.drawable.profile).into(image);
        }

        public void setTime(String time){
            TextView PostTime = (TextView) mView.findViewById(R.id.calendar_post_time);
            PostTime.setText(time);
        }

        public void setDate(String date){
            TextView PostDate = (TextView) mView.findViewById(R.id.calendar_post_date);
            PostDate.setText(date);
        }

        public void setEvent(String event){
            TextView PostDescription = (TextView) mView.findViewById(R.id.calendar_post_description);
            PostDescription.setText(event);
        }
    }

    private void SendUserToPostActivity() {
        Intent adNewPostIntent = new Intent(getActivity(), PostActivity.class);
        startActivity(adNewPostIntent);
    }
}