package tu_chemnitz.tibitometit.optimizer;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ExpandableListView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity {

    /*Variable Declaration*/
    ExpandableListAdapter expandableListAdapter;            /*Manages the binding of the data to the ExpandableListView*/
    ExpandableListView expandableListView;                  /*ListView UI object that holds all the tasks acc to their category*/
    List<String> expandableListHeader;                      /*Header of the ExpandableListView that holds the Quadrant Names*/
    HashMap<String, List<Tasks>> expandableListChild;       /*The List of tasks under each quadrant*/
    public static String  TMITEM= "Optimizer_Task_Details"; /*Unique identifier to pass around Intents*/
    DBManager dbManager;                                    /*The interface to the SQLite Database*/
    List<Tasks> listTasks;                                  /*The actual list that holds all the tasks*/
    public static boolean flagInitialized = false;          /*A one time use flag to insert sample data into the database*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*Refresh the ExpandableListView on click */
        FloatingActionButton btnFloatingAction = (FloatingActionButton) findViewById(R.id.btnFloatingAction);
        btnFloatingAction.setOnClickListener(new View.OnClickListener(

        ) {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"Refreshed",Toast.LENGTH_SHORT).show();
                finish();
                startActivity(getIntent());
            }
        });

        expandableListHeader = new ArrayList<>();
        expandableListChild = new HashMap<>();

        /*Create the Database Manager object*/
        dbManager = new DBManager(this,null,null,1);

        /* Code to insert sample data into the database
        if(!flagInitialized) {
            insertSampleData();
            flagInitialized=true;

        }
*/

        // Preparing the data for the list of tasks
        processTasksList();

        // Get the ExpandableListView and set the adapter
        expandableListView = (ExpandableListView) findViewById(R.id.elvItems);
        expandableListAdapter = new ExpandableListAdapter(this, expandableListHeader, expandableListChild);
        expandableListView.setAdapter(expandableListAdapter);
        // Set the ExpandableListView Group click listener
        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
                // Toast.makeText(getApplicationContext(),
                // "Group Clicked " + expandableListHeader.get(groupPosition),
                // Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        // Set the ExpandableListView Child click listener
        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                /*Toast.makeText(
                        getApplicationContext(),
                        expandableListHeader.get(groupPosition)
                                + " : "
                                + expandableListChild.get(
                                expandableListHeader.get(groupPosition)).get(
                                childPosition), Toast.LENGTH_SHORT)
                        .show();
                        */
                showDetails(parent, expandableListChild.get(
                        expandableListHeader.get(groupPosition)).get(
                        childPosition));
                return false;
            }
        });

        // Set the ExpandableListView Group listener when expanded.
        /*expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
                Toast.makeText(getApplicationContext(),
                        expandableListHeader.get(groupPosition).substring(0,10) + " Expanded",
                        Toast.LENGTH_SHORT).show();
            }
        });

        // Set the ExpandableListView Group listener when collasped.
        expandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {
                Toast.makeText(getApplicationContext(),
                        expandableListHeader.get(groupPosition).substring(0,10) + " Collapsed",
                        Toast.LENGTH_SHORT).show();
            }
        });
*/
      /*  final Handler handler = new Handler();
        handler.postDelayed( new Runnable() {

            @Override
            public void run() {
                Toast.makeText(getApplicationContext(),"Refreshed",Toast.LENGTH_SHORT).show();
                handler.postDelayed( this, 60 * 1000 );
                finish();
                startActivity(getIntent());
            }
        }, 60 * 1000 );*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.menu_add:
                if(item.isChecked())
                    item.setChecked(false);
                else
                    item.setChecked(true);
                openAddActivity();
                return true;
            case R.id.menu_progress:
                if(item.isChecked())
                    item.setChecked(false);
                else
                    item.setChecked(true);
                openProgressActivity();
                return true;
            case R.id.menu_reset:
                new AlertDialog.Builder(this)
                        .setTitle("Delete all data")
                        .setMessage("Are you sure you want to delete all data")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // continue with delete
                                deleteTasks();
                                finish();
                                startActivity(getIntent());
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /*Truncate the database*/
    public void deleteTasks(){
        dbManager.deleteTasks();
    }

    /*Navigate to the activity to add a new activity*/
    public void openAddActivity(){
        Intent intent = new Intent(this,AddTask.class);
        String message = "Add a new Activity";
        intent.putExtra(TMITEM,message);
        startActivity(intent);

    }

    /*Show the progress of the user*/
    public void openProgressActivity(){
        Intent intent = new Intent(this,ShowProgress.class);
        String message = "Add a new Activity";
        intent.putExtra(TMITEM,message);
        startActivity(intent);

    }

    /*When one particular task is selected, show the details of the task*/
    public void showDetails(View view, Tasks task)
    {
        Intent intent = new Intent(this,TaskDetails.class);
        intent.putExtra(TMITEM,task.get_id());
        startActivity(intent);
    }


    /*Process the task and prepare the data for binding*/
    private void processTasksList() {

        /*Seggregate the Tasks into various lists
        * futureTasksList - Receive all future tasks for further seggregation
        * Quadrant 1 - iuTasksList(Important & Urgent)
        * Quadrant 2 - iTasksList(Important & not Urgent)
        * Quadrant 3 - uTasksList(not Important & Urgent)
        * Quadrant 3 - nTasksList(not Important & not Urgent)
        * pastTasksList - Tasklist that holds all the tasks that are already past
        * allTasksList - Tasklist that holds all the tasks
        */
        List<Tasks> futureTasksList = dbManager.getFutureTasks();
        List<Tasks> pastTasksList = dbManager.getPastTasks();
        List<Tasks> allTasksList = dbManager.getAllTasks();
        List<Tasks> iuTasksList = new ArrayList<>();
        List<Tasks> iTasksList = new ArrayList<>();
        List<Tasks> uTasksList = new ArrayList<>();
        List<Tasks> nTasksList = new ArrayList<>();

        for(int i=0;i<futureTasksList.size();i++){
            Date date = futureTasksList.get(i).get_deadline();
            switch (futureTasksList.get(i).get_category())
            {
                case "I": {
                    long dateDiff = date.getTime() - new Date().getTime();
                    long dateDiffDays = TimeUnit.DAYS.convert(dateDiff, TimeUnit.MILLISECONDS);
                    /*Criterion for urgent tasks - less than 2 days*/
                    if (dateDiffDays <= 2 && dateDiffDays >=0) {
                        futureTasksList.get(i).set_category("IU");
                        iuTasksList.add(futureTasksList.get(i));
                    } else {
                        futureTasksList.get(i).set_category("I");
                        iTasksList.add(futureTasksList.get(i));
                    }
                    break;
                }
                case "N": {
                    long dateDiff = date.getTime() - new Date().getTime();
                    long dateDiffDays = TimeUnit.DAYS.convert(dateDiff, TimeUnit.MILLISECONDS);
                    if (dateDiffDays <= 2 && dateDiffDays >=0) {
                        futureTasksList.get(i).set_category("U");
                        uTasksList.add(futureTasksList.get(i));
                    } else {
                        futureTasksList.get(i).set_category("N");
                        nTasksList.add(futureTasksList.get(i));
                    }
                    break;
                }
            }
        }

        /*Sort all the tasks according to their deadline - DateTime sorting*/
        Collections.sort(iuTasksList, new Comparator<Tasks>() {
            public int compare(Tasks o1, Tasks o2) {
                if (o1.get_deadline() == null || o2.get_deadline() == null)
                    return 0;
                return o1.get_deadline().compareTo(o2.get_deadline());
            }
        });
        Collections.sort(iTasksList, new Comparator<Tasks>() {
            public int compare(Tasks o1, Tasks o2) {
                if (o1.get_deadline() == null || o2.get_deadline() == null)
                    return 0;
                return o1.get_deadline().compareTo(o2.get_deadline());
            }
        });
        Collections.sort(uTasksList, new Comparator<Tasks>() {
            public int compare(Tasks o1, Tasks o2) {
                if (o1.get_deadline() == null || o2.get_deadline() == null)
                    return 0;
                return o1.get_deadline().compareTo(o2.get_deadline());
            }
        });
        Collections.sort(nTasksList, new Comparator<Tasks>() {
            public int compare(Tasks o1, Tasks o2) {
                if (o1.get_deadline() == null || o2.get_deadline() == null)
                    return 0;
                return o1.get_deadline().compareTo(o2.get_deadline());
            }
        });
        Collections.sort(pastTasksList, new Comparator<Tasks>() {
            public int compare(Tasks o1, Tasks o2) {
                if (o1.get_deadline() == null || o2.get_deadline() == null)
                    return 0;
                return o1.get_deadline().compareTo(o2.get_deadline());
            }
        });
        Collections.sort(allTasksList, new Comparator<Tasks>() {
            public int compare(Tasks o1, Tasks o2) {
                if (o1.get_deadline() == null || o2.get_deadline() == null)
                    return 0;
                return o1.get_deadline().compareTo(o2.get_deadline());
            }
        });
        // Adding the processed data to the ExpandableListView Header and Child

        expandableListHeader.add("Imp & Urg(Q1)"+"_"+iuTasksList.size());
        expandableListHeader.add("Imp & Not Urg(Q2)"+"_"+iTasksList.size());
        expandableListHeader.add("Not Imp & Urg(Q3)"+"_"+uTasksList.size());
        expandableListHeader.add("Not Imp & Not Urg(Q4)"+"_"+nTasksList.size());
        expandableListHeader.add("Past Tasks"+"_"+pastTasksList.size());
        expandableListHeader.add("All Tasks"+"_"+allTasksList.size());

        expandableListChild.put(expandableListHeader.get(0), iuTasksList); // Header, Child data
        expandableListChild.put(expandableListHeader.get(1), iTasksList);
        expandableListChild.put(expandableListHeader.get(2), uTasksList);
        expandableListChild.put(expandableListHeader.get(3), nTasksList);
        expandableListChild.put(expandableListHeader.get(4), pastTasksList);
        expandableListChild.put(expandableListHeader.get(5), allTasksList);
    }

    /*This is for one time initial insert of some sample data*/
    public void insertSampleData(){
        Calendar cal = Calendar.getInstance();
        cal.clear();
        Date date = new Date();
        SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
        int currentYear=Integer.parseInt(yearFormat.format(date));
        cal.set(currentYear,1,1);
            SimpleDateFormat  dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
            date = cal.getTime();
            dbManager.addTask(new Tasks("Pay Phone bill", "O2 bill", date, "I", false));
            cal.add(Calendar.DATE,1);
            date = cal.getTime();
            dbManager.addTask(new Tasks("Pay House Rent", "GGG", date, "I", false));
            cal.add(Calendar.DATE,2);
            date = cal.getTime();
            dbManager.addTask(new Tasks("Car Wash", "Volkswagen Service Center", date, "I", false));
            cal.add(Calendar.DATE,2);
            date = cal.getTime();
            dbManager.addTask(new Tasks("Exam", "SPAS", date, "I", false));
            cal.add(Calendar.DATE,2);
            date = cal.getTime();
            dbManager.addTask(new Tasks("Exam", "SESA", date, "N", false));
            cal.add(Calendar.DATE,2);
            date = cal.getTime();
            dbManager.addTask(new Tasks("Research Project", "SLAM should be done", date, "I", false));
            cal.add(Calendar.DATE,2);
            date = cal.getTime();
            dbManager.addTask(new Tasks("Register for Exam", "SESA", date, "N", false));
            cal.add(Calendar.DATE,2);
            date = cal.getTime();
            dbManager.addTask(new Tasks("Clean House up", "Kitchen specially", date, "I", false));
    }

    @Override
    public void onRestart(){
        super.onRestart();
        finish();
        startActivity(getIntent());
    }
}
