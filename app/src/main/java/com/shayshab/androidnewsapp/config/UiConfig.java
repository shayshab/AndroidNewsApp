package com.shayshab.androidnewsapp.config;

public class UiConfig {

    //layout configuration
    public static final boolean ENABLE_FIXED_BOTTOM_NAVIGATION = true;
    public static final boolean ENABLE_POST_COUNT_IN_CATEGORY = false;
    public static final boolean FORCE_PLAYER_TO_LANDSCAPE = true;
    public static final boolean ENABLE_DATE_DISPLAY = true;
    public static final boolean DATE_DISPLAY_AS_TIME_AGO = false;
    public static final boolean ENABLE_EXIT_DIALOG = false;

    //disable comment
    //if comment disabled, login and register feature will be disabled too
    public static final boolean DISABLE_COMMENT = false;

    //news description configuration
    public static final boolean ENABLE_TEXT_SELECTION = true;
    public static final boolean OPEN_LINK_INSIDE_APP = true;

    //push notification
    //if it disabled, all previous received notification will not updated and still displayed in the status bar
    public static final boolean UPDATE_PREVIOUS_NOTIFICATION = false;

    //if you use RTL Language e.g : Arabic Language or other, set true
    public static final boolean ENABLE_RTL_MODE = false;

    //load more for next news list
    public static final int LOAD_MORE = 20;

    //splash screen duration in millisecond
    public static final int SPLASH_TIME = 3000;

}
