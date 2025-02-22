package apcoders.in.krushitech.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

import apcoders.in.krushitech.R;
import apcoders.in.krushitech.adapters.ProductAdapter;
import apcoders.in.krushitech.models.ProductModel;
import apcoders.in.krushitech.utils.FetchAllProducts;
import apcoders.in.krushitech.utils.UserLocation;
import es.dmoral.toasty.Toasty;

public class NearByProductsFragment extends Fragment {
    // Views
    RecyclerView nearbyproducts_RecyclerView;
    ShimmerFrameLayout near_by_products_shimmer_layout;
    RelativeLayout permission_not_grant;
    Button give_permission_btn;

    LinearLayout near_by_products_realView, filter_container, filterLayout;
    SeekBar location_distance_radius_seekbar, price_seekbar;
    ArrayList<String> filter_options = new ArrayList<>();
    CardView filter_equipments_layout, filter_fertilizers_layout, filter_seeds_layout;
    View bottomSheetView;
    TextView location_distance_radius_value_textview, price_value_textview, filterResults_textView;
    MaterialButton reset_filter_btn, apply_filter_btn;
    ArrayList<ProductModel> NearResultProductData = new ArrayList<>();
    BottomSheetDialog bottomSheetDialog;
    boolean isFilterApply = false;

    boolean isPermissionGranted = false;

