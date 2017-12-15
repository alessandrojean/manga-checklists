package io.github.alessandrojean.mangachecklists.domain;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.util.Calendar;

/**
 * Created by Desktop on 14/12/2017.
 */

public class Manga implements Parcelable, Comparable<Manga> {
    public static final String MANGAS_KEY = "mangas_key";

    private String name;
    private int volume;
    private long date;
    private String thumbnailUrl;
    private String url;

    public Manga() {

    }

    public Manga(String name, int volume, long date, String thumbnailUrl, String url) {
        this.name = name;
        this.volume = volume;
        this.date = date;
        this.thumbnailUrl = thumbnailUrl;
        this.url = url;
    }

    protected Manga(Parcel in) {
        name = in.readString();
        volume = in.readInt();
        date = in.readLong();
        thumbnailUrl = in.readString();
        url = in.readString();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public long getDate() {
        return date;
    }

    public String getFormattedDate() {
        if (this.date == 0)
            return "";

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(this.date);

        return String.format(
                "%02d/%02d/%d",
                calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.YEAR)
        );
    }

    public void setDate(long date) {
        this.date = date;
    }

    public void setDate(String day, String month, int year) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, Integer.parseInt(month) - 1, Integer.parseInt(day));

        this.date = calendar.getTimeInMillis();
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(volume);
        dest.writeLong(date);
        dest.writeString(thumbnailUrl);
        dest.writeString(url);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Manga> CREATOR = new Parcelable.Creator<Manga>() {
        @Override
        public Manga createFromParcel(Parcel in) {
            return new Manga(in);
        }

        @Override
        public Manga[] newArray(int size) {
            return new Manga[size];
        }
    };

    @Override
    public int compareTo(@NonNull Manga manga) {
        return (int) (this.date - manga.getDate());
    }
}