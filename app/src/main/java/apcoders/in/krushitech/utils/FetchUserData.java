package apcoders.in.krushitech.utils;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QuerySnapshot;

import apcoders.in.krushitech.interfaces.FetchUserDataCallback;
import apcoders.in.krushitech.interfaces.FetchVendorDataCallback;
import apcoders.in.krushitech.models.User_Farmer_Model;
import apcoders.in.krushitech.models.VendorsModel;

public class FetchUserData {
    public interface GetUserByIdCallback {
        public void onCallback(String phone_Number);
    }
    public interface GetUserEmailByIdCallback {
        public void onCallback(String Email);
    }
    public FetchUserData() {
    }

    public static void getUserData(final FetchUserDataCallback callback) {

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        firebaseFirestore.setFirestoreSettings(settings);
        CollectionReference collectionReference = firebaseFirestore.collection("Farmers");

        if (firebaseAuth.getCurrentUser() != null) {
            String userId = firebaseAuth.getCurrentUser().getUid();
            Log.d("TAG", "getUserData: " + userId);

            collectionReference.whereEqualTo("user_id", userId).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        if (!task.getResult().getDocuments().isEmpty()) {
//                            if (userType.equals("Farmers")) {
                            User_Farmer_Model userData = null;
                            for (DocumentSnapshot document : task.getResult()) {
                                userData = new User_Farmer_Model(
                                        document.getString("user_id"),
                                        document.getString("full_name"),
                                        document.getString("email"),
                                        document.getString("phone_number"),
                                        document.getString("gender"),
                                        document.getString("user_type"),
                                        document.getString("state"),
                                        document.getString("user_avatar_url")
                                );
                            }
                            Log.d("TAG", "onComplete: Farmer data fetched successfully");
                            callback.onCallback(userData);
//
                        } else {
                            Log.d("TAG", "onComplete:Fetch Farmer Data ");
                            firebaseAuth.signOut();
                            callback.onCallback(null); // Return null if no data found
                        }
                    } else {
                        Log.e("TAG", "Error getting documents: ", task.getException());
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("TAG", "Failed to fetch user data: " + e.getMessage());
                }
            });
        }
    }

    public static void getVendorData(final FetchVendorDataCallback callback) {

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        firebaseFirestore.setFirestoreSettings(settings);
        CollectionReference collectionReference = firebaseFirestore.collection("Vendors");

        if (firebaseAuth.getCurrentUser() != null) {
            String userId = firebaseAuth.getCurrentUser().getUid();
            Log.d("TAG", "getUserData: " + userId);

            collectionReference.whereEqualTo("vendorId", userId).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        Log.d("TAG", "onComplete: " + task.getResult().getDocuments().size());
                        if (!task.getResult().getDocuments().isEmpty()) {
                            VendorsModel vendorData = null;

                            for (DocumentSnapshot document : task.getResult()) {
                                Log.d("TAG", "onComplete: ");
                                vendorData = new VendorsModel(
                                        document.getString("vendorId"),
                                        document.getString("name"),
                                        document.getString("email"),
                                        document.getString("shopName"),
                                        document.getString("phoneNumber"),
                                        document.getString("location"),
                                        document.getString("state"),
                                        document.getString("user_avatar_url"),
                                        document.getString("user_type")
                                );
                            }
                            Log.d("TAG", "onComplete: Vendor data fetched successfully");
                            callback.onCallback(vendorData);
                        } else {
                            Log.d("TAG", "Fetch Vendor Data: ");
//                            firebaseAuth.signOut();
                            callback.onCallback(null); // Return null if no data found
                        }
                    } else {
                        Log.e("TAG", "Error getting documents: ", task.getException());
                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("TAG", "Failed to fetch user data: " + e.getMessage());
                }
            });
        }
    }

    public static void getUserById(String userId, GetUserByIdCallback callback) {
        String phone_Number = null;
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        firebaseFirestore.setFirestoreSettings(settings);
        CollectionReference collectionReference = firebaseFirestore.collection("AllUsers");

        if (firebaseAuth.getCurrentUser() != null) {
            Log.d("TAG", "getUserData: " + userId);

            collectionReference.whereEqualTo("user_id", userId).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        if (!task.getResult().getDocuments().isEmpty()) {
                                callback.onCallback(task.getResult().getDocuments().get(0).getString("phone_number"));
                        }
                    }
                }
            });

            collectionReference.whereEqualTo("vendorId", userId).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        if (!task.getResult().getDocuments().isEmpty()) {
                            callback.onCallback(task.getResult().getDocuments().get(0).getString("phoneNumber"));
                        }
                    }
                }
            });
        }
    }


    public static void getUserEmailById(String userId, GetUserEmailByIdCallback callback) {
        String phone_Number = null;
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        firebaseFirestore.setFirestoreSettings(settings);
        CollectionReference collectionReference = firebaseFirestore.collection("AllUsers");

        if (firebaseAuth.getCurrentUser() != null) {
            Log.d("TAG", "getUserData: " + userId);
            Log.d("TAG", "onComplete: Farmer Data0");
            collectionReference.whereEqualTo("user_id", userId).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    Log.d("TAG", "onComplete: Farmer Data");
                    if (task.isSuccessful()) {
                        if (!task.getResult().getDocuments().isEmpty()) {
                            callback.onCallback(task.getResult().getDocuments().get(0).getString("email"));
                        }
                    }
                }
            });
            Log.d("TAG", "onComplete: Vendor Data0");
            collectionReference.whereEqualTo("vendorId", userId).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    Log.d("TAG", "onComplete: Vendor Data");
                    if (task.isSuccessful()) {
                        if (!task.getResult().getDocuments().isEmpty()) {
                            callback.onCallback(task.getResult().getDocuments().get(0).getString("email"));
                        }
                    }
                }
            });
        }
    }
}
