package co.ritiriwaj.android.rowadapter;

import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import co.ritiriwaj.android.R;

public class PackageRowAdapter extends ArrayAdapter<HashMap<String, String>>{

	Context context;
	List<HashMap<String, String>> objects;
	ViewHolder holder;

	public PackageRowAdapter(Context context,
			List<HashMap<String, String>> objects) {
		super(context, R.layout.materials_row_layout, R.id.materialNameTxtView,
				objects);
		this.context = context;
		this.objects = objects;
	}

	public class ViewHolder {
		TextView materialUnit;
		TextView materialName;
		String materialId;

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

		holder = new ViewHolder();
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.materials_row_layout,
					parent, false);
			holder.materialName = (TextView) convertView
					.findViewById(R.id.materialNameTxtView);
			holder.materialUnit = (TextView) convertView
					.findViewById(R.id.materialUnitTxtView);
			

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.materialName.setText(objects.get(position).get("materialName"));
		holder.materialUnit.setText(objects.get(position).get("materialUnit"));
		holder.materialId = objects.get(position).get("materialId");

		return convertView;
	}

}
