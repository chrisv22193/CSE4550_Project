package com.example.socialcalendar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class FindFriendsActivity extends AppCompatActivity {
    private ImageButton SearchButton;
    private EditText SearchInputText;

    private DatabaseReference UsersRef;

    private RecyclerView SearchResultList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends);

        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        SearchResultList = (RecyclerView) findViewById(R.id.search_result_list);
        SearchResultList.setHasFixedSize(true);
        SearchResultList.setLayoutManager(new LinearLayoutManager(this));

        SearchButton = (ImageButton) findViewById(R.id.search_friends_button);
        SearchInputText = (EditText) findViewById(R.id.search_box_input);

        SearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchBoxInput = SearchInputText.getText().toString();

                SearchForFriends(searchBoxInput);
            }
        });
    }

    private void SearchForFriends(String searchBoxInput) {
        Query searchFriendsQuery = UsersRef.orderByChild("fullname")
                .startAt(searchBoxInput).endAt(searchBoxInput + "\uf8ff");

        FirebaseRecyclerAdapter<FindFriends, FindFriendsActivity.FindFriendsViewHolder> firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<FindFriends, FindFriendsActivity.FindFriendsViewHolder>
                (
                        FindFriends.class,
                        R.layout.all_users_display_layout,
                        FindFriendsActivity.FindFriendsViewHolder.class,
                        searchFriendsQuery
                ) {
            @Override
            protected void populateViewHolder(FindFriendsActivity.FindFriendsViewHolder findFriendsViewHolder, FindFriends findFriends, int i) {
                findFriendsViewHolder.setFullname(findFriends.getFullname());
                findFriendsViewHolder.setProfileimage(findFriends.getProfileimage());
                findFriendsViewHolder.setStatus(findFriends.getStatus());

                findFriendsViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String visit_user_id = getRef(i).getKey();

                        Intent profileIntent = new Intent(FindFriendsActivity.this, PersonsProfileActivity.class);
                        profileIntent.putExtra("visit_user_id", visit_user_id);
                        startActivity(profileIntent);
                    }
                });
            }
        };
        SearchResultList.setAdapter(firebaseRecyclerAdapter);

    }

    public static class FindFriendsViewHolder extends RecyclerView.ViewHolder{
        View mView;

        public FindFriendsViewHolder(@NonNull View itemView) {
            super(itemView);

            mView = itemView;
        }

        public void setProfileimage(String profileimage){
            CircleImageView myImage = (CircleImageView) mView.findViewById(R.id.all_user_profile_image);
            Picasso.get().load(profileimage).placeholder(R.drawable.profile).into(myImage);
        }

        public void setFullname(String fullname){
            TextView myName = (TextView) mView.findViewById(R.id.all_users_profile_name);
            myName.setText(fullname);
        }

        public void setStatus(String status){
            TextView myStatus = (TextView) mView.findViewById(R.id.all_users_profile_bio);
            myStatus.setText(status);
        }
    }
}