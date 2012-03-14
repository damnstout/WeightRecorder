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
	private Intent profileActIntent = new Intent(WeightRecorderActivity.this,
			ProfileActivity.class);

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		DatabaseHelper.initial(getApplicationContext());
		setContentView(R.layout.main);
		profile = ProfileDao.getInstance().getFirstProfile();
		startActivity(profileActIntent);
		addButton3Event();
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