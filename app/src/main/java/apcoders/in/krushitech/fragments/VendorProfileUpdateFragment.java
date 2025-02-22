package apcoders.in.krushitech.fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

import apcoders.in.krushitech.R;
import apcoders.in.krushitech.interfaces.FetchVendorDataCallback;
import apcoders.in.krushitech.models.VendorsModel;
import apcoders.in.krushitech.utils.FetchUserData;
import es.dmoral.toasty.Toasty;

public class VendorProfileUpdateFragment extends Fragment {

    String[] States = {"Select State", "Andhra Pradesh", "Arunachal Pradesh", "Assam", "Bihar", "Chhattisgarh", "Goa", "Gujarat", "Haryana", "Himachal Pradesh", "Jammu and Kashmir", "Jharkhand", "Karnataka", "Kerala", "Madhya Pradesh", "Maharashtra", "Manipur", "Meghalaya", "Mizoram", "Nagaland", "Odisha", "Punjab", "Rajasthan", "Sikkim", "Tamil Nadu", "Telangana", "Tripura", "Uttarakhand", "Uttar Pradesh", "West Bengal", "Andaman and Nicobar Islands", "Chandigarh", "Dadra and Nagar Haveli", "Daman and Diu", "Delhi", "Lakshadweep", "Puducherry"};

    String selected_spinner_State_Item;
    private ImageView vendorAvatar;
    private TextInputEditText vendorFullName, vendorEmail, vendorShopName, vendorPhoneNumber, vendorLocation, vendorId, vendorUserType;
    private Spinner vendorStateSpinner;
    private Button updateButton;
    private ProgressBar progressBar;

    private Uri imageUri;
    private String userId; // get the current user ID

    // Firebase instances
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private StorageReference storageReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_vendor_profile_update, container, false);

        vendorAvatar = view.findViewById(R.id.vendor_avatar);
        vendorFullName = view.findViewById(R.id.vendor_full_name);
        vendorEmail = view.findViewById(R.id.vendor_email);
        vendorShopName = view.findViewById(R.id.vendor_shop_name);
        vendorPhoneNumber = view.findViewById(R.id.vendor_phone_number);
        vendorLocation = view.findViewById(R.id.vendor_location);
        vendorId = view.findViewById(R.id.vendor_id);
        vendorUserType = view.findViewById(R.id.vendor_user_type);
        vendorStateSpinner = view.findViewById(R.id.vendor_state_spinner);
        updateButton = view.findViewById(R.id.vendor_profile_update_btn);
        progressBar = view.findViewById(R.id.vendor_progress_bar);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference("UserAvatar");

        userId = auth.getCurrentUser().getUid(); // assuming user is authenticated

        vendorAvatar.setOnClickListener(v -> selectImage());

        updateButton.setOnClickListener(v -> updateVendorProfile());

        // Show loading indicator while fetching data
        progressBar.setVisibility(View.VISIBLE);
        FetchUserData.getVendorData(new FetchVendorDataCallback() {
            @Override
            public void onCallback(VendorsModel vendorData) {
                progressBar.setVisibility(View.GONE); // hide progress bar once data is fetched
                vendorFullName.setText(vendorData.getName());
                vendorEmail.setText(vendorData.getEmail());
                vendorPhoneNumber.setText(vendorData.getPhoneNumber());
                vendorShopName.setText(vendorData.getShopName());
                vendorLocation.setText(vendorData.getLocation());
                vendorId.setText(vendorData.getVendorId());
                Glide.with(requireActivity()).load(vendorData.getUser_avatar_url()).into(vendorAvatar);

                // Set the state in the spinner
                if (selected_spinner_State_Item != null) {
                    int selectedIndex = findIndex(States, vendorData.getState());
                    if (selectedIndex >= 0) {
                        vendorStateSpinner.setSelection(selectedIndex);
                    }
                }
            }
        });

        SetSpinnerStates();

        return view;
    }

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 1000);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000 && resultCode == Activity.RESULT_OK && data != null) {
            imageUri = data.getData();
            vendorAvatar.setImageURI(imageUri);
        }
    }

    private void updateVendorProfile() {
        String fullName = vendorFullName.getText().toString();
        String email = vendorEmail.getText().toString();
        String shopName = vendorShopName.getText().toString();
        String phoneNumber = vendorPhoneNumber.getText().toString();
        String location = vendorLocation.getText().toString();
        String state = vendorStateSpinner.getSelectedItem().toString();
        String userType = vendorUserType.getText().toString();

        // Check if any required field is empty
        if (fullName.isEmpty() || email.isEmpty() || shopName.isEmpty() || phoneNumber.isEmpty() || location.isEmpty() || state.equals("Select State") || userType.isEmpty()) {
            Toasty.error(getActivity(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        if (imageUri != null) {
            StorageReference fileRef = storageReference.child(userId + ".jpg");
            fileRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                String imageUrl = uri.toString();
                saveVendorProfile(fullName, email, shopName, phoneNumber, location, state, userType, imageUrl);
            }));
        } else {
            saveVendorProfile(fullName, email, shopName, phoneNumber, location, state, userType, null);
        }
    }

    private void saveVendorProfile(String fullName, String email, String shopName, String phoneNumber, String location, String state, String userType, String imageUrl) {
        Map<String, Object> vendorUpdates = new HashMap<>();
        vendorUpdates.put("name", fullName);
        vendorUpdates.put("email", email);
        vendorUpdates.put("shopName", shopName);
        vendorUpdates.put("phoneNumber", phoneNumber);
        vendorUpdates.put("location", location);
        vendorUpdates.put("state", state);
        vendorUpdates.put("user_type", userType);
        if (imageUrl != null) vendorUpdates.put("user_avatar_url", imageUrl);

        db.collection("Vendors").document(userId).update(vendorUpdates).addOnSuccessListener(aVoid -> {
            progressBar.setVisibility(View.GONE);
            db.collection("AllUsers").document(userId).update(vendorUpdates).addOnSuccessListener(bVoid -> {
                progressBar.setVisibility(View.GONE);
                Toasty.success(requireContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show();
                getActivity().onBackPressed();
            }).addOnFailureListener(e -> {
                progressBar.setVisibility(View.GONE);
                Toasty.error(requireContext(), "Failed to update profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        }).addOnFailureListener(e -> {
            progressBar.setVisibility(View.GONE);
            Toasty.error(requireContext(), "Failed to update profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void SetSpinnerStates() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireActivity().getApplicationContext(), R.layout.dropdown_layout, States);
        vendorStateSpinner.setAdapter(adapter);

        vendorStateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selected_spinner_State_Item = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selected_spinner_State_Item = "Select State";
            }
        });
    }

    public static int findIndex(String[] array, String element) {
        for (int i = 0; i < array.length; i++) {
            if (array[i].equals(element)) {
                return i;
            }
        }
        return -1;
    }
}
