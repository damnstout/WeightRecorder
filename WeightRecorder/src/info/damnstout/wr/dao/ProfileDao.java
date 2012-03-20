package info.damnstout.wr.dao;

import info.damnstout.wr.DatabaseOpenHelper;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class ProfileDao {

	private SQLiteDatabase db;

	private static ProfileDao instance;

	public static ProfileDao getInstance() {
		if (null == instance) {
			instance = new ProfileDao(DatabaseOpenHelper.getInstance()
					.getWritableDatabase());
		}
		return instance;
	}

	public ProfileDao(SQLiteDatabase fdb) {
		db = fdb;
	}

	public Profile getProfile(int profId) {
		Cursor cur = db.rawQuery(
				"select p_id, birth_year, gender, height, goal "
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
				"select p_id, birth_year, gender, height, goal "
						+ "from profile", null);
		if (0 == cur.getCount()) {
			return null;
		}
		cur.moveToFirst();
		Profile rst = ProfileDao.buildFromCursor(cur);
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
		if (null == getFirstProfile()) {
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
		return -1 != db.insert("profile", null, vals);
	}

	public boolean update(Profile p) {
		ContentValues vals = new ContentValues();
		vals.put("birth_year", p.getBirthYear());
		vals.put("gender", p.getGender());
		vals.put("height", p.getHeight());
		vals.put("goal", p.getGoal());
		return 0 < db.update("profile", vals, null, null);
	}

	static Profile buildFromCursor(Cursor cur) {
		return new Profile(cur.getInt(0), cur.getInt(1), cur.getInt(2),
				cur.getInt(3), cur.getDouble(4));
	}

	public SQLiteDatabase getDb() {
		return db;
	}

	public void setDb(SQLiteDatabase db) {
		this.db = db;
	}

}
