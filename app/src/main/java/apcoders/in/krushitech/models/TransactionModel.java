package apcoders.in.krushitech.models;
import java.util.Date;

public class TransactionModel {
    private String transactionId;
    private String orderId;
    private String userId;
    String productId ;
    private String paymentMethod;
    private double amountPaid;
    private Date transactionDate;
    private String paymentStatus;

    // Empty constructor needed for Firestore serialization
    public TransactionModel() {}

    // Full constructor
    public TransactionModel(String transactionId, String orderId, String userId,String productId, String paymentMethod, double amountPaid, Date transactionDate, String paymentStatus) {
        this.transactionId = transactionId;
        this.orderId = orderId;
        this.userId = userId;
        this.productId = productId;
        this.paymentMethod = paymentMethod;
        this.amountPaid = amountPaid;
        this.transactionDate = transactionDate;
        this.paymentStatus = paymentStatus;
    }

    // Getters and Setters
    public String getTransactionId() {
        return transactionId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public double getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(double amountPaid) {
        this.amountPaid = amountPaid;
    }

    public Date getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(Date transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
}
