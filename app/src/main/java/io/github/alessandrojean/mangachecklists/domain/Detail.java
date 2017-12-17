package io.github.alessandrojean.mangachecklists.domain;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Desktop on 17/12/2017.
 */

public class Detail implements Parcelable {
    private String name;
    private String detail;

    public Detail() {
    }

    public Detail(String name, String detail) {
        this.name = name;
        this.detail = detail;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    protected Detail(Parcel in) {
        name = in.readString();
        detail = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(detail);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Detail> CREATOR = new Parcelable.Creator<Detail>() {
        @Override
        public Detail createFromParcel(Parcel in) {
            return new Detail(in);
        }

        @Override
        public Detail[] newArray(int size) {
            return new Detail[size];
        }
    };
}