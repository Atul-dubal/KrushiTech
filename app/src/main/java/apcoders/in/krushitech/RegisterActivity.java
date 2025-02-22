package apcoders.in.krushitech;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Objects;

import apcoders.in.krushitech.models.User_Farmer_Model;
import apcoders.in.krushitech.models.VendorsModel;
import apcoders.in.krushitech.utils.WalletManagement;
import es.dmoral.toasty.Toasty;

public class RegisterActivity extends AppCompatActivity {

    String[] States = {"Select State", "Andhra Pradesh", "Arunachal Pradesh", "Assam", "Bihar", "Chhattisgarh", "Goa", "Gujarat", "Haryana", "Himachal Pradesh", "Jammu and Kashmir", "Jharkhand", "Karnataka", "Kerala", "Madhya Pradesh", "Maharashtra", "Manipur", "Meghalaya", "Mizoram", "Nagaland", "Odisha", "Punjab", "Rajasthan", "Sikkim", "Tamil Nadu", "Telangana", "Tripura", "Uttarakhand", "Uttar Pradesh", "West Bengal", "Andaman and Nicobar Islands", "Chandigarh", "Dadra and Nagar Haveli", "Daman and Diu", "Delhi", "Lakshadweep", "Puducherry"};
    Spinner spinner_states;
    FirebaseFirestore firebaseFirestore;
    CollectionReference collectionReference;
    FirebaseAuth firebaseAuth;
    MaterialButton register_btn;
    ImageView register_user_avatar;
    String phone_number;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    String selected_spinner_State_Item;
    ProgressBar register_progress_bar;
    Uri AvatarImageURI = null;
    String imageUrl = null;

    TextInputEditText full_name, email, phone_number_edittext, register_password;
    RadioGroup radio_group_gender;
    RadioButton radio_gender_btn;

    TextInputEditText vendor_name, vendor_phone_number, vendor_email, vendor_shop_name, vendor_shop_address;

    RadioButton user_type_farmer, user_type_vendor;
    LinearLayout farmer_register_layout, vendor_register_layout;


    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        firebaseFirestore = FirebaseFirestore.getInstance();
        collectionReference = firebaseFirestore.collection("Farmers");
        firebaseStorage = FirebaseStorage.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        user_type_farmer = findViewById(R.id.user_type_farmer);
        user_type_vendor = findViewById(R.id.user_type_vendor);

        farmer_register_layout = findViewById(R.id.farmer_register_layout);
        vendor_register_layout = findViewById(R.id.vendor_register_layout);

        user_type_farmer.setChecked(true); // or radioButton2.setChecked(true);
        farmer_register_layout.setVisibility(View.VISIBLE);
        vendor_register_layout.setVisibility(View.GONE);


        final int PROFILE_AVATAR_REQUEST_CODE = 400;
        register_user_avatar = findViewById(R.id.register_user_avatar);
        spinner_states = findViewById(R.id.spinner_states);
        phone_number_edittext = findViewById(R.id.register_phone_number);
        full_name = findViewById(R.id.register_full_name);
        register_password = findViewById(R.id.register_password);
        email = findViewById(R.id.register_email);
        radio_group_gender = findViewById(R.id.radio_gender);
        register_btn = findViewById(R.id.register_btn);
        register_progress_bar = findViewById(R.id.register_progress_bar);

        vendor_name = findViewById(R.id.register_vendor_full_name);
        vendor_phone_number = findViewById(R.id.register_vendor_phone_number);
        vendor_email = findViewById(R.id.register_vendor_email);
        vendor_shop_name = findViewById(R.id.register_vendor_shop_name);
        vendor_shop_address = findViewById(R.id.register_vendor_shop_address);


