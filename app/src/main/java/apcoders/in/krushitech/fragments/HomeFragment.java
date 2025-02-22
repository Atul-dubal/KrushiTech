package apcoders.in.krushitech.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import apcoders.in.krushitech.R;
import apcoders.in.krushitech.Upload_Product_Activity;
import apcoders.in.krushitech.adapters.ViewpagerImageSliderAdapter;
import apcoders.in.krushitech.interfaces.FragmentChangeListener;

public class HomeFragment extends Fragment {

    Context context;
    ViewPager2 viewPager;
    ShimmerFrameLayout shimmerFrameLayout;
    RelativeLayout RealView;
    LinearLayout My_Orders, add_product_layout, see_more_layout, equipments_layout, fertilizers_layout, seeds_layout;

    public HomeFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        banner_recyclerview_setup(view);
        shimmerFrameLayout = view.findViewById(R.id.shimmerLayout);
        My_Orders = view.findViewById(R.id.my_orders_layout);
        add_product_layout = view.findViewById(R.id.add_product_layout);
        see_more_layout = view.findViewById(R.id.see_more_layout);
        RealView = view.findViewById(R.id.RealView);
        equipments_layout = view.findViewById(R.id.equipments_layout);
        seeds_layout = view.findViewById(R.id.seeds_layout);
        fertilizers_layout = view.findViewById(R.id.fertilizers_layout);
        shimmerFrameLayout.startLayoutAnimation();

//        equipments_recycleView_setup(view);
//        fertilizers_recycleView_setup(view);
//        seeds_recycleView_setup(view);
        shimmerFrameLayout.hideShimmer();
        shimmerFrameLayout.setVisibility(View.GONE);
        RealView.setVisibility(View.VISIBLE);
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

        My_Orders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyOrdersFragment ordersFragment = new MyOrdersFragment();
                // Begin the fragment transaction
                loadFragment(ordersFragment, R.id.my_orders);
            }
        });
        add_product_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), Upload_Product_Activity.class));
            }
        });

        see_more_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new ProfileFragment(), R.id.profile);
            }
        });

        return view;
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

    int[] imageIds = new int[]{};

//    private void seeds_recycleView_setup(View view) {
//        FetchAllProducts.FetchFilterProductsData("seeds", new FetchAllProducts.FetchProductDataCallback() {
//            @Override
//            public void onCallback(ArrayList<ProductModel> ProductDataList) {
//                shimmerFrameLayout.hideShimmer();
//                shimmerFrameLayout.setVisibility(View.GONE);
//                RealView.setVisibility(View.VISIBLE);
//                RecyclerView seeds_recycleView = view.findViewById(R.id.seeds_recycleView);
//                seeds_recycleView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
//                ProductAdapter productAdapter = new ProductAdapter(getActivity(), ProductDataList, R.layout.product_home_screen_layout);
//                seeds_recycleView.setAdapter(productAdapter);
//            }
//        });
//    }
//
//    private void fertilizers_recycleView_setup(View view) {
//        FetchAllProducts.FetchFilterProductsData("fertilizers", new FetchAllProducts.FetchProductDataCallback() {
//            @Override
//            public void onCallback(ArrayList<ProductModel> ProductDataList) {
//                RecyclerView fertilizers_recycleView = view.findViewById(R.id.fertilizers_recycleView);
//                fertilizers_recycleView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
//                ProductAdapter productAdapter = new ProductAdapter(getActivity(), ProductDataList, R.layout.product_home_screen_layout);
//                fertilizers_recycleView.setAdapter(productAdapter);
//            }
//        });
//    }
//
//    private void equipments_recycleView_setup(View view) {
//        FetchAllProducts.FetchFilterProductsData("equipments", new FetchAllProducts.FetchProductDataCallback() {
//            @Override
//            public void onCallback(ArrayList<ProductModel> ProductDataList) {
//                RecyclerView equipments_recycleView = view.findViewById(R.id.equipments_recycleView);
//                equipments_recycleView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
//                ProductAdapter productAdapter = new ProductAdapter(getActivity(), ProductDataList, R.layout.product_home_screen_layout);
//                equipments_recycleView.setAdapter(productAdapter);
//            }
//        });
//    }

    private void banner_recyclerview_setup(View view) {
        RecyclerView Banner_recycleView = view.findViewById(R.id.Banner_recycleView);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        Banner_recycleView.setLayoutManager(linearLayoutManager);

        // Array of drawable resource IDs
        List<Uri> imageIds = new ArrayList<>();
        imageIds.add(Uri.parse("android.resource://apcoders.in.krushitech/" + R.drawable.banner_1));
        imageIds.add(Uri.parse("android.resource://apcoders.in.krushitech/" + R.drawable.banner_2));
        imageIds.add(Uri.parse("android.resource://apcoders.in.krushitech/" + R.drawable.banner_3));

        ViewpagerImageSliderAdapter adapter = new ViewpagerImageSliderAdapter(requireContext(), imageIds);
        Banner_recycleView.setAdapter(adapter);

        final int scrollSpeed = 300;   // Scroll Speed in Milliseconds (Increased for slower rate)
        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            int x = 10;        // Reduced Pixels To Move/Scroll (for smoother transition)
            boolean flag = true;
            int scrollPosition = linearLayoutManager.findLastCompletelyVisibleItemPosition();
            int arraySize = imageIds.size();  // Gets RecyclerView's Adapter's Array Size

            @Override
            public void run() {
                if (scrollPosition < arraySize) {
                    if (scrollPosition == arraySize - 1) {
                        flag = false;
                    } else if (scrollPosition <= 1) {
                        flag = true;
                    }
                    if (!flag) {
                        try {
                            // Delay in Seconds So User Can Completely Read Till Last String
                            TimeUnit.SECONDS.sleep(1);
                            Banner_recycleView.scrollToPosition(0);  // Jumps Back Scroll To Start Point
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    // Know The Last Visible Item
                    scrollPosition = linearLayoutManager.findLastCompletelyVisibleItemPosition();

                    Banner_recycleView.smoothScrollBy(x, 0);
                    handler.postDelayed(this, scrollSpeed);  // Adjust delay between scroll movements
                }
            }
        };
        handler.postDelayed(runnable, scrollSpeed); // Start the scrolling with the new speed
    }
}
