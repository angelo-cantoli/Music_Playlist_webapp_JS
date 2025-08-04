package it.polimi.tiw.progetto_tiw_js.beans;

import com.google.gson.annotations.SerializedName;

import java.sql.Date;

public class Playlist {
    @SerializedName("id")
    private int id;
    private String title;
    private Date creationDate;
    private int creatorId;
    private boolean customOrder;
    private String sorting;

    public String getTitle() {
        return title;
    }

    public int getId() {
        return id;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public int getCreatorId() {
        return creatorId;
    }

    public boolean hasCustomOrder() {
        return customOrder;
    }

    public String getSorting() {
        return sorting;
    }

    public void setSorting(String sorting) {
        this.sorting = sorting;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCreationDate(Date date) {
        this.creationDate = date;
    }


    public void setCreatorId(int creatorId) {
        this.creatorId = creatorId;
    }


    public void setCustomOrder(boolean customOrder) {
        this.customOrder = customOrder;
    }
}
