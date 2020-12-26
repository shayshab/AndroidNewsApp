package com.shayshab.androidnewsapp.activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.shayshab.androidnewsapp.R;
import com.shayshab.androidnewsapp.adapter.AdapterImageSlider;
import com.shayshab.androidnewsapp.callbacks.CallbackPostDetail;
import com.shayshab.androidnewsapp.config.AppConfig;
import com.shayshab.androidnewsapp.config.UiConfig;
import com.shayshab.androidnewsapp.models.Images;
import com.shayshab.androidnewsapp.rests.RestAdapter;
import com.shayshab.androidnewsapp.utils.NetworkCheck;
import com.shayshab.androidnewsapp.utils.Tools;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityImageSlider extends AppCompatActivity {

    private Call<CallbackPostDetail> callbackCall = null;
    ImageButton lyt_close, lyt_save;
    TextView txt_number;
    ViewPager viewPager;
    Long nid;
    int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Tools.getTheme(this);
        setContentView(R.layout.activity_image_slider);

        if (UiConfig.ENABLE_RTL_MODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }
        }

        lyt_close = findViewById(R.id.lyt_close);
        lyt_save = findViewById(R.id.lyt_save);
        txt_number = findViewById(R.id.txt_number);

        nid = getIntent().getLongExtra("nid", 0);
        position = getIntent().getIntExtra("position", 0);
        //post = (News) getIntent().getSerializableExtra(EXTRA_OBJC);
        requestAction();

        initToolbar();

    }

    private void requestAction() {
        showFailedView(false, "");
        requestPostData();
    }

    private void requestPostData() {
        this.callbackCall = RestAdapter.createAPI().getNewsDetail(nid);
        this.callbackCall.enqueue(new Callback<CallbackPostDetail>() {
            public void onResponse(Call<CallbackPostDetail> call, Response<CallbackPostDetail> response) {
                CallbackPostDetail responseHome = response.body();
                if (responseHome == null || !responseHome.status.equals("ok")) {
                    onFailRequest();
                    return;
                }
                displayAllData(responseHome);
            }

            public void onFailure(Call<CallbackPostDetail> call, Throwable th) {
                Log.e("onFailure", th.getMessage());
                if (!call.isCanceled()) {
                    onFailRequest();
                }
            }
        });
    }

    private void onFailRequest() {
        if (NetworkCheck.isConnect(ActivityImageSlider.this)) {
            showFailedView(true, getString(R.string.msg_no_network));
        } else {
            showFailedView(true, getString(R.string.msg_offline));
        }
    }

    private void showFailedView(boolean show, String message) {
        View lyt_failed = findViewById(R.id.lyt_failed_home);
        ((TextView) findViewById(R.id.failed_message)).setText(message);
        if (show) {
            lyt_failed.setVisibility(View.VISIBLE);
        } else {
            lyt_failed.setVisibility(View.GONE);
        }
        findViewById(R.id.failed_retry).setOnClickListener(view -> requestAction());
    }

    private void displayAllData(CallbackPostDetail responseHome) {
        displayImages(responseHome.images);
    }

    private void displayImages(final List<Images> list) {
        viewPager = findViewById(R.id.view_pager_image);
        final AdapterImageSlider adapter = new AdapterImageSlider(ActivityImageSlider.this, list);

        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(4);
        viewPager.setCurrentItem(position);

        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {

            public void onPageSelected(final int position) {
                super.onPageSelected(position);
                txt_number.setText((position + 1) + " of " + list.size());
                lyt_save.setOnClickListener(view -> requestStoragePermission(list, position));
            }
        });

        if (UiConfig.ENABLE_RTL_MODE) {
            viewPager.setRotationY(180);
        }

        txt_number.setText((position + 1) + " of " + list.size());
        lyt_save.setOnClickListener(view -> requestStoragePermission(list, position));

        lyt_close.setOnClickListener(view -> finish());

    }

    private void initToolbar() {
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setTitle("");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @TargetApi(16)
    private void requestStoragePermission(final List<Images> list, final int position) {
        Dexter.withActivity(ActivityImageSlider.this)
                .withPermissions(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            (new saveImage(ActivityImageSlider.this)).execute(AppConfig.ADMIN_PANEL_URL + "/upload/" + list.get(position).image_name);
                        }
                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // show alert dialog navigating to Setting
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).
                withErrorListener(error -> Toast.makeText(getApplicationContext(), "Error occurred! " + error.toString(), Toast.LENGTH_SHORT).show())
                .onSameThread()
                .check();
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityImageSlider.this);
        builder.setTitle(R.string.permission_msg);
        builder.setMessage(R.string.permission_upload);
        builder.setPositiveButton(R.string.dialog_settings, (dialog, which) -> {
            dialog.cancel();
            openSettings();
        });
        builder.setNegativeButton(R.string.dialog_cancel, (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    public class saveImage extends AsyncTask<String, String, String> {

        private Context context;
        private ProgressDialog pDialog;
        URL myFileUrl;
        Bitmap bmImg = null;
        File file;

        public saveImage(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {

            super.onPreExecute();

            pDialog = new ProgressDialog(context);
            pDialog.setMessage(getResources().getString(R.string.download_msg));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... args) {
            String as[] = null;
            try {
                myFileUrl = new URL(args[0]);
                HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
                conn.setDoInput(true);
                conn.connect();
                InputStream is = conn.getInputStream();
                bmImg = BitmapFactory.decodeStream(is);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {

                String path = myFileUrl.getPath();
                String idStr = path.substring(path.lastIndexOf('/') + 1);
                File filepath = Environment.getExternalStorageDirectory();
                File dir = new File(filepath.getAbsolutePath() + "/" + getResources().getString(R.string.app_name) + "/");
                dir.mkdirs();
                String fileName = "Image_" + "_" + idStr;
                file = new File(dir, fileName);
                FileOutputStream fos = new FileOutputStream(file);
                bmImg.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.flush();
                fos.close();
                as = new String[1];
                as[0] = file.toString();

                MediaScannerConnection.scanFile(ActivityImageSlider.this, as, null, (s1, uri) -> {
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String args) {

            new Handler().postDelayed(() -> {
                Toast.makeText(getApplicationContext(), R.string.download_success, Toast.LENGTH_SHORT).show();
                pDialog.dismiss();
            }, 3000);
        }
    }

    public void onDestroy() {
        if (!(callbackCall == null || callbackCall.isCanceled())) {
            this.callbackCall.cancel();
        }
        super.onDestroy();
    }


}
