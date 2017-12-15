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

import android.app.LoaderManager.LoaderCallbacks;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;


import com.example.android.pets.data.PetContract;
import com.example.android.pets.data.PetDbHelper;


/**
 * Displays list of pets that were entered and stored in the app.
 */
public class CatalogActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {


    private static final String LOG_TAG = CatalogActivity.class.getSimpleName();
    private static final int LoaderId = 1;
    private PetDbHelper mDbHelper;
    private PetCursorAdapter petCursorAdapter;



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

        // Find ListView that will show pet data
        ListView petListView = (ListView) findViewById(R.id.list_view_pet);

        // Find and set empty view on list view so that it shows when list has 0 items
        View emptyView = findViewById(R.id.empty_view);
        petListView.setEmptyView(emptyView);

        // Assign cursor adapter to list view
        petCursorAdapter = new PetCursorAdapter(this, null);
        petListView.setAdapter(petCursorAdapter);




        // Prepare the loader.  Either re-connect with an existing one or start a new one.
        getLoaderManager().initLoader(LoaderId, null, this);

    }

    /**
     * runs every time the activity starts again
     */
    @Override
    protected void onStart() {

        super.onStart();

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
                return true;

            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:

                // Clear all pet data
                clearPetData();
                return true;
        }

        return super.onOptionsItemSelected(item);

    }


    /**
     * Adds a default pet to shelter.db
     */
    private void insertDefaultPet() {

        Log.v(LOG_TAG, "Adding default pet to db");

        // Create a ContentValues object where column names are the keys,
        // and Toto's pet attributes are the values.
        ContentValues values = new ContentValues();
        values.put(PetContract.PetEntry.COLUMN_PET_NAME, "Toto");
        values.put(PetContract.PetEntry.COLUMN_PET_BREED, "Terrier");
        values.put(PetContract.PetEntry.COLUMN_PET_GENDER, PetContract.PetEntry.GENDER_MALE);
        values.put(PetContract.PetEntry.COLUMN_PET_WEIGHT, 7);

        // Insert a new row for Toto into the provider using the ContentResolver.
        // Use the {@link PetEntry#CONTENT_URI} to indicate that we want to insert
        // into the pets database table.
        // Receive the new content URI that will allow us to access Toto's data in the future.
        Uri newUri = getContentResolver().insert(PetContract.PetEntry.CONTENT_URI, values);

        if (newUri != null) Log.v(LOG_TAG, "Pet added successfully");

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


    // Called when a new Loader needs to be created
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        String[] projection = new String[] {
                PetContract.PetEntry._ID,
                PetContract.PetEntry.COLUMN_PET_NAME,
                PetContract.PetEntry.COLUMN_PET_BREED
        };

        // Now create and return a CursorLoader that will take care of
        // creating a Cursor for the data being displayed.
        return new CursorLoader(
                this,
                PetContract.PetEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);

    }


    // Called when a previously created loader has finished loading
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        // Swap the new cursor in.  (The framework will take care of closing the
        // old cursor once we return.)
        petCursorAdapter.swapCursor(cursor);

    }

    // Called when a previously created loader is reset, making the data unavailable
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        petCursorAdapter.swapCursor(null);

    }

}
