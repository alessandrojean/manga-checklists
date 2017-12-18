package io.github.alessandrojean.mangachecklists.domain;

/**
 * Created by Desktop on 18/12/2017.
 */

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ChecklistData {

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

}
