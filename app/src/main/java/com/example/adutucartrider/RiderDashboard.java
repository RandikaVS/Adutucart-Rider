package com.example.adutucartrider;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import static com.example.adutucartrider.models.Constants.TITLE;
import static com.example.adutucartrider.models.Constants.TOPIC;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.adutucartrider.adapters.PendingOrdersViewAdapter;
import com.example.adutucartrider.adapters.PickupOrdersViewAdapter;
import com.example.adutucartrider.api.ApiUtilities;
import com.example.adutucartrider.models.NotificationData;
import com.example.adutucartrider.models.PendingOrderList;
import com.example.adutucartrider.models.PickupOrderList;
import com.example.adutucartrider.services.pushNotification;
import com.example.adutucartrider.services.sendNotification;
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

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RiderDashboard extends AppCompatActivity{

    private RecyclerView PickupOrdersRv,PendingOrdersRv;
    private DatabaseReference databaseReference;
    private FirebaseViewModel firebaseViewModel;
    private TextView PendingOrderCount,RiderName,RiderEmail,PickupOrderId,WaitingTime,
            orderId,address,paymentType,status,total,NoPickups;

    private SharedPreferences sharedPreferences;
    private ImageView Bell,Map,AddEvidence;

    private AppCompatButton LogOut;
    String riderName,riderMobile;

    String token,pickupKey;
    private Uri imageUri;
    private ProgressBar PickupProgress;

    private SwipeRefreshLayout swipeRefreshLayout;

    private Calendar calendar,calendar1;

    private List<PickupOrderList> currentPickup = null;

    private PickupOrderList currentSinglePickup = null;


    private Button Dismiss;
    private LinearLayout PickupCard;

    String orderKey,userId;


    PendingOrdersViewAdapter PendingOrdersViewAdapter;

    FirebaseStorage storage = FirebaseStorage.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_dashboard);

        PendingOrdersRv = findViewById(R.id.orders_rv);
        PendingOrderCount = findViewById(R.id.pending_order_count);
        RiderName = findViewById(R.id.rider_name);
        RiderEmail = findViewById(R.id.rider_email);
        Bell = findViewById(R.id.bell_icon);
        Map = findViewById(R.id.map_icon);
        LogOut = findViewById(R.id.logout_btn);
        PickupProgress = findViewById(R.id.pickup_progress);
        swipeRefreshLayout = findViewById(R.id.refreshLayout);
        PickupOrderId = findViewById(R.id.pickup_order_id);
        WaitingTime = findViewById(R.id.pickup_waiting_time);
        AddEvidence = findViewById(R.id.pickup_add_evidence);
        PickupCard = findViewById(R.id.pickup_card);
        NoPickups = findViewById(R.id.no_pickups);

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




        setPickupOrders();
        setPendingOrders();


        if(currentPickup==null){
            PickupCard.setVisibility(View.GONE);
            NoPickups.setVisibility(View.VISIBLE);
        }
        else{
            PickupCard.setVisibility(View.VISIBLE);
            NoPickups.setVisibility(View.GONE);
        }

        Bell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!currentPickup.isEmpty()){

                    pushNotification pushNotification = new pushNotification(new NotificationData(TITLE,"From : "+riderName+"\nOrder Id : "+currentSinglePickup.getOrderId()),TOPIC);
                    sendAlert(pushNotification);
                }
                else{
                    Toast.makeText(RiderDashboard.this, "No Pickups", Toast.LENGTH_SHORT).show();
                }
            }
        });

        PickupOrderId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LayoutInflater inflater = (LayoutInflater)
                        v.getContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                View popupView = inflater.inflate(R.layout.pickup_order_details, null);

                orderId = popupView.findViewById(R.id.details_order_id);
                address = popupView.findViewById(R.id.detaild_order_address);
                paymentType = popupView.findViewById(R.id.details_payment_type);
                status = popupView.findViewById(R.id.details_status);
                total = popupView.findViewById(R.id.details_total);
                Dismiss = popupView.findViewById(R.id.details_dismiss_btn);

                if(currentSinglePickup!=null) {

                    orderId.setText(currentSinglePickup.getOrderId());
                    address.setText(currentSinglePickup.getAddress());
                    paymentType.setText(currentSinglePickup.getPaymentType());
                    status.setText(currentSinglePickup.getStatus());
                    total.setText(currentSinglePickup.getTotal());
                }

                int width = 600;
                int height = LinearLayout.LayoutParams.WRAP_CONTENT;
                boolean focusable = true; // lets taps outside the popup also dismiss it

                final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

                // show the popup window
                // which view you pass in doesn't matter, it is only used for the window tolken
                popupWindow.showAtLocation(v.getRootView(), Gravity.CENTER, 0, 0);

                popupWindow.setElevation(20);

                popupView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        popupWindow.dismiss();
                        return true;
                    }
                });
                Dismiss.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupWindow.dismiss();
                    }
                });
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onRefresh() {
                setPickupOrders();
                PendingOrdersViewAdapter.notifyDataSetChanged();
                setPendingOrders();
                PendingOrdersViewAdapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);


            }
        });

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
                    }
                });

        //FirebaseMessaging.getInstance().unsubscribeFromTopic("pickup");


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void sendAlert(pushNotification pushNotification) {

        ApiUtilities.getClient().sendNotification(pushNotification).enqueue(new Callback<com.example.adutucartrider.services.pushNotification>() {
            @Override
            public void onResponse(Call<com.example.adutucartrider.services.pushNotification> call, Response<com.example.adutucartrider.services.pushNotification> response) {

                if(response.isSuccessful()){
                    Toast.makeText(RiderDashboard.this, "Pickup complete request send", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(RiderDashboard.this, "Fail to to send request", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<com.example.adutucartrider.services.pushNotification> call, Throwable t) {
                Toast.makeText(RiderDashboard.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void addEvidence(){

        PickupProgress.setVisibility(View.VISIBLE);
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
                        databaseReference = FirebaseDatabase.getInstance().getReference("Orders").child(userId).child(orderKey);

                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("evidence",url);
                        databaseReference.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
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


    private void setPickupOrders(){

        firebaseViewModel = new ViewModelProvider(this).get(FirebaseViewModel.class);


        firebaseViewModel.getAllData();

        firebaseViewModel.getOrderMutableLiveDataPickup().observe(RiderDashboard.this, new Observer<List<PickupOrderList>>() {
            @Override
            public void onChanged(List<PickupOrderList> currentPickup2) {

                if(currentPickup2!=null) {
                    //System.out.println("Rider Pickup sop "+currentPickup2.get(0).getRiderId());
                    currentPickup = currentPickup2;

                    for (int i = 0; i < currentPickup.size(); i++) {

                        if (currentPickup.get(i).getRiderId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()) && !currentPickup.get(i).getStatus().equals("Completed")) {

                            PickupCard.setVisibility(View.VISIBLE);
                            NoPickups.setVisibility(View.GONE);
                            currentSinglePickup = currentPickup.get(i);
                            PickupOrderId.setText(currentPickup.get(i).getOrderId());

                            orderKey = currentPickup.get(i).getOrderId();
                            userId = currentPickup.get(i).getUserId();

                            if (!currentPickup.get(i).getEvidence().equals("null")) {
                                Glide.with(RiderDashboard.this)

                                        .load(currentPickup.get(i).getEvidence())

                                        .placeholder(R.drawable.add_image)

                                        .into(AddEvidence);
                            } else {
                                AddEvidence.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        addEvidence();
                                    }
                                });

                            }

                            if (!currentPickup.get(i).getWaitingTime().equals(" ")) {
                                WaitingTime.setText(currentPickup.get(i).getWaitingTime());
                                WaitingTime.setEnabled(false);
                            } else {

                                String finalOrderKey = orderKey;
                                String finalUserId = userId;
                                WaitingTime.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        setWaitingTime(finalOrderKey, finalUserId);
                                    }
                                });

                            }
                        }
                        else{
                            PickupCard.setVisibility(View.GONE);
                            NoPickups.setVisibility(View.VISIBLE);
                            Toast.makeText(RiderDashboard.this, "Current pickup is empty", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                else{
                    Toast.makeText(RiderDashboard.this, "Current pickup is empty", Toast.LENGTH_SHORT).show();
                }

            }
        });

        firebaseViewModel.getDatabaseErrorMutableLiveData().observe(this, new Observer<DatabaseError>() {
            @Override
            public void onChanged(DatabaseError error) {
                Toast.makeText(RiderDashboard.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void setWaitingTime(String orderKey,String userId) {


        calendar = Calendar.getInstance();
        int hours = calendar.get(Calendar.HOUR_OF_DAY);
        int mins = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                calendar1 = Calendar.getInstance();
                calendar1.set(Calendar.HOUR_OF_DAY,hourOfDay);
                calendar1.set(Calendar.MINUTE,minute);
                calendar1.setTimeZone(TimeZone.getDefault());

                if(calendar1.getTimeInMillis() > calendar.getTimeInMillis()){


                    long millis = calendar1.getTimeInMillis()-calendar.getTimeInMillis();
//                            long h = TimeUnit.MILLISECONDS.toHours(millis);
//
//                            long min = TimeUnit.MILLISECONDS.toMinutes(millis);

//                            System.out.println("min : "+min);
//                            System.out.println("hour : "+h);


                    // Calculating the difference in Hours
                    long differenceInHours
                            = (millis / (60 * 60 * 1000))
                            % 24;

                    // Calculating the difference in Minutes
                    long differenceInMinutes
                            = (millis / (60 * 1000)) % 60;

                    String waitingTime;

                    if(differenceInHours<=0){
                        WaitingTime.setText(differenceInMinutes+" min");
                        waitingTime = differenceInMinutes+" min";
                    }
                    else{
                        WaitingTime.setText(differenceInHours+" hour "+differenceInMinutes+" min");
                        waitingTime = differenceInHours+" hour "+differenceInMinutes+" min";
                    }

                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("waitingTime",waitingTime);
                    hashMap.put("status","ToReceive");
                    databaseReference = FirebaseDatabase.getInstance().getReference("Orders").child(userId).child(orderKey);

                    databaseReference.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                Toast.makeText(RiderDashboard.this, "Waiting time added", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(RiderDashboard.this, "Fail to add time to database", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
                else{
                    Toast.makeText(RiderDashboard.this, "Selected time is less than current time", Toast.LENGTH_SHORT).show();
                }

            }
        },hours,mins,false);
        timePickerDialog.show();

    }


    private void setPendingOrders(){

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



        PendingOrdersRv.setHasFixedSize(true);
        PendingOrdersRv.setLayoutManager(new WrapContentLinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        PendingOrdersViewAdapter = new PendingOrdersViewAdapter();
        PendingOrdersRv.setAdapter(PendingOrdersViewAdapter);

        firebaseViewModel = new ViewModelProvider(this).get(FirebaseViewModel.class);


        firebaseViewModel.getAllDataRider();
        firebaseViewModel.getOrderMutableLiveDataRider().observe(RiderDashboard.this, new Observer<List<PendingOrderList>>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChanged(List<PendingOrderList> pendingOrderListList) {
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

    public void setPendingOrderCount(String count){
        PendingOrderCount.setText("("+count+")");
    }


}