        user_type_farmer.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                register_user_avatar.setImageDrawable(getDrawable(R.drawable.farmer_avatar));
                farmer_register_layout.setVisibility(View.VISIBLE);
                vendor_register_layout.setVisibility(View.GONE);
                collectionReference = firebaseFirestore.collection("Farmers");
                Log.d("TAG", "onCreate: " + getIntent().getBooleanExtra("isPhoneVerify", false));
                if (getIntent().getBooleanExtra("isPhoneVerify", false)) {
                    phone_number = getIntent().getStringExtra("phone_number");
                    phone_number_edittext.setText(phone_number);
                    phone_number_edittext.setEnabled(false);
                }
            }
        });

        user_type_vendor.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                register_user_avatar.setImageDrawable(getDrawable(R.drawable.vendor_avatar));
                farmer_register_layout.setVisibility(View.GONE);
                vendor_register_layout.setVisibility(View.VISIBLE);
                collectionReference = firebaseFirestore.collection("Vendors");
                Log.d("TAG", "onCreate: " + getIntent().getBooleanExtra("isPhoneVerify", false));
                if (getIntent().getBooleanExtra("isPhoneVerify", false)) {
                    phone_number = getIntent().getStringExtra("phone_number");
                    vendor_phone_number.setText(phone_number);
                    vendor_phone_number.setEnabled(false);
                }
            }
        });

        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (user_type_vendor.isChecked()) {
                    register_progress_bar.setVisibility(View.VISIBLE);
                    register_btn.setCheckable(false);

                    if (TextUtils.isEmpty(vendor_name.getText()) || TextUtils.isEmpty(register_password.getText().toString()) || TextUtils.isEmpty(vendor_email.getText()) || TextUtils.isEmpty(vendor_phone_number.getText().toString()) || TextUtils.isEmpty(vendor_shop_name.getText().toString()) || TextUtils.isEmpty(vendor_shop_address.getText()) || selected_spinner_State_Item.equals("Select State")) {
                        register_progress_bar.setVisibility(View.GONE);
                        register_btn.setCheckable(true);
                        Toasty.error(RegisterActivity.this, "Fill All The Fields", Toast.LENGTH_SHORT).show();
                        Log.d("TAG", "onClick: " + Objects.requireNonNull(phone_number_edittext.getText()).toString());
//                    Toast.makeText(RegisterActivity.this, "" + full_name.getText() + email.getText() + phone_number + radio_gender_btn.getText() + selected_spinner_State_Item, Toast.LENGTH_SHORT).show();
                    } else if (register_password.getText().toString().length() < 8) {
                        register_progress_bar.setVisibility(View.GONE);
                        register_btn.setCheckable(true);
                        Toasty.warning(RegisterActivity.this, "Password Must Contain 8 Characters", Toast.LENGTH_SHORT).show();
                    } else {
//                  Toast.makeText(RegisterActivity.this, "" + full_name.getText() + email.getText()+firebaseAuth.getCurrentUser().getPhoneNumber() + radio_gender_btn.getText() + selected_spinner_State_Item, Toast.LENGTH_SHORT).show();
                        Log.d("RegisterActivity", "Name: " + vendor_name.getText().toString());
                        Log.d("RegisterActivity", "Email: " + vendor_email.getText().toString());
                        Log.d("RegisterActivity", "Phone: " + vendor_phone_number.getText().toString());
                        Log.d("RegisterActivity", "Shop Name: " + vendor_shop_name.getText().toString());
                        Log.d("RegisterActivity", "Shop Address: " + vendor_shop_address.getText().toString());
                        Log.d("RegisterActivity", "State: " + selected_spinner_State_Item);

                        RegisterVendor();


                    }

                } else if (user_type_farmer.isChecked()) {
                    register_progress_bar.setVisibility(View.VISIBLE);
                    register_btn.setCheckable(false);
                    int radio_group_selectId = radio_group_gender.getCheckedRadioButtonId();
                    radio_gender_btn = findViewById(radio_group_selectId);

                    if (TextUtils.isEmpty(full_name.getText()) || TextUtils.isEmpty(register_password.getText().toString()) || TextUtils.isEmpty(email.getText()) || TextUtils.isEmpty(phone_number_edittext.getText().toString()) || radio_group_selectId == -1 || selected_spinner_State_Item.equals("Select State")) {
                        register_progress_bar.setVisibility(View.GONE);
                        register_btn.setCheckable(true);
                        Toasty.error(RegisterActivity.this, "Fill All The Fields", Toast.LENGTH_SHORT).show();
                        Log.d("TAG", "onClick: " + Objects.requireNonNull(phone_number_edittext.getText()).toString());
//                    Toast.makeText(RegisterActivity.this, "" + full_name.getText() + email.getText() + phone_number + radio_gender_btn.getText() + selected_spinner_State_Item, Toast.LENGTH_SHORT).show();
                    } else if (register_password.getText().toString().length() < 8) {
                        register_progress_bar.setVisibility(View.GONE);
                        register_btn.setCheckable(true);
                        Toasty.warning(RegisterActivity.this, "Password Must Contain 8 Characters", Toast.LENGTH_SHORT).show();
                    } else {
//                  Toast.makeText(RegisterActivity.this, "" + full_name.getText() + email.getText()+firebaseAuth.getCurrentUser().getPhoneNumber() + radio_gender_btn.getText() + selected_spinner_State_Item, Toast.LENGTH_SHORT).show();
                        Log.d("RegisterActivity", "Name: " + full_name.getText().toString());
                        Log.d("RegisterActivity", "Email: " + email.getText().toString());
                        Log.d("RegisterActivity", "Phone: " + phone_number_edittext.getText().toString());
                        Log.d("RegisterActivity", "Gender: " + radio_gender_btn.getText().toString());
                        Log.d("RegisterActivity", "State: " + selected_spinner_State_Item);

                        RegisterFarmer();


                    }

                }
            }

        });
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Request permission
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 200);
        }

        register_user_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                pickImageLauncher.launch(Intent.createChooser(intent, "Select Profile Picture"));

            }
        });


        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                R.layout.dropdown_layout,
                States
        );

        spinner_states.setAdapter(adapter);

        // Optionally, you can set an item selected listener
        spinner_states.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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

