package info.damnstout.wr;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseOpenHelper extends SQLiteOpenHelper {

	public static final String DATABASE_NAME = "wr.db";
	public static final int DATABASE_VERSION = 1;
	public static final String RROFILE_TABLE = "profile";
	public static final String RECORD_TABLE = "record";
	static final String TAG = "DatabaseHelper";

	static private DatabaseOpenHelper instance;
	private SQLiteDatabase readDB;
	private SQLiteDatabase writeDB;

	public static void initialize(Context context) {
		instance = new DatabaseOpenHelper(context);
	}

	public static void uninitialize() {
		if (null == instance) {
			return;
		}
		try {
			instance.readDB.close();
		} catch (Exception e) {
		} finally {
			if (null != instance.readDB && instance.readDB.isOpen()) {
				instance.readDB.close();
			}
			instance.readDB = null;
		}
		try {
			instance.writeDB.close();
		} catch (Exception e) {
		} finally {
			if (null != instance.writeDB && instance.writeDB.isOpen()) {
				instance.writeDB.close();
			}
			instance.writeDB = null;
		}
	}

	public static DatabaseOpenHelper getInstance() {
		return instance;
	}

	public SQLiteDatabase getReadDB() {
		if (null == readDB) {
			readDB = getReadableDatabase();
		}
		return readDB;
	}

	public SQLiteDatabase getWriteDB() {
		if (null == writeDB) {
			writeDB = getWritableDatabase();
		}
		return writeDB;
	}

	private DatabaseOpenHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE " + RROFILE_TABLE + " ("
				+ " p_id INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ " birth_year INTEGER," + " gender INTEGER,"
				+ " height INTEGER," + " goal DECIMAL(3, 1)" + ");");
		db.execSQL("CREATE TABLE " + RECORD_TABLE + " ("
				+ " r_date CHAR(8) PRIMARY KEY," + " weight DECIMAL(3, 1)"
				+ ");");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(DatabaseOpenHelper.TAG, "Upgrading database from version "
				+ oldVersion + " to " + newVersion
				+ ", which will destroy all old data");
		db.execSQL(String.format("DROP TABLE IF EXISTS %s", DATABASE_NAME,
				RECORD_TABLE));
		db.execSQL(String.format("DROP TABLE IF EXISTS %s", DATABASE_NAME,
				RROFILE_TABLE));
		onCreate(db);
	}
}