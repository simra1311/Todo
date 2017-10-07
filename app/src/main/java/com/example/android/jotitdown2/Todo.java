package com.example.android.jotitdown2;

import java.io.Serializable;

/**
 * Created by Simra Afreen on 24-09-2017.
 */

public class Todo implements Serializable {
    public long id;
    private String title;
    private String note;
    private long epoch;
    private int hour,minute;

    public Todo(Long id, String title, String note,long epoch,int hr,int min) {
        this.id = id;
        this.title = title;
        this.note = note;
        this.epoch = epoch;
        this.hour = hr;
        this.minute = min;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getEpoch() {
        return epoch;
    }

    public void setEpoch(long epoch) {
        this.epoch = epoch;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }
}
