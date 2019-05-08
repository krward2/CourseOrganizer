package edu.utep.cs.cs4330.courseorganizer;

import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.zip.Inflater;

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

        dbHelper.addTasks("Read Gravity's Rainbow", insertedCourseList.get(2).getCourseTitle(), "3/3/3");

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
                    }
                });
        android.app.AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void addCourseDialog3(){
        //Attaches the calling activity to the dialog
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainActivity.this);
        //Retrieve and prepare the UI for the dialog box
        View view = getLayoutInflater().inflate(R.layout.delete_dialog, null);

        //Set textViews

        //Assigns the UI to the dialog box and sets the title and behavior of positive
        //and negative buttons
        builder.setView(view)
                .setTitle("Delete Course")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        android.app.AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void addCourseDialog4(){
        //Attaches the calling activity to the dialog
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainActivity.this);
        //Retrieve and prepare the UI for the dialog box
        View view = getLayoutInflater().inflate(R.layout.delete_dialog, null);

        //Set textViews

        //Assigns the UI to the dialog box and sets the title and behavior of positive
        //and negative buttons
        builder.setView(view)
                .setTitle("Delete Course")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        android.app.AlertDialog dialog = builder.create();
        dialog.show();
    }

    public ArrayList<Task> getTaskList(){return taskList;}


}