package mahmoud.com.atglance.service;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.util.ArrayList;

import mahmoud.com.atglance.Preferences;
import mahmoud.com.atglance.model.Article;
import mahmoud.com.atglance.model.NewsResponse;
import mahmoud.com.atglance.rest.ApiService;
import mahmoud.com.atglance.rest.RetrofitClient;
import mahmoud.com.atglance.widget.ArticleWidgetProvider;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//credit https://laaptu.wordpress.com/2013/07/24/populate-appwidget-listview-with-remote-datadata-from-web/
//and other snippets

public class WidgetUpdateService extends Service {
	public static ArrayList<Article> list = new ArrayList<>();
	Preferences preferences;
	ApiService newsSevice;
	private int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
	
	/**
	 * Retrieve appwidget id from intent it is needed to update widget later
	 * initialize our AQuery class
	 */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.e(" onStartCommand: ", "WidgetUpdateService");
		newsSevice = RetrofitClient.getClient().create(ApiService.class);
		preferences = Preferences.getInstance(this);
		if (intent.hasExtra(AppWidgetManager.EXTRA_APPWIDGET_ID)) {
			appWidgetId = intent.getIntExtra(
				AppWidgetManager.EXTRA_APPWIDGET_ID,
				AppWidgetManager.INVALID_APPWIDGET_ID);
			Log.e(" onStartCommand: ", "appwidgetid=" + appWidgetId);
		}
		fetchData();
		return super.onStartCommand(intent, flags, startId);
	}
	
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
	private void fetchData() {
		Call<NewsResponse> call;
		
		call = newsSevice.getCountryCategoryNews("general", preferences.getKey("default_country_code"));
		
		call.enqueue(new Callback<NewsResponse>() {
			@Override
			public void onResponse(Call<NewsResponse> call, Response<NewsResponse> response) {
				
				//Log.e("code",String.valueOf(response.code()));
				
				if (response.code() > 200) {
					Log.e(" server error: ", "onresponse");
					return;
				}
				list = response.body().getArticles();
				// Log.e(" list:in service ", list.toString());
				Log.e(" completed", "success");
				populateWidget();
				
			}
			
			@Override
			public void onFailure(Call<NewsResponse> call, Throwable t) {
				// Log error here since request failed
				Log.e("ERROR", t.toString());
				
			}
		});
	}
	
	private void populateWidget() {
		
		Intent widgetUpdateIntent = new Intent();
		widgetUpdateIntent.setAction(ArticleWidgetProvider.DATA_FETCHED);
		widgetUpdateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
			appWidgetId);
		sendBroadcast(widgetUpdateIntent);
		
		this.stopSelf();
	}
}
