package subatom.eden_beta.backend;

/**
 * Created by JSP on 11/28/2017.
 */

//A helper class for pairing a reading of an emotion in a certain period of time in the video
public class Pair<L,R> {
    private L l;
    private R r;
    public Pair(L l, R r){
        this.l = l;
        this.r = r;
    }
    public L getL(){ return l; }
    public R getR(){ return r; }
    public void setL(L l){ this.l = l; }
    public void setR(R r){ this.r = r; }
    public String toString() { return l.toString() + " " + r.toString() + "     ";}
}