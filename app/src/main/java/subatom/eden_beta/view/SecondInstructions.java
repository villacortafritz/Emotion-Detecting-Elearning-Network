package subatom.eden_beta;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Explode;
import android.view.View;
import android.widget.Button;

public class SecondInstructions extends Activity {

    public class Next2 implements View.OnClickListener{
        public void onClick(View v){

            Intent i = new Intent(SecondInstructions.this, ThirdInstructions.class);
            finish();
            startActivity(i);
        }
    }

    private static int SPLASH_TIME_OUT = 8000;
    private Button btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secondinstructions);

        btnNext = (Button) findViewById(R.id.btnNext);
        btnNext.setOnClickListener(new Next2());
    }
}
