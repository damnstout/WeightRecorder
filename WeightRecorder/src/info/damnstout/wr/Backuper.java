package info.damnstout.wr;

import info.damnstout.wr.dao.Profile;
import info.damnstout.wr.dao.ProfileDao;
import info.damnstout.wr.dao.Record;
import info.damnstout.wr.dao.RecordDao;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;

import android.content.Context;

public class Backuper {

	private static Backuper instance;

	private String errorMessage = "";

	private Backuper() {
	}

	public static Backuper getInstance() {
		if (null == instance) {
			instance = new Backuper();
		}
		return instance;
	}

	/**
	 * backup data to specified server, the server will create a file named by
	 * the id and time, then save profile and weight history line by line
	 * 
	 * @param context
	 * @return
	 */
	public boolean backupToServer(Context context) {
		try {
			URL url = new URL(context.getResources().getString(
					R.string.backupUrl));
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setDoInput(true);
			conn.setDoOutput(true);
			PrintWriter out = new PrintWriter(conn.getOutputStream());
			out.print(makeHttpBakParams(context));
			out.flush();
			DataInputStream bin = new DataInputStream(conn.getInputStream());
			byte[] bytes = new byte[bin.available()];
			bin.read(bytes);
			String serverMsg = new String(bytes);
			out.close();
			bin.close();
			conn.disconnect();
			if ("succ".equals(serverMsg)) {
				return true;
			} else {
				errorMessage = serverMsg;
				return false;
			}
		} catch (ProtocolException e) {
			errorMessage = e.getMessage();
			return false;
		} catch (IOException e) {
			errorMessage = e.getMessage();
			return false;
		}
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	/**
	 * create the http request post parameter
	 * 
	 * @param context
	 * @return
	 */
	private String makeHttpBakParams(Context context) {
		StringBuilder sb = new StringBuilder();
		sb.append("machine=").append(SystemPropertyReader.getUniqueId(context))
				.append("&vals=").append(makeVals());
		return sb.toString();
	}

	/**
	 * make data content with record utilities
	 * 
	 * @return
	 */
	public String makeVals() {
		StringBuilder sb = new StringBuilder();
		Profile p = ProfileDao.getInstance().getDBProfile();
		if (p != null) {
			sb.append("Profile:").append(p.getHeight()).append(",")
					.append(p.getGender()).append(",").append(p.getBirthYear())
					.append(",").append(p.getGoal()).append("\r\n");
		}
		List<Record> records = RecordDao.getInstance().getRecords();
		for (Record r : records) {
			sb.append(r.getDate()).append(",").append(r.getWeightStr())
					.append("\r\n");
		}
		return sb.toString();
	}
}
