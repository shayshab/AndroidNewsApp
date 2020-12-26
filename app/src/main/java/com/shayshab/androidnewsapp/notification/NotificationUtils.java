package com.shayshab.androidnewsapp.notification;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.shayshab.androidnewsapp.R;
import com.shayshab.androidnewsapp.activities.ActivityNotificationDetail;
import com.shayshab.androidnewsapp.activities.ActivityWebView;
import com.shayshab.androidnewsapp.activities.ActivityWebViewImage;
import com.shayshab.androidnewsapp.config.UiConfig;
import com.squareup.picasso.Picasso;

import java.util.List;

public class NotificationUtils {

    private Context context;

    public NotificationUtils(Context context) {
        this.context = context;
    }

    public static boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }

        return isInBackground;
    }

    public void playNotificationSound() {
        try {
            Uri sound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getPackageName() + "");
            Ringtone r = RingtoneManager.getRingtone(context, sound);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Clears notification tray messages
    public static void clearNotifications(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

    public static void oneSignalNotificationHandler(Activity activity, Intent intent) {

        if (intent.hasExtra("nid")) {

            long nid = intent.getLongExtra("nid", 0);
            String url =intent.getStringExtra("external_link");

            if (nid == 0) {
                if (url.equals("") || url.equals("no_url")) {
                    Log.d("OneSignal", "do nothing");
                } else {
                    Intent act1 = new Intent(activity, ActivityWebView.class);
                    act1.putExtra("url", url);
                    activity.startActivity(act1);
                }
            } else {
                Intent act2 = new Intent(activity, ActivityNotificationDetail.class);
                act2.putExtra("id", nid);
                activity.startActivity(act2);
            }

        }

    }

    public static void fcmNotificationHandler(Activity activity, Intent intent) {

        long fcm_id = intent.getLongExtra("id", 1L);
        String url = intent.getStringExtra("link");
        if (fcm_id != 1L) {
            if (fcm_id == 0) {
                if (!url.equals("")) {
                    if (UiConfig.OPEN_LINK_INSIDE_APP) {
                        if (url.startsWith("http://")) {
                            Intent a = new Intent(activity, ActivityWebView.class);
                            a.putExtra("url", url);
                            activity.startActivity(a);
                        }
                        if (url.startsWith("https://")) {
                            Intent b = new Intent(activity, ActivityWebView.class);
                            b.putExtra("url", url);
                            activity.startActivity(b);
                        }
                        if (url.endsWith(".jpg") || url.endsWith(".jpeg") || url.endsWith(".png")) {
                            Intent c = new Intent(activity, ActivityWebViewImage.class);
                            c.putExtra("image_url", url);
                            activity.startActivity(c);
                        }
                        if (url.endsWith(".pdf")) {
                            Intent d = new Intent(Intent.ACTION_VIEW);
                            d.setData(Uri.parse(url));
                            activity.startActivity(d);
                        }
                    } else {
                        Intent e = new Intent(Intent.ACTION_VIEW);
                        e.setData(Uri.parse(url));
                        activity.startActivity(e);
                    }
                }
                Log.d("FCM_INFO", " id : " + fcm_id);
            } else {
                Intent action = new Intent(activity, ActivityNotificationDetail.class);
                action.putExtra("id", fcm_id);
                activity.startActivity(action);
                Log.d("FCM_INFO", "id : " + fcm_id);
            }

        }
    }

    public static void showDialogNotification(Activity activity, Intent intent) {

        final long id = intent.getLongExtra("id", 1L);
        final String title = intent.getStringExtra("title");
        final String message = intent.getStringExtra("message");
        final String image_url = intent.getStringExtra("image_url");
        final String url = intent.getStringExtra("link");

        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(activity);
        View view = layoutInflaterAndroid.inflate(R.layout.custom_dialog, null);

        final AlertDialog.Builder alert = new AlertDialog.Builder(activity);
        alert.setView(view);

        final TextView notification_title = view.findViewById(R.id.title);
        final TextView notification_message = view.findViewById(R.id.message);
        final ImageView notification_image = view.findViewById(R.id.big_image);

        if (id != 1L) {
            if (id == 0) {
                if (!url.equals("")) {
                    notification_title.setText(title);
                    notification_message.setText(Html.fromHtml(message));
                    Picasso.get()
                            .load(image_url.replace(" ", "%20"))
                            .placeholder(R.drawable.ic_thumbnail)
                            .into(notification_image);
                    alert.setPositiveButton("Open link", (dialogInterface, i) -> {
                        if (UiConfig.OPEN_LINK_INSIDE_APP) {
                            if (url.startsWith("http://")) {
                                Intent intent1 = new Intent(activity, ActivityWebView.class);
                                intent1.putExtra("url", url);
                                activity.startActivity(intent1);
                            }
                            if (url.startsWith("https://")) {
                                Intent intent1 = new Intent(activity, ActivityWebView.class);
                                intent1.putExtra("url", url);
                                activity.startActivity(intent1);
                            }
                            if (url.endsWith(".jpg") || url.endsWith(".jpeg") || url.endsWith(".png")) {
                                Intent intent1 = new Intent(activity, ActivityWebViewImage.class);
                                intent1.putExtra("image_url", url);
                                activity.startActivity(intent1);
                            }
                            if (url.endsWith(".pdf")) {
                                Intent intent1 = new Intent(Intent.ACTION_VIEW);
                                intent1.setData(Uri.parse(url));
                                activity.startActivity(intent1);
                            }
                        } else {
                            Intent intent1 = new Intent(Intent.ACTION_VIEW);
                            intent1.setData(Uri.parse(url));
                            activity.startActivity(intent1);
                        }
                    });
                    alert.setNegativeButton(activity.getResources().getString(R.string.dialog_dismiss), null);
                } else {
                    notification_title.setText(title);
                    notification_message.setText(Html.fromHtml(message));
                    Picasso.get()
                            .load(image_url.replace(" ", "%20"))
                            .placeholder(R.drawable.ic_thumbnail)
                            .into(notification_image);
                    alert.setPositiveButton(activity.getResources().getString(R.string.dialog_ok), null);
                }
            } else {
                notification_title.setText(title);
                notification_message.setText(Html.fromHtml(message));
                Picasso.get()
                        .load(image_url.replace(" ", "%20"))
                        .placeholder(R.drawable.ic_thumbnail)
                        .into(notification_image);

                alert.setPositiveButton(activity.getResources().getString(R.string.dialog_read_more), (dialog, which) -> {
                    Intent intent12 = new Intent(activity, ActivityNotificationDetail.class);
                    intent12.putExtra("id", id);
                    activity.startActivity(intent12);
                });
                alert.setNegativeButton(activity.getResources().getString(R.string.dialog_dismiss), null);
            }
            alert.setCancelable(false);
            alert.show();

        }

    }

}
