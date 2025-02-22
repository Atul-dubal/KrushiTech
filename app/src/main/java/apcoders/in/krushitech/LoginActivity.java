package apcoders.in.krushitech;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.firestore.FirebaseFirestore;

import es.dmoral.toasty.Toasty;

public class LoginActivity extends AppCompatActivity {

    CardView continue_with_phone_cardview, register_with_email_cardview;
    TextInputEditText login_email, login_password;
    MaterialButton login_btn;
    FirebaseAuth firebaseAuth;
    TextView forgot_password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);
        firebaseAuth = FirebaseAuth.getInstance();


        continue_with_phone_cardview = findViewById(R.id.continue_with_phone_cardview);
        register_with_email_cardview = findViewById(R.id.register_with_email_cardview);
        login_email = findViewById(R.id.login_email);
        login_password = findViewById(R.id.login_password);
        login_btn = findViewById(R.id.login_btn);
        forgot_password = findViewById(R.id.forgot_password);

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(login_email.getText()) || TextUtils.isEmpty(login_password.getText()) || login_email.getText().toString().isEmpty() || login_password.getText().toString().isEmpty()) {
                    Toasty.error(LoginActivity.this, "Please Fill All fields").show();
                } else {
                    signIn(login_email.getText().toString(), login_password.getText().toString());
                }
            }
        });

        forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = getLayoutInflater().inflate(R.layout.forgot_password_layout, null, false);
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this).setView(view);
                MaterialButton send_link_btn = view.findViewById(R.id.send_link_btn);
                MaterialButton cancle_btn = view.findViewById(R.id.cancle_btn);
                TextInputEditText email = view.findViewById(R.id.forgot_password_email);
                AlertDialog dialog = builder.create();
                dialog.show();
                send_link_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        send_forgot_password_link(email.getText().toString());
                        dialog.dismiss();
                    }
                });
                cancle_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

            }
        });
        continue_with_phone_cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, PhoneLoginActivity.class);
                startActivity(i);
            }
        });
        register_with_email_cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
                i.putExtra("isPhoneVerify", false);
                startActivity(i);
            }
        });
    }

    private void send_forgot_password_link(String email) {
        firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toasty.success(LoginActivity.this, "Password Reset Email Send").show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("TAG", "onFailure: " + e.toString());
            }
        });
    }

    private void signIn(String email, String password) {
        View view = LayoutInflater.from(this).inflate(R.layout.loading_layout, null, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(this).setView(view).setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.show();
        login_btn.setCheckable(false);
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    dialog.dismiss();
                    login_btn.setCheckable(true);

                    String userId = firebaseAuth.getCurrentUser().getUid();

                    // Reference to Firestore
                    FirebaseFirestore db = FirebaseFirestore.getInstance();

                    // Fetch user data from Firestore
                    db.collection("AllUsers").document(userId).get()
                            .addOnSuccessListener(documentSnapshot -> {
                                if (documentSnapshot.exists()) {
                                    // Retrieve the user type from the document
                                    String userType = documentSnapshot.getString("user_type");

                                    // Save user type in SharedPreferences
                                    SharedPreferences sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString("user_type", userType);
                                    editor.apply();
                                    Log.d("onComplete: ", sharedPreferences.getString("user_type", "Null"));
                                    // Start the main activity
                                    Toasty.success(LoginActivity.this, "Login Success", Toasty.LENGTH_LONG).show();
                                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                    finish();
                                } else {
                                    // Handle the case where the user document does not exist
                                    firebaseAuth.signOut();
                                    Toasty.error(LoginActivity.this, "Invalid Credentials ").show();
                                    Log.d("LoginActivity", "User document does not exist.");
                                }
                            })
                            .addOnFailureListener(e -> {
                                // Handle any errors while fetching the user document
                                Log.e("LoginActivity", "Error fetching user data", e);
                            });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    dialog.dismiss();
                    login_btn.setCheckable(true);
                    Toasty.error(LoginActivity.this, "Invalid Credientials").show();
                }
            }
        });
    }
}