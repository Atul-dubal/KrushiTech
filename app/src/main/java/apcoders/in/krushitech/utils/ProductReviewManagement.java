package apcoders.in.krushitech.utils;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

import apcoders.in.krushitech.models.ReviewModel;

public class ProductReviewManagement {
    public interface isReviewSubmitted {
        public void onCallback(boolean isSubmitted);
    }

    public interface GetReviewsOfProduct {
        public void onCallback(ArrayList<ReviewModel> ReviewList);
    }

    public ProductReviewManagement() {

    }

    public static void SubmitReview(int RatingStars, String ReviewResponse, String ProductId, isReviewSubmitted isReviewSubmitted) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        String UserId = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        CollectionReference collectionReference = firebaseFirestore.collection("Reviews");
        ReviewModel reviewModel = new ReviewModel(RatingStars, ReviewResponse, UserId, ProductId);
        collectionReference.add(reviewModel).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                isReviewSubmitted.onCallback(true);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                isReviewSubmitted.onCallback(false);
            }
        });
    }

    public static void GetReviewsOfProduct(String ProductId, GetReviewsOfProduct GetReviewsOfProduct) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        CollectionReference collectionReference = firebaseFirestore.collection("Reviews");

        collectionReference.whereEqualTo("productId", ProductId).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    ArrayList<ReviewModel> ProductReviewResult = new ArrayList<>();
                    for (DocumentSnapshot Review : task.getResult().getDocuments()) {
                        ReviewModel reviewModel = Review.toObject(ReviewModel.class);
                        ProductReviewResult.add(reviewModel);
                    }
                    GetReviewsOfProduct.onCallback(ProductReviewResult);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                GetReviewsOfProduct.onCallback(null);
            }
        });
    }
}
