package com.example.android.bookstoreinventoryapp;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.android.bookstoreinventoryapp.data.BookInventoryContract.InventoryEntry;
import com.example.android.bookstoreinventoryapp.data.BookInventoryDbHelper;


public class EditorActivity extends AppCompatActivity {

    /** EditText field to enter the book name, price, quantity, supplier name and phone */
    private EditText mBookNameEditText;
    private EditText mBookPriceEditText;
    private EditText mBookQuantityEditText;
    private Spinner mBookSupplierNameSpinner;
    private EditText mBookSupplierPhoneNumberEditText;
    private int supplierUnknown = InventoryEntry.SUPPLIER_UNKNOWN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        mBookNameEditText = findViewById(R.id.book_name_edit_text);
        mBookPriceEditText = findViewById(R.id.book_price_edit_text);
        mBookQuantityEditText = findViewById(R.id.book_quantity_edit_text);
        mBookSupplierNameSpinner = findViewById(R.id.book_supplier_name_spinner);
        mBookSupplierPhoneNumberEditText = findViewById(R.id.book_supplier_phone_number_edit_text);
        setupSpinner();
    }

    private void setupSpinner() {

        ArrayAdapter bookSupplierNameSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_supplier_options, android.R.layout.simple_spinner_item);

        bookSupplierNameSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        mBookSupplierNameSpinner.setAdapter(bookSupplierNameSpinnerAdapter);

        mBookSupplierNameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.supplier_penguin))) {
                        supplierUnknown = InventoryEntry.SUPPLIER_PENGUIN;
                    } else if (selection.equals(getString(R.string.supplier_pearson))) {
                        supplierUnknown = InventoryEntry.SUPPLIER_PEARSON;
                    } else if (selection.equals(getString(R.string.supplier_leya))) {
                        supplierUnknown = InventoryEntry.SUPPLIER_LEYA;
                    } else {
                        supplierUnknown = InventoryEntry.SUPPLIER_UNKNOWN;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                supplierUnknown = InventoryEntry.SUPPLIER_UNKNOWN;
            }
        });
    }
    //Using trim() eliminates any leading or trailing white space from the string we got. Bruno Andre -> Bruno
    private void insertBook() {
        // Read from input fields
        // Use trim to eliminate leading or trailing white space
        String bookNameString = mBookNameEditText.getText().toString().trim();

        String bookPriceString = mBookPriceEditText.getText().toString().trim();

        //Convert a String into an integer eg  Integer.parseInt(bookQuantityString)
        int bookPriceInteger = Integer.parseInt(bookPriceString);

        String bookQuantityString = mBookQuantityEditText.getText().toString().trim();
        int bookQuantityInteger = Integer.parseInt(bookQuantityString);

        String bookSupplierPhoneNumberString = mBookSupplierPhoneNumberEditText.getText().toString().trim();
        int bookSupplierPhoneNumberInteger = Integer.parseInt(bookSupplierPhoneNumberString);

        // Create database helper
        BookInventoryDbHelper mDbHelper = new BookInventoryDbHelper(this);

        // Gets the database in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();


        // Create a ContentValues object where column names are the keys,
        // and books attributes from the editor are the values.
        ContentValues values = new ContentValues();
        values.put(InventoryEntry.COLUMN_BOOK_NAME, bookNameString);
        values.put(InventoryEntry.COLUMN_BOOK_PRICE, bookPriceInteger);
        values.put(InventoryEntry.COLUMN_BOOK_QUANTITY, bookQuantityInteger);
        values.put(InventoryEntry.COLUMN_BOOK_SUPPLIER_NAME, supplierUnknown);
        values.put(InventoryEntry.COLUMN_BOOK_SUPPLIER_PHONE_NUMBER, bookSupplierPhoneNumberInteger);

        long newRowId = db.insert(InventoryEntry.TABLE_NAME, null, values);

        if (newRowId == -1) {
            Toast.makeText(this, "Error with saving book", Toast.LENGTH_SHORT).show();
            Log.d("Error Message:", "Doesn't insert row on table");

        } else {
            Toast.makeText(this, "Book saved with row id: " + newRowId, Toast.LENGTH_SHORT).show();
            Log.d("Row Message:", "New row on table inserted");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        Log.d("Menu Message:", "menu_editor open from EditorActivity");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Save book to database
                insertBook();
                // Exit activity
                finish();
            case android.R.id.home:
                // Navigate back to parent activity (BookInventoryActivity)
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}