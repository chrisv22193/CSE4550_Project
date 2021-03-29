package com.example.socialcalendar;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class FrontActivity extends AppCompatActivity {

    private Button CreateNewAccountButton;
    private TextView LoginLink;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_front);

        mAuth = FirebaseAuth.getInstance();

        LoginLink = (TextView) findViewById(R.id.log_in_link);
        CreateNewAccountButton = (Button) findViewById(R.id.create_new_account);

        CreateNewAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FrontActivity.this, Register_Activity.class));
            }
        });

        LoginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FrontActivity.this, LoginActivity.class));
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null)
        {
            SendUserToMainActivity();
        }
    }

    private void SendUserToMainActivity() {
        Intent frontIntent = new Intent(FrontActivity.this, MainActivity.class);
        frontIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(frontIntent);
        finish();
    }
}