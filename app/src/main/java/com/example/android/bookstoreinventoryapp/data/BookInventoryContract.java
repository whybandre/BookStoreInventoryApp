package com.example.android.bookstoreinventoryapp.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;
/**
 * API Contract for the Book Inventory app.
 */
public class BookInventoryContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public BookInventoryContract() {
    }

    /**
     * The "Content authority" is a name for the entire content provider, similar to the
     * relationship between a domain name and its website.  A convenient string to use for the
     * content authority is the package name for the app, which is guaranteed to be unique on the
     * device.
     */
    public static final String CONTENT_AUTHORITY = "com.example.android.bookstoreinventoryapp";

    /**
     * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
     * the content provider.
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /**
     * Possible path (appended to base content URI for possible URI's)
     * For instance, content://com.example.android.bookstoreinventoryapp/bookstoreinventoryapp/ is a valid path for
     * looking at book data. content://com.example.android.bookstoreinventoryapp/staff/ will fail,
     * as the ContentProvider hasn't been given any information on what to do with "staff".
     */
    public static final String PATH_BOOKS = "books";

    /**
     * Inner class that defines constant values for the books database table.
     * Each entry in the table represents a single book.
     */
    public final static class InventoryEntry implements BaseColumns {

        /**
         * The content URI to access the book data in the provider
         */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_BOOKS);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of books.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKS;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single book.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKS;

        /**
         * Name of database table for books
         */
        public final static String TABLE_NAME = "books";

        /**
         * Unique ID number for the book (only for use in the database table).
         * Type: INTEGER
         */
        public final static String _ID = BaseColumns._ID;

        /**
         * Name of the Book.
         * Type: TEXT
         */
        public final static String COLUMN_BOOK_NAME = "book_name";

        /**
         * Price of the book.
         * Type: TEXT
         */
        public final static String COLUMN_BOOK_PRICE = "price";

        /**
         * Quantity of books in inventory.
         * Type: TEXT
         */
        public final static String COLUMN_BOOK_QUANTITY = "quantity";

        /**
         * Name of the book supplier.
         * Type: TEXT
         */
        public final static String COLUMN_BOOK_SUPPLIER_NAME = "supplier_name";

        /**
         * Supplier phone number
         * Type: TEXT
         */
        public final static String COLUMN_BOOK_SUPPLIER_PHONE_NUMBER = "supplier_phone_number";

        /**
         * Supplier of the book.
         *
         * The only possible values are {@link #SUPPLIER_UNKNOWN}, {@link #SUPPLIER_PENGUIN},
         * {@link #SUPPLIER_PEARSON} or {@link #SUPPLIER_LEYA}.
         * Type: INTEGER
         */
        public final static int SUPPLIER_UNKNOWN = 0;
        public final static int SUPPLIER_PENGUIN  = 1;
        public final static int SUPPLIER_PEARSON = 2;
        public final static int SUPPLIER_LEYA = 3;

        /**
         * Returns whether or not the given gender is {@link #SUPPLIER_UNKNOWN}, {@link #SUPPLIER_PENGUIN},
         * {@link #SUPPLIER_PEARSON} or {@link #SUPPLIER_LEYA}.
         */
        public static boolean isValidSupplier(int supplier) {
            return supplier == SUPPLIER_UNKNOWN || supplier == SUPPLIER_PENGUIN || supplier == SUPPLIER_PEARSON
                    || supplier == SUPPLIER_LEYA;
        }
    }
    }