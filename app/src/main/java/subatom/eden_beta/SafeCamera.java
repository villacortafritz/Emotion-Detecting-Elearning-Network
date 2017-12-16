package subatom.eden_beta;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.view.SurfaceHolder;

import java.io.IOException;

/**
 * A wrapper class to enforce thread-safe access to the camera and its properties.
 */
@SuppressWarnings("deprecation")
class SafeCamera {
    private Camera camera;
    private volatile int cameraId = -1;
    private boolean taken;

    /**
     * Attempts to open the specified camera.
     *Safe
     * @param cameraToOpen one of {@link CameraInfo#CAMERA_FACING_BACK}
     *                     or {@link CameraInfo#CAMERA_FACING_FRONT}
     * @throws IllegalStateException if the device does not have a camera of the requested type or
     *                               the camera is already locked by another process
     */
    public SafeCamera(int cameraToOpen) throws IllegalStateException {

        int cnum = Camera.getNumberOfCameras();
        CameraInfo caminfo = new CameraInfo();

        for (int i = 0; i < cnum; i++) {
            Camera.getCameraInfo(i, caminfo);
            if (caminfo.facing == cameraToOpen) {
                cameraId = i;
                break;
            }
        }
        if (cameraId == -1) {
            throw new IllegalStateException("This device does not have a camera of the requested type");
        }
        try {
            camera = Camera.open(cameraId); // attempt to get a Camera instance.
        } catch (RuntimeException e) {
            // Camera is not available (in use or does not exist). Translate to a more appropriate exception type.
            String msg = "Camera is unavailable. Please close the app that is using the camera and then try again.\n"
                    + "Error:  " + e.getMessage();
            throw new IllegalStateException(msg, e);
        }
    }

    public int getCameraId() {
        return cameraId; // volatile, no need for synchronized keyword
    }

    synchronized public Camera.Parameters getParameters() {
        checkTaken();
        return camera.getParameters();
    }

    synchronized public void setParameters(Camera.Parameters parameters) {
        checkTaken();
        camera.setParameters(parameters);
    }

    synchronized public void unlock() {
        checkTaken();
        camera.unlock();
    }

    synchronized public void addCallbackBuffer(byte[] buffer) {
        checkTaken();
        camera.addCallbackBuffer(buffer);
    }

    synchronized public void setPreviewCallbackWithBuffer(Camera.PreviewCallback callback) {
        checkTaken();
        camera.setPreviewCallbackWithBuffer(callback);
    }

    synchronized public void setPreviewCallback(Camera.PreviewCallback callback) {
        checkTaken();
        camera.setPreviewCallback(callback);
    }

    synchronized public void setPreviewDisplay(SurfaceHolder holder) throws IOException {
        checkTaken();
        camera.setPreviewDisplay(holder);
    }

    synchronized public void setPreviewTexture(SurfaceTexture texture) throws IOException {
        checkTaken();
        camera.setPreviewTexture(texture);
    }

    synchronized public void setOneShotPreviewCallback(Camera.PreviewCallback callback) {
        checkTaken();
        camera.setOneShotPreviewCallback(callback);
    }

    synchronized public void startPreview() {
        checkTaken();
        camera.startPreview();
    }

    synchronized public void stopPreview() {
        checkTaken();
        camera.stopPreview();
    }

    synchronized public void setDisplayOrientation(int degrees) {
        checkTaken();
        camera.setDisplayOrientation(degrees);
    }

    synchronized public CameraInfo getCameraInfo() {
        checkTaken();
        CameraInfo result = new CameraInfo();
        Camera.getCameraInfo(cameraId, result);
        return result;
    }

    /**
     * Yield control of the camera to a caller.  To avoid unsafe access, this class will allow no
     * calls to the Camera until it is returned via {@link #returnCamera}, instead throwing
     * an IllegalStateException in that situation.
     *
     * @return the Camera
     */
    synchronized public Camera takeCamera() {
        checkTaken();
        taken = true;
        return camera;
    }

    synchronized public void returnCamera() {
        taken = false;
    }

    synchronized public void release() {
        checkTaken();

        try {
            camera.release();
            camera = null;
        } catch (Exception e) {
            //do nothing, exception thrown because camera was already closed or camera was null (if it failed to open at all)
        }
    }

    private void checkTaken() throws IllegalStateException {
        if (taken) {
            throw new IllegalStateException("cannot take or interact with camera while it has been taken");
        }
    }
}
