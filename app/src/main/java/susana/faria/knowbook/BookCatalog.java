package susana.faria.knowbook;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import susana.faria.knowbook.data.BookContract.BookEntry;

public class BookCatalog extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int BOOK_LOADER = 0;
    private BookCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_catalog);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BookCatalog.this, BookEditor.class);
                startActivity(intent);
            }
        });

        // Find the ListView which will be populated with the data
        ListView bookListView = (ListView) findViewById(R.id.list);
        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.emptyView);
        bookListView.setEmptyView(emptyView);

        // Setup cursor adapter using cursor from last step
        mCursorAdapter = new BookCursorAdapter(this, null);
        // Attach cursor adapter to the ListView
        bookListView.setAdapter(mCursorAdapter);

        bookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                final Uri currentBookUri = ContentUris.withAppendedId(BookEntry.CONTENT_URI, id);
                Intent bookEdit = new Intent(BookCatalog.this, BookEditor.class);
                bookEdit.setData(currentBookUri);
                startActivity(bookEdit);

            }
        });
        getLoaderManager().initLoader(BOOK_LOADER, null, this);
    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    private void insertBook() {
        // Create a ContentValues object to test if the app runs without problems.
        ContentValues values = new ContentValues();
        values.put(BookEntry.COLUMN_BOOK_NAME, getString(R.string.remains));
        values.put(BookEntry.COLUMN_BOOK_AUTHOR, getString(R.string.ishiguro));
        values.put(BookEntry.COLUMN_BOOK_PRICE, getString(R.string.fake_price));
        values.put(BookEntry.COLUMN_BOOK_QUANTITY, getString(R.string.fake_quantity));
        values.put(BookEntry.COLUMN_BOOK_GENRE, BookEntry.NOVEL);
        values.put(BookEntry.COLUMN_SUPPLIER, getString(R.string.fake_supplier));
        values.put(BookEntry.COLUMN_SUPPLIER_NUM, getString(R.string.fake_supp_phone));

        getContentResolver().insert(BookEntry.CONTENT_URI, values);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_book_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_book_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_fake_data:
                insertBook();

                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                showDeleteConfirmationDialog();

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.confirm_delete_all);
        builder.setPositiveButton(R.string.delete_yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pets.
                deleteAllBooks();
            }
        });
        builder.setNegativeButton(R.string.delete_no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void deleteAllBooks() {
        if (BookEntry.CONTENT_URI != null) {
            int rowsDeleted = getContentResolver().delete(BookEntry.CONTENT_URI, null, null);
            Log.v("CatalogActivity", rowsDeleted + " rows deleted from book database");
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {BookEntry._ID,
                BookEntry.COLUMN_BOOK_NAME,
                BookEntry.COLUMN_BOOK_AUTHOR,
                BookEntry.COLUMN_BOOK_QUANTITY,
                BookEntry.COLUMN_BOOK_PRICE};
        return new CursorLoader(this, BookEntry.CONTENT_URI, projection, null,
                null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);

    }
}