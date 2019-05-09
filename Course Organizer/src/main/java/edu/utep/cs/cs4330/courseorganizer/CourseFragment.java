package edu.utep.cs.cs4330.courseorganizer;
/** @author Kenneth Ward
 * @version 1.0
 * @since 5/8/2019
 */

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import ca.antonious.materialdaypicker.MaterialDayPicker;

/**
 * CourseFragment course is responsible for displaying all information related
 * to a specific course. Gets course information through queries to an SQLite database
 * containing all the courses, and uses this information to populate UI's.
 * CourseFragment also updates the database if course informatoin is modified by the user.
 */
public class CourseFragment extends Fragment {
    ListView listView;
    ArrayList<Task> taskList;
    Course course;
    TextView textProfessorName;
    TextView textProfessorPhone;
    TextView textProfessorEmail;
    TextView textProfessorOffice;
    TextView textProfessorOfficeHours;
    TextView textCourseLocation;
    TextView textCourseDays;
    TextView textCourseTime;
    DBHelper dbHelper;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //Setting the layout
        View view =  inflater.inflate(R.layout.fragment_course, container, false);

        //Retrieving the database of courses
        dbHelper = new DBHelper(getContext());
        course = dbHelper.getCourse(getArguments().getString("courseTitle"));

        //Allows the CourseFragment to display an options menu
        setHasOptionsMenu(true);

        //All TextViews that display course info are retrieved
        textProfessorName = view.findViewById(R.id.textProfessorName);
        textProfessorPhone = view.findViewById(R.id.textProfessorPhone);
        textProfessorEmail = view.findViewById(R.id.textProfessorEmail);
        textProfessorOffice = view.findViewById(R.id.textProfessorOffice);
        textProfessorOfficeHours = view.findViewById(R.id.textProfessorOfficeHours);
        textCourseLocation = view.findViewById(R.id.textCourseLocation);
        textCourseDays = view.findViewById(R.id.textCourseDays);
        textCourseTime = view.findViewById(R.id.textCourseTime);

        //The TextViews are populated with information from the course
        updateTextViews();

        int position = getArguments().getInt("position");
        Log.i("Course Position", String.valueOf(position));
        Log.i("Position mod 3", String.valueOf(position%3));

        int red, green, blue;
        if(position%3 == 0){
            red = 0;
            green = 142;
            blue = 180;
        }
        else if(position%3 == 1){
            red = 31;
            green = 184;
            blue = 230;
        }
        else{
            red = 38;
            green = 187;
            blue = 203;
        }
        //Color theme is set
        view.findViewById(R.id.titleInstructor).setBackgroundColor(Color.rgb(red, green, blue));
        view.findViewById(R.id.titleLocation).setBackgroundColor(Color.rgb(red, green, blue));
        view.findViewById(R.id.titleTasks).setBackgroundColor(Color.rgb(red, green, blue));

        //The specific course that the fragment was called to display is retrieved
        //from the database
        taskList = dbHelper.getCourseTasks(getArguments().getString("courseTitle"));
        CustomAdapter listAdapter = new CustomAdapter(getContext(), taskList);
        listView = view.findViewById(R.id.listViewTasks);
        listView.setAdapter(listAdapter);

        //Allows the first two CardViews to have context menus
        registerForContextMenu(view.findViewById(R.id.card_view));
        registerForContextMenu(view.findViewById(R.id.card_view2));

