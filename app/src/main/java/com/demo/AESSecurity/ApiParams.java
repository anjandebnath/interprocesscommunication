package com.demo.AESSecurity;

import android.os.Parcel;
import android.os.Parcelable;

public class ApiParams implements Parcelable {


    protected ApiParams(Parcel in) {
        name = in.readString();
        email = in.readString();
        appName = in.readString();
        apiKey = in.readString();
    }

    public static final Creator<ApiParams> CREATOR = new Creator<ApiParams>() {
        @Override
        public ApiParams createFromParcel(Parcel in) {
            return new ApiParams(in);
        }

        @Override
        public ApiParams[] newArray(int size) {
            return new ApiParams[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    String name;
    String email;
    String appName;
    String apiKey;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(email);
        dest.writeString(appName);
        dest.writeString(apiKey);
    }
}
