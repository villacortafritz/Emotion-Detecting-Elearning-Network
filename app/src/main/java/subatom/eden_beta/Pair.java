package subatom.eden_beta;

/**
 * Created by JSP on 11/28/2017.
 */

//A helper class for pairing a reading of an emotion in a certain period of time in the video
public class Pair<L,R> {
    private L timeframe;
    private R metric;
    public Pair(L l, R r){
        this.timeframe = l;
        this.metric = r;
    }
    public L getL(){ return timeframe; }
    public R getR(){ return metric; }
    public void setL(L l){ this.timeframe = l; }
    public void setR(R r){ this.metric = r; }
    public String toString() { return timeframe.toString() + " " + metric.toString() + "     ";}
}