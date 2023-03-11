package com.example.adutucartrider.db;

import com.example.adutucartrider.models.Rider;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RiderDB {

    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;

    public  RiderDB(){
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        databaseReference = db.getReference(Rider.class.getSimpleName());
        mAuth=FirebaseAuth.getInstance();
    }

    public Task<Void> addRider(Rider rider){

        return databaseReference.child(mAuth.getCurrentUser().getUid()).setValue(rider);

    }

    public Task<AuthResult> riderAuth(Rider adm){

        return mAuth.createUserWithEmailAndPassword(adm.getEmail(), adm.getPassword());

    }

    public Task<AuthResult> riderSignIn(String email, String password){
        return mAuth.signInWithEmailAndPassword(email,password);
    }

    public  Task<Void> riderUpdate(String key, HashMap<String,Object> hashMap){
        return databaseReference.child(key).updateChildren(hashMap);
    }

    public Task<Void> riderDelete(String key){
        return databaseReference.child(key).removeValue();
    }
}
