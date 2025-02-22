package apcoders.in.krushitech.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.auth.FirebaseAuth;

import apcoders.in.krushitech.LoginActivity;
import apcoders.in.krushitech.ManageEquipmentsActivity;
import apcoders.in.krushitech.R;
import apcoders.in.krushitech.SettingsActivity;
import apcoders.in.krushitech.TermsConditionsActivity;
import apcoders.in.krushitech.Wishlist_Products_Activity;
import apcoders.in.krushitech.Withdrawal_Activity;
import apcoders.in.krushitech.interfaces.FetchUserDataCallback;
import apcoders.in.krushitech.interfaces.FetchVendorDataCallback;
import apcoders.in.krushitech.interfaces.FragmentChangeListener;
import apcoders.in.krushitech.models.User_Farmer_Model;
import apcoders.in.krushitech.models.VendorsModel;
import apcoders.in.krushitech.utils.FetchUserData;
import apcoders.in.krushitech.utils.WalletManagement;

public class ProfileFragment extends Fragment {

    private Button getHelpBtn, withdrawBtn;
    private TextView profileUserName, profileEmail, profilePhone, userType, walletBalance;
    private CardView shopCardView;
    private LinearLayout profileLogout, totalearning_layout, profileSettings, terms_and_conditionsLayout, profileUpdateLayout, myOrdersLayout, wishlistedProductsLayout, communityLayout, rateAppLayout, transactionsLayout, manageEquipmentsLayout;
    private ImageView userAvatar;
    private LinearLayout realContentView;
    private ShimmerFrameLayout shimmerLayout;
    String userTypeValue;
    private FirebaseAuth firebaseAuth;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize views
        initViews(view);

        firebaseAuth = FirebaseAuth.getInstance();
        shimmerLayout.startShimmer(); // Start shimmer effect
        shimmerLayout.setVisibility(View.VISIBLE);
        realContentView.setVisibility(View.GONE);

        // Setup UI actions
        setupUIActions();

        // Fetch user data
        fetchUserData();

