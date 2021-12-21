package org.aldomanco.plantsensor.authentication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import org.aldomanco.plantsensor.R;
import org.aldomanco.plantsensor.home.LoggedUserActivity;
import org.aldomanco.plantsensor.home.SetPlantInfoActivity;
import org.aldomanco.plantsensor.weather_state.WeatherFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intentFirstActivity = null;

        if (isOnline()){
            intentFirstActivity = new Intent(this, LoggedUserActivity.class);
        } else {
            // esistono problemi di connessione
            intentFirstActivity = new Intent(this, ConnectionProblemsActivity.class);
        }

        intentFirstActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intentFirstActivity);
        ActivityCompat.finishAffinity(this);
    }

    /**
     * Verifica se l'utente è connesso a internet
     */
    public boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        return (networkInfo != null && networkInfo.isConnected());
    }
}