package apcoders.in.krushitech.fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import java.util.Objects;

import apcoders.in.krushitech.R;
import apcoders.in.krushitech.interfaces.FetchUserDataCallback;
import apcoders.in.krushitech.models.User_Farmer_Model;
import apcoders.in.krushitech.utils.FetchUserData;
import es.dmoral.toasty.Toasty;

public class Farmer_Profile_Update_Fragment extends Fragment {

    String[] States = {"Select State", "Andhra Pradesh", "Arunachal Pradesh", "Assam", "Bihar", "Chhattisgarh", "Goa", "Gujarat", "Haryana", "Himachal Pradesh", "Jammu and Kashmir", "Jharkhand", "Karnataka", "Kerala", "Madhya Pradesh", "Maharashtra", "Manipur", "Meghalaya", "Mizoram", "Nagaland", "Odisha", "Punjab", "Rajasthan", "Sikkim", "Tamil Nadu", "Telangana", "Tripura", "Uttarakhand", "Uttar Pradesh", "West Bengal", "Andaman and Nicobar Islands", "Chandigarh", "Dadra and Nagar Haveli", "Daman and Diu", "Delhi", "Lakshadweep", "Puducherry"};

    String selected_spinner_State_Item;
    private ImageView profileImageView;
    private TextInputEditText fullNameEditText, phoneEditText, emailEditText, profileUserId;
    private Spinner stateSpinner;
    private RadioGroup genderRadioGroup;
    RadioButton radio_gender_btn;
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
        View view = inflater.inflate(R.layout.fragment_farmer_profile__update, container, false);

        profileUserId = view.findViewById(R.id.farmer_user_id);
        profileImageView = view.findViewById(R.id.farmer_avatar);
        fullNameEditText = view.findViewById(R.id.farmer_full_name);
        phoneEditText = view.findViewById(R.id.farmer_phone_number);
        emailEditText = view.findViewById(R.id.farmer_email);
        stateSpinner = view.findViewById(R.id.farmer_state_spinner);
        genderRadioGroup = view.findViewById(R.id.radio_gender);
        updateButton = view.findViewById(R.id.farmer_profile_update_btn);
        progressBar = view.findViewById(R.id.farmer_progress_bar);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference("UserAvatar");

        userId = auth.getCurrentUser().getUid(); // assuming user is authenticated

        profileImageView.setOnClickListener(v -> selectImage());

        updateButton.setOnClickListener(v -> updateProfile());

        FetchUserData.getUserData(new FetchUserDataCallback() {
            @Override
            public void onCallback(User_Farmer_Model userData) {

                fullNameEditText.setText(userData.getFull_name());
                emailEditText.setText(userData.getEmail());
                phoneEditText.setText(userData.getPhone_number());
                profileUserId.setText(userData.getUser_id());
                Glide.with(requireActivity()).load(userData.getUser_avatar_url()).into(profileImageView);
                if (userData.getGender().equals("Male")) {
                    radio_gender_btn = view.findViewById(R.id.gender_male);
                }
                if (userData.getGender().equals("Female")) {
                    radio_gender_btn = view.findViewById(R.id.gender_female);
                }
                radio_gender_btn.setChecked(true);

                if (selected_spinner_State_Item != null) {
                    int selectedIndex = findIndex(States, userData.getState());
                    Log.d("TAG", "onCallback: " + selectedIndex);
                    if (selectedIndex >= 0) {
                        stateSpinner.setSelection(selectedIndex);
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
            profileImageView.setImageURI(imageUri);
        }
    }

    private void updateProfile() {
        String fullName = Objects.requireNonNull(fullNameEditText.getText()).toString();
        String phoneNumber = Objects.requireNonNull(phoneEditText.getText()).toString();
        String state = stateSpinner.getSelectedItem().toString();
        String gender = ((RadioButton) requireView().findViewById(genderRadioGroup.getCheckedRadioButtonId())).getText().toString();

        if (fullName.isEmpty() || phoneNumber.isEmpty() || state.equals("Select State") || gender.isEmpty()) {
            Toasty.error(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            return; // Prevent update if required fields are empty
        }

        progressBar.setVisibility(View.VISIBLE);

        if (imageUri != null) {
            StorageReference fileRef = storageReference.child(userId + ".jpg");
            fileRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                String imageUrl = uri.toString();
                saveUserProfile(fullName, phoneNumber, state, gender, imageUrl);
            }).addOnFailureListener(e -> {
                progressBar.setVisibility(View.GONE);
                Toasty.error(requireContext(), "Image upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }));
        } else {
            saveUserProfile(fullName, phoneNumber, state, gender, null);
        }
    }

    private void saveUserProfile(String fullName, String phoneNumber, String state, String gender, String imageUrl) {
        Map<String, Object> userUpdates = new HashMap<>();
        userUpdates.put("full_name", fullName);
        userUpdates.put("phone_number", phoneNumber);
        userUpdates.put("state", state);
        userUpdates.put("gender", gender);
        if (imageUrl != null) userUpdates.put("user_avatar_url", imageUrl);

        db.collection("Farmers").document(userId).update(userUpdates)
                .addOnSuccessListener(aVoid -> {
                    progressBar.setVisibility(View.GONE);

                    db.collection("AllUsers").document(userId).update(userUpdates)
                            .addOnSuccessListener(bVoid -> {
                                progressBar.setVisibility(View.GONE);
                                Toasty.success(requireContext(), "Profile updated", Toast.LENGTH_SHORT).show();
                                getActivity().onBackPressed();
                            })
                            .addOnFailureListener(e -> {
                                progressBar.setVisibility(View.GONE);
                                Toasty.error(requireContext(), "Update failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toasty.error(requireContext(), "Update failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });

    }

    private void SetSpinnerStates() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireActivity().getApplicationContext(), R.layout.dropdown_layout, States);

        stateSpinner.setAdapter(adapter);

        // Optionally, you can set an item selected listener
        stateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selected_spinner_State_Item = (String) parent.getItemAtPosition(position);
                // Handle the selected item
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle the case when nothing is selected
                selected_spinner_State_Item = "Select State";
            }
        });
    }

    public static int findIndex(String[] array, String element) {
        for (int i = 0; i < array.length; i++) {
            if (array[i].equals(element)) {
                return i;  // Return the index if found
            }
        }
        return -1;  // Return -1 if not found
    }
}
