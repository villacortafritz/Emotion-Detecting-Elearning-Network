package subatom.eden_beta;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Explode;

public class SplashScreen extends Activity {

    private static int SPLASH_TIME_OUT = 3000;



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
        }, SPLASH_TIME_OUT);
    }
}
