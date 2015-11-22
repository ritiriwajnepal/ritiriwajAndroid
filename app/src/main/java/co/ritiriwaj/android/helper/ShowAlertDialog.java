package co.ritiriwaj.android.helper;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import co.ritiriwaj.android.R;

public class ShowAlertDialog extends DialogFragment {

	Context context;
	String message;
	TextView messageTextView;
	AlertDialog.Builder builder;

//	public ShowAlertDialog() {
//	}

	public ShowAlertDialog(Context context, String message) {
		this.context = context;
		this.message = message;
	}

	public void setMessage(String message) {
		this.messageTextView.setText(message);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the Builder class for convenient dialog construction
		builder = new AlertDialog.Builder(context);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.loading_screen_layout,
				(ViewGroup) ((Activity) context).getWindow().getDecorView()
						.findViewById(android.R.id.content), false);

		messageTextView = (TextView) view.findViewById(R.id.loadingTextView);
		messageTextView.setText(message);

		ImageView loadImageView = (ImageView) view
				.findViewById(R.id.loadingImageView);
		Animation rotateAnimation = AnimationUtils.loadAnimation(context,
				R.anim.rotate);
		loadImageView.startAnimation(rotateAnimation);

		builder.setView(view);

		// Create the AlertDialog object and return it
		return builder.create();
	}
}
