package com.example.adutucartrider;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.adutucartrider.db.RiderDB;
import com.example.adutucartrider.models.Rider;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

import javax.xml.validation.Validator;

public class RiderRegister extends AppCompatActivity {

    private ImageView Back;
    private EditText Email,Name,Password,Phone;
    private AppCompatButton Login;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_register);

        Back = findViewById(R.id.back_arrow_register);
        Email = findViewById(R.id.rider_register_email);
        Name = findViewById(R.id.rider_register_name);
        Password = findViewById(R.id.rider_register_password);
        Phone = findViewById(R.id.rider_register_phone);
        Login = findViewById(R.id.rider_register);
        progressBar = findViewById(R.id.register_progressbar);


        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                RiderDB riderDB = new RiderDB();

                String name = Name.getText().toString();
                String email = Email.getText().toString();
                String phone = Phone.getText().toString();
                String password = Password.getText().toString();
                boolean isSuccess = true;

                if(name.isEmpty()){
                    Name.setError("Field required");
                    Name.requestFocus();
                    isSuccess = false;
                }
                if(email.isEmpty()){
                    Email.setError("Field required");
                    Email.requestFocus();
                    isSuccess = false;
                }
                if(phone.isEmpty()){
                    Phone.setError("Field required");
                    Phone.requestFocus();
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
                    Rider rider = new Rider(name,email,phone,password,"0");

                    riderDB.riderAuth(rider).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                Toast.makeText(RiderRegister.this, "User with this email already exist.", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                riderDB.addRider(rider).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if (task.isSuccessful()) {
                                            Toast.makeText(RiderRegister.this, "Rider Registered Successfully", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(RiderRegister.this, RiderLogin.class));
                                        } else {
                                            Toast.makeText(RiderRegister.this, "Fail to register rider", Toast.LENGTH_SHORT).show();

                                        }
                                        progressBar.setVisibility(View.GONE);
                                    }
                                });
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(RiderRegister.this, "Failed to register", Toast.LENGTH_SHORT).show();
                        }
                    });
                }else{
                    progressBar.setVisibility(View.GONE);
                }


            }
        });
    }
}