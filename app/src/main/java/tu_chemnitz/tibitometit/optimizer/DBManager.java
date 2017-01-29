package tu_chemnitz.tibitometit.optimizer;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;
import android.content.ContentValues;

import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Created by TibiTom on 28-12-2016.
 * This class is the interface to the database
 */

public class DBManager extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Tasks.db";
    private static final String TABLE_Tasks = "Tasks";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_Name = "_name";
    private static final String COLUMN_Description = "_description";
    private static final String COLUMN_Deadline = "_deadline";
    private static final String COLUMN_Category = "_category";
    private static final String COLUMN_Completed = "_status";
    private static final String COLUMN_Cancelled = "_cancelled";

    /*Constructor*/
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

    List<String> getTaskDates(){
        SQLiteDatabase db = getWritableDatabase();
        List<String> listDates = new ArrayList<>();
        int index = 0;
        //DateFormat datetimeFormat = DateFormat.getDateInstance(DateFormat.LONG, Locale.GERMANY);
        //DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.DEFAULT, Locale.GERMANY);
        SimpleDateFormat  datetimeFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
        SimpleDateFormat  dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String query = "SELECT DISTINCT "+COLUMN_Deadline+" FROM " + TABLE_Tasks + " WHERE NOT "+COLUMN_Cancelled;
        Cursor cur = db.rawQuery(query, null);
        cur.moveToFirst();

        try {
            while (!cur.isAfterLast()) {
                if (cur.getString(0) != null){
                    //Date datetime = datetimeFormat.parse(cur.getString(cur.getColumnIndex("Deadline")));
                    Date datetime = datetimeFormat.parse(cur.getString(0));
                    String strDate = dateFormat.format(datetime);
                    listDates.add(strDate);
                }
                cur.moveToNext();
            }
        } catch (ParseException e) {
            e.printStackTrace();
            //listTasks.add(null);
        }
        cur.close();
        return listDates;
    }
    public DayTaskNumQuadType getCompletedITaskOnDates(String date){
        SQLiteDatabase db = getWritableDatabase();
        DayTaskNumQuadType taskNumQuad=null;
        int index = 0;
        //DateFormat datetimeFormat = DateFormat.getDateInstance(DateFormat.LONG, Locale.GERMANY);
        //DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.DEFAULT, Locale.GERMANY);
        SimpleDateFormat  datetimeFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
        SimpleDateFormat  dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String query = "SELECT "+COLUMN_Deadline+","+COLUMN_Name+","+COLUMN_Category+","+COLUMN_Completed+" FROM " + TABLE_Tasks + " WHERE NOT "+COLUMN_Cancelled;
        Cursor cur = db.rawQuery(query, null);
        cur.moveToFirst();
        try {
            taskNumQuad = new DayTaskNumQuadType();
            while (!cur.isAfterLast()) {
                Date deadline = datetimeFormat.parse(cur.getString(cur.getColumnIndex(COLUMN_Deadline)));
                String strDeadline = dateFormat.format(deadline);
                if (strDeadline.equals(date)) {
                    if (cur.getString(cur.getColumnIndex(COLUMN_Name)) != null) {
                        if (Boolean.parseBoolean(cur.getString(cur.getColumnIndex(COLUMN_Completed)))) {
                            taskNumQuad.compTotalTasks += 1;
                        }
                    }
                    taskNumQuad.totalTasks += 1;
                    }
                cur.moveToNext();
                }

            }
        catch (ParseException e) {
            e.printStackTrace();
        }
        cur.close();
        return taskNumQuad;
    }


    /*Function to add a new task*/
    void addTask(Tasks tasks) {
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

    /*Function to cacel a task*/
    void cancelTask(Tasks task) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_Cancelled, true);
        SQLiteDatabase db = getWritableDatabase();
        db.update(TABLE_Tasks, values, COLUMN_ID + "=" + task.get_id(), null);
        db.close();
    }

    /*Function to delete all task*/
    int deleteTasks(){
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

    /*Function to update the details of tasks*/
    void updateTask(Tasks tasks) {
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

    /*Function to retrieve all the future tasks*/
    List<Tasks> getFutureTasks() {
        SQLiteDatabase db = getWritableDatabase();
        List<Tasks> listTasks = new ArrayList<>();
        int index = 0;
        //DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.LONG, Locale.GERMANY);
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
        cur.close();
        return listTasks;
    }

    /*Function to get all the tasks that are already past*/
    List<Tasks> getPastTasks() {
        SQLiteDatabase db = getWritableDatabase();
        List<Tasks> listTasks = new ArrayList<>();
        int index = 0;
        //DateFormat datetimeFormat = DateFormat.getDateInstance(DateFormat.LONG, Locale.GERMANY);
        //DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.DEFAULT, Locale.GERMANY);
        SimpleDateFormat  datetimeFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
        SimpleDateFormat  dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        //String query = "SELECT * FROM " + TABLE_Tasks + " WHERE date(" + COLUMN_Deadline + ")<date('now') AND NOT "+COLUMN_Cancelled;
        //String query = "SELECT * FROM " + TABLE_Tasks + " WHERE date(" + COLUMN_Deadline + ")<'"+dateFormat.format(new Date())+"' AND NOT "+COLUMN_Cancelled;
        String query = "SELECT * FROM " + TABLE_Tasks + " WHERE NOT "+COLUMN_Cancelled;
        Cursor cur = db.rawQuery(query, null);
        Date todaysDate = new Date();

        cur.moveToFirst();
        try {
        while (!cur.isAfterLast()) {
            Date deadline = datetimeFormat.parse(cur.getString(cur.getColumnIndex(COLUMN_Deadline)));
            if (cur.getString(cur.getColumnIndex(COLUMN_Name)) != null &&
                    deadline.getTime()<todaysDate.getTime()) {
                    Tasks tasks = new Tasks();
                    tasks.set_id(Integer.parseInt(cur.getString(cur.getColumnIndex(COLUMN_ID))));
                    tasks.set_name(cur.getString(cur.getColumnIndex(COLUMN_Name)));
                    tasks.set_description(cur.getString(cur.getColumnIndex(COLUMN_Description)));
                    tasks.set_deadline(deadline);
                    tasks.set_status(Boolean.parseBoolean(cur.getString(cur.getColumnIndex(COLUMN_Completed))));
                    tasks.set_category(cur.getString(cur.getColumnIndex(COLUMN_Category)));
                    tasks.set_cancelled(Boolean.parseBoolean(cur.getString(cur.getColumnIndex(COLUMN_Cancelled))));
                    listTasks.add(tasks);
            }
            cur.moveToNext();
        }
            cur.close();
        } catch (ParseException e) {
            e.printStackTrace();
            //listTasks.add(null);
        }
        cur.close();
        return listTasks;
    }

    /*Function to get all the tasks.*/
    List<Tasks> getAllTasks() {
        SQLiteDatabase db = getWritableDatabase();
        List<Tasks> listTasks = new ArrayList<>();
        int index = 0;
        //DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.LONG, Locale.GERMANY);
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
            //listTasks.add(null);
        }
        cur.close();
        return listTasks;
    }

    /*Function to get the details of a particular task*/
    Tasks getTaskDetails(int taskId) {
        SQLiteDatabase db = getWritableDatabase();
        int index = 0;
        //DateFormat datetimeFormat = DateFormat.getDateInstance(DateFormat.LONG, Locale.GERMANY);
        //DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.DEFAULT, Locale.GERMANY);
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
                catch (NullPointerException ne) {
                    ne.printStackTrace();
                    tasks=null;
                }
            }
        }
        cur.close();
        return tasks;
    }
}


