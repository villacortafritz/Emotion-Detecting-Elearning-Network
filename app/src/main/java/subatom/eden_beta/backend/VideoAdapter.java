package subatom.eden_beta.backend;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import subatom.eden_beta.activity.MainActivity;
import subatom.eden_beta.R;
import subatom.eden_beta.model.VideoObject;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder>{

    private Context mCtx;
    private ArrayList<VideoObject> VideoLists;

    public VideoAdapter(Context mCtx, ArrayList<VideoObject> VideoList) {
        this.mCtx = mCtx;
        this.VideoLists = VideoList;
    }

    @Override
    public VideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.list_layout, null);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final VideoViewHolder holder, int position) {
        final VideoObject video = VideoLists.get(position);
        Picasso.with(mCtx).load(video.getDef()).into(holder.imageView);
        holder.lblTitle.setText(video.getTitle());
        holder.lblDescription.setText(video.getDescription());
        holder.lblDuration.setText(video.getLength());
        holder.Video_ID.setText(video.getVideoId());
    }

    @Override
    public int getItemCount() {
        return VideoLists.size();
    }

    class VideoViewHolder extends RecyclerView.ViewHolder{
        TextView lblDescription, lblTitle, lblDuration, Video_ID;
        ImageView imageView;
        public View view;

        public VideoViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String id = Video_ID.getText().toString();
                    VideoObject video = null;

                    for(VideoObject currVideo : VideoLists){
                        if(currVideo.getVideoId().equals(id)){
                            video = currVideo;
                            break;
                        }
                    }

                    Intent i = new Intent(mCtx, MainActivity.class);
                    i.putExtra("video", video);
                    mCtx.startActivity(i);
                }
            });

            lblDescription = itemView.findViewById(R.id.lblDescription);
            Video_ID = itemView.findViewById(R.id.Video_ID);
            lblDuration = itemView.findViewById(R.id.lblDuration);
            lblTitle = itemView.findViewById(R.id.lblTitle);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}
