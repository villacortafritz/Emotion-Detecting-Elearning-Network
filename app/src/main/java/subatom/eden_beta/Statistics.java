package subatom.eden_beta;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.jar.Attributes;

public class Statistics extends Activity {

    double confusion = 0f, attention = 0f, engagement = 0f, positive = 0f, negative = 0f;

    private Button btnSend;
    private Button btnBack;
    private Button btnReplay;
    private EditText txtStat;
    //private TextView txtStat;
    private EmotionPoints e = new EmotionPoints();

    private String linkReplay;
    String len_video;
    LineChart lineChart;

    ArrayList<ILineDataSet> dataSets = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        btnSend = (Button)findViewById(R.id.btnSend);
        lineChart = (LineChart) findViewById(R.id.lineChart);
        linkReplay = getIntent().getStringExtra("url");

        String student_id = getIntent().getStringExtra("student_id");
        len_video = getIntent().getStringExtra("length_video");

        attention = Double.parseDouble(len_video);
        txtStat = (EditText) findViewById(R.id.editTextStat);
        txtStat.setFocusable(false);
        btnBack = (Button)findViewById(R.id.btnToMain);
        btnReplay = (Button)findViewById(R.id.btnReplay);
        e.drawChart();
        txtStat.setText(/*e.size + "\n\n" +*/e.updateConfusions() + "\n\n" + e.updateEngagement() + "\n\n" + e.updateNotPayingAttention()  + "\n\n" + e.getOverallValence());
        btnBack.setOnClickListener(new Backer());
        btnReplay.setOnClickListener(new VideoReplayer());
        btnSend.setOnClickListener(new EmailSender());


