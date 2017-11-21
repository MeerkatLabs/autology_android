package org.meerkatlabs.autology.settings;

import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.afollestad.materialdialogs.folderselector.FolderChooserDialog;

import org.meerkatlabs.autology.R;

import java.io.File;

public class SettingsActivity extends AppCompatActivity implements FolderChooserDialog.FolderCallback {

    private static final String STORAGE_DIRECTORY_TAG = "__storage_directory";

    // Setting Key Constants
    public static final String STORAGE_DIRECTORY_KEY = "pref_key__storage_directory";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }

    @Override
    public void onFolderSelection(@NonNull FolderChooserDialog dialog, @NonNull File folder) {

        if (dialog.getTag() != null) {
            switch (dialog.getTag()) {
                case STORAGE_DIRECTORY_TAG:

                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
                    SharedPreferences.Editor editor = preferences.edit();

                    editor.putString(STORAGE_DIRECTORY_KEY, folder.getAbsolutePath());
                    editor.apply();
            }
        }
    }

    @Override
    public void onFolderChooserDismissed(@NonNull FolderChooserDialog dialog) {

    }

    public static class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.preferences);
        }

        @Override
        public void onResume() {
            super.onResume();
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            Preference preference = findPreference(STORAGE_DIRECTORY_KEY);
            preference.setSummary(preferences.getString(STORAGE_DIRECTORY_KEY, ""));

            preferences.registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onPause() {
            super.onPause();
            PreferenceManager.getDefaultSharedPreferences(getActivity()).unregisterOnSharedPreferenceChangeListener(this);
        }

        @Override
        public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {

            if (isAdded() && preference.hasKey()) {
                switch (preference.getKey()) {
                    case STORAGE_DIRECTORY_KEY:
                        new FolderChooserDialog.Builder(getActivity())
                                .chooseButton(R.string.md_choose_label)  // changes label of the choose button
                                .initialPath(Environment.getExternalStorageDirectory().getAbsolutePath())  // changes initial path, defaults to external storage directory
                                .tag(STORAGE_DIRECTORY_TAG)
                                .goUpLabel(getString(R.string.settings__parent_dir)) // custom go up label, default label is "..."
                                .allowNewFolder(true, 0)
                                .show(((AppCompatActivity)getActivity()));

                }
            }

            return super.onPreferenceTreeClick(preferenceScreen, preference);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            Preference preference = findPreference(STORAGE_DIRECTORY_KEY);
            preference.setSummary(sharedPreferences.getString(key, ""));
        }
    }
}
