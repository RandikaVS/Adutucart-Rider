package com.example.adutucartrider;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.adutucartrider.db.RiderDB;
import com.example.adutucartrider.models.Rider;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

import java.util.regex.Pattern;

public class RiderLogin extends AppCompatActivity {

    private ProgressBar progressBar;
    private EditText Email,Password;
    private AppCompatButton Login;
    private TextView ToRegister;

    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_login2);

        progressBar = findViewById(R.id.login_progressbar);
        Email = findViewById(R.id.rider_email);
        Password = findViewById(R.id.rider_password);
        Login = findViewById(R.id.rider_login_btn);
        ToRegister = findViewById(R.id.rider_register_btn);


        ToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RiderLogin.this,RiderRegister.class));
            }
        });

        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                RiderDB riderDB = new RiderDB();

                String email = Email.getText().toString();
                String password = Password.getText().toString();
                boolean isSuccess = true;

                if(email.isEmpty()){
                    Email.setError("Field required");
                    Email.requestFocus();
                    isSuccess = false;
                }
                if(password.isEmpty()){
                    Password.setError("Field required");
                    Password.requestFocus();
                    isSuccess = false;
                }
                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    Email.setError("Please enter valid email!!!");
                    Email.requestFocus();
                    isSuccess = false;
                }

                if(isSuccess){
                    Rider rider = new Rider();
                    rider.setEmail(email);
                    rider.setPassword(password);

                    riderDB.riderSignIn(rider.getEmail(),rider.getPassword()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){

                                sharedpreferences = getSharedPreferences("shared_prefs", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedpreferences.edit();

                                editor.putString("emailKey", email);
                                editor.putString("passwordKey", password);

                                editor.apply();
                                Toast.makeText(RiderLogin.this, "Logged in successfully", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(RiderLogin.this, RiderDashboard.class);
                                startActivity(intent);
                                finish();
                            }
                            else{
                                Toast.makeText(RiderLogin.this, "Fail to login", Toast.LENGTH_SHORT).show();

                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(RiderLogin.this, "Failed to login", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else{
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }
}