package com.example.android.jotitdown2;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

public class DetailActivity extends AppCompatActivity {

    EditText title;
    EditText content;
    Button date;
    Button time;
    Button button;
    Intent intent;
    Button start;
    Button stop;
    long[] epoch = new long[1];
    Todo todo;
    ExpenseOpenHelper openHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        title =(EditText) findViewById(R.id.titleedit);
        content = (EditText)findViewById(R.id.content);
        date = (Button)findViewById(R.id.date);
        time = (Button)findViewById(R.id.time);
        start = (Button)findViewById(R.id.alarm);
        stop = (Button)findViewById(R.id.stop);

        intent = getIntent();
        todo = (Todo)intent.getSerializableExtra("Todo");

        title.setText(todo.getTitle());
        content.setText(todo.getNote());
         epoch[0] = todo.getEpoch();


        Calendar c = Calendar.getInstance();
        final int mYear = c.get(Calendar.YEAR);
        final int mMonth = c.get(Calendar.MONTH);
        final int mDay = c.get(Calendar.DAY_OF_MONTH);
        final int mhour = c.get(Calendar.HOUR_OF_DAY);
        final int min = c.get(Calendar.MINUTE);

        final Calendar calendar = Calendar.getInstance();;

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(DetailActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        calendar.set(i,i1,i2,0,0,0);
                        epoch[0] = calendar.getTimeInMillis();
                    }
                },mYear,mMonth,mDay);
                datePickerDialog.show();
            }
        });

        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(DetailActivity.this, new TimePickerDialog.OnTimeSetListener(){
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                        calendar.set(Calendar.HOUR_OF_DAY,i);
                        calendar.set(Calendar.MINUTE,i1);
                        epoch[0] = calendar.getTimeInMillis();

                    }
                },mhour,min,false);
                timePickerDialog.show();
            }
        });

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendBroadcast();
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                Intent alarmIntent = new Intent(DetailActivity.this,AlarmReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(DetailActivity.this,1,alarmIntent,0);
                alarmManager.cancel(pendingIntent);
                Toast.makeText(DetailActivity.this,"Alarm cancelled",Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void buttonClicked(View view){
        button = (Button)view;
        String updatedtitle = title.getEditableText().toString();
        String updatedcontent = content.getEditableText().toString();
        long updated_epoch = epoch[0];

        Intent i = new Intent();
        i.putExtra("title",updatedtitle);
        i.putExtra("content",updatedcontent);
        i.putExtra("epoch",updated_epoch);
        setResult(10,i);
        finish();

        openHelper = ExpenseOpenHelper.getInstance(getApplicationContext());
        SQLiteDatabase sqLiteDatabase = openHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(Contract.TODO_TITLE,updatedtitle);
        contentValues.put(Contract.TODO_CONTENT,updatedcontent);
        contentValues.put(Contract.TODO_DATE,updated_epoch);

        sqLiteDatabase.update(Contract.TODO_TABLE_NAME,contentValues,Contract.TODO_ID + " = ?",new String[]{todo.id + ""} );

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail_activity,menu);
        return  true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.remove) {
            //code to remove the note
            AlertDialog.Builder builder = new AlertDialog.Builder(DetailActivity.this);
            View view1 = getLayoutInflater().inflate(R.layout.dialogue_remove_layout, null);
            TextView textView = view1.findViewById(R.id.title);
            textView.setText("Remove note?");
            builder.setView(view1);
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    openHelper = ExpenseOpenHelper.getInstance(getApplicationContext());
                    SQLiteDatabase db = openHelper.getWritableDatabase();
                    db.delete(Contract.TODO_TABLE_NAME, Contract.TODO_ID + " = ? ", new String[]{todo.id + ""});
                    finish();
                }
            });
            builder.setNegativeButton("Cancel", null);
            AlertDialog dialog = builder.create();
            dialog.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void sendBroadcast(){
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        Intent alarmIntent = new Intent(this,AlarmReceiver.class);
        alarmIntent.putExtra("title", title.getEditableText().toString());
        alarmIntent.putExtra("content", content.getEditableText().toString());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,1,alarmIntent,0);

        Toast.makeText(DetailActivity.this , "Alarm set", Toast.LENGTH_SHORT).show();
        alarmManager.set(AlarmManager.RTC,
                epoch[0],
                pendingIntent);
    }

//    public static String getCurrentTimeStamp(){
//        try {
//
//            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            String currentDateTime = dateFormat.format(new Date()); // Find todays date
//
//            return currentDateTime;
//        } catch (Exception e) {
//            e.printStackTrace();
//
//            return null;
//        }
//    }
}
