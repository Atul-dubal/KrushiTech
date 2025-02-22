package apcoders.in.krushitech;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

import apcoders.in.krushitech.models.OrderModel;
import apcoders.in.krushitech.models.ProductModel;
import apcoders.in.krushitech.models.TransactionModel;
import apcoders.in.krushitech.utils.FetchAllProducts;
import apcoders.in.krushitech.utils.Notification;
import apcoders.in.krushitech.utils.WalletManagement;
import es.dmoral.toasty.Toasty;


public class MakePaymentActivity extends AppCompatActivity implements PaymentResultListener {
    AppCompatButton make_payment_btn;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    boolean ProductTypeIsEquipment = false;
    ProductModel ProductData;
    Toolbar toolbar;
    RadioGroup selectPaymentMethodRadioGroup;
    TextView product_name, productQuantity, product_price, product_farmerName;
    String ProductId = "";
    String number_of_days = "1";
    int Quantity = 1;
    long fromDateMillis, toDateMillis;
    Date fromDate, toDate;
    ImageView product_image;
    RadioButton OnlineMethodRadioBtn;

    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private double equipmentPrice = 0.0;
    private final double serviceChargePercentage = 2.5;
    private double totalAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_payment);

        ProductTypeIsEquipment = getIntent().getBooleanExtra("ProductTypeIsEquipment", false);

        selectPaymentMethodRadioGroup = findViewById(R.id.paymentMethodRadioGroup);
        make_payment_btn = findViewById(R.id.make_payment_btn);
        toolbar = findViewById(R.id.toolbar);
        productQuantity = findViewById(R.id.productQuantity);
        product_name = findViewById(R.id.product_name);
        product_price = findViewById(R.id.product_price);
        product_farmerName = findViewById(R.id.ProductFarmerName);
        product_image = findViewById(R.id.product_image);
        firebaseAuth = FirebaseAuth.getInstance();

        setProductQuantityAsPerType();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.MakePayment));

        islogin();

        // Fetch Product Data once and cache it
        FetchAllProducts.FetchByProductId(ProductId, new FetchAllProducts.FetchProductDataCallback() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onCallback(ArrayList<ProductModel> ProductDataList) {
                if (ProductDataList.isEmpty()) {
                    return;  // No product data to proceed
                }

                ProductData = ProductDataList.get(0);
                equipmentPrice = ProductData.getProductPrice();
                double serviceCharge = (serviceChargePercentage / 100) * equipmentPrice * Quantity;
                totalAmount = equipmentPrice * Quantity + serviceCharge;
                totalAmount = Math.round(totalAmount);

                Glide.with(MakePaymentActivity.this).load(ProductData.getProductImagesUrl().get(0)).placeholder(R.drawable.logo).error(R.drawable.chat_bot_img).into(MakePaymentActivity.this.product_image);
                product_name.setText("Name :" + ProductData.getProductName());
                if (ProductTypeIsEquipment) {
                    productQuantity.setText("Quantity : " + number_of_days);
                } else {
                    productQuantity.setText("Quantity : " + Quantity);
                }
                product_price.setText("Price : ₹" + ProductData.getProductPrice());
                product_farmerName.setText("Addresss : " + ProductData.getProductAddress());

                // Adding amount data only once
                setupAmountTable(equipmentPrice, Quantity, serviceCharge, totalAmount);
            }
        });

        Checkout.preload(this);

        make_payment_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userId = firebaseAuth.getCurrentUser().getUid();
                int OnlineMethodRadioBtnId = selectPaymentMethodRadioGroup.getCheckedRadioButtonId();
                OnlineMethodRadioBtn = findViewById(OnlineMethodRadioBtnId);

                // Check network connection before proceeding with the order
                if (!isNetworkAvailable()) {
                    Toasty.info(MakePaymentActivity.this, "No internet connection. Please try again.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // If the user selects "Online Payment"
                Log.d("TAG", "onClick: "+OnlineMethodRadioBtn.getText().toString());
                if (OnlineMethodRadioBtn.getText().toString().equals(getString(R.string.use_online_payment))) {
                    Log.d("onClick: ", OnlineMethodRadioBtn.getText().toString());
                    Toasty.info(MakePaymentActivity.this, "Continue Payment", Toast.LENGTH_SHORT).show();
                    startPayment(totalAmount);

                    // If the user selects "Cash on Delivery" (COD)
                } else if (OnlineMethodRadioBtn.getText().toString().equals(getString(R.string.use_cash_on_delivery))) {
                    if (firebaseAuth.getCurrentUser() == null) {
                        Toasty.error(MakePaymentActivity.this, "Please log in first", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    new AlertDialog.Builder(MakePaymentActivity.this).setMessage(R.string.COD_Alert).setPositiveButton(R.string.yes, (dialog, which) -> {
                        long timestamp = System.currentTimeMillis();
                        String PaymentId = "COD_" + timestamp;
                        OrderModel orderModel = new OrderModel(userId, PaymentId, ProductData.getProductName(), ProductData.getProductId(), Quantity, totalAmount, new Date(), fromDate, toDate, "Pending");
                        saveOrder(orderModel);

                        // Create Transaction object
                        TransactionModel transactionModel = new TransactionModel(PaymentId, PaymentId, userId, ProductData.getProductId(), "COD", totalAmount, new Date(), "Pending");
                        saveTransaction(transactionModel);

//                        WalletManagement.creditToWallet(ProductData.getProductUserId(), PaymentId, equipmentPrice, ProductData.getProductName() + " Purchased By " + userId);
//                        WalletManagement.debitFromWallet(userId, PaymentId, totalAmount, "You Ordered " + ProductData.getProductName());

                        Notification.saveNotification(ProductData.getProductUserId(), firebaseAuth.getCurrentUser().getUid(), PaymentId, "New Order Received", "Your Order is Placed Successfully", "New Order Received Order details: " + orderModel.getOrderId(), "Your Order is Placed Successfully Order Id : " + orderModel.getOrderId(), ProductData.getProductImagesUrl().get(0).toString());
                        startActivity(new Intent(MakePaymentActivity.this, Order_Confirm_Activity.class));
                    }).setNegativeButton(R.string.no, (dialog, which) -> dialog.dismiss()).create().show();

                } else {
                    Toasty.error(MakePaymentActivity.this, "Please Select Payment Method", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setProductQuantityAsPerType() {
        ProductId = getIntent().getStringExtra("product_id");
        assert ProductId != null;
        if (ProductId.isEmpty()) {
            startActivity(new Intent(MakePaymentActivity.this, MainActivity.class));
            finish();
        }
        if (ProductTypeIsEquipment) {
            number_of_days = getIntent().getStringExtra("number_of_days");
            fromDateMillis = getIntent().getLongExtra("fromDate", -1);
            toDateMillis = getIntent().getLongExtra("toDate", -1);

            if (fromDateMillis != -1 && toDateMillis != -1) {
                fromDate = new Date(fromDateMillis); // Convert long to Date
                toDate = new Date(toDateMillis);    // Convert long to Date
                Log.d("TAG", "onCreate: " + ProductId + fromDate.toLocaleString().substring(0, 12) + toDate.toLocaleString().substring(0, 12) + " number of days " + number_of_days);
            } else {
                Toasty.error(MakePaymentActivity.this, "Select Valid Dates", Toast.LENGTH_LONG).show();
                onBackPressed();
            }
            try {
                if (number_of_days != null && !number_of_days.trim().isEmpty()) {
                    Quantity = Integer.parseInt(number_of_days);
                } else {
                    Quantity = 1;
                }
            } catch (NumberFormatException e) {
                Quantity = 1;
            }
        } else {
            Quantity = Integer.parseInt(getIntent().getStringExtra("quantity"));
            fromDate = new Date();
            toDate = new Date();
        }

    }

    private void islogin() {
        if (firebaseAuth.getCurrentUser() == null) {
            startActivity(new Intent(MakePaymentActivity.this, LoginActivity.class));
        }
    }

    private void setupAmountTable(double equipmentPrice, Integer quantity, double serviceCharge, double totalAmount) {
        TableLayout tableLayout = findViewById(R.id.amountTable);
        addRowToTable(tableLayout, getString(R.string.equipmentprice), "₹" + equipmentPrice);
        addRowToTable(tableLayout, getString(R.string.quantity), quantity + "");
        addRowToTable(tableLayout, getString(R.string.ServiceCharge), "₹" + Math.round(serviceCharge));
        addRowToTable(tableLayout, getString(R.string.TotalAmount), "₹" + totalAmount);
    }

    private void addRowToTable(TableLayout tableLayout, String label, String value) {
        TableRow row = new TableRow(this);
        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
        row.setLayoutParams(layoutParams);

        TextView labelText = new TextView(this);
        labelText.setText(label);
        labelText.setPadding(16, 16, 16, 16);
        labelText.setBackgroundResource(R.drawable.cell_border);
        labelText.setTextColor(getResources().getColor(android.R.color.black));
        labelText.setTextSize(16);

        TextView valueText = new TextView(this);
        valueText.setText(value);
        valueText.setPadding(16, 16, 16, 16);
        valueText.setBackgroundResource(R.drawable.cell_border);
        valueText.setTextColor(getResources().getColor(android.R.color.black));
        valueText.setTextSize(16);

        row.addView(labelText);
        row.addView(valueText);

        tableLayout.addView(row);
    }

    private void startPayment(double totalAmount) {
        try {
            JSONObject options = new JSONObject();
            options.put("name", "Sadhanseva");
            options.put("description", "Payment for Product" + ProductData.getProductName() + Objects.requireNonNull(firebaseAuth.getCurrentUser()).getDisplayName());
//            options.put("image", "https://example.com/your-logo.png");
            options.put("currency", "INR");
            options.put("amount", totalAmount * 100); // Convert to paisa

            Checkout checkout = new Checkout();
            checkout.open(MakePaymentActivity.this, options);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void saveOrder(OrderModel orderModel) {
        firebaseFirestore.collection("Orders").document(orderModel.getOrderId()).set(orderModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d("Order Saved", "Order saved successfully!");
                }
            }
        });
    }

    private void saveTransaction(TransactionModel transactionModel) {
        firebaseFirestore.collection("Transactions").document(transactionModel.getTransactionId()).set(transactionModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d("Transaction Saved", "Transaction saved successfully!");
                }
            }
        });
    }

    // Check if the user has internet connectivity
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onPaymentSuccess(String PaymentId) {
        String userId = firebaseAuth.getCurrentUser().getUid();
        Toasty.success(MakePaymentActivity.this, "Payment Successful", Toast.LENGTH_SHORT).show();

        OrderModel orderModel = new OrderModel(userId, PaymentId, ProductData.getProductName(), ProductData.getProductId(), Quantity, totalAmount, new Date(), fromDate, toDate, "Pending");
        saveOrder(orderModel);

        // Create Transaction object
        TransactionModel transactionModel = new TransactionModel(PaymentId, PaymentId, userId, ProductData.getProductId(), "Online", totalAmount, new Date(), "Success");
        saveTransaction(transactionModel);

        WalletManagement.creditToWallet(ProductData.getProductUserId(), PaymentId, equipmentPrice, ProductData.getProductName() + " Purchased By " + userId);
//        WalletManagement.debitFromWallet(userId, PaymentId, totalAmount, "You Ordered " + ProductData.getProductName());

        Notification.saveNotification(ProductData.getProductUserId(), firebaseAuth.getCurrentUser().getUid(), PaymentId, "New Order Received", "Your Order is Placed Successfully", "New Order Received Order details: " + orderModel.getOrderId(), "Your Order is Placed Successfully Order Id : " + orderModel.getOrderId(), ProductData.getProductImagesUrl().get(0).toString());
        startActivity(new Intent(MakePaymentActivity.this, Order_Confirm_Activity.class));
    }

    @Override
    public void onPaymentError(int code, String response) {
        Toasty.error(MakePaymentActivity.this, "Payment Canceled ", Toast.LENGTH_SHORT).show();
    }
}
