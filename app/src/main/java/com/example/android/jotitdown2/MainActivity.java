package com.example.android.jotitdown2;

import android.Manifest;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    ArrayList<Todo> list;
    CustomAdapter adapter;
    ExpenseOpenHelper openHelper;
    public static final String MY_ACTION = "my_action";

    BroadcastReceiver broadcastReceiver;
    LocalBroadcastManager broadcastManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        broadcastManager = LocalBroadcastManager.getInstance(this);

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Toast.makeText(MainActivity.this,"Custom Broadcast",Toast.LENGTH_SHORT).show();
            }
        };

        listView = (ListView)findViewById(R.id.list);

        list = new ArrayList<>();

        adapter = new CustomAdapter(this,list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Todo note = list.get(i);
                //Toast.makeText(MainActivity.this,note.getTitle(),Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this,DetailActivity.class);
                intent.putExtra("Todo", note);
//                openHelper = ExpenseOpenHelper.getInstance(getApplicationContext());
//                SQLiteDatabase sqLiteDatabase1 = openHelper.getWritableDatabase();
//                sqLiteDatabase1.delete(Contract.TODO_TABLE_NAME,Contract.TODO_ID + " = ?",new String[]{i + ""} );
                startActivityForResult(intent,1);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                //final Todo todo = list.get(i);
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                View view1 = getLayoutInflater().inflate(R.layout.dialogue_remove_layout,null);
                final int pos = i;
                TextView textView = view1.findViewById(R.id.title);
                textView.setText("Remove note?");
                builder.setView(view1);
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        openHelper = ExpenseOpenHelper.getInstance(getApplicationContext());
                        SQLiteDatabase db = openHelper.getWritableDatabase();
                        db.delete(Contract.TODO_TABLE_NAME,Contract.TODO_ID + " = ? ",new String[]{list.get(pos).id + ""} );
                        list.remove(pos);
                        adapter.notifyDataSetChanged();
                    }
                });
                builder.setNegativeButton("Cancel",null);
                AlertDialog dialog = builder.create();
                dialog.show();
                return true;
            }
        });
    }

    private void fetchFromDatabase() {
        list.clear();
        openHelper = ExpenseOpenHelper.getInstance(getApplicationContext());
        SQLiteDatabase sqLiteDatabase = openHelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query(Contract.TODO_TABLE_NAME,null,null,null,null,null,null);

        while (cursor.moveToNext()){
            String title = cursor.getString(cursor.getColumnIndex(Contract.TODO_TITLE));
            String note = cursor.getString(cursor.getColumnIndex(Contract.TODO_CONTENT));
            long date = cursor.getLong(cursor.getColumnIndex(Contract.TODO_DATE));
            int hr = cursor.getInt(cursor.getColumnIndex(Contract.HOUR));
            int min = cursor.getInt(cursor.getColumnIndex(Contract.MINUTE));
            long id = cursor.getLong(cursor.getColumnIndex(Contract.TODO_ID));
            Todo todo = new Todo(id, title,note,date,hr,min);
            list.add(todo);
        }
        cursor.close();
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onStart() {
        super.onStart();
        fetchFromDatabase();
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter(MY_ACTION);
        broadcastManager.registerReceiver(broadcastReceiver,intentFilter);
    }

    @Override
    protected void onPause() {

        broadcastManager.unregisterReceiver(broadcastReceiver);
        super.onPause();
    }

    public void sendBroadcast(){
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        Intent alarmIntent = new Intent(this,AlarmReceiver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,1,alarmIntent,0);

        alarmManager.set(AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + 10*1000,
                pendingIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return  true;
    }

    int hh,mm;

    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if (id == R.id.add){

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View view = getLayoutInflater().inflate(R.layout.dialogue_layout,null);


            TextView dialogue_textview = (TextView)view.findViewById(R.id.dialogue_textview);
            final EditText title = (EditText)view.findViewById(R.id.title);
            final EditText note = (EditText)view.findViewById(R.id.note);
            final long[] epoch = new long[1];
            final Button button = view.findViewById(R.id.date);
            Button button1 = view.findViewById(R.id.time);
            Calendar c = Calendar.getInstance();
            final int mYear = c.get(Calendar.YEAR);
            final int mMonth = c.get(Calendar.MONTH);
            final int mDay = c.get(Calendar.DAY_OF_MONTH);
            final int mhour = c.get(Calendar.HOUR_OF_DAY);
            final int min = c.get(Calendar.MINUTE);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                            Calendar calendar = Calendar.getInstance();
                            calendar.set(i,i1,i2,0,0,0);
                            epoch[0] = calendar.getTimeInMillis();
                        }
                    },mYear,mMonth,mDay);
                    datePickerDialog.show();
                }
            });
            //final DatePicker datePicker = view.findViewById(R.id.datePicker);

            button1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TimePickerDialog timePickerDialog = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener(){
                        @Override
                        public void onTimeSet(TimePicker timePicker, int i, int i1) {
                            Calendar calendar = Calendar.getInstance();
                            calendar.set(Calendar.HOUR_OF_DAY,i);
                            calendar.set(Calendar.MINUTE,i1);
//                            timePicker.setHour(i);
//                            timePicker.setMinute(i1);
                            hh = calendar.get(Calendar.HOUR_OF_DAY);
                            mm = calendar.get(Calendar.MINUTE);
                        }
                    },mhour,min,false);
                    timePickerDialog.show();
                }
            });

            dialogue_textview.setText("Add a new note?");
            builder.setView(view);
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    SQLiteDatabase sqLiteDatabase = openHelper.getWritableDatabase();

                    ContentValues contentValues = new ContentValues();
                    contentValues.put(Contract.TODO_TITLE,title.getEditableText().toString());
                    contentValues.put(Contract.TODO_CONTENT,note.getEditableText().toString());
                    contentValues.put(Contract.TODO_DATE,epoch[0]);
                    contentValues.put(Contract.HOUR,mhour);
                    contentValues.put(Contract.MINUTE,min);
                    long id = sqLiteDatabase.insert(Contract.TODO_TABLE_NAME,null,contentValues);

                    Todo todo = new Todo(id, title.getEditableText().toString(),note.getEditableText().toString(),epoch[0],hh,mm);
                    list.add(todo);
                    adapter.notifyDataSetChanged();
                    sendBroadcast();
                }
            });
            builder.setNegativeButton("Cancel",null);
            AlertDialog dialog = builder.create();
            dialog.show();
        }
        else if (id == R.id.about ){
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            String url = "https://google.co.in";
            intent.setData(Uri.parse(url));
            Intent chooser = Intent.createChooser(intent,"Open with");
            startActivity(chooser);
        }
        else if (id == R.id.feedback){
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:simra.afreen@yahoo.com"));
            intent.putExtra(Intent.EXTRA_SUBJECT,"Hello");
            startActivity(intent);
        }
        else if (id == R.id.share){
            Intent share = new Intent();
            share.setAction(Intent.ACTION_SEND);
            share.setType("text/plain");
            share.putExtra(Intent.EXTRA_TEXT,"Hey There!");
            startActivity(Intent.createChooser(share,"select:"));
        }
        else if (id == R.id.call) {
            Intent call = new Intent();
            int permission = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE);
            if (permission == 0) {
                call.setAction(Intent.ACTION_CALL);
                call.setData(Uri.parse("tel:111111"));
                startActivity(call);
            } else {
                call.setAction(Intent.ACTION_DIAL);
                call.setData(Uri.parse("tel:111"));
                startActivity(call);
            }
        }
        return super.onOptionsItemSelected(item);
    }

//    public void onActivityResult(int requestCode, int resultCode, Intent data){
//        if (requestCode == 1) {
//            if (resultCode == 10) {
//                String title = data.getStringExtra("title");
//                String content = data.getStringExtra("content");
//                int pos = data.getIntExtra("position",0);
//                Todo todo = list.get(pos);
//                todo.setTitle(title);
//                todo.setNote(content);
//                sendBroadcast(data);
//            }
//        }
//    }
}
