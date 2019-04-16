package com.kevin.appclient;

import android.os.Parcel;
import android.os.Parcelable;

public class Book implements Parcelable {
    public String name;
    public int id;

    public Book() {
    }

    public Book(int bookId, String s) {
        name = s;
        id = bookId;
    }

    protected Book(Parcel in) {
        name = in.readString();
        id = in.readInt();
    }

    public static final Creator<Book> CREATOR = new Creator<Book>() {
        @Override
        public Book createFromParcel(Parcel in) {
            return new Book(in);
        }

        @Override
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(id);
    }
}