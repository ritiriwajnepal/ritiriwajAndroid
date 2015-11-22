package co.ritiriwaj.android.rowadapter;

import java.util.List;

import co.ritiriwaj.android.R;
import co.ritiriwaj.android.R.id;
import co.ritiriwaj.android.R.layout;
import co.ritiriwaj.android.model.Procedure;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ProcedureRowAdapter extends ArrayAdapter<Procedure> {

	Context context;

	public ProcedureRowAdapter(Context context, int resourceId,
			List<Procedure> items) {
		super(context, resourceId, items);
		this.context = context;
	}

	/* private view holder class */
	private class ViewHolder {
		TextView txtSerialNo;
		TextView txtTitle;

	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		Procedure procedureRowItem = getItem(position);

		LayoutInflater mInflater = (LayoutInflater) context
				.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.row_procedure, null);
			holder = new ViewHolder();
			holder.txtSerialNo = (TextView) convertView
					.findViewById(R.id.serial_no);
			holder.txtTitle = (TextView) convertView
					.findViewById(R.id.procedureTitle);
			
			convertView.setTag(holder);
		} else
			holder = (ViewHolder) convertView.getTag();

		int serialNo = position + 1;

		holder.txtSerialNo.setText(convertToNepaleseNumber(
				String.valueOf(serialNo), context));
		holder.txtTitle.setText(procedureRowItem.getTitle());

		return convertView;
	}

	public static String convertToNepaleseNumber(String str, Context context) {
		String[] nepaleseChars = context.getResources().getStringArray(
				R.array.neaplese_number);
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < str.length(); i++) {
			if (Character.isDigit(str.charAt(i))) {
				builder.append(nepaleseChars[(int) (str.charAt(i)) - 48]);
			} else {
				builder.append(str.charAt(i));
			}
		}
		return builder.toString();
	}
}