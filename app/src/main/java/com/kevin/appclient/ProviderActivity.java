package com.kevin.appclient;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * Created by Kevin on 2019/4/10<br/>
 * Blog:https://blog.csdn.net/student9128<br/>
 * Describe:<br/>
 */
public class ProviderActivity extends AppCompatActivity {
    private static final String TAG = "ProviderActivity";
    private ContentResolver contentResolver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Uri bookUri = Uri.parse("content://com.kevin.server.provider/book");
        ContentValues contentValues = new ContentValues();
        contentValues.put("_id", 6);
        contentValues.put("name", "程序设计的艺术");
        contentResolver = getContentResolver();
        contentResolver.insert(bookUri, contentValues);
        Cursor bookCursor = contentResolver.query(bookUri, new String[]{"_id", "name"}, null, null, null);
        while (bookCursor.moveToNext()) {
            Book book = new Book();
            book.id = bookCursor.getInt(0);
            book.name = bookCursor.getString(1);
            Log.d(TAG, "query book: " + book.toString());
        }
        bookCursor.close();
        Uri userUri = Uri.parse("content://com.kevin.server.provider/user");
        Cursor userCursor = contentResolver.query(userUri, new String[]{"_id", "name", "gender"}, null, null, null);
        while (userCursor.moveToNext()) {
            User user = new User();
            user.userId = userCursor.getInt(0);
            user.userName = userCursor.getString(1);
            user.isMale = userCursor.getInt(2) == 1;
            Log.d(TAG, "query user: " + user.userId + "=" + user.userName + "=" + user.isMale);
        }
        userCursor.close();

    }
}
