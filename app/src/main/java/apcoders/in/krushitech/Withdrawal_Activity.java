package apcoders.in.krushitech;


import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

import apcoders.in.krushitech.adapters.WithdrawalRequestAdapter;
import apcoders.in.krushitech.models.Withdrawal_Model;
import apcoders.in.krushitech.utils.WalletManagement;
import apcoders.in.krushitech.utils.WithdrawManagement;
import es.dmoral.toasty.Toasty;

public class Withdrawal_Activity extends AppCompatActivity {
    private TextInputEditText withdrawal_upi_id, withdrawal_amount;
    private MaterialButton withdrawBtn;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    TextView my_wallet_balance;
    double userBalance;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    Toolbar toolbar;
    LinearLayout request_layout;
    RecyclerView withdrawal_recyclerview;
    String UserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_withdrawal);

        // Initialize Firebase
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        UserId = firebaseAuth.getCurrentUser().getUid();

        // Initialize views
        request_layout = findViewById(R.id.request_layout);
        withdrawal_upi_id = findViewById(R.id.withdrawal_upi_id);
        withdrawal_amount = findViewById(R.id.withdrawal_amount);
        withdrawBtn = findViewById(R.id.withdrawBtn);
        toolbar = findViewById(R.id.toolbar);
        withdrawal_recyclerview = findViewById(R.id.withdrawal_recyclerview);
        my_wallet_balance = findViewById(R.id.my_wallet_balance);

        setupToolbar();

        showAllWithdrawalRequests();
        WalletManagement.getBalance(UserId, new WalletManagement.OnBalanceRetrievedListener() {
            @Override
            public void onBalanceRetrieved(Double balance) {
                my_wallet_balance.setText("Balance : " + balance);
                userBalance = balance;
                withdrawBtn.setCheckable(true);
            }
        });

        withdrawBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String upiId = withdrawal_upi_id.getText().toString().trim();
                String amountString = withdrawal_amount.getText().toString().trim();

                if (TextUtils.isEmpty(upiId)) {
                    Toasty.error(Withdrawal_Activity.this, "Please enter UPI ID", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(amountString)) {
                    Toasty.error(Withdrawal_Activity.this, "Please enter amount", Toast.LENGTH_SHORT).show();
                    return;
                }

                double amount = Double.parseDouble(amountString);

                // Validate amount
                // Assume `userBalance` is fetched from Firestore (you can add logic to fetch it)

                if (amount <= 0 || amount > userBalance) {
                    Toasty.error(Withdrawal_Activity.this, "Enter a valid amount (<= balance)", Toast.LENGTH_SHORT).show();
                    return;
                }

                submitWithdrawalRequest(upiId, amount);
            }
        });


    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }
    }

    private void showAllWithdrawalRequests() {
        withdrawal_recyclerview.setLayoutManager(new LinearLayoutManager(Withdrawal_Activity.this));
        WithdrawManagement.FetchWithdrawalRequests(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid(), new WithdrawManagement.WithdrawalRequestListener() {
            @Override
            public void onWithdrawalRequestsFetched(ArrayList<Withdrawal_Model> withdrawalRequests) {
                if (withdrawalRequests != null) {
                    Log.d("TAG", "onWithdrawalRequestsFetched: " + withdrawalRequests.size());
                    request_layout.setVisibility(View.VISIBLE);
                    WithdrawalRequestAdapter adapter = new WithdrawalRequestAdapter(withdrawalRequests);
                    withdrawal_recyclerview.setAdapter(adapter);
                }

            }
        });


    }

    private void submitWithdrawalRequest(String upiId, double amount) {
        String userId = firebaseAuth.getCurrentUser().getUid();
        String requestId = UUID.randomUUID().toString();


        Withdrawal_Model withdrawalRequest = new Withdrawal_Model(userId, upiId, amount, new Date(), "Wait 2-3 working days.", "Pending", requestId);

        firebaseFirestore.collection("Withdrawal")
                .document(requestId)
                .set(withdrawalRequest)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toasty.success(Withdrawal_Activity.this, "Withdrawal request submitted", Toast.LENGTH_SHORT).show();
                            // Clear input fields
                            withdrawal_upi_id.setText("");
                            withdrawal_amount.setText("");
                            showAllWithdrawalRequests();
                        } else {
                            Toasty.error(Withdrawal_Activity.this, "Failed to submit request", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
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