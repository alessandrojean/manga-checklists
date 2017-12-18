package io.github.alessandrojean.mangachecklists.domain;

/**
 * Created by Desktop on 18/12/2017.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Checklist {

    @SerializedName("month")
    @Expose
    private Integer month;
    @SerializedName("id")
    @Expose
    private Integer id;

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

}