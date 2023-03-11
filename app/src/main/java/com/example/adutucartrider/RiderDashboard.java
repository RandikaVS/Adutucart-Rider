package com.example.adutucartrider;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.example.adutucartrider.adapters.PendingOrdersViewAdapter;
import com.example.adutucartrider.adapters.PickupOrdersViewAdapter;
import com.example.adutucartrider.models.PendingOrderList;
import com.example.adutucartrider.models.PickupOrderList;
import com.example.adutucartrider.viewModel.FirebaseViewModel;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class RiderDashboard extends AppCompatActivity {

    private RecyclerView PickupOrdersRv,PendingOrdersRv;
    private DatabaseReference databaseReference;
    private FirebaseViewModel firebaseViewModel;
    private TextView PendingOrderCount,RiderName,RiderEmail;

    private SharedPreferences sharedPreferences;

    String riderName,riderMobile;

    PendingOrdersViewAdapter PendingOrdersViewAdapter;
    PickupOrdersViewAdapter pickupOrdersViewAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_dashboard);

        PickupOrdersRv = findViewById(R.id.pickup_orders_rv);
        PendingOrdersRv = findViewById(R.id.orders_rv);
        PendingOrderCount = findViewById(R.id.pending_order_count);
        RiderName = findViewById(R.id.rider_name);
        RiderEmail = findViewById(R.id.rider_email);

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

        Query query = databaseReference.child("cIC5l2GwhQhGJUzMlZEPRepaXD23");

        FirebaseRecyclerOptions<PickupOrderList> options
                = new FirebaseRecyclerOptions.Builder<PickupOrderList>()
                .setQuery(query, PickupOrderList.class)
                .build();

        if(options==null) {
            System.out.println("OptionsisNullllllll################################################3");
        }
        else {
            System.out.println("Options##########################" + options);
        }

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
                PendingOrdersViewAdapter.setOrderList(pendingOrderListList,riderName,riderMobile);
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
}