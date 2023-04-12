package com.example.adutucartrider.adapters;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static androidx.core.content.ContextCompat.getSystemService;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.adutucartrider.ImageClickListener;
import com.example.adutucartrider.R;
import com.example.adutucartrider.RiderDashboard;
import com.example.adutucartrider.models.PickupOrderList;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.HashMap;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class PickupOrdersViewAdapter extends FirebaseRecyclerAdapter<PickupOrderList, PickupOrdersViewAdapter.taskViewHolder> {

    private Context context;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private Uri imageUri = null;
    private TextView orderId,address,paymentType,status,total;
    private Button Dismiss;
    private Calendar calendar,calendar1;
    public PickupOrdersViewAdapter(@NonNull FirebaseRecyclerOptions<PickupOrderList> options, Context context) {
        super(options);
        this.context = context;
        System.out.println("Options##########################"+options);
    }


    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onBindViewHolder(@NonNull taskViewHolder holder, @SuppressLint("RecyclerView") int position, @NonNull PickupOrderList model) {

        if(model==null){
            notifyDataSetChanged();
            notifyItemRemoved(position);
        }
        holder.PickupOrderId.setText(model.getOrderId());
        holder.PickupOrderId.setPaintFlags(holder.PickupOrderId.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        if(model.getEvidence()!=null){
            Glide.with(context)

                    .load(model.getEvidence())

                    .placeholder(R.drawable.add_image)

                    .into(holder.Evidence);
        }
        else {
            holder.Evidence.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onClick(View v) {

                    //((RiderDashboard) context).addEvidence(position, getRef(position).getKey());
                    notifyDataSetChanged();
                    //holder.Evidence.setImageURI(imageUri);
                }
            });
        }


        holder.PickupOrderId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                databaseReference = FirebaseDatabase.getInstance().getReference("Orders").child(model.getUserId()).child(model.getOrderId());


                LayoutInflater inflater = (LayoutInflater)
                        v.getContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                View popupView = inflater.inflate(R.layout.pickup_order_details, null);

                orderId = popupView.findViewById(R.id.details_order_id);
                address = popupView.findViewById(R.id.detaild_order_address);
                paymentType = popupView.findViewById(R.id.details_payment_type);
                status = popupView.findViewById(R.id.details_status);
                total = popupView.findViewById(R.id.details_total);
                Dismiss = popupView.findViewById(R.id.details_dismiss_btn);

                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            orderId.setText(model.getOrderId());
                            address.setText(model.getAddress());
                            paymentType.setText(snapshot.child("paymentType").getValue(String.class));
                            status.setText(snapshot.child("status").getValue(String.class));
                            total.setText(snapshot.child("subTotal").getValue(String.class));


                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(context, "Failed to get data", Toast.LENGTH_SHORT).show();
                    }
                });
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



        if(!model.getWaitingTime().equals(" ")){

            holder.Time.setText(model.getWaitingTime());
            holder.Time.setEnabled(false);
        }
        holder.Time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar = Calendar.getInstance();
                int hours = calendar.get(Calendar.HOUR_OF_DAY);
                int mins = calendar.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
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
                                holder.Time.setText(differenceInMinutes+" min");
                                waitingTime = differenceInMinutes+" min";
                            }
                            else{
                                holder.Time.setText(differenceInHours+" hour "+differenceInMinutes+" min");
                                waitingTime = differenceInHours+" hour "+differenceInMinutes+" min";
                            }

                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("waitingTime",waitingTime);
                            databaseReference = FirebaseDatabase.getInstance().getReference("RiderPickups").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(getRef(position).getKey());

                            databaseReference.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()) {
                                        Toast.makeText(context, "Waiting time added", Toast.LENGTH_SHORT).show();
                                        notifyDataSetChanged();
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(context, "Fail to add time to database", Toast.LENGTH_SHORT).show();
                                }
                            });

                            databaseReference = FirebaseDatabase.getInstance().getReference("Orders").child(model.getUserId()).child(model.getOrderId());

                            databaseReference.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()) {
                                        Toast.makeText(context, "Customer updated", Toast.LENGTH_SHORT).show();
                                        notifyDataSetChanged();
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(context, "Fail to add time to database", Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                        else{
                            Toast.makeText(context, "Selected time is less than current time", Toast.LENGTH_SHORT).show();
                        }

                    }
                },hours,mins,false);
                timePickerDialog.show();
            }
        });
    }


    @NonNull
    @Override
    public PickupOrdersViewAdapter.taskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pickup_orders_card, parent, false);
        return new PickupOrdersViewAdapter.taskViewHolder(view);
    }




    class taskViewHolder extends RecyclerView.ViewHolder {


        private TextView PickupOrderId;
        private ImageView Evidence;
        private TextView Time;
        private ProgressBar progressBar;

        public taskViewHolder(@NonNull View itemView) {
            super(itemView);

            PickupOrderId = itemView.findViewById(R.id.order_id);
            Evidence  = itemView.findViewById(R.id.add_evidence);
            Time = itemView.findViewById(R.id.waiting_time);
            progressBar = itemView.findViewById(R.id.evidence_progress_bar);

        }
    }

//    private void selectImage(int position,String key) {
//
//        Intent intent = new Intent();
//        intent.setType("image/*");
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//        ((Activity) context).startActivityForResult(intent, 100);
//
//    }
//
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        this.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == 100 && data != null && data.getData() != null){
//
//            imageUri = data.getData();
//            //AddImage.setImageURI(imageUri);
//            System.out.println(imageUri);
//
//        }
//    }
}
