package com.example.pets.data;


import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.example.pets.CatalogActivity;

import java.util.PrimitiveIterator;

/**
 * {@link ContentProvider} for Pets app.
 */
public class PetProvider extends ContentProvider {

    private static final int PETS = 100;
    private static final int PETS_ID = 101;
    private static final String LOG_TAG = PetProvider.class.getSimpleName();
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {

        sUriMatcher.addURI(PetContract.CONTENT_AUTHORITY, PetContract.PATH_PETS, PETS);
        sUriMatcher.addURI(PetContract.CONTENT_AUTHORITY, PetContract.PATH_PETS + "/#", PETS_ID);

    }

    private PetDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new PetDbHelper(getContext());
        return true;

    }

    /**
     * Perform the query for the given URI. Use the given projection, selection, selection arguments, and sort order.
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {

        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        Cursor cursor;
       final int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                cursor = database.query(PetContract.PetEntry.TABLE_NAME,
                        projection,
                        null,
                        null,
                        null,
                        null,
                        null
                );
                break;
            case PETS_ID:
                selection = PetContract.PetEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(PetContract.PetEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;

            default:
                throw new IllegalArgumentException("Cannot query unknown URI" + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(),uri);

        return cursor;
    }

    /**
     * Insert new data into the provider with the given ContentValues.
     */
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {

       final int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                return insertPets(uri, contentValues);
            default:
                throw new IllegalArgumentException("Cannot query unknown URI" + uri);
        }


    }

    private Uri insertPets(Uri uri, ContentValues Values) {
        String name = Values.getAsString(PetContract.PetEntry.COLUMN_PET_NAME);
        if ( name == null || TextUtils.isEmpty(name)) {
            throw new IllegalArgumentException("Pet requires a name..");
        }

        Integer gender = Values.getAsInteger(PetContract.PetEntry.COLUMN_PET_GENDER);
        if (gender == null || !PetContract.PetEntry.isValidGender(gender)) {
            throw new IllegalArgumentException("Requires valid gender..");
        }

        Integer weight = Values.getAsInteger(PetContract.PetEntry.COLUMN_PET_WEIGHT);
        if(weight ==null || weight < 0){
            throw new IllegalArgumentException("Required valid weight..");
        }
        //bread can be null because in petContract class bread is not NotNull..

        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        long id = database.insert(PetContract.PetEntry.TABLE_NAME, null,Values);

        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert new row..");
            return null;
        }

        getContext().getContentResolver().notifyChange(uri,null);
        return ContentUris.withAppendedId(uri, id);

    }

    /**
     * Updates the data at the given selection and selection arguments, with the new ContentValues.
     */
    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                return updatePet(uri, contentValues, selection, selectionArgs);

            case PETS_ID:
                // For the PET_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = PetContract.PetEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updatePet(uri, contentValues, selection, selectionArgs);

            default:
                throw new IllegalArgumentException("update is not Supported for this" + uri);
        }
    }
    private int updatePet(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // If the {@link PetEntry#COLUMN_PET_NAME} key is present,
        // check that the name value is not null.
        if (values.containsKey(PetContract.PetEntry.COLUMN_PET_NAME)) {
            String name = values.getAsString(PetContract.PetEntry.COLUMN_PET_NAME);
            if (name == null) {
                throw new IllegalArgumentException("pet requires name");
            }

        }
        // If the {@link PetEntry#COLUMN_PET_GENDER} key is present,
        // check that the gender value is valid.
        if (values.containsKey(PetContract.PetEntry.COLUMN_PET_GENDER)) {
            Integer gender = values.getAsInteger(PetContract.PetEntry.COLUMN_PET_GENDER);
            if (gender == null || !PetContract.PetEntry.isValidGender(gender)) {
                throw new IllegalArgumentException(" Valid gender required");
            }
        }
        // If the {@link PetEntry#COLUMN_PET_WEIGHT} key is present,
        // check that the weight value is valid.
        if (values.containsKey(PetContract.PetEntry.COLUMN_PET_WEIGHT)) {
            Integer weight = values.getAsInteger(PetContract.PetEntry.COLUMN_PET_WEIGHT);
            if (weight != null && weight < 0) {
                throw new IllegalArgumentException("Pet requires valid weight");
            }
        }
        if(values.size()==0)
        {
            return 0;
        }

        // No need to check the breed, any value is valid (including null).
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowUpdated = database.update(PetContract.PetEntry.TABLE_NAME,values,selection,selectionArgs);
        if(rowUpdated!=0){
            getContext().getContentResolver().notifyChange(uri,null);
        }
        return rowUpdated;
        // Returns the number of database rows affected by the update statement
//        return database.update(PetContract.PetEntry.TABLE_NAME,values,selection,selectionArgs);
    }
    /**
     * Delete the data at the given selection and selection arguments.
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
      final int match= sUriMatcher.match(uri);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
    int rowsDeleted;
        switch (match) {
            case PETS:
                rowsDeleted = db.delete(PetContract.PetEntry.TABLE_NAME,selection,selectionArgs);
                break;
//                return db.delete(PetContract.PetEntry.TABLE_NAME,selection,selectionArgs);

            case PETS_ID:
                // For the PET_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = PetContract.PetEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = db.delete(PetContract.PetEntry.TABLE_NAME,selection,selectionArgs);
//                return db.delete(PetContract.PetEntry.TABLE_NAME,selection,selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not Supported for this" + uri);
        }
        if(rowsDeleted!=0){
            getContext().getContentResolver().notifyChange(uri,null);
        }
      return rowsDeleted;
    }

    @Override
    public String getType(Uri uri) {
        final int match= sUriMatcher.match(uri);
        switch (match){
            case PETS:
                    return PetContract.PetEntry.CONTENT_LIST_TYPE;
            case PETS_ID:
                return PetContract.PetEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown Uri"+uri+"with match");

        }

    }
}