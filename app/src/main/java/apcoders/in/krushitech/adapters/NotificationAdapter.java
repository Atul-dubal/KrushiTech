package apcoders.in.krushitech.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Objects;

import apcoders.in.krushitech.R;
import apcoders.in.krushitech.models.NotificationModel;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private final Context context;
    private final String UserId;
    private final List<NotificationModel> NotificationDataList;

    public NotificationAdapter(Context context, String UserId, List<NotificationModel> NotificationDataList) {
        this.context = context;
        this.UserId = UserId;
        this.NotificationDataList = NotificationDataList;
    }

    // Method to update the notification list
    @SuppressLint("NotifyDataSetChanged")
    public void updateList(List<NotificationModel> newNotificationDataList) {
        // Clear the existing list
        NotificationDataList.clear();
        // Add all items from the new list
        NotificationDataList.addAll(newNotificationDataList);
        // Notify RecyclerView that data has changed
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.notification_recyclerview_layout, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override

    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NotificationModel notification = NotificationDataList.get(position);
        if (Objects.equals(notification.getProductSenderId(), UserId) || Objects.equals(notification.getProductReceiverId(), UserId)) {
            // Load image using Glide with disk cache strategy for better performance
            Glide.with(context)
                    .load(notification.getImageUrl())
                    .placeholder(R.drawable.logo)
                    .error(R.drawable.chat_bot_img)
                    .diskCacheStrategy(DiskCacheStrategy.ALL) // Caching images
                    .into(holder.notification_image);

            String title = null, message = null;

            // Check if the notification has valid data

            // Handling sender's notification
            if (UserId.equals(notification.getProductSenderId())) {
                title = notification.getProductSendertitle();
                message = notification.getProductSendermessage();
                Log.d("TAG", "Sender Notification Date: " + notification.getNotificationDate());
            }
            // Handling receiver's notification
            else if (UserId.equals(notification.getProductReceiverId())) {
                title = notification.getProductReceivertitle();
                message = notification.getProductRceivermessage();
                Log.d("TAG", "Receiver Notification Date: " + notification.getNotificationDate());
            }

            // Format the date properly using SimpleDateFormat
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy"); // Adjust format as needed
            String formattedDate = null;

            if (notification.getNotificationDate() != null) {
                formattedDate = sdf.format(notification.getNotificationDate());
            }

            if (formattedDate != null) {
                holder.notification_date.setText(formattedDate);
            } else {
                Log.e("TAG", "Notification date is null");
            }

            // Only set text if title and message are not null or empty
            if (title != null && !title.isEmpty()) {
                holder.notification_title.setText(title);
            } else {
                holder.notification_title.setVisibility(View.GONE); // Hide title if empty
            }

            if (message != null && !message.isEmpty()) {
                holder.notification_message.setText(message);
            } else {
                holder.notification_message.setVisibility(View.GONE); // Hide message if empty
            }
        }
    }

    @Override
    public int getItemCount() {
        return NotificationDataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView notification_image;
        TextView notification_title, notification_date, notification_message;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            notification_date = itemView.findViewById(R.id.notification_date);
            notification_image = itemView.findViewById(R.id.notification_image);
            notification_title = itemView.findViewById(R.id.notification_title);
            notification_message = itemView.findViewById(R.id.notification_message);
        }
    }
}
