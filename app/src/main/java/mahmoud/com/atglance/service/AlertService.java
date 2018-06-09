package mahmoud.com.atglance.service;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.util.Log;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import mahmoud.com.atglance.Preferences;
import mahmoud.com.atglance.Utils;
import mahmoud.com.atglance.model.Article;
import mahmoud.com.atglance.model.NewsResponse;
import mahmoud.com.atglance.rest.ApiService;
import mahmoud.com.atglance.rest.RetrofitClient;
import mahmoud.com.atglance.widget.ArticleWidgetProvider;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AlertService extends JobService implements Callback<NewsResponse> {
	public final String TAG = AlertService.this.getClass().getSimpleName();
	
	Preferences preferences;
	ApiService newsSevice;
	Utils utils;
	String[] keywordsArray;
	String[] categories = {"general", "sports", "science", "health", "entertainment", "business", "technology"};
	
	@Override
	public boolean onStartJob(JobParameters job) {
		
		Log.e(TAG + " onStartJob: ", "started alert service");
		Intent serviceIntent = new Intent(this, WidgetUpdateService.class);
		
		int ids[] = AppWidgetManager.getInstance(this).getAppWidgetIds(new ComponentName(this, ArticleWidgetProvider.class));
		if (ids.length > 0) {
			Log.e(TAG + " widgets ids ", Arrays.toString(ids));
			serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
				ids[0]);
			startService(serviceIntent);
		}
		preferences = Preferences.getInstance(this);
		if (preferences.getKey(Preferences.KEY_MUTE_ALERTS).equals("yes"))
			return false;
		long lastTimeRun = Long.valueOf(preferences.getKey(Preferences.KEY_SERVICE_LAST_TIME_RUN).equals("null") ? "0" : preferences.getKey(Preferences.KEY_SERVICE_LAST_TIME_RUN));
		if (Calendar.getInstance().getTimeInMillis() - lastTimeRun < (10 * 60 * 1000)) { //minimum time to get updates 10 minutes
			Log.e(TAG + " onStartJob: ", "returned before 15 minutes");
			return false;
			
		}
		newsSevice = RetrofitClient.getClient().create(ApiService.class);
		utils = new Utils(this);
		String keywordsSaved = preferences.getKey(Preferences.KEY_KEYWORDS);
		if (keywordsSaved.equals("null") || keywordsSaved.equals(""))  //no alert keywords
			return false;
		keywordsArray = keywordsSaved.split(",");
		
		Call<NewsResponse> call;
		for (String category : categories) {
			call = newsSevice.getCountryCategoryNews(category, preferences.getKey(Preferences.KEY_DEFAULT_COUNTRY_CODE));
			call.enqueue(this);
			//	Log.e(TAG + " onStartJob: enqueued ", category);
		}
		
		preferences.saveKey(Preferences.KEY_SERVICE_LAST_TIME_RUN, String.valueOf(Calendar.getInstance().getTimeInMillis()));
		return false;
	}
	
	@Override
	public boolean onStopJob(JobParameters job) {
		return false;
	}
	
	@Override
	public void onResponse(Call<NewsResponse> call, Response<NewsResponse> response) {
		
		String requestType = getRequestType(call);
		
		if (response.code() > 200) {
			Log.e("failed with code", String.valueOf(response.code()));
			
			return;
		}
		
		ArrayList<Article> articles = response.body().getArticles();
		Log.e("request", " total : " + String.valueOf(articles.size()));
		
		if (articles != null && articles.size() > 0) {
			Log.e(TAG, " searching key words in category : " + requestType);
			for (int i = 0; i < keywordsArray.length; i++) {
				for (int j = 0; j < articles.size(); j++) {
					if (articles.get(j).getTitle().toLowerCase().contains(keywordsArray[i])) {
						Log.e(TAG + " found keyword : ", keywordsArray[i] + "------" + articles.get(j).getTitle());
						if (preferences.getKey(keywordsArray[i]).compareTo(articles.get(j).getPublishedAt()) > 0)   //if not same time then it's new headline
						{
							utils.showNotification(articles.get(j).getTitle(), requestType);
							preferences.saveKey(keywordsArray[i], articles.get(j).getPublishedAt());
							
						} else
							Log.e(TAG + " keyword " + keywordsArray[i], "same article skip");
					}
				}
				
			}
			
		}
		
	}
	
	@Override
	public void onFailure(Call<NewsResponse> call, Throwable t) {
		Log.e("alertService ERROR", t.toString());
		
	}
	
	public String getRequestType(Call<NewsResponse> call) {
		if (call.request().toString().contains("general"))
			return "general";
		
		else if (call.request().toString().contains("business"))
			return "business";
		
		else if (call.request().toString().contains("sports"))
			return "sports";
		
		else if (call.request().toString().contains("science"))
			return "science";
		
		else if (call.request().toString().contains("technology"))
			return "technology";
		
		else if (call.request().toString().contains("health"))
			return "health";
		
		else if (call.request().toString().contains("entertainment"))
			return "entertainment";
		else
			return "";
	}
	
}
