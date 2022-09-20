package com.example.pets;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.PerformanceHintManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pets.data.PetContract;
import com.example.pets.data.PetDbHelper;
import com.example.pets.data.PetProvider;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.example.pets.data.PetContract.PetEntry;

public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private PetDbHelper mDbHelper;
    private static final int PET_LOADER = 0;
    PetCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        ListView petListView = findViewById(R.id.catalog_list);

        View emptyView = findViewById(R.id.empty_view);
        petListView.setEmptyView(emptyView);//Sets the view to show if the adapter is empty..

        mCursorAdapter = new PetCursorAdapter(this, null);
        petListView.setAdapter(mCursorAdapter);

        petListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);

                Uri currentPetUri = ContentUris.withAppendedId(PetEntry.CONTENT_URI, id);

                intent.setData(currentPetUri);
                startActivity(intent);
            }
        });

        getLoaderManager().initLoader(PET_LOADER, null, this);


        //displayDatabaseInfo();
    }
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//        displayDatabaseInfo();
//    }
//
//    private void displayDatabaseInfo() {
//        // To access our database, we instantiate our subclass of SQLiteOpenHelper
//        // and pass the context, which is the current activity.
//        PetDbHelper mDbHelper = new PetDbHelper(this);
//        PetProvider petProvider = new PetProvider();
//
//        // Create and/or open a database to read from it
//        SQLiteDatabase db = mDbHelper.getReadableDatabase(); // .open shelter.db
//
//        // Perform this raw SQL query "SELECT * FROM pets"
    // to get a Cursor that contains all rows from the pets table.
    //Cursor cursor = db.rawQuery("SELECT * FROM " + PetContract.PetEntry.TABLE_NAME, null);

//        Cursor c = db.query(PetEntry.TABLE_NAME,
//               projection,
//                null,
//                null,
//               null,
//                null,
//            null
//        );


    //  try {
    // Display the number of rows in the Cursor (which reflects the number of rows in the
    // pets table in the database).

//            TextView displayView = (TextView) findViewById(R.id.text_view_pet);
//            displayView.setText("Number of rows in pets database table: " + cursor.getCount()+"\n"+"\n");
//
//            for (int i = 0; i <= 4; i++) {
//                displayView.append(c.getColumnName(i));
//               displayView.append("  ");
//            }
//            displayView.append("\n"+"\n");
//            headers.append(c.getColumnName(0)); headers.append("  ");
//            headers.append(c.getColumnName(1)); headers.append("  ");
//            headers.append(c.getColumnName(2)); headers.append("  ");
//            headers.append(c.getColumnName(3)); headers.append("  ");
//            headers.append(c.getColumnName(4));


//            while (c.moveToNext()) {
//                displayView.append("\n");
//
//                for (int i = 0; i <= 4; i++) {
//                    displayView.append(c.getString(i));
//                    if(i==4) break;
//                    displayView.append(" - ");
//                }
//                mTextData.append(c.getString(0));
//                mTextData.append(" - ");
//                mTextData.append(c.getString(1));
//                mTextData.append(" - ");
//                mTextData.append(c.getString(2));
//                mTextData.append(" - ");
//                mTextData.append(c.getString(3));
//                mTextData.append(" - ");
//                mTextData.append(c.getString(4));


//            }
//
//
//        } finally {
//            // Always close the cursor when you're done reading from it. This releases all its
//            // resources and makes it invalid.
//            cursor.close();
//            c.close();
//        }

//        String[] projection = {
//                PetEntry._ID,
//                PetEntry.COLUMN_PET_NAME,
//                PetEntry.COLUMN_PET_BREED,
//                PetEntry.COLUMN_PET_GENDER,
//                PetEntry.COLUMN_PET_WEIGHT
//        };
//
//        Cursor c = getContentResolver().query(PetEntry.CONTENT_URI, projection, null, null, null);
//
//        ListView petListView = (ListView) findViewById(R.id.catalog_list);
//
//        PetCursorAdapter todoAdapter = new PetCursorAdapter(this, c);
//
//        petListView.setAdapter(todoAdapter);
//
//        todoAdapter.changeCursor(c);
//    }

    private void insertPet() {

//     SQLiteDatabase db = mDbHelper.getWritableDatabase(); iss line lo matt linkhan nhi too nullPoint exception aa ajayegi....
        ContentValues values = new ContentValues();
        values.put(PetEntry.COLUMN_PET_NAME, "TOTO");
        values.put(PetEntry.COLUMN_PET_BREED, "Terrier");
        values.put(PetEntry.COLUMN_PET_GENDER, PetEntry.GENDER_MALE);
        values.put(PetEntry.COLUMN_PET_WEIGHT, 7);

        Uri newRowId = getContentResolver().insert(PetEntry.CONTENT_URI, values);


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
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertPet();
//                displayDatabaseInfo();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                showDeleteConformationDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                PetEntry._ID,
                PetEntry.COLUMN_PET_NAME,
                PetEntry.COLUMN_PET_BREED,
        };
        return new CursorLoader(
                this,
                PetEntry.CONTENT_URI,
                projection,
                null,
                null,
                null
        );

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }


    private void showDeleteConformationDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.do_you_want_to_delete);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteAllPets();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null) {
                    dialog.dismiss();
                }

            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteAllPets() {

            int rowsDeleted = getContentResolver().delete(PetEntry.CONTENT_URI, null, null);
            Log.v("Catalog Activity", rowsDeleted+"row deleted from pet database");

    }
}