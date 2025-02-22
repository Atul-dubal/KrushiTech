package apcoders.in.krushitech.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import apcoders.in.krushitech.R;
import apcoders.in.krushitech.models.Withdrawal_Model;

import java.util.ArrayList;

public class WithdrawalRequestAdapter extends RecyclerView.Adapter<WithdrawalRequestAdapter.ViewHolder> {

    private ArrayList<Withdrawal_Model> withdrawalRequests =new ArrayList<>();

    public WithdrawalRequestAdapter(ArrayList<Withdrawal_Model> withdrawalRequests) {
        this.withdrawalRequests = withdrawalRequests;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_withdrawal_request, parent, false);

        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Withdrawal_Model withdrawal = withdrawalRequests.get(position);

        holder.upiIdTextView.setText("UPI ID: " + withdrawal.getUpiId());
        holder.amountTextView.setText(""+ withdrawal.getAmount());
        holder.tvRequestId.setText("Request ID: " + withdrawal.getRequestId());
        if (withdrawal.getStatus().equals("Pending")){
            holder.statusTextView.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.black));
            holder.statusTextView.setBackgroundColor(holder.itemView.getContext().getResources().getColor(R.color.yellow));
        } else if (withdrawal.getStatus().equals("Completed")) {
            holder.statusTextView.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.white));
        holder.statusTextView.setBackgroundColor(holder.itemView.getContext().getResources().getColor(R.color.dark_green));
        }else {
            holder.statusTextView.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.white));
            holder.statusTextView.setBackgroundColor(holder.itemView.getContext().getResources().getColor(R.color.red));
        }
        holder.statusTextView.setText("Status: " + withdrawal.getStatus());
        holder.tvMessage.setText("Message: " + withdrawal.getMsg());
        holder.tvTransactionDate.setText("Date: " + withdrawal.getRequestDate().toLocaleString().substring(0,12));
    }

    @Override
    public int getItemCount() {
        return withdrawalRequests.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView upiIdTextView, amountTextView,tvRequestId,tvMessage, statusTextView,tvTransactionDate;

        public ViewHolder(View itemView) {
            super(itemView);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            tvRequestId = itemView.findViewById(R.id.tvRequestId);
            tvTransactionDate = itemView.findViewById(R.id.tvTransactionDate);
            upiIdTextView = itemView.findViewById(R.id.tvUPI_ID);
            amountTextView = itemView.findViewById(R.id.text_amount);
            statusTextView = itemView.findViewById(R.id.text_status);
        }
    }
}
