package tu_chemnitz.tibitometit.optimizer;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

public class AddTask extends AppCompatActivity implements View.OnClickListener{

    private int mYear, mMonth, mDay, mHour, mMinute;
    private EditText dateText,timeText,taskNameText,descText;
    private TextView errorMessage;
    private RadioButton impRB,notImpRB,completedRB,notCompletedRB;
    private Button addButton;
    private DBManager dbManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
        dbManager=new DBManager(this,null,null,1);
        impRB=(RadioButton)findViewById(R.id.rbtnICategory);
        notImpRB=(RadioButton)findViewById(R.id.rbtnNICategory);
        completedRB=(RadioButton)findViewById(R.id.rbtnStatusY);
        notCompletedRB=(RadioButton)findViewById(R.id.rbtnStatusN);
        taskNameText = (EditText) findViewById( R.id.etTaskName);
        descText= (EditText) findViewById( R.id.mltDescription);
        dateText = (EditText) findViewById( R.id.etDate);
        timeText = (EditText) findViewById( R.id.etTime);
        errorMessage = (TextView) findViewById( R.id.tvErrorMessage);
        addButton= (Button)findViewById(R.id.btnAdd);

        //DateFormat datetimeFormat = DateFormat.getDateInstance(DateFormat.LONG, Locale.GERMANY);
        //DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.DEFAULT, Locale.GERMANY);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = dateFormat.format(new Date());
        dateText.setText(dateString);
        dateFormat.applyPattern("HH:mm");
        //dateFormat= DateFormat.getDateInstance(DateFormat.SHORT, Locale.GERMANY);
        String timeString = dateFormat.format(new Date());
        timeText.setText(timeString);
        dateText.setOnClickListener(this);
        timeText.setOnClickListener(this);
        addButton.setOnClickListener(this);
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
                            dateText.setText(year + "-" + (monthOfYear + 1)+"-" +dayOfMonth   );
                        }
                    }, mYear, mMonth, mDay);
            datePickerDialog.show();

        }
        else if (v == timeText) {

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
        else if(v==addButton){
            if(taskNameText.getText().toString().isEmpty()){
                errorMessage.setText(R.string.error_taskname);
                return;
            }
            if(descText.getText().toString().isEmpty()){
                errorMessage.setText(R.string.error_description);
                return;
            }
            errorMessage.setText("");

            Tasks tasks = new Tasks();
            tasks.set_name(taskNameText.getText().toString());
            //DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.LONG, Locale.GERMANY);
            //DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.DEFAULT, Locale.GERMANY);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date date=new Date();
            try {
                 date= dateFormat.parse(dateText.getText().toString() + " " + timeText.getText().toString());
            }
            catch (ParseException e){
                e.printStackTrace();
            }
            tasks.set_description(descText.getText().toString());
            tasks.set_deadline(date);
            tasks.set_cancelled(false);

            if(impRB.isChecked()){
                tasks.set_category("I");
            }
            else if(notImpRB.isChecked()){
                tasks.set_category("N");
            }
            try {
                dbManager.addTask(tasks);
                Toast.makeText(getApplicationContext(), "The task " + tasks.get_name() + " got added newly", Toast.LENGTH_SHORT).show();
                long secondDelay = 1000;
                //wait(secondDelay);
                returnToMainTask();
            }
            //catch (InterruptedException ie){
            //    ie.printStackTrace();
            //}
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public void returnToMainTask() {
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }

}
