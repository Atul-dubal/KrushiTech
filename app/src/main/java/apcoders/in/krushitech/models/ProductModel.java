package apcoders.in.krushitech.models;

import com.google.firebase.firestore.GeoPoint;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ProductModel implements Serializable {
    private String ServiceType;
    private String productId;         // Unique ID for the product in Firestore
    private String productName;       // Name of the equipment
    private double productPrice;      // Price of the equipment
    private int productQuantity;      // Quantity available for rent/purchase
    private String productCategory;   // Category/type of the equipment
    private GeoPoint productLocation; // Geographical location of the equipment
    private String productAddress;    // Physical address of the equipment
    private String productDescription;// Description of the equipment
    private String productUserId;     // ID of the user uploading the product
    private Date availableFromDate;   // Start of available date range
    private Date availableToDate;     // End of available date range
    private Date uploadDate;          // Date of product upload
    private String searchableString;  // Concatenated searchable string for querying
    private List<String> productImagesUrl; // List of image URLs for the product

    // Default constructor (required for Firestore)
    public ProductModel() {
        this.productImagesUrl = new ArrayList<>();
        this.uploadDate = new Date(); // Set to current date by default
    }

    // Constructor with essential fields
    public ProductModel(String productUserId, String productName, double productPrice, String serviceType, int productQuantity,
                        String productCategory, GeoPoint productLocation, String productAddress,
                        String productDescription, String searchableString, Date availableFromDate, Date availableToDate,
                        List<String> productImagesUrl) {
        this.productUserId = productUserId;
        this.productName = productName;
        this.productPrice = productPrice;
        this.ServiceType = serviceType;
        this.productQuantity = productQuantity;
        this.searchableString = searchableString;
        this.productCategory = productCategory;
        this.productLocation = productLocation;
        this.productAddress = productAddress;
        this.productDescription = productDescription;
        this.availableFromDate = availableFromDate;
        this.availableToDate = availableToDate;
        this.uploadDate = new Date(); // Set to current date
        this.productImagesUrl = productImagesUrl != null ? productImagesUrl : new ArrayList<>();
    }

    public ProductModel(String ProductId, String productUserId, String productName, double productPrice, String serviceType, int productQuantity,
                        String productCategory, GeoPoint productLocation, String productAddress,
                        String productDescription, String searchableString, Date availableFromDate, Date availableToDate,
                        List<String> productImagesUrl) {
        this.productUserId = productUserId;
        this.productName = productName;
        this.productPrice = productPrice;
        this.productId = ProductId;
        this.productQuantity = productQuantity;
        this.searchableString = searchableString;
        this.productCategory = productCategory;
        this.ServiceType = serviceType;
        this.productLocation = productLocation;
        this.productAddress = productAddress;
        this.productDescription = productDescription;
        this.availableFromDate = availableFromDate;
        this.availableToDate = availableToDate;
        this.uploadDate = new Date(); // Set to current date
        this.productImagesUrl = productImagesUrl != null ? productImagesUrl : new ArrayList<>();
    }

    // Getters and Setters
    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public String getServiceType() {
        return ServiceType;
    }

    public void setServiceType(String serviceType) {
        ServiceType = serviceType;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public double getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(double productPrice) {
        this.productPrice = productPrice;
    }

    public int getProductQuantity() {
        return productQuantity;
    }

    public void setProductQuantity(int productQuantity) {
        this.productQuantity = productQuantity;
    }

    public String getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(String productCategory) {
        this.productCategory = productCategory;
    }

    public GeoPoint getProductLocation() {
        return productLocation;
    }

    public void setProductLocation(GeoPoint productLocation) {
        this.productLocation = productLocation;
    }

    public String getProductAddress() {
        return productAddress;
    }

    public void setProductAddress(String productAddress) {
        this.productAddress = productAddress;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public String getProductUserId() {
        return productUserId;
    }

    public void setProductUserId(String productUserId) {
        this.productUserId = productUserId;
    }

    public Date getAvailableFromDate() {
        return availableFromDate;
    }

    public void setAvailableFromDate(Date availableFromDate) {
        this.availableFromDate = availableFromDate;
    }

    public Date getAvailableToDate() {
        return availableToDate;
    }

    public void setAvailableToDate(Date availableToDate) {
        this.availableToDate = availableToDate;
    }

    public Date getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(Date uploadDate) {
        this.uploadDate = uploadDate;
    }

    public String getSearchableString() {
        return searchableString;
    }

    public void setSearchableString(String searchableString) {
        this.searchableString = searchableString;
    }

    public List<String> getProductImagesUrl() {
        return productImagesUrl;
    }

    public void setProductImagesUrl(List<String> productImagesUrl) {
        this.productImagesUrl = productImagesUrl;
    }

    // Optional: toString() method for easier debugging
    @Override
    public String toString() {
        return "ProductModel{" +
                "productId='" + productId + '\'' +
                ", productName='" + productName + '\'' +
                ", productPrice=" + productPrice +
                ", productQuantity=" + productQuantity +
                ", productCategory='" + productCategory + '\'' +
                ", productLocation=" + productLocation +
                ", productAddress='" + productAddress + '\'' +
                ", productDescription='" + productDescription + '\'' +
                ", productUserId='" + productUserId + '\'' +
                ", availableFromDate=" + availableFromDate +
                ", availableToDate=" + availableToDate +
                ", uploadDate=" + uploadDate +
                ", searchableString='" + searchableString + '\'' +
                ", productImagesUrl=" + productImagesUrl +
                '}';
    }
}
