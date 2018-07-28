package susana.faria.knowbook;

import android.annotation.SuppressLint;
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

import java.util.Locale;

import susana.faria.knowbook.data.BookContract.BookEntry;

/**
 * Allows user to create a new book or edit an existing one.
 */
public class BookEditor extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {


    private static final int BOOK_LOADER = 0;
    Uri mCurrentBookUri;
    int quantity;
    private EditText mBookNameEditText;
    private EditText mBookAuthorEditText;
    private Spinner mBookGenreSpinner;
    private EditText mBookPriceEditText;
    private EditText mBookQuantityEditText;
    private EditText mBookSupplierEditText;
    private EditText mBookSupplierNumEditText;
    private int mGenre = BookEntry.OTHER;
    private boolean mBookHasChanged;
    private int mRowsDeleted = 0;
    private Long bookId;
    private Button mPlusButton;
    private Button mMinusButton;
    private Button mOrderButton;
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mBookHasChanged = true;
            return false;
        }
    };

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_editor);

        Intent bookEdit = getIntent();
        mCurrentBookUri = bookEdit.getData();
        mPlusButton = findViewById(R.id.plus_btn);
        mMinusButton = findViewById(R.id.minus_btn);
        mOrderButton = findViewById(R.id.order);

        if (mCurrentBookUri == null) {
            setTitle("Add a Book");
            // Invalidate the options menu, so the "Delete" menu option can be hidden.
            invalidateOptionsMenu();
            mPlusButton.setVisibility(View.INVISIBLE);
            mMinusButton.setVisibility(View.INVISIBLE);
        } else {
            setTitle("Edit Book");

            getLoaderManager().initLoader(BOOK_LOADER, null, this);
        }

        // Find all relevant views that we will need to read user input from
        mBookNameEditText = findViewById(R.id.edit_book_name);
        mBookAuthorEditText = findViewById(R.id.edit_book_author);
        mBookGenreSpinner = findViewById(R.id.spinner_genre);
        mBookPriceEditText = findViewById(R.id.edit_book_price);
        mBookQuantityEditText = findViewById(R.id.edit_book_quantity);
        mBookSupplierEditText = findViewById(R.id.edit_book_supplier);
        mBookSupplierNumEditText = findViewById(R.id.edit_book_supplier_num);

        mBookNameEditText.setOnTouchListener(mTouchListener);
        mBookAuthorEditText.setOnTouchListener(mTouchListener);
        mBookGenreSpinner.setOnTouchListener(mTouchListener);
        mBookPriceEditText.setOnTouchListener(mTouchListener);
        mBookQuantityEditText.setOnTouchListener(mTouchListener);
        mBookSupplierEditText.setOnTouchListener(mTouchListener);
        mBookSupplierNumEditText.setOnTouchListener(mTouchListener);

        mPlusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ContentValues values = new ContentValues();
                Uri currentBookUri = ContentUris.withAppendedId(BookEntry.CONTENT_URI, bookId);

                quantity++;
                values.put(BookEntry.COLUMN_BOOK_QUANTITY, quantity);

                String selection = BookEntry._ID + "=?";

                String[] selectionArgs = new String[]{String.valueOf(quantity)};
                getApplicationContext().getContentResolver().update(currentBookUri, values, selection, selectionArgs);
                mBookQuantityEditText.setText(String.valueOf(quantity));
            }


        });


        mMinusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (quantity == 0) {
                    mMinusButton.setEnabled(false);
                    Toast.makeText(BookEditor.this, getString(R.string.no_negative_values), Toast.LENGTH_SHORT).show();
                } else if (quantity > 0) {
                    ContentValues values = new ContentValues();
                    Uri currentBookUri = ContentUris.withAppendedId(BookEntry.CONTENT_URI, bookId);
                    quantity--;
                    values.put(BookEntry.COLUMN_BOOK_QUANTITY, quantity);
                    String selection = BookEntry._ID + "=?";

                    String[] selectionArgs = new String[]{String.valueOf(quantity)};
                    getApplicationContext().getContentResolver().update(currentBookUri, values, selection, selectionArgs);

                    mBookQuantityEditText.setText(String.valueOf(quantity));
                    mMinusButton.setEnabled(true);
                }
            }
        });

        mOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent callSupplier = new Intent(Intent.ACTION_DIAL);
                callSupplier.setData(Uri.parse("tel:" + mBookSupplierNumEditText.getText()));
                startActivity(callSupplier);
            }
        });

        setupSpinner();
    }

    /**
     * Setup the dropdown spinner that allows the user to select the gender of the pet.
     */
    private void setupSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter genreSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.genre_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        genreSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mBookGenreSpinner.setAdapter(genreSpinnerAdapter);

        // Set the integer mSelected to the constant values
        mBookGenreSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.biography))) {
                        mGenre = BookEntry.BIOGRAPHY;
                    } else if (selection.equals(getString(R.string.travel))) {
                        mGenre = BookEntry.TRAVEL;
                    } else if (selection.equals(getString(R.string.self_help))) {
                        mGenre = BookEntry.SELF_HELP;
                    } else if (selection.equals(getString(R.string.history))) {
                        mGenre = BookEntry.HISTORY;
                    } else if (selection.equals(getString(R.string.science))) {
                        mGenre = BookEntry.SCIENCE;
                    } else if (selection.equals(getString(R.string.health))) {
                        mGenre = BookEntry.HEALTH;
                    } else if (selection.equals(getString(R.string.religion))) {
                        mGenre = BookEntry.RELIGION_SPIRIT_NEWAGE;
                    } else if (selection.equals(getString(R.string.encyclopedia))) {
                        mGenre = BookEntry.ENCYCLOPEDIA;
                    } else if (selection.equals(getString(R.string.dictionary))) {
                        mGenre = BookEntry.DICTIONARY;
                    } else if (selection.equals(getString(R.string.art))) {
                        mGenre = BookEntry.ART;
                    } else if (selection.equals(getString(R.string.cooking))) {
                        mGenre = BookEntry.COOKING;
                    } else if (selection.equals(getString(R.string.school))) {
                        mGenre = BookEntry.SCHOOL;
                    } else if (selection.equals(getString(R.string.mystery))) {
                        mGenre = BookEntry.MYSTERY;
                    } else if (selection.equals(getString(R.string.crime))) {
                        mGenre = BookEntry.CRIME;
                    } else if (selection.equals(getString(R.string.adventure))) {
                        mGenre = BookEntry.ADVENTURE;
                    } else if (selection.equals(getString(R.string.science_fiction))) {
                        mGenre = BookEntry.SCIENCE_FICTION;
                    } else if (selection.equals(getString(R.string.horror))) {
                        mGenre = BookEntry.HORROR;
                    } else if (selection.equals(getString(R.string.drama))) {
                        mGenre = BookEntry.DRAMA;
                    } else if (selection.equals(getString(R.string.theatre))) {
                        mGenre = BookEntry.THEATRE;
                    } else if (selection.equals(getString(R.string.children))) {
                        mGenre = BookEntry.CHILDREN;
                    } else if (selection.equals(getString(R.string.poetry))) {
                        mGenre = BookEntry.POETRY;
                    } else if (selection.equals(getString(R.string.fantasy))) {
                        mGenre = BookEntry.FANTASY;
                    } else if (selection.equals(getString(R.string.novel))) {
                        mGenre = BookEntry.NOVEL;
                    } else if (selection.equals(getString(R.string.comics))) {
                        mGenre = BookEntry.COMICS;
                    } else {
                        mGenre = BookEntry.OTHER;
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mGenre = BookEntry.OTHER;
            }
        });
    }

    /**
     * Get user input from editor and save new book into database.
     */
    private void saveBook() {
        // Read from input fields
        // Use trim to eliminate leading or trailing white space
        String bookName = mBookNameEditText.getText().toString().trim();
        String authorName = mBookAuthorEditText.getText().toString().trim();
        String priceString = mBookPriceEditText.getText().toString().trim();
        String quantityString = mBookQuantityEditText.getText().toString().trim();
        String supplierName = mBookSupplierEditText.getText().toString().trim();
        String supplierNumString = mBookSupplierNumEditText.getText().toString().trim();


        if (mCurrentBookUri == null &&
                TextUtils.isEmpty(bookName) && TextUtils.isEmpty(authorName) &&
                TextUtils.isEmpty(priceString) && TextUtils.isEmpty(quantityString) && TextUtils.isEmpty(supplierName)
                && TextUtils.isEmpty(supplierNumString) && mGenre == BookEntry.OTHER) {
            return;
        }


        // Create a ContentValues object where column names are the keys,
        // and attributes from the editor are the values.
        ContentValues values = new ContentValues();
        values.put(BookEntry.COLUMN_BOOK_NAME, bookName);
        values.put(BookEntry.COLUMN_BOOK_AUTHOR, authorName);
        values.put(BookEntry.COLUMN_BOOK_GENRE, mGenre);
        values.put(BookEntry.COLUMN_BOOK_PRICE, priceString);
        values.put(BookEntry.COLUMN_BOOK_QUANTITY, quantityString);
        values.put(BookEntry.COLUMN_SUPPLIER, supplierName);
        values.put(BookEntry.COLUMN_SUPPLIER_NUM, supplierNumString);

        if (mCurrentBookUri == null) {

            // Insert a new book into the provider, returning the content URI for the new book.
            Uri newUri = getContentResolver().insert(BookEntry.CONTENT_URI, values);

            // Show a toast message depending on whether or not the insertion was successful
            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, getString(R.string.unsuccessful),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.success),
                        Toast.LENGTH_SHORT).show();
            }
        } else {


            // Otherwise this is an EXISTING book, so update the book with content URI: mCurrentBookUri
            // and pass in the new ContentValues. Pass in null for the selection and selection args
            // because mCurrentBookUri will already identify the correct row in the database that
            // we want to modify.
            int rowsAffected = getContentResolver().update(mCurrentBookUri, values, null, null);
            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, getString(R.string.update_error),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.update_success),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_book_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_book_editor, menu);
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
            // Respond to a click on the "Done" button in the app Bar
            case R.id.action_save:
                // Save book to database
                saveBook();
                // Exit activity and go back to catalog Activity
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Home" button in the app bar
            case android.R.id.home:

                // If the pet hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogActivity}.
                if (!mBookHasChanged) {
                    // Navigate back to parent activity (CatalogActivity)
                    NavUtils.navigateUpFromSameTask(this);
                    return true;
                }
// Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, navigate to parent activity.
                        NavUtils.navigateUpFromSameTask(BookEditor.this);
                    }
                };
                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

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

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.alert_dialog);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.stay, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
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

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.alert_dialog);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                deleteBook();
            }
        });
        builder.setNegativeButton(R.string.stay, new DialogInterface.OnClickListener() {
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

    /**
     * Perform the deletion of the pet in the database.
     */
    private void deleteBook() {
        // Only perform the delete if this is an existing pet.
        if (mCurrentBookUri != null) {
            // Call the ContentResolver to delete the pet at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentPetUri
            // content URI already identifies the pet that we want.
            int rowsDeleted = getContentResolver().delete(mCurrentBookUri, null, null);

            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.delete_fail),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.delete_success),
                        Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                BookEntry._ID,
                BookEntry.COLUMN_BOOK_NAME,
                BookEntry.COLUMN_BOOK_AUTHOR,
                BookEntry.COLUMN_BOOK_GENRE,
                BookEntry.COLUMN_BOOK_PRICE,
                BookEntry.COLUMN_BOOK_QUANTITY,
                BookEntry.COLUMN_SUPPLIER,
                BookEntry.COLUMN_SUPPLIER_NUM};
        return new CursorLoader(
                this,
                mCurrentBookUri,
                projection,
                null,
                null,
                null);
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
            // Find the columns of pet attributes that we're interested in
            int book_id = cursor.getColumnIndex(BookEntry._ID);
            int nameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_NAME);
            int authorColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_AUTHOR);
            int genreColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_GENRE);
            int priceColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_QUANTITY);
            int supplierColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_SUPPLIER);
            int supplierNumColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_SUPPLIER_NUM);

            // Extract out the value from the Cursor for the given column index
            bookId = cursor.getLong(book_id);
            String name = cursor.getString(nameColumnIndex);
            String author = cursor.getString(authorColumnIndex);
            int genre = cursor.getInt(genreColumnIndex);
            int price = cursor.getInt(priceColumnIndex);
            quantity = cursor.getInt(quantityColumnIndex);
            String supplier = cursor.getString(supplierColumnIndex);
            int supplierNum = cursor.getInt(supplierNumColumnIndex);

            // Update the views on the screen with the values from the database
            mBookNameEditText.setText(name);
            mBookAuthorEditText.setText(author);
            mBookPriceEditText.setText(String.format(Locale.getDefault(), "%d", price));
            mBookQuantityEditText.setText(String.format(Locale.getDefault(), "%d", quantity));
            mBookSupplierEditText.setText(supplier);
            mBookSupplierNumEditText.setText(String.format(Locale.getDefault(), "%d", supplierNum));

            // Genre is a dropdown spinner, so map the constant value from the database
            // into one of the dropdown options.
            // Then call setSelection() so that option is displayed on screen as the current selection.
            switch (genre) {
                case BookEntry.BIOGRAPHY:
                    mBookGenreSpinner.setSelection(1);
                    break;
                case BookEntry.TRAVEL:
                    mBookGenreSpinner.setSelection(2);
                    break;
                case BookEntry.SELF_HELP:
                    mBookGenreSpinner.setSelection(3);
                    break;
                case BookEntry.HISTORY:
                    mBookGenreSpinner.setSelection(4);
                    break;
                case BookEntry.SCIENCE:
                    mBookGenreSpinner.setSelection(5);
                    break;
                case BookEntry.HEALTH:
                    mBookGenreSpinner.setSelection(6);
                    break;
                case BookEntry.RELIGION_SPIRIT_NEWAGE:
                    mBookGenreSpinner.setSelection(7);
                    break;
                case BookEntry.ENCYCLOPEDIA:
                    mBookGenreSpinner.setSelection(8);
                    break;
                case BookEntry.DICTIONARY:
                    mBookGenreSpinner.setSelection(9);
                    break;
                case BookEntry.ART:
                    mBookGenreSpinner.setSelection(10);
                    break;
                case BookEntry.COOKING:
                    mBookGenreSpinner.setSelection(11);
                    break;
                case BookEntry.SCHOOL:
                    mBookGenreSpinner.setSelection(12);
                    break;
                case BookEntry.MYSTERY:
                    mBookGenreSpinner.setSelection(13);
                    break;
                case BookEntry.CRIME:
                    mBookGenreSpinner.setSelection(14);
                    break;
                case BookEntry.ADVENTURE:
                    mBookGenreSpinner.setSelection(15);
                    break;
                case BookEntry.SCIENCE_FICTION:
                    mBookGenreSpinner.setSelection(16);
                    break;
                case BookEntry.HORROR:
                    mBookGenreSpinner.setSelection(17);
                    break;
                case BookEntry.DRAMA:
                    mBookGenreSpinner.setSelection(18);
                    break;
                case BookEntry.THEATRE:
                    mBookGenreSpinner.setSelection(19);
                    break;
                case BookEntry.CHILDREN:
                    mBookGenreSpinner.setSelection(20);
                    break;
                case BookEntry.POETRY:
                    mBookGenreSpinner.setSelection(21);
                    break;
                case BookEntry.FANTASY:
                    mBookGenreSpinner.setSelection(22);
                    break;
                case BookEntry.NOVEL:
                    mBookGenreSpinner.setSelection(23);
                    break;
                case BookEntry.COMICS:
                    mBookGenreSpinner.setSelection(24);
                    break;

                default:
                    mBookGenreSpinner.setSelection(0);
                    break;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        mBookNameEditText.setText("");
        mBookAuthorEditText.setText("");
        mBookGenreSpinner.setSelection(0);// Select "Other" genre
        mBookPriceEditText.setText("");
        mBookQuantityEditText.setText("");
        mBookSupplierEditText.setText("");
        mBookSupplierNumEditText.setText("");
    }
}