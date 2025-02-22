package apcoders.in.krushitech;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import apcoders.in.krushitech.adapters.ProductImageAdapter;
import apcoders.in.krushitech.utils.BasicUtils;
import apcoders.in.krushitech.utils.ProductManagement;
import apcoders.in.krushitech.utils.UserLocation;
import es.dmoral.toasty.Toasty;

public class Upload_Product_Activity extends AppCompatActivity {
    Toolbar toolbar;
    LinearLayout product_images_layout;
    List<Uri> selectedImages = new ArrayList<>();
    ImageView product_image_add_image;
    RecyclerView product_images_recyclerView;
    Spinner spinner_category;
    Spinner spinner_servicetype;
    String selected_item_category_spinner = "select category";
    MaterialButton new_equipment_upload_btn;
    Button fromDateBtn, ToDateBtn;
    TextInputEditText product_name_edittext, product_description_edittext, product_quantity_edittext, product_price_edittext, product_address_edittext;
    String product_name, product_description, product_address;
    int product_quantity;
    double product_price;
    String selected_service_type = "Rent";
    String[] service_type_option = new String[]{"Rent", "Buy"};
    String[] category_options = new String[]{"select category", "equipments", "fertilizers", "seeds"};
    TextView location_cooridnates;
    double latitudeValue = 0.0;
    double longitudeValue = 0.0;
    Date FromDate = new Date(), ToDate = new Date();
    FirebaseAuth firebaseAuth;
    boolean isLocationPermissionGranted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_product);

        fromDateBtn = findViewById(R.id.fromDateBtn);
        ToDateBtn = findViewById(R.id.ToDateBtn);

        fromDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BasicUtils.showToDatePicker(Upload_Product_Activity.this, new BasicUtils.DatePickupCallback() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDateSelected(Date date) {
                        if (date != null ) {
                            FromDate = date;
                            fromDateBtn.setText("From : " + date.toLocaleString().substring(0, 12));
                        }else{
                            Toasty.error(Upload_Product_Activity.this,"Select Valid Date",Toasty.LENGTH_LONG).show();
                        }
                    }
                });
