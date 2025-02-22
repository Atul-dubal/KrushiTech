package apcoders.in.krushitech.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;

import java.util.List;

import apcoders.in.krushitech.ProductDetailsActivity;
import apcoders.in.krushitech.R;
import apcoders.in.krushitech.models.ProductModel;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {

    Context context;
    int layout;
    List<ProductModel> ProductDataList;

    public ProductAdapter(Context context, List<ProductModel> ProductDataList, int layout) {
        this.context = context;
        this.ProductDataList = ProductDataList;
        this.layout = layout;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context.getApplicationContext()).inflate(layout, parent, false);
        return new ViewHolder(context, ProductDataList, view);
    }

    @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n", "ResourceAsColor"})
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Glide.with(context).load(ProductDataList.get(position).getProductImagesUrl().get(0))
                .placeholder(R.drawable.logo)
                .error(R.drawable.chat_bot_img)
                .into(holder.product_image);
        if(ProductDataList.get(position).getServiceType().equals("Rent")){
            holder.service_type.setBackground(context.getResources().getDrawable(R.drawable.outline_shape));
        }
        holder.service_type.setText(ProductDataList.get(position).getServiceType());
        holder.product_name.setText(ProductDataList.get(position).getProductName());
        holder.product_location.setText(ProductDataList.get(position).getProductAddress());
        holder.product_price.setText("₹" + ProductDataList.get(position).getProductPrice());

    }

    @Override
    public int getItemCount() {
        return ProductDataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView product_image;
        TextView product_name,service_type, product_price, product_location;
        CardView product_CardView;
        MaterialButton seeProductbtn;

        public ViewHolder(Context context, List<ProductModel> ProductDataList, @NonNull View itemView) {
            super(itemView);
            seeProductbtn = itemView.findViewById(R.id.seeProductbtn);
            product_CardView = itemView.findViewById(R.id.productCardView);
            service_type = itemView.findViewById(R.id.service_type);
            product_image = itemView.findViewById(R.id.product_image);
            product_name = itemView.findViewById(R.id.product_name);
            product_price = itemView.findViewById(R.id.product_price);
            product_location = itemView.findViewById(R.id.product_location);
            try {
                seeProductbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            Intent i = new Intent(context, ProductDetailsActivity.class);
                            Log.e("TAG", "onClick: " + ProductDataList.get(position).getProductId());
                            i.putExtra("ProductId", ProductDataList.get(position).getProductId());
                            context.startActivity(i);
                        }
                    }
                });
            } catch (Exception e) {

            }
            product_CardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        Intent i = new Intent(context, ProductDetailsActivity.class);
                        Log.e("TAG", "onClick: " + ProductDataList.get(position).getProductId());
                        i.putExtra("ProductId", ProductDataList.get(position).getProductId());
                        context.startActivity(i);
                    }
                }
            });

        }

    }
    @SuppressLint("NotifyDataSetChanged")
    public void updateProductList(List<ProductModel> newProductList) {
        this.ProductDataList.clear();
        this.ProductDataList.addAll(newProductList);
        notifyDataSetChanged();
    }
}