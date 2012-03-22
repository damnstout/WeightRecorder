package info.damnstout.wr.dao;

import info.damnstout.wr.DatabaseOpenHelper;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class ProfileDao {

	private static ProfileDao instance;

	private SQLiteDatabase readDB;
	private SQLiteDatabase writeDB;

	public static ProfileDao getInstance() {
		if (null == instance) {
			instance = new ProfileDao(DatabaseOpenHelper.getInstance()
					.getReadDB(), DatabaseOpenHelper.getInstance().getWriteDB());
		}
		return instance;
	}

	private ProfileDao(SQLiteDatabase readDB, SQLiteDatabase writeDB) {
		this.readDB = readDB;
		this.writeDB = writeDB;
	}

	public Profile getDBProfile() {
		Profile rst = null;
		Cursor cur = readDB.rawQuery(
				"select p_id, birth_year, gender, height, goal "
						+ "from profile", null);
		if (cur.moveToFirst()) {
			rst = new Profile(cur);
		}
		try {
			cur.close();
		} catch (Exception e) {
		} finally {
			if (!cur.isClosed())
				cur.close();
			cur = null;
		}
		return rst;
	}

	public boolean saveOrUpdate(Profile p) {
		if (null == getDBProfile()) {
			return save(p);
		} else {
			return update(p);
		}
	}

	public boolean save(Profile p) {
		ContentValues vals = new ContentValues();
		vals.put("birth_year", p.getBirthYear());
		vals.put("gender", p.getGender());
		vals.put("height", p.getHeight());
		vals.put("goal", p.getGoal());
		return -1 != writeDB.insert("profile", null, vals);
	}

	public boolean update(Profile p) {
		ContentValues vals = new ContentValues();
		vals.put("birth_year", p.getBirthYear());
		vals.put("gender", p.getGender());
		vals.put("height", p.getHeight());
		vals.put("goal", p.getGoal());
		return 0 < writeDB.update("profile", vals, null, null);
	}
}
