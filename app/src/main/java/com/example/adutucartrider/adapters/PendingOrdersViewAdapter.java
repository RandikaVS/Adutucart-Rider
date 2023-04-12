package com.example.adutucartrider.adapters;

import static android.os.ParcelFileDescriptor.MODE_APPEND;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import static com.example.adutucartrider.models.Constants.TITLE;
import static com.example.adutucartrider.models.Constants.TOPIC;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.adutucartrider.R;
import com.example.adutucartrider.RiderDashboard;
import com.example.adutucartrider.WrapContentLinearLayoutManager;
import com.example.adutucartrider.api.ApiUtilities;
import com.example.adutucartrider.models.NotificationData;
import com.example.adutucartrider.models.PendingOrderList;
import com.example.adutucartrider.models.Rider;
import com.example.adutucartrider.services.pushNotification;
import com.example.adutucartrider.services.sendNotification;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PendingOrdersViewAdapter extends RecyclerView.Adapter<PendingOrdersViewAdapter.taskViewHolder> {

    private List<PendingOrderList> pendingOrderLists;


    private DatabaseReference databaseReference;

    private Context context;


    String riderName,riderMobile,token;



    public void setOrderList(List<PendingOrderList> pendingOrderListList,String riderName,String riderMobile,Context context){

        this.riderName = riderName;
        this.riderMobile = riderMobile;
        this.context = context;

        this.pendingOrderLists = pendingOrderListList;

        SharedPreferences sharedpreferences;
        token="null";

        sharedpreferences = context.getSharedPreferences("shared_prefs", Context.MODE_PRIVATE);

        if(sharedpreferences.getString("token", "")!=null){
            token = sharedpreferences.getString("token", "");
        }

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        //Get new FCM registration token
                        token = task.getResult();

                        System.out.println("token ################################# "+token);


                    }
                });

    }

    @NonNull
    @Override
    public PendingOrdersViewAdapter.taskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pending_orders_card, parent, false);

        return new PendingOrdersViewAdapter.taskViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull PendingOrdersViewAdapter.taskViewHolder holder, int position) {


        PendingOrderList pendingOrderList = pendingOrderLists.get(position);

        ((RiderDashboard)context).setPendingOrderCount(String.valueOf(pendingOrderLists.size()));


            holder.PendingOrderId.setText(pendingOrderList.getOrderKey());
            holder.PendingOrderAddress.setText(pendingOrderList.getAddress());
            holder.PendingOrderPaymentType.setText(pendingOrderList.getPaymentType());
            holder.PendingOrderStatus.setText(pendingOrderList.getStatus());
            holder.PendingOrderTotal.setText(pendingOrderList.getSubTotal());

            holder.PendingOrdersCard.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onClick(View v) {
                    holder.OrderDetails.setVisibility(View.VISIBLE);
                    holder.OrderDetailsRv.setHasFixedSize(true);
                    holder.OrderDetailsRv.setLayoutManager(new WrapContentLinearLayoutManager(v.getContext(), LinearLayoutManager.VERTICAL, false));
                    PendingOrderDetailsViewAdapter pendingOrderDetailsViewAdapter = new PendingOrderDetailsViewAdapter();
                    pendingOrderDetailsViewAdapter.setOrderList(pendingOrderList.getPendingOrders());
                    holder.OrderDetailsRv.setAdapter(pendingOrderDetailsViewAdapter);
                    pendingOrderDetailsViewAdapter.notifyDataSetChanged();
                }
            });

            holder.DetailsCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.OrderDetails.setVisibility(View.GONE);
                }
            });

            holder.PickupOrder.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onClick(View v) {



                databaseReference = FirebaseDatabase.getInstance().getReference("Rider");

                    databaseReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){
                                String pending = snapshot.child("pending").getValue(String.class);

                                assert pending != null;
                                if(!pending.equals("1")){
                                    getPickup(pendingOrderList);
                                    notifyDataSetChanged();
                                }
                                else {
                                    Toast.makeText(context, "Please complete current pickup", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

//                databaseReference = FirebaseDatabase.getInstance().getReference("RiderPickups").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
//
//                databaseReference.orderByChild("status").equalTo("ToShip").addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        if(snapshot.exists()) {
//                            Toast.makeText(v.getContext(), "Please complete current order !!!", Toast.LENGTH_SHORT).show();
//                        }
//                        else {
//
//                            AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
//
//                            // Set the message show for the Alert time
//                            builder.setMessage("Do you want to pickup order ?");
//
//                            // Set Alert Title
//                            builder.setTitle("Alert !");
//
//                            // Set Cancelable false for when the user clicks on the outside the Dialog Box then it will remain show
//                            builder.setCancelable(false);
//
//                            // Set the positive button with yes name Lambda OnClickListener method is use of DialogInterface interface.
//                            builder.setPositiveButton("Yes", (DialogInterface.OnClickListener) (dialog, which) -> {
//
//
//                                        databaseReference = FirebaseDatabase.getInstance().getReference("RiderPickups").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).push();
//
//                                        String riderPickupKey = databaseReference.getKey();
//
//                                        HashMap<String, Object> hashMap = new HashMap<>();
//                                        hashMap.put("userId", pendingOrderList.getUserId());
//                                        hashMap.put("orderId",pendingOrderList.getOrderKey());
//                                        hashMap.put("orderAddress",pendingOrderList.getAddress());
//                                        hashMap.put("status","ToShip");
//                                        hashMap.put("waitingTime"," ");
//                                        hashMap.put("token",token);
//
//
//                                        databaseReference.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                            @SuppressLint("NotifyDataSetChanged")
//                                            @Override
//                                            public void onComplete(@NonNull Task<Void> task) {
//                                                Toast.makeText(v.getContext(), "Order Taken", Toast.LENGTH_SHORT).show();
//                                                notifyDataSetChanged();
//
//                                                try {
//                                                    notification.sendNotificationToCustomer(pendingOrderList.getOrderKey(),pendingOrderList.getToken());
//                                                } catch (IOException e) {
//                                                    throw new RuntimeException(e);
//                                                }
//
//                                                databaseReference = FirebaseDatabase.getInstance().getReference("Orders");
//                                                HashMap<String, Object> hashMap = new HashMap<>();
//                                                hashMap.put("waitingTime"," ");
//                                                hashMap.put("status", "ToShip");
//                                                hashMap.put("riderName",riderName);
//                                                hashMap.put("riderMobile",riderMobile);
//                                                hashMap.put("riderId",FirebaseAuth.getInstance().getCurrentUser().getUid());
//                                                hashMap.put("riderPickupKey",riderPickupKey);
//
//                                                databaseReference.child(pendingOrderList.getUserId()).child(pendingOrderList.getOrderKey()).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                    @SuppressLint("NotifyDataSetChanged")
//                                                    @Override
//                                                    public void onComplete(@NonNull Task<Void> task) {
//                                                        if(task.isSuccessful()) {
//                                                            notifyDataSetChanged();
//                                                            try {
//                                                                notifyDataSetChanged();
//                                                                notification.sendNotificationToRider(riderPickupKey,token);
//                                                                notification.sendNotificationToChannel(pendingOrderList.getOrderKey(),"riderPickup");
//                                                            } catch (IOException e) {
//                                                                throw new RuntimeException(e);
//                                                            }
//
//                                                            Toast.makeText(v.getContext(), "Order status updated", Toast.LENGTH_SHORT).show();
//                                                        }
//                                                        else{
//                                                            Toast.makeText(v.getContext(), "Failed to update order status", Toast.LENGTH_SHORT).show();
//                                                        }
//
//
//                                                    }
//                                                }).addOnFailureListener(new OnFailureListener() {
//                                                    @Override
//                                                    public void onFailure(@NonNull Exception e) {
//                                                        Toast.makeText(v.getContext(), "Failed to update order db error", Toast.LENGTH_SHORT).show();
//                                                    }
//                                                });
//                                            }
//                                        }).addOnFailureListener(new OnFailureListener() {
//                                            @Override
//                                            public void onFailure(@NonNull Exception e) {
//                                                Toast.makeText(v.getContext(), "Order Taken failed", Toast.LENGTH_SHORT).show();
//
//                                            }
//                                        });
//
//                            });
//
//                            // Set the Negative button with No name Lambda OnClickListener method is use of DialogInterface interface.
//                            builder.setNegativeButton("No", (DialogInterface.OnClickListener) (dialog, which) -> {
//                                // If user click no then dialog box is canceled.
//                                dialog.cancel();
//                            });
//
//                            // Create the Alert dialog
//                            AlertDialog alertDialog = builder.create();
//                            // Show the Alert Dialog box
//                            alertDialog.show();
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//                        Toast.makeText(v.getContext(), "System error !!!", Toast.LENGTH_SHORT).show();
//                    }
//                });

                }
            });
    }

    @Override
    public int getItemCount() {
        if(pendingOrderLists!=null){
            System.out.println("List is not null #########################");
            return pendingOrderLists.size();
        }
        else{
            System.out.println("List is null #########################");

            return 0;
        }
    }




    class taskViewHolder extends RecyclerView.ViewHolder {


        private TextView PendingOrderId,PendingOrderAddress,PendingOrderPaymentType,PendingOrderStatus,PendingOrderTotal;
        private LinearLayout PendingOrdersCard,OrderDetails;

        private RecyclerView OrderDetailsRv;
        private ImageView DetailsCancel;
        private AppCompatButton PickupOrder;
        public taskViewHolder(@NonNull View itemView) {
            super(itemView);

            PendingOrderId = itemView.findViewById(R.id.pending_order_id);
            PendingOrderAddress = itemView.findViewById(R.id.pending_order_address);
            PendingOrderPaymentType = itemView.findViewById(R.id.pending_order_payment_type);
            PendingOrderStatus = itemView.findViewById(R.id.pending_order_status);
            PendingOrdersCard = itemView.findViewById(R.id.pending_order_card);
            PendingOrderTotal = itemView.findViewById(R.id.pending_order_total);
            OrderDetailsRv = itemView.findViewById(R.id.order_details_rv);
            DetailsCancel = itemView.findViewById(R.id.details_cancel);
            OrderDetails = itemView.findViewById(R.id.order_details);
            PickupOrder = itemView.findViewById(R.id.order_pickup_btn);


        }
    }

    private void getPickup(PendingOrderList pendingOrderList){

        sendNotification notification = new sendNotification();



                    AlertDialog.Builder builder = new AlertDialog.Builder(context);

                    // Set the message show for the Alert time
                    builder.setMessage("Do you want to pickup order ?");

                    // Set Alert Title
                    builder.setTitle("Alert !");

                    // Set Cancelable false for when the user clicks on the outside the Dialog Box then it will remain show
                    builder.setCancelable(false);

                    // Set the positive button with yes name Lambda OnClickListener method is use of DialogInterface interface.
                    String finalCustomerToken = pendingOrderList.getToken();
                    builder.setPositiveButton("Yes", (DialogInterface.OnClickListener) (dialog, which) -> {


                        databaseReference = FirebaseDatabase.getInstance().getReference("Orders");
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("status", "ToShip");
                        hashMap.put("riderName", riderName);
                        hashMap.put("riderMobile", riderMobile);
                        hashMap.put("evidence", "null");
                        hashMap.put("riderId", FirebaseAuth.getInstance().getCurrentUser().getUid());

                        databaseReference.child(pendingOrderList.getUserId()).child(pendingOrderList.getOrderKey()).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @SuppressLint("NotifyDataSetChanged")
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    setRiderPendingOrder();
                                    notifyDataSetChanged();

                                        notifyDataSetChanged();
                                        pushNotification pushNotification = new pushNotification(new NotificationData("Order Pickup Alert","Order Id "+pendingOrderList.getOrderKey()+"\nRider Name : "+riderName),finalCustomerToken);
                                        sendNotify(pushNotification);
                                        pushNotification pushNotification2 = new pushNotification(new NotificationData("Order Pickup Alert","Order Id "+pendingOrderList.getOrderKey()),token);
                                        sendNotify(pushNotification2);

                                    Toast.makeText(context, "Order status updated", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(context, "Failed to update order status", Toast.LENGTH_SHORT).show();
                                }


                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(context, "Failed to update order db error", Toast.LENGTH_SHORT).show();
                            }
                        });


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

    private void setRiderPendingOrder(){

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("pending", "1");
        databaseReference = FirebaseDatabase.getInstance().getReference("Rider");

        databaseReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(context, "Your pending count updated", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Database error", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void sendNotify(pushNotification pushNotification) {

        ApiUtilities.getClient().sendNotification(pushNotification).enqueue(new Callback<com.example.adutucartrider.services.pushNotification>() {
            @Override
            public void onResponse(Call<com.example.adutucartrider.services.pushNotification> call, Response<com.example.adutucartrider.services.pushNotification> response) {

                if(response.isSuccessful()){
                    Toast.makeText(context, "Notification send", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(context, "Fail to to send notification", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<com.example.adutucartrider.services.pushNotification> call, Throwable t) {
                Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
