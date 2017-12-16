package subatom.eden_beta;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class VideoList extends Activity {
    ArrayList<VideoObject> YoutubeData;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_list);

        YoutubeData = (ArrayList<VideoObject>) getIntent().getSerializableExtra("YoutubeData");

        /* Get all ID */
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < YoutubeData.size(); i++) {
            sb.append(YoutubeData.get(i).getVideoId() + ",");


        }
        String URL = "https://www.googleapis.com/youtube/v3/videos?id=" + sb.toString() + "&part=contentDetails&key=AIzaSyD7SrK4gozRDE1BE_9__G639Ml3Dl-JygI";
        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... urlString) {
                String result = "";
                StringBuilder sb = new StringBuilder();
                java.net.URL url;
                HttpURLConnection urlConnection1 = null;
                try {
                    url = new URL(urlString[0]);
                    urlConnection1 = (HttpURLConnection) url.openConnection();
                    urlConnection1.connect();
                    InputStream in = urlConnection1.getInputStream();
                    InputStreamReader isw1 = new InputStreamReader(in);

                    int data = isw1.read();
                    while (data != -1) {
                        char current = (char) data;
                        data = isw1.read();
                        sb.append(current);
                    }
                    result = sb.toString();
                } catch (Exception e) {
                } finally {
                    if (urlConnection1 != null) {
                        urlConnection1.disconnect();
                    }
                }
                return result;
            }

            @Override
            protected void onPostExecute(String message) {
                try {
                    JSONObject JSON_Object = new JSONObject(message);
                    JSONArray YT_Items = JSON_Object.getJSONArray("items");

                    for (int i = 0; i < YoutubeData.size(); i++) {
                        JSONObject data = YT_Items.getJSONObject(i).getJSONObject("contentDetails");
                        YoutubeData.get(i).setLength(parseDuration(data.getString("duration")));
                    }

                    //getting the recyclerview from xml
                    recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setLayoutManager(new LinearLayoutManager(VideoList.this));

                    //creating recyclerview adapter
                    VideoAdapter adapter = new VideoAdapter(VideoList.this, YoutubeData);

                    //setting adapter to recycler view
                    recyclerView.setAdapter(adapter);
                } catch (JSONException e) {
                    Log.d("ERROR", "Something went wrong - JSONEXCEPTION");
                }
            }
        }.execute(URL);
    }

    public String parseDuration(String str){
        boolean hours=false, minutes=false, seconds=false;
        if(str.indexOf('H') >=0 ) hours = true;
        if(str.indexOf('M') >=0 ) minutes = true;
        if(str.indexOf('S') >=0 ) seconds = true;

        str = str.replace("PT","").replace("H",":").replace("M",":").replace("S","");
        String arr[]= str.split(":");

        if(!hours && !minutes && seconds){str = "0:" + arr[0]; }
        else if(!hours && minutes && !seconds){ str = arr[0]+":00"; }
        else if(!hours && minutes && seconds){ str = arr[0]+ ":" + String.format("%02d", Integer.parseInt(arr[1])); }
        else if(hours && !minutes && !seconds){ str = arr[0]+ ":00:00"; }
        else if(hours && !minutes && seconds){ str = arr[0]+ ":00:" + String.format("%02d", Integer.parseInt(arr[1]));; }
        else if(hours && minutes && !seconds){ str = arr[0]+ ":" + arr[1] +":00"; }
        else if(hours && minutes && seconds){ str = arr[0]+":"+ String.format("%02d:%02d", Integer.parseInt(arr[1]), Integer.parseInt(arr[2])); }

        return str;
    }


}
