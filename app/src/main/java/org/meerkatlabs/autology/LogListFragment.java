package org.meerkatlabs.autology;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.meerkatlabs.autology.utilities.LogEntry;
import org.meerkatlabs.autology.utilities.LogProvider;

import java.util.Calendar;

/**
 * Created by rerobins on 11/21/17.
 */

public class LogListFragment extends Fragment {

    private Calendar currentDate;
    private LogProvider logProvider;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_log_list, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        logProvider = new LogProvider(getActivity());

        TextView header = getActivity().findViewById(R.id.log_list_header);
        header.setText("2017-11-21");

        ListView listView = getActivity().findViewById(R.id.log_list);
        listView.setAdapter(new ArrayAdapter<>(getActivity(), R.layout.view_log_entry,
                R.id.log_date, logProvider.getFilesList() ));
    }
}
