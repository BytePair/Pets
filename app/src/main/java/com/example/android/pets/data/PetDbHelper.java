package com.example.android.pets.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class PetDbHelper extends SQLiteOpenHelper {


    private static final String LOG_TAG = PetDbHelper.class.getSimpleName();


    /**
     * Name of the database file
     */
    private static final String DATABASE_NAME = "shelter.db";


    /**
     * Database version. If you change the database schema, you must increment the database version.
     */
    private static final int DATABASE_VERSION = 1;


    /**
     * Constructor
     **/
    public PetDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String SQL_CREATE_PETS_TABLE = "CREATE TABLE " + PetContract.PetEntry.TABLE_NAME
                                            + "("
                                            + PetContract.PetEntry._ID  + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                                            + PetContract.PetEntry.COLUMN_PET_NAME + " TEXT NOT NULL,"
                                            + PetContract.PetEntry.COLUMN_PET_BREED + " TEXT,"
                                            + PetContract.PetEntry.COLUMN_PET_GENDER + " INTEGER NOT NULL,"
                                            + PetContract.PetEntry.COLUMN_PET_WEIGHT + " INTEGER NOT NULL DEFAULT 0"
                                            + ");";
        sqLiteDatabase.execSQL(SQL_CREATE_PETS_TABLE);
        Log.v(LOG_TAG, "Finished creating table \"" + PetContract.PetEntry.TABLE_NAME
                     + "\" in db \"" + DATABASE_NAME + "\"");
    }


    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // TODO: finish me later when we move past version 1
    }

}
