package io.github.alessandrojean.mangachecklists.domain;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Calendar;

/**
 * Created by Desktop on 16/12/2017.
 */

public class Plan implements Parcelable {
    private Manga manga;
    private long sentDate;
    private String gift;

    public Plan() {
    }

    public Plan(Manga manga, long sentDate, String gift) {
        this.manga = manga;
        this.sentDate = sentDate;
        this.gift = gift;
    }

    protected Plan(Parcel in) {
        manga = (Manga) in.readValue(Manga.class.getClassLoader());
        sentDate = in.readLong();
        gift = in.readString();
    }

    public Manga getManga() {
        return manga;
    }

    public void setManga(Manga manga) {
        this.manga = manga;
    }

    public long getSentDate() {
        return sentDate;
    }

    public String getFormattedSentDate() {
        if (this.sentDate == 0)
            return "";

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(this.sentDate);

        return String.format(
                "%02d/%02d/%d",
                calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.YEAR)
        );
    }

    public void setSentDate(long sentDate) {
        this.sentDate = sentDate;
    }

    public void setSentDate(String day, String month, String year) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Integer.parseInt(year), Integer.parseInt(month) - 1, Integer.parseInt(day));

        this.sentDate = calendar.getTimeInMillis();
    }

    public String getGift() {
        return gift;
    }

    public void setGift(String gift) {
        this.gift = gift;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(manga);
        dest.writeLong(sentDate);
        dest.writeString(gift);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Plan> CREATOR = new Parcelable.Creator<Plan>() {
        @Override
        public Plan createFromParcel(Parcel in) {
            return new Plan(in);
        }

        @Override
        public Plan[] newArray(int size) {
            return new Plan[size];
        }
    };
}