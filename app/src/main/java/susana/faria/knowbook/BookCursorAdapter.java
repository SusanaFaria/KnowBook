package susana.faria.knowbook;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import susana.faria.knowbook.data.BookContract.BookEntry;

public class BookCursorAdapter extends CursorAdapter {

    public BookCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        // Find fields to populate in inflated template
        TextView book_name = (TextView) view.findViewById(R.id.name);
        TextView book_author = (TextView) view.findViewById(R.id.summary);
        TextView book_quantity = (TextView) view.findViewById(R.id.quantity_display);
        TextView book_price = (TextView) view.findViewById(R.id.price_display);
        final Button sale_btn = (Button) view.findViewById(R.id.sale_button);

        //find the columns of book attributes we're interested in//
        int nameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_NAME);
        int authorColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_AUTHOR);
        int quantityColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_QUANTITY);
        int priceColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_PRICE);
        // Extract properties from cursor
        String name = cursor.getString(nameColumnIndex);
        String author = cursor.getString(authorColumnIndex);
        final String quantity = cursor.getString(quantityColumnIndex);
        String price = cursor.getString(priceColumnIndex);

        final int book_id = cursor.getInt(cursor.getColumnIndexOrThrow(BookEntry._ID));

        // Update the TextViews with the attributes for the current book//
        book_name.setText(name);
        book_author.setText(author);
        book_quantity.setText(String.format("%s%s", context.getString(R.string.in_stock), quantity));
        book_price.setText(String.format("%s%s", price, context.getString(R.string.euro_symbol)));

        sale_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ContentValues values = new ContentValues();

                int quantityValue = Integer.parseInt(quantity) - 1;
                values.put(BookEntry.COLUMN_BOOK_QUANTITY, quantityValue);
                String selection = BookEntry._ID + "=?";
                Uri currentBookUri = ContentUris.withAppendedId(BookEntry.CONTENT_URI, book_id);
                String[] selectionArgs = new String[]{String.valueOf(book_id)};
                context.getContentResolver().update(currentBookUri, values, selection, selectionArgs);

                if (quantityValue == 0) {
                    sale_btn.setEnabled(false);
                    Toast.makeText(context.getApplicationContext(), R.string.no_negative_values, Toast.LENGTH_SHORT).show();
                } else if (quantityValue > 0) {
                    sale_btn.setEnabled(true);
                }
            }
        });
    }
}