package info.damnstout.wr;

import info.damnstout.wr.dao.Profile;
import info.damnstout.wr.dao.ProfileDao;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * 
 * @author damnstout
 * 
 */
public class WeightRecorderActivity extends Activity {

	private Profile profile;
	private Intent profileActIntent;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		DatabaseOpenHelper.initial(getApplicationContext());
		profileActIntent = new Intent(WeightRecorderActivity.this,
				ProfileActivity.class);
		setContentView(R.layout.main);
		addButton3Event();
		initialProfile();
		if (!profile.isValid()) {
			startActivity(profileActIntent);
		}
	}
	
	/**
	 * initiate the profile from database, if there has no data, the profile
	 * will be invalid
	 */
	private void initialProfile() {
		Profile p = ProfileDao.getInstance().getFirstProfile();
		profile = Profile.getSingleton();
		if (null != p) {
			profile.copyIn(p);
		}
	}

	public void addButton3Event() {
		Button b = (Button) findViewById(R.id.button1);
		b.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(profileActIntent);
			}
		});
	}
}