package com.example.android.jotitdown2;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Simra Afreen on 25-09-2017.
 */

public class ExpenseOpenHelper extends SQLiteOpenHelper {

    private static ExpenseOpenHelper instance;

    public static ExpenseOpenHelper getInstance(Context context) {
        if(instance == null){
            instance = new ExpenseOpenHelper(context);
        }
        return instance;
    }

    private ExpenseOpenHelper(Context context) {
        super(context, "expense_db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String query = "CREATE TABLE " + Contract.TODO_TABLE_NAME + " ( " +
                Contract.TODO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                Contract.TODO_TITLE + " TEXT, " +
                Contract.TODO_DATE + " INTEGER, " +
                Contract.TODO_CONTENT + " TEXT)";
        sqLiteDatabase.execSQL(query);
    }
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    }
}
