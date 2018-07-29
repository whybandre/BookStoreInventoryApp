package com.example.android.bookstoreinventoryapp;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.android.bookstoreinventoryapp.data.BookInventoryContract.InventoryEntry;

import java.util.Locale;

/**
 * Allows user to create a new book or edit an existing one.
 */

public class EditorActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * Identifier for the book data loader
     */
    private static final int EXISTING_BOOK_LOADER = 0;

    /**
     * Content URI for the existing book (null if it's a new book)
     */
    private Uri mCurrentBookUri;

    /** EditText field to enter the book name, price, quantity, supplier name and phone */
    private EditText mBookNameEditText;
    private EditText mBookPriceEditText;
    private EditText mBookQuantityEditText;
    private Spinner mBookSupplierNameSpinner;
    private EditText mBookSupplierPhoneNumberEditText;

    /**
     * Supplier of the book. The possible valid values are in the BookInventoryContract.java file:
     * {@link InventoryEntry#SUPPLIER_UNKNOWN}, {@link InventoryEntry#SUPPLIER_PENGUIN},
     * {@link InventoryEntry#SUPPLIER_PEARSON} or {@link InventoryEntry#SUPPLIER_LEYA}
     */
    private int mSupplierUnknown = InventoryEntry.SUPPLIER_UNKNOWN;

    /**
     * Boolean flag that keeps track of whether the book has been edited (true) or not (false)
     */
    private boolean mBookHasChanged = false;

    /**
     * OnTouchListener that listens for any user touches on a View, implying that they are modifying
     * the view, and we change the mBookHasChanged boolean to true.
     */
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mBookHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // Examine the intent that was used to launch this activity,
        // in order to figure out if we're creating a new book or editing an existing one.
        Intent intent = getIntent();
        mCurrentBookUri = intent.getData();

        // If the intent DOES NOT contain the book content URI, then we know that we are
        // creating the new book.
        if (mCurrentBookUri == null) {
            // This is the new book, so change the app bar to say "Add a new Book"
            setTitle(getString(R.string.add_new_books));

            // Invalidate the options menu, so the "Delete" menu option can be hidden.
            // (It doesn't make sense to delete the book that hasn't been created yet.)
            invalidateOptionsMenu();
        } else {
            // Otherwise this is an existing product, so change app bar to say "Edit Book"
            setTitle(getString(R.string.edit_book));

            // Initialize a loader to read the data from the database and display the current values in the editor
            getLoaderManager().initLoader(EXISTING_BOOK_LOADER, null, this);
        }

        // Find all relevant views that we will need to read user input from
        mBookNameEditText = findViewById(R.id.book_name_edit_text);
        mBookPriceEditText = findViewById(R.id.book_price_edit_text);
        mBookQuantityEditText = findViewById(R.id.book_quantity_edit_text);
        mBookSupplierNameSpinner = findViewById(R.id.book_supplier_name_spinner);
        mBookSupplierPhoneNumberEditText = findViewById(R.id.book_supplier_phone_number_edit_text);
        Button mButtonIncrease = findViewById(R.id.increase_button);
        Button mButtonDecrease = findViewById(R.id.decrease_button);
        Button mButtonOrderMore = findViewById(R.id.order_button);

        // Setup OnTouchListeners on all the input fields, so we can determine if the user
        // has touched or modified them. This will let us know if there are unsaved changes
        // or not, if the user tries to leave the editor without saving.
        mBookNameEditText.setOnTouchListener(mTouchListener);
        mBookPriceEditText.setOnTouchListener(mTouchListener);
        mBookQuantityEditText.setOnTouchListener(mTouchListener);
        mBookSupplierNameSpinner.setOnTouchListener(mTouchListener);
        mBookSupplierPhoneNumberEditText.setOnTouchListener(mTouchListener);
        mButtonIncrease.setOnTouchListener(mTouchListener);
        mButtonDecrease.setOnTouchListener(mTouchListener);

        setupSpinner();

        mButtonIncrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String quantity = mBookQuantityEditText.getText().toString();
                if (TextUtils.isEmpty(quantity)) {
                    mBookQuantityEditText.setText("1");
                } else {
                    int not_null_quantity = Integer.parseInt(mBookQuantityEditText.getText().toString().trim());
                    not_null_quantity++;
                    mBookQuantityEditText.setText(String.valueOf(not_null_quantity));
                }
            }
        });

        mButtonDecrease.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String quantity = mBookQuantityEditText.getText().toString();
                if (TextUtils.isEmpty(quantity)) {
                    mBookQuantityEditText.setText("0");
                } else {
                    int new_quantity = Integer.parseInt(mBookQuantityEditText.getText().toString().trim());
                    if (new_quantity > 0) {
                        new_quantity--;
                        mBookQuantityEditText.setText(String.valueOf(new_quantity));
                    } else {
                        Toast.makeText(EditorActivity.this, "The quantity cannot be negative!",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        mButtonOrderMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = mBookSupplierPhoneNumberEditText.getText().toString().trim();
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + phone));
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });
    }

    /**
     * Setup the dropdown spinner that allows the user to select the supplier of the book.
     */
    private void setupSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter genderSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_supplier_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        genderSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mBookSupplierNameSpinner.setAdapter(genderSpinnerAdapter);

        // Set the integer mSelected to the constant values
        mBookSupplierNameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.supplier_penguin))) {
                        mSupplierUnknown = InventoryEntry.SUPPLIER_PENGUIN;
                    }
                    if (selection.equals(getString(R.string.supplier_pearson))) {
                        mSupplierUnknown = InventoryEntry.SUPPLIER_PEARSON;
                    }
                    if (selection.equals(getString(R.string.supplier_leya))) {
                        mSupplierUnknown = InventoryEntry.SUPPLIER_LEYA;
                    } else {
                        mSupplierUnknown = InventoryEntry.SUPPLIER_UNKNOWN;
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mSupplierUnknown = InventoryEntry.SUPPLIER_UNKNOWN;
            }
        });
    }

    /**
     * Get user input from EditText views and save Books details into database.
     */
    private void saveBook() {
        // Read from input fields
        // Use trim to eliminate leading or trailing white space
        String nameString = mBookNameEditText.getText().toString().trim();
        String priceString = mBookPriceEditText.getText().toString().trim();
        String quantityString = mBookQuantityEditText.getText().toString().trim();
        String supplierPhoneString = mBookSupplierPhoneNumberEditText.getText().toString().trim();

        // Check if this is supposed to be a new book
        // and check if all the fields in the editor are blank
        if (mCurrentBookUri == null &&
                TextUtils.isEmpty(nameString) && TextUtils.isEmpty(priceString) &&
                TextUtils.isEmpty(quantityString) && TextUtils.isEmpty(supplierPhoneString)
                && mSupplierUnknown == InventoryEntry.SUPPLIER_UNKNOWN) {
            // Since no fields were modified, we can return early without creating a new book.
            // No need to create ContentValues and no need to do any ContentProvider operations.
            Toast.makeText(this, R.string.no_changes_info, Toast.LENGTH_SHORT).show();
            // Exit activity
            finish();
            return;
        }

        if (TextUtils.isEmpty(nameString)) {
            mBookNameEditText.requestFocus();
            mBookNameEditText.setError(getString(R.string.empty_field_error));
            Toast.makeText(this, getString(R.string.add_book_name), Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(priceString)) {
            mBookPriceEditText.requestFocus();
            mBookPriceEditText.setError(getString(R.string.empty_field_error));
            Toast.makeText(this, getString(R.string.add_book_price), Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(quantityString)) {
            mBookQuantityEditText.requestFocus();
            mBookQuantityEditText.setError(getString(R.string.empty_field_error));
            Toast.makeText(this, getString(R.string.add_book_quantity), Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(supplierPhoneString)) {
            mBookSupplierPhoneNumberEditText.requestFocus();
            mBookSupplierPhoneNumberEditText.setError(getString(R.string.empty_field_error));
            Toast.makeText(this, getString(R.string.add_book_supplier_number), Toast.LENGTH_LONG).show();
            return;
        }

        // Create a ContentValues object where column names are the keys,
        // and books attributes from the editor are the values.
        ContentValues values = new ContentValues();
        values.put(InventoryEntry.COLUMN_BOOK_NAME, nameString);
        float priceFloat = Float.parseFloat(priceString);
        values.put(InventoryEntry.COLUMN_BOOK_PRICE, priceFloat);
        values.put(InventoryEntry.COLUMN_BOOK_QUANTITY, quantityString);
        values.put(InventoryEntry.COLUMN_BOOK_SUPPLIER_NAME, mSupplierUnknown);
        values.put(InventoryEntry.COLUMN_BOOK_SUPPLIER_PHONE_NUMBER, supplierPhoneString);

        // Determine if this is a new or existing book by checking if mCurrentBookUri is null or not
        if (mCurrentBookUri == null) {

            // This is a NEW book, so insert a new book into the provider,
            // returning the content URI for the new book.
            Uri newUri = getContentResolver().insert(InventoryEntry.CONTENT_URI, values);

            // Show a toast message depending on whether or not the insertion was successful.
            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, R.string.save_error_info,
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, R.string.book_added_info,
                        Toast.LENGTH_SHORT).show();
                // Exit activity
                finish();
            }
        } else {
            // Otherwise this is an existing book, so update the book with content URI: mCurrentBookUri
            // and pass in the new ContentValues. Pass in null for the selection and selection args
            // because mCurrentBookUri will already identify the correct row in the database that
            // we want to modify.
            int rowsAffected = getContentResolver().update(mCurrentBookUri, values, null, null);

            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, R.string.update_error_info,
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, R.string.updated_book_info,
                        Toast.LENGTH_SHORT).show();
            }
            // Exit activity
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new book, hide the "Delete" menu item.
        if (mCurrentBookUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Save book to database
                saveBook();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If the book hasn't changed, continue with navigating up to parent activity
                // which is the {@link BookInventoryActivity}.
                if (!mBookHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This method is called when the back button is pressed.
     */
    @Override
    public void onBackPressed() {
        // If the book hasn't changed, continue with handling back button press
        if (!mBookHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Since the editor shows all the books attributes, define a projection that contains
        // all columns from the books table
        String[] projection = {
                InventoryEntry._ID,
                InventoryEntry.COLUMN_BOOK_NAME,
                InventoryEntry.COLUMN_BOOK_PRICE,
                InventoryEntry.COLUMN_BOOK_QUANTITY,
                InventoryEntry.COLUMN_BOOK_SUPPLIER_NAME,
                InventoryEntry.COLUMN_BOOK_SUPPLIER_PHONE_NUMBER};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                mCurrentBookUri,        // Query the content URI for the current book
                projection,             // Columns to include in the resulting Cursor
                null,          // No selection clause
                null,       // No selection arguments
                null);         // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {
            // Find the columns of the book attributes that we're interested in
            int nameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_BOOK_NAME);
            int priceColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_BOOK_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_BOOK_QUANTITY);
            int supplier_nameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_BOOK_SUPPLIER_NAME);
            int supplier_phoneColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_BOOK_SUPPLIER_PHONE_NUMBER);

            // Extract out the value from the Cursor for the given column index
            String name = cursor.getString(nameColumnIndex);
            Float price = cursor.getFloat(priceColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            int supplier_name = cursor.getInt(supplier_nameColumnIndex);
            String phone = cursor.getString(supplier_phoneColumnIndex);

            // Update the views on the screen with the values from the database
            mBookNameEditText.setText(name);
            mBookPriceEditText.setText(String.format(Float.toString(price), Locale.getDefault()));
            mBookQuantityEditText.setText(String.format(Integer.toString(quantity), Locale.getDefault()));
            mBookSupplierPhoneNumberEditText.setText(phone);

            // Supplier is a dropdown spinner, so map the constant value from the database
            // into one of the dropdown options (0 is Unknown, 1 is SUPPLIER_PENGUIN, 2 is SUPPLIER_PEARSON,
            // 3 is SUPPLIER_LEYA).
            // Then call setSelection() so that option is displayed on screen as the current selection.
            switch (supplier_name) {
                case InventoryEntry.SUPPLIER_PENGUIN:
                    mBookSupplierNameSpinner.setSelection(1);
                    break;
                case InventoryEntry.SUPPLIER_PEARSON:
                    mBookSupplierNameSpinner.setSelection(2);
                    break;
                case InventoryEntry.SUPPLIER_LEYA:
                    mBookSupplierNameSpinner.setSelection(2);
                    break;
                default:
                    mBookSupplierNameSpinner.setSelection(0);
                    break;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        mBookNameEditText.setText(R.string.blank);
        mBookPriceEditText.setText(R.string.blank);
        mBookQuantityEditText.setText(R.string.blank);
        mBookSupplierNameSpinner.setSelection(0); // Select "Unknown" supplier
        mBookSupplierPhoneNumberEditText.setText(R.string.blank);
    }

    /**
     * Show a dialog that warns the user there are unsaved changes that will be lost
     * if they continue leaving the editor.     *
     *
     * @param discardButtonClickListener is the click listener for what to do when
     *                                   the user confirms they want to discard their changes
     */
    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.discard_changes_info);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the book.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the book.
                deleteBook();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the book.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Delete book from the database.
     */
    private void deleteBook() {
        // Only perform the delete if this is an existing book.
        if (mCurrentBookUri != null) {
            // Call the ContentResolver to delete the book at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentBookUri
            // content URI already identifies the book that we want.
            int rowsDeleted = getContentResolver().delete(mCurrentBookUri, null, null);
            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, R.string.delete_error_info,
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, R.string.book_deleted,
                        Toast.LENGTH_SHORT).show();
            }
            // Close the activity
            finish();
        }
    }
}