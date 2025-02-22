package apcoders.in.krushitech.utils;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import apcoders.in.krushitech.models.OrderModel;


public class OrderManagement {
    public interface FetchAllOrderDataCallback {
        public void onCallback(List<OrderModel> OrderDataList);
    }

    public interface FetchOrderByIDCallback {
        public void onCallback(OrderModel OrderData);
    }

    public static void FetchAllOrders() {

    }

    public static void FetchAllOrderData(FetchAllOrderDataCallback fetchAllOrderDataCallback) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        firebaseFirestore.setFirestoreSettings(settings);

        CollectionReference collectionReference = firebaseFirestore.collection("Orders");

        // Query to get orders for the current user and sort by orderDate in descending order
        collectionReference
                .whereEqualTo("userId", Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid())
                .orderBy("orderDate", com.google.firebase.firestore.Query.Direction.DESCENDING) // Sort by date descending
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ArrayList<OrderModel> OrderDataList = new ArrayList<>();
                        QuerySnapshot querySnapshot = task.getResult();

                        if (querySnapshot != null) {
                            for (DocumentSnapshot documentSnapshot : querySnapshot) {
                                try {
                                    OrderDataList.add(new OrderModel(
                                            Objects.requireNonNull(documentSnapshot.get("userId")).toString(),
                                            documentSnapshot.get("orderId").toString(),
                                            Objects.requireNonNull(documentSnapshot.get("productName")).toString(),
                                            documentSnapshot.get("productId").toString(),
                                            Integer.parseInt(Objects.requireNonNull(documentSnapshot.get("quantity")).toString()),
                                            Double.parseDouble(documentSnapshot.get("totalAmount").toString()),
                                            documentSnapshot.getDate("orderDate"),
                                            documentSnapshot.getDate("order_ProductFromDate"),
                                            documentSnapshot.getDate("order_ProductToDate"),
                                            documentSnapshot.get("orderStatus").toString()
                                    ));
                                } catch (Error | Exception e) {
                                    Log.d("TAG", "onComplete: " + e.toString());
                                }
                            }
                        }

                        Log.d("TAG", "onComplete: FetchAllOrderData " + OrderDataList.size());
                        fetchAllOrderDataCallback.onCallback(OrderDataList);
                    }
                }).addOnFailureListener(e -> Log.d("onFailure: FetchAllOrderData ", e.toString()));
    }

    public static void fetchOrderDetails(String orderId, FetchOrderByIDCallback fetchOrderByIDCallback) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        firebaseFirestore.setFirestoreSettings(settings);
        CollectionReference db = firebaseFirestore.collection("Orders");

        firebaseFirestore.collection("Orders").document(orderId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        OrderModel order = documentSnapshot.toObject(OrderModel.class);
                        fetchOrderByIDCallback.onCallback(order);
                    } else {
                        Log.d("TAG", "Order not found");
                    }
                })
                .addOnFailureListener(e -> Log.d("TAG", "Order not found"));
    }
}
