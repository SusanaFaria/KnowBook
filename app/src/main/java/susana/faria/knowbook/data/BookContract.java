package susana.faria.knowbook.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class BookContract {
    public static final String CONTENT_AUTHORITY = "susana.faria.knowbook";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_BOOKS = "Book_Inventory";
    private BookContract() {
    }

    public static final class BookEntry implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_BOOKS);

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKS;


        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKS;


        public final static String TABLE_NAME = "Book_Inventory";

        public final static String _ID = BaseColumns._ID;

        public final static String COLUMN_BOOK_NAME = "Book_Name";

        public final static String COLUMN_BOOK_AUTHOR = "Author";

        public final static String COLUMN_BOOK_GENRE = "Genre";

        public final static String COLUMN_BOOK_PRICE = "Price";

        public final static String COLUMN_BOOK_QUANTITY = "InStock_Quantity";

        public final static String COLUMN_SUPPLIER = "Supplier_Name";

        public final static String COLUMN_SUPPLIER_NUM = "Supplier_Num";

        //possible values for the books genre
        public final static int OTHER = 0;
        public final static int BIOGRAPHY = 1;
        public final static int TRAVEL = 2;
        public final static int SELF_HELP = 3;
        public final static int HISTORY = 4;
        public final static int SCIENCE = 5;
        public final static int HEALTH = 6;
        public final static int RELIGION_SPIRIT_NEWAGE = 7;
        public final static int ENCYCLOPEDIA = 8;
        public final static int DICTIONARY = 9;
        public final static int ART = 10;
        public final static int COOKING = 11;
        public final static int SCHOOL = 12;
        public final static int MYSTERY = 13;
        public final static int CRIME = 14;
        public final static int ADVENTURE = 15;
        public final static int SCIENCE_FICTION = 16;
        public final static int HORROR = 17;
        public final static int DRAMA = 18;
        public final static int THEATRE = 19;
        public final static int CHILDREN = 20;
        public final static int POETRY = 21;
        public final static int FANTASY = 22;
        public final static int NOVEL = 23;
        public final static int COMICS = 24;
    }

}