package apcoders.in.krushitech;

import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class About_Activity extends AppCompatActivity {
    Toolbar toolbar;
    TextView about_us_text1,about_us_text2,about_us_text3,about_us_text4;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        toolbar = findViewById(R.id.toolbar);
        about_us_text1 = findViewById(R.id.about_us_1);
        about_us_text2 = findViewById(R.id.about_us_2);
        about_us_text3 = findViewById(R.id.about_us_3);
        about_us_text4 = findViewById(R.id.about_us_4);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        about_us_text1.setText(Html.fromHtml(getString(R.string.about_us_1), Html.FROM_HTML_MODE_LEGACY));
        about_us_text2.setText(Html.fromHtml(getString(R.string.about_us_2), Html.FROM_HTML_MODE_LEGACY));
        about_us_text3.setText(Html.fromHtml(getString(R.string.about_us_3), Html.FROM_HTML_MODE_LEGACY));
        about_us_text4.setText(Html.fromHtml(getString(R.string.about_us_4), Html.FROM_HTML_MODE_LEGACY));

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