package com.example.adutucartrider;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.adutucartrider.adapters.PendingOrdersViewAdapter;
import com.example.adutucartrider.adapters.PickupOrdersViewAdapter;
import com.example.adutucartrider.models.PendingOrderList;
import com.example.adutucartrider.models.PickupOrderList;
import com.example.adutucartrider.viewModel.FirebaseViewModel;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class RiderDashboard extends AppCompatActivity{

    private RecyclerView PickupOrdersRv,PendingOrdersRv;
    private DatabaseReference databaseReference;
    private FirebaseViewModel firebaseViewModel;
    private TextView PendingOrderCount,RiderName,RiderEmail;

    private SharedPreferences sharedPreferences;
    private ImageView Bell,Map;

    private AppCompatButton LogOut;
    String riderName,riderMobile;

    String token,pickupKey;
    private Uri imageUri;
    private ProgressBar PickupProgress;

    PendingOrdersViewAdapter PendingOrdersViewAdapter;
    PickupOrdersViewAdapter pickupOrdersViewAdapter;
    FirebaseStorage storage = FirebaseStorage.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_dashboard);

        PickupOrdersRv = findViewById(R.id.pickup_orders_rv);
        PendingOrdersRv = findViewById(R.id.orders_rv);
        PendingOrderCount = findViewById(R.id.pending_order_count);
        RiderName = findViewById(R.id.rider_name);
        RiderEmail = findViewById(R.id.rider_email);
        Bell = findViewById(R.id.bell_icon);
        Map = findViewById(R.id.map_icon);
        LogOut = findViewById(R.id.logout_btn);
        PickupProgress = findViewById(R.id.pickup_progress);

        LogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(RiderDashboard.this);

                // Set the message show for the Alert time
                builder.setMessage("Do you want to logout ?");

                // Set Alert Title
                builder.setTitle("Alert !");

                // Set Cancelable false for when the user clicks on the outside the Dialog Box then it will remain show
                builder.setCancelable(false);

                // Set the positive button with yes name Lambda OnClickListener method is use of DialogInterface interface.
                builder.setPositiveButton("Yes", (DialogInterface.OnClickListener) (dialog, which) -> {
                    SharedPreferences settings = getSharedPreferences("shared_prefs", Context.MODE_PRIVATE);
                    settings.edit().clear().commit();
                    Intent intent = new Intent(RiderDashboard.this, RiderLogin.class);
                    startActivity(intent);
                    finish();

                });

                // Set the Negative button with No name Lambda OnClickListener method is use of DialogInterface interface.
                builder.setNegativeButton("No", (DialogInterface.OnClickListener) (dialog, which) -> {
                    // If user click no then dialog box is canceled.
                    dialog.cancel();
                });

                // Create the Alert dialog
                AlertDialog alertDialog = builder.create();
                // Show the Alert Dialog box
                alertDialog.show();
            }
        });

        FirebaseMessaging.getInstance().subscribeToTopic("adminNotes")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "Subscribed";
                        if (!task.isSuccessful()) {
                            msg = "Subscribe failed";
                        }
                        Log.d(TAG, msg);
                        //Toast.makeText(RiderDashboard.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });

        databaseReference = FirebaseDatabase.getInstance().getReference("Rider");

        databaseReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    riderName = snapshot.child("name").getValue(String.class);
                    riderMobile = snapshot.child("phone").getValue(String.class);
                    RiderName.setText(riderName);
                    RiderEmail.setText(snapshot.child("email").getValue(String.class));


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        PickupOrdersRv.setLayoutManager(new WrapContentLinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));


        databaseReference = FirebaseDatabase.getInstance().getReference("RiderPickups");

        Query query = databaseReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).orderByChild("status").equalTo("ToShip");

        FirebaseRecyclerOptions<PickupOrderList> options
                = new FirebaseRecyclerOptions.Builder<PickupOrderList>()
                .setQuery(query, PickupOrderList.class)
                .build();



        pickupOrdersViewAdapter = new PickupOrdersViewAdapter(options,this);
        PickupOrdersRv.setAdapter(pickupOrdersViewAdapter);



        PendingOrdersRv.setHasFixedSize(true);
        PendingOrdersRv.setLayoutManager(new WrapContentLinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        PendingOrdersViewAdapter = new PendingOrdersViewAdapter();
        PendingOrdersRv.setAdapter(PendingOrdersViewAdapter);

        firebaseViewModel = new ViewModelProvider(this).get(FirebaseViewModel.class);


        firebaseViewModel.getAllDataRider();
        firebaseViewModel.getOrderMutableLiveDataRider().observe(RiderDashboard.this, new Observer<List<PendingOrderList>>() {
            @Override
            public void onChanged(List<PendingOrderList> pendingOrderListList) {
                PendingOrderCount.setText("("+pendingOrderListList.size()+")");
                PendingOrdersViewAdapter.setOrderList(pendingOrderListList,riderName,riderMobile,RiderDashboard.this);
                PendingOrdersViewAdapter.notifyDataSetChanged();
            }
        });

        firebaseViewModel.getDatabaseErrorMutableLiveData().observe(this, new Observer<DatabaseError>() {
            @Override
            public void onChanged(DatabaseError error) {
                Toast.makeText(RiderDashboard.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });



    }

    @Override
    protected void onStart() {
        super.onStart();
        pickupOrdersViewAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        pickupOrdersViewAdapter.stopListening();
    }

    public void addEvidence(int position,String key){

        PickupProgress.setVisibility(View.VISIBLE);
            this.pickupKey = key;
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, 100);

    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && data != null && data.getData() != null) {

            imageUri = data.getData();
            //AddImage.setImageURI(imageUri);
            System.out.println(imageUri);
            saveImage();
        }
        else{
            PickupProgress.setVisibility(View.GONE);
        }
    }

    public void saveImage(){
        StorageReference storageRef = storage.getReference().child("ImageFolder" );

        StorageReference ImageName = storageRef.child("evidence/"+ UUID.randomUUID().toString());

        ImageName.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(RiderDashboard.this, "Image uploaded", Toast.LENGTH_SHORT).show();

                ImageName.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String url = String.valueOf(uri);
                        databaseReference = FirebaseDatabase.getInstance().getReference("RiderPickups").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("evidence",url);
                        databaseReference.child(pickupKey).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                PickupProgress.setVisibility(View.GONE);
                                Toast.makeText(RiderDashboard.this, "Image added to evidence", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        PickupProgress.setVisibility(View.GONE);
                        Toast.makeText(RiderDashboard.this, "Failed to get image url", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                PickupProgress.setVisibility(View.GONE);
                Toast.makeText(RiderDashboard.this, "Failed to add image to database", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void setBellNotification(){
        Bell.setImageResource(R.drawable.notify);
    }



}