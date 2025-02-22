package apcoders.in.krushitech.models;

import java.util.Date;

public class OrderModel {
    private String userId;
    private String orderId;
    private String productName;
    private int quantity;
    String productId;
    private double totalAmount;
    private Date orderDate;
    private Date order_ProductFromDate, order_ProductToDate;
    private String orderStatus;

    // Empty constructor needed for Firestore serialization
    public OrderModel() {
    }

    // Full constructor
    public OrderModel(String userId, String orderId, String productName, String productId, int quantity, double totalAmount, Date orderDate, Date order_ProductFromDate, Date order_ProductToDate, String orderStatus) {
        this.userId = userId;
        this.productId = productId;
        this.orderId = orderId;
        this.productName = productName;
        this.quantity = quantity;
        this.order_ProductFromDate = order_ProductFromDate;
        this.order_ProductToDate = order_ProductToDate;
        this.totalAmount = totalAmount;
        this.orderDate = orderDate;
        this.orderStatus = orderStatus;
    }

    // Getters and Setters
    public String getUserId() {
        return userId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public Date getOrder_ProductFromDate() {
        return order_ProductFromDate;
    }

    public void setOrder_ProductFromDate(Date order_ProductFromDate) {
        this.order_ProductFromDate = order_ProductFromDate;
    }

    public Date getOrder_ProductToDate() {
        return order_ProductToDate;
    }

    public void setOrder_ProductToDate(Date order_ProductToDate) {
        this.order_ProductToDate = order_ProductToDate;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }
}
