package com.shayshab.androidnewsapp.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.shayshab.androidnewsapp.R;
import com.shayshab.androidnewsapp.config.AdsConfig;
import com.shayshab.androidnewsapp.config.AppConfig;
import com.shayshab.androidnewsapp.config.UiConfig;
import com.shayshab.androidnewsapp.models.News;
import com.shayshab.androidnewsapp.utils.Constant;
import com.shayshab.androidnewsapp.utils.NativeTemplateStyle;
import com.shayshab.androidnewsapp.utils.TemplateView;
import com.shayshab.androidnewsapp.utils.ThemePref;
import com.shayshab.androidnewsapp.utils.Tools;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.squareup.picasso.Picasso;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.Date;
import java.util.List;

public class AdapterRecent extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_PROG = 0;
    private final int VIEW_HEAD = 1;
    private final int VIEW_ITEM = 2;

    private List<Object> items;

    private boolean loading;
    private OnLoadMoreListener onLoadMoreListener;

    private Context context;
    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, News obj, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public AdapterRecent(Context context, RecyclerView view, List<Object> items) {
        this.items = items;
        this.context = context;
        lastItemViewDetector(view);
    }

    public class HeadingViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView title;
        public ImageView ic_date;
        public TextView date;
        public TextView category;
        public TextView comment;
        public ImageView image;
        public ImageView thumbnail_video;
        public RelativeLayout lyt_parent;
        public LinearLayout lyt_comment;

        public HeadingViewHolder(View v) {
            super(v);
            title = v.findViewById(R.id.title);
            ic_date = v.findViewById(R.id.ic_date);
            date = v.findViewById(R.id.date);
            category = v.findViewById(R.id.category_name);
            comment = v.findViewById(R.id.comment);
            image = v.findViewById(R.id.image);
            thumbnail_video = v.findViewById(R.id.thumbnail_video);
            lyt_parent = v.findViewById(R.id.lyt_parent);
            lyt_comment = v.findViewById(R.id.lyt_comment);
        }
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView title;
        public ImageView ic_date;
        public TextView date;
        public TextView category;
        public TextView comment;
        public ImageView image;
        public ImageView thumbnail_video;
        public LinearLayout lyt_parent;
        public LinearLayout lyt_comment;
        public TemplateView native_template;

        public OriginalViewHolder(View v) {
            super(v);
            title = v.findViewById(R.id.title);
            ic_date = v.findViewById(R.id.ic_date);
            date = v.findViewById(R.id.date);
            category = v.findViewById(R.id.category_name);
            comment = v.findViewById(R.id.comment);
            image = v.findViewById(R.id.image);
            thumbnail_video = v.findViewById(R.id.thumbnail_video);
            lyt_parent = v.findViewById(R.id.lyt_parent);
            lyt_comment = v.findViewById(R.id.lyt_comment);
            native_template = v.findViewById(R.id.native_template);
        }

        public void bindNativeAdView() {
            AdLoader adLoader = new AdLoader.Builder(context, context.getString(R.string.admob_native_unit_id))
                    .forUnifiedNativeAd(unifiedNativeAd -> {
                        ThemePref themePref = new ThemePref(context);
                        if (themePref.getIsDarkTheme()) {
                            ColorDrawable colorDrawable = new ColorDrawable(ContextCompat.getColor(context, R.color.colorBackgroundDark));
                            NativeTemplateStyle styles = new NativeTemplateStyle.Builder().withMainBackgroundColor(colorDrawable).build();
                            native_template.setStyles(styles);
                        } else {
                            ColorDrawable colorDrawable = new ColorDrawable(ContextCompat.getColor(context, R.color.colorBackgroundLight));
                            NativeTemplateStyle styles = new NativeTemplateStyle.Builder().withMainBackgroundColor(colorDrawable).build();
                            native_template.setStyles(styles);
                        }
                        native_template.setNativeAd(unifiedNativeAd);
                    }).withAdListener(new AdListener() {
                        @Override
                        public void onAdLoaded() {
                            super.onAdLoaded();
                            if (getAdapterPosition() % AdsConfig.NATIVE_AD_NEWS_FEED_INTERVAL == AdsConfig.NATIVE_AD_NEWS_FEED_INDEX) {
                                native_template.setVisibility(View.VISIBLE);
                            } else {
                                native_template.setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void onAdFailedToLoad(int errorCode) {
                            native_template.setVisibility(View.GONE);
                        }
                    })
                    .build();
            adLoader.loadAd(Tools.getAdRequest((Activity) context));
        }

    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {

        public ProgressBar progressBar;

        public ProgressViewHolder(View v) {
            super(v);
            progressBar = v.findViewById(R.id.progressBar1);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_HEAD:
                View headingItemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.lsv_item_heading, parent, false);
                return new HeadingViewHolder(headingItemView);
            case VIEW_ITEM:
                View menuItemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.lsv_item_recent, parent, false);
                return new OriginalViewHolder(menuItemView);
            case VIEW_PROG:
                // fall through
            default:
                View loadMoreView = LayoutInflater.from(parent.getContext()).inflate(R.layout.lsv_item_load_more, parent, false);
                return new ProgressViewHolder(loadMoreView);
        }

    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        int viewType = getItemViewType(position);

        switch (viewType) {
            case VIEW_HEAD:
                final News p = (News) items.get(position);
                HeadingViewHolder vItem = (HeadingViewHolder) holder;
                vItem.title.setText(Html.fromHtml(p.news_title));

                if (UiConfig.ENABLE_DATE_DISPLAY) {
                    vItem.date.setVisibility(View.VISIBLE);
                    vItem.ic_date.setVisibility(View.VISIBLE);
                } else {
                    vItem.date.setVisibility(View.GONE);
                    vItem.ic_date.setVisibility(View.GONE);
                }

                if (UiConfig.DATE_DISPLAY_AS_TIME_AGO) {
                    PrettyTime prettyTime = new PrettyTime();
                    long timeAgo = Tools.timeStringtoMilis(p.news_date);
                    vItem.date.setText(prettyTime.format(new Date(timeAgo)));
                } else {
                    vItem.date.setText(Tools.getFormatedDateSimple(p.news_date));
                }

                if (UiConfig.DISABLE_COMMENT) {
                    vItem.lyt_comment.setVisibility(View.GONE);
                }

                vItem.category.setText(Html.fromHtml(p.news_description));

                vItem.comment.setText(p.comments_count + "");

                if (p.content_type != null && p.content_type.equals("Post")) {
                    vItem.thumbnail_video.setVisibility(View.GONE);
                } else {
                    vItem.thumbnail_video.setVisibility(View.VISIBLE);
                }

                if (p.content_type != null && p.content_type.equals("youtube")) {
                    Picasso.get()
                            .load(Constant.YOUTUBE_IMG_FRONT + p.video_id + Constant.YOUTUBE_IMG_BACK)
                            .placeholder(R.drawable.ic_thumbnail)
                            .into(vItem.image);
                } else {
                    Picasso.get()
                            .load(AppConfig.ADMIN_PANEL_URL + "/upload/" + p.news_image.replace(" ", "%20"))
                            .placeholder(R.drawable.ic_thumbnail)
                            .into(vItem.image);
                }

                vItem.lyt_parent.setOnClickListener(view -> {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(view, p, position);
                    }
                });
                break;

            case VIEW_ITEM:
                final News news_item = (News) items.get(position);
                final OriginalViewHolder item_holder = (OriginalViewHolder) holder;

                if (AdsConfig.NATIVE_AD_ON_NEWS_FEED) {
                    if (holder.getAdapterPosition() % AdsConfig.NATIVE_AD_NEWS_FEED_INTERVAL == AdsConfig.NATIVE_AD_NEWS_FEED_INDEX) {
                        item_holder.bindNativeAdView();
                    } else {
                        item_holder.native_template.setVisibility(View.GONE);
                    }
                }

                item_holder.title.setText(Html.fromHtml(news_item.news_title));

                if (UiConfig.ENABLE_DATE_DISPLAY) {
                    item_holder.date.setVisibility(View.VISIBLE);
                    item_holder.ic_date.setVisibility(View.VISIBLE);
                } else {
                    item_holder.date.setVisibility(View.GONE);
                    item_holder.ic_date.setVisibility(View.GONE);
                }

                if (UiConfig.DATE_DISPLAY_AS_TIME_AGO) {
                    PrettyTime prettyTime = new PrettyTime();
                    long timeAgo = Tools.timeStringtoMilis(news_item.news_date);
                    item_holder.date.setText(prettyTime.format(new Date(timeAgo)));
                } else {
                    item_holder.date.setText(Tools.getFormatedDateSimple(news_item.news_date));
                }

                if (UiConfig.DISABLE_COMMENT) {
                    item_holder.lyt_comment.setVisibility(View.GONE);
                }

                item_holder.category.setText(Html.fromHtml(news_item.news_description));

                item_holder.comment.setText(news_item.comments_count + "");

                if (news_item.content_type != null && news_item.content_type.equals("Post")) {
                    item_holder.thumbnail_video.setVisibility(View.GONE);
                } else {
                    item_holder.thumbnail_video.setVisibility(View.VISIBLE);
                }

                if (news_item.content_type != null && news_item.content_type.equals("youtube")) {
                    Picasso.get()
                            .load(Constant.YOUTUBE_IMG_FRONT + news_item.video_id + Constant.YOUTUBE_IMG_BACK)
                            .placeholder(R.drawable.ic_thumbnail)
                            .into(item_holder.image);
                } else {
                    Picasso.get()
                            .load(AppConfig.ADMIN_PANEL_URL + "/upload/" + news_item.news_image.replace(" ", "%20"))
                            .placeholder(R.drawable.ic_thumbnail)
                            .into(item_holder.image);
                }

                item_holder.lyt_parent.setOnClickListener(view -> {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(view, news_item, position);
                    }
                });

                break;

            case VIEW_PROG:
                //fall through
            default:
                ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {

        if (items.get(position) != null) {
            if (position == 0) {
                return VIEW_HEAD;
            } else {
                return VIEW_ITEM;
            }
        } else {
            return VIEW_PROG;
        }
    }

    public void insertData(List<News> items) {
        setLoaded();
        int positionStart = getItemCount();
        int itemCount = items.size();
        this.items.addAll(items);
        notifyItemRangeInserted(positionStart, itemCount);
    }

    public void setLoaded() {
        loading = false;
        for (int i = 0; i < getItemCount(); i++) {
            if (items.get(i) == null) {
                items.remove(i);
                notifyItemRemoved(i);
            }
        }
    }

    public void setLoading() {
        if (getItemCount() != 0) {
            this.items.add(null);
            notifyItemInserted(getItemCount() - 1);
            loading = true;
        }
    }

    public void resetListData() {
        this.items.clear();
        notifyDataSetChanged();
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    private void lastItemViewDetector(RecyclerView recyclerView) {
        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
            final LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    int lastPos = layoutManager.findLastVisibleItemPosition();
                    if (!loading && lastPos == getItemCount() - 1 && onLoadMoreListener != null) {
                        if (onLoadMoreListener != null) {
                            int current_page = getItemCount() / UiConfig.LOAD_MORE;
                            onLoadMoreListener.onLoadMore(current_page);
                        }
                        loading = true;
                    }
                }
            });
        }
    }

    public interface OnLoadMoreListener {
        void onLoadMore(int current_page);
    }

}