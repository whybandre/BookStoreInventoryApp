package com.example.android.bookstoreinventoryapp.data;

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
     * Inner class that defines constant values for the books database table.
     * Each entry in the table represents a single book.
     */
    public final static class InventoryEntry implements BaseColumns {

        /** Name of database table for books */
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
    }
}