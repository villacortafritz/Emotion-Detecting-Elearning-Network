package subatom.eden_beta;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Toast;

import com.affectiva.android.affdex.sdk.Frame;
import com.affectiva.android.affdex.sdk.detector.Face;
import com.affectiva.android.affdex.sdk.detector.FrameDetector;

import java.util.List;

import subatom.eden_beta.model.Emotion;
import subatom.eden_beta.model.Pair;

/**
 * Manages a background thread that connects to the front-facing camera and sends preview
 * frames to an Affdex FrameDetector.
 */
public class DetectorService extends Service {

    public static final String LOG_TAG = "DetectorService";
    private HandlerThread detectionThread;
    private DetectionHandler detectionHandler;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null; // binding support not implemented
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        if (!CameraHelper.checkPermission(this)) {
            Log.w(DemoApplication.LOG_TAG, "app does not have camera permission, stopping service");
            stopSelf();
        } else if (detectionThread == null) {
            // fire up the background thread
            detectionThread = new DetectionThread();
            detectionThread.start();
            detectionHandler = new DetectionHandler(getApplicationContext(), detectionThread);
            detectionHandler.sendStartMessage();
        }

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        // clean up
        if (detectionHandler != null) {
            detectionHandler.sendStopMessage();
            try {
                detectionThread.join();
                detectionThread = null;
                detectionHandler = null; // facilitate GC
            } catch (InterruptedException ignored) {
            }
        }
        super.onDestroy();
    }

    private static class DetectionThread extends HandlerThread {
        private DetectionThread() {
            super("DetectionThread");
        }
    }

    /**
     * A handler for the DetectionThread.
     */
    private static class DetectionHandler extends Handler {
        //Incoming message codes
        private static final int START = 0;
        private static final int STOP = 1;

        private CameraHelper cameraHelper;
        private FrameDetector frameDetector;
        private SurfaceTexture surfaceTexture;
        private DetectorListener listener;

        private DetectionHandler(Context context, HandlerThread detectionThread) {
            // note: getLooper will block until the the thread's looper has been prepared
            super(detectionThread.getLooper());


            Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
            cameraHelper = new CameraHelper(context, display, new CameraHelperListener());
            surfaceTexture = new SurfaceTexture(0); // a dummy texture

            // Set up the FrameDetector.  For the purposes of this sample app, we'll just request
            // listen for face events and request Joy scores.
            frameDetector = new FrameDetector(context);

            frameDetector.setDetectEngagement(true);
            frameDetector.setDetectJoy(true);
            frameDetector.setDetectAttention(true);
            frameDetector.setDetectBrowFurrow(true);
            frameDetector.setDetectValence(true);

            listener = new DetectorListener(context);
            frameDetector.setImageListener(listener);
            frameDetector.setFaceListener(listener);
        }

        /**
         * asynchronously start processing on the background thread
         */
        private void sendStartMessage() {
            sendMessage(obtainMessage(START));
        }

        /**
         * asynchronously stop processing on the background thread
         */
        private void sendStopMessage() {
            sendMessage(obtainMessage(STOP));
        }

        /**
         * Process incoming messages
         *
         * @param msg message to handle
         */
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case START:
                    Log.d(DemoApplication.LOG_TAG, "starting background processing of frames");
                    try {
                        frameDetector.start();
                        //noinspection deprecation
                        cameraHelper.acquire(Camera.CameraInfo.CAMERA_FACING_FRONT);
                        cameraHelper.start(surfaceTexture); // initiates previewing
                    } catch (IllegalStateException e) {
                        Log.d(DemoApplication.LOG_TAG, "couldn't open camera: " + e.getMessage());
                        return;
                    }
                    break;
                case STOP:
                    Log.d(DemoApplication.LOG_TAG, "stopping background processing of frames");
                    cameraHelper.stop(); // stops previewing
                    cameraHelper.release();
                    frameDetector.stop();

                    Log.d(DemoApplication.LOG_TAG, "quitting detection thread");
                    ((HandlerThread) getLooper().getThread()).quit();
                    break;

                default:
                    break;
            }
        }

        /**
         * A listener for CameraHelper callbacks
         */
        private class CameraHelperListener implements CameraHelper.Listener {
            private static final float TIMESTAMP_DELTA = .01f;
            private float lastTimestamp = -1f;

            @Override
            public void onFrameAvailable(byte[] frame, int width, int height, Frame.ROTATE rotation) {
                Log.d(DemoApplication.LOG_TAG, "received frame");
                float timeStamp = (float) SystemClock.elapsedRealtime() / 1000f;
                if (timeStamp > (lastTimestamp + TIMESTAMP_DELTA)) {
                    lastTimestamp = timeStamp;
                    frameDetector.process(createFrameFromData(frame, width, height, rotation), timeStamp);
                }
            }

            @Override
            public void onFrameSizeSelected(int width, int height, Frame.ROTATE rotation) {
            }

            private Frame createFrameFromData(byte[] frameData, int width, int height, Frame.ROTATE rotation) {
                Frame.ByteArrayFrame frame = new Frame.ByteArrayFrame(frameData, width, height, Frame.COLOR_FORMAT.YUV_NV21);
                frame.setTargetRotation(rotation);
                return frame;
            }
        }

        /**
         * A listener for FrameDetector callbacks
         */
        private static class DetectorListener implements FrameDetector.ImageListener, FrameDetector.FaceListener {
            private Context context;

            private DetectorListener(Context context) {
                this.context = context;
                //Emotion.startDetecting = true;
            }

            @Override
            public void onFaceDetectionStarted() {
                Toast.makeText(context, "Face Found", Toast.LENGTH_SHORT).show();
                //Emotion.detect = true;

            }

            @Override
            public void onFaceDetectionStopped() {
                Toast.makeText(context, "Face Lost", Toast.LENGTH_SHORT).show();
                //Emotion.detect = false;
            }

            @Override

            public void onImageResults(List<Face> faces, Frame frame, float v) {
                if (faces.size() > 0 && Emotion.detect) {
                    Face face = faces.get(0);
                    //String time = StopWatch.getTime();
                    double time = StopWatch.getTime();
                    //Toast.makeText(context, Float.toString(face.emotions.getJoy()), Toast.LENGTH_SHORT).show();
                    //Toast.makeText(context, Float.toString(face.expressions.getAttention()), Toast.LENGTH_SHORT).show();
                    Emotion.addBrowFurrow(new Pair<Double, Float>(time, face.expressions.getBrowFurrow()));
                    Emotion.addEngagement(new Pair<Double, Float>(time, face.emotions.getEngagement()));
                    Emotion.addAttention(new Pair<Double, Float>(time, face.expressions.getAttention()));
                    Emotion.addJoy(new Pair<Double, Float>(time, face.emotions.getJoy()));
                    Emotion.addValence(new Pair<Double, Float>(time, face.emotions.getValence()));

                    //Emotion.addSeconds(YoutubePlayer.millis);
                    //Toast.makeText(context, Float.toString(face.emotions.getEngagement()), Toast.LENGTH_SHORT).show();
                    Log.d(DemoApplication.LOG_TAG, "Time = " + Double.toString(StopWatch.getTime()));
                    Log.d(DemoApplication.LOG_TAG, "Joy score = " + Float.toString(face.emotions.getJoy()));
                    Log.d(DemoApplication.LOG_TAG, "attention score = " + Float.toString(face.expressions.getAttention()));
                    Log.d(DemoApplication.LOG_TAG, "engagement score = " + Float.toString(face.emotions.getEngagement()));
                    Log.d(DemoApplication.LOG_TAG, "brightness score = " + Float.toString(face.qualities.getBrightness()));

                } else if (faces.size() == 0 && Emotion.detect) {
                    //String time = StopWatch.getTime();
                    double time = StopWatch.getTime();
                    Emotion.addBrowFurrow(new Pair<Double,Float>(time,100f));
                    Emotion.addEngagement(new Pair<Double,Float>(time,0f));
                    Emotion.addAttention(new Pair<Double,Float>(time,0f));
                    Emotion.addJoy(new Pair<Double,Float>(time,0f));
                    Emotion.addValence(new Pair<Double,Float>(time,0f));

                }
            }
        }
    }
}
