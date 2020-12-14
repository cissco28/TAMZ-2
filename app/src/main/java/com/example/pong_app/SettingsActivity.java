package com.example.pong_app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.MultiSelectListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceGroup;
import androidx.preference.SeekBarPreference;

import java.util.Map;

public class SettingsActivity extends AppCompatActivity {

    private SharedPreferences.OnSharedPreferenceChangeListener onSharedPreferenceChangeListener;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }


    public static class SettingsFragment extends PreferenceFragmentCompat  implements SharedPreferences.OnSharedPreferenceChangeListener {
        private SharedPreferences sharedPreferences;

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

        }

        @Override
        public void onPause() {
            sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
            super.onPause();
        }

        @Override
        public void onResume() {
            super.onResume();

            sharedPreferences = getPreferenceManager().getSharedPreferences();

            // we want to watch the preference values' changes
            sharedPreferences.registerOnSharedPreferenceChangeListener(this);

            Map<String, ?> preferencesMap = sharedPreferences.getAll();

            for (Map.Entry<String, ?> preferenceEntry : preferencesMap.entrySet()) {
                if (preferenceEntry instanceof SeekBarPreference) {
                    updateSummary((SeekBarPreference) preferenceEntry);
                }
            }
        }

        private void updateSummary(SeekBarPreference preference) {
            // set the EditTextPreference's summary value to its current text
            preference.setSummary(preference.getValue());
        }

        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            Map<String, ?> preferencesMap = sharedPreferences.getAll();

            // get the preference that has been changed
            Object changedPreference = preferencesMap.get(key);
            // and if it's an instance of EditTextPreference class, update its summary
            if (preferencesMap.get(key) instanceof SeekBarPreference) {
                updateSummary((SeekBarPreference) changedPreference);
            }
        }

    }

    /*
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Map<String, ?> preferencesMap = sharedPreferences.getAll();

        // get the preference that has been changed
        Object changedPreference = preferencesMap.get(key);
        // and if it's an instance of EditTextPreference class, update its summary
        if (preferencesMap.get(key) instanceof SeekBarPreference) {
            updateSummary((SeekBarPreference) changedPreference);
        }
    }

    private void updateSummary(SeekBarPreference preference) {
        // set the EditTextPreference's summary value to its current text
        preference.setSummary(preference.getValue());
    }*/


    @Override
    public void onBackPressed() {
        startActivity(new Intent(this,MainActivity.class));
        finish();
    }
}