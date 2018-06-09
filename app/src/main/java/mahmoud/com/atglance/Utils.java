package mahmoud.com.atglance;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import mahmoud.com.atglance.activity.IntroActivity;
import mahmoud.com.atglance.activity.MainActivity;

/**
 * Created by mahmoud on 27/01/2018.
 */

public class Utils {
	public Context context;
	
	public Utils(Context context) {
		this.context = context;
	}
	
	public boolean isOnline() {
		ConnectivityManager cm =
			(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		return netInfo != null && netInfo.isConnectedOrConnecting();
	}
	
	public void showNotification(String title, String category) {
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		
		int notificationId = 1;
		String channelId = "channel-01";
		String channelName = context.getString(R.string.app_name);
		int importance = NotificationManager.IMPORTANCE_DEFAULT;
		
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
			NotificationChannel mChannel = new NotificationChannel(
				channelId, channelName, importance);
			notificationManager.createNotificationChannel(mChannel);
		}
		
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, channelId)
			.setSmallIcon(R.drawable.ic_feeds)
			.setContentTitle(context.getString(R.string.app_name) + " -" + category)
			.setAutoCancel(true)
			
			.setContentText(title);
		Intent resultIntent = new Intent(context, MainActivity.class);
		resultIntent.putExtra("category", category);
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
			0,
			PendingIntent.FLAG_UPDATE_CURRENT
		);
		mBuilder.setContentIntent(resultPendingIntent);
		mBuilder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE);
		
		notificationManager.notify(notificationId, mBuilder.build());
	}
	
	public void showCountriesDialog() {
		
		// setup the alert builder
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(context.getString(R.string.choose_country));
		
		// add a list
		final String[] countries = context.getResources().getStringArray(R.array.countries_names);
		final String[] countriesCodes = context.getResources().getStringArray(R.array.countries_codes);
		builder.setItems(countries, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
				Preferences.getInstance(context).saveKey(Preferences.KEY_DEFAULT_COUNTRY_CODE, countriesCodes[which]);
				Preferences.getInstance(context).saveKey(Preferences.KEY_DEFAULT_COUNTRY_NAME, countries[which]);
				context.startActivity(new Intent(context, MainActivity.class));
				((IntroActivity) context).finish();
			}
		});
		
		// create and show the alert dialog
		AlertDialog dialog = builder.create();
		dialog.show();
	}
	
	public void showEditAlertsDialog() {
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final View dialogView = inflater.inflate(R.layout.custom_alerts_dialog, null);
		dialogBuilder.setView(dialogView);
		
		final EditText keywords = (EditText) dialogView.findViewById(R.id.keywords_edittxt);
		keywords.setText(Preferences.getInstance(context).getKey("keywords") == "null" ? "" : Preferences.getInstance(context).getKey("keywords"));
		dialogBuilder.setTitle(context.getString(R.string.alerts));
		
		dialogBuilder.setPositiveButton(context.getString(R.string.done), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				Preferences.getInstance(context).saveKey("keywords", keywords.getText().toString().toLowerCase());
				Log.e(" keyword: ", keywords.getText().toString());
				Toast.makeText(context, context.getString(R.string.alerts_saved), Toast.LENGTH_SHORT).show();
				
			}
		});
		dialogBuilder.setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				//pass
			}
		});
		AlertDialog b = dialogBuilder.create();
		b.show();
	}
	
	public void showAboutUsDialog() {
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final View dialogView = inflater.inflate(R.layout.about_us_custom, null);
		dialogBuilder.setView(dialogView);
		
		dialogBuilder.setTitle(context.getString(R.string.about));
		
		AlertDialog b = dialogBuilder.create();
		b.show();
	}
}
