package apcoders.in.krushitech;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import apcoders.in.krushitech.models.User_Farmer_Model;
import apcoders.in.krushitech.models.VendorsModel;
import es.dmoral.toasty.Toasty;

public class OTP_verification_Activity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    CollectionReference collectionReference;
    TextInputEditText otp_edittext;
    long TimeOut = 60L;
    TextView resend_text;
    String verification_code;
    Button verify_otp_btn;
    PhoneAuthProvider.ForceResendingToken resendingToken;
    String phone_Number;
    ProgressBar otp_verify_progress_bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verification);
        otp_edittext = findViewById(R.id.otp_edittext);
        verify_otp_btn = findViewById(R.id.verify_otp_btn);
        resend_text = findViewById(R.id.resend_text);
        otp_verify_progress_bar = findViewById(R.id.otp_verify_progress_bar);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        collectionReference = firebaseFirestore.collection("AllUsers");
        phone_Number = getIntent().getStringExtra("phone_number");


        sendOTP(phone_Number, false);
        verify_otp_btn.setEnabled(true);
        verify_otp_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent i = new Intent(OTP_verification_Activity.this, RegisterActivity.class);
//                i.putExtra("phone_number", phone_Number);
//                i.putExtra("isPhoneVerify",true);
//                startActivity(i);
                verify_otp_btn.setEnabled(false);
                String opt_edittext_data = otp_edittext.getText().toString();
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verification_code, opt_edittext_data);
                signIn(credential);
            }
        });

        resend_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verify_otp_btn.setEnabled(false);
                sendOTP(phone_Number, true);
            }
        });
    }

    private void sendOTP(String phone_number, boolean isResend) {
        otp_verify_progress_bar.setVisibility(View.VISIBLE);
        startResendTimer();
//        Toast.makeText(this, phone_number + isResend, Toast.LENGTH_SHORT).show();
        PhoneAuthOptions.Builder options =
                PhoneAuthOptions.newBuilder(firebaseAuth)
                        .setPhoneNumber(phone_number)       // Phone number to verify
                        .setTimeout(TimeOut, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // (optional) Activity for callback binding
                        // If no activity is passed, reCAPTCHA verification can not be used.
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                verify_otp_btn.setEnabled(true);
                                signIn(phoneAuthCredential);
                                Toast.makeText(OTP_verification_Activity.this, " Verification Success", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                Toast.makeText(OTP_verification_Activity.this, "Verification Failed", Toast.LENGTH_SHORT).show();
                                otp_verify_progress_bar.setVisibility(View.GONE);
                            }

                            @Override
                            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                super.onCodeSent(s, forceResendingToken);
                                verify_otp_btn.setEnabled(true);
                                verification_code = s;
                                resendingToken = forceResendingToken;
                                Toasty.success(OTP_verification_Activity.this, "OTP Sent Successfully", Toast.LENGTH_SHORT).show();
                            }
                        });
        if (isResend) {
            PhoneAuthProvider.verifyPhoneNumber(options.setForceResendingToken(resendingToken).build());
        } else {
            PhoneAuthProvider.verifyPhoneNumber(options.build());

        }
    }

    private void startResendTimer() {
        resend_text.setEnabled(false);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                TimeOut--;
                resend_text.setText("Resend OTP in " + TimeOut + " Seconds");
                if (TimeOut <= 0) {
                    TimeOut = 60L;
                    resend_text.setText("Resend OTP");
                    timer.cancel();
                    runOnUiThread(() -> {
                        resend_text.setEnabled(true);
                    });
                }
            }
        }, 0, 1000);

    }

    private void signIn(PhoneAuthCredential phoneAuthCredential) {
        firebaseAuth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    otp_edittext.setText("");
                    Toasty.success(OTP_verification_Activity.this, "OTP verified", Toast.LENGTH_SHORT).show();
                    collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                try {
//                                    For Farmer
                                    List<User_Farmer_Model> users = new ArrayList<>();
                                    for (DocumentSnapshot document : task.getResult()
                                    ) {
                                        User_Farmer_Model user = document.toObject(User_Farmer_Model.class);
                                        assert user != null;
                                        if (Objects.equals(user.getPhone_number(), Objects.requireNonNull(firebaseAuth.getCurrentUser()).getPhoneNumber())) {
                                            users.add(user);
                                        }
                                    }
                                    if (users.isEmpty()) {
                                        Intent i = new Intent(OTP_verification_Activity.this, RegisterActivity.class);
                                        i.putExtra("phone_number", phone_Number);
                                        i.putExtra("isPhoneVerify", true);
                                        startActivity(i);
                                    } else {
                                        startActivity(new Intent(OTP_verification_Activity.this, MainActivity.class));
                                    }
                                    Log.d("TAG", "onComplete: " + users);
                                } catch (Exception | Error error) {
//                                    For Vendor
                                    List<VendorsModel> vendors = new ArrayList<>();
                                    for (DocumentSnapshot document : task.getResult()
                                    ) {
                                        VendorsModel vendor = document.toObject(VendorsModel.class);
                                        assert vendor != null;
                                        if (Objects.equals(vendor.getPhoneNumber(), Objects.requireNonNull(firebaseAuth.getCurrentUser()).getPhoneNumber())) {
                                            vendors.add(vendor);
                                        }
                                    }
                                    if (vendors.isEmpty()) {
                                        Intent i = new Intent(OTP_verification_Activity.this, RegisterActivity.class);
                                        i.putExtra("phone_number", phone_Number);
                                        i.putExtra("isPhoneVerify", true);
                                        startActivity(i);
                                    } else {
                                        startActivity(new Intent(OTP_verification_Activity.this, MainActivity.class));
                                    }
                                }

                            } else {
                                Log.d("RegisterActivity", "Failed to get documents", task.getException());
                            }
                            otp_verify_progress_bar.setVisibility(View.GONE);
                        }
                    });

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                otp_verify_progress_bar.setVisibility(View.GONE);
                verify_otp_btn.setEnabled(true);
                Toasty.error(OTP_verification_Activity.this, "OTP Verification Failed", Toast.LENGTH_SHORT).show();
            }
        });

    }


}