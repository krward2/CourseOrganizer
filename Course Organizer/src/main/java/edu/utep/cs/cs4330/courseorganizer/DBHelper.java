package edu.utep.cs.cs4330.courseorganizer;

/**
 * @author Kenneth Ward & Jesus Garcia
 * @version 1.0
 * @since 5/8/2019
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {
    //Defining the database and its tables
    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "courseDatabase";
    private static final String COURSE_TABLE = "courses";
    private static final String TASK_TABLE = "tasks";

    //Columns of both tables
    private static final String KEY_ID = "_id";
    private static final String KEY_COURSE = "courseName";

    //Columns of Task Table
    private static final String KEY_TASK = "task";
    private static final String KEY_DUE_DATE = "dueDate";

    //Columns of Course Table
    private static final String KEY_DAYS = "days";
    private static final String KEY_TIME = "time";
    private static final String KEY_LOCATION = "location";
    private static final String KEY_PROFESSOR = "professorName";
    private static final String KEY_PROFESSOR_PHONE = "professorPhone";
    private static final String KEY_PROFESSOR_EMAIL = "professorEmail";
    private static final String KEY_PROFESSOR_OFFICE = "professorOffice";
    private static final String KEY_PROFESSOR_OFFICE_HOURS = "professorOfficeHours";

    //SQLite query string for creating the task table
    private static final String CREATE_TASK_TABLE = "CREATE TABLE " + TASK_TABLE + "("
            + KEY_ID + " INTEGER PRIMARY KEY, "
            + KEY_COURSE+ " TEXT, "
            + KEY_TASK + " TEXT, "
            + KEY_DUE_DATE + " TEXT"
            + ")";

    //SQLite query string for creating the course table
    //restructured the course table
    private static final String CREATE_COURSE_TABLE = "CREATE TABLE " + COURSE_TABLE + "("
            + KEY_ID + " INTEGER PRIMARY KEY, "
            + KEY_COURSE+ " TEXT, "
            + KEY_DAYS + " TEXT, "
            + KEY_TIME + " TEXT, "
            + KEY_LOCATION + " TEXT, "
            + KEY_PROFESSOR + " TEXT, "
            + KEY_PROFESSOR_PHONE + " TEXT, "
            + KEY_PROFESSOR_EMAIL + " TEXT, "
            + KEY_PROFESSOR_OFFICE + " TEXT, "
            + KEY_PROFESSOR_OFFICE_HOURS + " TEXT"
            + ")";


    public DBHelper(Context context){
        super(context,DB_NAME,null,DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TASK_TABLE);
        db.execSQL(CREATE_COURSE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TASK_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + COURSE_TABLE);
        onCreate(db);

    }

    /**
     * Adds all fields from a course object to the database
     * @param course The course to be copied
     */
    public void addCourse(Course course){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        //cleaned up the add changes
        values.put(KEY_COURSE, course.getCourseTitle());
        values.put(KEY_DAYS, course.getDays());
        values.put(KEY_TIME, course.getTime());
        values.put(KEY_LOCATION, course.getLocation());
        values.put(KEY_PROFESSOR, course.getProfessorName());
        values.put(KEY_PROFESSOR_PHONE, course.getProfessorPhone());
        values.put(KEY_PROFESSOR_EMAIL, course.getProfessorEmail()) ;
        values.put(KEY_PROFESSOR_OFFICE, course.getProfessorOfficeLocation());
        values.put(KEY_PROFESSOR_OFFICE_HOURS, course.getProfessorOfficeHours());

        db.insert(COURSE_TABLE, null, values);
    }

    /**
     * Adds an entire list of course objects
     * @param courseList The list to be added
     */
    public void addCourseList(ArrayList<Course> courseList){
        for(Course course: courseList) addCourse(course);
    }

    /**
     * Allows the database to change columns pertaining to a specific course.
     * @param changedCourse The course to be changed
     */
    //restructured the update courses
    public void updateCourse(Course changedCourse){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + COURSE_TABLE +
                " SET " + KEY_PROFESSOR + " = '" + changedCourse.getProfessorName() +
                "', " + KEY_PROFESSOR_PHONE + " = '" + changedCourse.getProfessorPhone() +
                "', " + KEY_PROFESSOR_EMAIL + " = '" + changedCourse.getProfessorEmail() +
                "', " + KEY_PROFESSOR_OFFICE + " = '" + changedCourse.getProfessorOfficeLocation() +
                "', " + KEY_PROFESSOR_OFFICE_HOURS + " = '" + changedCourse.getProfessorOfficeHours() +
                "', " + KEY_LOCATION + " = '" + changedCourse.getLocation() +
                "', " + KEY_DAYS + " = '" + changedCourse.getDays() +
                "', " + KEY_TIME + " = '" + changedCourse.getTime() +
                "' WHERE " + KEY_COURSE + " = '" + changedCourse.getCourseTitle() + "'";
        db.execSQL(query);
    }

    /**
     * Makes a query from the database using the course name. Once the
     * course is found in the database a course object is created and returned
     * @param courseName The name of the course to be retrieved
     * @return Returns a course of the same title contained in the database
     */
    public Course getCourse(String courseName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + COURSE_TABLE + " WHERE "+ KEY_COURSE + " = ?",new String[]{courseName});

        if (cursor.moveToFirst()) {
            return new Course(cursor.getString(cursor.getColumnIndex(KEY_COURSE)),
                    cursor.getString(cursor.getColumnIndex(KEY_DAYS)),
                    cursor.getString(cursor.getColumnIndex(KEY_TIME)),
                    cursor.getString(cursor.getColumnIndex(KEY_LOCATION)),
                    cursor.getString(cursor.getColumnIndex(KEY_PROFESSOR)),
                    cursor.getString(cursor.getColumnIndex(KEY_PROFESSOR_PHONE)),
                    cursor.getString(cursor.getColumnIndex(KEY_PROFESSOR_EMAIL)),
                    cursor.getString(cursor.getColumnIndex(KEY_PROFESSOR_OFFICE)),
                    cursor.getString(cursor.getColumnIndex(KEY_PROFESSOR_OFFICE_HOURS)));
        }
        return null;
    }

    /**
     * @return a list of all courses contained in the database
     */
    public ArrayList<Course> getAllCourses(){
        ArrayList<Course> courseList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                COURSE_TABLE,
                null,
                null,
                null,
                null,
                null,
                null
        );

        if(cursor.moveToFirst()){
            do{
                courseList.add(new Course(cursor.getString(cursor.getColumnIndex(KEY_COURSE)),
                        cursor.getString(cursor.getColumnIndex(KEY_DAYS)),
                        cursor.getString(cursor.getColumnIndex(KEY_TIME)),
                        cursor.getString(cursor.getColumnIndex(KEY_LOCATION)),
                        cursor.getString(cursor.getColumnIndex(KEY_PROFESSOR)),
                        cursor.getString(cursor.getColumnIndex(KEY_PROFESSOR_PHONE)),
                        cursor.getString(cursor.getColumnIndex(KEY_PROFESSOR_EMAIL)),
                        cursor.getString(cursor.getColumnIndex(KEY_PROFESSOR_OFFICE)),
                        cursor.getString(cursor.getColumnIndex(KEY_PROFESSOR_OFFICE_HOURS))));
            } while(cursor.moveToNext());
        }
        return courseList;
    }

    /**
     * Removes courses with same name as the given course
     * @param course The course to be removed
     */
    public void deleteCourse(Course course){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + COURSE_TABLE + " WHERE " +
                KEY_COURSE + " ='" + course.getCourseTitle() + "'";
        db.execSQL(query);
    }

    /**
     * Adds a task to the database
     * @param task The task description
     * @param course The associated course
     * @param dueDate Task due date
     */
    public void addTasks(String task,String course, String dueDate){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_TASK, task);
        values.put(KEY_COURSE, course);
        values.put(KEY_DUE_DATE, dueDate);
        db.insert(TASK_TABLE, null, values);
    }

    public ArrayList<Task> getCourseTasks(String courseName) {
        ArrayList<Task> taskList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TASK_TABLE + " WHERE " + KEY_COURSE + " = ?",new String[]{courseName});

        if (cursor.moveToFirst()) {
            do {
                taskList.add(new Task(cursor.getString(cursor.getColumnIndex(KEY_TASK)),
                        cursor.getString(cursor.getColumnIndex(KEY_COURSE)),
                        cursor.getString(cursor.getColumnIndex(KEY_DUE_DATE))));
            } while (cursor.moveToNext());
        }
        return taskList;
    }

    /**
     * Removes tasks fom the database with the same description
     * @param task
     */
    public void deleteTask(Task task){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + TASK_TABLE + " WHERE " +
                KEY_TASK + " ='" + task.getTask() + "'";
        db.execSQL(query);
    }


    public void deleteData(String id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TASK_TABLE, KEY_ID +  "=" + id, null) ;
        db.close();
    }

    public String professor(String profe) {
        List<String> watchedPrices = new ArrayList<>();
        Task task1;
        String task = null;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT "+ KEY_PROFESSOR +" FROM " + TASK_TABLE + " WHERE "+ KEY_PROFESSOR+ " = ?",new String[]{profe});

        if (cursor.moveToFirst()) {
            do {
                String p = cursor.getString(0); //here is the problem


                watchedPrices.add(p);
            } while (cursor.moveToNext());
        }
        String pro = watchedPrices.get(0);
        return pro;
    }

    public String getDays(String profe) {
        List<String> watchedPrices = new ArrayList<>();
        Task task1;
        String task = null;

        SQLiteDatabase db = this.getReadableDatabase();
        //   Cursor cursor = db.rawQuery(selectQuery, new String[]{profe});
        Cursor cursor = db.rawQuery("SELECT "+ KEY_DAYS +" FROM " + TASK_TABLE + " WHERE "+ KEY_PROFESSOR+ " = ?",new String[]{profe});

        if (cursor.moveToFirst()) {
            do {

                String p = cursor.getString(0); //here is the problem


                watchedPrices.add(p);
            } while (cursor.moveToNext());
        }
        String pro = watchedPrices.get(0);
        return pro;
    }

    public String getTime(String profe) {
        List<String> watchedPrices = new ArrayList<>();
        Task task1;
        String task = null;

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT "+ KEY_TIME +" FROM " + TASK_TABLE + " WHERE "+ KEY_PROFESSOR+ " = ?",new String[]{profe});

        if (cursor.moveToFirst()) {
            do {

                String p = cursor.getString(0); //here is the problem


                watchedPrices.add(p);
            } while (cursor.moveToNext());
        }
        String pro = watchedPrices.get(0);
        return pro;
    }


    public void update(float curr,String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        db.update(TASK_TABLE, values, KEY_TASK + " = ?", new String[]{id});
        db.close();
    }

    public void deleteAll(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ TASK_TABLE);
    }
}