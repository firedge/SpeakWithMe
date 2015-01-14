package com.fdgproject.firedge.speakwithme;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Firedge on 12/01/2015.
 */
public class Message implements Serializable{
    private String msg;
    private boolean clever;
    private Date date;

    public Message(String msg, boolean clever, Date date) {
        this.msg = msg;
        this.clever = clever;
        this.date = date;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public boolean isClever() {
        return clever;
    }

    public void setClever(boolean clever) {
        this.clever = clever;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String fecha(){
        SimpleDateFormat dt = new SimpleDateFormat("dd/MM/yyyy hh:mm");
        return dt.format(date);
    }
}
