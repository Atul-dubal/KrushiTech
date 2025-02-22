package apcoders.in.krushitech.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import apcoders.in.krushitech.models.ProductModel;
import es.dmoral.toasty.Toasty;

public class UserLocation {
    public interface LocationCallback {
        void onLocationReceived(double latitude, double longitude);

        void onLocationError(String errorMessage);
    }

    public UserLocation() {

    }

    public static void GetCurrentLocation(Context context, LocationCallback callback) {
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener((Activity) context, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                double latitude = location.getLatitude();
                                double longitude = location.getLongitude();
                                callback.onLocationReceived(latitude, longitude);
                                Log.d("Current Location : ", "Latitude: " + latitude + ", Longitude: " + longitude);
//                                Toast.makeText(context,
//                                        "Latitude: " + latitude + ", Longitude: " + longitude,
//                                        Toast.LENGTH_LONG).show();
                            } else {
                                String errorMessage = "Location not found. Make sure location is enabled on the device.";
                                callback.onLocationError(errorMessage);
                                Toasty.info(context,
                                        "Location not found. Make sure location is enabled on the device.",
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }else{
            callback.onLocationError("Location permission not granted");
        }

    }

    public static void searchEquipment(String type, String name, String description) {
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        CollectionReference ProductsCollectionreference = firebaseFirestore.collection("Products");
        ProductsCollectionreference.whereEqualTo("type", type)
                .whereGreaterThanOrEqualTo("name", name)
                .whereGreaterThanOrEqualTo("description", description)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        // Display or process each document (equipment)
                    }
                })
                .addOnFailureListener(e -> {
                    Log.w("Search", "Error searching equipment", e);
                });
    }


    public static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double earthRadius = 6371; // in kilometers
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return earthRadius * c;
    }


    public static void findNearbyEquipment(double userLat, double userLon, double maxDistance, FetchAllProducts.FetchProductDataCallback fetchProductDataCallback) {
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

        CollectionReference ProductsCollectionreference = firebaseFirestore.collection("Products");
        ProductsCollectionreference.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        ArrayList<ProductModel> ProductDataList = new ArrayList<>();
                        for (DocumentSnapshot document : queryDocumentSnapshots) {
                            GeoPoint location = document.getGeoPoint("productLocation");
                            Log.d("TAG", "findNearbyEquipment: " + document.get("productName") + "  " + location.getLatitude() + "  " + location.getLongitude());
                            if (location != null) {
                                double distance = calculateDistance(userLat, userLon, location.getLatitude(), location.getLongitude());
                                Log.d("onSuccess: Distance",  document.get("productName").toString()+ distance + "");
                                if (distance <= maxDistance) {
                                    ProductDataList.add(document.toObject(ProductModel.class));
                                    // Equipment is within the radius
                                }
                            }
                        }
                        fetchProductDataCallback.onCallback(ProductDataList);
                    }
                });
    }

    public void searchWithAllFilters(String type, boolean availability, double minPrice, double maxPrice,
                                     double userLat, double userLon, double radiusKm) {
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        CollectionReference ProductsCollectionreference = firebaseFirestore.collection("Products");
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Query query = ProductsCollectionreference.whereEqualTo("type", type)
                .whereEqualTo("available", availability)
                .whereGreaterThanOrEqualTo("price", minPrice)
                .whereLessThanOrEqualTo("price", maxPrice);

        query.get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (!queryDocumentSnapshots.isEmpty()) {
                for (DocumentSnapshot document : queryDocumentSnapshots) {
//                    Equipment equipment = document.toObject(Equipment.class);
//                    if (equipment != null && isWithinRadius(userLat, userLon, equipment.getLatitude(), equipment.getLongitude(), radiusKm)) {
//                        // This equipment matches all filters and is within the radius
//                        // Process the equipment data
//                    }
                }
            } else {
                Log.d("searchWithAllFilters: ", "No equipment found with the given filters");
            }
        }).addOnFailureListener(e -> Log.d("searchWithAllFilters: ", "Failed to search"));
    }

}

