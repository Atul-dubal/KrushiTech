package apcoders.in.krushitech.fragments;

import android.os.Bundle;
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
import apcoders.in.krushitech.adapters.TransactionAdapter;
import apcoders.in.krushitech.models.TransactionModel;
import apcoders.in.krushitech.utils.TransactionsManagement;

public class TransactionsFragment extends Fragment {

    ShimmerFrameLayout shimmer_view_container;
    RecyclerView transactions_recycler_view;
    LinearLayout transactions_layout;

    public TransactionsFragment() {
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
        View view = inflater.inflate(R.layout.fragment_transactions, container, false);
        transactions_recycler_view = view.findViewById(R.id.transactions_recycler_view);
        transactions_layout = view.findViewById(R.id.transactions_layout);
        shimmer_view_container = view.findViewById(R.id.shimmer_view_container);
        shimmer_view_container.startShimmer();

        TransactionsManagement.FetchAllTransactions(new TransactionsManagement.FetchAllTransactionsCallback() {
            @Override
            public void onCallback(List<TransactionModel> TransactionsDataList) {
                transactions_recycler_view.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                if (TransactionsDataList != null) {
                    if (!TransactionsDataList.isEmpty()) {
                        shimmer_view_container.hideShimmer();
                        shimmer_view_container.setVisibility(View.GONE);
                        transactions_layout.setVisibility(View.VISIBLE);
                        transactions_recycler_view.setAdapter(new TransactionAdapter(getContext(), TransactionsDataList));
                    } else {
                        shimmer_view_container.hideShimmer();
                        shimmer_view_container.setVisibility(View.GONE);
                    }
                } else {
                    shimmer_view_container.hideShimmer();
                    shimmer_view_container.setVisibility(View.GONE);
                }
            }
        });
        return view;
    }
}