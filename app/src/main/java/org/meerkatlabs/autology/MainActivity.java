package org.meerkatlabs.autology;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialogFragment;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.simplelist.MaterialSimpleListAdapter;
import com.afollestad.materialdialogs.simplelist.MaterialSimpleListItem;

import org.meerkatlabs.autology.permissions.StoragePermissionFragment;
import org.meerkatlabs.autology.settings.SettingsActivity;
import org.meerkatlabs.autology.utilities.logs.LogEntry;
import org.meerkatlabs.autology.utilities.logs.LogProvider;
import org.meerkatlabs.autology.utilities.templates.BaseTemplate;
import org.meerkatlabs.autology.utilities.templates.TemplateProvider;

import java.io.File;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements LogProvider.ILogProvider, DatePickerDialog.OnDateSetListener, LogEntry.ILogEntryEditor,
        MaterialSimpleListAdapter.Callback {

    private Fragment currentFragment = null;
    private LogProvider provider;
    private Calendar currentDate = Calendar.getInstance();
    private LogEntry currentlyEditingFile = null;
    private byte[] currentHash = null;

    private static final int EXTERNAL_STORAGE_REQUEST = 1;
    private static final String EDITING_ENTRY_URI_KEY = "editing_entry_uri";
    private static final String EDITING_ENTRY_HASH_KEY = "editing_entry_hash";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Load up the preferences from the system
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        if (savedInstanceState != null) {
            // Check to see if left because editing a file, and if so, then finish editing the file
            Uri currentlyEditing = savedInstanceState.getParcelable(EDITING_ENTRY_URI_KEY);
            if (currentlyEditing != null) {
                File logFile = new File(currentlyEditing.getPath());
                currentlyEditingFile = LogEntry.loadLogEntry(logFile);
            }

            currentHash = savedInstanceState.getByteArray(EDITING_ENTRY_HASH_KEY);
        }

        // TODO: Check the status of the file system configured
        // If the value is not provided for the storage directory, then create the value by appending
        // autology to the getExternalDirectory().

        // TODO: When the directory structure is changed, then need to reload the content of the
        // list panel

        // TODO: Storage Engine for the application, responsible for providing the contents of
        // the directory log, adding/removing files from the log

        // TODO: Move the STORAGE_DIRECTORY_KEY value to a new location, or provide a means of
        // abstracting that content away so that the key isn't known to the other acivities.

        // TODO: Would be interesting to fall back to internal storage so that the app is usable
        // out of the box, and then when external/internal is selected, it will copy the content
        // over to the other mode.

    }

    @Override
    protected void onResume() {
        super.onResume();
        // Check permissions and create the main view when required permissions have been approved
        createView();

        if (currentlyEditingFile != null) {

            byte[] newHash = currentlyEditingFile.calculateHash();

            if (!Arrays.equals(newHash, currentHash)) {
                Map<String, Object> frontMatter = currentlyEditingFile.getFrontmatter();
                BaseTemplate template = new BaseTemplate();
                template.post(frontMatter);
                currentlyEditingFile.writeFile();
            }

            currentlyEditingFile = null;
            currentHash = null;

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (currentFragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .remove(currentFragment).commit();
            currentFragment = null;
        }
    }

    private void createView() {
        if (checkStoragePermissions()) {
            // Can load up the list view fragment here
            currentFragment = LogListFragment.createFragment(Calendar.getInstance());
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.main_fragment_container, currentFragment).commit();

            // Load up the on click listener for the fab button
            // TODO: The visibility functionality should be removed when the permission is broken
            // out to it's own activity.
            FloatingActionButton newNote = findViewById(R.id.log_list__new_note);
            newNote.setVisibility(View.VISIBLE);
            newNote.setOnClickListener(newNoteListener);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId())
        {
            case R.id.action_settings:

                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;

            case R.id.action_select_date:

                AppCompatDialogFragment newFragment = DatePickerFragment.createInstance(currentDate);
                newFragment.show(getSupportFragmentManager(), "datePicker");
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private boolean checkStoragePermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }

        // Going to load up the storage permission fragment and ask for permissions
        // TODO: Should open up this fragment in a new activity instead of as a fragment on this display, the whole functionality
        // should be moved to a new activity.
        currentFragment = new StoragePermissionFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.main_fragment_container, currentFragment).commit();

        return false;
    }

    public void storagePermissionClickHandler(View button) {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case EXTERNAL_STORAGE_REQUEST:
                getSupportFragmentManager().beginTransaction()
                        .remove(currentFragment).commit();
                currentFragment = null;
                createView();
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public LogProvider getProvider() {
        if (provider == null) {
            provider = new LogProvider(this);
            provider.initialize();
        }
        return provider;
    }

    FloatingActionButton.OnClickListener newNoteListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            final TemplateProvider provider = new TemplateProvider();

            final MaterialSimpleListAdapter adapter = new MaterialSimpleListAdapter(MainActivity.this);

            for (BaseTemplate template : provider.getTemplates()) {
                adapter.add(new MaterialSimpleListItem.Builder(MainActivity.this)
                        .content(getString(template.getNameResource()))
                        .tag(template)
                        .build());
            }

            new MaterialDialog.Builder(MainActivity.this)
                    .title("Select Template")
                    .adapter(adapter, null)
                    .show();
        }
    };

    public void editLogEntry(@NonNull LogEntry logEntry) {

        Uri uri = Uri.fromFile(logEntry.getLogFile());
        String mimeType = "text/markdown";

        currentlyEditingFile = logEntry;
        currentHash = currentlyEditingFile.calculateHash();

        // TODO: Currently using the view intent, because Markor 1.5 is broken when
        // using the edit intent.  And this is the only version available in FDroid.
        // TODO: Make this a configuration option
        Intent viewIntent = new Intent(Intent.ACTION_VIEW);
        viewIntent.setDataAndType(uri, mimeType);
        Intent chooserIntent = Intent.createChooser(viewIntent, getString(R.string.action_editor_selection));
        startActivity(chooserIntent);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        // Can load up the list view fragment here
        currentDate.set(year, month, dayOfMonth);
        currentFragment = LogListFragment.createFragment(currentDate);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_fragment_container, currentFragment).commit();
    }

    @Override
    public void onMaterialListItemSelected(MaterialDialog dialog, int index, MaterialSimpleListItem item) {

        final LogProvider lp = getProvider();
        // Have the log provider create a new log file of that template, and then provide
        // it to the editor for manipulation.
        BaseTemplate t = (BaseTemplate) item.getTag();
        dialog.dismiss();

        // Need to create a new calendar based on the value in current date, but need to set the
        // HH:MM:SS to the current date and time value.

        Calendar entryCalendar = Calendar.getInstance();
        entryCalendar.set(currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DAY_OF_MONTH));

        LogEntry logEntry = lp.createNewLogFile(entryCalendar, t);
        editLogEntry(logEntry);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (currentlyEditingFile != null) {
            outState.putParcelable(EDITING_ENTRY_URI_KEY, Uri.fromFile(currentlyEditingFile.getLogFile()));
            outState.putByteArray(EDITING_ENTRY_HASH_KEY, currentHash);
        }
    }
}
