package subatom.eden_beta;

import android.support.v7.app.AppCompatActivity;

/**
 * A base activity for all activities in the app.  It relays start and stop events to
 * DemoApplication, so that it can keep count of the number of started Activities in the app.
 */
class BaseActivity extends AppCompatActivity {
    @Override
    protected void onStart() {
        super.onStart();
        ((DemoApplication)getApplication()).onActivityStarted();
    }

    @Override
    protected void onStop() {
        super.onStop();
        ((DemoApplication)getApplication()).onActivityStopped();
    }
}
