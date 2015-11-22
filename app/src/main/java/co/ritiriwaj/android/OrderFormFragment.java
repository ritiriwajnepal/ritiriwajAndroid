package co.ritiriwaj.android;

import java.util.HashMap;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class OrderFormFragment extends DialogFragment implements
		OnClickListener {

	View view;
	Button btnSubmitOrder;
	Button btnCancelOrder;

	private String packageId;
	private String fullName;
	private String phone;
	private String deliveryAddress;

	private static AsyncHttpClient clientPackageOrder = new AsyncHttpClient();

	public static OrderFormFragment newInstance(Bundle args) {

		OrderFormFragment orderFormFragment = new OrderFormFragment();
		orderFormFragment.setArguments(args);
		return orderFormFragment;

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		int style = DialogFragment.STYLE_NORMAL, theme = android.R.style.Theme_Holo_Light_Dialog;
		setStyle(style, theme);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		view = inflater.inflate(R.layout.fragment_order_package, container,
				false);
		getDialog().setTitle("Order Package");
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		super.onActivityCreated(savedInstanceState);
		btnSubmitOrder = (Button) view.findViewById(R.id.btn_confirm_order);
		btnSubmitOrder.setOnClickListener(this);

		packageId = getArguments().getString("packageId");

	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.btn_confirm_order:

			fullName = ((EditText) view
					.findViewById(R.id.txt_delivery_full_name)).getText()
					.toString();
			phone = ((EditText) view.findViewById(R.id.txt_delivery_phone))
					.getText().toString();
			deliveryAddress = ((EditText) view
					.findViewById(R.id.txt_delivery_address)).getText()
					.toString();

			HashMap<String, String> packageOrder = new HashMap<String, String>();
			packageOrder.put("packageId", packageId);
			packageOrder.put("fullName", fullName);
			packageOrder.put("phone", phone);
			packageOrder.put("deliveryAddress", deliveryAddress);

			Log.i("Hashmap_PackageOrder", packageOrder.toString());
			RequestParams params = new RequestParams(packageOrder);

			clientPackageOrder.post(MainActivity.IP + "postPackageOrder/"
					+ packageId, params, new JsonHttpResponseHandler() {
				@Override
				public void onSuccess(int statusCode, Header[] headers,
						JSONObject response) {
					try {

						if (response.getString("response").equalsIgnoreCase(
								"success")) {

							dismiss();
							Toast.makeText(
									getActivity(),
									"Order Successful! We'll contact you within next 48 hours.",
									Toast.LENGTH_LONG).show();

						} else {

							dismiss();
							Toast.makeText(getActivity(),
									"Something went wrong. Please Try Again!",
									Toast.LENGTH_LONG).show();

						}

					} catch (JSONException e) {
						e.printStackTrace();
					}

				}

				@Override
				public void onFailure(int statusCode, Header[] headers,
						Throwable throwable, JSONArray errorResponse) {
					Log.i("OrderForm", "Post Failure.");

				}
			});

			break;

		case R.id.btn_cancel_order:
			dismiss();
			break;

		default:
			break;
		}

	}
}
