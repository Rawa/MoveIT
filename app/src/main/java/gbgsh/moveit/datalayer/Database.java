package gbgsh.moveit.datalayer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class Database {

    private static final String LOG_TAG = "Database";
    private DatabaseHelper dbHelper;
    private SQLiteDatabase database;
    private Float currentLevel=0.7f;

    public Database(Context context){
        dbHelper = new DatabaseHelper(context);
        database = dbHelper.getWritableDatabase();
    }
    public float getCurrentLevel(){
        return currentLevel;
    }
    public void setCurrentLevel(Float tmpCurrentLevel){
        Log.d(LOG_TAG, "setCurrentLevel: " + tmpCurrentLevel);
        //        Log.d(LOG_TAG, "Level set to: " + level);
        currentLevel=tmpCurrentLevel;
    }


    public long insert(int count){
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.StepEntry.COLUMN_NAME_COUNT, count);
        values.put(DatabaseHelper.StepEntry.COLUMN_NAME_TIMESTAMP, System.currentTimeMillis());


        //add mothly
        //add to dayly

        return database.insert(DatabaseHelper.StepEntry.TABLE_NAME, null, values);
    }

    public SQLiteDatabase connection() {
        return database;
    }

    private int executeCountQuery(String query, String column) {
        Log.d(LOG_TAG, "Executing sql query: " + query);
        Cursor cursor = database.rawQuery(query, null);
        try {
            if (cursor.getCount() > 0) {
                cursor.move(cursor.getCount());
                return cursor.getInt(cursor.getColumnIndex(column));
            }
        } finally {
            cursor.close();
        }

        return 0;
    }

    public int getTotalSteps() {
        String query = "SELECT SUM(count) AS the_sum FROM step LIMIT 1";
        return executeCountQuery(query, "the_sum");
    }

    public int getLatestSteps(long intervalMinutes) {
        long before = Math.max(System.currentTimeMillis() - intervalMinutes * 60 * 1000, 0);
        String query = "SELECT SUM(count) AS the_sum FROM step WHERE timestamp > " + before + " LIMIT 1";
        return executeCountQuery(query, "the_sum");
    }
}