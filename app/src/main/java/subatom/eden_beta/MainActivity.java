package subatom.eden_beta;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

public class MainActivity extends Activity {
    private Button btnPlay;
    private TextView txtMainName,txtTitle;

    private String student_name;
    private String student_id;
    private String student_gender;
    private int student_age;
    private boolean student_excel;

    private String videoID = null,title;
    private ImageView img, search;

    private VideoObject video;

    public class Transferrer implements View.OnClickListener{ //Pending
        public void onClick (View v){
            if(video != null){
                Intent i = new Intent(MainActivity.this, YoutubePlayer.class);
                i.putExtra("url", videoID);
                i.putExtra("videoTitle", title);
                //i.putExtra("student_id", student_id);
                i.putExtra("student_name", student_name);
                i.putExtra("student_age", student_age);
                i.putExtra("student_gender", student_gender);
                i.putExtra("student_excel", student_excel);
                startActivity(i);
            }else{
                Toast.makeText(getApplicationContext(),"Search for a video first",Toast.LENGTH_SHORT).show();
            }

        }
    }

    private class VideoSearcher implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            startActivity(new Intent(MainActivity.this, YoutubeSearch.class));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        img= (ImageView) findViewById(R.id.imageView);
        btnPlay = (Button)findViewById(R.id.btnplay);
        txtMainName = (TextView)findViewById(R.id.txtMainName);
        search = (ImageView) findViewById(R.id.youtube);
        txtTitle = (TextView) findViewById(R.id.txtTitle);

//        btnPlay.setEnabled(false);
        Intent intent = getIntent();

        /*Get the video*/
        if(intent.hasExtra("video")){
            video = (VideoObject) intent.getSerializableExtra("video");
            Picasso.with(this).load(video.getDef()).into(img);
            videoID = video.getVideoId();
            //txtTitle.setText(video.getTitle());
            title = video.getTitle();
            txtTitle.setText(title);
            //btnPlay.setEnabled(true);
        }

        /*Get the user info*/
        try{
            student_name = intent.getStringExtra("student_name");
            //student_id = intent.getStringExtra("student_id");
            student_gender = intent.getStringExtra("student_gender");
            student_age = intent.getIntExtra("student_age", 8);
            student_excel = intent.getBooleanExtra("student_excel", true);

            txtMainName.setText("Hello there!");
        } catch (Exception ex) { }

        btnPlay.setOnClickListener(new Transferrer());
        search.setOnClickListener(new VideoSearcher());
    }
    protected void onResume() {
        super.onResume();
    }
}
