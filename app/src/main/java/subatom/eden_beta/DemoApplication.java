package subatom.eden_beta;

import android.app.Application;
import android.content.Intent;

/**
 * Manages the lifetime of the DetectionService so that it is running whenever there is a
 * started Activity, but is shutdown when there are no started Activities.
 *
 * See the README.md file at the root of the ServiceFrameDetectorDemo module for more info on this
 * sample app.
 */
public class DemoApplication extends Application {
    public static final String LOG_TAG = "ServiceFrameDetector";
    private int startedActivityCount;

    public void onActivityStarted() {
        // when the number of started activities transitions from 0 to 1, start the service
        if (++startedActivityCount == 1) {
            startService(new Intent(this, DetectorService.class));
        }
    }

    public void onActivityStopped() {
        // when the number of started activities transitions from 1 to 0, stop the service
        if (--startedActivityCount == 0) {
            stopService(new Intent(this, DetectorService.class));
        }
    }
}
