package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.myapplication.ClassInstance.ClassSample;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Database name and version
    private static final String DATABASE_NAME = "YogaApp.db";
    private static final int DATABASE_VERSION = 3; // new version

    // Classes table
    public static final String TABLE_CLASSES = "Classes";
    public static final String COLUMN_CLASS_ID = "class_id";
    public static final String COLUMN_DAY_OF_WEEK = "day_of_week";
    public static final String COLUMN_TIME = "time";
    public static final String COLUMN_CAPACITY = "capacity";
    public static final String COLUMN_DURATION = "duration";
    public static final String COLUMN_PRICE = "price";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_TEACHER = "teacher";
    public static final String COLUMN_DESCRIPTION = "description";

//    // Schedules table
    public static final String TABLE_CLASS_INSTANCE = "Class_Instance";
    public static final String COLUMN_CLASS_INSTANCE_ID = "class_instance_id";
    public static final String COLUMN_CLASS_YOGA_ID = "class_id";  // Foreign key to Classes
    public static final String COLUMN_DATE = "text";
    public static final String COLUMN_SCHEDULE_TEACHER = "teacher";
    public static final String COLUMN_COMMENTS = "comments";

    // SQL statements to create tables
    private static final String CREATE_TABLE_CLASSES = "CREATE TABLE " + TABLE_CLASSES + " ("
            + COLUMN_CLASS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_DAY_OF_WEEK + " TEXT NOT NULL, "
            + COLUMN_TIME + " TEXT NOT NULL, "
            + COLUMN_CAPACITY + " INTEGER, "
            + COLUMN_DURATION + " INTEGER, "
            + COLUMN_PRICE + " REAL, "
            + COLUMN_TYPE + " TEXT, "
            + COLUMN_TEACHER + " TEXT NOT NULL, "
            + COLUMN_DESCRIPTION + " TEXT);";

    private static final String CREATE_TABLE_CLASS_INSTANCE = "CREATE TABLE " + TABLE_CLASS_INSTANCE + " ("
            + COLUMN_CLASS_INSTANCE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_CLASS_YOGA_ID + " INTEGER, "
            + COLUMN_DATE + " TEXT NOT NULL, "
            + COLUMN_SCHEDULE_TEACHER + " TEXT NOT NULL, "
            + COLUMN_COMMENTS + " TEXT, "
            + "FOREIGN KEY(" + COLUMN_CLASS_YOGA_ID + ") REFERENCES " + TABLE_CLASSES + "(" + COLUMN_CLASS_ID + "));";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_CLASSES);
        db.execSQL(CREATE_TABLE_CLASS_INSTANCE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CLASSES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CLASS_INSTANCE);
        onCreate(db);
    }

    // CRUD operations for Classes

    // Add new class
    public boolean addClass(String dayOfWeek, String time, int capacity, int duration, double price, String type, String teacher, String description) {
        SQLiteDatabase db = this.getWritableDatabase();

        String query = "SELECT * FROM " + TABLE_CLASSES + " WHERE " +
                COLUMN_DAY_OF_WEEK + " = ? AND " +
                COLUMN_TIME + " = ? AND " +
                COLUMN_TEACHER + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{dayOfWeek, time, teacher});

        if (cursor.moveToFirst()) {
            cursor.close();
            db.close();
            return false;
        }
        cursor.close();

        ContentValues values = new ContentValues();
        values.put(COLUMN_DAY_OF_WEEK, dayOfWeek);
        values.put(COLUMN_TIME, time);
        values.put(COLUMN_CAPACITY, capacity);
        values.put(COLUMN_DURATION, duration);
        values.put(COLUMN_PRICE, price);
        values.put(COLUMN_TYPE, type);
        values.put(COLUMN_TEACHER, teacher);
        values.put(COLUMN_DESCRIPTION, description);

        long result = db.insert(TABLE_CLASSES, null, values);
        db.close();

        return result != -1;
    }


    public boolean updateClass(int ID, String dayOfWeek, String time, int capacity, int duration, double price, String type, String teacher, String description) {
        SQLiteDatabase db = this.getWritableDatabase();

        String query = "SELECT * FROM " + TABLE_CLASSES + " WHERE " +
                COLUMN_DAY_OF_WEEK + " = ? AND " +
                COLUMN_TIME + " = ? AND " +
                COLUMN_TEACHER + " = ? AND " +
                "class_id != ?";

        Cursor cursor = db.rawQuery(query, new String[]{dayOfWeek, time, teacher, String.valueOf(ID)});


        if (cursor.moveToFirst()) {
            cursor.close();
            db.close();
            return false;
        }
        cursor.close();

        ContentValues values = new ContentValues();
        values.put(COLUMN_DAY_OF_WEEK, dayOfWeek);
        values.put(COLUMN_TIME, time);
        values.put(COLUMN_CAPACITY, capacity);
        values.put(COLUMN_DURATION, duration);
        values.put(COLUMN_PRICE, price);
        values.put(COLUMN_TYPE, type);
        values.put(COLUMN_TEACHER, teacher);
        values.put(COLUMN_DESCRIPTION, description);

        int rowsUpdated = db.update(TABLE_CLASSES, values, "class_id = ?", new String[]{String.valueOf(ID)});
        db.close();

        return rowsUpdated > 0;
    }


    public  boolean deleteClass(int ID){
        SQLiteDatabase DB = this.getWritableDatabase();
        long result = DB.delete(TABLE_CLASSES, COLUMN_CLASS_ID + " = ?", new String[]{String.valueOf(ID)});
        DB.close();
        return  result > 0;
    }

    public Cursor getClassesByName(String teacherName){
        SQLiteDatabase db = this.getReadableDatabase();

        // Define the selection criteria
        String selection = COLUMN_TEACHER + " LIKE ?";
        String[] selectionArgs = { "%" + teacherName + "%" };  // Allows partial matching

        // Execute the query
        return db.query(TABLE_CLASSES, null, selection, selectionArgs, null, null, null);
    }

    public Cursor getClassById(int classId) {
        SQLiteDatabase db = this.getReadableDatabase();

        // Define the selection criteria
        String selection = COLUMN_CLASS_ID + " = ?";
        String[] selectionArgs = { String.valueOf(classId) }; // Use the class ID as a selection argument

        // Execute the query
        return db.query(TABLE_CLASSES, null, selection, selectionArgs, null, null, null);
    }

    // Retrieve all classes
    public Cursor getAllClasses() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_CLASSES, null, null, null, null, null, null);
    }

    // CRUD operations for Schedules

    // Add new schedule instance
    public boolean addClassInstance(int classId, String date, String teacher, String comments) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CLASS_YOGA_ID, classId);
        values.put(COLUMN_DATE, date);
        values.put(COLUMN_SCHEDULE_TEACHER, teacher);
        values.put(COLUMN_COMMENTS, comments);

        long result = db.insert(TABLE_CLASS_INSTANCE, null, values);
        db.close();
        return result > 0;
    }

    // Retrieve schedules for a specific class
    public Cursor getClassInstanceById(String classId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COLUMN_CLASS_YOGA_ID + " = ?";
        String[] selectionArgs = {String.valueOf(classId)};
        return db.query(TABLE_CLASS_INSTANCE, null, selection, selectionArgs, null, null, null);
    }

