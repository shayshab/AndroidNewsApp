package com.shayshab.androidnewsapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.shayshab.androidnewsapp.R;
import com.shayshab.androidnewsapp.config.AppConfig;
import com.shayshab.androidnewsapp.config.UiConfig;
import com.shayshab.androidnewsapp.models.Images;
import com.shayshab.androidnewsapp.utils.Constant;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterImage extends PagerAdapter {

    private Context context;
    private List<Images> items;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, Images images, int position);
    }

    public boolean isViewFromObject(@NonNull View view, @NonNull Object obj) {
        return view == obj;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public AdapterImage(Context context, List<Images> list) {
        this.context = context;
        this.items = list;
    }

    @NonNull
    public Object instantiateItem(ViewGroup viewGroup, final int position) {
        final Images post = items.get(position);
        View inflate = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.lsv_item_image_detail, viewGroup, false);

        ImageView news_image = inflate.findViewById(R.id.image_detail);

        if (post.content_type != null && post.content_type.equals("youtube")) {
            Picasso.get()
                    .load(Constant.YOUTUBE_IMG_FRONT + post.video_id + Constant.YOUTUBE_IMG_BACK)
                    .placeholder(R.drawable.ic_thumbnail)
                    .into(news_image);
        } else if (post.content_type != null && post.content_type.equals("Url")) {

            Picasso.get()
                    .load(AppConfig.ADMIN_PANEL_URL + "/upload/" + post.image_name.replace(" ", "%20"))
                    .placeholder(R.drawable.ic_thumbnail)
                    .into(news_image);
        } else if (post.content_type != null && post.content_type.equals("Upload")) {

            Picasso.get()
                    .load(AppConfig.ADMIN_PANEL_URL + "/upload/" + post.image_name.replace(" ", "%20"))
                    .placeholder(R.drawable.ic_thumbnail)
                    .into(news_image);
        } else {
            Picasso.get()
                    .load(AppConfig.ADMIN_PANEL_URL + "/upload/" + post.image_name.replace(" ", "%20"))
                    .placeholder(R.drawable.ic_thumbnail)
                    .into(news_image);
        }

        if (UiConfig.ENABLE_RTL_MODE) {
            news_image.setRotationY(180);
        }

        news_image.setOnClickListener(view -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(view, post, position);
            }
        });

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