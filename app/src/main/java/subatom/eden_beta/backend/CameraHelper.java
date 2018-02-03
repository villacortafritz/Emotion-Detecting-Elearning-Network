package subatom.eden_beta.backend;

/*IMPORTED*/

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Size;
import android.os.Process;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Display;
import android.view.OrientationEventListener;
import android.view.Surface;

import com.affectiva.android.affdex.sdk.Frame;

import java.io.IOException;
import java.util.List;

/**
 * The CameraHelper class encapsulates interaction with the Camera, including configuring and
 * coordinating previewing in a way that is optimized for use with FrameDetector.  Preview
 * frames are delivered through the Listener callback interface.
 */
@SuppressWarnings("deprecation")
public class CameraHelper {
    private static final String LOG_TAG = "CameraHelper";
    private static final float TARGET_FRAME_RATE = 30;
    private SafeCamera safeCamera;
    private int displayRotation;
    private Listener listener = null;
    private boolean isPreviewing = false;
    private Display display;
    private Frame.ROTATE frameRotation;
    private int previewWidth;
    private int previewHeight;
    private OrientationHelper orientationHelper;
    private CameraPreviewer cameraPreviewer;

    public CameraHelper(@NonNull Context context, @NonNull Display display, @NonNull Listener listener) {
        if (!checkPermission(context)) {
            throw new IllegalStateException("app does not have camera permission");
        }

        this.display = display;
        this.listener = listener;
        displayRotation = display.getRotation();
        frameRotation = Frame.ROTATE.NO_ROTATION;
        orientationHelper = new OrientationHelper(context);
        cameraPreviewer = new CameraPreviewer();
    }

