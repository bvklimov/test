package com.spb.kbv.sampleskbv.helpers;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;

public class ConnectionHelper {
    private final Context context;

    public ConnectionHelper(Context context) {
        this.context = context;
    }

    public boolean isInternetConnected() {
        ConnectivityManager manager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager == null)
            return false;
        NetworkInfo info = manager.getActiveNetworkInfo();
        return info != null && info.isAvailable() && info.isConnected();
    }

    public boolean isInAirplaneMode() {
        return Settings.Global.getInt(context.getContentResolver(),
                Settings.Global.AIRPLANE_MODE_ON, 0) != 0;
    }
}
