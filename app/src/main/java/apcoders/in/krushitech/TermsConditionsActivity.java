package apcoders.in.krushitech;

import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class TermsConditionsActivity extends AppCompatActivity {
    Toolbar toolbar;
    TextView terms_conditions_1, terms_conditions_2, terms_conditions_3, terms_conditions_4, terms_conditions_5, terms_conditions_6, privacy_policy_1, privacy_policy_2, privacy_policy_3, privacy_policy_4, privacy_policy_5;
    CheckBox checkBox;
    Button btnAccept;
    Button btnDecline;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_terms_conditions);
        toolbar = findViewById(R.id.toolbar);

//        Share preference
//        SharedPreferences preferences = getSharedPreferences("UserData", MODE_PRIVATE);
//        SharedPreferences.Editor editor = preferences.edit();

//        Terms & Conditioned TextViews
        terms_conditions_1 = findViewById(R.id.terms_conditions_1);
        terms_conditions_2 = findViewById(R.id.terms_conditions_2);
        terms_conditions_3 = findViewById(R.id.terms_conditions_3);
        terms_conditions_4 = findViewById(R.id.terms_conditions_4);
        terms_conditions_5 = findViewById(R.id.terms_conditions_5);
        terms_conditions_6 = findViewById(R.id.terms_conditions_6);
//        Privacy TextViews
        privacy_policy_1 = findViewById(R.id.privacy_policy_1);
        privacy_policy_2 = findViewById(R.id.privacy_policy_2);
        privacy_policy_3 = findViewById(R.id.privacy_policy_3);
        privacy_policy_4 = findViewById(R.id.privacy_policy_4);
        privacy_policy_5 = findViewById(R.id.privacy_policy_5);

//        Checkbox and Buttons
//        checkBox = findViewById(R.id.checkbox_accept);
//        btnAccept = findViewById(R.id.btn_accept);
//        btnDecline = findViewById(R.id.btn_decline);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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