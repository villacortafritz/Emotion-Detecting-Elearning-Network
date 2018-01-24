package subatom.eden_beta;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Explode;

public class SplashScreen extends Activity {

    private static int SPLASH_TIME_OUT = 3000;

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager)   getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm. getActiveNetworkInfo();
        if (networkInfo == null) {
            return false;
        }
        else{
            return true;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Explode explode = new Explode();
                getWindow().setExitTransition(explode);

                SharedPreferences preferences = getPreferences(MODE_PRIVATE);
                boolean ranBefore = preferences.getBoolean("RanBefore", false);
                if (!isNetworkConnected()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(SplashScreen.this, android.R.style.Theme_Material_Dialog_Alert);
                    builder.setTitle("Can't Reach Network!")
                            .setMessage("Unable to connect to EDEN. Please check your network settings or try later.")
                            .setPositiveButton("Quit", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                    System.exit(0);
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
                else{
                    if (!ranBefore) {
                        //show dialog if app never launch
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putBoolean("RanBefore", true);
                        editor.commit();
                        Intent i = new Intent(SplashScreen.this, firstinstructions.class);
                        finish();
                        startActivity(i);
                    }
                    else{
                        Intent i = new Intent(SplashScreen.this, NameSetter.class);
                        finish();
                        startActivity(i);

                    }
                }


            }
        }, SPLASH_TIME_OUT);
    }
}
