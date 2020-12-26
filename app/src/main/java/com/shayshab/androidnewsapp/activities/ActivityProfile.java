package com.shayshab.androidnewsapp.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.shayshab.androidnewsapp.BuildConfig;
import com.shayshab.androidnewsapp.R;
import com.shayshab.androidnewsapp.callbacks.CallbackUser;
import com.shayshab.androidnewsapp.config.AppConfig;
import com.shayshab.androidnewsapp.config.UiConfig;
import com.shayshab.androidnewsapp.models.User;
import com.shayshab.androidnewsapp.rests.ApiInterface;
import com.shayshab.androidnewsapp.rests.RestAdapter;
import com.shayshab.androidnewsapp.utils.Constant;
import com.shayshab.androidnewsapp.utils.NetworkCheck;
import com.shayshab.androidnewsapp.utils.ThemePref;
import com.shayshab.androidnewsapp.utils.Tools;
import com.balysv.materialripple.MaterialRippleLayout;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityProfile extends AppCompatActivity {

    private Call<CallbackUser> callbackCall = null;
    MyApplication myApplication;
    User user;
    View lyt_sign_in, lyt_sign_out;
    TextView txt_login;
    TextView txt_register, txt_username, txt_email;
    ImageView img_profile;
    ProgressDialog progressDialog;
    MaterialRippleLayout btn_logout;
    ThemePref themePref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Tools.getTheme(this);
        setContentView(R.layout.activity_profile);

        if (UiConfig.ENABLE_RTL_MODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }
        }

        themePref = new ThemePref(this);

        myApplication = MyApplication.getInstance();

        setupToolbar();
        initComponent();
        requestAction();

    }

    public void setupToolbar() {
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (themePref.getIsDarkTheme()) {
            toolbar.setBackgroundColor(getResources().getColor(R.color.colorToolbarDark));
        } else {
            toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        }

        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setTitle(getResources().getString(R.string.title_menu_profile));
        }
    }

    private void initComponent() {
        lyt_sign_in = findViewById(R.id.view_sign_in);
        lyt_sign_out = findViewById(R.id.view_sign_out);

        txt_login = findViewById(R.id.btn_login);
        txt_register = findViewById(R.id.txt_register);

        txt_username = findViewById(R.id.txt_username);
        txt_email = findViewById(R.id.txt_email);
        img_profile = findViewById(R.id.img_profile);

        btn_logout = findViewById(R.id.btn_logout);

        Switch switch_theme = findViewById(R.id.switch_theme);
        if (themePref.getIsDarkTheme()) {
            switch_theme.setChecked(true);
        } else {
            switch_theme.setChecked(false);
        }

        switch_theme.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Log.e("INFO", "" + isChecked);
            themePref.setIsDarkTheme(isChecked);
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });

        findViewById(R.id.btn_privacy_policy).setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), ActivityPrivacyPolicy.class)));
        findViewById(R.id.btn_rate).setOnClickListener(view -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID))));
        findViewById(R.id.btn_more).setOnClickListener(view -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.play_more_apps)))));
        findViewById(R.id.btn_about).setOnClickListener(view -> aboutDialog());

    }

    private void requestAction() {
        if (myApplication.getIsLogin()) {
            lyt_sign_in.setVisibility(View.VISIBLE);
            lyt_sign_out.setVisibility(View.GONE);

            btn_logout.setVisibility(View.VISIBLE);
            btn_logout.setOnClickListener(view -> logoutDialog());

            requestPostApi();
        } else {
            lyt_sign_in.setVisibility(View.GONE);
            lyt_sign_out.setVisibility(View.VISIBLE);
            txt_login.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), ActivityUserLogin.class)));
            txt_register.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), ActivityUserRegister.class)));
            btn_logout.setVisibility(View.GONE);
        }
    }

    private void requestPostApi() {
        ApiInterface apiInterface = RestAdapter.createAPI();
        callbackCall = apiInterface.getUser(myApplication.getUserId());
        callbackCall.enqueue(new Callback<CallbackUser>() {
            @Override
            public void onResponse(Call<CallbackUser> call, Response<CallbackUser> response) {
                CallbackUser resp = response.body();
                if (resp != null && resp.status.equals("ok")) {
                    user = resp.response;
                    displayData();
                } else {
                    onFailRequest();
                }
            }

            @Override
            public void onFailure(Call<CallbackUser> call, Throwable t) {
                if (!call.isCanceled()) onFailRequest();
            }

        });
    }

    public void displayData() {

        ((TextView) findViewById(R.id.txt_username)).setText(user.name);

        ((TextView) findViewById(R.id.txt_email)).setText(user.email);

        ImageView img_profile = findViewById(R.id.img_profile);
        if (user.image.equals("")) {
            img_profile.setImageResource(R.drawable.ic_user_account);
        } else {
            Picasso.get()
                    .load(AppConfig.ADMIN_PANEL_URL + "/upload/avatar/" + user.image.replace(" ", "%20"))
                    .resize(300, 300)
                    .centerCrop()
                    .placeholder(R.drawable.ic_user_account)
                    .into(img_profile);
        }

        RelativeLayout btn_edit = findViewById(R.id.btn_edit);
        btn_edit.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), ActivityEditProfile.class);
            intent.putExtra("name", user.name);
            intent.putExtra("email", user.email);
            intent.putExtra("user_image", user.image);
            intent.putExtra("password", user.password);
            startActivity(intent);
        });

    }

    private void onFailRequest() {
        if (!NetworkCheck.isConnect(this)) {
            Toast.makeText(getApplicationContext(), getString(R.string.msg_no_network), Toast.LENGTH_SHORT).show();
        }
    }

    public void logoutDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityProfile.this);
        builder.setTitle(R.string.logout_title);
        builder.setMessage(R.string.logout_message);
        builder.setPositiveButton(R.string.dialog_yes, (di, i) -> {

            progressDialog = new ProgressDialog(ActivityProfile.this);
            progressDialog.setTitle(getResources().getString(R.string.title_please_wait));
            progressDialog.setMessage(getResources().getString(R.string.logout_process));
            progressDialog.setCancelable(false);
            progressDialog.show();

            MyApplication.getInstance().saveIsLogin(false);

            new Handler().postDelayed(() -> {
                progressDialog.dismiss();
                AlertDialog.Builder builder1 = new AlertDialog.Builder(ActivityProfile.this);
                builder1.setMessage(R.string.logout_success);
                builder1.setPositiveButton(R.string.dialog_ok, (dialogInterface, i1) -> finish());
                builder1.setCancelable(false);
                builder1.show();
            }, Constant.DELAY_PROGRESS_DIALOG);

        });
        builder.setNegativeButton(R.string.dialog_cancel, null);
        builder.show();

    }

    public void aboutDialog() {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(ActivityProfile.this);
        View view = layoutInflaterAndroid.inflate(R.layout.custom_dialog_about, null);
        final AlertDialog.Builder alert = new AlertDialog.Builder(ActivityProfile.this);
        alert.setView(view);
        alert.setCancelable(false);
        alert.setPositiveButton(R.string.dialog_ok, (dialog, which) -> dialog.dismiss());
        alert.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {

            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        requestAction();
    }

}
