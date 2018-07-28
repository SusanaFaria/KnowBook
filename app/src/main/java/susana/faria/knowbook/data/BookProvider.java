package susana.faria.knowbook.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import susana.faria.knowbook.R;
import susana.faria.knowbook.data.BookContract.BookEntry;

public class BookProvider extends ContentProvider {

    public static final String LOG_TAG = BookProvider.class.getSimpleName();
    private static final int BOOKS = 10;
    private static final int BOOK_ID = 5;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(BookContract.CONTENT_AUTHORITY, BookContract.PATH_BOOKS, BOOKS);
        sUriMatcher.addURI(BookContract.CONTENT_AUTHORITY, BookContract.PATH_BOOKS + "/#", BOOK_ID);
    }

    private BookDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new BookDbHelper(getContext());

        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        Cursor cursor;
        int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:

                cursor = database.query(BookEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case BOOK_ID:
                selection = BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                cursor = database.query(BookEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return insertBook(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertBook(Uri uri, ContentValues values) {
        // Check that the values are not null
        String name = values.getAsString(BookEntry.COLUMN_BOOK_NAME);
        if ((TextUtils.isEmpty(name))) {
            Toast.makeText(getContext(), R.string.name_required, Toast.LENGTH_SHORT).show();
            return null;
        }
        String author = values.getAsString(BookEntry.COLUMN_BOOK_AUTHOR);
        if ((TextUtils.isEmpty(author))) {
            Toast.makeText(getContext(), R.string.author_required, Toast.LENGTH_SHORT).show();
            return null;
        }
       Integer genre = values.getAsInteger(BookEntry.COLUMN_BOOK_GENRE);
        if (genre == null) {
            Toast.makeText(getContext(), R.string.genre_required, Toast.LENGTH_SHORT).show();
            return null;
        }

        Double price = values.getAsDouble(BookEntry.COLUMN_BOOK_PRICE);
        if (price == null || price < 0) {
            Toast.makeText(getContext(), R.string.price_required, Toast.LENGTH_SHORT).show();
           return null;
        }

        Long quantity = values.getAsLong(BookEntry.COLUMN_BOOK_QUANTITY);
        if (quantity == null || quantity < 0) {
            Toast.makeText(getContext(), R.string.quantity_required, Toast.LENGTH_SHORT).show();
           return null;
        }
        String supplier = values.getAsString(BookEntry.COLUMN_SUPPLIER);
        if ((TextUtils.isEmpty(supplier))) {
            Toast.makeText(getContext(), R.string.supplier_required, Toast.LENGTH_SHORT).show();
           return null;
        }
        Long supplier_num = values.getAsLong(BookEntry.COLUMN_SUPPLIER_NUM);
        if ((supplier_num != null && supplier_num < 0)) {
            Toast.makeText(getContext(), R.string.supplier_num_required, Toast.LENGTH_SHORT).show();
           return null;
        }
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        // Insert the new book with the given values
        long id = database.insert(BookEntry.TABLE_NAME, null, values);
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }
        // Notify all listeners that the data has changed for the book content URI
        getContext().getContentResolver().notifyChange(uri, null);

        // Once we know the ID of the new row in the table,
        // return the new URI with the ID appended to the end of it
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return updateBook(uri, contentValues, selection, selectionArgs);
            case BOOK_ID:
                selection = BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateBook(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateBook(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        if (values.containsKey(BookEntry.COLUMN_BOOK_NAME)) {
            String name = values.getAsString(BookEntry.COLUMN_BOOK_NAME);
            if (TextUtils.isEmpty(name)) {
                Toast.makeText(getContext(), R.string.name_required, Toast.LENGTH_SHORT).show();
                return 0;
            }
        }

        if (values.containsKey(BookEntry.COLUMN_BOOK_AUTHOR)) {
            String author = values.getAsString(BookEntry.COLUMN_BOOK_AUTHOR);
            if (TextUtils.isEmpty(author)) {
                Toast.makeText(getContext(), R.string.author_required, Toast.LENGTH_SHORT).show();
                return 0;
            }
        }

        if(values.containsKey(BookEntry.COLUMN_BOOK_GENRE)) {
            Integer genre = values.getAsInteger(BookEntry.COLUMN_BOOK_GENRE);
            if(genre == null) {
                Toast.makeText(getContext(), R.string.genre_required, Toast.LENGTH_SHORT).show();
                return 0;
            }
        }

        if (values.containsKey(BookEntry.COLUMN_BOOK_PRICE)) {
            Double price = values.getAsDouble(BookEntry.COLUMN_BOOK_PRICE);
            if (price == null || price < 0) {
                Toast.makeText(getContext(), R.string.price_required, Toast.LENGTH_SHORT).show();
                return 0;
            }
        }
        if (values.containsKey(BookEntry.COLUMN_BOOK_QUANTITY)) {
            Double quantity = values.getAsDouble(BookEntry.COLUMN_BOOK_QUANTITY);
            if (quantity == null || quantity < 0) {
                Toast.makeText(getContext(), R.string.no_negative_values, Toast.LENGTH_SHORT).show();
                return 0;
            }
        }
        if (values.containsKey(BookEntry.COLUMN_SUPPLIER)) {
            String supplier = values.getAsString(BookEntry.COLUMN_SUPPLIER);
            if ((TextUtils.isEmpty(supplier))) {
                Toast.makeText(getContext(), R.string.supplier_required, Toast.LENGTH_SHORT).show();
                return 0;
            }
        }
        if (values.containsKey(BookEntry.COLUMN_SUPPLIER_NUM)) {
            Long supplier_num = values.getAsLong(BookEntry.COLUMN_SUPPLIER_NUM);
            if ((supplier_num == null || supplier_num < 0)) {
                Toast.makeText(getContext(), R.string.supplier_num_required, Toast.LENGTH_SHORT).show();
                return 0;
            }
        }
        if (values.size() == 0) {
            return 0;
        }
        int rowsUpdated = database.update(BookEntry.TABLE_NAME, values, selection, selectionArgs);
        // If 1 or more rows were updated, then notify all listeners that the data at the
        // given URI has changed
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Returns the number of database rows affected by the update statement
        return rowsUpdated;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {

        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int rowsDeleted;
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                // Delete all rows that match the selection and selection args
                // For  case BOOKS
                rowsDeleted = database.delete(BookEntry.TABLE_NAME, selection, selectionArgs);
                break;

            case BOOK_ID:
                // Delete a single row given by the ID in the URI
                selection = BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(BookEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        // If 1 or more rows were deleted, then notify all listeners that the data at the
        // given URI has changed
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    /**
     * Returns the MIME type of data for the content URI.
     */
    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {

        final int match = sUriMatcher.match(uri);

        switch (match) {
            case BOOKS:
                return BookEntry.CONTENT_LIST_TYPE;
            case BOOK_ID:
                return BookEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }

    }
}