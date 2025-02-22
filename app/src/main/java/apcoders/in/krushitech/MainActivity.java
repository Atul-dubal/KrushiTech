package apcoders.in.krushitech;

import static apcoders.in.krushitech.utils.Notification.NotificationCheck;
import static apcoders.in.krushitech.utils.SetLangauge.setLocale;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.ads.nativead.MediaView;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.concurrent.atomic.AtomicReference;

import apcoders.in.krushitech.fragments.HomeFragment;
import apcoders.in.krushitech.fragments.MyOrdersFragment;
import apcoders.in.krushitech.fragments.NearByProductsFragment;
import apcoders.in.krushitech.fragments.NotificationFragment;
import apcoders.in.krushitech.fragments.ProfileFragment;
import apcoders.in.krushitech.fragments.SearchFragment;
import apcoders.in.krushitech.interfaces.FragmentChangeListener;

public class MainActivity extends AppCompatActivity implements FragmentChangeListener {
    private boolean isNotificationAvailable = false;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private LinearLayout drawerAboutUsLayout, terms_and_conditionsLayout, drawerContactUsLayout, drawerHelpLayout, followOnInstagram;
    private BottomNavigationView bottomNavigationView;
    private NavigationView navigationView;
    private FrameLayout frameLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    TextView terms_conditions_1, terms_conditions_2, terms_conditions_3, terms_conditions_4, terms_conditions_5, terms_conditions_6, privacy_policy_1, privacy_policy_2, privacy_policy_3, privacy_policy_4, privacy_policy_5;
    CheckBox checkBox;
    Button btnAccept;
    Button btnDecline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Views
        initializeViews();
        setupToolbar();
        setupDrawer();
        checkTermsAndConditionsStatus();
        // Create Notification Channel
        createNotificationChannel();

        // Display default or specific fragment
        displaySpecificFragment(savedInstanceState);

        // Set Bottom Navigation Listener
        setupBottomNavigation();
        // Check User Location Permission
        getUserLocation();
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

