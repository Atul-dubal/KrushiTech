package apcoders.in.krushitech.utils;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import apcoders.in.krushitech.R;
import apcoders.in.krushitech.models.ProductModel;

public class ProductManagement {
   public interface uploadProductCallback {
       public  void  onCallback (String message);
    }
    public static void uploadProduct(Context context, final String productUserId, final String productName, final double productPrice,String serviceType,
                                     final int productQuantity, final String productCategory, final GeoPoint productLocation,
                                     final String productAddress, final String productDescription,
                                     final Date availableFromDate, final Date availableToDate, List<Uri> imageUris, uploadProductCallback uploadProductCallback) {
        Log.d("TAG", "uploadProduct: ");
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        List<String> uploadedImageUrls = new ArrayList<>();

        // Step 1: Upload each image
        for (Uri imageUri : imageUris) {
            uploadImage(context, imageUri, new OnImageUploadListener() {
                @Override
                public void onImageUploaded(String imageUrl) {
                    uploadedImageUrls.add(imageUrl);

                    // When all images are uploaded, add product to Firestore
                    if (uploadedImageUrls.size() == imageUris.size()) {
                        saveProductToFirestore(productUserId, productName, productPrice,serviceType, productQuantity,
                                productCategory, productLocation, productAddress, productDescription,
                                availableFromDate, availableToDate, uploadedImageUrls, new uploadProductCallback() {
                                    @Override
                                    public void onCallback(String message) {
                                        Log.d("TAG", "onCallback: upload Success");
                                        uploadProductCallback.onCallback("upload Success");
                                    }
                                });
                    }
                }

                @Override
                public void onFailure(Exception e) {
//                    Toast.makeText(context, "Image upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    public static void uploadImage(Context context, Uri imageUri, final OnImageUploadListener listener) {
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        final StorageReference imageRef = storageReference.child("product_images/" + System.currentTimeMillis() + "_" + imageUri.getLastPathSegment());

        View view = LayoutInflater.from(context).inflate(R.layout.product_upload_progress, null, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(context).setView(view).setCancelable(false);
        ProgressBar progressBar = view.findViewById(R.id.progressBar);
        TextView progress_name = view.findViewById(R.id.progress_name_textview);
        AlertDialog dialog = builder.create();

        imageRef.putFile(imageUri).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                double progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());

                dialog.show();

                // Update the progress bar with the current upload progress
                progressBar.setProgress((int) progress);

                // Update the progress text with the percentage value
                progress_name.setText("Product Images Are Uploading : " + (int) progress + "%");
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        dialog.dismiss();
                        listener.onImageUploaded(uri.toString());
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                listener.onFailure(e);
            }
        });
    }

    public static void saveProductToFirestore(String productUserId, String productName, double productPrice,String serviceType, int productQuantity,
                                              String productCategory, GeoPoint productLocation, String productAddress,
                                              String productDescription, Date availableFromDate, Date availableToDate,
                                              List<String> productImagesUrl, uploadProductCallback uploadProductCallback) {

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        Log.d("saveProductToFirestore: ", productName + " " + productPrice + " " + productQuantity + " " + productCategory + " " + productLocation + " " + productAddress + " " + productDescription);
        String searchaableString = productName.toLowerCase() + " " + productPrice + " " + productQuantity + " " + productCategory.toLowerCase()  + " " + productAddress.toLowerCase() + " " + productDescription.toLowerCase();
//

        // Create product object
        ProductModel product = new ProductModel(productUserId, productName, productPrice,serviceType, productQuantity,
                productCategory, productLocation, productAddress, productDescription, searchaableString, availableFromDate,
                availableToDate, productImagesUrl);

        // Step 2: Save product in Firestore
        firebaseFirestore.collection("Products").add(product).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        if (documentReference != null) {
                            String documentId = documentReference.getId();
                            documentReference.update("productId", documentId);
                        }
                    }
                }).
                addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                            Log.d("TAG", "onComplete: Product Uploaded Successfully ");
                            uploadProductCallback.onCallback("Product Uploaded Successfully");
//                    Toast.makeText(context, "Product uploaded successfully", Toast.LENGTH_SHORT).show();
                        } else {
//                    Toast.makeText(context, "Product upload failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    // Interface to listen for image upload completion
    interface OnImageUploadListener {
        void onImageUploaded(String imageUrl);

        void onFailure(Exception e);
    }

    public static void updateProblemStatus(String orderId, String newStatus, ProblemStatusUpdateCallback callback) {
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        CollectionReference collection = firebaseFirestore.collection("Orders");

        // Step 1: Find the document where problemId matches
        collection.whereEqualTo("orderId", orderId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                // Get the document ID
                String documentId = task.getResult().getDocuments().get(0).getId();

                // Step 2: Update the document's status
                collection.document(documentId).update("orderStatus", newStatus)
                        .addOnSuccessListener(aVoid -> {
                            callback.onStatusUpdated(true, "Problem status updated successfully.");
                            Log.d(TAG, "Problem status updated for problemId: " + orderId);
                        })
                        .addOnFailureListener(e -> {
                            callback.onStatusUpdated(false, "Failed to update problem status.");
                            Log.e(TAG, "Error updating problem status for problemId: " + orderId, e);
                        });
            } else {
                callback.onStatusUpdated(false, "Problem not found.");
                Log.e(TAG, "Problem not found for problemId: " + orderId);
            }
        }).addOnFailureListener(e -> {
            callback.onStatusUpdated(false, "Error fetching problem.");
            Log.e(TAG, "Error fetching problem for status update: ", e);
        });
    }

    // Callback interface for status updates
    public interface ProblemStatusUpdateCallback {
        void onStatusUpdated(boolean success, String message);
    }
}
