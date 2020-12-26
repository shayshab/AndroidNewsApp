package com.shayshab.androidnewsapp.models;

import java.io.Serializable;

public class Setting implements Serializable {

    public String package_name;
    public String comment_approval;
    public String privacy_policy;

    public String getPackage_name() {
        return package_name;
    }

    public void setPackage_name(String package_name) {
        this.package_name = package_name;
    }

    public String getComment_approval() {
        return comment_approval;
    }

    public void setComment_approval(String comment_approval) {
        this.comment_approval = comment_approval;
    }

    public String getPrivacy_policy() {
        return privacy_policy;
    }

    public void setPrivacy_policy(String privacy_policy) {
        this.privacy_policy = privacy_policy;
    }
}
