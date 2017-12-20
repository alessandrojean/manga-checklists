package io.github.alessandrojean.mangachecklists.domain;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Desktop on 14/12/2017.
 */

public class Manga implements Parcelable, Comparable<Manga> {
    public static final String MANGAS_KEY = "mangas_key";
    public static final int TYPE_JBC = 0;
    public static final int TYPE_PANINI = 1;
    public static final int TYPE_NEWPOP = 2;

    private String name;
    private int volume;
    private long date;
    private double price;
    private String thumbnailUrl;
    private String url;
    private int type;

    private String subtitle;
    private String synopsis;
    private String headerUrl;

    private List<DetailGroup> detailGroups;

    public Manga() {
        this.detailGroups = new ArrayList<>();
    }

    public Manga(String name, int volume, long date, String thumbnailUrl, String url) {
        this.name = name;
        this.volume = volume;
        this.date = date;
        this.thumbnailUrl = thumbnailUrl;
        this.url = url;

        this.detailGroups = new ArrayList<>();
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

    public void setDate(String dateStr) {
        try {
            Date date = new SimpleDateFormat("dd/MM/yyyy").parse(dateStr);

            this.date = date.getTime();
        } catch (ParseException e) { }
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

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    public String getHeaderUrl() {
        return headerUrl;
    }

    public void setHeaderUrl(String headerUrl) {
        this.headerUrl = headerUrl;
    }

    public List<DetailGroup> getDetailGroups() {
        return detailGroups;
    }

    public void setDetailGroups(List<DetailGroup> detailGroups) {
        this.detailGroups = detailGroups;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public int compareTo(@NonNull Manga manga) {
        /*if (this.date != 0 && manga.getDate() != 0)
            return (int) (this.date - manga.getDate());
        else if (this.date == 0)
            return -1;
        else
            return 1;*/

        if (this.date == 0 && manga.getDate() == 0)
            return 0;
        if (this.date == 0)
            return 1;
        if (manga.getDate() == 0)
            return -1;
        return (int) (this.date - manga.getDate());
    }

    protected Manga(Parcel in) {
        name = in.readString();
        volume = in.readInt();
        date = in.readLong();
        price = in.readDouble();
        thumbnailUrl = in.readString();
        url = in.readString();
        type = in.readInt();
        subtitle = in.readString();
        synopsis = in.readString();
        headerUrl = in.readString();
        if (in.readByte() == 0x01) {
            detailGroups = new ArrayList<DetailGroup>();
            in.readList(detailGroups, DetailGroup.class.getClassLoader());
        } else {
            detailGroups = null;
        }
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
        dest.writeDouble(price);
        dest.writeString(thumbnailUrl);
        dest.writeString(url);
        dest.writeInt(type);
        dest.writeString(subtitle);
        dest.writeString(synopsis);
        dest.writeString(headerUrl);
        if (detailGroups == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(detailGroups);
        }
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
}