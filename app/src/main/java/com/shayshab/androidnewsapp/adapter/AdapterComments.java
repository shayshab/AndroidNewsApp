package com.shayshab.androidnewsapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.shayshab.androidnewsapp.R;
import com.shayshab.androidnewsapp.activities.MyApplication;
import com.shayshab.androidnewsapp.config.AppConfig;
import com.shayshab.androidnewsapp.models.Comments;
import com.shayshab.androidnewsapp.utils.Tools;
import com.squareup.picasso.Picasso;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AdapterComments extends RecyclerView.Adapter<AdapterComments.ViewHolder> {

    private List<Comments> items;
    MyApplication myApplication;
    private Context ctx;
    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, Comments obj, int position, Context context);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public AdapterComments(Context context, List<Comments> items) {
        this.items = items;
        ctx = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView user_name;
        public ImageView user_image;
        public TextView comment_date;
        public TextView comment_message;
        public LinearLayout lyt_parent;

        public ViewHolder(View v) {
            super(v);
            user_name = v.findViewById(R.id.user_name);
            user_image = v.findViewById(R.id.user_image);
            comment_date = v.findViewById(R.id.comment_date);
            comment_message = v.findViewById(R.id.edt_comment_message);
            lyt_parent = v.findViewById(R.id.lyt_parent);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.lsv_item_comments, parent, false);
        ViewHolder vh = new ViewHolder(v);
        myApplication = MyApplication.getInstance();
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Comments c = items.get(position);

        if (myApplication.getIsLogin() && myApplication.getUserId().equals(c.user_id)) {
            holder.user_name.setText(c.name + " ( " + ctx.getResources().getString(R.string.txt_you) + " )");
        } else {
            holder.user_name.setText(c.name);
        }

        Picasso.get()
                .load(AppConfig.ADMIN_PANEL_URL + "/upload/avatar/" + c.image.replace(" ", "%20"))
                .resize(200, 200)
                .centerCrop()
                .placeholder(R.drawable.ic_user_account)
                .into(holder.user_image);


        //holder.comment_date.setText(c.date_time);
        PrettyTime prettyTime = new PrettyTime();
        long timeAgo = Tools.timeStringtoMilis(c.date_time);
        holder.comment_date.setText(prettyTime.format(new Date(timeAgo)));

        holder.comment_message.setText(c.content);

        holder.lyt_parent.setOnClickListener(view -> {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(view, c, position, ctx);
            }
        });
    }

    public void setListData(List<Comments> items){
        this.items = items;
        notifyDataSetChanged();
    }

    public void resetListData() {
        this.items = new ArrayList<>();
        notifyDataSetChanged();
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return items.size();
    }

}