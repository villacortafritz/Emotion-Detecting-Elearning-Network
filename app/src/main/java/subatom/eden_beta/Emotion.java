package subatom.eden_beta;

import android.content.Context;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by JSP on 10/6/2017.
 */

public class Emotion {
    public static boolean detect = false;
    public static boolean startDetecting = false;
    public static ArrayList<Pair<Double,Float>> joy  = new ArrayList<>();
    public static ArrayList<Pair<Double,Float>> attention = new ArrayList<>();
    public static ArrayList<Pair<Double,Float>> brow_furrow = new ArrayList<>();
    public static ArrayList<Pair<Double,Float>> engagement = new ArrayList<>();
    public static ArrayList<Pair<Double,Float>> valence = new ArrayList<>();

    public static void addJoy(Pair p) { joy.add(p);}
    public static void addAttention(Pair p) { attention.add(p);}
    public static void addBrowFurrow(Pair p) { brow_furrow.add(p);}
    public static void addEngagement(Pair p) { engagement.add(p);}
    public static void addValence(Pair p) { valence.add(p);}
    //
    public static Pair<Double, Float> getJoy(int i) {return joy.get(i);}
    public static Pair<Double, Float> getAttention(int i) {return attention.get(i);}
    public static Pair<Double, Float> getBrowFurrow(int i) {return brow_furrow.get(i);}
    public static Pair<Double, Float> getEngagement(int i) {return engagement.get(i);}
    public static Pair<Double, Float> getValence(int i) {return valence.get(i);}



 /*   private static String printAnEmotion(int i) {
        return getAttention(i).toString();
    }

    public static void printResults(Context c) {
        String print = "Attention: ";
        *//*for (int i = 0; i < attention.size(); i+=100) {
            print += (printAnEmotion(i) + " ");
        }*//*
        print += (printAnEmotion(engagement.size() - 1) + " ");

        Toast.makeText(c, print, Toast.LENGTH_LONG).show();
    }*/

    public static void clearData() {
        joy.clear();
        attention.clear();
        engagement.clear();
        valence.clear();
        brow_furrow.clear();
    }







}
