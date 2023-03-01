package com.ehealthkiosk.kiosk.storage;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

public class SQLStorageHelper {
    public static void setSharedBooleanValue(Context context, String key, Boolean value){
        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = YolohealthContentProvider.CONTENT_URI;
        ContentValues contentValues = new ContentValues();
        contentValues.put("value", value);

        if(booleanKeyExists(context, key)){
            // Key already in the database so just update it;
            String whereClause = "name=?";
            String whereClauseArgs[] = {key};
            contentResolver.update(uri, contentValues, whereClause, whereClauseArgs);

        }else {
            // key doesn't exists so we need to insert it;
            contentValues.put("name", key);
            contentResolver.insert(uri, contentValues);
        }
    }

    public static boolean readSharedBooleanValue(Context context, String key){
        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = YolohealthContentProvider.CONTENT_URI;
        String whereClause = "name='" + key+"'";
        Cursor cursor = contentResolver.query(uri, null, whereClause, null, null);

        if(cursor!=null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            int value = cursor.getInt(cursor.getColumnIndex("value"));
            return value == 1;
        } else {
            return false;
        }

    }

    public static boolean booleanKeyExists(Context context, String key){
        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = YolohealthContentProvider.CONTENT_URI;
        String whereClause = "name='" + key+"'";
        Cursor cursor = contentResolver.query(uri, null, whereClause, null, null);

        if(cursor!=null && cursor.getCount() > 0) {
            return true;
        } else {
            return false;
        }

    }
}
