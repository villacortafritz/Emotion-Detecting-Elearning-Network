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

    private String name;
    private String student_id;

    private String videoID = null,title;
    private ImageView img, search;

    private VideoObject video;

    public class Transferrer implements View.OnClickListener{ //Pending
        public void onClick (View v){
            if(video != null){
                Intent i = new Intent(MainActivity.this, YoutubePlayer.class);
                i.putExtra("url", videoID);
                i.putExtra("student_id", student_id);
                i.putExtra("student_name", name);
                startActivity(i);
            }else{
                Toast.makeText(getApplicationContext(),"Search for a video first",Toast.LENGTH_SHORT).show();
            }

        }
    }

    private class Search implements View.OnClickListener {
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
            name = intent.getStringExtra("student_name");
            student_id = intent.getStringExtra("student_id");
            txtMainName.setText("Hello there!");
        } catch (Exception ex) { }

        btnPlay.setOnClickListener(new Transferrer());
        search.setOnClickListener(new Search());
    }
    protected void onResume() {
        super.onResume();
    }
}
