package apcoders.in.krushitech;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import apcoders.in.krushitech.adapters.ProductImageAdapter;
import apcoders.in.krushitech.adapters.ReviewAdapter;
import apcoders.in.krushitech.models.ProductModel;
import apcoders.in.krushitech.models.ReviewModel;
import apcoders.in.krushitech.utils.BasicUtils;
import apcoders.in.krushitech.utils.CartManagement;
import apcoders.in.krushitech.utils.FetchAllProducts;
import apcoders.in.krushitech.utils.ProductReviewManagement;
import es.dmoral.toasty.Toasty;

public class ProductDetailsActivity extends AppCompatActivity {
    Toolbar toolbar;
    List<Uri> ProductImagesUri = new ArrayList<>();
    RecyclerView product_images_recyclerView;
    TextView product_name, product_location, outOfStockTextView, product_price_bottom, product_price, product_available_date, product_description_text;
    RelativeLayout bottomFixedLayout;
    ProductModel ProductData;
    MaterialButton addToCartBtn;
    ShimmerFrameLayout shimmerFrameLayout;
    RelativeLayout RealView;
    Boolean isProductAvailable = false;
    ImageView addToWishlist, ShareProduct;
    LinearLayout give_review_layout, no_review_show_layout, review_show_layout;
    boolean ProductTypeIsEquipment = false;
    RecyclerView ReviewRecyclerView;

