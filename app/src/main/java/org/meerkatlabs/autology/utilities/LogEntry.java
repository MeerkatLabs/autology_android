package org.meerkatlabs.autology.utilities;

import java.util.Calendar;

/**
 * Created by rerobins on 11/21/17.
 */

public class LogEntry {

    Calendar entryDate;

    public LogEntry(Calendar _entryDate) {
        entryDate = _entryDate;
    }

    public Calendar getEntryDate() {
        return entryDate;
    }
}
