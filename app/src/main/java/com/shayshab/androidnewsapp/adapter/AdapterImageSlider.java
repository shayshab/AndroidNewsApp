package com.shayshab.androidnewsapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.shayshab.androidnewsapp.R;
import com.shayshab.androidnewsapp.activities.ActivityVideoPlayer;
import com.shayshab.androidnewsapp.activities.ActivityYoutubePlayer;
import com.shayshab.androidnewsapp.config.AppConfig;
import com.shayshab.androidnewsapp.config.UiConfig;
import com.shayshab.androidnewsapp.models.Images;
import com.shayshab.androidnewsapp.utils.Constant;
import com.shayshab.androidnewsapp.utils.TouchImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterImageSlider extends PagerAdapter {

    private Context context;
    private List<Images> items;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, Images images);
    }

    public boolean isViewFromObject(@NonNull View view, @NonNull Object obj) {
        return view == obj;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public AdapterImageSlider(Context context, List<Images> list) {
        this.context = context;
        this.items = list;
    }

    @NonNull
    public Object instantiateItem(ViewGroup viewGroup, final int position) {
        final Images post = items.get(position);
        View inflate = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.lsv_item_image_slider, viewGroup, false);

        TouchImageView news_image = inflate.findViewById(R.id.image_detail);

        if (post.content_type != null && post.content_type.equals("youtube")) {
            Picasso.get()
                    .load(Constant.YOUTUBE_IMG_FRONT + post.video_id + Constant.YOUTUBE_IMG_BACK)
                    .placeholder(R.drawable.ic_thumbnail)
                    .into(news_image);

            news_image.setOnClickListener(v -> {
                Intent intent = new Intent(context, ActivityYoutubePlayer.class);
                intent.putExtra("video_id", post.video_id);
                context.startActivity(intent);
            });

        } else if (post.content_type != null && post.content_type.equals("Url")) {

            Picasso.get()
                    .load(AppConfig.ADMIN_PANEL_URL + "/upload/" + post.image_name.replace(" ", "%20"))
                    .placeholder(R.drawable.ic_thumbnail)
                    .into(news_image);

            news_image.setOnClickListener(v -> {
                Intent intent = new Intent(context, ActivityVideoPlayer.class);
                intent.putExtra("video_url", post.video_url);
                context.startActivity(intent);
            });
        } else if (post.content_type != null && post.content_type.equals("Upload")) {

            Picasso.get()
                    .load(AppConfig.ADMIN_PANEL_URL + "/upload/" + post.image_name.replace(" ", "%20"))
                    .placeholder(R.drawable.ic_thumbnail)
                    .into(news_image);

            news_image.setOnClickListener(v -> {
                Intent intent = new Intent(context, ActivityVideoPlayer.class);
                intent.putExtra("video_url", AppConfig.ADMIN_PANEL_URL + "/upload/video/" + post.video_url);
                context.startActivity(intent);
            });
        } else {
            Picasso.get()
                    .load(AppConfig.ADMIN_PANEL_URL + "/upload/" + post.image_name.replace(" ", "%20"))
                    .placeholder(R.drawable.ic_thumbnail)
                    .into(news_image);

            news_image.setOnClickListener(view -> {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(view, post);
                }
            });

        }

        if (UiConfig.ENABLE_RTL_MODE) {
            news_image.setRotationY(180);
        }

        viewGroup.addView(inflate);
        return inflate;
    }

    public int getCount() {
        return this.items.size();
    }

    public void destroyItem(ViewGroup viewGroup, int i, Object obj) {
        viewGroup.removeView((View) obj);
    }

}