package apcoders.in.krushitech.models;

public class ReviewModel {
    int RatingStars;
    String ReviewResponse, UserId, ProductId;

    public ReviewModel(){

    }

    public ReviewModel(int ratingStars, String reviewResponse, String userId, String productId) {
        RatingStars = ratingStars;
        ReviewResponse = reviewResponse;
        UserId = userId;
        ProductId = productId;
    }

    public int getRatingStars() {
        return RatingStars;
    }

    public void setRatingStars(int ratingStars) {
        RatingStars = ratingStars;
    }

    public String getReviewResponse() {
        return ReviewResponse;
    }

    public void setReviewResponse(String reviewResponse) {
        ReviewResponse = reviewResponse;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public String getProductId() {
        return ProductId;
    }

    public void setProductId(String productId) {
        ProductId = productId;
    }
}
