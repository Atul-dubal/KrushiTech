package apcoders.in.krushitech;

import static apcoders.in.krushitech.utils.SetLangauge.setLocale;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.nativead.MediaView;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import apcoders.in.krushitech.adapters.SettingsAdapter;
import apcoders.in.krushitech.models.SettingOption;
import apcoders.in.krushitech.utils.FetchUserData;
import es.dmoral.toasty.Toasty;


public class SettingsActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    RecyclerView General_settingsRecyclerView, Account_settingsRecyclerView;
    Toolbar toolbar;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String userId = user.getUid();
    String userTypeValue = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        firebaseAuth = FirebaseAuth.getInstance();
        General_settingsRecyclerView = findViewById(R.id.General_settingsRecyclerView);
        Account_settingsRecyclerView = findViewById(R.id.Account_settingsRecyclerView);

        setGeneralOptions();
        setAccountOptions();


    }

    private void populateNativeAdView(NativeAd nativeAd, NativeAdView adView) {
        // Set the media content
        MediaView mediaView = adView.findViewById(R.id.ad_media);
        adView.setMediaView(mediaView);
        mediaView.setMediaContent(nativeAd.getMediaContent());

        // Set the headline
        TextView headlineView = adView.findViewById(R.id.ad_headline);
        headlineView.setText(nativeAd.getHeadline());
        adView.setHeadlineView(headlineView);

        // Call this method to complete population
        adView.setNativeAd(nativeAd);
    }
    private void setAccountOptions() {
        Account_settingsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<SettingOption> settings = new ArrayList<>();
        settings.add(new SettingOption(getResources().getString(R.string.delete_account), R.drawable.ic_delete, () -> {
            deleteUserAccount(user);
        }));

        settings.add(new SettingOption(getResources().getString(R.string.logout), R.drawable.baseline_logout_24, () -> {
            new AlertDialog.Builder(SettingsActivity.this).setMessage(getResources().getString(R.string.are_you_sure_logout)).setPositiveButton(getResources().getString(R.string.yes), (dialog, which) -> {
                firebaseAuth.signOut();
                startActivity(new Intent(SettingsActivity.this, LoginActivity.class));
                finish();
            }).setNegativeButton(getResources().getString(R.string.no), (dialog, which) -> dialog.dismiss()).create().show();
        }));

        SettingsAdapter adapter = new SettingsAdapter(settings);
        Account_settingsRecyclerView.setAdapter(adapter);
    }

    private void deleteUserDataFromFirestore(String userId) {
        try {
            SharedPreferences sharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE);
            userTypeValue = sharedPreferences.getString("user_type", "");

            if (userTypeValue.equals("farmer")) {
                db.collection("Farmers").document(userId).delete(); // Example: delete user's profile data
            } else {
                db.collection("Vendors").document(userId).delete(); // Example: delete user's profile data
            }
        } catch (Exception | Error e) {
            Log.d("TAG", "deleteUserDataFromFirestore: " + e.getMessage());
        }

        // Delete documents from the collections related to the user (e.g., profile, orders, etc.)
        db.collection("AllUsers").document(userId).delete(); // Example: delete user's profile data

        // You can add more collection deletions if needed
        db.collection("Orders").whereEqualTo("userId", userId).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        documentSnapshot.getReference().delete(); // Delete orders related to the user
                    }
                });

        db.collection("Transactions").whereEqualTo("userId", userId).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        documentSnapshot.getReference().delete(); // Delete transactions related to the user
                    }
                });
        db.collection("Notifications").whereEqualTo("ProductSenderId", userId).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        documentSnapshot.getReference().delete(); // Delete transactions related to the user
                    }
                });
        db.collection("Notifications").whereEqualTo("ProductReceiverId", userId).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        documentSnapshot.getReference().delete(); // Delete transactions related to the user
                    }
                });

        db.collection("Products").whereEqualTo("productUserId", userId).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        documentSnapshot.getReference().delete(); // Delete transactions related to the user
                    }
                });
    }

    private void deleteUserAccount(FirebaseUser user) {
        View view = getLayoutInflater().inflate(R.layout.delete_account_login, null, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this).setView(view);
        MaterialButton send_link_btn = view.findViewById(R.id.send_link_btn);
        MaterialButton cancle_btn = view.findViewById(R.id.cancle_btn);
        TextInputEditText password = view.findViewById(R.id.forgot_password_email);
        AlertDialog dialog = builder.create();
        dialog.show();


        send_link_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (TextUtils.isEmpty(password.getText().toString())) {
                    Toasty.error(SettingsActivity.this, "Please Enter Password", Toasty.LENGTH_LONG).show();
                } else {
                    FetchUserData.getUserEmailById(firebaseAuth.getCurrentUser().getUid(), new FetchUserData.GetUserEmailByIdCallback() {
                        @Override
                        public void onCallback(String Email) {
                            Log.d("TAG", "onCallback: " + Email + "  " + password.getText().toString());
                            firebaseAuth.signInWithEmailAndPassword(Email, password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> atask) {

                                    deleteUserDataFromFirestore(userId);

                                    user.delete()
                                            .addOnCompleteListener(task -> {
                                                if (task.isSuccessful()) {
                                                    // Account deleted successfully
                                                    Log.d("DeleteAccount", "User account deleted.");
                                                    // Optionally, you can log the user out or navigate to a different activity
                                                    FirebaseAuth.getInstance().signOut();
                                                    startActivity(new Intent(SettingsActivity.this, LoginActivity.class));
                                                    finish();
                                                    // Redirect user to login screen or show a confirmation
                                                } else {
                                                    // Handle errors, e.g., user cannot delete account if they are not authenticated
                                                    Log.e("DeleteAccount", "Error deleting account: " + task.getException().getMessage());
                                                }
                                            });
                                    dialog.dismiss();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("TAG", "onFailure: " + e.getMessage());
                                }
                            });
                        }
                    });

                }

            }
        });
        cancle_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

    }


    private void setGeneralOptions() {
        General_settingsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<SettingOption> settings = new ArrayList<>();
        settings.add(new SettingOption(getResources().getString(R.string.change_language), R.drawable.multi_lang_selection_img, () -> {

            AtomicReference<String> lang = new AtomicReference<>("");
            final int[] checkedItem = {-1};
            String[] languages = {getResources().getString(R.string.language_marathi), getResources().getString(R.string.language_hindi), getResources().getString(R.string.language_english)};
            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
            builder.setTitle(getResources().getString(R.string.select_language));
            builder.setSingleChoiceItems(languages, checkedItem[0], (dialog, which) -> {
                if (languages[which].equals("Marathi")) {
                    lang.set("mr");
                } else if (languages[which].equals("Hindi")) {
                    lang.set("hi");
                } else {
                    lang.set("en");
                }
            });
            builder.setPositiveButton(getResources().getString(R.string.change_language), (dialog, which) -> {
                setLocale(SettingsActivity.this, lang.get(), false, true);
                dialog.dismiss();
            });

            builder.setNegativeButton(getResources().getString(R.string.cancel), (dialog, which) -> dialog.dismiss());
            builder.create().show();
        }));
        settings.add(new SettingOption(getResources().getString(R.string.help), R.drawable.help, this::OpenWhatsAppCommunity));
        settings.add(new SettingOption(getResources().getString(R.string.contact_us), R.drawable.contact, this::OpenWhatsAppCommunity));
        settings.add(new SettingOption(getResources().getString(R.string.about_us), R.drawable.profile, () -> {
            startActivity(new Intent(this, About_Activity.class));
        }));

        SettingsAdapter adapter = new SettingsAdapter(settings);
        General_settingsRecyclerView.setAdapter(adapter);

    }

    private void OpenWhatsAppCommunity() {
        Intent intent = new Intent(Intent.ACTION_VIEW, android.net.Uri.parse("https://chat.whatsapp.com/HPhugoGZ4Oi8N8vACMS1yv"));
        startActivity(intent);
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
