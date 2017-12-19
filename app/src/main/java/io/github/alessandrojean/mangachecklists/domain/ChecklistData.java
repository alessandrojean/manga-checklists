package io.github.alessandrojean.mangachecklists.domain;

/**
 * Created by Desktop on 18/12/2017.
 */

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ChecklistData implements Parcelable {

    @SerializedName("checklists")
    @Expose
    private List<Checklist> checklists;
    @SerializedName("year")
    @Expose
    private Integer year;

    public ChecklistData() {
        this.checklists = new ArrayList<>();
    }

    public List<Checklist> getChecklists() {
        return checklists;
    }

    public void setChecklists(List<Checklist> checklists) {
        this.checklists = checklists;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }


    protected ChecklistData(Parcel in) {
        if (in.readByte() == 0x01) {
            checklists = new ArrayList<Checklist>();
            in.readList(checklists, Checklist.class.getClassLoader());
        } else {
            checklists = null;
        }
        year = in.readByte() == 0x00 ? null : in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (checklists == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(checklists);
        }
        if (year == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeInt(year);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<ChecklistData> CREATOR = new Parcelable.Creator<ChecklistData>() {
        @Override
        public ChecklistData createFromParcel(Parcel in) {
            return new ChecklistData(in);
        }

        @Override
        public ChecklistData[] newArray(int size) {
            return new ChecklistData[size];
        }
    };
}