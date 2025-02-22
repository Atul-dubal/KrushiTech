package apcoders.in.krushitech;

import static apcoders.in.krushitech.utils.SetLangauge.setLocale;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.appopen.AppOpenAd;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import apcoders.in.krushitech.models.ProductModel;

public class SplashActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    String user = null;
    ArrayList<ProductModel> EquipmentsProductData = new ArrayList<>();
    ArrayList<ProductModel> FertilizersProductData;
    ArrayList<ProductModel> SeedsProductData;
    boolean app_lang_set = false;
    SharedPreferences sharedPreferences;
    private boolean isAdDisplayed = false;
    private AppOpenAd appOpenAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash);

        sharedPreferences = getSharedPreferences("Settings", MODE_PRIVATE);
        app_lang_set = sharedPreferences.getBoolean("app_lang_set", false);



        firebaseAuth = FirebaseAuth.getInstance();
        try {
            user = firebaseAuth.getCurrentUser().getUid();
        } catch (NullPointerException exception) {
            firebaseAuth.signOut();
            user = null;
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isAdDisplayed) {
                    navigateToMain();
                }
            }
        }, 3000);
    }

    private void navigateToMain() {
        if (app_lang_set) {

            String Storedlang = sharedPreferences.getString("app_lang", "mr");
            setLocale(SplashActivity.this, Storedlang, false, false);

            if (user == null) {
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            } else {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
            }
            finish();
        } else {
            Intent i = new Intent(SplashActivity.this, LanguageSelectActivity.class);
            startActivity(i);
            finish();
        }
    }


    private void showAppOpenAd() {
        if (appOpenAd != null) {
            isAdDisplayed = true;

            // Set the FullScreenContentCallback to handle ad lifecycle events
            appOpenAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdDismissedFullScreenContent() {
                    // Ad dismissed, proceed to main activity
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            navigateToMain();
                        }
                    }, 100);
                }

                @Override
                public void onAdFailedToShowFullScreenContent(com.google.android.gms.ads.AdError adError) {
                    // Handle the failure to display the ad
                    navigateToMain();
                }

                @Override
                public void onAdShowedFullScreenContent() {
                    // Ad is being shown
                    appOpenAd = null; // Clear the reference to prevent reuse
                }
            });

            // Show the ad
            appOpenAd.show(this);
        } else {
            navigateToMain(); // Proceed if the ad isn't available
        }
    }
}