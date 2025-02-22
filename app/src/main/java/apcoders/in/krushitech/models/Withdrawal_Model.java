package apcoders.in.krushitech.models;

import java.util.Date;

public class Withdrawal_Model {
    private String userId;
    private String upiId;
    private double amount;
    private Date RequestDate;
    private String msg;
    private String status; // "Pending", "Completed", or "Rejected"
    private String requestId;

    public Withdrawal_Model() {
        // Empty constructor for Firebase
    }

    public Withdrawal_Model(String userId, String upiId, double amount, Date RequestDate, String msg, String status, String requestId) {
        this.userId = userId;
        this.upiId = upiId;
        this.amount = amount;
        this.RequestDate = RequestDate;
        this.msg = msg;
        this.status = status;
        this.requestId = requestId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUpiId() {
        return upiId;
    }

    public void setUpiId(String upiId) {
        this.upiId = upiId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Date getRequestDate() {
        return RequestDate;
    }

    public void setRequestDate(Date requestDate) {
        RequestDate = requestDate;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
}
