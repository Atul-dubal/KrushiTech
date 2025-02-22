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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

import apcoders.in.krushitech.R;
import apcoders.in.krushitech.adapters.ProductAdapter;
import apcoders.in.krushitech.interfaces.FragmentChangeListener;
import apcoders.in.krushitech.models.ProductModel;
import apcoders.in.krushitech.utils.FetchAllProducts;
import apcoders.in.krushitech.utils.UserLocation;
import es.dmoral.toasty.Toasty;

public class SearchFragment extends Fragment {

    ShimmerFrameLayout search_fragment_shimmer_layout, search_fragment_with_query_shimmer_layout;
    private TextInputEditText search_query;
    private ImageView search_btn;
    private String searchQuery = "";
    private LinearLayout product_categories, filter_container;
    private RecyclerView searchResultRecyclerview;
    private int redirect = 0;
    LinearLayout equipments_layout, fertilizers_layout, seeds_layout, filterLayout, home_equipment_listing;
    SeekBar location_distance_radius_seekbar, price_seekbar;
    ArrayList<String> filter_options = new ArrayList<>();
    CardView filter_equipments_layout, filter_fertilizers_layout, filter_seeds_layout;
    View bottomSheetView;
    TextView location_distance_radius_value_textview, price_value_textview, filterResults_textView;
    MaterialButton reset_filter_btn, apply_filter_btn;
    ArrayList<ProductModel> searchResultProductData = new ArrayList<>();
    BottomSheetDialog bottomSheetDialog;
    boolean isFilterApply = false;
    boolean isPermissionGranted = false;

    public SearchFragment() {
        // Required empty public constructor
    }

