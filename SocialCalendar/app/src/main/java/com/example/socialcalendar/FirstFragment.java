package com.example.socialcalendar;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FirstFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FirstFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FirstFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FirstFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FirstFragment newInstance(String param1, String param2) {
        FirstFragment fragment = new FirstFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    private RecyclerView postList;
    private FirebaseAuth mAuth;
    private DatabaseReference UsersRef, PostRef;
    private ImageView addNewPostButton;

    String currentUserID;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_first, container, false);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        PostRef = FirebaseDatabase.getInstance().getReference().child("Post");

//        addNewPostButton = (ImageView) v.findViewById(R.id.add_new_post_button);

        postList = (RecyclerView) v.findViewById(R.id.all_users_post_list);
        postList.setLayoutManager(new LinearLayoutManager(getContext()));

//        addNewPostButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                SendUserToPostActivity();
//            }
//        });

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Posts> options = new FirebaseRecyclerOptions.Builder<Posts>()
                .setQuery(PostRef, Posts.class)
                .build();

        FirebaseRecyclerAdapter<Posts, PostsViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Posts, PostsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull PostsViewHolder holder, int position, @NonNull Posts model) {
                holder.username.setText(model.getUsername());
                holder.date.setText(model.getDate());
                holder.time.setText(model.getTime());
                holder.description.setText(model.getDescription());
                Picasso.get().load(model.getProfileimage()).into(holder.profileimage);
                Picasso.get().load(model.getPostimage()).placeholder(R.drawable.background).into(holder.postimage);
            }

            @NonNull
            @Override
            public PostsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.all_post_layout,  viewGroup, false);
                PostsViewHolder viewHolder = new PostsViewHolder(view);
                return viewHolder;
            }
        };
        firebaseRecyclerAdapter.startListening();
        postList.setAdapter(firebaseRecyclerAdapter);
    }


    public static class PostsViewHolder extends RecyclerView.ViewHolder{
        TextView username, date, time, description;
        CircleImageView profileimage;
        ImageView postimage;

        public PostsViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.post_user_name);
            date = itemView.findViewById(R.id.post_date);
            time = itemView.findViewById(R.id.post_time);
            description = itemView.findViewById(R.id.post_description);
            profileimage = itemView.findViewById(R.id.post_profile_image);
            postimage = itemView.findViewById(R.id.post_image);

        }
    }

    private void SendUserToPostActivity() {
        Intent adNewPostIntent = new Intent(getActivity(), PostActivity.class);
        startActivity(adNewPostIntent);
    }
}