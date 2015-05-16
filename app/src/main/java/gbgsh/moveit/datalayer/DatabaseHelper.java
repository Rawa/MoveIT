package gbgsh.moveit.datalayer;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "moveit";

    private static final int DATABASE_VERSION = 2;

    /* Inner class that defines the table contents */
    public static abstract class StepEntry implements BaseColumns {
        public static final String TABLE_NAME = "step";
        public static final String COLUMN_NAME_COUNT = "count";
        public static final String COLUMN_NAME_TIMESTAMP = "timestamp";
    }

    private static final String INT_TYPE = " INT";
    private static final String TIMESTAMP_TYPE = " TIMESTAMP";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + StepEntry.TABLE_NAME + " (" +
                    StepEntry._ID + " INTEGER PRIMARY KEY," +
                    StepEntry.COLUMN_NAME_COUNT + INT_TYPE + COMMA_SEP +
                    StepEntry.COLUMN_NAME_TIMESTAMP + TIMESTAMP_TYPE +
                    " )";


    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + StepEntry.TABLE_NAME;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Method is called during creation of the database
    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(SQL_CREATE_ENTRIES);
    }

    // Method is called during an upgrade of the database,
    @Override
    public void onUpgrade(SQLiteDatabase database,int oldVersion,int newVersion){
        Log.w(DatabaseHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        database.execSQL(SQL_DELETE_ENTRIES);
        onCreate(database);
    }
}