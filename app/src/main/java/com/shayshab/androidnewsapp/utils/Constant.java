package com.shayshab.androidnewsapp.utils;

import static com.shayshab.androidnewsapp.config.AppConfig.ADMIN_PANEL_URL;

public class Constant {

    public static final String REGISTER_URL = ADMIN_PANEL_URL + "/api/user_register/?user_type=normal&name=";
    public static final String NORMAL_LOGIN_URL = ADMIN_PANEL_URL + "/api/get_user_login/?email=";
    public static final String FORGET_PASSWORD_URL = ADMIN_PANEL_URL + "/api/forgot_password/?email=";
    public static final String CATEGORY_ARRAY_NAME = "result";
    public static int GET_SUCCESS_MSG;
    public static final String MSG = "msg";
    public static final String SUCCESS = "success";
    public static final String USER_NAME = "name";
    public static final String USER_ID = "user_id";
    public static final long DELAY_REFRESH = 1000;
    public static final int DELAY_PROGRESS_DIALOG = 2000;

    public static final long DELAY_TIME = 1000;
    public static final long DELAY_TIME_MEDIUM = 500;
    public static final String YOUTUBE_IMG_FRONT = "https://img.youtube.com/vi/";
    public static final String YOUTUBE_IMG_BACK = "/mqdefault.jpg";
    public static final int MAX_SEARCH_RESULT = 100;

    public static final String TOPIC_GLOBAL = "global";
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String PUSH_NOTIFICATION = "pushNotification";
    public static final String SHARED_PREF = "ah_firebase";
    public static final String NOTIFICATION_CHANNEL_NAME = "news_channel_01";
    public static final int NOTIFICATION_ID = 100;
    public static final int NOTIFICATION_ID_BIG_IMAGE = 101;
    public static final String EXTRA_OBJC = "key.EXTRA_OBJC";

}