package org.meerkatlabs.autology.permissions;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import org.meerkatlabs.autology.R;

public class PermissionsActivity extends AppCompatActivity {

    public static final String PERMISSION_TYPE = PermissionsActivity.class.getName() + ".PERMISSION_TYPE";
    private static final int EXTERNAL_STORAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permissions);

        ActionBar ab = getSupportActionBar();
        ab.setTitle(R.string.permission__title);

        String permissionType = getIntent().getStringExtra(PERMISSION_TYPE);

        if (Manifest.permission.WRITE_EXTERNAL_STORAGE.equals(permissionType)) {
            StoragePermissionFragment currentFragment = new StoragePermissionFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.permission_fragment_container, currentFragment).commit();
        }
    }

    public void storagePermissionClickHandler(View button) {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case EXTERNAL_STORAGE_REQUEST:
                finish();
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

}
