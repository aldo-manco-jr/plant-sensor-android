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

public class MainActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intentFirstActivity = null;

        if (isOnline()){
            // l'utente è connesso a internet
            SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.sharedpreferences_authentication), Context.MODE_PRIVATE);
            String token = sharedPreferences.getString(getString(R.string.sharedpreferences_token), null);

            if (token != null) {
                try {
                            // il token è ancora valido
                            /*intentFirstActivity = new Intent(this, LoggedUserActivity.class);
                            intentFirstActivity.putExtra("authToken", token);
                            intentFirstActivity.putExtra("username", username);
                            intentFirstActivity.putExtra("_id", id);*/
                } catch (Exception ignored) {}
            }
        } else {
            // esistono problemi di connessione
            intentFirstActivity = new Intent(this, ConnectionProblemsActivity.class);
        }

        if (intentFirstActivity == null){
            // non era stata effettuata alcuna scelta, quindi l'utente non era connesso
            intentFirstActivity = new Intent(this, LoggedUserActivity.class);
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