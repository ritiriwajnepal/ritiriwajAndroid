package co.ritiriwaj.android;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class UnderConstructionActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_under_construction);

		TextView header = (TextView) findViewById(R.id.headerText);
		TextView message = (TextView) findViewById(R.id.messageText);
		ImageView icon = (ImageView) findViewById(R.id.iconImageView);

		String text = getIntent().getExtras().getString("text");
		if (text.contentEquals("Games")) {
			header.setText("Ritiriwaj Games");
			message.setText("Provide entertainment for kids along with knowledge of traditions");
			icon.setImageDrawable(getResources().getDrawable(
					R.drawable.gamepad1));
		} else {
			header.setText("Ritiriwaj E-market");
			message.setText("Expand your business with us");
			icon.setImageDrawable(getResources().getDrawable(
					R.drawable.shopping_bag));
		}
	}
}
