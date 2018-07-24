package com.example.android.bookstoreinventoryapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.android.bookstoreinventoryapp.data.BookInventoryContract.InventoryEntry;

/**
 * Database helper for Books app. Manages database creation and version management.
 */
public class BookInventoryDbHelper extends SQLiteOpenHelper {


    //get classname to log
    public static final String LOG_TAG = BookInventoryDbHelper.class.getSimpleName();

    /** Name of the database file */
    private static final String DATABASE_NAME = "book_inventory.db";

    /**
     * Database version. If you change the database schema, you must increment the database version.
     */
    private static final int DATABASE_VERSION = 1;

    /**
     * Constructs a new instance of {@link BookInventoryDbHelper}.
     *
     * @param context of the app
     */
    public BookInventoryDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * This is called when the database is created for the first time.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a String that contains the SQL statement to create the books table
        String SQL_CREATE_BOOK_TABLE = "CREATE TABLE " + InventoryEntry.TABLE_NAME + " ("
                + InventoryEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + InventoryEntry.COLUMN_BOOK_NAME + " TEXT NOT NULL, "
                + InventoryEntry.COLUMN_BOOK_PRICE + " INTEGER NOT NULL, "
                + InventoryEntry.COLUMN_BOOK_QUANTITY + " INTEGER NOT NULL, "
                + InventoryEntry.COLUMN_BOOK_SUPPLIER_NAME + " INTEGER NOT NULL DEFAULT 0, "
                + InventoryEntry.COLUMN_BOOK_SUPPLIER_PHONE_NUMBER + " INTEGER );";

        // Execute the SQL statement
        db.execSQL(SQL_CREATE_BOOK_TABLE);

        //Testing insert/read methods with log calls
        Log.d("Mission accomplished" , "Database has now a table");
    }

    /**
     * This is called when the database needs to be upgraded.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // The database is still at version 1, so there's nothing to do be done here.
    }
}