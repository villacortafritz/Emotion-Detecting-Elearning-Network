package subatom.eden_beta;

import android.Manifest;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.affectiva.android.affdex.sdk.Frame;
import com.affectiva.android.affdex.sdk.detector.CameraDetector;
import com.affectiva.android.affdex.sdk.detector.Detector;
import com.affectiva.android.affdex.sdk.detector.Face;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.ErrorReason;
import com.google.android.youtube.player.YouTubePlayer.PlaybackEventListener;
import com.google.android.youtube.player.YouTubePlayer.PlayerStateChangeListener;
import com.google.android.youtube.player.YouTubePlayer.Provider;
import com.google.android.youtube.player.YouTubePlayerView;

import java.util.List;


public class YoutubePlayer extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener, Detector.ImageListener, CameraDetector.CameraEventListener {

    public static final String API_KEY = "AIzaSyAhSHZi4V8YvFqLWNJWsAkICj4l8Wkug_k";
    private String VIDEO_ID, video_title, student_name, student_id, student_gender;
    private int student_age;
    private boolean student_excel;
    private boolean isPlaying = false;

        private final static int CAMERA_PERMISSIONS_REQUEST_CODE = 0;
        private final static String[] CAMERA_PERMISSIONS_REQUEST = new String[]{android.Manifest.permission.CAMERA};
        private boolean handleCameraPermissionGrant;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube_player);
        //this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        VIDEO_ID = getIntent().getStringExtra("url");
        video_title = getIntent().getStringExtra("title");
        student_name = getIntent().getStringExtra("student_name");
        student_gender = getIntent().getStringExtra("student_gender");
        student_age = getIntent().getIntExtra("student_age", 8);
        student_excel = getIntent().getBooleanExtra("student_excel", true);
        YouTubePlayerView youTubePlayerView = (YouTubePlayerView)findViewById(R.id.youtube_player);
        youTubePlayerView.initialize(API_KEY, this);


    }



    @Override
    protected void onResume () {
        super.onResume();
    }

    @Override
    protected void onStop()
    {
        super.onStop();
    }

    @Override
    public void onInitializationFailure(Provider provider, YouTubeInitializationResult result) {
        Toast.makeText(this, "Failured to Initialize!", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onInitializationSuccess(Provider provider, YouTubePlayer player, boolean wasRestored) {
        /** add listeners to YouTubePlayer instance **/
        player.setFullscreen(true);
        player.setPlayerStateChangeListener(playerStateChangeListener);
        player.setPlaybackEventListener(playbackEventListener);
        player.setShowFullscreenButton(false);
        player.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT); //no play, no meter

        /** Start buffering **/
        if (!wasRestored) {
                player.cueVideo(VIDEO_ID);
        }
        Emotion.detect = true;

        //player.seekToMillis(10000);
        // Toast.makeText(this, player.getDurationMillis() + " ", Toast.LENGTH_SHORT).show();


    }

    private PlaybackEventListener playbackEventListener = new PlaybackEventListener() {

        public void onBuffering(boolean arg0) {
            if (arg0 == true) {
                StopWatch.pause();
                Emotion.detect = false;
            }
            else {
                StopWatch.resume();
                Emotion.detect = true;
            }




        }
        @Override
        public void onPaused() {
            //pause timer, pause detector
            //Toast.makeText(YoutubePlayer.this, "PAUSE LIKE A POSSE", Toast.LENGTH_SHORT).show();
            Emotion.detect = false;
            isPlaying = false;
            StopWatch.pause();
        }
        @Override
        public void onPlaying() {
            //resume timer, resume detector
            //Toast.makeText(YoutubePlayer.this, , Toast.LENGTH_SHORT).show();
            //isPlaying = true;
            Emotion.detect = true;
            StopWatch.resume();
        }
        @Override
        public void onSeekTo(int ms) {
            //overwrite list
            //Toast.makeText(YoutubePlayer.this, ytp.getCurrentTimeMillis() + " ", Toast.LENGTH_SHORT).show();
        }
        @Override
        public void onStopped() {
            //Toast.makeText(YoutubePlayer.this, "STOP LIKE A CADILLAC", Toast.LENGTH_SHORT).show();
            Emotion.detect = false;
            StopWatch.stop();
            StopWatch.reset();
        }
    };
    private PlayerStateChangeListener playerStateChangeListener = new PlayerStateChangeListener() {
        @Override
        public void onAdStarted() {
            Emotion.detect = false;
        }
        @Override
        public void onError(ErrorReason arg0) {
            //Toast.makeText(YoutubePlayer.this, arg0.name(), Toast.LENGTH_SHORT).show();
            stopService(new Intent(YoutubePlayer.this, DetectorService.class));
            Emotion.detect = false;
            StopWatch.stop();
            StopWatch.reset();
        }
        @Override
        public void onLoaded(String arg0) {
            //Emotion.detect = true;

        }
        @Override
        public void onLoading() {
            //Toast.makeText(YoutubePlayer.this, "LOADING LIKE YOUR MOM'S MOUTH RIGHT NOW", Toast.LENGTH_SHORT).show();
            Emotion.detect = false;
            StopWatch.pause();
        }
        @Override
        public void onVideoEnded() {
            Emotion.detect = false;
            stopService(new Intent(YoutubePlayer.this, DetectorService.class));
            Intent i = new Intent(YoutubePlayer.this, Statistics.class);
            String len = Double.toString(StopWatch.getTime());
            i.putExtra("url", VIDEO_ID);
            i.putExtra("video_title", video_title);
            i.putExtra("student_id", student_id);
            i.putExtra("student_name", student_name);
            i.putExtra("length_video", len);
            i.putExtra("student_age", student_age);
            i.putExtra("student_gender", student_gender);
            i.putExtra("student_excel", student_excel);
            startActivity(i);

            //Toast.makeText(YoutubePlayer.this, Emotion.getBrowFurrow(0) + " ", Toast.LENGTH_SHORT).show();
        }
        @Override
        public void onVideoStarted() {

            if (!CameraHelper.checkPermission(YoutubePlayer.this)) {
                requestPermissions(CAMERA_PERMISSIONS_REQUEST, CAMERA_PERMISSIONS_REQUEST_CODE);
            } else {
                    if (CameraHelper.checkPermission(getApplicationContext())) {
                        startService(new Intent(YoutubePlayer.this, DetectorService.class));
                    } else {
                        Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
                    }
                }


                if (handleCameraPermissionGrant) {
                    // a response to our camera permission request was received
                    if (CameraHelper.checkPermission(getApplicationContext())) {
                        startService(new Intent(getApplicationContext(), DetectorService.class));
                    } else {
                    Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
                }
                handleCameraPermissionGrant = false;
            }
            serviceStart();
            Emotion.detect = true;
            StopWatch.start();

        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CAMERA_PERMISSIONS_REQUEST_CODE) {
            for (String permission : permissions) {
                if (permission.equals(Manifest.permission.CAMERA)) {
                    // next time through onResume, handle the grant result
                    handleCameraPermissionGrant = true;
                    break;
                }
            }
        }

    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    @Override
    public void onCameraSizeSelected(int i, int i1, Frame.ROTATE rotate) {

    }

    @Override
    public void onImageResults(List<Face> list, Frame frame, float v) {

    }

    public void serviceStart() {
        startService(new Intent(this, DetectorService.class));
    }


}