package apcoders.in.krushitech.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import apcoders.in.krushitech.R;
import apcoders.in.krushitech.models.NotificationModel;

public class Notification {
    public interface FetchAllNotificationsCallback {
        void onCallback(List<NotificationModel> NotificationDataList);
    }

    public interface NotificationAvailabilityCallback {
        void onNotificationAvailable(boolean hasUnreadNotifications);
    }

    private static final Set<String> processedDocIds = new HashSet<>();

    public static void NotificationCheck(Context context, NotificationAvailabilityCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        String userId = firebaseAuth.getCurrentUser().getUid();

        // Fetch unread notifications where the current user is the sender
        db.collection("Notifications")
                .whereEqualTo("productSenderId", userId)
                .whereEqualTo("senderreadStatus", false)
                .get()
                .addOnCompleteListener(task -> handleNotificationQuery(task, context, callback, "Sender"));

        // Fetch unread notifications where the current user is the receiver
        db.collection("Notifications")
                .whereEqualTo("productReceiverId", userId)
                .whereEqualTo("receiverreadStatus", false)
                .get()
                .addOnCompleteListener(task -> handleNotificationQuery(task, context, callback, "Receiver"));
    }

    private static void handleNotificationQuery(Task<QuerySnapshot> task, Context context,
                                                NotificationAvailabilityCallback callback, String role) {
        if (task.isSuccessful() && task.getResult() != null) {
            boolean hasUnread = false;

            for (QueryDocumentSnapshot document : task.getResult()) {
                String docId = document.getId();
                if (!processedDocIds.contains(docId)) {
                    hasUnread = true;
                    processedDocIds.add(docId);

                    String title = role.equals("Sender")
                            ? document.getString("productSendertitle")
                            : document.getString("productReceivertitle");

                    String message = role.equals("Sender")
                            ? document.getString("productSendermessage")
                            : document.getString("productReceivermessage");

                    if (title != null && message != null) {
                        sendNotification(context, title, message);
                        updateReadStatus(role, docId);
                    }
                }
            }

            callback.onNotificationAvailable(hasUnread);
        } else {
            Log.w("FirestoreQuery", "Failed to fetch notifications.", task.getException());
            callback.onNotificationAvailable(false);
        }
    }

    private static void updateReadStatus(String role, String documentId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> update = new HashMap<>();
        if (role.equals("Sender")) {
            update.put("senderreadStatus", true);
        } else if (role.equals("Receiver")) {
            update.put("receiverreadStatus", true);
        }
        db.collection("Notifications").document(documentId).update(update)
                .addOnSuccessListener(aVoid -> Log.d("FirestoreUpdate", "Read status updated for " + documentId))
                .addOnFailureListener(e -> Log.w("FirestoreUpdate", "Failed to update read status for " + documentId, e));
    }

    public static void sendNotification(Context context, String title, String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "OrderNotification")
                .setSmallIcon(R.drawable.baseline_notifications_24) // Use your app icon here
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        notificationManager.notify(new Random().nextInt(), builder.build()); // Unique ID for each notification
    }

    public static void saveNotification(String productSenderId, String productReceiverId, String orderId, String productSendertitle, String productReceivertitle, String productSendermessage, String productReceivermessage, String imageUrl) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        NotificationModel notification = new NotificationModel(
                productSenderId,
                productReceiverId,
                orderId,
                productSendertitle,
                productReceivertitle,
                productSendermessage,
                productReceivermessage,
                imageUrl,
                new Date(),
                false,
                false
        );

        db.collection("Notifications").add(notification)
                .addOnSuccessListener(documentReference -> Log.d("SaveNotification", "Notification added with ID: " + documentReference.getId()))
                .addOnFailureListener(e -> Log.w("SaveNotification", "Error adding notification: " + e.getMessage()));
    }

    public static void FetchAllNotifications(FetchAllNotificationsCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Fetch notifications and order them by date in descending order
        db.collection("Notifications")
                .orderBy("notificationDate", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<NotificationModel> notificationList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            NotificationModel notification = document.toObject(NotificationModel.class);
                            notificationList.add(notification);
                        }
                        callback.onCallback(notificationList);
                    } else {
                        Log.w("FetchAllNotifications", "Failed to fetch notifications.", task.getException());
                        callback.onCallback(null);
                    }
                });
    }

}
