package com.gunho0406.imagecash;

public class Item {
    String user,bitmap,title,date,subject,content;
    int imgnum;

    public Item(String user, String bitmap, String title, String date, String subject,String content, int imgnum) {
        super();
        this.user = user;
        this.bitmap = bitmap;
        this.title = title;
        this.date = date;
        this.subject = subject;
        this.content = content;
        this.imgnum = imgnum;
    }
}
