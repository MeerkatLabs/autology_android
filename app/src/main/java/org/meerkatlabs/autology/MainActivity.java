package org.meerkatlabs.autology;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.simplelist.MaterialSimpleListAdapter;
import com.afollestad.materialdialogs.simplelist.MaterialSimpleListItem;

import org.meerkatlabs.autology.permissions.StoragePermissionFragment;
import org.meerkatlabs.autology.settings.SettingsActivity;
import org.meerkatlabs.autology.utilities.LogProvider;
import org.meerkatlabs.autology.utilities.TemplateProvider;
import org.meerkatlabs.autology.utilities.templates.BaseTemplate;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements LogProvider.ILogProvider, DatePickerDialog.OnDateSetListener {

    private Fragment currentFragment = null;
    private LogProvider provider;
    private Calendar currentDate = Calendar.getInstance();

    private static final int EXTERNAL_STORAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Load up the preferences from the system
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

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
            final LogProvider lp = getProvider();
            final TemplateProvider provider = new TemplateProvider();
            // TODO: Find a list of templates that are installed, and display a selection dialog
            // TODO: Have the log provider create a new file of that template and provide it to
            // whatever listener is installed to edit it.



            final MaterialSimpleListAdapter adapter = new MaterialSimpleListAdapter(new MaterialSimpleListAdapter.Callback() {
                @Override
                public void onMaterialListItemSelected(MaterialDialog dialog, int index, MaterialSimpleListItem item) {
                    Log.i("RER", "Selected item: " + item.getId() + " " + index );
                    BaseTemplate t = (BaseTemplate) item.getTag();
                    Log.i("RER", t.getName());
                    dialog.dismiss();

                    lp.createNewLogFile(currentDate, t);
                }
            });

            for (BaseTemplate template : provider.getTemplates()) {
                adapter.add(new MaterialSimpleListItem.Builder(MainActivity.this)
                        .content(template.getName())
                        .tag(template)
                        .build());
            }

            new MaterialDialog.Builder(MainActivity.this)
                    .title("Select Template")
                    .adapter(adapter, null)
                    .show();
        }
    };

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        // Can load up the list view fragment here
        currentDate.set(year, month, dayOfMonth);
        currentFragment = LogListFragment.createFragment(currentDate);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_fragment_container, currentFragment).commit();
    }

}
