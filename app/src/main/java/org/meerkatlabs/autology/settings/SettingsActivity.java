package org.meerkatlabs.autology.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.afollestad.materialdialogs.folderselector.FolderChooserDialog;

import org.meerkatlabs.autology.R;

import java.io.File;

// TODO: Request location fine permission in order to allow the logs to be tagged with the location
// values.  Then will use a similar functionality to the external storage permissions functionality
// from the Main Activity.

public class SettingsActivity extends AppCompatActivity implements FolderChooserDialog.FolderCallback {

    private static final String STORAGE_DIRECTORY_TAG = "__storage_directory";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar ab = getSupportActionBar();
        ab.setTitle(R.string.settings__title);
        ab.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onFolderSelection(@NonNull FolderChooserDialog dialog, @NonNull File folder) {

        if (dialog.getTag() != null) {
            switch (dialog.getTag()) {
                case STORAGE_DIRECTORY_TAG:

                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
                    SharedPreferences.Editor editor = preferences.edit();

                    editor.putString(getString(R.string.pref_key__storage_directory), folder.getAbsolutePath());
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
            Preference preference = findPreference(getString(R.string.pref_key__storage_directory));
            preference.setSummary(preferences.getString(getString(R.string.pref_key__storage_directory), ""));

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

                if (getString(R.string.pref_key__storage_directory).equals(preference.getKey())) {
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
            Preference preference = findPreference(getString(R.string.pref_key__storage_directory));
            preference.setSummary(sharedPreferences.getString(key, ""));
        }
    }
}
