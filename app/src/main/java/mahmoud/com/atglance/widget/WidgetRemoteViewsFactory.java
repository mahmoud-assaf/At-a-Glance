package mahmoud.com.atglance.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.ArrayList;

import mahmoud.com.atglance.Preferences;
import mahmoud.com.atglance.R;
import mahmoud.com.atglance.model.Article;
import mahmoud.com.atglance.rest.ApiService;
import mahmoud.com.atglance.service.WidgetUpdateService;

public class WidgetRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

	Preferences preferences;
	ApiService newsSevice;
	private Context mContext;
	private int appWidgetId;
	private ArrayList<Article> list = new ArrayList<Article>();
	// private ArrayList<String> list;

	public WidgetRemoteViewsFactory(Context applicationContext, Intent intent) {
		Log.e(" constructor : ", "WidgetRemoteViewsFactory");
		mContext = applicationContext;
		appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
			AppWidgetManager.INVALID_APPWIDGET_ID);
	}

	@Override
	public void onCreate() {
		Log.e(" onCreate: ", "WidgetRemoteViewsFactory");

		list = (ArrayList<Article>) WidgetUpdateService.list.clone();

	}

	@Override
	public void onDataSetChanged() {
		Log.e(" onDataSetChanged: ", "WidgetRemoteViewsFactory");
		list = (ArrayList<Article>) WidgetUpdateService.list.clone();

	}

	@Override
	public void onDestroy() {

	}

	@Override
	public int getCount() {
		Log.e(" getCount: ", String.valueOf(list.size()));
		return list == null ? 0 : list.size();
	}

	@Override
	public RemoteViews getViewAt(int position) {
		if (position == AdapterView.INVALID_POSITION ||
			list == null) {
			Log.e(" getViewAt: ", "list null or invalid position");
			return null;
		}
		Log.e(" getViewAt: ", "setting remote views");
		RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.widget_list_item);
		// rv.setTextViewText(R.id.textViewtitle, list.get(position).getTitle());
		rv.setTextViewText(R.id.textViewtitle, list.get(position).getTitle());
		//  rv.setTextViewText(R.id.textViewsource, list.get(position).getSource().getName());
		rv.setTextViewText(R.id.textViewsource, list.get(position).getSource().getName());

		return rv;
	}

	@Override
	public RemoteViews getLoadingView() {
		return null;
	}

	@Override
	public int getViewTypeCount() {
		return 1;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

}