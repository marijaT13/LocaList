package com.example.localist.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;

import java.util.Locale;

    public class LocaleHelper {

        private static final String SELECTED_LANGUAGE = "Locale.Helper.Selected.Language";

        public static Context setLocale(Context context, String language) {
            persistLanguage(context, language);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                return updateResources(context, language);
            }

            return updateResourcesLegacy(context, language);
        }

        public static String getPersistedLanguage(Context context) {
            SharedPreferences prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
            return prefs.getString(SELECTED_LANGUAGE, "en");
        }

        private static void persistLanguage(Context context, String language) {
            SharedPreferences prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(SELECTED_LANGUAGE, language);
            editor.apply();
        }

        private static Context updateResources(Context context, String language) {
            Locale locale = new Locale(language);
            Locale.setDefault(locale);

            Configuration config = new Configuration(context.getResources().getConfiguration());
            config.setLocale(locale);
            config.setLayoutDirection(locale);

            return context.createConfigurationContext(config);
        }

        @SuppressWarnings("deprecation")
        private static Context updateResourcesLegacy(Context context, String language) {
            Locale locale = new Locale(language);
            Locale.setDefault(locale);

            Configuration config = context.getResources().getConfiguration();
            config.locale = locale;
            config.setLayoutDirection(locale);

            context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());

            return context;
        }
    }

