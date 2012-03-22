package info.damnstout.wr.dao;

import java.util.Calendar;

import android.database.Cursor;

public class Profile {

	private int id;

	private int birthYear;

	private int gender;

	private int height;

	private double goal;

	private String errorMsg;

	private static Profile _instance;

	public static Profile getSingleton() {
		if (null == _instance) {
			_instance = new Profile();
		}
		return _instance;
	}

	public Profile() {
		id = birthYear = gender = height = -1;
		goal = -1;
	}

	public Profile(Cursor cur) {
		init(cur.getInt(0), cur.getInt(1), cur.getInt(2), cur.getInt(3),
				cur.getDouble(4));
	}

	public Profile(int fid, int fBirthYear, int fGender, int fHeight,
			double fWeight) {
		init(fid, fBirthYear, fGender, fHeight, fWeight);
	}

	private void init(int fid, int fBirthYear, int fGender, int fHeight,
			double fWeight) {
		id = fid;
		birthYear = fBirthYear;
		gender = fGender;
		height = fHeight;
		goal = fWeight;
	}

	public void copyIn(Profile other) {
		if (!other.isValid()) {
			return;
		}
		birthYear = other.birthYear;
		gender = other.gender;
		height = other.height;
		goal = other.goal;
	}

	public boolean isValid() {
		if (-1 == birthYear) {
			errorMsg = "尚未选择出生年份";
			return false;
		}
		if (-1 == gender) {
			errorMsg = "尚未选择您的性别";
			return false;
		}
		if (-1 == height) {
			errorMsg = "尚未输入您的身高";
			return false;
		}
		return true;
	}

	public boolean isCompleted() {
		if (!isValid()) {
			return false;
		}
		if (0 > goal) {
			errorMsg = "尚未输入您的目标体重";
			return false;
		}
		return true;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getAge() {
		return Calendar.getInstance().get(Calendar.YEAR) - birthYear;
	}

	public int getBirthYear() {
		return birthYear;
	}

	public void setBirthYear(int birthYear) {
		if (1900 <= birthYear
				&& Calendar.getInstance().get(Calendar.YEAR) >= birthYear)
			this.birthYear = birthYear;
	}

	public boolean trySetBirthYear(String sBirthYear) {
		try {
			setBirthYear(Integer.parseInt(sBirthYear));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public int getGender() {
		return gender;
	}

	public void setGender(int gender) {
		if (0 == gender || 1 == gender)
			this.gender = gender;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		if (50 <= height && 255 >= height)
			this.height = height;
	}

	public boolean trySetHeight(String sHeight) {
		try {
			setHeight(Integer.parseInt(sHeight));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public double getGoal() {
		return goal;
	}

	public void setGoal(double goal) {
		if (5 <= goal && 500 >= goal)
			this.goal = goal;
	}

	public boolean trySetGoal(String sGoal) {
		try {
			goal = Integer.parseInt(sGoal);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public String getErrorMsg() {
		return errorMsg;
	}

}
