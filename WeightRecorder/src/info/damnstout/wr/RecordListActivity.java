package info.damnstout.wr;

import info.damnstout.wr.dao.Record;
import info.damnstout.wr.dao.RecordDao;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class RecordListActivity extends ListActivity {

	private List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();

	private static final SimpleDateFormat listDateFormat = new SimpleDateFormat(
			"yyyy/MM/dd");

	private int editingPosition = 0;
	private String editingDate = Record.formatDate(Calendar.getInstance()
			.getTime());

	private RecordListAdapter recordListAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		data = getData();
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(getListView());
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.record_list_header);
		recordListAdapter = new RecordListAdapter(this);
		setListAdapter(recordListAdapter);
	}

	private List<Map<String, Object>> getData() {
		List<Map<String, Object>> rst = new ArrayList<Map<String, Object>>();
		for (Record r : RecordDao.getInstance().getRecords()) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("date", listDateFormat.format(r.getDateObj().getTime()));
			map.put("change", r.getChange());
			map.put("weight", r.getWeightStr());
			rst.add(map);
		}
		return rst;
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		editingPosition = position;
		Map<String, Object> recMap = (Map<String, Object>) data.get(position);
		try {
			editingDate = Record.formatDate(listDateFormat.parse(recMap.get(
					"date").toString()));
		} catch (ParseException e1) {
			editingDate = Record.formatDate(Calendar.getInstance().getTime());
		}
		Record rec = new Record();
		try {
			rec = new Record(editingDate,
					((Double) recMap.get("change")).doubleValue(),
					Double.parseDouble(recMap.get("weight").toString()));
		} catch (NumberFormatException e) {
		}
		new AlertDialog.Builder(this)
				.setTitle("是否删除以下记录：")
				.setMessage(
						String.format("%s：%s千克", rec.getDisplayDate(),
								rec.getWeightStr()))
				.setPositiveButton("删除", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						RecordDao.getInstance().delete(editingDate);
						data.remove(editingPosition);
						recordListAdapter.notifyDataSetChanged();
						WeightRecorderActivity.setChartDataChanged(true);
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				}).show();
		super.onListItemClick(l, v, position, id);
	}

	public final class ViewHolder {
		public TextView date;
		public TextView change;
		public TextView weight;
	}

	public class RecordListAdapter extends BaseAdapter {

		private LayoutInflater layoutInflater;

		public RecordListAdapter(Context context) {
			this.layoutInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return data.size();
		}

		@Override
		public Object getItem(int position) {
			return data.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (null == convertView) {
				holder = new ViewHolder();
				convertView = layoutInflater.inflate(R.layout.record_list_item,
						null);
				holder.date = (TextView) convertView
						.findViewById(R.id.recListTextviewDate);
				holder.change = (TextView) convertView
						.findViewById(R.id.recListTextviewChange);
				holder.weight = (TextView) convertView
						.findViewById(R.id.recListTextviewWeight);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.date.setText(data.get(position).get("date").toString());
			Double changeValue = (Double) data.get(position).get("change");
			holder.change.setText(String.format("%.1f",
					changeValue.doubleValue()));
			if (0 > changeValue) {
				holder.change.setTextColor(Color.GREEN);
			} else if (0 < changeValue) {
				holder.change.setTextColor(Color.RED);
			}
			holder.weight.setText(data.get(position).get("weight").toString());
			return convertView;
		}

	}
}
