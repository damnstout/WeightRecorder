package info.damnstout.wr;

import info.damnstout.wr.WeightCalc.Range;
import info.damnstout.wr.dao.Profile;
import info.damnstout.wr.dao.ProfileDao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class ProfileActivity extends Activity {

	/** a profile object work for the profile activity */
	private Profile profile = new Profile();

	private EditText etHeight;
	private TextView tvWeightRange;
	private Spinner spBirth;
	private RadioGroup rgGender;
	private EditText etGoal;
	private Button btnSave;

	private String[] birthYearArray;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// displayProfile();
		setContentView(R.layout.profile);
		findViews();
		initialData();
		addEvents();
		displayProfile();
	}

	private void initialData() {
		initialSpinnerBirthData();
		initialProfileFromDB();
	}

	private void initialProfileFromDB() {
		Profile dbProfile = ProfileDao.getInstance().getDBProfile();
		if (null != dbProfile && dbProfile.isValid()) {
			Profile.getSingleton().copyIn(dbProfile);
			profile.copyIn(dbProfile);
		} else {
			profile.setGender(0);
			profile.setBirthYear(Calendar.getInstance().get(Calendar.YEAR) - 25);
		}
	}

	private void addEvents() {
		addEditTextHeightChanged();
		addSpinnerBirthSelected();
		addGenderSelected();
		addSaveButtonClicked();
		addEditTextGoalChanged();
	}

	private void addEditTextGoalChanged() {
		etGoal.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable arg0) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				profile.trySetGoal(etGoal.getText().toString());
			}
		});
	}

	private void addSaveButtonClicked() {
		btnSave.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!profile.isCompleted()) {
					Toast.makeText(ProfileActivity.this, profile.getErrorMsg(),
							Constants.TOAST_DURATION).show();
					return;
				}
				Profile dbProfile = Profile.getSingleton();
				dbProfile.copyIn(profile);
				if (!ProfileDao.getInstance().saveOrUpdate(dbProfile)) {
					Toast.makeText(ProfileActivity.this, "±£¥Ê ß∞‹",
							Constants.TOAST_DURATION).show();
					return;
				}
				ProfileActivity.this.finish();
			}
		});
	}

	private void addGenderSelected() {
		rgGender.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				int gender = -1;
				if (checkedId == R.id.profileRadioGenderFemale) {
					gender = 0;
				}
				if (checkedId == R.id.profileRadioGenderMale) {
					gender = 1;
				}
				profile.setGender(gender);
				updateRecommendWeight();
			}
		});
	}

	private void initialSpinnerBirthData() {
		int thisYear = Calendar.getInstance().get(Calendar.YEAR);
		List<String> birthYearList = new ArrayList<String>();
		for (int i = 1; i < 70; i++) {
			birthYearList.add(Integer.toString(thisYear - i));
		}
		birthYearArray = birthYearList
				.toArray(new String[birthYearList.size()]);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, birthYearArray);
		spBirth.setAdapter(adapter);
	}

	private void addSpinnerBirthSelected() {
		spBirth.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				if (profile.trySetBirthYear(birthYearArray[position])) {
					updateRecommendWeight();
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}

		});
	}

	private void addEditTextHeightChanged() {
		etHeight.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable arg0) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (profile.trySetHeight(etHeight.getText().toString())) {
					updateRecommendWeight();
				}
			}
		});
	}

	private void findViews() {
		etHeight = (EditText) findViewById(R.id.profileEditTextHeight);
		tvWeightRange = (TextView) findViewById(R.id.profileTextviewWeightRange);
		spBirth = (Spinner) findViewById(R.id.profileSpinnerBirth);
		rgGender = (RadioGroup) findViewById(R.id.profileRadioGroupGender);
		etGoal = (EditText) findViewById(R.id.profileEditTextGoal);
		btnSave = (Button) findViewById(R.id.profileSave);
	}

	private void updateRecommendWeight() {
		if (!profile.isValid()) {
			return;
		}
		Range weightRange = WeightCalc.StandardWeightRange(profile.getAge(),
				profile.getGender(), profile.getHeight());
		tvWeightRange.setText(weightRange.toString());
	}

	private void displayProfile() {
		if (!profile.isValid()) {
			spBirth.setSelection(24);
			rgGender.check(R.id.profileRadioGenderFemale);
			return;
		}
		etHeight.setText(Integer.toString(profile.getHeight()));
		spBirth.setSelection(Calendar.getInstance().get(Calendar.YEAR)
				- profile.getBirthYear() - 1);
		switch (profile.getGender()) {
		case 0:
			rgGender.check(R.id.profileRadioGenderFemale);
			break;
		case 1:
			rgGender.check(R.id.profileRadioGenderMale);
			break;
		default:
			break;
		}
		etGoal.setText(String.format("%.1f", profile.getGoal()));
		// ListView lv = new ListView(this);// (ListView)
		// // findViewById(R.id.profileList);
		// profile = ProfileDao.getInstance().getFirstProfile();
		// ArrayList<Map<String, Object>> arr = new ArrayList<Map<String,
		// Object>>();
		// Map<String, Object> m1 = new HashMap<String, Object>();
		// m1.put("name", "damnstout");
		// m1.put("val", "178");
		// arr.add(m1);
		// SimpleAdapter sa = new SimpleAdapter(this, arr,
		// android.R.layout.two_line_list_item, new String[] { "name",
		// "val" }, new int[] { android.R.id.title,
		// android.R.id.text2 });
		// lv.setAdapter(sa);
		// LayoutInflater inflater = (LayoutInflater) getApplicationContext()
		// .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// LinearLayout layout = (LinearLayout)
		// inflater.inflate(R.layout.profile,
		// null);
		// layout.removeAllViews();
		// setContentView(lv);
	}

	public Profile getProfile() {
		return profile;
	}

	public void setProfile(Profile profile) {
		this.profile = profile;
	}

}
