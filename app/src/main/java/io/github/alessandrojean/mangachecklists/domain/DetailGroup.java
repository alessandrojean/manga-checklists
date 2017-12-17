package io.github.alessandrojean.mangachecklists.domain;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Desktop on 17/12/2017.
 */

public class DetailGroup implements Parcelable {
    private String name;
    private List<Detail> details;

    public DetailGroup() {
        this.details = new ArrayList<>();
    }

    public DetailGroup(String name, List<Detail> details) {
        this.name = name;
        this.details = details;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Detail> getDetails() {
        return details;
    }

    public void setDetails(List<Detail> details) {
        this.details = details;
    }

    protected DetailGroup(Parcel in) {
        name = in.readString();
        if (in.readByte() == 0x01) {
            details = new ArrayList<Detail>();
            in.readList(details, Detail.class.getClassLoader());
        } else {
            details = null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        if (details == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(details);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<DetailGroup> CREATOR = new Parcelable.Creator<DetailGroup>() {
        @Override
        public DetailGroup createFromParcel(Parcel in) {
            return new DetailGroup(in);
        }

        @Override
        public DetailGroup[] newArray(int size) {
            return new DetailGroup[size];
        }
    };
}