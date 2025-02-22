package apcoders.in.krushitech.utils;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import apcoders.in.krushitech.models.TransactionModel;

public class TransactionsManagement {
    public interface FetchAllTransactionsCallback {
        void onCallback(List<TransactionModel> TransactionsDataList);
    }

    public static void FetchAllTransactions(FetchAllTransactionsCallback fetchAllTransactionsCallback) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        String userId = firebaseAuth.getCurrentUser().getUid();
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        CollectionReference collectionReference = firebaseFirestore.collection("Transactions");

        collectionReference
                .whereEqualTo("userId", userId)
                .orderBy("transactionDate", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e("TAG", "onEvent: FetchAllTransactions failed", error);
                        fetchAllTransactionsCallback.onCallback(null);
                        return;
                    }

                    if (value != null && !value.isEmpty()) {
                        List<TransactionModel> TransactionsDataList = value.toObjects(TransactionModel.class);
                        fetchAllTransactionsCallback.onCallback(TransactionsDataList);
                    } else {
                        fetchAllTransactionsCallback.onCallback(null);
                    }
                });
    }

}
