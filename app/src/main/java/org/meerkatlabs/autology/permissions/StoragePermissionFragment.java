package org.meerkatlabs.autology.permissions;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.meerkatlabs.autology.R;

/**
 * Fragment that will request external storage read/write permissions.
 */
public class StoragePermissionFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_storage_permission, container, false);
    }

}
