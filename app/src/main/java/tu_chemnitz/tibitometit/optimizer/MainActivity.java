package tu_chemnitz.tibitometit.optimizer;

import android.content.DialogInterface;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ExpandableListView;
import android.widget.Toast;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity {

    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<Tasks>> listDataChild;
    public static String  TMITEM= "Optimizer_Task_Details";
    DBManager dbManager;
    List<Tasks> listTasks;
    public static boolean flagInitialized = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<Tasks>>();

        dbManager = new DBManager(this,null,null,1);
        //if(!flagInitialized) {
        //insertSampleData();
        //flagInitialized=true;
        //}

        // preparing list data
        prepareListData();

        // get the listview
        expListView = (ExpandableListView) findViewById(R.id.elvItems);
        listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);

        // setting list adapter
        expListView.setAdapter(listAdapter);
        // Listview Group click listener
        expListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
                // Toast.makeText(getApplicationContext(),
                // "Group Clicked " + listDataHeader.get(groupPosition),
                // Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        // Listview on child click listener
        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                Toast.makeText(
                        getApplicationContext(),
                        listDataHeader.get(groupPosition)
                                + " : "
                                + listDataChild.get(
                                listDataHeader.get(groupPosition)).get(
                                childPosition), Toast.LENGTH_SHORT)
                        .show();
                showDetails(parent,listDataChild.get(
                        listDataHeader.get(groupPosition)).get(
                        childPosition));
                return false;
            }
        });

        // Listener for the Listview Group when expanded
        expListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
                Toast.makeText(getApplicationContext(),
                        listDataHeader.get(groupPosition) + " Expanded",
                        Toast.LENGTH_SHORT).show();
            }
        });

        // Listener for the Listview Group when collasped
        expListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {
                Toast.makeText(getApplicationContext(),
                        listDataHeader.get(groupPosition) + " Collapsed",
                        Toast.LENGTH_SHORT).show();
            }
        });

        final Handler handler = new Handler();
        handler.postDelayed( new Runnable() {

            @Override
            public void run() {
                Toast.makeText(getApplicationContext(),"Refreshed",Toast.LENGTH_SHORT).show();
                handler.postDelayed( this, 60 * 1000 );
                finish();
                startActivity(getIntent());
            }
        }, 60 * 1000 );
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

    public void deleteTasks(){
        dbManager.deleteTasks();
    }
    public void openAddActivity(){
        Intent intent = new Intent(this,AddTask.class);
        String message = "Add a new Activity";
        intent.putExtra(TMITEM,message);
        startActivity(intent);

    }

    public void openProgressActivity(){
        Intent intent = new Intent(this,ShowProgress.class);
        String message = "Add a new Activity";
        intent.putExtra(TMITEM,message);
        startActivity(intent);

    }

    public void showDetails(View view, Tasks task)
    {
        Intent intent = new Intent(this,TaskDetails.class);
        intent.putExtra(TMITEM,task.get_id());
        startActivity(intent);
    }
    /*
    * Preparing the list data
    * Temporary, to be removed
    */
    private void prepareListData() {

        List<Tasks> futureTasksList = dbManager.getFutureTasks();
        List<Tasks> pastTasksList = dbManager.getPastTasks();
        List<Tasks> allTasksList = dbManager.getAllTasks();
        List<Tasks> iuTasksList = new ArrayList<Tasks>();
        List<Tasks> iTasksList = new ArrayList<Tasks>();
        List<Tasks> uTasksList = new ArrayList<Tasks>();
        List<Tasks> nTasksList = new ArrayList<Tasks>();

        for(int i=0;i<futureTasksList.size();i++){
            Date date = futureTasksList.get(i).get_deadline();
            switch (futureTasksList.get(i).get_category())
            {
                case "I": {
                    long dateDiff = date.getTime() - new Date().getTime();
                    long dateDiffDays = TimeUnit.DAYS.convert(dateDiff, TimeUnit.MILLISECONDS);
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
        // Adding child data
        listDataHeader.add("Quadrant 1"+"_"+iuTasksList.size());
        listDataHeader.add("Quadrant 2"+"_"+iTasksList.size());
        listDataHeader.add("Quadrant 3"+"_"+uTasksList.size());
        listDataHeader.add("Quadrant 4"+"_"+nTasksList.size());
        listDataHeader.add("Past Tasks"+"_"+pastTasksList.size());
        listDataHeader.add("All Tasks"+"_"+allTasksList.size());
/*
        // Adding child data
        List<String> quadrant1 = new ArrayList<String>();
        quadrant1.add("The Shawshank Redemption");
        quadrant1.add("The Godfather");
        quadrant1.add("The Godfather: Part II");
        quadrant1.add("Pulp Fiction");
        quadrant1.add("The Good, the Bad and the Ugly");
        quadrant1.add("The Dark Knight");
        quadrant1.add("12 Angry Men");

        List<String> quadrant2 = new ArrayList<String>();
        quadrant2.add("The Conjuring");
        quadrant2.add("Despicable Me 2");
        quadrant2.add("Turbo");
        quadrant2.add("Grown Ups 2");
        quadrant2.add("Red 2");
        quadrant2.add("The Wolverine");

        List<String> quadrant3 = new ArrayList<String>();
        quadrant3.add("2 Guns");
        quadrant3.add("The Smurfs 2");
        quadrant3.add("The Spectacular Now");
        quadrant3.add("The Canyons");
        quadrant3.add("Europa Report");

        List<String> quadrant4 = new ArrayList<String>();
        quadrant4.add("2 Guns");
        quadrant4.add("The Smurfs 2");
        quadrant4.add("The Spectacular Now");
        quadrant4.add("The Canyons");
        quadrant4.add("Europa Report");

        List<String> pastTasks = new ArrayList<String>();
        pastTasks.add("2 Guns");
        pastTasks.add("The Smurfs 2");
        pastTasks.add("The Spectacular Now");
        pastTasks.add("The Canyons");
        pastTasks.add("Europa Report");
*/
        listDataChild.put(listDataHeader.get(0), iuTasksList); // Header, Child data
        listDataChild.put(listDataHeader.get(1), iTasksList);
        listDataChild.put(listDataHeader.get(2), uTasksList);
        listDataChild.put(listDataHeader.get(3), nTasksList);
        listDataChild.put(listDataHeader.get(4), pastTasksList);
        listDataChild.put(listDataHeader.get(5), allTasksList);
    }

    public void insertSampleData(){
        Calendar cal = Calendar.getInstance();
        cal.set(2016,12,25); // Adds 1 days
        Date date;
        SimpleDateFormat  dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
            date = cal.getTime();
            dbManager.addTask(new Tasks("Project", "Emb", date, "I", false));
            cal.add(Calendar.DATE,1);
            date = cal.getTime();
            dbManager.addTask(new Tasks("Report", "Automotive", date, "I", false));
            cal.add(Calendar.DATE,2);
            date = cal.getTime();
            dbManager.addTask(new Tasks("Project", "SESA", date, "I", false));
            cal.add(Calendar.DATE,2);
            date = cal.getTime();
            dbManager.addTask(new Tasks("Study", "SPAS", date, "I", false));
            cal.add(Calendar.DATE,2);
            date = cal.getTime();
            dbManager.addTask(new Tasks("Project", "RP", date, "N", false));
            cal.add(Calendar.DATE,2);
            date = cal.getTime();
            dbManager.addTask(new Tasks("Project", "SOSA", date, "I", false));
            cal.add(Calendar.DATE,2);
            date = cal.getTime();
            dbManager.addTask(new Tasks("Project", "DOSA", date, "N", false));
            cal.add(Calendar.DATE,2);
            date = cal.getTime();
            dbManager.addTask(new Tasks("Biriyani", "Chicken", date, "I", false));
    }

    @Override
    public void onRestart(){
        super.onRestart();
        finish();
        startActivity(getIntent());
    }
}