//    // Update schedule
    public boolean updateClassInstance(int class_Instance_id, String date, String teacher, String comments) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DATE, date);
        values.put(COLUMN_SCHEDULE_TEACHER, teacher);
        values.put(COLUMN_COMMENTS, comments);

        String whereClause = COLUMN_CLASS_INSTANCE_ID + " = ?";
        String[] whereArgs = {String.valueOf(class_Instance_id)};
        int rowsUpdated = db.update(TABLE_CLASS_INSTANCE, values, whereClause, whereArgs);
        db.close();
        return rowsUpdated > 0;
    }

//    // Delete schedule
    public boolean deleteClassInstance(int classInstanceId) {
        SQLiteDatabase db = this.getWritableDatabase();
        String whereClause = COLUMN_CLASS_INSTANCE_ID + " = ?";
        String[] whereArgs = {String.valueOf(classInstanceId)};
        int rowsDeleted = db.delete(TABLE_CLASS_INSTANCE, whereClause, whereArgs);
        db.close();
        return rowsDeleted > 0;
    }

    public List<ClassSample> getClassSample(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cs = db.rawQuery("SELECT * FROM "+ TABLE_CLASSES, null);
        List<ClassSample> classSamples = new ArrayList<ClassSample>();

        if (cs.getCount() != 0){
            while (cs.moveToNext()){
                @SuppressLint("Range") int id = cs.getInt(cs.getColumnIndex("class_id"));
                @SuppressLint("Range") String name = cs.getString(cs.getColumnIndex("teacher"));
                @SuppressLint("Range") String type = cs.getString(cs.getColumnIndex("type"));
                @SuppressLint("Range") String day = cs.getString(cs.getColumnIndex("day_of_week"));
                ClassSample classSample = new ClassSample(id, type, name, day);

                classSamples.add(classSample);
            }
        }
        cs.close();
        db.close();
        return  classSamples;
    }

    public Cursor getClassInstances(){
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_CLASS_INSTANCE, null, null, null, null, null, null);
    }

    public ClassSample getClassSampleById(String id){
        ClassSample classSample = new ClassSample();
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COLUMN_CLASS_ID + " = ?";
        String[] selectionArgs = { String.valueOf(id) };
        Cursor cs = db.query(TABLE_CLASSES, null, selection, selectionArgs, null, null, null);

        if (cs != null && cs.moveToFirst()){
            @SuppressLint("Range") int class_id = cs.getInt(cs.getColumnIndex("class_id"));
            @SuppressLint("Range") String name = cs.getString(cs.getColumnIndex("teacher"));
            @SuppressLint("Range") String type = cs.getString(cs.getColumnIndex("type"));
            @SuppressLint("Range") String day = cs.getString(cs.getColumnIndex("day_of_week"));
            classSample.setId(class_id);
            classSample.setClassType(type);
            classSample.setTeacher(name);
            classSample.setDayOfWeek(day);
        }
        cs.close();
        db.close();
        return classSample;
    }
}
