
package apcoders.in.krushitech.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import apcoders.in.krushitech.ProductDetailsActivity;
import apcoders.in.krushitech.R;
import apcoders.in.krushitech.models.ProductModel;
import es.dmoral.toasty.Toasty;

public class ProductManagementAdapter extends RecyclerView.Adapter<ProductManagementAdapter.ViewHolder> {

    Context context;
    int layout;
    List<ProductModel> ProductDataList;

    public ProductManagementAdapter(Context context, List<ProductModel> ProductDataList, int layout) {
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

    @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Glide.with(context).load(ProductDataList.get(position).getProductImagesUrl().get(0))
                .placeholder(R.drawable.logo)
                .error(R.drawable.chat_bot_img)
                .into(holder.product_image);
        holder.product_name.setText(ProductDataList.get(position).getProductName());

        holder.product_price.setText("₹" + ProductDataList.get(position).getProductPrice() );

    }

    @Override
    public int getItemCount() {
        return ProductDataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView product_image;
        TextView product_name, product_price;
        LinearLayout product_CardView,btnProductRemoveFromCart;

        public ViewHolder(Context context, List<ProductModel> ProductDataList, @NonNull View itemView) {
            super(itemView);
            product_CardView = itemView.findViewById(R.id.productCardView);
            product_image = itemView.findViewById(R.id.product_image);
            product_name = itemView.findViewById(R.id.product_name);
            product_price = itemView.findViewById(R.id.product_price);
            btnProductRemoveFromCart  = itemView.findViewById(R.id.btnProductRemoveFromCart);

            btnProductRemoveFromCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        ProductModel product = ProductDataList.get(position);

                        // Reference to the product in Firebase
                        FirebaseFirestore.getInstance()
                                .collection("Products")
                                .document(product.getProductId())
                                .delete()
                                .addOnSuccessListener(aVoid -> {
                                    // Remove the product from the list
                                    ProductDataList.remove(position);
                                    // Notify the adapter about the removed item
                                    notifyItemRemoved(position);
                                    notifyItemRangeChanged(position, ProductDataList.size());
                                    Toasty.success(context, "Product removed successfully", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    Toasty.error(context, "Failed to remove product: ", Toast.LENGTH_SHORT).show();
                                });
                    }
                }
            });

            product_CardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        Intent i = new Intent(context, ProductDetailsActivity.class);
                        Log.e("TAG", "onClick: "+ProductDataList.get(position).getProductId() );
                        i.putExtra("ProductId", ProductDataList.get(position).getProductId());
                        context.startActivity(i);
                    }
                }
            });
        }
    }
}