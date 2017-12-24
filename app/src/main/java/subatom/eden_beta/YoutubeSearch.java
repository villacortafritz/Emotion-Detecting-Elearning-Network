package subatom.eden_beta;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class YoutubeSearch extends Activity {
    EditText etSearch;
    Button btnSearch;
    ProgressBar progressBar;
    ArrayList<VideoObject> YoutubeData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.youtube_activity_main);
        YoutubeData = new ArrayList<VideoObject>();
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        etSearch = (EditText) findViewById(R.id.etSearch);
        btnSearch = (Button) findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(new Search());
    }

    private static class HTTPRequest extends AsyncTask<String, Void, String> {
        private WeakReference<YoutubeSearch> activityReference;

        // only retain a weak reference to the activity
        HTTPRequest(YoutubeSearch context) {
            activityReference = new WeakReference<>(context);
        }

        @Override
        protected String doInBackground(String... urlString) {
            String result = "";
            StringBuilder sb = new StringBuilder();
            URL url;
            HttpURLConnection urlConnection = null;
            try {
                url = new URL(urlString[0]);

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader isw = new InputStreamReader(in);

                int data = isw.read();
                while (data != -1) {
                    char current = (char) data;
                    data = isw.read();
                    sb.append(current);
                }
                result = sb.toString();
            } catch (Exception e) {
                Toast.makeText(activityReference.get().getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return result;
        }

        @Override
        protected void onPostExecute(String message) {
            YoutubeSearch activity = activityReference.get();

            try{
                JSONObject JSON_Object = new JSONObject(message);
                JSONArray YT_Items = JSON_Object.getJSONArray("items");
                int length = YT_Items.length();
                int ctr = 0;
                for(int i=0; i<length ; i++){
                    if(ctr>=15)break;
                    ctr++;
                    /* Containers */
                    JSONObject data = YT_Items.getJSONObject(i);
                    VideoObject video = new VideoObject();

                    /* Video Info */
                    try{
                        video.setVideoId(data.getJSONObject("id").getString("videoId"));
                    } catch (Exception ex){ continue; }

                    video.setTitle(data.getJSONObject("snippet").getString("title"));
                    video.setDescription(data.getJSONObject("snippet").getString("description"));

                    /* Video Thumbnail */
                    try{
                        data = data.getJSONObject("snippet").getJSONObject("thumbnails");
                        video.setDef(data.getJSONObject("medium").getString("url")); // 320 x 180
                    } catch (Exception ex){ continue; }


                    /* Insert to list */
                    activity.YoutubeData.add(video);
                }
                activity.nextActivity();
            } catch (JSONException e) {
                Log.d("ERROR", "Something went wrong - JSONEXCEPTION");
            } catch (Exception ex){
                Toast.makeText(activity.getApplicationContext(), ex.toString(), Toast.LENGTH_LONG).show();
            }
        }
    }

    public void nextActivity(){
         /* Switch to next activity */
        Intent i = new Intent(this, VideoList.class);
        i.putExtra("YoutubeData", YoutubeData);
        startActivity(i);
        finish();
    }

    private class Search implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            if(etSearch.length()>0) {
                progressBar.setVisibility(View.VISIBLE);
                /* Build the query */
                Uri.Builder builder = new Uri.Builder();
                builder.scheme("https")
                        .authority("www.googleapis.com")
                        .appendPath("youtube")
                        .appendPath("v3")
                        .appendPath("search")
                        .appendQueryParameter("q", etSearch.getText().toString())
                        .appendQueryParameter("part", "snippet")
                        .appendQueryParameter("maxResults", "25")
                        .appendQueryParameter("key", "AIzaSyD7SrK4gozRDE1BE_9__G639Ml3Dl-JygI");
                String URL = builder.build().toString();

                /* Execute HTTP Requeset*/
                new HTTPRequest(YoutubeSearch.this).execute(URL);
            }
            else{

            }
        }
    }


}
