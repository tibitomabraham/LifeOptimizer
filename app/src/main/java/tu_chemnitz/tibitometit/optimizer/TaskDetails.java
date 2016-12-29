package tu_chemnitz.tibitometit.optimizer;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class TaskDetails extends AppCompatActivity implements View.OnClickListener{

    DBManager dbManager;
    Tasks task;
    private int mYear, mMonth, mDay, mHour, mMinute;
    private EditText dateText,timeText,descText;
    private TextView taskNameText;
    private RadioButton impRB,notImpRB,completedRB,notCompletedRB;
    private LinearLayout imgLayout;
    private Button updateButton, removeButton;
    private ImageView taskPicIV;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_details);
        dbManager=new DBManager(this,null,null,1);
        Intent intent = getIntent();
        //int taskId = Integer.parseInt(intent.getStringExtra(MainActivity.TMITEM));
        int taskId = intent.getIntExtra(MainActivity.TMITEM,0);
        task =dbManager.getTaskDetails(taskId);
        taskNameText = (TextView)findViewById(R.id.tvTaskName);
        descText = (EditText) findViewById( R.id.mltDescription);
        dateText = (EditText) findViewById( R.id.etDate);
        timeText = (EditText) findViewById( R.id.etTime);
        impRB = (RadioButton)findViewById(R.id.rbtnICategory);
        notImpRB = (RadioButton)findViewById(R.id.rbtnNICategory);
        updateButton = (Button)findViewById(R.id.btnUpdate);
        completedRB = (RadioButton)findViewById(R.id.rbtnStatusY);
        notCompletedRB =(RadioButton)findViewById(R.id.rbtnStatusN);
        removeButton = (Button)findViewById(R.id.btnRemove);
        taskPicIV = (ImageView)findViewById(R.id.ivTaskPic);
        imgLayout = (LinearLayout)findViewById(R.id.thumbnail);

        taskNameText.setText(task.get_name());
        descText.setText(task.get_description());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = dateFormat.format(task.get_deadline());
        dateText.setText(dateString);
        dateFormat.applyPattern("HH:mm");
        String timeString = dateFormat.format(task.get_deadline());
        timeText.setText(timeString);

        if (task.is_cancelled()) {
            updateButton.setEnabled(false);
            removeButton.setEnabled(false);
        }
        else{
            updateButton.setEnabled(true);
            removeButton.setEnabled(true);
        }
        if (task.is_status()) {
            completedRB.setChecked(true);
            notCompletedRB.setChecked(false);
            imgLayout.setBackgroundColor(Color.CYAN);
        }
        else{
            completedRB.setChecked(false);
            notCompletedRB.setChecked(true);
            imgLayout.setBackgroundColor(Color.RED);
        }
        switch (task.get_category())
        {
            case "I": {
                Date date = task.get_deadline();
                long dateDiff = date.getTime() - new Date().getTime();
                long dateDiffDays = TimeUnit.DAYS.convert(dateDiff, TimeUnit.MILLISECONDS);
                if (dateDiffDays <= 2 && dateDiffDays >=0) {
                    task.set_category("IU");
                    taskPicIV.setImageResource(R.drawable.imp_urg);
                } else {
                    task.set_category("I");
                    taskPicIV.setImageResource(R.drawable.imp);
                }
                impRB.setChecked(true);
                notImpRB.setChecked(false);
                break;
            }
            case "N": {
                Date date = task.get_deadline();
                long dateDiff = date.getTime() - new Date().getTime();
                long dateDiffDays = TimeUnit.DAYS.convert(dateDiff, TimeUnit.MILLISECONDS);
                if (dateDiffDays <= 2 && dateDiffDays >=0) {
                    task.set_category("U");
                    taskPicIV.setImageResource(R.drawable.urg);
                } else {
                    task.set_category("N");
                    taskPicIV.setImageResource(R.drawable.not_imp_urg);
                }
                impRB.setChecked(false);
                notImpRB.setChecked(true);
                break;
            }
        }
        dateText.setOnClickListener(this);
        timeText.setOnClickListener(this);
        updateButton.setOnClickListener(this);
        removeButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == dateText) {
                    // Get Current Date
                    final Calendar c = Calendar.getInstance();
                    mYear = c.get(Calendar.YEAR);
                    mMonth = c.get(Calendar.MONTH);
                    mDay = c.get(Calendar.DAY_OF_MONTH);


                    DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                            new DatePickerDialog.OnDateSetListener() {

                                @Override
                                public void onDateSet(DatePicker view, int year,
                                                      int monthOfYear, int dayOfMonth) {

                                    dateText.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                                }
                            }, mYear, mMonth, mDay);
                    datePickerDialog.show();

        }
        if (v == timeText) {

            // Get Current Time
            final Calendar c = Calendar.getInstance();
            mHour = c.get(Calendar.HOUR_OF_DAY);
            mMinute = c.get(Calendar.MINUTE);

            // Launch Time Picker Dialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                    new TimePickerDialog.OnTimeSetListener() {

                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay,
                                              int minute) {
                            timeText.setText(((hourOfDay/10<1)?"0"+hourOfDay:hourOfDay) + ":" + ((minute/10)<1?"0"+minute:minute));
                        }
                    }, mHour, mMinute, false);
            timePickerDialog.show();
        }

        if (v == updateButton) {
            task.set_name(taskNameText.getText().toString());
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date date=new Date();
            try {
                date= dateFormat.parse(dateText.getText().toString() + " " + timeText.getText().toString());
            }
            catch (ParseException e){
                e.printStackTrace();
            }
            task.set_description(descText.getText().toString());
            task.set_deadline(date);
            task.set_cancelled(false);

            if(impRB.isChecked()){
                task.set_category("I");
            }
            else if(notImpRB.isChecked()){
                task.set_category("N");
            }
            if(completedRB.isChecked()){
                task.set_status(true);
            }
            else if(notImpRB.isChecked()){
                task.set_status(false);
            }
            try {
                dbManager.updateTask(task);
                Toast.makeText(getApplicationContext(), "The task " + task.get_name() + " is updated", Toast.LENGTH_SHORT).show();
                finish();
                startActivity(getIntent());
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }

        if(v== removeButton){
         try{
            //Confirm Cancellation
             new AlertDialog.Builder(this)
                     .setTitle("Remove Task?")
                     .setMessage("Are you sure you want to remove the task "+task.get_name()+"?")
                     .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                         public void onClick(DialogInterface dialog, int which) {
                             // continue with delete
                             dbManager.cancelTask(task);
                             alertRemoval(task);
                             returnToMainTask();
                         }
                     })
                     .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                         public void onClick(DialogInterface dialog, int which) {
                             // do nothing
                         }
                     })
                     .setIcon(android.R.drawable.ic_dialog_alert)
                     .show();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        }
    }

    public void alertRemoval(Tasks task){
        new AlertDialog.Builder(this)
                .setTitle("Task Removed")
                .setMessage("The task "+ task.get_name()+" is removed.")
                .setNeutralButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public void returnToMainTask() {
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }
}
