package info.damnstout.wr;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
	
	private static final String DATABASE_NAME = "wr.db";
	private static final int DATABASE_VERSION = 1;
	private static final String RROFILE_TABLE = "profile";
	private static final String RECORD_TABLE = "record";
	static final String TAG = "DatabaseHelper";

	DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE " + RROFILE_TABLE + " ("
				+ " p_id INTEGER PRIMARY KEY AUTOINCREMENT," + " name TEXT,"
				+ " birth_year INTEGER," + " gender INTEGER,"
				+ " height INTEGER," + " weight DECIMAL(3, 1)" + ");");
		db.execSQL("CREATE TABLE " + RECORD_TABLE + " ("
				+ " r_id INTEGER PRIMARY KEY AUTOINCREMENT," + " profile_id INTEGER,"
				+ " r_date DATE," + " weight DECIMAL(3, 1)" + ");");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(DatabaseHelper.TAG, "Upgrading database from version " + oldVersion + " to "
				+ newVersion + ", which will destroy all old data");
		db.execSQL(String.format("DROP TABLE IF EXISTS %s", DATABASE_NAME,
				RECORD_TABLE));
		db.execSQL(String.format("DROP TABLE IF EXISTS %s", DATABASE_NAME,
				RROFILE_TABLE));
		onCreate(db);
	}
}