        view.findViewById(R.id.buttonCall).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + textProfessorPhone.getText().toString()));
                startActivity(intent);
            }
        });

        view.findViewById(R.id.buttonEmail).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"emailaddress@emailaddress.com"});

                startActivity(Intent.createChooser(intent, "Send Email"));
            }
        });

        return view;
    }

    /**
     * A helper method that is typically called after one or more course fields have been modified.
     * Helps keep TextViews formatted.
     */
    public void updateTextViews(){
        textProfessorName.setText(course.getProfessorName());
        textProfessorPhone.setText("Phone: " + course.getProfessorPhone());
        textProfessorEmail.setText("Email: " + course.getProfessorEmail());
        textProfessorOffice.setText("Office: " + course.getProfessorOfficeLocation());
        textProfessorOfficeHours.setText("Office Hours: " + course.getProfessorOfficeHours());
        textCourseLocation.setText("Location: " + course.getLocation());
        textCourseDays.setText("Days: " + course.getDays());
        textCourseTime.setText("Time: " + course.getTime());
    }

    //Initializes the layout for the context menu. The menu is dynamically
    // selected based on which view called it.
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        switch (v.getId()){
            case R.id.card_view:
                menu.setHeaderTitle("Edit Instructor");
                getActivity().getMenuInflater().inflate(R.menu.instructor_context_menu, menu);

                break;
            case R.id.card_view2:
                menu.setHeaderTitle("Edit Location and Time");
                getActivity().getMenuInflater().inflate(R.menu.location_context_menu, menu);

                break;
        }
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        //Switch statement decides which dialog will be opened
        //based on which MenuItem in the context menu was pressed.
        switch (item.getItemId()){
            case R.id.contextMenuName :
                editTextDialog(R.id.contextMenuName);
                break;
            case R.id.contextMenuPhone :
                editPhoneDialog();
                break;
            case R.id.contextMenuEmail :
                editTextDialog(R.id.contextMenuEmail);
                break;
            case R.id.contextMenuOffice :
                editTextDialog(R.id.contextMenuOffice);
                break;
            case R.id.contextMenuOfficeHours :
                editTimeDialog(true, R.id.contextMenuOfficeHours);
                break;
            case R.id.contextMenuLocation :
                editTextDialog(R.id.contextMenuLocation);
                break;
            case R.id.contextMenuDays :
                editDayDialog();
                break;
            case R.id.contextMenuTime :
                editTimeDialog(true, R.id.contextMenuTime);
                break;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //OPtions menu layout set
        inflater.inflate(R.menu.course_fragment_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Decides which dialog to opened based on which options menu item was pressed
        switch(item.getItemId()){
            case R.id.courseMenuButtonAdd:
                addTaskDialog();
                break;

            case R.id.courseMenuButtonDelete:
                deleteCourseDialog();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A helper method that configures a dialog which is used to edit multiple
     * fields in the CourseFragment, dynamically changes the title, entry field, and more,
     * based on where it was called from
     * @param id The resource id of the MenuItem which triggered this method.
     */
    public void editTextDialog(int id){
        //Attaches the calling activity to the dialog
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
        //Retrieve and prepare the UI for the dialog box
        View view = getLayoutInflater().inflate(R.layout.edit_one_dialog, null);
        EditText editText = view.findViewById(R.id.editText);
        editText.setInputType(InputType.TYPE_CLASS_TEXT);

        //Sets the title and editText hint based on which button was pressed
        String title;
        switch (id){
            case R.id.contextMenuName :
                title ="Edit Instructor Name";
                editText.setHint("Enter Name");
                break;
            case R.id.contextMenuEmail :
                title = "Edit Email";
                editText.setHint("Enter Email");
                break;
            case R.id.contextMenuOffice :
                title = "Edit Office Location";
                editText.setHint("Enter Office Location");
                break;
            case R.id.contextMenuLocation :
                editText.setHint("Enter Course Location");
                title = "Edit Location";
                break;
            default:
                title = "Edit";
                break;
        }
        //Assigns the UI to the dialog box and sets the title and behavior of positive
        //and negative buttons
        builder.setView(view)
                .setTitle(title)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Determines which course field to modify
                        switch (id){
                            case R.id.contextMenuName :
                                course.setProfessorName(editText.getText().toString());
                                dbHelper.updateCourse(course);
                                break;
                            case R.id.contextMenuEmail :
                                course.setProfessorEmail(editText.getText().toString());
                                dbHelper.updateCourse(course);
                                break;
                            case R.id.contextMenuOffice :
                                course.setProfessorOfficeLocation(editText.getText().toString());
                                dbHelper.updateCourse(course);
                                break;
                            case R.id.contextMenuLocation :
                                course.setLocation(editText.getText().toString());
                                dbHelper.updateCourse(course);
                                break;
                            default:
                                break;
                        }
                        updateTextViews();
                    }
                });
        android.app.AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Not yet implemented
     */
    public void editDateDialog(){
        //Sets dialog layout
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
        View view = getLayoutInflater().inflate(R.layout.edit_one_dialog, null);
        //Formats editText to take in dates and times
        EditText editText = view.findViewById(R.id.editText);
        editText.setInputType(InputType.TYPE_CLASS_DATETIME);

        builder.setView(view)
                .setTitle("Add Task")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        android.app.AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * A helper method which initializes and configures a dialog which is used
     * to modify the time and professorOfficeHours field of the course. Calls itself
     * once to recieve the end time of either the course or office hours
     * @param isStart True ndicates if this is the first call to the method.
     *                False indicates it has been called a second time to accept to emd time from the user.
     * @param id
     */
    public void editTimeDialog(boolean isStart, int id){
        //Creates calender object to communicate with TimePickerDialog
        final Calendar myCalender = Calendar.getInstance();
        int hour = myCalender.get(Calendar.HOUR_OF_DAY);
        int minute = myCalender.get(Calendar.MINUTE);

        //Sets the listener that is called when the time is set
        TimePickerDialog.OnTimeSetListener myTimeListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                if (view.isShown()) {
                    //Updates the calendar object
                    Log.i("Time Picker", String.valueOf(hourOfDay));
                    myCalender.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    myCalender.set(Calendar.MINUTE, minute);
                    final String time = String.valueOf(myCalender.get(Calendar.HOUR_OF_DAY)) +
                            ":" + String.valueOf(myCalender.get(Calendar.MINUTE));

                    //Decides which time textview to modify. Updates database and UI
                    switch (id){
                        case R.id.contextMenuOfficeHours:
                            //Wipes the old time and adds new start time
                            if(isStart){course.setProfessorOfficeHours(time);}
                            //Appends the end time
                            else{
                                course.setProfessorOfficeHours(course.getProfessorOfficeHours() + " - " + time);
                                updateTextViews();
                                dbHelper.updateCourse(course);
                            }
                            break;
                        case R.id.contextMenuTime:
                            //Wipes the old time and adds new start time
                            if(isStart){course.setTime(time);}
                            //Appends the end time
                            else{
                                course.setTime(course.getTime() + " - " + time);
                                updateTextViews();
                                dbHelper.updateCourse(course);
                            }
                            break;
                    }

                    //Opens the dialog
                    if(isStart)editTimeDialog(false, id);
                }
            }
        };

        //Starts the dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), android.R.style.Theme_Material_Dialog_NoActionBar, myTimeListener, hour, minute, true);
        if(isStart){timePickerDialog.setTitle("Choose start time:");}
        else {timePickerDialog.setTitle("Choose end time:");}
        timePickerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        timePickerDialog.show();
    }

    /**
     * Helper method for creating a dialog that lets the user edit the instructor's phone number.
     */
    public void editPhoneDialog(){
        //Set dialog layout
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
        View view = getLayoutInflater().inflate(R.layout.edit_one_dialog, null);
        EditText editText = view.findViewById(R.id.editText);
        editText.setInputType(InputType.TYPE_CLASS_PHONE);
        editText.setHint("Enter phone number");

        builder.setView(view)
                .setTitle("Edit Phone")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        course.setProfessorPhone(editText.getText().toString());
                        updateTextViews();
                    }
                });
        android.app.AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Creates a dialog that allows the user to specify what days of the week the course falls on.
     */
    public void editDayDialog(){
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
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

                        //Assign formatted weekdays to course
                        course.setDays(formatWeekDays(daysSelected));

                        //Update UI and DB
                        dbHelper.updateCourse(course);
                        updateTextViews();
                    }
                });
        android.app.AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Takes in the list of Weekday objects returned by MaterialDayPicker object, and
     * converts the weekdays into one letter representations for each weekday i.e
     * {MONDAY, WEDNESDAY, FRIDAY} becomes MWF
     * @param weekdays weekdays objects
     * @return
     */
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

    /**
     * Creates a dialog that allows the user to add a task to the course task list.
     * Recieves a task description and a due date.
     */
    public void addTaskDialog(){
        //Attaches the calling activity to the dialog
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
        //Retrieve and prepare the UI for the dialog box
        View view = getLayoutInflater().inflate(R.layout.add_task_dialog, null);

        //Set textViews

        //Assigns the UI to the dialog box and sets the title and behavior of positive
        //and negative buttons
        builder.setView(view)
                .setTitle("Add Task")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Creates new task object
                        Task t = new Task(((TextView)view.findViewById(R.id.addTask)).getText().toString(),
                                course.getCourseTitle(),
                                ((TextView)view.findViewById(R.id.addDueDate)).getText().toString());

                        //Updates listView
                        taskList.add(t);
                        listView.setAdapter(new CustomAdapter(getContext(), taskList));

                        //Updates DB
                        dbHelper.addTasks(t.getTask(), t.getCourse(), t.getDueDate());
                    }
                });
        android.app.AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Creates a dialog that allows the user to delete a course.
     */
    public void deleteCourseDialog(){
        //Attaches the calling activity to the dialog
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
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

                        //The current course being displayed is removed from the database
                        ArrayList<Course> courseList = dbHelper.getAllCourses();
                        for(Course c : courseList){
                            if(c.getCourseTitle().equals(course.getCourseTitle())){
                                courseList.remove(c);
                                break;
                            }
                        }
                        dbHelper.deleteCourse(course);

                        //The navigation menu is updated and a new CourseFragment for another course is opened
                        ((MainActivity)getActivity()).updateNavigationMenu(courseList);
                        ((MainActivity)getActivity()).deleteAndSwitchFragments();
                    }
                });
        android.app.AlertDialog dialog = builder.create();
        dialog.show();
    }
}