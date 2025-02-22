package apcoders.in.krushitech.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import apcoders.in.krushitech.R;
import apcoders.in.krushitech.fragments.OrderSummaryFragment;
import apcoders.in.krushitech.models.TransactionModel;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {

    Context context;

    List<TransactionModel> TransactionDataList;

    public TransactionAdapter(Context context, List<TransactionModel> TransactionDataList) {
        this.context = context;
        this.TransactionDataList = TransactionDataList;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context.getApplicationContext()).inflate(R.layout.transaction_item, parent, false);
        return new ViewHolder(view,context, TransactionDataList);
    }

    @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.tvTransactionId.setText("Transaction ID: " + TransactionDataList.get(position).getTransactionId());
        holder.OrderId.setText("Order ID: " + TransactionDataList.get(position).getOrderId());
        holder.tvTransactionDate.setText("Date: " + (TransactionDataList.get(position).getTransactionDate().toLocaleString().substring(0, 12)));
        holder.tvPaymentStatus.setText("Status: " + TransactionDataList.get(position).getPaymentStatus());
        holder.tvPaymentMethod.setText("Pay Method: " + TransactionDataList.get(position).getPaymentMethod());
        holder.tvAmountPaid.setText("₹" + TransactionDataList.get(position).getAmountPaid());

    }

    @Override
    public int getItemCount() {
        return TransactionDataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout transaction_item_container;
        TextView tvAmountPaid, tvTransactionId, tvPaymentStatus, tvPaymentMethod, OrderId, tvTransactionDate;


        public ViewHolder(@NonNull View itemView, Context context, List<TransactionModel> TransactionDataList   ) {
            super(itemView);
            transaction_item_container = itemView.findViewById(R.id.transaction_item_container);
            tvAmountPaid = itemView.findViewById(R.id.tvAmountPaid);
            tvTransactionId = itemView.findViewById(R.id.tvTransactionId);
            OrderId = itemView.findViewById(R.id.OrderID);
            tvTransactionDate = itemView.findViewById(R.id.tvTransactionDate);
            tvPaymentStatus = itemView.findViewById(R.id.tvPaymentStatus);
            tvPaymentMethod = itemView.findViewById(R.id.tvPaymentMethod);

            transaction_item_container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        // Get the order ID for the clicked order
                        String orderId = TransactionDataList.get(position).getOrderId();

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
}