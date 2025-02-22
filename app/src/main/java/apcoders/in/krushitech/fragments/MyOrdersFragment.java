package apcoders.in.krushitech.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.List;

import apcoders.in.krushitech.R;
import apcoders.in.krushitech.adapters.OrderAdapter;
import apcoders.in.krushitech.models.OrderModel;
import apcoders.in.krushitech.utils.OrderManagement;

public class MyOrdersFragment extends Fragment {

    LinearLayout my_orders_layout, no_order_layout;
    ShimmerFrameLayout MyOrdersshimmerFrameLayout;
    RecyclerView my_orders_recycler_view;

    public MyOrdersFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_orders, container, false);
        my_orders_layout = view.findViewById(R.id.orders_layout);
        no_order_layout = view.findViewById(R.id.no_order_layout);

        MyOrdersshimmerFrameLayout = view.findViewById(R.id.shimmer_view_container);
        MyOrdersshimmerFrameLayout.startShimmer();

        my_orders_recycler_view = view.findViewById(R.id.my_orders_recycler_view);

        OrderManagement.FetchAllOrderData(new OrderManagement.FetchAllOrderDataCallback() {
            @Override
            public void onCallback(List<OrderModel> OrderDataList) {
                if (!OrderDataList.isEmpty()) {
                    my_orders_recycler_view.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                    my_orders_recycler_view.setAdapter(new OrderAdapter(getContext(), OrderDataList));
                    MyOrdersshimmerFrameLayout.hideShimmer();
                    MyOrdersshimmerFrameLayout.setVisibility(View.GONE);
                    my_orders_layout.setVisibility(View.VISIBLE);
                    no_order_layout.setVisibility(View.GONE);

                    // Adjust smooth scroll behavior with reduced frame skip rate
                    smoothScrollRecyclerView(my_orders_recycler_view);
                } else {
                    MyOrdersshimmerFrameLayout.hideShimmer();
                    MyOrdersshimmerFrameLayout.setVisibility(View.GONE);
                    my_orders_layout.setVisibility(View.GONE);
                    no_order_layout.setVisibility(View.VISIBLE);
                }
            }
        });
        return view;
    }


    // This function ensures smooth scrolling with reduced frame skips
    private void smoothScrollRecyclerView(RecyclerView recyclerView) {
        final Handler handler = new Handler();
        final LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        final int scrollSpeed = 100;  // Reduced scroll speed for smoother transition
        final int scrollSkip = 10;    // Reduced scroll skip rate for smoother scrolling

        final Runnable runnable = new Runnable() {
            int currentPosition = layoutManager.findFirstCompletelyVisibleItemPosition();
            boolean scrollFlag = true;

            @Override
            public void run() {
                int totalItems = layoutManager.getItemCount();
                if (currentPosition < totalItems) {
                    if (currentPosition == totalItems - 1) {
                        scrollFlag = false;
                    } else if (currentPosition <= 1) {
                        scrollFlag = true;
                    }

                    if (!scrollFlag) {
                        recyclerView.scrollToPosition(0);
                    }
                    currentPosition = layoutManager.findFirstCompletelyVisibleItemPosition();
                    recyclerView.smoothScrollBy(scrollSkip, 0);
                    handler.postDelayed(this, scrollSpeed);
                }
            }
        };
        handler.postDelayed(runnable, scrollSpeed); // Start the smooth scroll with reduced speed
    }

}
