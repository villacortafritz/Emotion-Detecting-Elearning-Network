package subatom.eden_beta.backend;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by JSP on 10/11/2017.
 */

//mainly para madungan sa time sa video

public class StopWatch {
    private static long startTime = 0;
    private static boolean running = false;
    private static long currentTime = 0;

    public static void start() {
        startTime = System.currentTimeMillis();
        running = true;
    }

    public static void stop() {
        running = false;
    }

    public static void pause() {
        running = false;
        currentTime = System.currentTimeMillis() - startTime;
    }

    public static void resume() {
        running = true;
        startTime = System.currentTimeMillis() - currentTime;
    }

    //elaspsed time in milliseconds
    public static long getElapsedTimeMili() {
        long elapsed = 0;
        if (running) {
            elapsed =((System.currentTimeMillis() - startTime)/100) % 1000 ;
        }
        return elapsed;
    }

    //elaspsed time in seconds
    public static long getElapsedTimeSecs() {
        long elapsed = 0;
        if (running) {
            elapsed = ((System.currentTimeMillis() - startTime) / 1000) % 60;
        }
        return elapsed;
    }

    //elaspsed time in minutes
    public static long getElapsedTimeMin() {
        long elapsed = 0;
        if (running) {
            elapsed = (((System.currentTimeMillis() - startTime) / 1000) / 60 ) % 60;
        }
        return elapsed;
    }

    //elaspsed time in hours
    public static long getElapsedTimeHour() {
        long elapsed = 0;
        if (running) {
            elapsed = ((((System.currentTimeMillis() - startTime) / 1000) / 60 ) / 60);
        }
        return elapsed;
    }

    public static long getVideoSeconds() {
        long elapsed = 0;
        if (running) {
            elapsed = ((System.currentTimeMillis() - startTime) / 1000);
        }
        return elapsed;
    }

    public static void reset() {
        currentTime = 0;
        running = false;
    }

    /*public static String getTime() {
        return  getElapsedTimeMin() + " : "
                + String.format("%2s", getElapsedTimeSecs()) + "." + String.format("%2s", getElapsedTimeMili()) + " seconds";
        *//*Log.d(DetectorService.LOG_TAG, getElapsedTimeHour() + ":" + getElapsedTimeMin() + ":"
                + getElapsedTimeSecs() + "." + getElapsedTimeMili());*//*
    }*/

    public static double getTime() {
        return Double.parseDouble(getVideoSeconds() +"." + Long.toString(getElapsedTimeMili()));
    }


}