        return view;
    }

    private void initViews(View view) {
        shimmerLayout = view.findViewById(R.id.shimmerLayout);
        realContentView = view.findViewById(R.id.RealView);
        walletBalance = view.findViewById(R.id.my_wallet_balance);
        getHelpBtn = view.findViewById(R.id.getHelpBtn);
        terms_and_conditionsLayout = view.findViewById(R.id.terms_and_conditionsLayout);
        totalearning_layout = view.findViewById(R.id.totalearning_layout);
        profileSettings = view.findViewById(R.id.settings);
        shopCardView = view.findViewById(R.id.shop_cardview);
        communityLayout = view.findViewById(R.id.community);
        withdrawBtn = view.findViewById(R.id.withdrawBtn);
        wishlistedProductsLayout = view.findViewById(R.id.wishlisted_products_layout);
        myOrdersLayout = view.findViewById(R.id.my_orders_layout);
        rateAppLayout = view.findViewById(R.id.ratetheapp);

        profileEmail = view.findViewById(R.id.profile_user_email_text);
        profileUserName = view.findViewById(R.id.profile_user_name_text);
        profilePhone = view.findViewById(R.id.profile_user_phone_text);
        profileLogout = view.findViewById(R.id.profile_logout);
        userAvatar = view.findViewById(R.id.userAccountImage);
        userType = view.findViewById(R.id.userType);
        manageEquipmentsLayout = view.findViewById(R.id.manage_equipments_layout);
        profileUpdateLayout = view.findViewById(R.id.profile_update_layout);
        transactionsLayout = view.findViewById(R.id.transactions_layout);
    }

    private void setupUIActions() {
        totalearning_layout.setOnClickListener(v -> WithdrawBalance());
        profileUpdateLayout.setOnClickListener(v -> updateProfile());
        withdrawBtn.setOnClickListener(v -> WithdrawBalance());
        getHelpBtn.setOnClickListener(v -> openHelpLink());
        communityLayout.setOnClickListener(v -> openCommunityLink());
        transactionsLayout.setOnClickListener(v -> openTransactionsFragment());
        profileSettings.setOnClickListener(v -> openSettingsActivity());
        wishlistedProductsLayout.setOnClickListener(v -> openWishlistActivity());
        myOrdersLayout.setOnClickListener(v -> openOrdersFragment());
        profileLogout.setOnClickListener(v -> logout());
        terms_and_conditionsLayout.setOnClickListener(v -> startActivity(new Intent(requireActivity(), TermsConditionsActivity.class)));
        manageEquipmentsLayout.setOnClickListener(v -> openManageEquipmentsActivity());
        rateAppLayout.setOnClickListener(v -> RateApp());
    }

    private void WithdrawBalance() {
        startActivity(new Intent(requireActivity(), Withdrawal_Activity.class));
    }

    private void RateApp() {
        Intent intent = new Intent(Intent.ACTION_VIEW, android.net.Uri.parse("https://indusapp.store/qr0jezvq"));
        startActivity(intent);
    }

    private void fetchUserData() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("UserData", Context.MODE_PRIVATE);
        userTypeValue = sharedPreferences.getString("user_type", "");

        WalletManagement.getBalance(firebaseAuth.getCurrentUser().getUid(), balance -> walletBalance.setText("₹ " + balance));

        if ("farmer".equals(userTypeValue)) {
            fetchFarmerData();
        } else if ("vendor".equals(userTypeValue)) {
//            shopCardView.setVisibility(View.VISIBLE);
            fetchVendorData();
        } else {
            redirectToLogin();
        }
    }

    private void fetchFarmerData() {
        FetchUserData.getUserData(new FetchUserDataCallback() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onCallback(User_Farmer_Model userData) {
                if (userData != null) {
                    updateUI(userData.getFull_name(), userData.getPhone_number(), userData.getEmail(), "Farmer", userData.getUser_avatar_url());
                } else {
                    redirectToLogin();
                }
            }
        });
    }

    private void fetchVendorData() {
        FetchUserData.getVendorData(new FetchVendorDataCallback() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onCallback(VendorsModel vendorData) {
                if (vendorData != null) {
                    updateUI(vendorData.getName(), vendorData.getPhoneNumber(), vendorData.getEmail(), "Vendor", vendorData.getUser_avatar_url());
                } else {
                    redirectToLogin();
                }
            }
        });
    }

    private void updateUI(String name, String phoneNumber, String email, String type, String avatarUrl) {
        profileUserName.setText(name);
        profileEmail.setText(email);
        userType.setText(type);
        profilePhone.setText(phoneNumber);

        Glide.with(requireActivity())
                .load(avatarUrl)
                .placeholder(R.drawable.farmer_avatar)
                .error(R.drawable.farmer_avatar)
                .into(userAvatar);

        shimmerLayout.hideShimmer(); // Stop shimmer effect
        shimmerLayout.setVisibility(View.GONE);
        realContentView.setVisibility(View.VISIBLE);
    }

    private void redirectToLogin() {
        firebaseAuth.signOut();
        startActivity(new Intent(requireActivity(), LoginActivity.class));
        requireActivity().finish();
    }

    private void logout() {
        new AlertDialog.Builder(requireContext())
                .setMessage(getResources().getString(R.string.are_you_sure_logout))
                .setPositiveButton(getResources().getString(R.string.yes), (dialog, which) -> {
                    firebaseAuth.signOut();
                    startActivity(new Intent(requireActivity(), LoginActivity.class));
                    requireActivity().finish();
                })
                .setNegativeButton(getResources().getString(R.string.no), (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    private void openHelpLink() {
        Intent intent = new Intent(Intent.ACTION_VIEW, android.net.Uri.parse("https://chat.whatsapp.com/HPhugoGZ4Oi8N8vACMS1yv"));
        startActivity(intent);
    }

    private void openCommunityLink() {
        Intent intent = new Intent(Intent.ACTION_VIEW, android.net.Uri.parse("https://chat.whatsapp.com/GDqZ7UemZIoJ1eJlzwTF6B"));
        startActivity(intent);
    }

    private void openSettingsActivity() {
        startActivity(new Intent(requireActivity(), SettingsActivity.class));
    }

    private void openWishlistActivity() {
        startActivity(new Intent(requireActivity(), Wishlist_Products_Activity.class));
    }

    private void openOrdersFragment() {
        Fragment fragment = new MyOrdersFragment();
        if (getActivity() instanceof FragmentChangeListener) {
            ((FragmentChangeListener) getActivity()).changeFragment(fragment, R.id.my_orders);
        }

        FragmentTransaction transaction = requireFragmentManager().beginTransaction();
        transaction.replace(R.id.framelayout, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void openTransactionsFragment() {
        Fragment fragment = new TransactionsFragment();
        FragmentTransaction transaction = requireFragmentManager().beginTransaction();
        transaction.replace(R.id.framelayout, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void openManageEquipmentsActivity() {
        startActivity(new Intent(requireActivity(), ManageEquipmentsActivity.class));
    }

    private void updateProfile() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("UserData", Context.MODE_PRIVATE);
        userTypeValue = sharedPreferences.getString("user_type", "");
        FragmentTransaction transaction = requireFragmentManager().beginTransaction();
        if (userTypeValue.equals("farmer")) {
            transaction.replace(R.id.framelayout, new Farmer_Profile_Update_Fragment());
        } else {
            transaction.replace(R.id.framelayout, new VendorProfileUpdateFragment());
        }


        transaction.addToBackStack(null);
        transaction.commit();
    }
}
