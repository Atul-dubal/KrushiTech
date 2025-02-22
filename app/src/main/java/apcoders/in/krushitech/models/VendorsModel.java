package apcoders.in.krushitech.models;


import android.util.Log;

public class VendorsModel {
    private String vendorId;
    private String name;
    private String email;
    String shopName;
    private String phoneNumber;
    private String location;
    private String state, user_avatar_url;
    private String user_type;

    // Empty constructor for Firebase
    public VendorsModel() {
    }

    public VendorsModel(String vendorId, String name, String email, String shopName, String phoneNumber, String location, String state, String user_avatar_url, String user_type) {
        this.vendorId = vendorId;
        this.name = name;
        this.email = email;
        this.shopName = shopName;
        this.phoneNumber = phoneNumber;
        this.location = location;
        this.state = state;
        this.user_avatar_url = user_avatar_url;
        this.user_type = user_type;
    }

    public String getUser_type() {
        return user_type;
    }

    public void setUser_type(String user_type) {
        this.user_type = user_type;
    }

    public String getVendorId() {
        return vendorId;
    }

    public void setVendorId(String vendorId) {
        this.vendorId = vendorId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getState() {
        return state;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getUser_avatar_url() {
        return user_avatar_url;
    }

    public void setUser_avatar_url(String user_avatar_url) {
        this.user_avatar_url = user_avatar_url;
    }


    public void displayData() {
        Log.d("User Id :", this.vendorId);
        Log.d("Full Name :", this.name);
        Log.d("Phone Number :", this.phoneNumber);
        Log.d("Email :", this.email);
        Log.d("Shop Name :", this.shopName);
        Log.d("State :", this.state);

    }
}
