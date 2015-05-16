package gbgsh.moveit.datalayer;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class Database {

    private DatabaseHelper dbHelper;
    private SQLiteDatabase database;

    public Database(Context context){
        dbHelper = new DatabaseHelper(context);
        database = dbHelper.getWritableDatabase();
    }


    public long insert(int count){
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.StepEntry.COLUMN_NAME_COUNT, count);
        values.put(DatabaseHelper.StepEntry.COLUMN_NAME_TIMESTAMP, System.currentTimeMillis());
        return database.insert(DatabaseHelper.StepEntry.TABLE_NAME, null, values);
    }

    public SQLiteDatabase connection() {
        return database;
    }
}