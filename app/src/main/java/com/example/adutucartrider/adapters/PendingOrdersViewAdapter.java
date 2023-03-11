package com.example.adutucartrider.adapters;

import static android.os.ParcelFileDescriptor.MODE_APPEND;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
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
import com.example.adutucartrider.WrapContentLinearLayoutManager;
import com.example.adutucartrider.models.PendingOrderList;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;

public class PendingOrdersViewAdapter extends RecyclerView.Adapter<PendingOrdersViewAdapter.taskViewHolder> {

    private List<PendingOrderList> pendingOrderLists;

    private DatabaseReference databaseReference;

    String riderName,riderMobile;



    public void setOrderList(List<PendingOrderList> pendingOrderListList,String riderName,String riderMobile){
        this.pendingOrderLists = pendingOrderListList;
        this.riderName = riderName;
        this.riderMobile = riderMobile;

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
        holder.PendingOrderId.setText(pendingOrderList.getOrderKey());
        holder.PendingOrderAddress.setText(pendingOrderList.getAddress());
        holder.PendingOrderPaymentType.setText(pendingOrderList.getPaymentType());
        holder.PendingOrderStatus.setText(pendingOrderList.getStatus());
        holder.PendingOrderTotal.setText(pendingOrderList.getSubTotal());

        holder.PendingOrdersCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.OrderDetails.setVisibility(View.VISIBLE);
                holder.OrderDetailsRv.setHasFixedSize(true);
                holder.OrderDetailsRv.setLayoutManager(new WrapContentLinearLayoutManager(v.getContext(), LinearLayoutManager.VERTICAL,false));
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
            @Override
            public void onClick(View v) {



                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());

                // Set the message show for the Alert time
                builder.setMessage("Do you want to pickup order ?");

                // Set Alert Title
                builder.setTitle("Alert !");

                // Set Cancelable false for when the user clicks on the outside the Dialog Box then it will remain show
                builder.setCancelable(false);

                // Set the positive button with yes name Lambda OnClickListener method is use of DialogInterface interface.
                builder.setPositiveButton("Yes", (DialogInterface.OnClickListener) (dialog, which) -> {


                            databaseReference = FirebaseDatabase.getInstance().getReference("RiderPickups").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).push();
                            String riderPickupKey = databaseReference.getKey();

                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("userId", pendingOrderList.getUserId());
                            hashMap.put("orderId",pendingOrderList.getOrderKey());
                            hashMap.put("orderAddress",pendingOrderList.getAddress());
                            hashMap.put("status","Pending");

                            databaseReference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(v.getContext(), "Order Taken", Toast.LENGTH_SHORT).show();

                                    databaseReference = FirebaseDatabase.getInstance().getReference("Orders");
                                    HashMap<String, Object> hashMap = new HashMap<>();
                                    hashMap.put("status", "ToShip");
                                    hashMap.put("riderName",riderName);
                                    hashMap.put("riderMobile",riderMobile);
                                    hashMap.put("riderId",FirebaseAuth.getInstance().getCurrentUser().getUid());
                                    hashMap.put("riderPickupKey",riderPickupKey);

                                    databaseReference.child(pendingOrderList.getUserId()).child(pendingOrderList.getOrderKey()).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            notifyDataSetChanged();
                                            Toast.makeText(v.getContext(), "Order status updated", Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(v.getContext(), "Failed to update order status", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(v.getContext(), "Order Taken failed", Toast.LENGTH_SHORT).show();

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
}
