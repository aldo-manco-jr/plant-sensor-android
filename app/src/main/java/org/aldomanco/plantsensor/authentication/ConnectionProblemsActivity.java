package org.aldomanco.plantsensor.authentication;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.aldomanco.plantsensor.R;
import org.aldomanco.plantsensor.home.LoggedUserActivity;

public class ConnectionProblemsActivity extends AppCompatActivity implements View.OnClickListener {

    private Button buttonTryAgain;
    private Button buttonExit;

    private boolean isLogged;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection_problems);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        isLogged = getIntent().getBooleanExtra("is_logged", false);

        buttonTryAgain = findViewById(R.id.try_connect_again);
        buttonExit = findViewById(R.id.button_exit);

        buttonTryAgain.setOnClickListener(this);
        buttonExit.setOnClickListener(this);
    }

    /**
     * Viene eseguito un nuovo intent verso {@link MainActivity} per riprovare a stabilire una
     * connessione
     */
    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.try_connect_again:

                Intent intentFirstActivity;

                if (isLogged){
                    intentFirstActivity = new Intent(this, LoggedUserActivity.class);
                }else {
                    intentFirstActivity = new Intent(this, MainActivity.class);
                }

                intentFirstActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intentFirstActivity);

                Intent intent = new Intent(this, ConnectionProblemsActivity.class);
                stopService(intent);

                ActivityCompat.finishAffinity(this);
                break;

            case R.id.button_exit:
                finish();
                break;
            default:
                break;
        }
    }
}