package info.damnstout.wr;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.telephony.TelephonyManager;

public class SystemPropertyReader {

	/**
	 * retrieve unique id for the device, ordered by:
	 * 1. google account
	 * 2. device id
	 * @param context
	 * @return
	 */
	public static String getUniqueId(Context context) {
		String googleAccount = getGoogleAccount(context);
		if (null != googleAccount) {
			return googleAccount;
		}
		String deviceId = getDeviceId(context);
		if (null != deviceId) {
			return deviceId;
		}
		return null;
	}

	/**
	 * get bond google account of the device
	 * @param context
	 * @return
	 */
	public static String getGoogleAccount(Context context) {
		AccountManager accountManager = AccountManager.get(context);
		Account[] accounts = accountManager.getAccountsByType("com.google");
		if (null == accounts || 0 >= accounts.length) {
			return null;
		}
		return accounts[0].name;
	}

	/**
	 * get device id from the phone
	 * @param context
	 * @return
	 */
	public static String getDeviceId(Context context) {
		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		return tm.getDeviceId();
	}

}