    public SearchFragment(String searchQuery) {
        // Constructor with query passed, used for redirection
        this.searchQuery = searchQuery;
        this.redirect = 1;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        // Initialize views
        filterLayout = view.findViewById(R.id.filterLayout);
        filter_container = view.findViewById(R.id.filter_container);
        equipments_layout = view.findViewById(R.id.equipments_layout);
        seeds_layout = view.findViewById(R.id.seeds_layout);
        fertilizers_layout = view.findViewById(R.id.fertilizers_layout);
        search_query = view.findViewById(R.id.search_query);
        search_btn = view.findViewById(R.id.search_btn);
        home_equipment_listing = view.findViewById(R.id.home_equipment_listing);
        search_fragment_shimmer_layout = view.findViewById(R.id.search_fragment_shimmer_layout);
        search_fragment_shimmer_layout.startLayoutAnimation();
        search_fragment_with_query_shimmer_layout = view.findViewById(R.id.search_fragment_with_query_shimmer_layout);


        searchResultRecyclerview = view.findViewById(R.id.searchResultRecyclerview);
        product_categories = view.findViewById(R.id.product_categories);

        bottomSheetView = getLayoutInflater().inflate(R.layout.products_filter_layout, null, false);
        filterResults_textView = view.findViewById(R.id.filterResults_textView);
        filter_equipments_layout = bottomSheetView.findViewById(R.id.equipments_card);
        filter_fertilizers_layout = bottomSheetView.findViewById(R.id.fertilizers_card);
        filter_seeds_layout = bottomSheetView.findViewById(R.id.seeds_card);

        location_distance_radius_value_textview = bottomSheetView.findViewById(R.id.location_distance_radius_value_textview);
        price_value_textview = bottomSheetView.findViewById(R.id.price_value_textview);
        location_distance_radius_seekbar = bottomSheetView.findViewById(R.id.location_distance_radius_seekbar);
        price_seekbar = bottomSheetView.findViewById(R.id.price_seekbar);

        reset_filter_btn = bottomSheetView.findViewById(R.id.reset_filter_btn);
        apply_filter_btn = bottomSheetView.findViewById(R.id.apply_filter_btn);

        equipments_recycleView_setup(view);
        fertilizers_recycleView_setup(view);
        seeds_recycleView_setup(view);

        price_seekbar.setProgress(2500);
        price_value_textview.setText(price_seekbar.getProgress() + "");
        location_distance_radius_seekbar.setProgress(300);
        location_distance_radius_value_textview.setText(location_distance_radius_seekbar.getProgress() + "");
        GetUserLocation();
        SetFilterOption();
        // Initialize search view based on the query passed (if any)
        setSearchViewAsPerQuery(searchQuery);

        equipments_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchFragment searchFragment = new SearchFragment("equipments");
                // Begin the fragment transaction
                loadFragment(searchFragment, R.id.search);
            }
        });
        fertilizers_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchFragment searchFragment = new SearchFragment("fertilizers");
                // Begin the fragment transaction
                loadFragment(searchFragment, R.id.search);
            }
        });

        seeds_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchFragment searchFragment = new SearchFragment("seeds");
                // Begin the fragment transaction
                loadFragment(searchFragment, R.id.search);
            }
        });

        // Search button click listener
        search_btn.setOnClickListener(v -> {
            searchQuery = search_query.getText().toString();
            setSearchViewAsPerQuery(searchQuery);
        });

        return view;
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
                        if (searchResultProductData != null) {
                            bottomSheetDialog.dismiss();
                            showSearchQueryResult(isFilterApply);
                        }
                    }
                });

                apply_filter_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        isFilterApply = true;
                        Log.d("TAG", "Price : " + price_seekbar.getProgress());
                        Log.d("TAG", "Location : " + location_distance_radius_seekbar.getProgress());
                        search_fragment_with_query_shimmer_layout.setVisibility(View.VISIBLE);
                        search_fragment_with_query_shimmer_layout.startLayoutAnimation();
                        if (searchResultProductData != null) {
                            bottomSheetDialog.dismiss();
                            showSearchQueryResult(isFilterApply);
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

    // Method to handle search based on query
    private void setSearchViewAsPerQuery(String searchQuery) {
        // If redirected with a search query, pre-fill the query text
        if (redirect == 1) {
            search_query.setText(searchQuery);
        }
        // Check if search query is not empty
        if (!searchQuery.isEmpty()) {
            Log.d("Search Query: ", searchQuery);
            // Fetch products based on search query
            search_fragment_with_query_shimmer_layout.setVisibility(View.VISIBLE);
            search_fragment_with_query_shimmer_layout.startLayoutAnimation();
            fetchProductsBySearchQuery(searchQuery);
            // Hide product categories (if search query exists)
            product_categories.setVisibility(View.GONE);
            search_fragment_shimmer_layout.setVisibility(View.GONE);
            ViewGroup.LayoutParams params = home_equipment_listing.getLayoutParams();
            params.height = 0;
            params.width = 0;
            home_equipment_listing.setLayoutParams(params);
            Log.d("TAG", "setSearchViewAsPerQuery: " + home_equipment_listing.isShown());
            filterLayout.setVisibility(View.VISIBLE);
        } else {
            // If query is empty, set the default search view (category suggestions)
            setDefaultSearchView();
            resetFilters();
        }
    }

    // Method to fetch products based on search query
    private void fetchProductsBySearchQuery(String query) {
        FetchAllProducts.FetchBySearchQuery(query, new FetchAllProducts.FetchProductDataCallback() {
            @Override
            public void onCallback(ArrayList<ProductModel> productDataList) {
                // Set the visibility of search results
                searchResultRecyclerview.setVisibility(View.VISIBLE);
                // Setup RecyclerView with GridLayoutManager and adapter
                searchResultProductData = productDataList;
                showSearchQueryResult(isFilterApply);
            }
        });
    }

    @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
    private void showSearchQueryResult(boolean isFilterApply) {
        searchResultRecyclerview.setLayoutManager(new GridLayoutManager(getContext(), 2));
        if (!isFilterApply) {
            filterResults_textView.setText(searchResultProductData.size() + " " + getString(R.string.results_found));
            ProductAdapter adapter = new ProductAdapter(getContext(), searchResultProductData, R.layout.product_layout);
            adapter.notifyDataSetChanged();
            searchResultRecyclerview.setAdapter(adapter);
            search_fragment_with_query_shimmer_layout.stopShimmer();
            search_fragment_with_query_shimmer_layout.setVisibility(View.GONE);
        } else {
            ArrayList<ProductModel> filteredList = new ArrayList<>();
            if (isPermissionGranted) {
                findNearProducts();
            } else {
                GetUserLocation();
            }
            for (ProductModel product : searchResultProductData) {
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
                ProductAdapter adapter = new ProductAdapter(getContext(), filteredList, R.layout.product_layout);
                adapter.notifyDataSetChanged();
                searchResultRecyclerview.setAdapter(adapter);
            } else {
                Log.d("TAG", "showSearchQueryResult: " + filteredList.size() + " Results Found");
                filterResults_textView.setText(filteredList.size() + " Results Found");
                ProductAdapter adapter = new ProductAdapter(getContext(), filteredList, R.layout.product_layout);
                adapter.notifyDataSetChanged();
                searchResultRecyclerview.setAdapter(adapter);
                Toasty.info(requireContext(), "No Product Found", Toasty.LENGTH_SHORT).show();
            }
            Log.d("TAG", "onCallback: " + filteredList.size());
            search_fragment_with_query_shimmer_layout.stopShimmer();
            search_fragment_with_query_shimmer_layout.setVisibility(View.GONE);
        }
    }

    private void findNearProducts() {
        GetUserLocation();
        UserLocation.GetCurrentLocation(requireActivity(), new UserLocation.LocationCallback() {
            @Override
            public void onLocationReceived(double latitude, double longitude) {
                UserLocation.findNearbyEquipment(latitude, longitude, location_distance_radius_seekbar.getProgress(), new FetchAllProducts.FetchProductDataCallback() {
                    @Override
                    public void onCallback(ArrayList<ProductModel> ProductDataList) {
                        searchResultProductData = ProductDataList;
                    }
                });
            }

            @Override
            public void onLocationError(String errorMessage) {

            }
        });
    }

    // Method to handle default view for empty search
    private void setDefaultSearchView() {
        product_categories.setVisibility(View.VISIBLE);
        home_equipment_listing.setVisibility(View.VISIBLE);
        searchResultRecyclerview.setVisibility(View.GONE);
        search_fragment_with_query_shimmer_layout.stopShimmer();
        search_fragment_with_query_shimmer_layout.setVisibility(View.GONE);
        filterLayout.setVisibility(View.GONE);
    }

    private void loadFragment(Fragment fragment, int navItemId) {
        if (getActivity() instanceof FragmentChangeListener) {
            ((FragmentChangeListener) getActivity()).changeFragment(fragment, navItemId);
        }
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        // Replace the current fragment with the new fragment and add the transaction to the back stack
        transaction.replace(R.id.framelayout, fragment); // 'fragment_container' should be the ID of your container layout
        transaction.addToBackStack(null); // Optional: adds the transaction to the back stack so the user can navigate back
        transaction.commit(); // Commit the transaction
    }

    private void seeds_recycleView_setup(View view) {
        FetchAllProducts.FetchFilterProductsData("seeds", new FetchAllProducts.FetchProductDataCallback() {
            @Override
            public void onCallback(ArrayList<ProductModel> ProductDataList) {

                RecyclerView seeds_recycleView = view.findViewById(R.id.seeds_recycleView);
                seeds_recycleView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
                ProductAdapter productAdapter = new ProductAdapter(getActivity(), ProductDataList, R.layout.product_home_screen_layout);
                seeds_recycleView.setAdapter(productAdapter);
                search_fragment_shimmer_layout.stopShimmer();
                search_fragment_shimmer_layout.setVisibility(View.GONE);
                if (home_equipment_listing.getVisibility() == View.GONE) {
                    home_equipment_listing.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void fertilizers_recycleView_setup(View view) {
        FetchAllProducts.FetchFilterProductsData("fertilizers", new FetchAllProducts.FetchProductDataCallback() {
            @Override
            public void onCallback(ArrayList<ProductModel> ProductDataList) {
                RecyclerView fertilizers_recycleView = view.findViewById(R.id.fertilizers_recycleView);
                fertilizers_recycleView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
                ProductAdapter productAdapter = new ProductAdapter(getActivity(), ProductDataList, R.layout.product_home_screen_layout);
                fertilizers_recycleView.setAdapter(productAdapter);
                search_fragment_shimmer_layout.stopShimmer();
                search_fragment_shimmer_layout.setVisibility(View.GONE);
                if (home_equipment_listing.getVisibility() == View.GONE) {
                    home_equipment_listing.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void equipments_recycleView_setup(View view) {
        FetchAllProducts.FetchFilterProductsData("equipments", new FetchAllProducts.FetchProductDataCallback() {
            @Override
            public void onCallback(ArrayList<ProductModel> ProductDataList) {
                RecyclerView equipments_recycleView = view.findViewById(R.id.equipments_recycleView);
                equipments_recycleView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
                ProductAdapter productAdapter = new ProductAdapter(getActivity(), ProductDataList, R.layout.product_home_screen_layout);
                equipments_recycleView.setAdapter(productAdapter);
                search_fragment_shimmer_layout.stopShimmer();
                search_fragment_shimmer_layout.setVisibility(View.GONE);
                if (home_equipment_listing.getVisibility() == View.GONE) {
                    home_equipment_listing.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void GetUserLocation() {
        Log.d("TAG", "GetUserLocation: ");
        if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Log.d("TAG", "GetUserLocation: Permission not granted");
            showPermissionDeniedDialog();
//            if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),
//                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {
//                showPermissionDeniedDialog();
//            } else {
//                ActivityCompat.requestPermissions(requireActivity(),
//                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 2);
//            }
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
                //FetchNearbyProducts(location_distance_radius_seekbar.getProgress());
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
