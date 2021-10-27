package org.aldomanco.plantsensor.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.appcompat.app.AlertDialog;

import org.aldomanco.plantsensor.R;
import org.aldomanco.plantsensor.home.LoggedUserActivity;

public class ConnectionLostReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        // TODO: This method is called when the BroadcastReceiver is receiving an Intent broadcast.
        boolean isNotConnected = intent.getBooleanExtra("noConnectivity", false);

        if (isNotConnected) {

            new AlertDialog.Builder(LoggedUserActivity.getLoggedUserActivity())
                    .setIcon(R.drawable.ic_no_internet_connection_24)
                    .setTitle("Network Disconnected")
                    .setMessage("Detected an internet disconnection.\nCheck your connection otherwise app won't work properly.")
                    .setPositiveButton("OK", null)
                    .show();
        }
    }
}