//
//    private void SaveFarmerData(User_Farmer_Model UserData) {
//
//        collectionReference.add(UserData).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentReference> task) {
//                if (task.isSuccessful()) {
//                    CollectionReference AllUsers = FirebaseFirestore.getInstance().collection("AllUsers");
//                    try {
//                        AllUsers.add(UserData);
//                    } catch (Error | Exception ignored) {
//
//                    }
//                    SharedPreferences sharedPreferences = getSharedPreferences("UserData",MODE_PRIVATE);
//                    SharedPreferences.Editor editor = sharedPreferences.edit();
//                    editor.putString("user_type","farmer");
//                    editor.apply();
//
//                    Toast.makeText(RegisterActivity.this, "Data Store", Toast.LENGTH_SHORT).show();
//                    register_progress_bar.setVisibility(View.GONE);
//                    register_btn.setCheckable(true);
//                    startActivity(new Intent(RegisterActivity.this, MainActivity.class));
//                    finish();
//                }
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Toast.makeText(RegisterActivity.this, "Data Not Store", Toast.LENGTH_SHORT).show();
//                register_btn.setCheckable(true);
//                register_progress_bar.setVisibility(View.GONE);
//            }
//        }).addOnCanceledListener(new OnCanceledListener() {
//            @Override
//            public void onCanceled() {
//                Toast.makeText(RegisterActivity.this, "Cancel", Toast.LENGTH_SHORT).show();
//                register_btn.setCheckable(true);
//                register_progress_bar.setVisibility(View.GONE);
//            }
//        });
//    }
//
//
//    private void SaveVendorData(VendorsModel VendorData) {
//
//        collectionReference.add(VendorData).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentReference> task) {
//                if (task.isSuccessful()) {
//                    CollectionReference AllUsers = FirebaseFirestore.getInstance().collection("AllUsers");
//                    try {
//                        AllUsers.add(VendorData);
//                    } catch (Error | Exception ignored) {
//
//                    }
//                    SharedPreferences sharedPreferences = getSharedPreferences("UserData",MODE_PRIVATE);
//                    SharedPreferences.Editor editor = sharedPreferences.edit();
//                    editor.putString("user_type","vendor");
//                    editor.apply();
//                    Toast.makeText(RegisterActivity.this, "Data Store", Toast.LENGTH_SHORT).show();
//                    register_progress_bar.setVisibility(View.GONE);
//                    register_btn.setCheckable(true);
//                    startActivity(new Intent(RegisterActivity.this, MainActivity.class));
//                    finish();
//                }
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Toast.makeText(RegisterActivity.this, "Data Not Store", Toast.LENGTH_SHORT).show();
//                register_btn.setCheckable(true);
//                register_progress_bar.setVisibility(View.GONE);
//            }
//        }).addOnCanceledListener(new OnCanceledListener() {
//            @Override
//            public void onCanceled() {
//                Toast.makeText(RegisterActivity.this, "Cancel", Toast.LENGTH_SHORT).show();
//                register_btn.setCheckable(true);
//                register_progress_bar.setVisibility(View.GONE);
//            }
//        });
//    }

    private void saveUserData(Object userData, String userType) {
        // Generate a unique document ID
//        String documentId = FirebaseFirestore.getInstance().collection("AllUsers").document().getId();
        String documentId = firebaseAuth.getCurrentUser().getUid();
        // Set up the references
        CollectionReference allUsers = FirebaseFirestore.getInstance().collection("AllUsers");
        DocumentReference allUsersDocRef = allUsers.document(documentId);

        CollectionReference specificCollection = userType.equals("farmer") ?
                FirebaseFirestore.getInstance().collection("Farmers") :
                FirebaseFirestore.getInstance().collection("Vendors");
        DocumentReference specificDocRef = specificCollection.document(documentId);

        // Save the data with the same document ID in both collections
        specificDocRef.set(userData).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {

                // Now add the same data to AllUsers collection with the same document ID
                allUsersDocRef.set(userData).addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        // Save user type in SharedPreferences
                        SharedPreferences sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("user_type", userType);
                        editor.apply();

                        Toasty.success(RegisterActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                        register_progress_bar.setVisibility(View.GONE);
                        register_btn.setCheckable(true);
                        WalletManagement.initializeWallet(firebaseAuth.getCurrentUser().getUid());
                        startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                        finish();
                    } else {
                        Log.e("RegisterActivity", "Failed to save in AllUsers: " + task1.getException().getMessage());
                    }
                });
            } else {
                Toasty.error(RegisterActivity.this, "Registration Unsuccessful", Toast.LENGTH_SHORT).show();
                register_btn.setCheckable(true);
                register_progress_bar.setVisibility(View.GONE);
            }
        }).addOnFailureListener(e -> {
            Toasty.error(RegisterActivity.this, "Registration Unsuccessful", Toast.LENGTH_SHORT).show();
            register_btn.setCheckable(true);
            register_progress_bar.setVisibility(View.GONE);
        });
    }


    public void RegisterFarmer() {
        uploadImageToFirebase(AvatarImageURI, new UploadImageToFirebaseCallback() {
            @Override
            public void onCallback(Boolean IsUploaded) {
                if (IsUploaded && AvatarImageURI != null) {
                    String user = "null";
                    try {
                        user = firebaseAuth.getCurrentUser().getUid();
                    } catch (NullPointerException exception) {
                        user = "null";
                    }

                    if (user.equals("null")) {
                        firebaseAuth.createUserWithEmailAndPassword(Objects.requireNonNull(email.getText()).toString(), Objects.requireNonNull(register_password.getText()).toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    User_Farmer_Model UserData;

                                    if (phone_number != null) {
                                        UserData = new User_Farmer_Model(firebaseAuth.getCurrentUser().getUid(), full_name.getText().toString(), email.getText().toString(), phone_number, radio_gender_btn.getText().toString(), "farmer", selected_spinner_State_Item, imageUrl);
                                        saveUserData(UserData, "farmer");
                                    } else {
                                        UserData = new User_Farmer_Model(firebaseAuth.getCurrentUser().getUid(), full_name.getText().toString(), email.getText().toString(), phone_number_edittext.getText().toString(), radio_gender_btn.getText().toString(), "farmer", selected_spinner_State_Item, imageUrl);
                                        saveUserData(UserData, "farmer");
                                    }


                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                register_progress_bar.setVisibility(View.GONE);
                                register_btn.setCheckable(true);
                                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                                    Toasty.error(RegisterActivity.this, "Invalid Email".toString(), Toast.LENGTH_SHORT).show();
                                } else {
                                    Log.d("TAG", "onFailure: " + e.toString());
                                    Toasty.error(RegisterActivity.this, "Registration Unsuccessful" + e.toString(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        User_Farmer_Model UserData;

                        if (phone_number != null) {
                            UserData = new User_Farmer_Model(firebaseAuth.getCurrentUser().getUid(), full_name.getText().toString(), email.getText().toString(), phone_number, radio_gender_btn.getText().toString(), "farmer", selected_spinner_State_Item, imageUrl);
                            saveUserData(UserData, "farmer");
                        } else {
                            UserData = new User_Farmer_Model(firebaseAuth.getCurrentUser().getUid(), full_name.getText().toString(), email.getText().toString(), phone_number_edittext.getText().toString(), radio_gender_btn.getText().toString(), "farmer", selected_spinner_State_Item, imageUrl);
                            saveUserData(UserData, "farmer");
                        }

                    }


                } else {
                    register_progress_bar.setVisibility(View.GONE);
                    register_btn.setCheckable(true);
                    Toasty.info(RegisterActivity.this, "Please Select Profile Picture", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void RegisterVendor() {
        uploadImageToFirebase(AvatarImageURI, new UploadImageToFirebaseCallback() {
            @Override
            public void onCallback(Boolean IsUploaded) {
                if (IsUploaded && AvatarImageURI != null) {
                    String user = "null";
                    try {
                        user = firebaseAuth.getCurrentUser().getUid();
                    } catch (NullPointerException exception) {
                        user = "null";
                    }

                    if (user.equals("null")) {
                        firebaseAuth.createUserWithEmailAndPassword(Objects.requireNonNull(vendor_email.getText()).toString(), Objects.requireNonNull(register_password.getText()).toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    VendorsModel VendorData;

                                    if (phone_number != null) {
                                        VendorData = new VendorsModel(firebaseAuth.getCurrentUser().getUid(), vendor_name.getText().toString(), vendor_email.getText().toString(), vendor_shop_name.getText().toString(), phone_number, vendor_shop_address.getText().toString(), selected_spinner_State_Item, imageUrl, "vendor");
                                        saveUserData(VendorData, "vendor");
                                    } else {
                                        VendorData = new VendorsModel(firebaseAuth.getCurrentUser().getUid(), vendor_name.getText().toString(), vendor_email.getText().toString(), vendor_shop_name.getText().toString(), vendor_phone_number.getText().toString(), vendor_shop_address.getText().toString(), selected_spinner_State_Item, imageUrl, "vendor");
                                        saveUserData(VendorData, "vendor");
                                    }

                                }
                            }

                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                register_progress_bar.setVisibility(View.GONE);
                                register_btn.setCheckable(true);
                                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                                    Toasty.error(RegisterActivity.this, "Invalid Email".toString(), Toast.LENGTH_SHORT).show();
                                } else {
                                    Log.d("TAG", "onFailure: " + e.toString());
                                    Toasty.error(RegisterActivity.this, "Registration Unsuccessful" + e.toString(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        VendorsModel VendorData;

                        if (phone_number != null) {
                            VendorData = new VendorsModel(firebaseAuth.getCurrentUser().getUid(), vendor_name.getText().toString(), vendor_email.getText().toString(), vendor_shop_name.getText().toString(), phone_number, vendor_shop_address.toString(), "vendor", selected_spinner_State_Item, imageUrl);
                            saveUserData(VendorData, "vendor");
                        } else {
                            VendorData = new VendorsModel(firebaseAuth.getCurrentUser().getUid(), vendor_name.getText().toString(), vendor_email.getText().toString(), vendor_shop_name.getText().toString(), vendor_phone_number.getText().toString(), vendor_shop_address.toString(), "vendor", selected_spinner_State_Item, imageUrl);
                            saveUserData(VendorData, "vendor");
                        }

                    }


                } else {
                    register_progress_bar.setVisibility(View.GONE);
                    register_btn.setCheckable(true);
                    Toasty.error(RegisterActivity.this, "Please Select Profile Picture ", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    ActivityResultLauncher<Intent> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        AvatarImageURI = result.getData().getData();
                        // Handle the selected image URI (e.g., display it in an ImageView)
                        register_user_avatar.setImageURI(AvatarImageURI);

                    }
                }
            });

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 200) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                Toasty.success(this, "Permission granted", Toast.LENGTH_SHORT).show();
            } else {
                // Permission denied
                Toasty.warning(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }


    public void uploadImageToFirebase(Uri imageUri, UploadImageToFirebaseCallback uploadImageToFirebaseCallback) {

        if (imageUri != null) {
            // Define the file path and name in Firebase Storage
            String name = "";
            if (user_type_farmer.isChecked()) {
                name = Objects.requireNonNull(full_name.getText()).toString();
            }
            if (user_type_vendor.isChecked()) {
                name = Objects.requireNonNull(vendor_name.getText()).toString();
            }
            StorageReference imageRef = firebaseStorage.getReference("UserAvatar").child(name + ".jpg");

            // Start the upload
            UploadTask uploadTask = imageRef.putFile(imageUri);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // Get the download URL after a successful upload
                    imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri downloadUri) {
                            // The image is successfully uploaded and you have the download URL
                            imageUrl = downloadUri.toString();
                            uploadImageToFirebaseCallback.onCallback(true);
                            register_btn.setCheckable(true);
//                            Toast.makeText(RegisterActivity.this, "Upload successful! URL: " + imageUrl, Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Handle the failure
                    register_progress_bar.setVisibility(View.GONE);
                    register_btn.setCheckable(true);
                    Toasty.error(RegisterActivity.this, "User Profile Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            register_progress_bar.setVisibility(View.GONE);
            register_btn.setCheckable(true);
            Toasty.error(RegisterActivity.this, "Select Profile Picture", Toast.LENGTH_SHORT).show();
        }
    }

    interface UploadImageToFirebaseCallback {
        public void onCallback(Boolean IsUploaded);
    }
}
