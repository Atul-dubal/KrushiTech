package apcoders.in.krushitech.utils;

import android.annotation.SuppressLint;
import android.util.Log;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import apcoders.in.krushitech.models.Withdrawal_Model;

public class WithdrawManagement {
    public interface WithdrawalRequestListener {
        void onWithdrawalRequestsFetched(ArrayList<Withdrawal_Model> withdrawalRequests);
    }

    public WithdrawManagement() {
    }

    // Fetch withdrawal requests of the current user from Firestore
    @SuppressLint("NotifyDataSetChanged")
    public static void FetchWithdrawalRequests(String currentUserId, WithdrawalRequestListener listener) {

        ArrayList<Withdrawal_Model> withdrawalRequests = new ArrayList<>();

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

        // Filter by the current user's ID
        firebaseFirestore.collection("Withdrawal")
                .whereEqualTo("userId", currentUserId) // Assumes userId is a field in the Withdrawal document
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null) {
                            for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                                Withdrawal_Model withdrawal = document.toObject(Withdrawal_Model.class);
                                withdrawalRequests.add(withdrawal);
                            }
                            listener.onWithdrawalRequestsFetched(withdrawalRequests);
                        } else {
                            listener.onWithdrawalRequestsFetched(new ArrayList<>());
                        }
                    } else {
                        listener.onWithdrawalRequestsFetched(null);
                        Log.d("FetchWithdrawalRequests", "Failed to fetch data");
                    }
                });
    }
}
