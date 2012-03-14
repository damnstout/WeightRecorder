package info.damnstout.wr;

import info.damnstout.wr.dao.Profile;
import android.app.Activity;
import android.os.Bundle;

public class ProfileActivity extends Activity {
	
	private Profile profile;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.profile);
	}
	
	private void displayProfile() {
		
	}

	public Profile getProfile() {
		return profile;
	}

	public void setProfile(Profile profile) {
		this.profile = profile;
	}

}
