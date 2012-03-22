package info.damnstout.wr.dao;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.database.Cursor;

public class Record {

	static SimpleDateFormat internalSdf = new SimpleDateFormat("yyyyMMdd");
	static SimpleDateFormat displaySdf = new SimpleDateFormat("yyyy年M月d日");

	private String date;

	private double change;

	private double weight;

	private String errorMsg = "";

	public static String formatDate(Date d) {
		return internalSdf.format(d);
	}

	public static Date parseDateString(String str) {
		try {
			return internalSdf.parse(str);
		} catch (ParseException e) {
			e.printStackTrace();
			return Calendar.getInstance().getTime();
		}
	}

	public Record() {
		date = getCurrentDay();
		change = 0;
		weight = -1;
	}

	/**
	 * @return
	 */
	public static String getCurrentDay() {
		return formatDate(Calendar.getInstance().getTime());
	}

	public Record(String fDate, double fChange, double fWeight) {
		init(fDate, fChange, fWeight);
	}

	public Record(Cursor cursor) {
		String fDate = cursor.getString(0);
		double fWeight = cursor.getDouble(1);
		double fChange = 0;
		if (cursor.moveToNext()) {
			fChange = fWeight - cursor.getDouble(1);
			cursor.moveToPrevious();
		}
		init(fDate, fChange, fWeight);
	}

	private void init(String fDate, double fChange, double fWeight) {
		date = fDate;
		change = fChange;
		weight = fWeight;
	}

	public String getDate() {
		return date;
	}

	public Calendar getDateObj() {
		try {
			Calendar rst = Calendar.getInstance();
			rst.setTime(internalSdf.parse(date));
			return rst;
		} catch (ParseException e) {
			e.printStackTrace();
			return Calendar.getInstance();
		}
	}

	public int getYear() {
		return getDateObj().get(Calendar.YEAR);
	}

	public int getMonth() {
		return getDateObj().get(Calendar.MONTH);
	}

	public int getDay() {
		return getDateObj().get(Calendar.DAY_OF_MONTH);
	}

	public String getDisplayDate() {
		return displaySdf.format(getDateObj().getTime());
	}

	public void setDate(String date) {
		this.date = date;
	}

	public void setDate(Date date) {
		this.date = internalSdf.format(date);
	}

	public double getChange() {
		return change;
	}

	public void setChange(double change) {
		this.change = change;
	}

	public double getWeight() {
		return weight;
	}

	public String getWeightStr() {
		return String.format("%.1f", weight);
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public boolean trySetWeight(String sWeight) {
		try {
			setWeight(Double.parseDouble(sWeight));
			return true;
		} catch (Exception e) {
			weight = -1;
			return false;
		}
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public boolean isValid() {
		if (null == date || "".equals(date)) {
			errorMsg = "请选择日期";
			return false;
		}
		if (0 > weight) {
			errorMsg = "请输入体重数值";
			return false;
		}
		return true;
	}
}