    public NearByProductsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("TAG", "onCreate: ");
        GetUserLocation(); // Request location permission on creation
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_near_by_products, container, false);

        // Initialize views
        nearbyproducts_RecyclerView = view.findViewById(R.id.nearbyproducts_RecyclerView);

        permission_not_grant = view.findViewById(R.id.permission_not_grant);
        give_permission_btn = view.findViewById(R.id.give_permission_btn);
        near_by_products_shimmer_layout = view.findViewById(R.id.near_by_products_shimmer_layout);
        near_by_products_shimmer_layout.startLayoutAnimation();
        near_by_products_realView = view.findViewById(R.id.near_by_products_realView);

        // Set button click listener to show permission dialog
        give_permission_btn.setOnClickListener(v -> showPermissionDeniedDialog());

        bottomSheetView = getLayoutInflater().inflate(R.layout.products_filter_layout, null, false);
        filterResults_textView = view.findViewById(R.id.filterResults_textView);
        filter_equipments_layout = bottomSheetView.findViewById(R.id.equipments_card);
        filter_fertilizers_layout = bottomSheetView.findViewById(R.id.fertilizers_card);
        filter_seeds_layout = bottomSheetView.findViewById(R.id.seeds_card);
        filterLayout = view.findViewById(R.id.filterLayout);
        filter_container = view.findViewById(R.id.filter_container);
        location_distance_radius_value_textview = bottomSheetView.findViewById(R.id.location_distance_radius_value_textview);
        price_value_textview = bottomSheetView.findViewById(R.id.price_value_textview);
        location_distance_radius_seekbar = bottomSheetView.findViewById(R.id.location_distance_radius_seekbar);
        price_seekbar = bottomSheetView.findViewById(R.id.price_seekbar);

        reset_filter_btn = bottomSheetView.findViewById(R.id.reset_filter_btn);
        apply_filter_btn = bottomSheetView.findViewById(R.id.apply_filter_btn);

        price_seekbar.setProgress(5000);
        price_value_textview.setText(price_seekbar.getProgress() + "");
        location_distance_radius_seekbar.setProgress(50);
        location_distance_radius_value_textview.setText(location_distance_radius_seekbar.getProgress() + "");
        FetchNearbyProducts(location_distance_radius_seekbar.getProgress());
        SetFilterOption();
        return view;
    }

    // Fetch nearby products based on distance
    private void FetchNearbyProducts(int maxDistance) {
        GetUserLocation(); // Ensure user location is fetched before querying products
        Log.d("TAG", "FetchNearbyProducts: ");

        // Get current location
        UserLocation.GetCurrentLocation(getContext(), new UserLocation.LocationCallback() {
            @Override
            public void onLocationReceived(double latitude, double longitude) {
                Log.d("TAG", "onLocationReceived: ");

                // Find nearby products
                UserLocation.findNearbyEquipment(latitude, longitude, maxDistance, new FetchAllProducts.FetchProductDataCallback() {
                    @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
                    @Override
                    public void onCallback(ArrayList<ProductModel> ProductDataList) {
                        NearResultProductData = ProductDataList;

                        if (isFilterApply) {
                            ArrayList<ProductModel> filteredList = new ArrayList<>();
                            for (ProductModel product : NearResultProductData) {
                                Log.d("TAG", "showSearchQueryResult: " + product.getProductPrice());
                                if (filter_options.isEmpty()) {
                                    Log.d("TAG", "showSearchQueryResult: " + product.getProductPrice() + price_seekbar.getProgress());
                                    if (product.getProductPrice() <= price_seekbar.getProgress()) {
                                        filteredList.add(product);
                                        Log.d("TAG", "showSearchQueryResult: " + product.getProductName());
                                    }
                                } else {
                                    Log.d("TAG", "showSearchQueryResult: " + product.getProductPrice() + price_seekbar.getProgress());
                                    if (filter_options.contains(product.getProductCategory()) && product.getProductPrice() <= price_seekbar.getProgress()) {
                                        filteredList.add(product);
                                        Log.d("TAG", "showSearchQueryResult: " + product.getProductName());
                                    }
                                }
                            }
                            if (!filteredList.isEmpty()) {
                                Log.d("TAG", "showSearchQueryResult: " + filteredList.size() + " Results Found");
                                filterResults_textView.setText(filteredList.size() + " Results Found");
                                updateRecyclerView(filteredList);

                            } else {
                                Log.d("TAG", "showSearchQueryResult: " + filteredList.size() + " Results Found");
                                filterResults_textView.setText(filteredList.size() + " Results Found");
                                updateRecyclerView(filteredList);
                                Toasty.info(requireContext(), "No Product Found", Toasty.LENGTH_SHORT).show();
                            }
                            Log.d("TAG", "onCallback: " + filteredList.size());

                        } else {
                            filterResults_textView.setText(NearResultProductData.size() + " Results Found");
                            updateRecyclerView(NearResultProductData);
                        }
                    }
                });

            }

            @Override
            public void onLocationError(String errorMessage) {
                permission_not_grant.setVisibility(View.VISIBLE);
                near_by_products_realView.setVisibility(View.VISIBLE);
                near_by_products_shimmer_layout.hideShimmer();
                near_by_products_shimmer_layout.setVisibility(View.GONE);
                if (errorMessage.equals("Location permission not granted")) {
                    showPermissionDeniedDialog(); // Show permission dialog if location access is denied
                }
                Log.d("TAG", "onLocationError: " + errorMessage);
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void updateRecyclerView(ArrayList<ProductModel> ProductDataList) {
        nearbyproducts_RecyclerView.setVisibility(View.VISIBLE);
        permission_not_grant.setVisibility(View.GONE);
        near_by_products_realView.setVisibility(View.VISIBLE);
        near_by_products_shimmer_layout.hideShimmer();
        near_by_products_shimmer_layout.stopShimmer();
        near_by_products_shimmer_layout.setVisibility(View.GONE);
        nearbyproducts_RecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        ProductAdapter adapter = new ProductAdapter(getActivity(), ProductDataList, R.layout.product_layout);
        adapter.notifyDataSetChanged();
        nearbyproducts_RecyclerView.setAdapter(adapter);
    }

    private void SetFilterOption() {

        filter_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Inflate the custom layout for the bottom sheet
                reset_filter_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        resetFilters();
                        if (NearResultProductData != null) {
                            bottomSheetDialog.dismiss();
//                            showSearchQueryResult(NearResultProductData, isFilterApply);
                        }
                    }
                });

                apply_filter_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        isFilterApply = true;
                        Log.d("TAG", "Price : " + price_seekbar.getProgress());
                        Log.d("TAG", "Location : " + location_distance_radius_seekbar.getProgress());


                        if (NearResultProductData != null) {
                            bottomSheetDialog.dismiss();
                            nearbyproducts_RecyclerView.setVisibility(View.GONE);
                            permission_not_grant.setVisibility(View.GONE);
                            near_by_products_realView.setVisibility(View.VISIBLE);
                            near_by_products_shimmer_layout.setVisibility(View.VISIBLE);
//                        near_by_products_shimmer_layout.startShimmer();
                            near_by_products_shimmer_layout.startLayoutAnimation();
                            FetchNearbyProducts(location_distance_radius_seekbar.getProgress());
                        }
                    }
                });


                filter_equipments_layout.setOnClickListener(new View.OnClickListener() {
                    @SuppressLint("ResourceAsColor")
                    @Override
                    public void onClick(View v) {
                        if (!filter_options.contains("equipments")) {
                            filter_options.add("equipments");
                            filter_equipments_layout.setCardBackgroundColor(getResources().getColor(R.color.background_light_green));
                        } else {
                            filter_options.remove("equipments");
                            filter_equipments_layout.setCardBackgroundColor(getResources().getColor(R.color.white));
                        }
                    }
                });

                filter_fertilizers_layout.setOnClickListener(new View.OnClickListener() {
                    @SuppressLint("ResourceAsColor")
                    @Override
                    public void onClick(View v) {
                        if (!filter_options.contains("fertilizers")) {
                            filter_options.add("fertilizers");
                            filter_fertilizers_layout.setCardBackgroundColor(getResources().getColor(R.color.background_light_green));
                        } else {
                            filter_options.remove("fertilizers");
                            filter_fertilizers_layout.setCardBackgroundColor(getResources().getColor(R.color.white));
                        }
                    }
                });

                filter_seeds_layout.setOnClickListener(new View.OnClickListener() {
                    @SuppressLint("ResourceAsColor")
                    @Override
                    public void onClick(View v) {
                        if (!filter_options.contains("seeds")) {
                            filter_options.add("seeds");
                            filter_seeds_layout.setCardBackgroundColor(getResources().getColor(R.color.background_light_green));
                        } else {
                            filter_options.remove("seeds");
                            filter_seeds_layout.setCardBackgroundColor(getResources().getColor(R.color.white));
                        }
                    }
                });

                // SeekBar change listener for price and location radius
                price_seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        new Handler().removeCallbacksAndMessages(null);
                        new Handler().postDelayed(() -> price_value_textview.setText(String.valueOf(price_seekbar.getProgress())), 300);

                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });
                location_distance_radius_seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        new Handler().removeCallbacksAndMessages(null);
                        new Handler().postDelayed(() -> location_distance_radius_value_textview.setText(String.valueOf(location_distance_radius_seekbar.getProgress())), 300);


                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });

                if (bottomSheetView.getParent() != null) {
                    ((ViewGroup) bottomSheetView.getParent()).removeView(bottomSheetView);
                }
                // Initialize the BottomSheetDialog
                bottomSheetDialog = new BottomSheetDialog(requireContext());
                bottomSheetDialog.setContentView(bottomSheetView);

                bottomSheetDialog.show();
                View bottomSheet = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
                if (bottomSheet != null) {
                    bottomSheet.getLayoutParams().height = 700; // Set height to 300 pixels
                    BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(bottomSheet);
                    behavior.setPeekHeight(700); // Ensure the peek height is also 300 pixels
                    behavior.setState(BottomSheetBehavior.STATE_COLLAPSED); // Ensure it stays collapsed at 300 pixels
                }
            }
        });
    }

    private void resetFilters() {
        isFilterApply = false;
        filter_equipments_layout.setCardBackgroundColor(getResources().getColor(R.color.white));
        filter_fertilizers_layout.setCardBackgroundColor(getResources().getColor(R.color.white));
        filter_equipments_layout.setCardBackgroundColor(getResources().getColor(R.color.white));
        price_seekbar.setProgress(500);
        location_distance_radius_seekbar.setProgress(50);
        filter_options.clear();
    }


    // Request user location permission if not granted
    private void GetUserLocation() {
        Log.d("TAG", "GetUserLocation: ");
        if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Log.d("TAG", "GetUserLocation: Permission not granted");
            if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                showPermissionDeniedDialog();
            } else {
                ActivityCompat.requestPermissions(requireActivity(),
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 2);
            }
        } else {
            isPermissionGranted = true;
        }
    }

    // Handle the result of the permission request
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 2) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                isPermissionGranted = true;

                FetchNearbyProducts(location_distance_radius_seekbar.getProgress());
            } else {
                if (!ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                    showPermissionDeniedDialog(); // User denied permanently
                } else {
                    Toasty.error(requireContext(), "Location permission required for nearby products.").show();
                }
            }
        }
    }

    // Show dialog when permission is denied permanently
    private void showPermissionDeniedDialog() {
        Log.d("TAG", "showPermissionDeniedDialog: ");
        new AlertDialog.Builder(requireContext())
                .setTitle("Enable Location Permission")
                .setCancelable(true)
                .setMessage("Location access is required to suggest nearby farm equipment. Please enable it in settings.")
                .setPositiveButton("Go to Settings", (dialog, which) -> {
                    Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.parse("package:" + requireActivity().getPackageName()));
                    startActivity(intent);
                })
                .create()
                .show();
    }
}
