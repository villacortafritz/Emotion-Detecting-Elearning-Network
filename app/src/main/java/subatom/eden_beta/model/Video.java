package subatom.eden_beta;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by JSP on 11/27/2017.
 */

public class Video {
    String video_id;
    String video_link;
    ArrayList<Double> metrics = new ArrayList<Double>(); //in order of confusion, attention, engagement, positive valence, negative valence

    public ArrayList<Pair<Double,Float>> joy  = new ArrayList<>();
    public ArrayList<Pair<Double,Float>> attention = new ArrayList<>();
    public ArrayList<Pair<Double,Float>> brow_furrow = new ArrayList<>();
    public ArrayList<Pair<Double,Float>> engagement = new ArrayList<>();
    public ArrayList<Pair<Double,Float>> valence = new ArrayList<>();

    public Video(String id, String link, ArrayList<Double> e, ArrayList<Pair<Double,Float>> joy , ArrayList<Pair<Double,Float>> engagement , ArrayList<Pair<Double,Float>> attention, ArrayList<Pair<Double,Float>> brow_furrow, ArrayList<Pair<Double,Float>> valence) {
        this.video_id = id;
        this.video_link = link;
        this.metrics.addAll(e);
        this.joy.addAll(joy);
        this.attention.addAll(attention);
        this.brow_furrow.addAll(brow_furrow);
        this.engagement.addAll(engagement);
        this.valence.addAll(valence);
    }



}