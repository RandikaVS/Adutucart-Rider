package com.example.adutucartrider.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.adutucartrider.R;
import com.example.adutucartrider.models.PendingOrderList;
import com.example.adutucartrider.models.PendingOrders;

import java.util.List;

public class PendingOrderDetailsViewAdapter extends RecyclerView.Adapter<PendingOrderDetailsViewAdapter.taskViewHolder>{

    private List<PendingOrders> pendingOrdersList;

    public void setOrderList(List<PendingOrders> pendingOrderLists){
        this.pendingOrdersList = pendingOrderLists;

    }

    @NonNull
    @Override
    public PendingOrderDetailsViewAdapter.taskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_details_card, parent, false);

        return new PendingOrderDetailsViewAdapter.taskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PendingOrderDetailsViewAdapter.taskViewHolder holder, int position) {

        PendingOrders pendingOrders = pendingOrdersList.get(position);


        holder.ProductId.setText(pendingOrders.getProductId());
        holder.ProductName.setText(pendingOrders.getProductName());
        holder.ProductQty.setText(pendingOrders.getQty());

        Glide.with(holder.itemView).load(pendingOrders.getProductImage()).placeholder(R.drawable.add_image).into(holder.ProductImage);




    }

    @Override
    public int getItemCount() {
        if(pendingOrdersList!=null){
            System.out.println("List is not null #########################");
            return pendingOrdersList.size();
        }
        else{
            System.out.println("List is null #########################");

            return 0;
        }
    }

    class taskViewHolder extends RecyclerView.ViewHolder {


        private TextView ProductId,ProductName,ProductQty;
        private ImageView ProductImage;
        public taskViewHolder(@NonNull View itemView) {
            super(itemView);

            ProductId = itemView.findViewById(R.id.product_id);
            ProductName = itemView.findViewById(R.id.product_name);
            ProductQty = itemView.findViewById(R.id.product_qty);
            ProductImage = itemView.findViewById(R.id.product_image);



        }
    }
}
