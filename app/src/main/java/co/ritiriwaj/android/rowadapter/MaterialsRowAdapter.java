package co.ritiriwaj.android.rowadapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public class MaterialsRowAdapter extends ArrayAdapter<String> {
	Context context;
	List<String> objects;

	public MaterialsRowAdapter(Context context, List<String> objects) {
		super(context, android.R.layout.simple_list_item_1, android.R.id.text1,
				objects);
		this.context = context;
		this.objects = objects;
	}

}