//                showDatePickerDialog(fromDateBtn, "From");
            }
        });
        ToDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               BasicUtils.showToDatePicker(Upload_Product_Activity.this, new BasicUtils.DatePickupCallback() {
                   @SuppressLint("SetTextI18n")
                   @Override
                   public void onDateSelected(Date date) {
                       if (date != null && !date.before(FromDate)) {
                           ToDate = date;
                           ToDateBtn.setText("To : " + date.toLocaleString().substring(0, 12));
                       }else{
                           Toasty.error(Upload_Product_Activity.this,"Select Valid Date",Toasty.LENGTH_LONG).show();
                       }
                   }
               });
            }
        });

        product_name_edittext = findViewById(R.id.product_name);
        product_description_edittext = findViewById(R.id.product_description);
        product_quantity_edittext = findViewById(R.id.product_quantity);
        product_price_edittext = findViewById(R.id.product_price);
        product_address_edittext = findViewById(R.id.product_address);
        toolbar = findViewById(R.id.toolbar);
        spinner_servicetype = findViewById(R.id.service_type_spinner);
        spinner_category = findViewById(R.id.spinner_category);
        product_images_layout = findViewById(R.id.product_images_layout);
        product_image_add_image = findViewById(R.id.product_image_add_image);
        product_images_recyclerView = findViewById(R.id.product_images_recyclerView);
        new_equipment_upload_btn = findViewById(R.id.new_equipment_upload_btn);
        location_cooridnates = findViewById(R.id.location_cooridnates);

        firebaseAuth = FirebaseAuth.getInstance();

        setCategorySelectBox();
        SelectServiceType();
        SelectMultipleProductImages();
        UploadProduct();


        UserLocation.GetCurrentLocation(this, new UserLocation.LocationCallback() {
                    @Override
                    public void onLocationReceived(double latitude, double longitude) {
                        latitudeValue = latitude;
                        longitudeValue = longitude;
                        location_cooridnates.setText("Latitude: " + latitude + ", Longitude: " + longitude);
                    }

                    @Override
                    public void onLocationError(String errorMessage) {
                        location_cooridnates.setText(errorMessage);
                        GetUserLocation();
                    }

                }
        );


        toolbar.setTitle("Add Equipment");

        setSupportActionBar(toolbar);

        getSupportActionBar().
                setDisplayHomeAsUpEnabled(true);
    }

    private void SelectServiceType() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                R.layout.dropdown_layout,
                service_type_option
        );

        spinner_servicetype.setAdapter(adapter);

        // Optionally, you can set an item selected listener
        spinner_servicetype.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selected_service_type = (String) parent.getItemAtPosition(position);
                // Handle the selected item
                if(selected_service_type.equals("Rent")){
                    selected_item_category_spinner ="equipments";
                    spinner_category.setSelection(1);
                }else{

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle the case when nothing is selected
                selected_service_type = "Rent";
            }
        });
    }

    private void setCategorySelectBox() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                R.layout.dropdown_layout,
                category_options
        );

        spinner_category.setAdapter(adapter);

        // Optionally, you can set an item selected listener
        spinner_category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selected_item_category_spinner = (String) parent.getItemAtPosition(position);
                if (selected_item_category_spinner.equals("seeds") || selected_item_category_spinner.equals("fertilizers")) {
                    selected_service_type = "Buy";
                    service_type_option = new String[]{ "Buy"};
                    spinner_servicetype.setSelection(1); // Set "Buy" as the selected item in the service type spinner
                } else {
                    service_type_option = new String[]{"Rent", "Buy"};
                    selected_service_type = "Rent"; // Default to Rent if the category is something else
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle the case when nothing is selected
                selected_item_category_spinner = "select category";
            }
        });
    }

    private void UploadProduct() {
        new_equipment_upload_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new_equipment_upload_btn.setCheckable(false);

                product_name = Objects.requireNonNull(product_name_edittext.getText()).toString();
                product_description = Objects.requireNonNull(product_description_edittext.getText()).toString();
                product_quantity = Integer.parseInt(Objects.requireNonNull(product_quantity_edittext.getText()).toString());
                product_address = Objects.requireNonNull(product_address_edittext.getText()).toString();
                product_price = Double.parseDouble(Objects.requireNonNull(product_price_edittext.getText()).toString());


                if (TextUtils.isEmpty(product_name) || FromDate == null || ToDate == null || TextUtils.isEmpty(product_description) || product_quantity == 0 || TextUtils.isEmpty(product_address) || latitudeValue == 0.0 || longitudeValue == 0.0 || selected_item_category_spinner.equals("select category")) {
                    Toasty.error(Upload_Product_Activity.this, "Please Fill All The Fields", Toast.LENGTH_SHORT).show();
                    GetUserLocation();
                    new_equipment_upload_btn.setCheckable(true);
                } else {
                    if (!selectedImages.isEmpty()) {
                        ProductManagement.uploadProduct(Upload_Product_Activity.this,
                                Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid(), product_name, product_price, selected_service_type, product_quantity, selected_item_category_spinner,
                                new GeoPoint(latitudeValue, longitudeValue), product_address,
                                product_description, FromDate, ToDate, selectedImages, new ProductManagement.uploadProductCallback() {
                                    @Override
                                    public void onCallback(String message) {
                                        selectedImages.clear();
                                        product_name_edittext.setText(null);
                                        product_description_edittext.setText(null);
                                        product_quantity_edittext.setText(null);
                                        product_address_edittext.setText(null);
                                        product_price_edittext.setText(null);
                                        product_images_recyclerView.setAdapter(null);
                                        product_image_add_image.setVisibility(View.VISIBLE);
                                        product_images_recyclerView.setVisibility(View.GONE);
                                        onBackPressed();
                                    }
                                });
                    } else {
                        new_equipment_upload_btn.setCheckable(true);
                        Toasty.error(Upload_Product_Activity.this, "Please Select Product Images", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });
    }

    private void SelectMultipleProductImages() {
        product_images_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openGallary = new Intent(Intent.ACTION_PICK);
                openGallary.setType("image/*");
                openGallary.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                ImagePickerLauncher.launch(Intent.createChooser(openGallary, "Select Product Images"));
            }
        });
    }

    ActivityResultLauncher<Intent> ImagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult activityResult) {
                    Intent data = activityResult.getData();
                    Log.d("Selected Images :", selectedImages.size() + "");
                    if (activityResult.getResultCode() == RESULT_OK && data != null) {
                        assert activityResult.getData() != null;
                        if (activityResult.getData().getClipData() != null) {
                            int count = Objects.requireNonNull(data.getClipData()).getItemCount();
                            for (int i = 0; i < count; i++) {
                                Uri imageUri = data.getClipData().getItemAt(i).getUri();
                                // Add the imageUri to a list and update UI
                                selectedImages.add(imageUri);
                            }
                            Log.d("Selected Images :", selectedImages.size() + "");
                            if (!selectedImages.isEmpty()) {
                                product_image_add_image.setVisibility(View.GONE);
                                product_images_recyclerView.setVisibility(View.VISIBLE);
                                ProductImageAdapter adapter = new ProductImageAdapter(Upload_Product_Activity.this, selectedImages);
                                product_images_recyclerView.setLayoutManager(new LinearLayoutManager(Upload_Product_Activity.this, LinearLayoutManager.HORIZONTAL, false));
                                product_images_recyclerView.setAdapter(adapter);
                            } else {
                                product_image_add_image.setVisibility(View.VISIBLE);
                                product_images_recyclerView.setVisibility(View.GONE);
                            }
                        }
                    }
                }
            }
    );


