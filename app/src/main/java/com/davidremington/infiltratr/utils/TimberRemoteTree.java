package com.davidremington.infiltratr.utils;

import android.util.Log;

import com.davidremington.infiltratr.services.FirebaseService;
import com.google.firebase.database.DatabaseReference;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import timber.log.Timber;

public class TimberRemoteTree extends Timber.Tree {
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
    private SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS a zzz", Locale.getDefault());
    private String date = dateFormat.format(new Date(System.currentTimeMillis()));

    private DeviceDetails deviceDetails;
    private static DatabaseReference logRef;

    public TimberRemoteTree(DeviceDetails deviceDetails) {
        this.deviceDetails = deviceDetails;
    }

    private DatabaseReference logRef() {
        if (logRef == null) {
            logRef = FirebaseService.getInstance().getLoggingDatabaseReference(date, deviceDetails.getDeviceId());
        }
        return logRef;
    }

    @Override
    protected void log(int priority, @Nullable String tag, @NotNull String message, @Nullable Throwable t) {
        long timestamp = System.currentTimeMillis();
        String time = timeFormat.format(new Date(timestamp));
        RemoteLog remoteLog = new RemoteLog(priorityAsString(priority), tag, message, (t == null ? "" : t.toString()),time);

        logRef().updateChildren(remoteLog.map());
        logRef().child(String.valueOf(timestamp)).setValue(remoteLog);
    }

    private String priorityAsString(int priority) {
        String priorityAsString;
        switch (priority) {
            case Log.VERBOSE:
                priorityAsString = "VERBOSE";
                break;
            case Log.DEBUG:
                priorityAsString = "DEBUG";
                break;
            case Log.INFO:
                priorityAsString = "INFO";
                break;
            case Log.WARN:
                priorityAsString = "WARN";
                break;
            case Log.ERROR:
                priorityAsString = "ERROR";
                break;
            case Log.ASSERT:
                priorityAsString = "ASSERT";
                break;
            default:
                priorityAsString = String.valueOf(priority);
                break;
        }
        return priorityAsString;
    }
}
