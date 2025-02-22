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
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import apcoders.in.krushitech.models.ProductModel;

public class FetchAllProducts {
    public interface FetchProductDataCallback {
        public void onCallback(ArrayList<ProductModel> ProductDataList);
    }

    public FetchAllProducts() {

    }

    public static void FetchAllProductsData(FetchProductDataCallback fetchProductDataCallback) {
//        Log.d("TAG", "FetchAllProductsData: ");
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        firebaseFirestore.setFirestoreSettings(settings);
        CollectionReference collectionReference = firebaseFirestore.collection("Products");

        collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    int TotalProducts = task.getResult().getDocuments().size();
//                    Log.d("TAG", "onComplete: "+TotalProducts);
                    ArrayList<ProductModel> ProductDataList = new ArrayList<>();
                    for (DocumentSnapshot documentSnapshot : task.getResult()) {
                        ProductDataList.add(new ProductModel(Objects.requireNonNull(documentSnapshot.get("productId")).toString(), documentSnapshot.get("productUserId").toString(), Objects.requireNonNull(documentSnapshot.get("productName")).toString(), Double.parseDouble(documentSnapshot.get("productPrice").toString()),documentSnapshot.get("serviceType").toString(), Integer.parseInt(documentSnapshot.get("productQuantity").toString()), Objects.requireNonNull(documentSnapshot.get("productCategory")).toString(), (GeoPoint) Objects.requireNonNull(documentSnapshot.get("productLocation")), documentSnapshot.get("productAddress").toString(), Objects.requireNonNull(documentSnapshot.get("productDescription")).toString(), Objects.requireNonNull(documentSnapshot.get("searchableString")).toString(), documentSnapshot.getDate("availableFromDate"), documentSnapshot.getDate("availableToDate"), (List<String>) documentSnapshot.get("productImagesUrl")));
                    }

                    Log.d("TAG", "onComplete: " + ProductDataList.size());
//                    Log.d("TAG", "onComplete: "+ProductDataList.get(0).getProductImagesUrl().get(0));
//                    Log.d("TAG", "onComplete: "+ProductDataList.get(1).getProductName());

                    fetchProductDataCallback.onCallback(ProductDataList);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("onFailure: FetchAllProducts ", e.toString());
            }
        });
    }

    public static void FetchFilterProductsData(String filter, FetchProductDataCallback fetchProductDataCallback) {
        Log.d("FetchFilterProductsData: ", filter);
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        firebaseFirestore.setFirestoreSettings(settings);
        CollectionReference collectionReference = firebaseFirestore.collection("Products");

        collectionReference.whereEqualTo("productCategory", filter).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    int TotalProducts = task.getResult().getDocuments().size();
                    ArrayList<ProductModel> ProductDataList = new ArrayList<>();
                    for (DocumentSnapshot documentSnapshot : task.getResult()) {
                        try {
                            ProductDataList.add(new ProductModel(Objects.requireNonNull(documentSnapshot.get("productId")).toString(), documentSnapshot.get("productUserId").toString(), Objects.requireNonNull(documentSnapshot.get("productName")).toString(), Double.parseDouble(documentSnapshot.get("productPrice").toString()),documentSnapshot.get("serviceType").toString(), Integer.parseInt(documentSnapshot.get("productQuantity").toString()), Objects.requireNonNull(documentSnapshot.get("productCategory")).toString(), (GeoPoint) Objects.requireNonNull(documentSnapshot.get("productLocation")), documentSnapshot.get("productAddress").toString(), Objects.requireNonNull(documentSnapshot.get("productDescription")).toString(), Objects.requireNonNull(documentSnapshot.get("searchableString")).toString(), documentSnapshot.getDate("availableFromDate"), documentSnapshot.getDate("availableToDate"), (List<String>) documentSnapshot.get("productImagesUrl")));
                        } catch (NullPointerException e) {
                            Log.d("TAG", "onComplete: " + e.toString());
                        }
                    }
                    Collections.shuffle(ProductDataList);
                    fetchProductDataCallback.onCallback(ProductDataList);
//                    Log.d("TAG", "onComplete: "+ProductDataList.size());
//                    Log.d("TAG", "onComplete: "+ProductDataList.get(0).getProductImagesUrl().get(0));
//                    Log.d("TAG", "onComplete: "+ProductDataList.get(1).getProduct_name());
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("onFailure: FetchAllProducts ", e.toString());
            }
        });
    }

    public static void FetchByProductId(String ProductId, FetchProductDataCallback fetchProductDataCallback) {
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        firebaseFirestore.setFirestoreSettings(settings);
        CollectionReference collectionReference = firebaseFirestore.collection("Products");

        collectionReference.whereEqualTo("productId", ProductId).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    int TotalProducts = task.getResult().getDocuments().size();
                    ArrayList<ProductModel> ProductDataList = new ArrayList<>();
                    for (DocumentSnapshot documentSnapshot : task.getResult()) {
                        try {
                            ProductDataList.add(new ProductModel(Objects.requireNonNull(documentSnapshot.get("productId")).toString(), documentSnapshot.get("productUserId").toString(), Objects.requireNonNull(documentSnapshot.get("productName")).toString(), Double.parseDouble(documentSnapshot.get("productPrice").toString()),documentSnapshot.get("serviceType").toString(), Integer.parseInt(documentSnapshot.get("productQuantity").toString()), Objects.requireNonNull(documentSnapshot.get("productCategory")).toString(), (GeoPoint) Objects.requireNonNull(documentSnapshot.get("productLocation")), documentSnapshot.get("productAddress").toString(), Objects.requireNonNull(documentSnapshot.get("productDescription")).toString(), Objects.requireNonNull(documentSnapshot.get("searchableString")).toString(), documentSnapshot.getDate("availableFromDate"), documentSnapshot.getDate("availableToDate"), (List<String>) documentSnapshot.get("productImagesUrl")));

                        } catch (NullPointerException e) {
                            Log.d("TAG", "onComplete Error: " + e.toString());
                        }
                    }
                    fetchProductDataCallback.onCallback(ProductDataList);
//                    Log.d("TAG", "onComplete: "+ProductDataList.size());
//                    Log.d("TAG", "onComplete: "+ProductDataList.get(0).getProductImagesUrl().get(0));
//                    Log.d("TAG", "onComplete: "+ProductDataList.get(1).getProduct_name());
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("onFailure: FetchAllProducts ", e.toString());
            }
        });
    }

    public static void FetchBySearchQuery(String searchQuery, FetchProductDataCallback fetchProductDataCallback) {
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        firebaseFirestore.setFirestoreSettings(settings);
        CollectionReference collectionReference = firebaseFirestore.collection("Products");

        collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    int TotalProducts = task.getResult().getDocuments().size();
                    ArrayList<ProductModel> ProductDataList = new ArrayList<>();
                    for (DocumentSnapshot documentSnapshot : task.getResult()) {
                        try {
                            if (documentSnapshot.get("searchableString").toString().contains(searchQuery)) {
                                ProductDataList.add(new ProductModel(Objects.requireNonNull(documentSnapshot.get("productId")).toString(), documentSnapshot.get("productUserId").toString(), Objects.requireNonNull(documentSnapshot.get("productName")).toString(), Double.parseDouble(documentSnapshot.get("productPrice").toString()),documentSnapshot.get("serviceType").toString(), Integer.parseInt(documentSnapshot.get("productQuantity").toString()), Objects.requireNonNull(documentSnapshot.get("productCategory")).toString(), (GeoPoint) Objects.requireNonNull(documentSnapshot.get("productLocation")), documentSnapshot.get("productAddress").toString(), Objects.requireNonNull(documentSnapshot.get("productDescription")).toString(), Objects.requireNonNull(documentSnapshot.get("searchableString")).toString(), documentSnapshot.getDate("availableFromDate"), documentSnapshot.getDate("availableToDate"), (List<String>) documentSnapshot.get("productImagesUrl")));

                            }
                        } catch (NullPointerException e) {
                            Log.d("TAG", "onComplete: " + e.toString());
                        }
                    }
                    fetchProductDataCallback.onCallback(ProductDataList);
//                    Log.d("TAG", "onComplete: "+ProductDataList.size());
//                    Log.d("TAG", "onComplete: "+ProductDataList.get(0).getProductImagesUrl().get(0));
//                    Log.d("TAG", "onComplete: "+ProductDataList.get(1).getProduct_name());
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("onFailure: FetchAllProducts ", e.toString());
            }
        });
    }

    public static void FetchProductDataByIds(Set<String> ProductIds, FetchProductDataCallback fetchProductDataCallback) {
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        firebaseFirestore.setFirestoreSettings(settings);
        CollectionReference collectionReference = firebaseFirestore.collection("Products");

        collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    int TotalProducts = task.getResult().getDocuments().size();
                    ArrayList<ProductModel> ProductDataList = new ArrayList<>();
                    for (DocumentSnapshot documentSnapshot : task.getResult()) {
                        try {
                            if (ProductIds.contains(documentSnapshot.get("id"))) {
                                ProductDataList.add(new ProductModel(Objects.requireNonNull(documentSnapshot.get("productId")).toString(), documentSnapshot.get("productUserId").toString(), Objects.requireNonNull(documentSnapshot.get("productName")).toString(), Double.parseDouble(documentSnapshot.get("productPrice").toString()), documentSnapshot.get("serviceType").toString(),Integer.parseInt(documentSnapshot.get("productQuantity").toString()), Objects.requireNonNull(documentSnapshot.get("productCategory")).toString(), (GeoPoint) Objects.requireNonNull(documentSnapshot.get("productLocation")), documentSnapshot.get("productAddress").toString(), Objects.requireNonNull(documentSnapshot.get("productDescription")).toString(), Objects.requireNonNull(documentSnapshot.get("searchableString")).toString(), documentSnapshot.getDate("availableFromDate"), documentSnapshot.getDate("availableToDate"), (List<String>) documentSnapshot.get("productImagesUrl")));

                            }
                        } catch (NullPointerException e) {
                            Log.d("TAG", "onComplete: " + e.toString());
                        }
                    }
                    fetchProductDataCallback.onCallback(ProductDataList);
//                    Log.d("TAG", "onComplete: "+ProductDataList.size());
//                    Log.d("TAG", "onComplete: "+ProductDataList.get(0).getProductImagesUrl().get(0));
//                    Log.d("TAG", "onComplete: "+ProductDataList.get(1).getProduct_name());
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("onFailure: FetchAllProducts ", e.toString());
            }
        });
    }


    public static void FetchByProductsOfFarmers(FetchProductDataCallback fetchProductDataCallback) {
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        firebaseFirestore.setFirestoreSettings(settings);
        CollectionReference collectionReference = firebaseFirestore.collection("Products");

        collectionReference.whereEqualTo("productUserId", Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    int TotalProducts = task.getResult().getDocuments().size();
                    ArrayList<ProductModel> ProductDataList = new ArrayList<>();
                    for (DocumentSnapshot documentSnapshot : task.getResult()) {
                        try {
                            ProductDataList.add(new ProductModel(Objects.requireNonNull(documentSnapshot.get("productId")).toString(), documentSnapshot.get("productUserId").toString(), Objects.requireNonNull(documentSnapshot.get("productName")).toString(), Double.parseDouble(documentSnapshot.get("productPrice").toString()),documentSnapshot.get("serviceType").toString(), Integer.parseInt(documentSnapshot.get("productQuantity").toString()), Objects.requireNonNull(documentSnapshot.get("productCategory")).toString(), (GeoPoint) Objects.requireNonNull(documentSnapshot.get("productLocation")), documentSnapshot.get("productAddress").toString(), Objects.requireNonNull(documentSnapshot.get("productDescription")).toString(), Objects.requireNonNull(documentSnapshot.get("searchableString")).toString(), documentSnapshot.getDate("availableFromDate"), documentSnapshot.getDate("availableToDate"), (List<String>) documentSnapshot.get("productImagesUrl")));

                        } catch (NullPointerException e) {
                            Log.d("TAG", "onComplete Error: " + e.toString());
                        }
                    }
                    fetchProductDataCallback.onCallback(ProductDataList);
                    Log.d("TAG", "Products Of Current User : " + ProductDataList.size());
//                    Log.d("TAG", "onComplete: "+ProductDataList.get(0).getProductImagesUrl().get(0));
//                    Log.d("TAG", "onComplete: "+ProductDataList.get(1).getProduct_name());
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("onFailure: FetchAllProducts ", e.toString());
            }
        });
    }
}
