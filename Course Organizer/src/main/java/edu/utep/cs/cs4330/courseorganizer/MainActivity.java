package edu.utep.cs.cs4330.courseorganizer;

import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import ca.antonious.materialdaypicker.MaterialDayPicker;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;
    private DBHelper dbHelper;
    private SQLiteDatabase database;
    private ArrayList<Course> extractedCourseList;
    private ListView listView;
    private CheckBox checkBox;
    private ArrayList<Task> taskList;
    private Course newCourse;
    private Menu menu;
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Set MainActivity layout
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Configure Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Course Organizer");
        toolbar.setBackgroundColor(Color.rgb(0,88,135));

        //Configure Navigation Drawer
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //extractedCourseList = dbHelper.getAllCourses();
        menu = navigationView.getMenu();


        /* **************************************For testing purposes************************************************ */
        ArrayList<Course> insertedCourseList = new ArrayList<>();
        dbHelper = new DBHelper(this);

        insertedCourseList.add(new Course("CS 1401", "MWF", "12:00PM - 1:00PM",
                "CCSB 1.01", "Dr.Professor", "(111)111-1111",
                "prof@uni.edu", "CCSB 1.02", "1:00PM - 2:00PM"));

        dbHelper.addTasks("Create Skynet", insertedCourseList.get(0).getCourseTitle(), "1/1/1");
        dbHelper.addTasks("Hack NSA", insertedCourseList.get(0).getCourseTitle(), "1/1/1");
        dbHelper.addTasks("Towers of Hanoi", insertedCourseList.get(0).getCourseTitle(), "1/1/1");

        insertedCourseList.add(new Course("HIST 2020", "W", "2:00PM - 3:00PM",
                "LAC 320", "Dr. Pepper", "(222)222-2222",
                "pepp@uni.edu", "CCSB 1.03", "1:00PM - 2:00PM"));

        dbHelper.addTasks("Write paper on Napoleonic Wars", insertedCourseList.get(1).getCourseTitle(), "2/2/2");

        insertedCourseList.add(new Course("ENG 101", "TR", "7:00AM - 8:00AM",
                "UGLC 016", "Dr. Love", "(333)333-3333",
                "lov@uni.edu", "CCSB 1.04", "1:00PM - 2:00PM"));

        dbHelper.addTasks("Read Gravitys Rainbow", insertedCourseList.get(2).getCourseTitle(), "3/3/3");

        insertedCourseList.add(new Course("MATH 3141", "T", "11:00AM - 12:00PM",
                "BSN 240", "Dr. Oc", "(444)444-4444",
                "oc@uni.edu", "CCSB 1.05", "1:00PM - 2:00PM"));

        dbHelper.addTasks("Solve Riemman Hypothesis", insertedCourseList.get(3).getCourseTitle(), "4/4/4");

        dbHelper.addCourseList(insertedCourseList);

        dbHelper = new DBHelper(this);
        extractedCourseList = dbHelper.getAllCourses();

        Log.i("1", String.valueOf(extractedCourseList.size()));

        menu = navigationView.getMenu();
        /*MenuItem runtime_item = menu.add(0,0,0, extractedCourseList.get(0).getCourseTitle());
        runtime_item.setIcon(R.drawable.ic_school);

        runtime_item = menu.add(0,1,0,extractedCourseList.get(1).getCourseTitle());
        runtime_item.setIcon(R.drawable.ic_school);

        runtime_item = menu.add(0,2,0,extractedCourseList.get(2).getCourseTitle());
        runtime_item.setIcon(R.drawable.ic_school);

        runtime_item = menu.add(0,3,0,extractedCourseList.get(3).getCourseTitle());
        runtime_item.setIcon(R.drawable.ic_school);

        runtime_item = menu.add(1,5,0,"Add Course");
        runtime_item.setIcon(R.drawable.ic_add);*/
        updateNavigationMenu(extractedCourseList);

        //listView.setOnItemClickListener(this::removeOnListItemClick);

        if (savedInstanceState == null) {
            navigationView.setCheckedItem(menu.getItem(0));

            Fragment fragment = new CourseFragment();
            Bundle bundle = new Bundle();
            bundle.putString("courseTitle", extractedCourseList.get(0).getCourseTitle());
            fragment.setArguments(bundle);

            getSupportActionBar().setTitle("Course Organizer " + extractedCourseList.get(0).getCourseTitle());

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    fragment).commit();
        }
        /* ************************************************************************************************************* */
    }

    public void updateNavigationMenu(ArrayList<Course> courseList){
        menu.clear();
        MenuItem newMenuItem;
        for(int i = 0; i < courseList.size(); i++){
            newMenuItem = menu.add(0, i, 0, courseList.get(i).getCourseTitle());
            newMenuItem.setIcon(R.drawable.ic_school);
        }
        newMenuItem = menu.add(1, 0, 0, "Add");
        newMenuItem.setIcon(R.drawable.ic_add);

        navigationView.inflateMenu(R.menu.draw_menu);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if(item.getGroupId() == 1){
            addCourseDialog1();
            return true;
        }

        Fragment fragment = new CourseFragment();
        Bundle bundle = new Bundle();
        bundle.putString("courseTitle", extractedCourseList.get(item.getItemId()).getCourseTitle());
        bundle.putInt("position", item.getItemId());
        fragment.setArguments(bundle);

        getSupportActionBar().setTitle("Course Organizer " + extractedCourseList.get(item.getItemId()).getCourseTitle());

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                fragment).commit();

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public void addCourseDialog(){
        //Attaches the calling activity to the dialog
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainActivity.this);
        //Retrieve and prepare the UI for the dialog box
        View view = getLayoutInflater().inflate(R.layout.add_course_dialog, null);

        //Set textViews
        TextView courseTitle = view.findViewById(R.id.addCourseTitle);

        TextView instructorName = view.findViewById(R.id.addInstructorName);
        TextView instructorPhone = view.findViewById(R.id.addInstructorPhone);
        TextView instructorEmail = view.findViewById(R.id.addInstructorEmail);
        TextView instructorOffice = view.findViewById(R.id.addInstructorOffice);
        TextView instructorOfficeHours = view.findViewById(R.id.addInstructorOfficeHours);

        TextView courseLocation = view.findViewById(R.id.addLocation);
        TextView courseDays = view.findViewById(R.id.addCourseDays);
        TextView courseTime = view.findViewById(R.id.addCourseTime);

        //Assigns the UI to the dialog box and sets the title and behavior of positive
        //and negative buttons
        builder.setView(view)
                .setTitle("Add Course")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    /**
                     * Not implemented closes dialog by default.
                     * @param dialog The associated dialog
                     * @param which
                     */
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("Add Course", new DialogInterface.OnClickListener() {
                    /**
                     * Determines behavior of apply button on click, passes the string used to pass
                     * string back to DetailActivity
                     * @param dialog The associated dialog
                     * @param which
                     */
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Apply changes
                        newCourse = new Course(courseTitle.getText().toString(),
                                courseDays.getText().toString(),
                                courseTime.getText().toString(),
                                courseLocation.getText().toString(),
                                instructorName.getText().toString(),
                                instructorPhone.getText().toString(),
                                instructorEmail.getText().toString(),
                                instructorOffice.getText().toString(),
                                instructorOfficeHours.getText().toString());
                        extractedCourseList.add(newCourse);
                        dbHelper.addCourse(newCourse);
                        updateNavigationMenu(extractedCourseList);

                    }
                });
        android.app.AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void deleteAndSwitchFragments(){
        Fragment newFragment = new CourseFragment();

        extractedCourseList = dbHelper.getAllCourses();

        Bundle bundle = new Bundle();
        bundle.putString("courseTitle", extractedCourseList.get(0).getCourseTitle());
        newFragment.setArguments(bundle);

        // Insert the fragment by replacing any existing fragment
        getSupportActionBar().setTitle("Course Organizer " + extractedCourseList.get(0).getCourseTitle());
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                newFragment).commit();
    }

    public void addCourseDialog1(){
        //Attaches the calling activity to the dialog
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainActivity.this);
        //Retrieve and prepare the UI for the dialog box
        View view = getLayoutInflater().inflate(R.layout.add_course_1, null);
        EditText addCourseTitle = view.findViewById(R.id.addCourseTitle);
        //Set textViews

        //Assigns the UI to the dialog box and sets the title and behavior of positive
        //and negative buttons
        builder.setView(view)
                .setTitle("Add Course")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("Next", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Course newCourse = new Course(addCourseTitle.getText().toString());
                        addCourseDialog2(newCourse);
                    }
                });
        android.app.AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void addCourseDialog2(Course newCourse){
        Log.i("Course NAme", String.valueOf(newCourse.getCourseTitle()));
        //Attaches the calling activity to the dialog
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainActivity.this);
        //Retrieve and prepare the UI for the dialog box
        View view = getLayoutInflater().inflate(R.layout.add_course_2, null);

        //Assigns the UI to the dialog box and sets the title and behavior of positive
        //and negative buttons
        builder.setView(view)
                .setTitle("Instructor Information")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("Next", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        newCourse.setProfessorName(((EditText)view.findViewById(R.id.addInstructorName)).getText().toString());
                        newCourse.setProfessorPhone(((EditText)view.findViewById(R.id.addInstructorPhone)).getText().toString());
                        newCourse.setProfessorEmail(((EditText)view.findViewById(R.id.addInstructorEmail)).getText().toString());

                        addCourseDialog3(newCourse);
                    }
                });
        android.app.AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void addCourseDialog3(Course newCourse){
        //Attaches the calling activity to the dialog
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainActivity.this);
        //Retrieve and prepare the UI for the dialog box
        View view = getLayoutInflater().inflate(R.layout.add_course_3, null);
        EditText addInstructorOfficeHours = view.findViewById(R.id.addInstructorOfficeHours);
        addInstructorOfficeHours.setInputType(InputType.TYPE_NULL);

        addInstructorOfficeHours.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("EditText Listener", "Click heard");
                addTimeDialog1((EditText)v, newCourse, true);
                addInstructorOfficeHours.setText(newCourse.getProfessorOfficeHours());
            }
        });
        //Set textViews

        //Assigns the UI to the dialog box and sets the title and behavior of positive
        //and negative buttons
        builder.setView(view)
                .setTitle("Instructor Information cont.")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("Next", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        newCourse.setProfessorOfficeLocation(((EditText)view.findViewById(R.id.addInstructorOffice)).getText().toString());
                        addCourseDialog4(newCourse);
                    }
                });
        android.app.AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void addTimeDialog1(EditText editText, Course newCourse, boolean isStart) {
        //Creates calender object to communicate with TimePickerDialog
        final Calendar myCalender = Calendar.getInstance();
        int hour = myCalender.get(Calendar.HOUR_OF_DAY);
        int minute = myCalender.get(Calendar.MINUTE);

        //Sets the listener that is called when the time is set
        TimePickerDialog.OnTimeSetListener myTimeListener = new TimePickerDialog.OnTimeSetListener(){
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                Log.i("Time Picker input", String.valueOf(hourOfDay));
                myCalender.set(Calendar.HOUR_OF_DAY, hourOfDay);
                myCalender.set(Calendar.MINUTE, minute);
                final String time = String.valueOf(myCalender.get(Calendar.HOUR_OF_DAY)) +
                        ":" + String.valueOf(myCalender.get(Calendar.MINUTE));
                Log.i("TimerPicker output", time);
                if(isStart){
                    newCourse.setProfessorOfficeHours(time);
                    addTimeDialog1(editText, newCourse, false);
                }
                else{
                    newCourse.setProfessorOfficeHours(newCourse.getProfessorOfficeHours() + " - " + time);
                    editText.setText(newCourse.getProfessorOfficeHours());
                }
            }
        };

        TimePickerDialog timePickerDialog = new TimePickerDialog(MainActivity.this, android.R.style.Theme_Material_Dialog_NoActionBar, myTimeListener, hour, minute, true);
        if(isStart){timePickerDialog.setTitle("Choose start time:");}
        else {timePickerDialog.setTitle("Choose end time:");}
        timePickerDialog.getWindow();
        timePickerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        timePickerDialog.show();
    }

    public void addCourseDialog4(Course newCourse){
        //Attaches the calling activity to the dialog
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainActivity.this);
        //Retrieve and prepare the UI for the dialog box
        View view = getLayoutInflater().inflate(R.layout.add_course_4, null);
        EditText addCourseDays = view.findViewById(R.id.addCourseDays);
        EditText addCourseTime = view.findViewById(R.id.addCourseTime);

        addCourseDays.setInputType(InputType.TYPE_NULL);
        addCourseTime.setInputType(InputType.TYPE_NULL);

        addCourseDays.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDayDialog((EditText)v, newCourse);
            }
        });

        addCourseTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTimeDialog2((EditText) v, newCourse, true);
            }
        });
        //Set textViews

        //Assigns the UI to the dialog box and sets the title and behavior of positive
        //and negative buttons
        builder.setView(view)
                .setTitle("Course Information")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        newCourse.setLocation(((EditText)view.findViewById(R.id.addCourseLocation)).getText().toString());

                        extractedCourseList.add(newCourse);
                        updateNavigationMenu(extractedCourseList);
                        dbHelper.addCourse(newCourse);

                        Fragment fragment = new CourseFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("courseTitle", newCourse.getCourseTitle());
                        bundle.putInt("position", extractedCourseList.size()-1);
                        fragment.setArguments(bundle);

                        getSupportActionBar().setTitle("Course Organizer " + newCourse.getCourseTitle());

                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                fragment).commit();

                        drawer.closeDrawer(GravityCompat.START);
                    }
                });
        android.app.AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void addTimeDialog2(EditText editText, Course newCourse, boolean isStart) {
        //Creates calender object to communicate with TimePickerDialog
        final Calendar myCalender = Calendar.getInstance();
        int hour = myCalender.get(Calendar.HOUR_OF_DAY);
        int minute = myCalender.get(Calendar.MINUTE);

        //Sets the listener that is called when the time is set
        TimePickerDialog.OnTimeSetListener myTimeListener = new TimePickerDialog.OnTimeSetListener(){
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                Log.i("Time Picker input", String.valueOf(hourOfDay));
                myCalender.set(Calendar.HOUR_OF_DAY, hourOfDay);
                myCalender.set(Calendar.MINUTE, minute);
                final String time = String.valueOf(myCalender.get(Calendar.HOUR_OF_DAY)) +
                        ":" + String.valueOf(myCalender.get(Calendar.MINUTE));
                Log.i("TimerPicker output", time);
                if(isStart){
                    newCourse.setTime(time);
                    addTimeDialog2(editText, newCourse, false);
                }
                else{
                    newCourse.setTime(newCourse.getTime() + " - " + time);
                    editText.setText(newCourse.getTime());
                }
            }
        };

        TimePickerDialog timePickerDialog = new TimePickerDialog(MainActivity.this, android.R.style.Theme_Material_Dialog_NoActionBar, myTimeListener, hour, minute, true);
        if(isStart){timePickerDialog.setTitle("Choose start time:");}
        else {timePickerDialog.setTitle("Choose end time:");}
        timePickerDialog.getWindow();
        timePickerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        timePickerDialog.show();
    }

    public void addDayDialog(EditText editText, Course newCourse){
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainActivity.this);
        View view = getLayoutInflater().inflate(R.layout.edit_days_dialog, null);
        //Retrieves MaterialDay picker from XML
        MaterialDayPicker dayPicker = view.findViewById(R.id.day_picker);

        builder.setView(view)
                .setTitle("Select Course Days")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("Select", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Recieves selected days
                        List<MaterialDayPicker.Weekday> daysSelected = dayPicker.getSelectedDays();
                        String days = formatWeekDays(daysSelected);
                        editText.setText(days);
                        newCourse.setDays(days);

                    }
                });
        android.app.AlertDialog dialog = builder.create();
        dialog.show();
    }

    public String formatWeekDays(List<MaterialDayPicker.Weekday> weekdays){
        String formatted = "";
        for(MaterialDayPicker.Weekday w : weekdays){
            if(w.toString().equals("THURSDAY")){formatted += "R";}
            else{
                formatted += w.toString().charAt(0);
            }
        }
        return formatted;
    }

    public ArrayList<Task> getTaskList(){return taskList;}


}