    private void checkTermsAndConditionsStatus() {
        SharedPreferences preferences = getSharedPreferences("UserData", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        boolean isTermsAccepted = preferences.getBoolean("TermsAccepted", false);
        if (!isTermsAccepted) {
            View view = LayoutInflater.from(this).inflate(R.layout.terms_conditions_dialod, null, false);

//        Terms & Conditioned TextViews
            terms_conditions_1 = view.findViewById(R.id.terms_conditions_1);
            terms_conditions_2 = view.findViewById(R.id.terms_conditions_2);
            terms_conditions_3 = view.findViewById(R.id.terms_conditions_3);
            terms_conditions_4 = view.findViewById(R.id.terms_conditions_4);
            terms_conditions_5 = view.findViewById(R.id.terms_conditions_5);
            terms_conditions_6 = view.findViewById(R.id.terms_conditions_6);
//        Privacy TextViews
            privacy_policy_1 = view.findViewById(R.id.privacy_policy_1);
            privacy_policy_2 = view.findViewById(R.id.privacy_policy_2);
            privacy_policy_3 = view.findViewById(R.id.privacy_policy_3);
            privacy_policy_4 = view.findViewById(R.id.privacy_policy_4);
            privacy_policy_5 = view.findViewById(R.id.privacy_policy_5);

//        Checkbox and Buttons
            checkBox = view.findViewById(R.id.checkbox_accept);
            btnAccept = view.findViewById(R.id.btn_accept);
            btnDecline = view.findViewById(R.id.btn_decline);

            terms_conditions_1.setText(Html.fromHtml(getString(R.string.terms_conditions_1), Html.FROM_HTML_MODE_LEGACY));
            terms_conditions_2.setText(Html.fromHtml(getString(R.string.terms_conditions_2), Html.FROM_HTML_MODE_LEGACY));
            terms_conditions_3.setText(Html.fromHtml(getString(R.string.terms_conditions_3), Html.FROM_HTML_MODE_LEGACY));
            terms_conditions_4.setText(Html.fromHtml(getString(R.string.terms_conditions_4), Html.FROM_HTML_MODE_LEGACY));
            terms_conditions_5.setText(Html.fromHtml(getString(R.string.terms_conditions_5), Html.FROM_HTML_MODE_LEGACY));
            terms_conditions_6.setText(Html.fromHtml(getString(R.string.terms_conditions_6), Html.FROM_HTML_MODE_LEGACY));

            privacy_policy_1.setText(Html.fromHtml(getString(R.string.terms_conditions_1), Html.FROM_HTML_MODE_LEGACY));
            privacy_policy_2.setText(Html.fromHtml(getString(R.string.terms_conditions_2), Html.FROM_HTML_MODE_LEGACY));
            privacy_policy_3.setText(Html.fromHtml(getString(R.string.terms_conditions_3), Html.FROM_HTML_MODE_LEGACY));
            privacy_policy_4.setText(Html.fromHtml(getString(R.string.terms_conditions_4), Html.FROM_HTML_MODE_LEGACY));
            privacy_policy_5.setText(Html.fromHtml(getString(R.string.terms_conditions_5), Html.FROM_HTML_MODE_LEGACY));

            AlertDialog.Builder builder = new AlertDialog.Builder(this).setView(view)
                    .setCancelable(false);
            AlertDialog dialog = builder.create();
            dialog.show();

            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                btnAccept.setEnabled(isChecked);
            });

            btnAccept.setOnClickListener(v -> {
                // Handle accept action here
                editor.putBoolean("TermsAccepted", true);
                editor.apply();
                dialog.dismiss();
                Toast.makeText(this, "Terms Accepted", Toast.LENGTH_SHORT).show();
            });

            btnDecline.setOnClickListener(v -> {
                // Handle decline action here
                dialog.dismiss();
                Toast.makeText(this, "Terms Declined", Toast.LENGTH_SHORT).show();
                finish();
            });


        }
    }

    private void initializeViews() {
        frameLayout = findViewById(R.id.framelayout);
        followOnInstagram = findViewById(R.id.followOnInstagram);
        toolbar = findViewById(R.id.toolbar);
        terms_and_conditionsLayout = findViewById(R.id.terms_and_conditionsLayout);
        drawerAboutUsLayout = findViewById(R.id.drawer_about_us_layout);
        drawerContactUsLayout = findViewById(R.id.drawer_contact_us_layout);
        drawerHelpLayout = findViewById(R.id.drawer_help_layout);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        bottomNavigationView = findViewById(R.id.bottom_nav_bar);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);
        actionBarDrawerToggle.getDrawerArrowDrawable().setColor(getColor(R.color.black));
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }
    }

    private void setupDrawer() {
        drawerAboutUsLayout.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, About_Activity.class)));
        drawerContactUsLayout.setOnClickListener(v -> OpenWhatsAppCommunity());
        drawerHelpLayout.setOnClickListener(v -> OpenWhatsAppCommunity());
        terms_and_conditionsLayout.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, TermsConditionsActivity.class)));
        followOnInstagram.setOnClickListener(v -> OpenInstagram());
    }

    private void OpenInstagram() {
        Intent intent = new Intent(Intent.ACTION_VIEW, android.net.Uri.parse("https://www.instagram.com/sadhanseva.app/"));
        startActivity(intent);
    }

    private void OpenWhatsAppCommunity() {
        Intent intent = new Intent(Intent.ACTION_VIEW, android.net.Uri.parse("https://chat.whatsapp.com/HPhugoGZ4Oi8N8vACMS1yv"));
        startActivity(intent);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "OrderNotification";
            CharSequence name = "OrderChannel";
            String description = "Channel for order notifications";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(channelId, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    private void displaySpecificFragment(Bundle savedInstanceState) {
        if (getIntent() != null && savedInstanceState == null) {
            String displayFragment = getIntent().getStringExtra("display_fragment");
            if (displayFragment != null) {
                switch (displayFragment) {
                    case "my_orders":
                        loadFragment(new MyOrdersFragment(), false);
                        toolbar.setTitle(R.string.my_orders);
                        bottomNavigationView.setSelectedItemId(R.id.my_orders);
                        break;
                    case "notification":
                        loadFragment(new NotificationFragment(), false);
                        toolbar.setTitle("Notifications");
                        break;
                    default:
                        loadFragment(new HomeFragment(), true);
                        break;
                }
            } else {
                loadFragment(new HomeFragment(), true);
            }
        }
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            String title = getString(R.string.app_name);

            if (item.getItemId() == R.id.home) {
                selectedFragment = new HomeFragment();
                title = getString(R.string.app_name);
            } else if (item.getItemId() == R.id.profile) {
                selectedFragment = new ProfileFragment();
                title = getString(R.string.profile);
            } else if (item.getItemId() == R.id.search) {
                selectedFragment = new SearchFragment();
                title = getString(R.string.search);
            } else if (item.getItemId() == R.id.my_orders) {
                selectedFragment = new MyOrdersFragment();
                title = getString(R.string.my_orders);
            } else if (item.getItemId() == R.id.nearbyproducts) {
                selectedFragment = new NearByProductsFragment();
                title = "Products Near Me";
            }

            if (selectedFragment != null) {
                loadFragment(selectedFragment, false);
                toolbar.setTitle(title);
            }
            return true;
        });
    }


    private void getUserLocation() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    public void loadFragment(Fragment fragment, boolean isAdd) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (isAdd) {
            transaction.add(R.id.framelayout, fragment);
        } else {
            transaction.replace(R.id.framelayout, fragment);
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.option_menu_items, menu);
        MenuItem item = menu.findItem(R.id.notification);
        FrameLayout badgeLayout = (FrameLayout) getLayoutInflater().inflate(R.layout.menu_badge, null);
        item.setActionView(badgeLayout);
        badgeLayout.setOnClickListener(v -> loadFragment(new NotificationFragment(), false));
        return true;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        displaySpecificFragment(null);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem menuItem = menu.findItem(R.id.notification);
        FrameLayout badgeLayout = (FrameLayout) menuItem.getActionView();
        View badge = badgeLayout.findViewById(R.id.badge);
        badge.setVisibility(isNotificationAvailable ? View.VISIBLE : View.GONE);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onResume() {
        NotificationCheck(this, hasUnreadNotifications -> {
            isNotificationAvailable = hasUnreadNotifications;
            invalidateOptionsMenu();
        });
        super.onResume();
    }
    @Override
    public void changeFragment(Fragment fragment, int navItemId) {
        loadFragment(fragment, false);
        bottomNavigationView.setSelectedItemId(navItemId);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        if (item.getItemId() == R.id.langauges) {
            AtomicReference<String> lang = new AtomicReference<>("");
            final int[] checkedItem = {-1};
            String[] languages = {getResources().getString(R.string.language_marathi), getResources().getString(R.string.language_hindi), getResources().getString(R.string.language_english)};
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getResources().getString(R.string.select_app_language));
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
                setLocale(MainActivity.this, lang.get(), false, true);
                dialog.dismiss();
            });

            builder.setNegativeButton(getResources().getString(R.string.cancel), (dialog, which) -> dialog.dismiss());
            builder.create().show();
        }
        return true;
    }
}
