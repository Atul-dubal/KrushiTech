package apcoders.in.krushitech.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import apcoders.in.krushitech.R;
import apcoders.in.krushitech.adapters.ProductImageAdapter;
import apcoders.in.krushitech.models.OrderModel;
import apcoders.in.krushitech.models.ProductModel;
import apcoders.in.krushitech.models.TransactionModel;
import apcoders.in.krushitech.utils.FetchAllProducts;
import apcoders.in.krushitech.utils.FetchUserData;
import apcoders.in.krushitech.utils.OrderManagement;
import apcoders.in.krushitech.utils.ProductManagement;
import apcoders.in.krushitech.utils.WalletManagement;
import es.dmoral.toasty.Toasty;

public class OrderSummaryFragment extends Fragment {

    private TextView orderIdTextView, order_cancel, order_timeline, shipping_address, contact_info, productNameTextView, quantity_and_total, statusTextView, OrderDate;
    private FirebaseFirestore db;
    RecyclerView product_images_recyclerView;
    List<Uri> ProductImagesUri = new ArrayList<>();
    WebView webView;
    String userId;
    String orderId;
    FirebaseAuth firebaseAuth;
    int Quantity = 1;
    TableLayout tableLayout;
    private double equipmentPrice = 0.0;
    private double serviceChargePercentage = 2.5;
    private double totalAmount;
    OrderModel OrderData;
    double serviceCharge = 0.0;
    String[] status = {"Select Status", "Pending", "Shipping", "Delivered"};
    String SelectedStatus;

    public OrderSummaryFragment() {
        // Required empty public constructor
    }

