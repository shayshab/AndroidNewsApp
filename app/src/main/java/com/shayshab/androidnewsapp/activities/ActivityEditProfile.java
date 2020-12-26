package com.shayshab.androidnewsapp.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.shayshab.androidnewsapp.R;
import com.shayshab.androidnewsapp.config.AppConfig;
import com.shayshab.androidnewsapp.config.UiConfig;
import com.shayshab.androidnewsapp.models.User;
import com.shayshab.androidnewsapp.rests.ApiInterface;
import com.shayshab.androidnewsapp.rests.RestAdapter;
import com.shayshab.androidnewsapp.utils.Constant;
import com.shayshab.androidnewsapp.utils.ThemePref;
import com.shayshab.androidnewsapp.utils.Tools;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityEditProfile extends AppCompatActivity {

    EditText edt_email, edt_name, edt_password;
    MyApplication myApplication;
    Button btn_update;
    RelativeLayout lyt_profile;
    ImageView img_change;
    Bitmap bitmap;
    ImageView profile_image;
    ImageView tmp_image;
    ProgressDialog progressDialog;
    String str_name, str_email, str_image, str_password, str_new_image, str_old_image;
    private static final int IMAGE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Tools.getTheme(this);
        setContentView(R.layout.activity_edit_profile);

        if (UiConfig.ENABLE_RTL_MODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }
        }

        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ThemePref themePref = new ThemePref(this);
        if (themePref.getIsDarkTheme()) {
            toolbar.setBackgroundColor(getResources().getColor(R.color.colorToolbarDark));
        } else {
            toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        }

        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setTitle(R.string.title_menu_edit_profile);
        }

        Intent intent = getIntent();
        str_name = intent.getStringExtra("name");
        str_email = intent.getStringExtra("email");
        str_image = intent.getStringExtra("user_image");
        str_password = intent.getStringExtra("password");

        progressDialog = new ProgressDialog(ActivityEditProfile.this);
        progressDialog.setTitle(getResources().getString(R.string.title_please_wait));
        progressDialog.setMessage(getResources().getString(R.string.logout_process));
        progressDialog.setCancelable(false);

        myApplication = MyApplication.getInstance();

        profile_image = findViewById(R.id.profile_image);
        tmp_image = findViewById(R.id.tmp_image);
        img_change = findViewById(R.id.btn_change_image);

        lyt_profile = findViewById(R.id.lyt_profile);

        edt_email = findViewById(R.id.edt_email);
        edt_name = findViewById(R.id.edt_user);
        edt_password = findViewById(R.id.edt_password);

        edt_name.setText(str_name);
        edt_email.setText(str_email);
        edt_password.setText(str_password);
        displayProfileImage();

        img_change.setOnClickListener(view -> selectImage());

        btn_update = findViewById(R.id.btn_update);
        btn_update.setOnClickListener(view -> updateUserData());

    }

    private void displayProfileImage() {
        if (str_image.equals("")) {
            profile_image.setImageResource(R.drawable.ic_user_account);
        } else {
            Picasso.get()
                    .load(AppConfig.ADMIN_PANEL_URL + "/upload/avatar/" + str_image.replace(" ", "%20"))
                    .resize(300, 300)
                    .centerCrop()
                    .placeholder(R.drawable.ic_user_account)
                    .into(profile_image);
        }
    }

    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMAGE);
    }

    private String convertToString() {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imgByte = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imgByte, Base64.DEFAULT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE && resultCode == RESULT_OK && data != null) {
            Uri path = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), path);
                tmp_image.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void updateUserData() {

        progressDialog = new ProgressDialog(ActivityEditProfile.this);
        progressDialog.setTitle(R.string.updating_profile);
        progressDialog.setMessage(getResources().getString(R.string.waiting_message));
        progressDialog.setCancelable(false);
        progressDialog.show();

        str_name = edt_name.getText().toString();
        str_email = edt_email.getText().toString();
        str_password = edt_password.getText().toString();

        if (bitmap != null) {
            uploadImage();
        } else {
            updateData();
        }
    }

    private void updateData() {

        ApiInterface apiInterface = RestAdapter.createAPI();
        Call<User> call = apiInterface.updateUserData(myApplication.getUserId(), str_name, str_email, str_password);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                new Handler().postDelayed(() -> {
                    progressDialog.dismiss();
                    AlertDialog.Builder builder = new AlertDialog.Builder(ActivityEditProfile.this);
                    builder.setMessage(R.string.success_updating_profile);
                    builder.setPositiveButton(getResources().getString(R.string.dialog_ok), (dialogInterface, i) -> finish());
                    builder.setCancelable(false);
                    builder.show();
                }, Constant.DELAY_PROGRESS_DIALOG);
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                progressDialog.dismiss();
            }
        });

    }

    private void uploadImage() {

        str_old_image = str_image;
        str_new_image = convertToString();

        ApiInterface apiInterface = RestAdapter.createAPI();
        Call<User> call = apiInterface.updatePhotoProfile(myApplication.getUserId(), str_name, str_email, str_password, str_old_image, str_new_image);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                new Handler().postDelayed(() -> {
                    progressDialog.dismiss();
                    AlertDialog.Builder builder = new AlertDialog.Builder(ActivityEditProfile.this);
                    builder.setMessage(R.string.success_updating_profile);
                    builder.setPositiveButton(getResources().getString(R.string.dialog_ok), (dialogInterface, i) -> finish());
                    builder.setCancelable(false);
                    builder.show();
                }, Constant.DELAY_PROGRESS_DIALOG);
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                progressDialog.dismiss();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
