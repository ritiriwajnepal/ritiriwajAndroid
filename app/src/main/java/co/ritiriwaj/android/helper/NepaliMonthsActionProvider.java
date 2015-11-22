package co.ritiriwaj.android.helper;

import org.apache.http.Header;
import org.json.JSONArray;

import android.content.Context;
import android.support.v4.view.ActionProvider;
import android.util.Log;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.SubMenu;
import android.view.View;
import android.widget.Toast;
import co.ritiriwaj.android.MainActivity;
import co.ritiriwaj.android.R;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

public class NepaliMonthsActionProvider extends ActionProvider implements
		OnMenuItemClickListener {

	Context context;
	private String GET_CALENDAR_RITUAL_BY_MONTH = MainActivity.IP
			+ "getCalendarRitualsByNepaliMonth/";
	private AsyncHttpClient asyncHttpClient = new AsyncHttpClient();

	public NepaliMonthsActionProvider(Context context) {

		super(context);
		this.context = context;

	}

	@Override
	public View onCreateActionView() {

		return null;

	}

	@Override
	public boolean hasSubMenu() {
		return true;
	}

	@Override
	public void onPrepareSubMenu(SubMenu subMenu) {
		subMenu.clear();

		subMenu.add(SubMenu.NONE, 1, SubMenu.NONE, R.string.Baishakh)
				.setOnMenuItemClickListener(this);
		subMenu.add(SubMenu.NONE, 2, SubMenu.NONE, R.string.Jestha)
				.setOnMenuItemClickListener(this);
		subMenu.add(SubMenu.NONE, 3, SubMenu.NONE, R.string.Ashadh)
				.setOnMenuItemClickListener(this);
		subMenu.add(SubMenu.NONE, 4, SubMenu.NONE, R.string.Shrawan)
				.setOnMenuItemClickListener(this);
		subMenu.add(SubMenu.NONE, 5, SubMenu.NONE, R.string.Bhadra)
				.setOnMenuItemClickListener(this);
		subMenu.add(SubMenu.NONE, 6, SubMenu.NONE, R.string.Ashwin)
				.setOnMenuItemClickListener(this);
		subMenu.add(SubMenu.NONE, 7, SubMenu.NONE, R.string.Kartik)
				.setOnMenuItemClickListener(this);
		subMenu.add(SubMenu.NONE, 8, SubMenu.NONE, R.string.Mangsir)
				.setOnMenuItemClickListener(this);
		subMenu.add(SubMenu.NONE, 9, SubMenu.NONE, R.string.Poush)
				.setOnMenuItemClickListener(this);
		subMenu.add(SubMenu.NONE, 10, SubMenu.NONE, R.string.Magh)
				.setOnMenuItemClickListener(this);
		subMenu.add(SubMenu.NONE, 11, SubMenu.NONE, R.string.Falgun)
				.setOnMenuItemClickListener(this);
		subMenu.add(SubMenu.NONE, 12, SubMenu.NONE, R.string.Chaitra)
				.setOnMenuItemClickListener(this);
	}

	@Override
	public boolean onMenuItemClick(MenuItem item) {

		Log.i("LINK", GET_CALENDAR_RITUAL_BY_MONTH + item.getItemId());

		asyncHttpClient.get(GET_CALENDAR_RITUAL_BY_MONTH + item.getItemId(),
				new JsonHttpResponseHandler() {
					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONArray response) {

						if (!response.toString().equals("[]")) {

							Toast.makeText(context, response.toString(),
									Toast.LENGTH_LONG).show();

						}

						super.onSuccess(statusCode, headers, response);
					}

				});

		return true;
	}

}
