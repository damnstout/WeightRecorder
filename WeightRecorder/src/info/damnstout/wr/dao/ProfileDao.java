package info.damnstout.wr.dao;

import info.damnstout.wr.DatabaseHelper;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class ProfileDao {

	private SQLiteDatabase db;

	private static ProfileDao instance;

	public static ProfileDao getInstance() {
		if (null == instance) {
			instance = new ProfileDao(DatabaseHelper.getInstance()
					.getWritableDatabase());
		}
		return instance;
	}

	public ProfileDao(SQLiteDatabase fdb) {
		db = fdb;
	}

	public Profile getProfile(int profId) {
		Cursor cur = db.rawQuery(
				"select p_id, name, birth_year, gender, height, weight "
						+ "from profile where p_id=?",
				new String[] { Integer.toString(profId) });
		if (1 != cur.getCount()) {
			return null;
		}
		cur.moveToFirst();
		Profile rst = ProfileDao.buildFromCursor(cur);
		cur.close();
		return rst;
	}

	public Profile getFirstProfile() {
		Cursor cur = db.rawQuery(
				"select p_id, name, birth_year, gender, height, weight "
						+ "from profile", null);
		if (0 == cur.getCount()) {
			return null;
		}
		cur.moveToFirst();
		Profile rst = ProfileDao.buildFromCursor(cur);
		cur.close();
		return rst;
	}

	public boolean save(Profile p) {
		ContentValues vals = new ContentValues();
		vals.put("name", p.getName());
		vals.put("birth_year", p.getBirthYear());
		vals.put("gender", p.getGender());
		vals.put("height", p.getHeight());
		vals.put("weight", p.getWeight());
		return -1 != db.insert("profile", null, vals);
	}

	static Profile buildFromCursor(Cursor cur) {
		return new Profile(cur.getInt(0), cur.getString(1), cur.getInt(2),
				cur.getInt(3), cur.getInt(4), cur.getDouble(5));
	}

	public SQLiteDatabase getDb() {
		return db;
	}

	public void setDb(SQLiteDatabase db) {
		this.db = db;
	}

}
