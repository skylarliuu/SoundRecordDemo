package com.skylar.soundrecorddemo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**数据库类
 *
 * 数据库操作：增加一条记录、删除一条记录、修改记录的文件名、查询数据库中某个指定位置的记录
 * 获取数据库的记录总数（用于文件命名）
 * Created by Administrator on 2017/9/10.
 */
public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "saved_recordings.db";
    private static final int DATABASE_VERSION = 1;
    private static OnDatabaseChangeListner mListner;

    private static abstract  class DBHelperItem implements BaseColumns{
        private static final String TABLE_NAME = "saved_recordings";

        private static final String COLUMN_RECORD_NAME = "record_name";
        private static final String COLUMN_RECORD_PATH = "record_path";
        private static final String COLUMN_RECORD_LENGTH = "record_length";
        private static final String COLUMN_RECORD_ADDED_TIME = "added_time";
    }

    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + DBHelperItem.TABLE_NAME + " (" +
            DBHelperItem._ID + " INTEGER PRIMARY KEY " + COMMA_SEP +
            DBHelperItem.COLUMN_RECORD_NAME + TEXT_TYPE + COMMA_SEP +
            DBHelperItem.COLUMN_RECORD_PATH + TEXT_TYPE + COMMA_SEP +
            DBHelperItem.COLUMN_RECORD_LENGTH + " INTEGER " + COMMA_SEP +
            DBHelperItem.COLUMN_RECORD_ADDED_TIME + " INTEGER " + ")";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public static void setDatabaseChangeListner(OnDatabaseChangeListner listner){
        mListner = listner;
    }


    public long addRecord(String recordName,String recordPath,long length){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DBHelperItem.COLUMN_RECORD_NAME,recordName);
        cv.put(DBHelperItem.COLUMN_RECORD_PATH,recordPath);
        cv.put(DBHelperItem.COLUMN_RECORD_LENGTH,length);
        cv.put(DBHelperItem.COLUMN_RECORD_ADDED_TIME,System.currentTimeMillis());
        long rowId = db.insert(DBHelperItem.TABLE_NAME,null,cv);

        if(mListner!=null){
            mListner.addRecord();
        }
        return rowId;
    }

    public void removeRecord(int id){
        SQLiteDatabase db = getWritableDatabase();
        String[] whereArgs = {String.valueOf(id)};
        db.delete(DBHelperItem.TABLE_NAME,DBHelperItem._ID + "=?",whereArgs);
        if(mListner!=null){
            mListner.removeRecord();
        }
    }

    public void renameRecord(RecordItem item,String newName,String newPath){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DBHelperItem.COLUMN_RECORD_NAME,newName);
        cv.put(DBHelperItem.COLUMN_RECORD_PATH,newPath);
//        db.update(DBHelperItem.TABLE_NAME,cv,DBHelperItem._ID,new String[]{String.valueOf(item.getId())});
        db.update(DBHelperItem.TABLE_NAME, cv,
                DBHelperItem._ID + "=" + item.getId(), null);
        if(mListner!=null){
            mListner.renameRecord();
        }
    }

    public RecordItem getItemAtPosition(int position){
        SQLiteDatabase db = getWritableDatabase();
        String[] projection = {
                DBHelperItem._ID,
                DBHelperItem.COLUMN_RECORD_NAME,
                DBHelperItem.COLUMN_RECORD_PATH,
                DBHelperItem.COLUMN_RECORD_LENGTH,
                DBHelperItem.COLUMN_RECORD_ADDED_TIME
        };

        Cursor cursor = db.query(DBHelperItem.TABLE_NAME,projection,null,null,null,null,null);
        if(cursor.moveToPosition(position)){
            RecordItem item = new RecordItem();
            item.setId(cursor.getInt(cursor.getColumnIndex(DBHelperItem._ID)));
            item.setName(cursor.getString(cursor.getColumnIndex(DBHelperItem.COLUMN_RECORD_NAME)));
            item.setPath(cursor.getString(cursor.getColumnIndex(DBHelperItem.COLUMN_RECORD_PATH)));
            item.setLength(cursor.getInt(cursor.getColumnIndex(DBHelperItem.COLUMN_RECORD_LENGTH)));
            item.setTime(cursor.getLong(cursor.getColumnIndex(DBHelperItem.COLUMN_RECORD_ADDED_TIME)));
            cursor.close();
            return item;
        }

        return null;
    }

    public int getCount(){
        SQLiteDatabase db = getWritableDatabase();
        String[] projection = {DBHelperItem._ID};

        Cursor cursor = db.query(DBHelperItem.TABLE_NAME,projection,null,null,null,null,null);
        int count = cursor.getCount();
        return count;
    }
}
