package apcoders.in.krushitech.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Handler;

import com.jakewharton.processphoenix.ProcessPhoenix;

import java.util.Locale;

import es.dmoral.toasty.Toasty;

public class SetLangauge {

    public static void setLocale(Context context, String lang, boolean ischeck, boolean isRequiredRestart) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("Settings", Context.MODE_PRIVATE);

        if (ischeck) {
            boolean app_lang_set = sharedPreferences.getBoolean("app_lang_set", false);
            if (app_lang_set) {
                String Storedlang = sharedPreferences.getString("app_lang", "NotSet");
                if (!Storedlang.equals("NotSet")) {
                    setLocale(context, Storedlang, true, isRequiredRestart);
                } else {
                    setLocale(context, "mr", true, isRequiredRestart);
                }
            }
        } else {
            Locale locale = new Locale(lang);
            Locale.setDefault(locale);
            Resources resources = context.getResources();
            Configuration config = resources.getConfiguration();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                config.setLocale(locale);
                context = context.createConfigurationContext(config);
            } else {
                config.locale = locale;
                resources.updateConfiguration(config, resources.getDisplayMetrics());
            }
        }
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("app_lang", lang);
        editor.putBoolean("app_lang_set", true);
//        Toast.makeText(context, "lang" + lang, Toast.LENGTH_SHORT).show();
        editor.apply();
        if (isRequiredRestart) {
            Toasty.
                    success(context, "App Restart in 5 seconds", Toasty.LENGTH_LONG).

                    show();

            Context finalContext = context;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    ProcessPhoenix.triggerRebirth(finalContext);
                }
            }, 3000);
        }
    }
}
