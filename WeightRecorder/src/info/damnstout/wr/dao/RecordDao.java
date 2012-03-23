package info.damnstout.wr.dao;

import info.damnstout.wr.DatabaseOpenHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class RecordDao {

	private static RecordDao instance;

	private SQLiteDatabase readDB;
	private SQLiteDatabase writeDB;

	public static RecordDao getInstance() {
		if (null == instance) {
			instance = new RecordDao(DatabaseOpenHelper.getInstance()
					.getReadDB(), DatabaseOpenHelper.getInstance().getWriteDB());
		}
		return instance;
	}

	public String findNextEmptyDay(String date) {
		Date dateObj = Record.parseDateString(date);
		Date todayObj = Calendar.getInstance().getTime();
		if (dateObj.after(todayObj)) {
			return null;
		}
		String todayStr = Record.formatDate(todayObj);
		if (todayStr.equals(date)) {
			return null;
		}
		Calendar dateCalendar = Calendar.getInstance();
		dateCalendar.setTime(dateObj);
		dateCalendar.add(Calendar.DATE, 1);
		String nextDate = Record.formatDate(dateCalendar.getTime());
		if (null == getSimpleRecord(nextDate)) {
			return nextDate;
		} else {
			return findNextEmptyDay(nextDate);
		}
	}

	private RecordDao(SQLiteDatabase readDB, SQLiteDatabase writeDB) {
		this.readDB = readDB;
		this.writeDB = writeDB;
	}

	public boolean save(Record r) {
		ContentValues vals = new ContentValues();
		vals.put("r_date", r.getDate());
		vals.put("weight", r.getWeight());
		return -1 != writeDB.insert("record", null, vals);
	}

	public boolean update(Record r) {
		ContentValues vals = new ContentValues();
		vals.put("weight", r.getWeight());
		return 0 < writeDB.update("record", vals, "r_date=?",
				new String[] { r.getDate() });
	}
	
	public boolean delete(String date) {
		return 0 < writeDB.delete("record", "r_date=?", new String[] {date});
	}

	public List<Record> getRecords() {
		List<Record> rst = new ArrayList<Record>();
		Cursor cur = readDB.rawQuery("select r_date, weight from record "
				+ "order by r_date desc", null);
		boolean hasData = cur.moveToFirst();
		while (hasData) {
			rst.add(new Record(cur));
			hasData = cur.moveToNext();
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

	public Record getPreviousRecordBeforeDate(String date) {
		Record rst = null;
		Cursor cur = readDB.rawQuery(
				"select r_date, weight from record where r_date<?"
						+ " order by r_date desc limit 1",
				new String[] { date });
		if (cur.moveToFirst()) {
			rst = new Record(cur);
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

	public Record getLatestRecord() {
		Record rst = null;
		Cursor cur = readDB.rawQuery("select r_date, weight from record"
				+ " order by r_date desc limit 1", null);
		if (cur.moveToFirst()) {
			rst = new Record(cur);
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

	public Record getFullRecord(String date) {
		Record rst = null;
		Cursor cur = readDB.rawQuery("select r_date, weight from record"
				+ " where r_date<=? order by r_date desc limit 2",
				new String[] { date });
		if (cur.moveToFirst() && date.equals(cur.getString(0))) {
			rst = new Record(cur);
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

	public Record getSimpleRecord(String date) {
		Record rst = null;
		Cursor cur = readDB.rawQuery("select r_date, weight from record"
				+ " where r_date=?", new String[] { date });
		if (cur.moveToFirst()) {
			rst = new Record(cur);
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

}
