package org.meerkatlabs.autology;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.meerkatlabs.autology.utilities.logs.LogEntry;
import org.meerkatlabs.autology.utilities.logs.LogProvider;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by rerobins on 11/21/17.
 */

public class LogListFragment extends Fragment {

    private static final String CURRENT_DATE_BUNDLE_KEY = "currentDate";

    private LogProvider.ILogProvider provider;
    private LogEntry.ILogEntryEditor editor;
    private LogEntry[] logEntries = new LogEntry[] {};

    public static LogListFragment createFragment(Calendar currentDate) {
        LogListFragment fragment = new LogListFragment();

        Bundle args = new Bundle();
        args.putLong(CURRENT_DATE_BUNDLE_KEY, currentDate.getTime().getTime());

        fragment.setArguments(args);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_log_list, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            provider = (LogProvider.ILogProvider) context;
            editor = (LogEntry.ILogEntryEditor) context;
        } catch (ClassCastException cce) {
            Log.e("RER", getClass() + " must be attached to an ILogProvider");
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        long currentTime = getArguments().getLong(CURRENT_DATE_BUNDLE_KEY, Calendar.getInstance().getTime().getTime());
        Calendar currentDate = Calendar.getInstance();
        currentDate.setTime(new Date(currentTime));

        TextView header = getActivity().findViewById(R.id.log_list_header);
        header.setText(String.format("%tF", currentDate));

        logEntries =  provider.getProvider().getFilesList(currentDate);
        TextView noItems = getActivity().findViewById(R.id.no_log_files);
        ListView listView = getActivity().findViewById(R.id.log_list);

        if (logEntries.length == 0) {
            noItems.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        } else {
            // TODO: Instead of allowing the data to determine it's string, build it based on locale
            // so it can be set in the settings of the application or in the settings of the phone
            listView.setAdapter(new ArrayAdapter<>(getActivity(), R.layout.view_log_entry,
                    R.id.log_date, logEntries));
            listView.setVisibility(View.VISIBLE);
            noItems.setVisibility(View.GONE);
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                editor.editLogEntry(logEntries[position]);
            }
        });

    }
}
