package com.whatscan.whatsweb.whatzweb.whatwebscan.whatsapp_video;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.whatscan.whatsweb.whatzweb.whatwebscan.Utils.Common;
import com.whatscan.whatsweb.whatzweb.whatwebscan.model.StatusModel;
import com.whatscan.whatsweb.whatzweb.whatwebscan.R;
import com.whatscan.whatsweb.whatzweb.whatwebscan.databinding.RowStatusBinding;
import com.whatscan.whatsweb.whatzweb.whatwebscan.whatsapp_photos.ItemViewHolder;

import java.util.List;


public class VideoAdapter extends RecyclerView.Adapter<ItemViewHolder> {

    private final List<StatusModel> videoList;
    private Context context;
    private final RelativeLayout container;

    public VideoAdapter(List<StatusModel> videoList, RelativeLayout container) {
        this.videoList = videoList;
        this.container = container;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        context = parent.getContext();
        @NonNull RowStatusBinding itemBinding = RowStatusBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ItemViewHolder(itemBinding);

    }

    @Override
    public void onBindViewHolder(@NonNull final ItemViewHolder holder, int position) {

        final StatusModel status = videoList.get(position);

        if (status.isVideo()) {
            holder.ivVideo.setVisibility(View.VISIBLE);
        } else {
            holder.ivVideo.setVisibility(View.GONE);
        }
        if (status.isApi30()) {
//            holder.save.setVisibility(View.GONE);
            Glide.with(context).load(status.getDocumentFile().getUri()).into(holder.imageView);
        } else {
            holder.save.setVisibility(View.VISIBLE);
            Glide.with(context).load(status.getFile()).into(holder.imageView);
        }

        holder.share.setOnClickListener(v -> {

            Intent shareIntent = new Intent(Intent.ACTION_SEND);

            shareIntent.setType("image/mp4");
            if (status.isApi30()) {
                shareIntent.putExtra(Intent.EXTRA_STREAM, status.getDocumentFile().getUri());
            } else {
                shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + status.getFile().getAbsolutePath()));
            }
            context.startActivity(Intent.createChooser(shareIntent, "Share image"));

        });

        LayoutInflater inflater = LayoutInflater.from(context);
        final View view1 = inflater.inflate(R.layout.row_video_full_screen, null);

        holder.imageView.setOnClickListener(v -> {

            final AlertDialog.Builder alertDg = new AlertDialog.Builder(context);

            FrameLayout mediaControls = view1.findViewById(R.id.videoViewWrapper);

            if (view1.getParent() != null) {
                ((ViewGroup) view1.getParent()).removeView(view1);
            }

            alertDg.setView(view1);

            final VideoView videoView = view1.findViewById(R.id.video_full);

            final MediaController mediaController = new MediaController(context, false);

            videoView.setOnPreparedListener(mp -> {

                mp.start();
                mediaController.show(0);
                mp.setLooping(true);
            });

            videoView.setMediaController(mediaController);
            mediaController.setMediaPlayer(videoView);

            if (status.isApi30()) {
                videoView.setVideoURI(status.getDocumentFile().getUri());
            } else {
                videoView.setVideoURI(Uri.fromFile(status.getFile()));
            }
            videoView.requestFocus();

            ((ViewGroup) mediaController.getParent()).removeView(mediaController);

            if (mediaControls.getParent() != null) {
                mediaControls.removeView(mediaController);
            }

            mediaControls.addView(mediaController);

            final AlertDialog alert2 = alertDg.create();

            alert2.getWindow().getAttributes().windowAnimations = R.style.SlidingDialogAnimation;
            alert2.requestWindowFeature(Window.FEATURE_NO_TITLE);
            alert2.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            alert2.show();

        });

        holder.save.setOnClickListener(v -> Common.copyFile(status, context, container));

    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

}
