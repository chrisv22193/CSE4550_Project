package com.example.socialcalendar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordInSettingsActivity extends AppCompatActivity {
    private Button ResetPasswordButton;
    private TextView BackToLogin;
    private EditText ResetEmailInput;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password_in_settings);

        mAuth = FirebaseAuth.getInstance();

        ResetPasswordButton = (Button) findViewById(R.id.reset_password_button);
        ResetEmailInput = (EditText) findViewById(R.id.reset_password_email);
        BackToLogin = (TextView) findViewById(R.id.back_to_login);


        ResetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userEmail = ResetEmailInput.getText().toString();

                if(TextUtils.isEmpty(userEmail)){
                    Toast.makeText(ResetPasswordInSettingsActivity.this, "Please provide your email", Toast.LENGTH_SHORT).show();
                }
                else{
                    mAuth.sendPasswordResetEmail(userEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                mAuth.signOut();
                                startActivity(new Intent(ResetPasswordInSettingsActivity.this, LoginActivity.class));
                                Toast.makeText(ResetPasswordInSettingsActivity.this, "Please check your email for link to reset password", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                String message = task.getException().getMessage();
                                Toast.makeText(ResetPasswordInSettingsActivity.this, "Error Occurred" + message, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
}