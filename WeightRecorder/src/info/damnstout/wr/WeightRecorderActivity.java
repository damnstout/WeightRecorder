package info.damnstout.wr;

import info.damnstout.wr.dao.Profile;
import info.damnstout.wr.dao.ProfileDao;
import info.damnstout.wr.dao.Record;
import info.damnstout.wr.dao.RecordDao;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.ScaleAnimation;
import android.view.animation.Transformation;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Toast;
import android.widget.ToggleButton;

/**
 * 
 * @author damnstout
 * 
 */
public class WeightRecorderActivity extends Activity {

	private static final int MENU_ITEM_INPUT = 0;
	private static final int MENU_ITEM_LIST = 1;
	private static final int MENU_ITEM_STATISTIC = 2;
	private static final int MENU_ITEM_PROFILE = 3;
	private static final int DIALOG_DATE = 0;

	private static boolean isExiting = false;
	private static boolean hasTask = false;

	private Intent profileActIntent;
	private Intent recordListActIntent;

	private Record record;
	private Timer exitTimer;

	private LinearLayout inputLayout;
	private ToggleButton toggleLockInput;
	private Button btnDate;
	private EditText etWeight;
	private Button btnDateDecrease;
	private Button btnDateIncrease;
	private Button btnWeightDecrease;
	private Button btnWeightIncrease;
	private Button btnRecord;
	private DatePickerDialog datePicker;
	private TimerTask exitTimerTask;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		DatabaseOpenHelper.initialize(getApplicationContext());
		exitTimer = new Timer();
		exitTimerTask = new TimerTask() {
			@Override
			public void run() {
				isExiting = false;
				hasTask = true;
			}
		};
		profileActIntent = new Intent(WeightRecorderActivity.this,
				ProfileActivity.class);
		recordListActIntent = new Intent(WeightRecorderActivity.this,
				RecordListActivity.class);
		setContentView(R.layout.main);
		findViews();
		initialData();
		addEvents();
		requestInitialProfile();
	}

	private void addEvents() {
		addButton2Event();
		addButtonDateEvent();
		addButtonDateDecrease();
		addButtonDateIncrease();
		addButtonWeightDecrease();
		addButtonWeightIncrease();
		addButtonRecord();
		addEditTextWeightChange();
		addToggleInputChange();
	}

	private void addToggleInputChange() {
		toggleLockInput
				.setOnCheckedChangeListener(new ToggleButton.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (!isChecked) {
							setInputGone();
						}
					}
				});
	}

	private void addEditTextWeightChange() {
		etWeight.addTextChangedListener(new TextWatcher() {
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
				record.trySetWeight(etWeight.getText().toString());
			}
		});
	}

	private void addButtonRecord() {
		btnRecord.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!record.isValid()) {
					Toast.makeText(WeightRecorderActivity.this,
							record.getErrorMsg(), Constants.TOAST_DURATION)
							.show();
					return;
				}
				Record dbRecord = RecordDao.getInstance().getSimpleRecord(
						record.getDate());
				boolean recordSuccess = false;
				if (null == dbRecord) {
					recordSuccess = saveRecord();
				} else {
					recordSuccess = updateRecord();
				}
				if (!toggleLockInput.isChecked() && recordSuccess) {
					setInputGone();
				} else if (toggleLockInput.isChecked()) {
					toNextEmptyDay();
				}
				etWeight.requestFocus();
			}

			private void toNextEmptyDay() {
				String nextDay = RecordDao.getInstance().findNextEmptyDay(
						record.getDate());
				if (null != nextDay) {
					record.setDate(nextDay);
					updateRecordUI();
				}
			}

			private boolean updateRecord() {
				if (RecordDao.getInstance().update(record)) {
					Toast.makeText(
							WeightRecorderActivity.this,
							String.format("已更新%s体重为%s",
									record.getDisplayDate(),
									record.getWeightStr()),
							Constants.TOAST_DURATION).show();
					updateWeightEditText();
					return true;
				} else {
					Toast.makeText(WeightRecorderActivity.this, "更新体重记录失败",
							Constants.TOAST_DURATION).show();
					return false;
				}
			}

			private boolean saveRecord() {
				if (RecordDao.getInstance().save(record)) {
					Toast.makeText(WeightRecorderActivity.this,
							String.format("已记录%s体重", record.getDisplayDate()),
							Constants.TOAST_DURATION).show();
					updateWeightEditText();
					return true;
				} else {
					Toast.makeText(WeightRecorderActivity.this, "记录体重失败",
							Constants.TOAST_DURATION).show();
					return false;
				}
			}
		});
	}

	private void addButtonWeightIncrease() {
		btnWeightIncrease.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				changeWeight(0.1);
				etWeight.requestFocus();
			}
		});
	}

	private void addButtonWeightDecrease() {
		btnWeightDecrease.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				changeWeight(-0.1);
				etWeight.requestFocus();
			}
		});
	}

	private void changeWeight(double value) {
		if (!"".equals(etWeight.getText().toString())) {
			try {
				double uiValue = Double.parseDouble(etWeight.getText()
						.toString());
				double newValue = uiValue + value;
				if (newValue > 500 || newValue < 5) {
					newValue = uiValue;
				}
				etWeight.setText(String.format("%.1f", newValue));
				record.setWeight(newValue);
			} catch (NumberFormatException e) {
			}
		} else {
			try {
				double uiValue = Double.parseDouble(etWeight.getHint()
						.toString());
				double newValue = uiValue + value;
				if (newValue > 500 || newValue < 5) {
					newValue = uiValue;
				}
				etWeight.setHint(String.format("%.1f", newValue));
				record.setWeight(newValue);
			} catch (NumberFormatException e) {
			}

		}
	}

	private void addButtonDateIncrease() {
		btnDateIncrease.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				changeDay(1);
				etWeight.requestFocus();
			}
		});
	}

	private void addButtonDateDecrease() {
		btnDateDecrease.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				changeDay(-1);
				etWeight.requestFocus();
			}
		});
	}

	private void changeDay(int value) {
		Calendar cal = record.getDateObj();
		cal.add(Calendar.DATE, value);
		if (Calendar.getInstance().getTime().before(cal.getTime())) {
			return;
		}
		record.setDate(cal.getTime());
		updateRecordUI();
	}

	private void addButtonDateEvent() {
		btnDate.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				showDialog(DIALOG_DATE);
				etWeight.requestFocus();
			}
		});
	}

	private void initialData() {
		record = RecordDao.getInstance().getLatestRecord();
		if (null == record) {
			record = new Record();
		}
		String curDayStr = Record.formatDate(Calendar.getInstance().getTime());
		if (record.isValid() && curDayStr.equals(record.getDate())
				&& !toggleLockInput.isChecked()) {
			setInputGone();
		} else {
			setInputVisible();
			record.setDate(curDayStr);
			record.setWeight(-1);
			record.setChange(0);
		}
		updateRecordUI();
	}

	private void updateRecordWithDate() {
		Record dbRecord = RecordDao.getInstance().getSimpleRecord(
				record.getDate());
		if (null != dbRecord) {
			record.setWeight(dbRecord.getWeight());
		} else {
			record.setWeight(-1);
		}
	}

	private void updateWeightEditText() {
		if (0 < record.getWeight()) {
			etWeight.setText(record.getWeightStr());
			return;
		}
		etWeight.setText("");
		Record previousRecord = RecordDao.getInstance()
				.getPreviousRecordBeforeDate(record.getDate());
		if (null == previousRecord) {
			etWeight.setHint(R.string.mainEditTextMainInputHint);
		} else {
			etWeight.setHint(previousRecord.getWeightStr());
			record.setWeight(previousRecord.getWeight());
		}
	}

	private void updateDateButtonText() {
		Calendar calendar = Calendar.getInstance();
		String todayStr = Record.formatDate(calendar.getTime());
		calendar.add(Calendar.DATE, -1);
		String yesterdayStr = Record.formatDate(calendar.getTime());
		calendar.add(Calendar.DATE, -1);
		String twoDayAgoStr = Record.formatDate(calendar.getTime());
		String date = record.getDate();
		String displayDate = "";
		if (todayStr.equals(date)) {
			displayDate = "今天 ";
		} else if (yesterdayStr.equals(date)) {
			displayDate = "昨天";
		} else if (twoDayAgoStr.equals(date)) {
			displayDate = "前天 ";
		}
		displayDate = displayDate + record.getDisplayDate();
		btnDate.setText(displayDate);
		if (null != datePicker) {
			datePicker.updateDate(record.getYear(), record.getMonth(),
					record.getDay());
		}
	}

	private void findViews() {
		inputLayout = (LinearLayout) findViewById(R.id.mainLayoutInput);
		toggleLockInput = (ToggleButton) findViewById(R.id.mainToggleLockInput);
		btnDate = (Button) findViewById(R.id.mainBtnDate);
		etWeight = (EditText) findViewById(R.id.mainEditTextWeight);
		btnDateDecrease = (Button) findViewById(R.id.mainBtnDecreaseDay);
		btnDateIncrease = (Button) findViewById(R.id.mainBtnIncreaseDay);
		btnWeightDecrease = (Button) findViewById(R.id.mainBtnDecreaseWeight);
		btnWeightIncrease = (Button) findViewById(R.id.mainBtnIncreaseWeight);
		btnRecord = (Button) findViewById(R.id.mainBtnRecord);
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		if (hasFocus) {
			initialData();
		}
		super.onWindowFocusChanged(hasFocus);
	}

	@Override
	protected void onDestroy() {
		// DatabaseOpenHelper.uninitialize();
		super.onDestroy();
	}

	/**
	 * when the application started, request user set profile if it is empty
	 */
	private void requestInitialProfile() {
		Profile p = ProfileDao.getInstance().getDBProfile();
		if (null == p || !p.isValid()) {
			startActivity(profileActIntent);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		menu.add(0, MENU_ITEM_INPUT, 0, "录入体重").setIcon(
				android.R.drawable.ic_menu_add);
		menu.add(0, MENU_ITEM_LIST, 1, "列表").setIcon(
				android.R.drawable.ic_menu_agenda);
		// menu.add(0, MENU_ITEM_STATISTIC, 2, "统计").setIcon(
		// android.R.drawable.ic_menu_info_details);
		menu.add(0, MENU_ITEM_PROFILE, 3, "个人资料").setIcon(
				android.R.drawable.ic_menu_preferences);

		Intent intent = new Intent(null, getIntent().getData());
		intent.addCategory(Intent.CATEGORY_ALTERNATIVE);
		menu.addIntentOptions(Menu.CATEGORY_ALTERNATIVE, 0, 0,
				new ComponentName(this, WeightRecorderActivity.class), null,
				intent, 0, null);

		return true;
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_DATE:
			datePicker = new DatePickerDialog(this,
					new DatePickerDialog.OnDateSetListener() {
						public void onDateSet(DatePicker dp, int year,
								int month, int dayOfMonth) {
							String dateStr = String.format("%04d%02d%02d",
									year, month + 1, dayOfMonth);
							Date date = Record.parseDateString(dateStr);
							if (Calendar.getInstance().getTime().before(date)) {
								return;
							}
							record.setDate(dateStr);
							updateRecordUI();
						}
					}, record.getYear(), record.getMonth(), record.getDay());
			return datePicker;
		default:
			return super.onCreateDialog(id);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_ITEM_INPUT:
			setInputVisible();
			toggleLockInput.setChecked(true);
			return true;
		case MENU_ITEM_PROFILE:
			startActivity(profileActIntent);
			return true;
		case MENU_ITEM_LIST:
			startActivity(recordListActIntent);
			return true;
		case MENU_ITEM_STATISTIC:
			return true;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (isExiting == false) {
				isExiting = true;
				Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
				if (!hasTask) {
					exitTimer.schedule(exitTimerTask, 2000);
				}
			} else {
				finish();
				System.exit(0);
			}
			return true;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}

	private void updateRecordUI() {
		updateDateButtonText();
		updateRecordWithDate();
		updateWeightEditText();
	}

	private void addButton2Event() {
		Button b = (Button) findViewById(R.id.testbutton);
		b.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (View.VISIBLE == inputLayout.getVisibility()) {
					setInputGone();
				} else {
					setInputVisible();
				}
			}
		});
	}

	private void setInputGone() {
//		inputLayout.startAnimation(new MyScaler(1.0f, 1.0f, 1.0f, 0.0f, 500, inputLayout, true));
		inputLayout.setVisibility(View.GONE);
	}

	private void setInputVisible() {
		inputLayout.setVisibility(View.VISIBLE);
	}

	public class MyScaler extends ScaleAnimation {

		private View mView;

		private LayoutParams mLayoutParams;

		private int mMarginBottomFromY, mMarginBottomToY;

		private boolean mVanishAfter = false;

		public MyScaler(float fromX, float toX, float fromY, float toY,
				int duration, View view, boolean vanishAfter) {
			super(fromX, toX, fromY, toY);
			setDuration(duration);
			mView = view;
			mVanishAfter = vanishAfter;
			mLayoutParams = (LayoutParams) view.getLayoutParams();
			int height = mView.getHeight();
			mMarginBottomFromY = (int) (height * fromY)
					+ mLayoutParams.bottomMargin - height;
			mMarginBottomToY = (int) (0 - ((height * toY) + mLayoutParams.bottomMargin))
					- height;
		}

		@Override
		protected void applyTransformation(float interpolatedTime,
				Transformation t) {
			super.applyTransformation(interpolatedTime, t);
			if (interpolatedTime < 1.0f) {
				int newMarginBottom = mMarginBottomFromY
						+ (int) ((mMarginBottomToY - mMarginBottomFromY) * interpolatedTime);
				mLayoutParams.setMargins(mLayoutParams.leftMargin,
						mLayoutParams.topMargin, mLayoutParams.rightMargin,
						newMarginBottom);
				mView.getParent().requestLayout();
			} else if (mVanishAfter) {
				mView.setVisibility(View.GONE);
			}
		}

	}
}