package com.example.adutucartrider.db;

import androidx.annotation.NonNull;

import com.example.adutucartrider.models.PendingOrderList;
import com.example.adutucartrider.models.PendingOrders;
import com.example.adutucartrider.models.PickupOrderList;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FirebaseRepo {

    private DatabaseReference databaseReference;
    private OnRealTimeDbTaskComplete onRealTimeDbTaskComplete;

    public FirebaseRepo(OnRealTimeDbTaskComplete onRealTimeDbTaskComplete){
        this.onRealTimeDbTaskComplete = onRealTimeDbTaskComplete;
        databaseReference = FirebaseDatabase.getInstance().getReference("Orders");
    }

    public void getAllOrderData(){
        databaseReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                List<PickupOrderList> pickupOrderLists = new ArrayList<>();
//
//                for(DataSnapshot ds :snapshot.getChildren()){
//                    PickupOrderList pickupOrderList = new PickupOrderList();
//                    pickupOrderList.setAddress(ds.child("address").getValue(String.class));
////
//                    GenericTypeIndicator<ArrayList<PickupOrderItems>> genericTypeIndicator =
//                            new GenericTypeIndicator<ArrayList<PickupOrderItems>>() {};
//                    pickupOrderList.setPickupOrderItems(ds.child("items").getValue(genericTypeIndicator));
//
//                    pickupOrderList.setPaymentType(ds.child("paymentType").getValue(String.class));
//                    pickupOrderList.setStatus(ds.child("status").getValue(String.class));
//                    pickupOrderList.setSubTotal(ds.child("subTotal").getValue(String.class));
////                    pickupOrderList.setKey((ds.getKey()));
//
//
//                    pickupOrderLists.add(pickupOrderList);
//                }
//
//                onRealTimeDbTaskComplete.onSuccess(pickupOrderLists);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                onRealTimeDbTaskComplete.onFailure(error);
                throw  error.toException();
            }
        });
    }

    public void getAllOrderDataRider(){
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<PendingOrderList> pendingOrderListList = new ArrayList<>();

                for(DataSnapshot ds :snapshot.getChildren()){

                    databaseReference.child(ds.getKey()).orderByChild("status").equalTo("Pending").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {


                            for(DataSnapshot ds2 :snapshot.getChildren()){
                                PendingOrderList pendingOrderList = new PendingOrderList();
                                pendingOrderList.setAddress(ds2.child("address").getValue(String.class));

                                GenericTypeIndicator<ArrayList<PendingOrders>> genericTypeIndicator =
                                        new GenericTypeIndicator<ArrayList<PendingOrders>>() {};
                                pendingOrderList.setPendingOrders(ds2.child("items").getValue(genericTypeIndicator));

                                pendingOrderList.setPaymentType(ds2.child("paymentType").getValue(String.class));
                                pendingOrderList.setStatus(ds2.child("status").getValue(String.class));
                                pendingOrderList.setSubTotal(ds2.child("subTotal").getValue(String.class));
                                pendingOrderList.setToken(ds2.child("token").getValue(String.class));
                                pendingOrderList.setOrderKey((ds2.getKey()));
                                pendingOrderList.setUserId(ds.getKey());


                                pendingOrderListList.add(pendingOrderList);
                                System.out.println("Order ===== "+pendingOrderList.getOrderKey());
                            }
                            System.out.println("order list ==="+pendingOrderListList.size());
                            onRealTimeDbTaskComplete.onSuccessRider(pendingOrderListList);

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            onRealTimeDbTaskComplete.onFailure(error);
                            throw  error.toException();
                        }
                    });

                }



            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                onRealTimeDbTaskComplete.onFailure(error);
                throw  error.toException();
            }
        });

    }

    public interface OnRealTimeDbTaskComplete{

        void onSuccess(List<PickupOrderList> pickupOrderLists);
        void onSuccessRider(List<PendingOrderList> pendingOrderListList);
        void onFailure(DatabaseError error);

    }
}
