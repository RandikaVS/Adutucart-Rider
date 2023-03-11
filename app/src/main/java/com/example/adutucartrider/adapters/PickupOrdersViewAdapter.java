package com.example.adutucartrider.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.adutucartrider.R;
import com.example.adutucartrider.models.PickupOrderList;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class PickupOrdersViewAdapter extends FirebaseRecyclerAdapter<PickupOrderList, PickupOrdersViewAdapter.taskViewHolder> {

    private Context context;
    public PickupOrdersViewAdapter(@NonNull FirebaseRecyclerOptions<PickupOrderList> options, Context context) {
        super(options);
        this.context = context;
        System.out.println("Options##########################"+options);
    }

    @NonNull
    @Override
    public PickupOrdersViewAdapter.taskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rider_pickup_orders_card, parent, false);
        return new PickupOrdersViewAdapter.taskViewHolder(view);
    }


    @Override
    protected void onBindViewHolder(@NonNull taskViewHolder holder, int position, @NonNull PickupOrderList model) {

        System.out.println("OptionsOrder Id###############################################"+model.getAddress());
        holder.PickupOrderId.setText(model.getOrderKey());
    }


    class taskViewHolder extends RecyclerView.ViewHolder {


        private TextView PickupOrderId;

        public taskViewHolder(@NonNull View itemView) {
            super(itemView);

            PickupOrderId = itemView.findViewById(R.id.pickup_order_id);

        }
    }
}