        LineData data = new LineData(dataSets);
        lineChart.setData(data);
    }

    public class EmotionPoints {
        int start, end;
        String point;
        int size = Emotion.attention.size(); //since pareha rag size tanan

        public void drawChart() {
            ArrayList<Entry> confuse = new ArrayList<>();
            ArrayList<Entry> attention = new ArrayList<>();
            ArrayList<Entry> engagement = new ArrayList<>();
            ArrayList<Entry> joy = new ArrayList<>();
            ArrayList<Entry> valence = new ArrayList<>();
            // point = "Brow Furrow: \n";
            String dum = null, dum2 = null;
            for (int i = 0; i < size; i++) {
                //point += ("" + Emotion.getBrowFurrow(i).getL() + " seconds reading of  " + Emotion.getBrowFurrow(i).getR() + "\n");
                dum2 = Emotion.getBrowFurrow(i).getL().toString();
                dum = Emotion.getBrowFurrow(i).getR().toString();
                confuse.add(new Entry((float)Double.parseDouble(dum2), Float.parseFloat(dum)));
                dum2 = Emotion.getAttention(i).getL().toString();
                dum = Emotion.getAttention(i).getR().toString();
                attention.add(new Entry((float)Double.parseDouble(dum2), Float.parseFloat(dum)));
                dum2 = Emotion.getEngagement(i).getL().toString();
                dum = Emotion.getEngagement(i).getR().toString();
                engagement.add(new Entry((float)Double.parseDouble(dum2), Float.parseFloat(dum)));
                dum2 = Emotion.getJoy(i).getL().toString();
                dum = Emotion.getJoy(i).getR().toString();
                joy.add(new Entry((float)Double.parseDouble(dum2), Float.parseFloat(dum)));
                dum2 = Emotion.getValence(i).getL().toString();
                dum = Emotion.getValence(i).getR().toString();
                valence.add(new Entry((float)Double.parseDouble(dum2), Float.parseFloat(dum)));
            }

            LineDataSet data1 = new LineDataSet(confuse,"Confuse");
            LineDataSet data2 = new LineDataSet(attention,"Attention");
            LineDataSet data3 = new LineDataSet(engagement,"Engagement");
            LineDataSet data4 = new LineDataSet(joy,"Engagement");
            LineDataSet data5 = new LineDataSet(valence,"Valence");
            data1.setColor(Color.BLUE);
            data1.setDrawCircles(false);
            data2.setColor(Color.YELLOW);
            data2.setDrawCircles(false);
            data3.setColor(Color.GRAY);
            data3.setDrawCircles(false);
            data4.setColor(Color.MAGENTA);
            data4.setDrawCircles(false);
            data5.setColor(Color.GREEN);
            data5.setDrawCircles(false);


            dataSets.add(data1);
            dataSets.add(data2);
            dataSets.add(data3);
            dataSets.add(data4);
            dataSets.add(data5);


        }

        public String allAttention() {

            String dum= null,dum2 = null;
            point = "Attention: \n";
            for (int i = 0; i < size; i++) {
                point += ("" + Emotion.getAttention(i).getL() + " seconds reading of  " + Emotion.getAttention(i).getR() + "\n");
                dum2 = Emotion.getAttention(i).getL().toString();
                dum = Emotion.getAttention(i).getR().toString();
                // attention.add(new Entry((float)Double.parseDouble(dum2), Float.parseFloat(dum)));
            }
            return point;
        }

        public String updateEngagement() {
            boolean hasStart = false, hasEnd = false;
            point = "I seem to be having difficulties between these time frames: \n";
            //Toast.makeText(getApplicationContext(), " " + Emotion.brow_furrow.get(10).getR() + " and " + Emotion.brow_furrow.get(60).getR(), Toast.LENGTH_SHORT).show();
            for (int i = 0; i < size; i++) {
                if (Emotion.engagement.get(i).getR() >= 80 && !hasStart) {
                    start = i;
                    hasStart = true;
                }

                else if (Emotion.engagement.get(i).getR() < 80 && !hasEnd && hasStart) {
                    end = i;
                    hasEnd = true;
                }

                if (hasStart && hasEnd) {
                    hasStart = false;
                    hasEnd = false;
                    //point += (" " + Emotion.brow_furrow.get(start).getL() + " and " + Emotion.brow_furrow.get(end).getL() + "\n");
                    point += ("" + Emotion.getEngagement(start).getL() + " and " + Emotion.getEngagement(end).getL() + "\n");
                    //engagement += (Emotion.getEngagement(end).getL() - Emotion.getEngagement(start).getL());
                    start = 0;
                    end = 0;

                }
            }
//            start = 0;
//            end = 0;
            return point;
        }

        public String updateConfusions() {
            boolean hasStart = false, hasEnd = false;
            point = "I seem to be having difficulties between these time frames: \n";
            //Toast.makeText(getApplicationContext(), " " + Emotion.brow_furrow.get(10).getR() + " and " + Emotion.brow_furrow.get(60).getR(), Toast.LENGTH_SHORT).show();
            for (int i = 0; i < size; i++) {
                if (Emotion.brow_furrow.get(i).getR() >= 80 && !hasStart) {
                    start = i;
                    hasStart = true;
                }

                else if (Emotion.brow_furrow.get(i).getR() < 80 && !hasEnd && hasStart) {
                    end = i;
                    hasEnd = true;
                }

                if (hasStart && hasEnd) {
                    hasStart = false;
                    hasEnd = false;
                    //point += (" " + Emotion.brow_furrow.get(start).getL() + " and " + Emotion.brow_furrow.get(end).getL() + "\n");
                    point += ("" + Emotion.getBrowFurrow(start).getL() + " and " + Emotion.getBrowFurrow(end).getL() + "\n");
                    //confusion += (Emotion.getBrowFurrow(end).getL() - Emotion.getBrowFurrow(start).getL());
                    start = 0;
                    end = 0;

                }
            }
//            start = 0;
//            end = 0;
            return point;
        }

        public String updateNotPayingAttention() {
            boolean hasStart = false, hasEnd = false;
            point = "I didn't pay attention between these time frames: \n";

            for (int i = 0; i < size; i++) {
                if (Emotion.attention.get(i).getR() <= 70 && !hasStart) {
                    start = i;
                    hasStart = true;
                }

                else if (Emotion.attention.get(i).getR() > 70 && !hasEnd  && hasStart) {
                    end = i;
                    hasEnd = true;
                    if (hasStart && hasEnd) {
                        hasStart = false;
                        hasEnd = false;
                        point += (" " + Emotion.attention.get(start).getL() + " and " + Emotion.attention.get(end).getL() + "\n");
                        //attention = attention - (Emotion.getAttention(end).getL() - Emotion.getAttention(start).getL());
                        start = 0;
                        end = 0;

                    }
                }
//                start = 0;
//                end = 0;
            }
            return point;
        }

        public String getOverallValence() {
            point = "Your overall valence is: \n";
            double val = 0;
            for (int i = 0; i < size; i++) {
                val += Emotion.getValence(i).getR();
            }
            //point += Double.toString(val/size);
            point += Double.toString(val);
            boolean hasStart = false, hasEnd = false;

            for (int i = 0; i < size; i++) {
                if (Emotion.valence.get(i).getR() >= 0 && !hasStart) {
                    start = i;
                    hasStart = true;
                }

                else if (Emotion.valence.get(i).getR() > 70 && !hasEnd  && hasStart) {
                    end = i;
                    hasEnd = true;
                    if (hasStart && hasEnd) {
                        hasStart = false;
                        hasEnd = false;
                        //point += (" " + Emotion.attention.get(start).getL() + " and " + Emotion.attention.get(end).getL() + "\n");
                        positive += (Emotion.getValence(end).getL() - Emotion.getValence(start).getL());
                        start = 0;
                        end = 0;

                    }
                }
            }
            negative = Double.parseDouble(len_video) - positive;
            if (positive >= negative) {
                point += " positive. You seem to be interested and delighted to watch the video.";
            }
            else {
                point += " negative. You don't seem too impressed with the video.";
            }
            return point;
        }
    }

    public class EmailSender implements View.OnClickListener{
        public void onClick(View v){
            Date currentTime = Calendar.getInstance().getTime();
            String s = currentTime.toString();
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setData(Uri.parse("mailto:"));
            String[] to={""};
            intent.putExtra(Intent.EXTRA_EMAIL, to);
            intent.putExtra(Intent.EXTRA_SUBJECT, "EDEN Results of "+s);
            intent.putExtra(Intent.EXTRA_TEXT, txtStat.getText().toString());
            intent.setType("message/rfc822");
            Intent chooser = Intent.createChooser(intent, "Send Email");
            startActivity(chooser);

        }
    }

    public class Backer implements View.OnClickListener{
        public void onClick(View v){
            Emotion.clearData();
            Intent i = new Intent(Statistics.this, MainActivity.class);
            startActivity(i);
        }
    }

    private class VideoReplayer implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            Emotion.clearData();
            Intent i = new Intent(Statistics.this, YoutubePlayer.class);
            i.putExtra("url", linkReplay);
            startActivity(i);
        }
    }
}