    public static boolean checkPermission(Context context) {
        return context.checkPermission(Manifest.permission.CAMERA, Process.myPid(), Process.myUid())
                == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * attempts to open the specified camera
     *
     * @param cameraToOpen one of CameraInfo.
     */
    public void acquire(int cameraToOpen) {
        safeCamera = new SafeCamera(cameraToOpen);
    }

    /**
     * Configures the acquired camera for use with the Affdex SDK, and starts previewing.  Preview
     * frames will be delivered to the listener.
     */
    public void start(@NonNull SurfaceTexture texture) {
        Log.d(LOG_TAG, "CameraHelper.start()");
        if (safeCamera == null) {
            throw new IllegalStateException("acquire a camera before calling the start method");
        }
        if (!isPreviewing) {
            initCameraParams();
            Camera.Parameters params = safeCamera.getParameters();
            previewWidth = params.getPreviewSize().width;
            previewHeight = params.getPreviewSize().height;
            setCameraDisplayOrientation();
            startPreviewing(texture);
        }
    }

    /**
     * Stops and releases the camera.
     */
    public void stop() {
        Log.d(LOG_TAG, "CameraHelper.stop()");
        if (isPreviewing) {
            stopPreviewing();
        }
    }

    /**
     * Releases the acquired camera
     */
    public void release() {
        if (safeCamera != null) {
            safeCamera.release();
            safeCamera = null;
        }
    }


    private void setupPreviewWithCallbackBuffers() {
        // calculate the size for the callback buffers
        Camera.Parameters params = safeCamera.getParameters();
        int previewFormat = params.getPreviewFormat();
        int bitsPerPixel = ImageFormat.getBitsPerPixel(previewFormat);
        Size size = params.getPreviewSize();

        int bufSize = size.width * size.height * bitsPerPixel / 8;

        // add two buffers to the queue, so the camera can be working with one, while the callback is working with the
        // other. The callback will put each buffer it receives back into the buffer queue when it's done, so the
        // camera can use it again.
        safeCamera.addCallbackBuffer(new byte[bufSize]);
        safeCamera.addCallbackBuffer(new byte[bufSize]);

        safeCamera.setPreviewCallbackWithBuffer(cameraPreviewer);
    }

    /*
      Starts camera preview.
      Method should only be called when state is CameraHelperState.STARTED
   */
    private void startPreviewing(@NonNull SurfaceTexture texture) {
        Log.d(LOG_TAG, "startPreviewing");
        try {
            safeCamera.setPreviewTexture(texture);
        } catch (IOException e) {
            Log.i(LOG_TAG, "Unable to start camera preview" + e.getMessage());
        }

        orientationHelper.enable();

        // setPreviewCallbackWithBuffer only seems to work if you establish it after the first onPreviewFrame callback
        // (otherwise it never gets called back at all). So, use a one-shot callback for the first one, then
        // swap in the callback that uses the buffers.
        safeCamera.setOneShotPreviewCallback(new Camera.PreviewCallback() {
            @Override
            public void onPreviewFrame(byte[] data, Camera camera) {
                if (listener != null) {
                    listener.onFrameAvailable(data, previewWidth, previewHeight, frameRotation);
                }
                setupPreviewWithCallbackBuffers();
            }
        });

        isPreviewing = true;

        try {
            safeCamera.startPreview();
        } catch (Exception e) {
            Log.e(LOG_TAG, "Failed to start preview!");
            stopPreviewing();
        }
    }

    /*
        Stops camera preview.
        Method should only be called when state is CameraHelperState.STARTED
     */
    private void stopPreviewing() {
        Log.d(LOG_TAG, "stopPreviewing");
        if (isPreviewing) {
            safeCamera.stopPreview();
            safeCamera.setPreviewCallback(null);
            orientationHelper.disable();
        }
        isPreviewing = false;
    }

    // Make the camera image show in the same orientation as the display.
    // This code is partially based on sample code at http://developer.android.com/reference/android/hardware/Camera.html
    private void setCameraDisplayOrientation() {

        int degrees = 0;
        switch (displayRotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int rotation;
        CameraInfo info = safeCamera.getCameraInfo();

        if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
            //determine amount to rotate image and call computeFrameRotation()
            //to have the Frame.ROTATE object ready for CameraDetector to use
            rotation = (info.orientation + degrees) % 360;

            computeFrameRotation(rotation);

            //Android mirrors the image that will be displayed on screen, but not the image
            //that will be sent as bytes[] in onPreviewFrame(), so we compensate for mirroring after
            //calling computeFrameRotation()
            rotation = (360 - rotation) % 360; // compensate the mirror
        } else { // back-facing
            //determine amount to rotate image and call computeFrameRotation()
            //to have the Frame.ROTATE object ready for CameraDetector to use
            rotation = (info.orientation - degrees + 360) % 360;

            computeFrameRotation(rotation);
        }
        safeCamera.setDisplayOrientation(rotation);

        //Now that rotation has been determined (or updated) inform mListener of new frame size.
        if (listener != null) {
            listener.onFrameSizeSelected(previewWidth, previewHeight, frameRotation);
        }
    }

    private void computeFrameRotation(int rotation) {
        switch (rotation) {
            case 0:
                frameRotation = Frame.ROTATE.NO_ROTATION;
                break;
            case 90:
                frameRotation = Frame.ROTATE.BY_90_CW;
                break;
            case 180:
                frameRotation = Frame.ROTATE.BY_180;
                break;
            case 270:
                frameRotation = Frame.ROTATE.BY_90_CCW;
                break;
            default:
                frameRotation = Frame.ROTATE.NO_ROTATION;
        }
    }

    public interface Listener {
        void onFrameAvailable(byte[] frame, int width, int height, Frame.ROTATE rotation);

        void onFrameSizeSelected(int width, int height, Frame.ROTATE rotation);
    }


    private void initCameraParams() {
        Camera.Parameters cameraParams = safeCamera.getParameters();

            /* dump camera params to logcat - useful when debugging
            String flattened = cameraParams.flatten();
            StringTokenizer tokenizer = new StringTokenizer(flattened, ";");
            Log.d(LOG_TAG, "Dump all camera parameters:");
            while (tokenizer.hasMoreElements()) {
                Log.d(LOG_TAG, tokenizer.nextToken());
            }
            */

        setOptimalPreviewFrameRate(cameraParams);
        setOptimalPreviewSize(cameraParams, 480);
        safeCamera.setParameters(cameraParams);
    }

    //Sets camera frame to be as close to TARGET_FRAME_RATE as possible
    private void setOptimalPreviewFrameRate(@NonNull Camera.Parameters cameraParams) {
        int targetHiMS = (int) (1000 * TARGET_FRAME_RATE);

        List<int[]> ranges = cameraParams.getSupportedPreviewFpsRange();
        if (ranges == null || ranges.size() <= 1) {
            return; // no options or only one option: no need to set anything.
        }

        int[] optimalRange = null;
        int minDiff = Integer.MAX_VALUE;
        for (int[] range : ranges) {
            int currentDiff = Math.abs(range[1] - targetHiMS);
            if (optimalRange == null || currentDiff <= minDiff) {
                optimalRange = range;
                minDiff = currentDiff;
            }
        }

        if (optimalRange == null) {
            // This should not be reachable, but satisfying a Lint warning about possible null value
            return;
        }

        cameraParams.setPreviewFpsRange(optimalRange[0], optimalRange[1]); // this will take the biggest lo range.
    }

    // Finds the closest height - simple algo. NOTE: only height is used as a target, width is ignored!
    //TODO: this could benefit from revision - for example, it chooses a square image on the Nexus 7, which looks bad
    private void setOptimalPreviewSize(@NonNull Camera.Parameters cameraParams, int targetHeight) {
        List<Size> supportedPreviewSizes = cameraParams.getSupportedPreviewSizes();
        // according to Android bug #6271, the emulator sometimes returns null from getSupportedPreviewSizes,
        // although this shouldn't happen on a real device.
        // See https://code.google.com/p/android/issues/detail?id=6271
        if (null == supportedPreviewSizes || supportedPreviewSizes.isEmpty()) {
            Log.v(LOG_TAG, "Camera returning null for getSupportedPreviewSizes(), will use default");
            return;
        }

        Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        for (Size size : supportedPreviewSizes) {
            double currentDiff = Math.abs(size.height - targetHeight);
            if (optimalSize == null || currentDiff < minDiff) {
                optimalSize = size;
                minDiff = currentDiff;
            }
        }

        if (optimalSize == null) {
            // This should not be reachable, but satisfying a Lint warning about possible null value
            return;
        }

        cameraParams.setPreviewSize(optimalSize.width, optimalSize.height);
    }

    private class OrientationHelper extends OrientationEventListener {

        private OrientationHelper(Context context) {
            super(context);
        }

        // If you quickly rotate 180 degrees, Activity does not restart, so you need this orientation Listener.
        @Override
        public void onOrientationChanged(int orientation) {
            // this method gets called for every tiny 1 degree change in orientation, so it's called really often
            // if the device is handheld. We don't need to reset the camera display orientation unless there
            // is a change to the display rotation (i.e. a 90/180/270 degree switch).
            if (display.getRotation() != displayRotation) {
                displayRotation = display.getRotation();
                setCameraDisplayOrientation();
            }
        }
    }

    private class CameraPreviewer implements Camera.PreviewCallback {
        @Override
        public void onPreviewFrame(@NonNull byte[] data, @NonNull Camera camera) {
            if (listener != null) {
                listener.onFrameAvailable(data, previewWidth, previewHeight, frameRotation);
            }
            // put the buffer back in the queue, so that it can be used again
            camera.addCallbackBuffer(data);
        }
    }
}
