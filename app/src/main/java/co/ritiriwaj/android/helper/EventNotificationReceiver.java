package co.ritiriwaj.android.helper;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import co.ritiriwaj.android.MainActivity;
import co.ritiriwaj.android.R;
import co.ritiriwaj.android.SplashScreen;

public class EventNotificationReceiver extends BroadcastReceiver {

	int mId = 1001;

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i("notification", "arrived");
		Bundle bundle = intent.getExtras();
		String title = bundle.getString("title");
		String message = bundle.getString("message");

		Uri uri = RingtoneManager
				.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

		Intent resultIntent = new Intent(context, SplashScreen.class);

		NotificationCompat.Builder builder = new NotificationCompat.Builder(
				context).setSmallIcon(R.drawable.ic_launcher)
				.setPriority(NotificationCompat.PRIORITY_HIGH).setSound(uri)
				.setContentText(title).setContentText(message);

		TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
		stackBuilder.addParentStack(MainActivity.class);
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,
				PendingIntent.FLAG_UPDATE_CURRENT);

		builder.setContentIntent(resultPendingIntent);

		NotificationManager managerCompat = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		managerCompat.notify(mId, builder.build());
	}

}
