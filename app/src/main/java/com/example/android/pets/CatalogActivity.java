/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.pets;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.android.pets.data.PetContract;
import com.example.android.pets.data.PetDbHelper;


/**
 * Displays list of pets that were entered and stored in the app.
 */
public class CatalogActivity extends AppCompatActivity {


    private static final String LOG_TAG = CatalogActivity.class.getSimpleName();
    private PetDbHelper mDbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        // To access our database, we instantiate our subclass of SQLiteOpenHelper
        // and pass the context, which is the current activity
        mDbHelper = new PetDbHelper(this);
    }

    /**
     * runs every time the activity starts again
     */
    @Override
    protected void onStart() {
        super.onStart();
        // Display database info
        displayDatabaseInfo();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        // 3 button menu in the corner
        switch (item.getItemId()) {

            // Respond to a click on the "Insert dummy com.example.android.pets.data" menu option
            case R.id.action_insert_dummy_data:

                // Insert default pet
                insertDefaultPet();
                displayDatabaseInfo();
                return true;

            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:

                // Clear all pet data
                clearPetData();
                displayDatabaseInfo();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * Temporary helper method to display information in the onscreen TextView about the state of
     * the pets database.
     */
    private void displayDatabaseInfo() {

        // Create and/or open a database to read from it
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        /*
            Old Code
            Perform this raw SQL query "SELECT * FROM pets"
            to get a Cursor that contains all rows from the pets table.
            Cursor cursor = db.rawQuery("SELECT * FROM " + PetContract.PetEntry.TABLE_NAME, null);
         */

        // Define a projection that specifies which columns from the db you will use after this query
        String[] projection = {
                PetContract.PetEntry._ID,
                PetContract.PetEntry.COLUMN_PET_NAME,
                PetContract.PetEntry.COLUMN_PET_BREED,
                PetContract.PetEntry.COLUMN_PET_GENDER,
                PetContract.PetEntry.COLUMN_PET_WEIGHT
        };

        // Define where part of query
        String selection = PetContract.PetEntry._ID + "=?";

        // Specify arguments in placeholder order
        String[] selectionArgs = {"1"};

        // Get cursor that contains all rows from the pet table
        Cursor cursor = db.query(
                PetContract.PetEntry.TABLE_NAME,    // table to query
                projection,                         // projection: columns to return
                null,                          // selection: columns for the WHERE clause
                null,                      // selection args: values for the WHERE clause
                null,                      // groupBy: group the rows
                null,                       // having: filter the rows
                null                       // orderBy: sort order
        );

        try {
            /*
                Old Code
                Display the number of rows in the Cursor (which reflects the number of rows in the
                pets table in the database).
                displayView.setText("Number of rows in pets database table: " + cursor.getCount());
             */
            TextView displayView = (TextView) findViewById(R.id.text_view_pet);

            // Clear the old text
            displayView.setText("");

            // Iterate through all the returned rows in the cursor
            while (cursor.moveToNext()) {
                // Use that index to extract the String or Int value
                // of the word at the current row the cursor is on
                int currentID = cursor.getInt(cursor.getColumnIndex(PetContract.PetEntry._ID));
                String currentName = cursor.getString(cursor.getColumnIndex(PetContract.PetEntry.COLUMN_PET_NAME));
                // Display the values from each column of the current row in the cursor in the TextView
                displayView.append("\n" + currentID + " - " + currentName);
            }

        } finally {
            // Always close the cursor when you're done reading from it. This releases all its
            // resources and makes it invalid.
            cursor.close();
        }
    }


    /**
     * Adds a default pet to shelter.db
     */
    private void insertDefaultPet() {

        Log.v(LOG_TAG, "Adding default pet to db");

        // create object with content values of our new pet
        ContentValues values = new ContentValues();
        values.put(PetContract.PetEntry.COLUMN_PET_NAME, "Garfield");
        values.put(PetContract.PetEntry.COLUMN_PET_BREED, "Tabby");
        values.put(PetContract.PetEntry.COLUMN_PET_GENDER, PetContract.PetEntry.GENDER_MALE);
        values.put(PetContract.PetEntry.COLUMN_PET_WEIGHT, 7);

        // use PetDbHelper to get writable db object
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // insert the content values we defined above
        long result = db.insert(PetContract.PetEntry.TABLE_NAME, null, values);
        if (result != -1) Log.v(LOG_TAG, "Pet added successfully on row #" + String.valueOf(result));

    }


    /**
     * Clears all rows from shelter.db
     */
    private void clearPetData() {

        Log.v(LOG_TAG, "Attempting to delete all pet data");

        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // delete all rows from the pets table
        int result = db.delete(PetContract.PetEntry.TABLE_NAME, null, null);
        // delete ROWID from SQLITE_SEQUENCE table so primary key id resets
        int result2 = db.delete("SQLITE_SEQUENCE", "name=?", new String[]{PetContract.PetEntry.TABLE_NAME});

        if (result > 0 && result2 > 0) {
            Log.v(LOG_TAG, "Deleted " + String.valueOf(result) + " rows from shelter.db");
        } else if (result == 0 && result2 == 0) {
            Log.e(LOG_TAG, "Error deleting rows: No rows left to delete");
        } else {
            Log.e(LOG_TAG, "Error deleting rows: Delete returned " + String.valueOf(result));
        }

    }


}
