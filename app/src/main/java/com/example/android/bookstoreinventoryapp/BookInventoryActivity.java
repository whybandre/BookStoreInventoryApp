package com.example.android.bookstoreinventoryapp;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import android.widget.TextView;


import com.example.android.bookstoreinventoryapp.data.BookInventoryContract.InventoryEntry;
import com.example.android.bookstoreinventoryapp.data.BookInventoryDbHelper;

public class BookInventoryActivity extends AppCompatActivity {

    /** Database helper that will provide us access to the database */
    private BookInventoryDbHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_inventory);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BookInventoryActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });
        // To access our database, we instantiate our subclass of SQLiteOpenHelper
        // and pass the context, which is the current activity.
        mDbHelper = new BookInventoryDbHelper(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        displayDatabaseInfo();
    }

    /**
     * Temporary helper method to display information in the onscreen TextView about the state of
     * the pets database.
     */
    private void displayDatabaseInfo() {
        // Create and/or open a database to read from it
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                InventoryEntry._ID,
                InventoryEntry.COLUMN_BOOK_NAME,
                InventoryEntry.COLUMN_BOOK_PRICE,
                InventoryEntry.COLUMN_BOOK_QUANTITY,
                InventoryEntry.COLUMN_BOOK_SUPPLIER_NAME,
                InventoryEntry.COLUMN_BOOK_SUPPLIER_PHONE_NUMBER
        };

        Cursor cursor = db.query(
                InventoryEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
        );

        TextView displayView = findViewById(R.id.text_view_book);

        try {
            // Create a header in the Text View that looks like this:
            //
            // The books table contains <number of rows in Cursor> pets.
            // All separated by a paragraph _id - name - price - quantity - supplier name - supplier phone number
            //
            // In the while loop below, iterate through the rows of the cursor and display
            // the information from each column in this order.
            displayView.setText("Their are " + cursor.getCount() + " books in inventory.\n\n");

            displayView.append("The "+ InventoryEntry.TABLE_NAME + " table columns are:\n\n");
            displayView.append(InventoryEntry._ID + " \n " +
                    InventoryEntry.COLUMN_BOOK_NAME + " \n " +
                    InventoryEntry.COLUMN_BOOK_PRICE + " \n " +
                    InventoryEntry.COLUMN_BOOK_QUANTITY + " \n " +
                    InventoryEntry.COLUMN_BOOK_SUPPLIER_NAME + " \n " +
                    InventoryEntry.COLUMN_BOOK_SUPPLIER_PHONE_NUMBER + "\n");

            // Figure out the index of each column
            int idColumnIndex = cursor.getColumnIndex(InventoryEntry._ID);
            int nameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_BOOK_NAME);
            int priceColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_BOOK_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_BOOK_QUANTITY);
            int supplierNameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_BOOK_SUPPLIER_NAME);
            int supplierPhoneColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_BOOK_SUPPLIER_PHONE_NUMBER);

            // Iterate through all the returned rows in the cursor
            while (cursor.moveToNext()) {
                // Use that index to extract the String or Int value of the word
                // at the current row the cursor is on.
                int currentID = cursor.getInt(idColumnIndex);
                String currentName = cursor.getString(nameColumnIndex);
                float currentPrice = cursor.getFloat(priceColumnIndex);
                int currentQuantity = cursor.getInt(quantityColumnIndex);
                String currentNameSupplier = cursor.getString(supplierNameColumnIndex);
                String currentPhoneSupplier = cursor.getString(supplierPhoneColumnIndex);

                // Display the values from each column of the current row in the cursor in the TextView
                displayView.append(("\n" +
                        currentID + " \n " +
                        currentName + " \n " +
                        currentPrice + " \n " +
                        currentQuantity + " \n " +
                        currentNameSupplier + " \n " +
                        currentPhoneSupplier + " \n "));
            }
        } finally {
            // Always close the cursor when you're done reading from it. This releases all its
            // resources and makes it invalid.
            cursor.close();
        }
    }
}