    // Review
    ImageView review_star_1, review_star_2, review_star_3, review_star_4, review_star_5;
    TextView review_experience;
    MaterialButton submitReviewBtn;
    TextInputEditText ReviewResponseEditText;
    int ReviewRating = 0;
    ReviewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);
        toolbar = findViewById(R.id.toolbar);
        ReviewRecyclerView = findViewById(R.id.ReviewRecyclerView);
        bottomFixedLayout = findViewById(R.id.bottomFixedLayout);
        product_images_recyclerView = findViewById(R.id.product_images_recyclerView);
        product_name = findViewById(R.id.product_name);
        product_price = findViewById(R.id.product_price);
        shimmerFrameLayout = findViewById(R.id.shimmerLayout);
        review_show_layout = findViewById(R.id.review_show_layout);
        no_review_show_layout = findViewById(R.id.no_review_show_layout);
        RealView = findViewById(R.id.RealView);
        ShareProduct = findViewById(R.id.ShareProduct);
        addToWishlist = findViewById(R.id.addToWishlist);
        give_review_layout = findViewById(R.id.give_review_layout);
        product_price_bottom = findViewById(R.id.product_price_bottom);
        addToCartBtn = findViewById(R.id.addToCartBtn);
        outOfStockTextView = findViewById(R.id.outOfStockTextView);
        product_location = findViewById(R.id.product_location);
        product_available_date = findViewById(R.id.product_available_date);
        product_description_text = findViewById(R.id.product_description_text);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        shimmerFrameLayout.startLayoutAnimation();

        String ProductId = getIntent().getStringExtra("ProductId");
        if (ProductId != null) {
            Log.d("ProductId: ", ProductId);
        }

        addToWishlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CartManagement.AddToCart(ProductDetailsActivity.this, ProductId);
                Toasty.info(ProductDetailsActivity.this, "Added To Wishlist", Toast.LENGTH_SHORT).show();
            }
        });
        ShareProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND);

                // Construct a more professional and user-friendly message
                String msg = "🌟 Check out this amazing product! 🌟\n\n" +
                        "🛒 *Product Name:* " + ProductData.getProductName() + "\n" +
                        "📝 *Description:* " + ProductData.getProductDescription() + "\n\n" +
                        "📸 *Take a look at the image here:* \n" + ProductData.getProductImagesUrl().get(0) + "\n\n" +
                        "Hurry! Don't miss out!";

                i.putExtra(Intent.EXTRA_TEXT, msg);
                i.setType("text/plain");
                startActivity(Intent.createChooser(i, "Share Product Using"));
            }
        });

        addToCartBtn.setCheckable(false);

        FetchAllProducts.FetchByProductId(ProductId, new FetchAllProducts.FetchProductDataCallback() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onCallback(ArrayList<ProductModel> ProductDataList) {
                if (!ProductDataList.isEmpty()) {
                    ProductData = ProductDataList.get(0);

//                    Set Review Functionality When Product Data Load
                    setReviewFunctionality();
                    isProductAvailable = BasicUtils.isProductAvailable(ProductDataList.get(0).getAvailableFromDate(), ProductDataList.get(0).getAvailableToDate());

                    if (!isProductAvailable) {
                        outOfStockTextView.setVisibility(View.VISIBLE);
                        addToCartBtn.setCheckable(false);
                    }

                    if (ProductData.getProductCategory().equals("equipments")) {
                        ProductTypeIsEquipment = true;
                    }
                    shimmerFrameLayout.hideShimmer();
                    bottomFixedLayout.setVisibility(View.VISIBLE);
                    shimmerFrameLayout.setVisibility(View.GONE);
                    RealView.setVisibility(View.VISIBLE);
                    for (String url : ProductData.getProductImagesUrl()) {
                        ProductImagesUri.add(Uri.parse(url));
                    }
                    ProductImageAdapter adapter = new ProductImageAdapter(ProductDetailsActivity.this, ProductImagesUri);
                    product_images_recyclerView.setLayoutManager(new LinearLayoutManager(ProductDetailsActivity.this, LinearLayoutManager.HORIZONTAL, false));
                    product_images_recyclerView.setAdapter(adapter);

                    product_name.setText(ProductData.getProductName());
                    product_available_date.setText("Available From : " + ProductData.getAvailableFromDate().toLocaleString().substring(0, 12) + " To " + ProductData.getAvailableToDate().toLocaleString().substring(0, 12));
                    product_price_bottom.setText(getString(R.string.price) + "₹" + ProductData.getProductPrice() + " For 1 Day");
                    if (!ProductData.getServiceType().equals("Buy")) {
                        product_price.setText(getString(R.string.price) + "₹" + ProductData.getProductPrice() + " For 1 Day");
                        product_price_bottom.setText(getString(R.string.price) + "₹" + ProductData.getProductPrice() + " For 1 Day");
                    } else {
                        product_price.setText(getString(R.string.price) + "₹" + ProductData.getProductPrice());
                        product_price_bottom.setText(getString(R.string.price) + "₹" + ProductData.getProductPrice());
                    }
                    product_description_text.setText(ProductData.getProductDescription());
                    product_location.setText(ProductData.getProductAddress());
                    addToCartBtn.setCheckable(true);
                    Log.d("onCallback: ", ProductData.getProductName());
                } else {
                    shimmerFrameLayout.hideShimmer();
                    shimmerFrameLayout.setVisibility(View.GONE);
                    RealView.setVisibility(View.VISIBLE);
                }
            }
        });
        addToCartBtn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                if (isProductAvailable) {
                    if (ProductTypeIsEquipment) {
                        BasicUtils.showDateSelectionDialog(ProductDetailsActivity.this, new BasicUtils.DatePickupCallback() {
                            @Override
                            public void onDateSelected(Date fromdate) {
                                if (fromdate != null) {
                                    BasicUtils.showToDatePicker(ProductDetailsActivity.this, new BasicUtils.DatePickupCallback() {
                                        @Override
                                        public void onDateSelected(Date todate) {
                                            if (todate != null) {
                                                processSelectedDates(fromdate, todate, ProductId, ProductData.getAvailableFromDate(), ProductData.getAvailableToDate());
                                                Log.d("TAG", "onDateSelected: " + fromdate.toLocaleString().substring(0, 12) + todate.toLocaleString().substring(0, 12));
                                            } else {
                                                Toasty.error(ProductDetailsActivity.this, "Select Valid To Date").show();
                                            }
                                        }
                                    });
                                } else {
                                    Toasty.error(ProductDetailsActivity.this, "Select Valid From Date").show();
                                }
                            }
                        });

                    } else {
                        final int[] Quantity = {1};
                        AlertDialog.Builder builder = new AlertDialog.Builder(ProductDetailsActivity.this);
                        View view = LayoutInflater.from(ProductDetailsActivity.this).inflate(R.layout.select_seeds_fertilizers_quantity_layout, null, false);
                        builder.setView(view);
                        builder.create().show();
                        TextView quantity = view.findViewById(R.id.quantity);
                        TextView PName = view.findViewById(R.id.PName);
                        TextView PPrice = view.findViewById(R.id.PPrice);
                        PName.setText(getString(R.string.product_name) + ProductData.getProductName());
                        PPrice.setText(getString(R.string.price) + ProductData.getProductPrice());
                        quantity.setText(String.valueOf(Quantity[0]));
                        MaterialButton continueShoppingBtn = view.findViewById(R.id.continueShoppingBtn);
                        LinearLayout decrease_btn = view.findViewById(R.id.decrease_btn);
                        LinearLayout increase_btn = view.findViewById(R.id.increase_btn);

                        continueShoppingBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent i = new Intent(ProductDetailsActivity.this, MakePaymentActivity.class);
                                Log.d("TAG", "showPurchaseConfirmation: " + Quantity[0]);
                                i.putExtra("quantity", Quantity[0] + "");
                                i.putExtra("ProductTypeIsEquipment", ProductTypeIsEquipment);
                                i.putExtra("product_id", ProductId);
                                startActivity(i);
                            }
                        });

                        decrease_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (Quantity[0] > 0) {
                                    Quantity[0]--;
                                    quantity.setText(String.valueOf(Quantity[0]));
                                    PPrice.setText(getString(R.string.price) + "₹" + (ProductData.getProductPrice() * Quantity[0]));
                                }
                            }
                        });
                        increase_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (Quantity[0] >= 0) {
                                    Quantity[0]++;
                                    quantity.setText(String.valueOf(Quantity[0]));
                                    PPrice.setText(getString(R.string.price) + "₹" + (ProductData.getProductPrice() * Quantity[0]));
                                }
                            }
                        });
                        Toasty.success(ProductDetailsActivity.this, "Product Category Is not Equipment", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toasty.error(ProductDetailsActivity.this, "Out Of Stock", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void setReviewFunctionality() {
        ProductReviewManagement.GetReviewsOfProduct(ProductData.getProductId(), new ProductReviewManagement.GetReviewsOfProduct() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onCallback(ArrayList<ReviewModel> ReviewList) {
                if (!ReviewList.isEmpty()) {
                    no_review_show_layout.setVisibility(View.GONE);
                    review_show_layout.setVisibility(View.VISIBLE);
                    Log.d("TAG", "onCallback: " + ReviewList.get(0).getRatingStars());
                    ReviewRecyclerView.setLayoutManager(new LinearLayoutManager(ProductDetailsActivity.this, LinearLayoutManager.VERTICAL, false));
                    adapter = new ReviewAdapter(ProductDetailsActivity.this, ReviewList);
                    adapter.notifyDataSetChanged();
                    ReviewRecyclerView.setAdapter(adapter);
                } else {
                    review_show_layout.setVisibility(View.GONE);
                    no_review_show_layout.setVisibility(View.VISIBLE);
                }
            }
        });
        give_review_layout.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onClick(View v) {
                View ReviewBottomLayoutView = LayoutInflater.from(ProductDetailsActivity.this).inflate(R.layout.product_review_layout, null, false);
                BottomSheetDialog ReviewbottomSheetDialog = new BottomSheetDialog(ProductDetailsActivity.this);
                ReviewbottomSheetDialog.setContentView(ReviewBottomLayoutView);
                ReviewbottomSheetDialog.show();

                ReviewResponseEditText = ReviewBottomLayoutView.findViewById(R.id.ReviewResponseEditText);
                review_experience = ReviewBottomLayoutView.findViewById(R.id.review_experience);
                submitReviewBtn = ReviewBottomLayoutView.findViewById(R.id.submitReviewBtn);
                review_star_1 = ReviewBottomLayoutView.findViewById(R.id.review_star_1);
                review_star_2 = ReviewBottomLayoutView.findViewById(R.id.review_star_2);
                review_star_3 = ReviewBottomLayoutView.findViewById(R.id.review_star_3);
                review_star_4 = ReviewBottomLayoutView.findViewById(R.id.review_star_4);
                review_star_5 = ReviewBottomLayoutView.findViewById(R.id.review_star_5);

                review_star_1.setOnClickListener(v1 -> {
                    resetReviewStars();
                    ReviewRating = 1;
                    review_experience.setText(getString(R.string.How_Was_Your_Experience_Poor));
                    review_star_1.setImageDrawable(getResources().getDrawable(R.drawable.review_star_color));
                });
                review_star_2.setOnClickListener(v1 -> {
                    resetReviewStars();
                    ReviewRating = 2;
                    review_experience.setText(getString(R.string.How_Was_Your_Experience_Fair));
                    review_star_1.setImageDrawable(getResources().getDrawable(R.drawable.review_star_color));
                    review_star_2.setImageDrawable(getResources().getDrawable(R.drawable.review_star_color));
                });
                review_star_3.setOnClickListener(v1 -> {
                    resetReviewStars();
                    ReviewRating = 3;
                    review_experience.setText(getString(R.string.How_Was_Your_Experience_Good));
                    review_star_1.setImageDrawable(getResources().getDrawable(R.drawable.review_star_color));
                    review_star_2.setImageDrawable(getResources().getDrawable(R.drawable.review_star_color));
                    review_star_3.setImageDrawable(getResources().getDrawable(R.drawable.review_star_color));
                });
                review_star_4.setOnClickListener(v1 -> {
                    resetReviewStars();
                    ReviewRating = 4;
                    review_experience.setText(getString(R.string.How_Was_Your_Experience_Very_Good));
                    review_star_1.setImageDrawable(getResources().getDrawable(R.drawable.review_star_color));
                    review_star_2.setImageDrawable(getResources().getDrawable(R.drawable.review_star_color));
                    review_star_3.setImageDrawable(getResources().getDrawable(R.drawable.review_star_color));
                    review_star_4.setImageDrawable(getResources().getDrawable(R.drawable.review_star_color));

                });
                review_star_5.setOnClickListener(v1 -> {
                    resetReviewStars();
                    ReviewRating = 5;
                    review_experience.setText(getString(R.string.How_Was_Your_Experince_Very_Excellent));
                    review_star_1.setImageDrawable(getResources().getDrawable(R.drawable.review_star_color));
                    review_star_2.setImageDrawable(getResources().getDrawable(R.drawable.review_star_color));
                    review_star_3.setImageDrawable(getResources().getDrawable(R.drawable.review_star_color));
                    review_star_4.setImageDrawable(getResources().getDrawable(R.drawable.review_star_color));
                    review_star_5.setImageDrawable(getResources().getDrawable(R.drawable.review_star_color));
                });

                submitReviewBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        submitReviewBtn.setCheckable(false);
                        String ReviewResponse = Objects.requireNonNull(ReviewResponseEditText.getText()).toString();
                        if (TextUtils.isEmpty(ReviewResponse)) {
                            Toasty.error(ProductDetailsActivity.this, "Please Enter Your Experience", Toast.LENGTH_SHORT).show();
                        } else {
                            ProductReviewManagement.SubmitReview(ReviewRating, ReviewResponse, ProductData.getProductId(), new ProductReviewManagement.isReviewSubmitted() {
                                @Override
                                public void onCallback(boolean isSubmitted) {
                                    if (isSubmitted) {
                                        submitReviewBtn.setCheckable(true);
                                        ReviewbottomSheetDialog.dismiss();
                                        Toasty.success(ProductDetailsActivity.this, "Review Submitted" + ReviewRating + ReviewResponse, Toast.LENGTH_SHORT).show();
                                        setReviewFunctionality();
                                    } else {
                                        submitReviewBtn.setCheckable(true);
                                        ReviewbottomSheetDialog.dismiss();
                                        Toasty.error(ProductDetailsActivity.this, "Review Not Submit", Toasty.LENGTH_LONG).show();
                                    }
                                }
                            });
                        }
                    }

                });
            }
        });
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void resetReviewStars() {
        review_star_1.setImageDrawable(getDrawable(R.drawable.review_star_blank));
        review_star_2.setImageDrawable(getDrawable(R.drawable.review_star_blank));
        review_star_3.setImageDrawable(getDrawable(R.drawable.review_star_blank));
        review_star_4.setImageDrawable(getDrawable(R.drawable.review_star_blank));
        review_star_5.setImageDrawable(getDrawable(R.drawable.review_star_blank));

    }

    private void processSelectedDates(Date fromDate, Date toDate, String ProductId, Date ProductFromDate, Date ProductToDate) {
        if (fromDate.after(toDate)) {
            Toasty.error(this, "Invalid date range. 'To' date must be after 'From' date.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if the selected dates are within the product's available range
        if (isRangeOfDateIsFree(fromDate, toDate)) {
            long differenceInMilliseconds = toDate.getTime() - fromDate.getTime();
            int numberOfDays = (int) (differenceInMilliseconds / (1000 * 60 * 60 * 24)) + 1; // Include both days

            // Show confirmation dialog
            showPurchaseConfirmation(numberOfDays, fromDate, toDate, ProductId);
        }
    }

    private void showPurchaseConfirmation(int numberOfDays, Date fromDate, Date toDate, String ProductId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.confirm_purchase_title))
                .setMessage(getString(R.string.Youhaveselected)+" " + numberOfDays +" "+ getString(R.string.daysDoyouwanttoproceed))
                .setPositiveButton(getString(R.string.yes), (dialog, which) -> {
                    // Proceed with the purchase
                    Intent i = new Intent(ProductDetailsActivity.this, MakePaymentActivity.class);
                    Log.d("TAG", "showPurchaseConfirmation: " + numberOfDays);
                    i.putExtra("number_of_days", numberOfDays + "");
                    i.putExtra("fromDate", fromDate.getTime());
                    i.putExtra("ProductTypeIsEquipment", ProductTypeIsEquipment);
                    i.putExtra("toDate", toDate.getTime());
                    i.putExtra("product_id", ProductId);
                    startActivity(i);
                })
                .setNegativeButton(getString(R.string.no), (dialog, which) -> dialog.dismiss())
                .show();
    }

    public boolean isRangeOfDateIsFree(Date selectedFromDate, Date selectedToDate) {
        Date today = stripTime(new Date()); // Strip time to consider only the date
        Date availableFromDate = stripTime(ProductData.getAvailableFromDate());
        Date availableToDate = stripTime(ProductData.getAvailableToDate());

        // Check if the selected dates are valid
        boolean isValidRange = !selectedFromDate.before(today) && !selectedToDate.before(today); // Ensure no past dates
        boolean isWithinAvailability = !selectedFromDate.before(availableFromDate) && !selectedToDate.after(availableToDate);

        return isValidRange && isWithinAvailability;
    }

    // Utility method to strip time from a Date object
    private Date stripTime(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}