package subatom.eden_beta;

import java.util.ArrayList;

/**
 * Created by JSP on 11/27/2017.
 */

public class Student {
    //public String id;
    public String name;
    public int age;
    public boolean excel;
    public String gender;
    String video_link;
    String video_title;
    ArrayList<Double> metrics = new ArrayList<Double>(); //in order of confusion, attention, engagement, positive valence, negative valence

    public ArrayList<Pair<Double,Float>> joy  = new ArrayList<>();
    public ArrayList<Pair<Double,Float>> attention = new ArrayList<>();
    public ArrayList<Pair<Double,Float>> brow_furrow = new ArrayList<>();
    public ArrayList<Pair<Double,Float>> engagement = new ArrayList<>();
    public ArrayList<Pair<Double,Float>> valence = new ArrayList<>();

    public Student(String name, int age, String gender, boolean excel, String link, String name_video, ArrayList<Double> e, ArrayList<Pair<Double,Float>> joy , ArrayList<Pair<Double,Float>> engagement , ArrayList<Pair<Double,Float>> attention, ArrayList<Pair<Double,Float>> brow_furrow, ArrayList<Pair<Double,Float>> valence) {
        //this.id = id;
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.excel = excel;
        this.video_link = link;
        this.metrics.addAll(e);
        this.joy.addAll(joy);
        this.video_title = name_video;
        this.attention.addAll(attention);
        this.brow_furrow.addAll(brow_furrow);
        this.engagement.addAll(engagement);
        this.valence.addAll(valence);
    }

}
