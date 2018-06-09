package mahmoud.com.atglance.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import mahmoud.com.atglance.R;
import mahmoud.com.atglance.service.WidgetUpdateService;

public class ArticleWidgetProvider extends AppWidgetProvider {
	public static final String DATA_FETCHED = "mahmoud.com.atglance.DATA_FETCHED";
	
	private RemoteViews updateWidgetListView(Context context,
	                                         int appWidgetId) {
		Log.e(" updateWidgetListView: ", "widget id" + appWidgetId);
		//which layout to show on widget
		RemoteViews remoteViews = new RemoteViews(
			context.getPackageName(), R.layout.widget);
		
		//RemoteViews Service needed to provide adapter for ListView
		Intent svcIntent = new Intent(context, WidgetRemoteViewsService.class);
		//passing app widget id to that RemoteViews Service
		svcIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
		
		//setting adapter to listview of the widget
		remoteViews.setRemoteAdapter(R.id.headlines_listview, svcIntent);
		
		return remoteViews;
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
		if (intent.getAction().equals(DATA_FETCHED)) {
			int appWidgetId = intent.getIntExtra(
				AppWidgetManager.EXTRA_APPWIDGET_ID,
				AppWidgetManager.INVALID_APPWIDGET_ID);
			Log.e(" widgets id is  ", String.valueOf(appWidgetId));
			AppWidgetManager appWidgetManager = AppWidgetManager
				.getInstance(context);
			RemoteViews remoteViews = updateWidgetListView(context, appWidgetId);
			appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.headlines_listview);
			
			appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
		}
		
	}
	
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		final int N = appWidgetIds.length;
		Log.e(" onUpdate: ", "called");
		
		for (int i = 0; i < N; ++i) {
			Intent serviceIntent = new Intent(context, WidgetUpdateService.class);
			serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
				appWidgetIds[i]);
			context.startService(serviceIntent);
		}
		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}
}
