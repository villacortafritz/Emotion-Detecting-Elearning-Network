package subatom.eden_beta.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import subatom.eden_beta.R;

public class FirstInstructions extends Activity {

    public class Next1 implements View.OnClickListener{
        public void onClick(View v) {
            Intent i = new Intent(FirstInstructions.this, SecondInstructions.class);
            finish();
            startActivity(i);
        }
    }

    private static int SPLASH_TIME_OUT = 7000;
    private Button btnNext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firstinstructions);

        btnNext = (Button)findViewById(R.id.btnNext);
        btnNext.setOnClickListener(new Next1());
    }
}
