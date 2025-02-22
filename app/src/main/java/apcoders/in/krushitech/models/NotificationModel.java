package apcoders.in.krushitech.models;

import java.util.Date;

public class NotificationModel {
    private String ProductSenderId, ProductReceiverId,OrderId;
    private String ProductSendertitle, ProductReceivertitle;
    private String ProductSendermessage, ProductRceivermessage;
    String imageUrl;
    private Date notificationDate;
    private boolean SenderreadStatus, ReceiverreadStatus;

    // Empty constructor for Firebase
    public NotificationModel() {
    }

    public NotificationModel(String productSenderId, String productReceiverId, String orderId, String productSendertitle, String productReceivertitle, String productSendermessage, String productRceivermessage, String imageUrl, Date notificationDate, boolean SenderreadStatus, boolean ReceiverreadStatus) {
        ProductSenderId = productSenderId;
        ProductReceiverId = productReceiverId;
        OrderId = orderId;
        ProductSendertitle = productSendertitle;
        ProductReceivertitle = productReceivertitle;
        ProductSendermessage = productSendermessage;
        ProductRceivermessage = productRceivermessage;
        this.imageUrl = imageUrl;
        this.notificationDate = notificationDate;
        this.SenderreadStatus = SenderreadStatus;
        this.ReceiverreadStatus = ReceiverreadStatus;
    }

    public String getProductSenderId() {
        return ProductSenderId;
    }

    public boolean isSenderreadStatus() {
        return SenderreadStatus;
    }

    public void setSenderreadStatus(boolean senderreadStatus) {
        SenderreadStatus = senderreadStatus;
    }

    public boolean isReceiverreadStatus() {
        return ReceiverreadStatus;
    }

    public void setReceiverreadStatus(boolean receiverreadStatus) {
        ReceiverreadStatus = receiverreadStatus;
    }

    public void setProductSenderId(String productSenderId) {
        ProductSenderId = productSenderId;
    }

    public String getProductReceiverId() {
        return ProductReceiverId;
    }

    public void setProductReceiverId(String productReceiverId) {
        ProductReceiverId = productReceiverId;
    }

    public String getOrderId() {
        return OrderId;
    }

    public void setOrderId(String orderId) {
        OrderId = orderId;
    }

    public String getProductSendertitle() {
        return ProductSendertitle;
    }

    public void setProductSendertitle(String productSendertitle) {
        ProductSendertitle = productSendertitle;
    }

    public String getProductReceivertitle() {
        return ProductReceivertitle;
    }

    public void setProductReceivertitle(String productReceivertitle) {
        ProductReceivertitle = productReceivertitle;
    }

    public String getProductSendermessage() {
        return ProductSendermessage;
    }

    public void setProductSendermessage(String productSendermessage) {
        ProductSendermessage = productSendermessage;
    }

    public String getProductRceivermessage() {
        return ProductRceivermessage;
    }

    public void setProductRceivermessage(String productRceivermessage) {
        ProductRceivermessage = productRceivermessage;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Date getNotificationDate() {
        return notificationDate;
    }

    public void setNotificationDate(Date notificationDate) {
        this.notificationDate = notificationDate;
    }

}