    //    @SuppressLint("SetJavaScriptEnabled")
    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_order_summary, container, false);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        userId = firebaseAuth.getCurrentUser().getUid();
        // Initialize UI elements
        tableLayout = rootView.findViewById(R.id.amountTable);
        OrderDate = rootView.findViewById(R.id.OrderDate);
        orderIdTextView = rootView.findViewById(R.id.OrderID);
        order_timeline = rootView.findViewById(R.id.order_timeline);
        order_cancel = rootView.findViewById(R.id.order_cancel);
        shipping_address = rootView.findViewById(R.id.shipping_address);
        productNameTextView = rootView.findViewById(R.id.product_name);
        quantity_and_total = rootView.findViewById(R.id.quantity_and_total);
        statusTextView = rootView.findViewById(R.id.order_status);
        product_images_recyclerView = rootView.findViewById(R.id.product_images_recyclerView);
        webView = rootView.findViewById(R.id.webView);
        contact_info = rootView.findViewById(R.id.contact_info);

        // Enable JavaScript for the WebView
        webView.getSettings().setJavaScriptEnabled(true);

        // Set WebViewClient to ensure the URL is loaded within the WebView
        webView.setWebViewClient(new WebViewClient());

        // Set WebChromeClient for handling JavaScript dialogs and other features
        webView.setWebChromeClient(new WebChromeClient());
        // Initialize RecyclerView
        product_images_recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false));

        // Attach an empty adapter to prevent "No adapter attached" warning
        ProductImageAdapter emptyAdapter = new ProductImageAdapter(requireActivity(), ProductImagesUri);
        product_images_recyclerView.setAdapter(emptyAdapter);

        // Get order ID passed from the activity
        orderId = getArguments() != null ? getArguments().getString("ORDER_ID") : null;
        if (orderId != null) {
            OrderManagement.fetchOrderDetails(orderId, new OrderManagement.FetchOrderByIDCallback() {
                @SuppressLint({"SetTextI18n", "ResourceAsColor"})
                @Override
                public void onCallback(OrderModel OrderDataa) {
                    OrderData = OrderDataa;
                    orderIdTextView.setText("Order ID : " + OrderData.getOrderId());
                    OrderDate.setText("Date : " + OrderData.getOrderDate().toLocaleString().substring(0, 12));
                    productNameTextView.setText(OrderData.getProductName());
                    Quantity = OrderData.getQuantity();
                    quantity_and_total.setText(getString(R.string.quantity) + "- " + OrderData.getQuantity() + " - Total: " + "₹" + OrderData.getTotalAmount());
                    order_timeline.setText("From : " + OrderData.getOrder_ProductFromDate().toLocaleString().substring(0, 12) + " To : " + OrderData.getOrder_ProductToDate().toLocaleString().substring(0, 12));

                    statusTextView.setText("Status : " + OrderData.getOrderStatus());
                    if (OrderData.getOrderStatus().equals("Canceled")) {
                        order_cancel.setVisibility(View.GONE);
                        statusTextView.setBackgroundColor(getResources().getColor(R.color.yellow));
                        statusTextView.setTextColor(getResources().getColor(R.color.red));
                    }
                    totalAmount = OrderData.getTotalAmount();

                    FetchAllProducts.FetchByProductId(OrderData.getProductId(), new FetchAllProducts.FetchProductDataCallback() {
                        @SuppressLint("NotifyDataSetChanged")
                        @Override
                        public void onCallback(ArrayList<ProductModel> ProductDataList) {
                            if (!ProductDataList.isEmpty()) {
                                ProductModel ProductData = ProductDataList.get(0);
                                equipmentPrice = ProductData.getProductPrice();
                                serviceCharge = (serviceChargePercentage / 100) * equipmentPrice;
                                setupAmountTable(equipmentPrice, Quantity, serviceCharge, totalAmount);
                                shipping_address.setText(ProductData.getProductAddress());
                                Log.d("TAG", "onCallback: " + ProductData.getProductName());
                                ProductImagesUri.clear();
                                for (String url : ProductData.getProductImagesUrl()) {
                                    ProductImagesUri.add(Uri.parse(url));
                                }
                                // Notify adapter about data changes
                                Objects.requireNonNull(product_images_recyclerView.getAdapter()).notifyDataSetChanged();

                                FetchUserData.getUserById(ProductData.getProductUserId(), new FetchUserData.GetUserByIdCallback() {
                                    @Override
                                    public void onCallback(String phone_Number) {
                                        Log.d("TAG", "onCallback: " + phone_Number);
                                        contact_info.setText(phone_Number);
                                    }
                                });

                                String mapsUrl = "https://www.google.com/maps?q=" + ProductData.getProductLocation().getLatitude() + "," + ProductData.getProductLocation().getLongitude();

                                // Load the URL into the WebView
                                webView.loadUrl(mapsUrl);
                            } else {
                                Log.e("TAG", "No product data found for the given ID");
                            }
                        }
                    });
                }
            });
        } else {
            Toasty.error(getActivity(), "Order ID not found", Toast.LENGTH_SHORT).show();
        }
        ChangeStatusOfOrder();
        order_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                builder.setTitle(getString(R.string.CancelOrder)).setMessage(getString(R.string.Are_you_sure_you_want_to_cancel_this_order)).setPositiveButton(getString(R.string.yes), (dialog, which) -> cancelOrderAndProcessRefund()).setNegativeButton(getString(R.string.no), ((dialog, which) -> dialog.dismiss())).show();
            }
        });
        return rootView;
    }

    private void ChangeStatusOfOrder() {
        statusTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!"Canceled".equals(OrderData.getOrderStatus())) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                    View view = LayoutInflater.from(requireContext()).inflate(R.layout.change_status_layout, null, false);
                    builder.setView(view);
                    Spinner changeStatusSpinner = view.findViewById(R.id.changeStatusSpinner);
                    Button changeStatusBtn = view.findViewById(R.id.changeStatusBtn);

                    changeStatusBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!SelectedStatus.equals("Select Status")) {
                                ProductManagement.updateProblemStatus(OrderData.getOrderId(), SelectedStatus, new ProductManagement.ProblemStatusUpdateCallback() {
                                    @Override
                                    public void onStatusUpdated(boolean success, String message) {
                                        if (success) {
                                            Toasty.success(requireContext(), "Status updated", Toasty.LENGTH_LONG).show();
                                        } else {
                                            Toasty.success(requireContext(), "Status Not updated", Toasty.LENGTH_LONG).show();
                                        }
                                    }
                                });
                            }
                        }
                    });


                    ArrayAdapter adapter = new ArrayAdapter<>(requireContext(), R.layout.dropdown_layout, status);

                    changeStatusSpinner.setAdapter(adapter);

                    // Optionally, you can set an item selected listener
                    changeStatusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            SelectedStatus = (String) parent.getItemAtPosition(position);
                            // Handle the selected item
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                            // Handle the case when nothing is selected
                            SelectedStatus = "Select Status";
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });

    }

    private void cancelOrderAndProcessRefund() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if (orderId != null && userId != null) {
            // Update the order status to "Canceled"
            db.collection("Orders").document(orderId)
                    .update("orderStatus", "Canceled")
                    .addOnSuccessListener(aVoid -> {
                        Toasty.success(requireContext(), "Order canceled successfully", Toast.LENGTH_SHORT).show();

                        if (orderId.startsWith("pay")) {
                            // Fetch the transaction associated with the order
                            db.collection("Transactions").document(orderId)
                                    .get()
                                    .addOnSuccessListener(documentSnapshot -> {
                                        if (documentSnapshot.exists()) {
                                            // Extract original transaction details
                                            double amount = documentSnapshot.getDouble("amountPaid");
                                            String ProductId = documentSnapshot.getString("productId");
                                            String paymentMethod = documentSnapshot.getString("paymentMethod");
                                            String originalTransactionId = documentSnapshot.getId();
                                            String description = documentSnapshot.getString("description");

                                            // Create a new transaction for the refund
                                            String refundTransactionId = "refund_" + originalTransactionId;
                                            TransactionModel refundTransaction = new TransactionModel(
                                                    refundTransactionId,
                                                    originalTransactionId,
                                                    userId,
                                                    ProductId,
                                                    paymentMethod,
                                                    amount, new Date(),
                                                    "Refunded"
                                            );

                                            // Add the refund transaction
                                            db.collection("Transactions").document(refundTransactionId)
                                                    .set(refundTransaction)
                                                    .addOnSuccessListener(aVoid1 -> {
                                                        Toasty.success(requireContext(), "Refund transaction created", Toast.LENGTH_SHORT).show();

                                                        // Refund the wallet
                                                        WalletManagement.creditToWallet(
                                                                userId,
                                                                refundTransactionId,
                                                                amount,
                                                                description
                                                        );
                                                    })
                                                    .addOnFailureListener(e -> {
                                                        Log.e("CancelOrder", "Failed to create refund transaction: " + e.getMessage());
                                                        Toasty.error(requireContext(), "Failed to create refund transaction", Toast.LENGTH_SHORT).show();
                                                    });
                                        } else {
                                            Toasty.error(requireContext(), "Transaction not found. Refund failed.", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e("CancelOrder", "Failed to fetch transaction: " + e.getMessage());
                                        Toasty.error(requireContext(), "Failed to fetch transaction for refund", Toast.LENGTH_SHORT).show();
                                    });
                        } else {
                            db.collection("Transactions").document(orderId).update("paymentStatus", "canceled").addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toasty.success(requireContext(), "Transaction Status Changed", Toasty.LENGTH_LONG).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("TAG", "onFailure: " + e.getMessage());
                                }
                            });
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("CancelOrder", "Failed to cancel order: " + e.getMessage());
                        Toasty.error(requireContext(), "Failed to cancel order", Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toasty.error(requireContext(), "Invalid Order or User ID", Toast.LENGTH_SHORT).show();
        }
    }


    private void setupAmountTable(double equipmentPrice, int quantity,
                                  double serviceCharge, double totalAmount) {

        addRowToTable(tableLayout, getString(R.string.equipmentprice), "₹" + equipmentPrice);
        addRowToTable(tableLayout, getString(R.string.quantity), quantity + "");
        addRowToTable(tableLayout, getString(R.string.ServiceCharge), "₹" + serviceCharge);
        addRowToTable(tableLayout, getString(R.string.TotalAmount), "₹" + totalAmount);
    }

    private void addRowToTable(TableLayout tableLayout, String label, String value) {
        TableRow row = new TableRow(requireContext());
        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
        row.setLayoutParams(layoutParams);

        TextView labelText = new TextView(requireContext());
        labelText.setText(label);
        labelText.setPadding(16, 16, 16, 16);
        labelText.setBackgroundResource(R.drawable.cell_border);
        labelText.setTextColor(getResources().getColor(android.R.color.black));
        labelText.setTextSize(16);

        TextView valueText = new TextView(requireContext());
        valueText.setText(value);
        valueText.setPadding(16, 16, 16, 16);
        valueText.setBackgroundResource(R.drawable.cell_border);
        valueText.setTextColor(getResources().getColor(android.R.color.black));
        valueText.setTextSize(16);

        row.addView(labelText);
        row.addView(valueText);

        tableLayout.addView(row);
    }

}