//    private void showDatePickerDialog(Button button, String tag) {
//        // Get the current date
//        Calendar calendar = Calendar.getInstance();
//        int year = calendar.get(Calendar.YEAR);
//        int month = calendar.get(Calendar.MONTH);
//        int day = calendar.get(Calendar.DAY_OF_MONTH);
//
//        // Create a new DatePickerDialog and set the date
//        DatePickerDialog datePickerDialog = new DatePickerDialog(
//                Upload_Product_Activity.this,
//                new DatePickerDialog.OnDateSetListener() {
//                    @Override
//                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
//                        // Month is 0-based in DatePickerDialog, so add 1
//                        String selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;
//                        if(selectedDate.to)
//                        button.setText(tag + ": " + selectedDate);
//
//                        // Create a Calendar object with the selected date
//                        Calendar selectedDateCalendar = Calendar.getInstance();
//                        selectedDateCalendar.set(year, month, dayOfMonth);
//                        if (tag.equals("To")) {
//                            ToDate = selectedDateCalendar.getTime();
//                        } else {
//                            FromDate = selectedDateCalendar.getTime();
//                        }
//                    }
//                },
//                year, month, day
//        );
//        datePickerDialog.show();
//    }


    private void GetUserLocation() {
        // Check if location permission is granted
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // If permission is denied but can ask again (not "Don't ask again")
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show a rationale dialog explaining why the permission is needed
                showLocationPermissionRationale();
            } else {
                // Request the permission for the first time or if the user has not chosen "Don't ask again"
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        1);
            }
        } else {
            // Permission already granted, proceed with location-based functionality
            isLocationPermissionGranted = true;
        }
    }


    // Handles the result of the permission request
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with location-based functionality
                isLocationPermissionGranted = true;
            } else {
                // Permission denied, show message or take necessary action
                if (!ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                    // User has denied with "Don't ask again"
                    showPermissionDeniedDialog();
                } else {
                    Toasty.error(this, "Location permission required for nearby products.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    // Shows a rationale dialog for requesting location permission
    private void showLocationPermissionRationale() {
        new AlertDialog.Builder(this)
                .setTitle("Location Permission Needed")
                .setCancelable(false)
                .setMessage("This app requires location access to suggest nearby farm equipment. Please enable it for better functionality.")
                .setPositiveButton("Allow", (dialog, which) -> ActivityCompat.requestPermissions(Upload_Product_Activity.this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1))
                .setNegativeButton("Deny", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    // Shows a dialog when permission is denied permanently (user selected "Don't ask again")
    private void showPermissionDeniedDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Enable Location Permission")
                .setCancelable(false)
                .setMessage("Location access is required to suggest nearby farm equipment. Please enable it in settings.")
                .setPositiveButton("Go to Settings", (dialog, which) -> {
                    Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.parse("package:" + getPackageName()));
                    startActivity(intent);
                })
                .create()
                .show();
    }


}