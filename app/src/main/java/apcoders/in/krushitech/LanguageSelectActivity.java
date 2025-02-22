package apcoders.in.krushitech;

import static apcoders.in.krushitech.utils.SetLangauge.setLocale;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import es.dmoral.toasty.Toasty;


public class LanguageSelectActivity extends AppCompatActivity {

    CardView marathi_cardview, hindi_cardview, english_cardview;


    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_language_select);

        marathi_cardview = findViewById(R.id.marathi_cardview);
        hindi_cardview = findViewById(R.id.hindi_cardview);
        english_cardview = findViewById(R.id.english_cardview);

        marathi_cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLocale(LanguageSelectActivity.this, "mr", false, false);
                Toasty.success(LanguageSelectActivity.this, "अ\u200Dॅपची भाषा सेट केली: मराठी", Toasty.LENGTH_LONG).show();
                startActivity(new Intent(LanguageSelectActivity.this, LoginActivity.class));
                finish();
            }
        });

        hindi_cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLocale(LanguageSelectActivity.this, "hi", false, false);
                Toasty.success(LanguageSelectActivity.this, "ऐप की भाषा सेट की गई: हिंदी", Toasty.LENGTH_LONG).show();
                startActivity(new Intent(LanguageSelectActivity.this, LoginActivity.class));
                finish();
            }
        });

        english_cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLocale(LanguageSelectActivity.this, "en", false, false);
                Toasty.success(LanguageSelectActivity.this, "App Language Set As : English", Toasty.LENGTH_LONG).show();
                startActivity(new Intent(LanguageSelectActivity.this, LoginActivity.class));
                finish();
            }
        });

    }


}
