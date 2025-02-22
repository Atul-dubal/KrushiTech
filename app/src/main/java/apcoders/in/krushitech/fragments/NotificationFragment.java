package apcoders.in.krushitech.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import apcoders.in.krushitech.R;
import apcoders.in.krushitech.adapters.NotificationAdapter;
import apcoders.in.krushitech.models.NotificationModel;
import apcoders.in.krushitech.utils.Notification;

public class NotificationFragment extends Fragment {

    RecyclerView notification_recyclerview;
    NotificationAdapter notificationAdapter;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    Handler handler;
    Runnable refreshRunnable;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = new Handler();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notification, container, false);
        notification_recyclerview = view.findViewById(R.id.notification_recyclerview);
        notification_recyclerview.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        // Initialize and set the adapter with an empty list initially
        notificationAdapter = new NotificationAdapter(getContext(), firebaseAuth.getCurrentUser().getUid(), new ArrayList<>());
        notification_recyclerview.setAdapter(notificationAdapter);

        // Fetch and update notifications
        fetchAndDisplayNotifications();

        // Set up periodic refresh
        setupPeriodicRefresh();

        return view;
    }

    private void fetchAndDisplayNotifications() {
        // Fetch notifications and update the adapter
        Notification.FetchAllNotifications(NotificationDataList -> {
            List<NotificationModel> NotificationDataListNew = new ArrayList<>();
            for (NotificationModel notification : NotificationDataList) {
                if (notification.getProductSenderId().equals(firebaseAuth.getCurrentUser().getUid()) || notification.getProductReceiverId().equals(firebaseAuth.getCurrentUser().getUid())) {
                    NotificationDataListNew.add(notification);
                }
            }
            if (notificationAdapter != null) {
                notificationAdapter.updateList(NotificationDataListNew); // Update the adapter's data
            }
        });
    }

    private void setupPeriodicRefresh() {
        refreshRunnable = new Runnable() {
            @Override
            public void run() {
                fetchAndDisplayNotifications(); // Refresh data
                handler.postDelayed(this, 10000); // Refresh every 5 seconds
            }
        };
        handler.post(refreshRunnable); // Start the periodic refresh
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        // Remove callbacks to stop refreshing when the fragment is destroyed
        if (handler != null && refreshRunnable != null) {
            handler.removeCallbacks(refreshRunnable);
        }
    }

}
