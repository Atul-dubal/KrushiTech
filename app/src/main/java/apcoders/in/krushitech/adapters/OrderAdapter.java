package apcoders.in.krushitech.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import apcoders.in.krushitech.R;
import apcoders.in.krushitech.fragments.OrderSummaryFragment;
import apcoders.in.krushitech.models.OrderModel;
import apcoders.in.krushitech.models.ProductModel;
import apcoders.in.krushitech.utils.FetchAllProducts;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {

    private Context context;
    private List<OrderModel> OrderDataList;

    public OrderAdapter(Context context, List<OrderModel> OrderDataList) {
        this.context = context;
        this.OrderDataList = OrderDataList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order, parent, false);
        return new ViewHolder(context, OrderDataList, view);
    }

    @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n", "ResourceAsColor"})
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderModel order = OrderDataList.get(position);
        holder.product_name.setText(order.getProductName());
        holder.OrderId.setText("Order ID: " + order.getOrderId());
        holder.OrderDate.setText("Date: " + order.getOrderDate().toLocaleString().substring(0, 12));
        holder.orderStatus.setText("Status: " + order.getOrderStatus());
        if(order.getOrderStatus().equals("Canceled")){
            holder.orderStatus.setBackgroundColor(context.getResources().getColor(R.color.yellow));
            holder.orderStatus.setTextColor(context.getResources().getColor(R.color.red));
        }
        holder.orderQuantityAndPrice.setText("Quantity: " + order.getQuantity() + " - Total: ₹" + order.getTotalAmount());
        setProductImage(holder.product_image, order.getProductId());
    }

    @Override
    public int getItemCount() {
        return OrderDataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView product_image;
        TextView product_name, orderStatus, orderQuantityAndPrice, OrderId, OrderDate;
        RelativeLayout order_item;

        public ViewHolder(Context context, List<OrderModel> OrderDataList, @NonNull View itemView) {
            super(itemView);

            order_item = itemView.findViewById(R.id.order_item);
            product_image = itemView.findViewById(R.id.product_image);
            product_name = itemView.findViewById(R.id.ProductName);
            OrderId = itemView.findViewById(R.id.OrderID);
            OrderDate = itemView.findViewById(R.id.orderDate);
            orderStatus = itemView.findViewById(R.id.tvOrderStatus);
            orderQuantityAndPrice = itemView.findViewById(R.id.tvQuantityAndTotal);

            order_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        // Get the order ID for the clicked order
                        String orderId = OrderDataList.get(position).getOrderId();

                        // Create a bundle to pass the orderId to the fragment
                        Bundle bundle = new Bundle();
                        bundle.putString("ORDER_ID", orderId);

                        // Create the OrderSummaryFragment and set the arguments
                        Fragment fragment = new OrderSummaryFragment();
                        fragment.setArguments(bundle);

                        // Replace the current fragment with the OrderSummaryFragment
                        FragmentTransaction transaction = ((FragmentActivity) context).getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.framelayout, fragment);
                        transaction.addToBackStack(null);  // Add to back stack if needed
                        transaction.commit();
                    }
                }
            });
        }
    }

    private void setProductImage(ImageView order_product_img, String ProductId) {
        FetchAllProducts.FetchByProductId(ProductId, new FetchAllProducts.FetchProductDataCallback() {
            @Override
            public void onCallback(ArrayList<ProductModel> ProductDataList) {

                Glide.with(context).load(ProductDataList.get(0).getProductImagesUrl().get(0))
                        .placeholder(R.drawable.logo)
                        .error(R.drawable.chat_bot_img)
                        .into(order_product_img);
            }
        });

    }
}
