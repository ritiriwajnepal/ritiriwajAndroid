package co.ritiriwaj.android.rowadapter;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import co.ritiriwaj.android.MainActivity;
import co.ritiriwaj.android.R;
import co.ritiriwaj.android.ReasonsAndCommentsActivity;

public class CommentRowAdapter extends ArrayAdapter<HashMap<String, String>> {
	Context context;
	List<HashMap<String, String>> list;
	SharedPreferences sharedPreferences;
	String DELETE_COMMENT = MainActivity.IP + "deleteReasonComment";
	AsyncHttpClient client = new AsyncHttpClient();

	public CommentRowAdapter(Context context,
			List<HashMap<String, String>> objects) {
		super(context, R.layout.row_comment, R.id.commentText, objects);
		this.context = context;
		this.list = objects;
		sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(context);
	}

	private class ViewHolder {
		// ImageView profileImage;
		TextView commentText;
		TextView commenterName;
		ImageButton commentDelete;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		ViewHolder holder = new ViewHolder();
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.row_comment, parent, false);
			holder.commentText = (TextView) convertView
					.findViewById(R.id.commentText);
			// holder.profileImage = (ImageView) convertView
			// .findViewById(R.id.profileImage);
			holder.commenterName = (TextView) convertView
					.findViewById(R.id.commenterName);
			holder.commentDelete = (ImageButton) convertView
					.findViewById(R.id.delCommentImageButton);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		// File imageFile = getImage(list.get(position).get("userId"));
		// Bitmap bitmap =
		// BitmapFactory.decodeFile(imageFile.getAbsolutePath());
		// if (bitmap == null) {
		// holder.profileImage.setImageResource(R.drawable.ic_launcher);
		// } else {
		// holder.profileImage.setImageBitmap(bitmap);
		// }
		holder.commentText.setText(list.get(position).get("text"));
		holder.commenterName.setText(list.get(position).get("full_name"));
		if (sharedPreferences.getString("subscriberFullName", "")
				.contentEquals(list.get(position).get("full_name"))) {
			holder.commentDelete.setVisibility(View.VISIBLE);
			holder.commentDelete.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					HashMap<String, String> map = new HashMap<String, String>();
					map.put("subscriberId",
							sharedPreferences.getString("subscriberId", ""));
					map.put("commentId", list.get(position).get("commentId"));
					RequestParams params = new RequestParams(map);
					showAlertDialog(context,
							"Do you want to delete this comment?", params,
							position);
				}
			});
		}
		return convertView;
	}

	private void deleteComment(RequestParams params, final int position) {
		client.post(context, DELETE_COMMENT, params,
				new JsonHttpResponseHandler() {
					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						try {
							if (response.getString("response").contentEquals(
									"success")) {
								list.remove(position);
								((ReasonsAndCommentsActivity) context)
										.setComments(list);
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}

					@Override
					public void onFailure(int statusCode, Header[] headers,
							String responseString, Throwable throwable) {
						Log.i("delete comment ", responseString);
						Toast.makeText(context, throwable.getMessage(),
								Toast.LENGTH_LONG).show();
					}
				});
	}

	private void showAlertDialog(final Context context, String message,
			final RequestParams params, final int position) {
		final AlertDialog.Builder builder = new AlertDialog.Builder(context);

		builder.setMessage(message)
				.setCancelable(false)
				.setPositiveButton("Delete",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								deleteComment(params, position);
								arg0.dismiss();
							}
						})
				.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								arg0.dismiss();
								((Activity) context).finish();
							}
						});
		Log.i("builder", "dialog");
		final AlertDialog alert = builder.create();
		alert.show();
	}

	public static File getImage(String userID) {

		File mediaImage = null;
		try {
			String root = Environment.getExternalStorageDirectory().toString();
			File myDir = new File(root + "/RitiRiwaj/images/");
			if (!myDir.exists())
				return null;

			mediaImage = new File(myDir.getPath() + "/" + userID + ".jpg");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return mediaImage;
	}
}
