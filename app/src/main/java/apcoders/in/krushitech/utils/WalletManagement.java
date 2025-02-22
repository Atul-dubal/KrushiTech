package apcoders.in.krushitech.utils;

import android.util.Log;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.List;

import apcoders.in.krushitech.models.WalletModel;
import apcoders.in.krushitech.models.WalletTransaction;

public class WalletManagement {


    private static final String COLLECTION_NAME = "wallets";
    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String TAG = "WalletManager";

    // Method to initialize a wallet for a user with a starting balance
    public static void initializeWallet(String userId) {
        WalletModel newWallet = new WalletModel(0.0);
        newWallet.addTransaction("init_" + new Date().getTime(), 0.0, "credit", "Initial deposit");

        db.collection(COLLECTION_NAME).document(userId).set(newWallet)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Wallet initialized successfully."))
                .addOnFailureListener(e -> Log.e(TAG, "Failed to initialize wallet", e));
    }

    // Method to credit an amount to the user's wallet
    public static void creditToWallet(String userId, String transactionId, double amount, String description) {
        DocumentReference walletRef = db.collection(COLLECTION_NAME).document(userId);

        walletRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                WalletModel wallet = documentSnapshot.toObject(WalletModel.class);
                if (wallet != null) {
                    wallet.addTransaction(transactionId, amount, "credit", description);
                    walletRef.set(wallet)
                            .addOnSuccessListener(aVoid -> Log.d(TAG, "Amount credited successfully."))
                            .addOnFailureListener(e -> Log.e(TAG, "Failed to credit amount", e));
                }
            } else {
                // Initialize wallet if it does not exist
                initializeWallet(userId);
            }
        });
    }

    // Method to debit an amount from the user's wallet
    public static void debitFromWallet(String userId, String transactionId, double amount, String description) {
        DocumentReference walletRef = db.collection(COLLECTION_NAME).document(userId);

        walletRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                WalletModel wallet = documentSnapshot.toObject(WalletModel.class);
                if (wallet != null) {
                    // Check if there's enough balance before debiting
                    if (wallet.getCurrentBalance() >= amount) {
                        wallet.addTransaction(transactionId, amount, "debit", description);
                        walletRef.set(wallet)
                                .addOnSuccessListener(aVoid -> Log.d(TAG, "Amount debited successfully."))
                                .addOnFailureListener(e -> Log.e(TAG, "Failed to debit amount", e));
                    } else {
                        Log.e(TAG, "Insufficient balance for debit transaction.");
                    }
                }
            } else {
                Log.e(TAG, "Wallet does not exist. Cannot debit.");
            }
        });
    }

    // Method to retrieve the current balance of the user's wallet
    public static void getBalance(String userId, OnBalanceRetrievedListener listener) {
        db.collection(COLLECTION_NAME).document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        WalletModel wallet = documentSnapshot.toObject(WalletModel.class);
                        if (wallet != null) {
                            listener.onBalanceRetrieved(wallet.getCurrentBalance());
                        }
                    } else {
                        listener.onBalanceRetrieved(0.0); // Return 0 if wallet does not exist
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to retrieve balance", e);
                    listener.onBalanceRetrieved(null);
                });
    }

    // Method to retrieve transaction history of the user's wallet
    public static void getTransactions(String userId, OnTransactionsRetrievedListener listener) {
        db.collection(COLLECTION_NAME).document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        WalletModel wallet = documentSnapshot.toObject(WalletModel.class);
                        if (wallet != null) {
                            listener.onTransactionsRetrieved(wallet.getTransactions());
                        }
                    } else {
                        listener.onTransactionsRetrieved(null); // No transactions if wallet does not exist
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to retrieve transactions", e);
                    listener.onTransactionsRetrieved(null);
                });
    }

    // Interfaces for callback listeners
    public interface OnBalanceRetrievedListener {
        void onBalanceRetrieved(Double balance);
    }

    public interface OnTransactionsRetrievedListener {
        void onTransactionsRetrieved(List<WalletTransaction> walletTransactions);
    }

}
