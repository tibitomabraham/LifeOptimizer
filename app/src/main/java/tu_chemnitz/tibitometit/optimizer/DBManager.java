package tu_chemnitz.tibitometit.optimizer;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;
import android.content.ContentValues;
import android.icu.text.SimpleDateFormat;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by TibiTom on 28-12-2016.
 */

public class DBManager extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Tasks.db";
    private static final String TABLE_Tasks = "Tasks";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_Name = "_name";
    public static final String COLUMN_Description = "_description";
    public static final String COLUMN_Deadline = "_deadline";
    public static final String COLUMN_Category = "_category";
    public static final String COLUMN_Completed = "_status";
    public static final String COLUMN_Cancelled = "_cancelled";

    public DBManager(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_Tasks + "( " +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_Name + " TEXT, " +
                COLUMN_Description + " TEXT, " +
                COLUMN_Deadline + " DATETIME, " +
                COLUMN_Category + " TEXT, " +
                COLUMN_Completed + " BOOLEAN DEFAULT FALSE, " +
                COLUMN_Cancelled + " BOOLEAN DEFAULT FALSE " +
                ");";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_Tasks + "");
        onCreate(db);
    }

    public void addTask(Tasks tasks) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_Name, tasks.get_name());
        values.put(COLUMN_Description, tasks.get_description());
        values.put(COLUMN_Deadline, tasks.get_deadline().toString());
        values.put(COLUMN_Category, tasks.get_category());
        values.put(COLUMN_Completed, String.valueOf(tasks.is_status()));
        values.put(COLUMN_Cancelled, String.valueOf(tasks.is_cancelled()));
        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_Tasks, null, values);
        db.close();
    }

    public void cancelTask(Tasks task) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_Cancelled, true);
        SQLiteDatabase db = getWritableDatabase();
        db.update(TABLE_Tasks, values, COLUMN_ID + "=" + task.get_id(), null);
        db.close();
    }

    public int deleteTasks(){
        SQLiteDatabase db = getWritableDatabase();
        String deleteQuery = "DELETE FROM " + TABLE_Tasks + ";";
        try {
            db.execSQL(deleteQuery);
            return 0;
        }catch (Exception e){
            e.printStackTrace();
            return -1;
        }
    }

    public void updateTask(Tasks tasks) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_Name, tasks.get_name());
        values.put(COLUMN_Description, tasks.get_description());
        values.put(COLUMN_Deadline, tasks.get_deadline().toString());
        values.put(COLUMN_Category, tasks.get_category());
        values.put(COLUMN_Completed, String.valueOf(tasks.is_status()));
        values.put(COLUMN_Cancelled, String.valueOf(tasks.is_cancelled()));
        SQLiteDatabase db = getWritableDatabase();
        db.update(TABLE_Tasks, values, COLUMN_ID + "=" + tasks.get_id(), null);
        db.close();
    }

    public List<Tasks> getFutureTasks() {
        SQLiteDatabase db = getWritableDatabase();
        List<Tasks> listTasks = new ArrayList<Tasks>();
        ;
        int index = 0;
        SimpleDateFormat  dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
        //String query = "SELECT * FROM " + TABLE_Tasks + " WHERE "+ COLUMN_Deadline + " >= '"+dateFormat.format(new Date())+"' AND NOT "+COLUMN_Cancelled +" AND NOT "+ COLUMN_Completed;
        String query = "SELECT * FROM " + TABLE_Tasks + " WHERE NOT "+COLUMN_Cancelled +" AND NOT "+ COLUMN_Completed;
        Cursor cur = db.rawQuery(query, null);
        cur.moveToFirst();
        Date todaysDate = new Date();
        try {
        while (!cur.isAfterLast()) {
            if (cur.getString(cur.getColumnIndex(COLUMN_Name)) != null &&
                    dateFormat.parse(cur.getString(cur.getColumnIndex(COLUMN_Deadline))).getTime()>=todaysDate.getTime()){

                    Tasks tasks = new Tasks();
                    tasks.set_id(Integer.parseInt(cur.getString(cur.getColumnIndex(COLUMN_ID))));
                    tasks.set_name(cur.getString(cur.getColumnIndex(COLUMN_Name)));
                    tasks.set_description(cur.getString(cur.getColumnIndex(COLUMN_Description)));
                    Date date = dateFormat.parse(cur.getString(cur.getColumnIndex(COLUMN_Deadline)));
                    //Calendar currentDate = Calendar.getInstance();
                    //dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.GERMANY);
                    //date = dateFormat.parse(dateFormat.parse(date));
                    //String dateNow = dateFormat.format(currentDate.getTime());
                    //Date currDate =  dateFormat.parse(dateNow);
                    tasks.set_deadline(date);
                    tasks.set_status(Boolean.parseBoolean(cur.getString(cur.getColumnIndex(COLUMN_Completed))));
                    tasks.set_category(cur.getString(cur.getColumnIndex(COLUMN_Category)));
                    tasks.set_cancelled(Boolean.parseBoolean(cur.getString(cur.getColumnIndex(COLUMN_Cancelled))));
                    listTasks.add(tasks);

            }
            cur.moveToNext();
        }
        } catch (ParseException e) {
            e.printStackTrace();
            //listTasks.add(null);
        }
        return listTasks;
    }

    public List<Tasks> getPastTasks() {
        SQLiteDatabase db = getWritableDatabase();
        List<Tasks> listTasks = new ArrayList<Tasks>();
        ;
        int index = 0;
        SimpleDateFormat  dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
        //String query = "SELECT * FROM " + TABLE_Tasks + " WHERE " + COLUMN_Deadline + "<'"+dateFormat.format(new Date())+"' AND NOT "+COLUMN_Cancelled;
        String query = "SELECT * FROM " + TABLE_Tasks + " WHERE NOT "+COLUMN_Cancelled;
        Cursor cur = db.rawQuery(query, null);
        Date todaysDate = new Date();
        cur.moveToFirst();
        try {
        while (!cur.isAfterLast()) {
            if (cur.getString(cur.getColumnIndex(COLUMN_Name)) != null &&
                    dateFormat.parse(cur.getString(cur.getColumnIndex(COLUMN_Deadline))).getTime()<todaysDate.getTime()) {
                    Tasks tasks = new Tasks();
                    tasks.set_id(Integer.parseInt(cur.getString(cur.getColumnIndex(COLUMN_ID))));
                    tasks.set_name(cur.getString(cur.getColumnIndex(COLUMN_Name)));
                    tasks.set_description(cur.getString(cur.getColumnIndex(COLUMN_Description)));
                    Date date = dateFormat.parse(cur.getString(cur.getColumnIndex(COLUMN_Deadline)));
                    //Calendar currentDate = Calendar.getInstance();
                    //dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.GERMANY);
                    //date = dateFormat.parse(dateFormat.parse(date));
                    //String dateNow = dateFormat.format(currentDate.getTime());
                    //Date currDate =  dateFormat.parse(dateNow);
                    tasks.set_deadline(date);
                    tasks.set_status(Boolean.parseBoolean(cur.getString(cur.getColumnIndex(COLUMN_Completed))));
                    tasks.set_category(cur.getString(cur.getColumnIndex(COLUMN_Category)));
                    tasks.set_cancelled(Boolean.parseBoolean(cur.getString(cur.getColumnIndex(COLUMN_Cancelled))));
                    listTasks.add(tasks);
            }
            cur.moveToNext();
        }
        } catch (ParseException e) {
            e.printStackTrace();
            listTasks.add(null);
        }
        return listTasks;
    }

    public List<Tasks> getAllTasks() {
        SQLiteDatabase db = getWritableDatabase();
        List<Tasks> listTasks = new ArrayList<Tasks>();
        ;
        int index = 0;
        SimpleDateFormat  dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
        //String query = "SELECT * FROM " + TABLE_Tasks + " WHERE " + COLUMN_Deadline + "<'"+dateFormat.format(new Date())+"' AND NOT "+COLUMN_Cancelled;
        String query = "SELECT * FROM " + TABLE_Tasks + " WHERE NOT "+COLUMN_Cancelled;
        Cursor cur = db.rawQuery(query, null);
        Date todaysDate = new Date();
        cur.moveToFirst();
        try {
            while (!cur.isAfterLast()) {
                if (cur.getString(cur.getColumnIndex(COLUMN_Name)) != null) {
                    Tasks tasks = new Tasks();
                    tasks.set_id(Integer.parseInt(cur.getString(cur.getColumnIndex(COLUMN_ID))));
                    tasks.set_name(cur.getString(cur.getColumnIndex(COLUMN_Name)));
                    tasks.set_description(cur.getString(cur.getColumnIndex(COLUMN_Description)));
                    Date date = dateFormat.parse(cur.getString(cur.getColumnIndex(COLUMN_Deadline)));
                    //Calendar currentDate = Calendar.getInstance();
                    //dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.GERMANY);
                    //date = dateFormat.parse(dateFormat.parse(date));
                    //String dateNow = dateFormat.format(currentDate.getTime());
                    //Date currDate =  dateFormat.parse(dateNow);
                    tasks.set_deadline(date);
                    tasks.set_status(Boolean.parseBoolean(cur.getString(cur.getColumnIndex(COLUMN_Completed))));
                    tasks.set_category(cur.getString(cur.getColumnIndex(COLUMN_Category)));
                    tasks.set_cancelled(Boolean.parseBoolean(cur.getString(cur.getColumnIndex(COLUMN_Cancelled))));
                    listTasks.add(tasks);
                }
                cur.moveToNext();
            }
        } catch (ParseException e) {
            e.printStackTrace();
            listTasks.add(null);
        }
        return listTasks;
    }

    public Tasks getTaskDetails(int taskId) {
        SQLiteDatabase db = getWritableDatabase();
        int index = 0;
        SimpleDateFormat  dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
        String query = "SELECT * FROM " + TABLE_Tasks + " WHERE " + COLUMN_ID + "="+taskId+";";
        Cursor cur = db.rawQuery(query, null);
        cur.moveToFirst();
        Tasks tasks = new Tasks();
        while (!cur.isAfterLast()) {
            if (cur.getString(cur.getColumnIndex(COLUMN_Name)) != null) {
                try {
                    tasks.set_id(Integer.parseInt(cur.getString(cur.getColumnIndex(COLUMN_ID))));
                    tasks.set_name(cur.getString(cur.getColumnIndex(COLUMN_Name)));
                    tasks.set_description(cur.getString(cur.getColumnIndex(COLUMN_Description)));
                    Date date = dateFormat.parse(cur.getString(cur.getColumnIndex(COLUMN_Deadline)));
                    //Calendar currentDate = Calendar.getInstance();
                    //dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.GERMANY);
                    //date = dateFormat.parse(dateFormat.parse(date));
                    //String dateNow = dateFormat.format(currentDate.getTime());
                    //Date currDate =  dateFormat.parse(dateNow);
                    tasks.set_deadline(date);
                    tasks.set_status(Boolean.parseBoolean(cur.getString(cur.getColumnIndex(COLUMN_Completed))));
                    tasks.set_category(cur.getString(cur.getColumnIndex(COLUMN_Category)));
                    tasks.set_cancelled(Boolean.parseBoolean(cur.getString(cur.getColumnIndex(COLUMN_Cancelled))));
                    cur.moveToNext();
                } catch (ParseException e) {
                    e.printStackTrace();
                    tasks=null;
                }
            }
        }
        return tasks;
    }
}


