package mahmoud.com.atglance.activity;

import android.Manifest;
import android.app.ActivityOptions;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresPermission;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.transition.Transition;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.snapshot.WeatherResult;
import com.google.android.gms.awareness.state.Weather;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.util.ArrayUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import mahmoud.com.atglance.Preferences;
import mahmoud.com.atglance.R;
import mahmoud.com.atglance.Utils;
import mahmoud.com.atglance.adapter.ArticlesAdapter;
import mahmoud.com.atglance.adapter.ViewPageAdapter;
import mahmoud.com.atglance.fragment.SavedArticlesFragment;
import mahmoud.com.atglance.fragment.SubscriptionsFragment;
import mahmoud.com.atglance.fragment.TopFeedsFragment;
import mahmoud.com.atglance.model.Article;
import mahmoud.com.atglance.service.AlertService;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, View.OnClickListener {
	private static final int GET_WEATHER_PERMISSION_REQUEST_CODE = 1231;
	public FirebaseJobDispatcher dispatcher;
	public ViewPageAdapter adapter;
	public ArrayList<Article> searchresult;
	public String currentCategory = "general";
	public ArticlesAdapter fragmentAdapter;
	public ArrayList<Article> articles;
	public int[] weatherIcons;
	public ImageView[] weatherImages;
	Utils utils;
	Toolbar toolbar;
	TabLayout tabLayout;
	AppBarLayout appBarLayout;
	ViewPager viewPager;
	HorizontalScrollView categoriesView;
	WeatherConfig weatherConfig;
	TextView categoryGeneral, categorySports, categoryHealth,
		categoryScience, categoryBusiness,
		categoryEntertainment, categoryTechnology;
	TextView lastClicked;
	View weatherView;
	private GoogleApiClient mGoogleApiClient;
	private DrawerLayout mDrawerLayout;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Preferences.getInstance(this).saveKey(Preferences.KEY_FIRST_TIME_RUN, "no");
		
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
			
			getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
			Transition exitTrans = new Fade();
			getWindow().setExitTransition(exitTrans);
			
			Transition reenterTrans = new Fade();
			getWindow().setReenterTransition(reenterTrans);
		}
		weatherConfig = new WeatherConfig();
		setContentView(R.layout.activity_main);
		MobileAds.initialize(this, "ca-app-pub-8876457188000522~7467993962");
		if (getIntent().hasExtra("category")) {
			currentCategory = getIntent().getStringExtra("category");
			//  Log.e("from not: ", "yes");
		}
		buildApiClient();
		toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		ActionBar actionbar = getSupportActionBar();
		
		actionbar.setDisplayHomeAsUpEnabled(true);
		//  actionbar.setHomeButtonEnabled(true);
		actionbar.setHomeAsUpIndicator(R.drawable.menu);
		
		findAndSetViews();
		viewPager = findViewById(R.id.viewpager);
		setupViewPager(viewPager);
		getSupportActionBar().setSubtitle(getString(R.string.general_category));
		utils = new Utils(this);
		sartAlertService();
		setTopFeedsTo(currentCategory);
		
	}
	
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		//mDrawerToggle.syncState();
	}
	
	@Override
	public void onConnected(@Nullable final Bundle bundle) {
		if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(
				MainActivity.this,
				new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
				GET_WEATHER_PERMISSION_REQUEST_CODE);
			Log.e(" onconnected ", "not granted");
		} else {
			getWeather();
		}
	}
	
	@Override
	public void onConnectionSuspended(final int i) {
		new AlertDialog.Builder(this)
			.setMessage(R.string.error_connect_api_service)
			.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(final DialogInterface dialogInterface, final int i) {
					finish();
				}
			}).show();
	}
	
	@RequiresPermission("android.permission.ACCESS_FINE_LOCATION")
	private void getWeather() {
		//noinspection MissingPermission
		if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			Log.e(" getWeather: ", "requeting permissions ");
			ActivityCompat.requestPermissions(
				MainActivity.this,
				new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
				GET_WEATHER_PERMISSION_REQUEST_CODE);
		} else {
			Log.e(" getWeather: ", "permissions is granted ");
			Awareness.SnapshotApi.getWeather(mGoogleApiClient)
				.setResultCallback(new ResultCallback<WeatherResult>() {
					@Override
					public void onResult(@NonNull WeatherResult weatherResult) {
						if (!weatherResult.getStatus().isSuccess()) {
							Log.e(" getweather() ", "some error");
							//Toast.makeText(MainActivity.this, R.string.weather_error, Toast.LENGTH_LONG).show();
							((CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar)).setBackgroundResource(R.drawable.error_weather);
							((TextView) findViewById(R.id.temprature)).setVisibility(View.INVISIBLE);
							((TextView) findViewById(R.id.humidity)).setVisibility(View.INVISIBLE);
							
							((TextView) findViewById(R.id.condition)).setTextSize(10);
							((TextView) findViewById(R.id.condition)).setText(R.string.weather_info_error);
							//	weatherView.setVisibility(View.GONE);
							
							return;
						}
						
						//parse and display current weather status
						Weather weather = weatherResult.getWeather();
						if (weather != null) {
							int weatherUnit;
							String unit = Preferences.getInstance(MainActivity.this).getKey("default_unit");
							if (unit.equals("celsius"))
								weatherUnit = Weather.CELSIUS;
							else
								weatherUnit = Weather.FAHRENHEIT;
							setWeatherHeaderInfo(weather.getConditions(), weather.getTemperature(weatherUnit), weather.getHumidity());
							
						}
						
					}
				});
		}
		
	}
	
	public void setWeatherHeaderInfo(int[] conditions, float temp, int humidity) {
		weatherImages = new ImageView[10];
		weatherImages[0] = findViewById(R.id.no_image);
		weatherImages[1] = findViewById(R.id.clear_image);
		weatherImages[2] = findViewById(R.id.cloudy_image);
		weatherImages[3] = findViewById(R.id.foggy_image);
		weatherImages[4] = findViewById(R.id.hazyy_image);
		weatherImages[5] = findViewById(R.id.icy_image);
		weatherImages[6] = findViewById(R.id.rainy_image);
		weatherImages[7] = findViewById(R.id.snowy_image);
		weatherImages[8] = findViewById(R.id.stormy_image);
		weatherImages[9] = findViewById(R.id.windy_image);
		
		TextView tempTxt = findViewById(R.id.temprature);
		TextView humidityTxt = findViewById(R.id.humidity);
		
		tempTxt.setText(String.valueOf(Math.round(temp)));
		if (Preferences.getInstance(this).getKey(Preferences.KEY_DEFAULT_UNIT).equals("celsius"))
			tempTxt.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.celsius), null);
		else
			tempTxt.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.fahr), null);
		humidityTxt.setText(String.valueOf(humidity) + " %");
		String conditionsStr = "";
		Log.e("weather array", "arr: " + Arrays.toString(conditions));
		
		((CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar)).setBackgroundResource(getHeaderBitmap());
		for (int i : weatherConfig.conditionsCodes) {
			if (ArrayUtils.contains(conditions, i)) {
				conditionsStr += weatherConfig.conditionsDescription[i] + "  ";
				weatherImages[i].setImageResource(weatherIcons[i]);
				weatherImages[i].setVisibility(View.VISIBLE);
			}
			
		}
		
		((TextView) findViewById(R.id.condition)).setText(conditionsStr);
		// ((ConstraintLayout)findViewById(R.id.header_view)).setBackgroundResource(R.drawable.bgheader);
		
	}
	
	private void buildApiClient() {
		mGoogleApiClient = new GoogleApiClient.Builder(this)
			.addApi(Awareness.API)
			.addConnectionCallbacks(this)
			.build();
		mGoogleApiClient.connect();
	}
	
	private void setupViewPager(ViewPager viewPager) {
		
		adapter = new ViewPageAdapter(
			getSupportFragmentManager());
		adapter.addFrag(TopFeedsFragment.newInstance(), getString(R.string.latest));
		adapter.addFrag(SubscriptionsFragment.newInstance(), getString(R.string.subscriptions));
		adapter.addFrag(SavedArticlesFragment.newInstance(), getString(R.string.saved));
		
		viewPager.setAdapter(adapter);
		tabLayout.setupWithViewPager(viewPager);
		TextView tabOne;
		
		Drawable icons[] = {ContextCompat.getDrawable(this, R.drawable.ic_feeds_small), ContextCompat.getDrawable(this, R.drawable.ic_subs_small), ContextCompat.getDrawable(this, R.drawable.ic_save_small)};
		for (int i = 0; i < 3; i++) {
			tabOne = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
			tabOne.setText(adapter.getPageTitle(i));
			tabOne.setCompoundDrawablesWithIntrinsicBounds(icons[i], null, null, null);
			tabLayout.getTabAt(i).setCustomView(tabOne);
		}
		
		((TopFeedsFragment) adapter.getItem(0)).setCurrentCategory("general");
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_main, menu);
		MenuItem search = menu.findItem(R.id.search);
		SearchView searchView = (SearchView) MenuItemCompat.getActionView(search);
		searchView.setQueryHint(getString(R.string.search_current_news));
		searchView.setOnSearchClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.e("searchview", "clicked");
				
			}
		});
		search(searchView);
		return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			
			case android.R.id.home:
				mDrawerLayout.openDrawer(GravityCompat.START);  // OPEN DRAWER
				return true;
			
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void search(SearchView searchView) {
		
		searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String query) {
				//Log.e("searchview",query);
				
				return false;
			}
			
			@Override
			public boolean onQueryTextChange(String newText) {
				// Log.e("searchview",newText);
				//  mAdapter.getFilter().filter(newText);
				android.support.v4.app.Fragment mFragment = adapter.getItem(viewPager.getCurrentItem());
				
				if (mFragment instanceof TopFeedsFragment) {
					fragmentAdapter = ((TopFeedsFragment) mFragment).getAdapter();
					articles = ((TopFeedsFragment) mFragment).getArticles();
					
				} else if (mFragment instanceof SubscriptionsFragment) {
					fragmentAdapter = ((SubscriptionsFragment) mFragment).getAdapter();
					articles = ((SubscriptionsFragment) mFragment).getArticles();
					
				} else {
					fragmentAdapter = ((SavedArticlesFragment) mFragment).getAdapter();
					articles = ((SavedArticlesFragment) mFragment).getArticles();
					
				}
				processQuery(newText);
				return true;
			}
		});
	}
	
	private void processQuery(String query) {
		
		searchresult = new ArrayList<>();
		//TODO
		//test for null for adapter and articles
		// case insensitive search
		for (Article article : articles) {
			if (article.getTitle().toLowerCase().contains(query.toLowerCase())) {
				searchresult.add(article);
			}
		}
		
		fragmentAdapter.setArticles(searchresult);
	}
	
	@Override
	public void onBackPressed() {
		
		if (mDrawerLayout.isDrawerOpen(Gravity.START)) {
			
			mDrawerLayout.closeDrawer(Gravity.START);
			
			return;
		}
		super.onBackPressed();
	}
	
	@Override
	public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
			switch (requestCode) {
				
				case GET_WEATHER_PERMISSION_REQUEST_CODE://location permission granted
					//noinspection MissingPermission
					if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
						Log.e(" onRequest ", "not granted");
						//	weatherView.setVisibility(View.GONE);
						// TODO: Consider calling
						//    ActivityCompat#requestPermissions
						// here to request the missing permissions, and then overriding
						//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
						//                                          int[] grantResults)
						// to handle the case where the user grants the permission. See the documentation
						// for ActivityCompat#requestPermissions for more details.
						return;
					}
					Log.e(" onRequest ", "granted");
					getWeather();
					break;
			}
		}
	}
	
	public int getHeaderBitmap() {
		Calendar c = Calendar.getInstance();
		int timeOfDay = c.get(Calendar.HOUR_OF_DAY);
		
		if (timeOfDay >= 0 && timeOfDay < 12) {
			weatherIcons = weatherConfig.iconsDay;
			return R.drawable.bg_morning;
		} else if (timeOfDay >= 12 && timeOfDay < 16) {
			weatherIcons = weatherConfig.iconsDay;
			return R.drawable.afternoon;
		} else if (timeOfDay >= 16 && timeOfDay < 19) {
			weatherIcons = weatherConfig.iconsDay;
			return R.drawable.evening;
		} else if (timeOfDay >= 19 && timeOfDay < 24) {
			weatherIcons = weatherConfig.iconsNight;
			return R.drawable.bg_night;
		}
		return R.drawable.bg_morning;
	}
	
	public void setTopFeedsTo(String category) {
		((TopFeedsFragment) adapter.getItem(0)).setCurrentCategory(category);
		
	}
	
	private void findAndSetViews() {
		mDrawerLayout = findViewById(R.id.drawer_layout);
		
		NavigationView navigationView = findViewById(R.id.nav_view);
		navigationView.setItemIconTintList(null);
		navigationView.setNavigationItemSelectedListener(
			new NavigationView.OnNavigationItemSelectedListener() {
				@Override
				public boolean onNavigationItemSelected(MenuItem menuItem) {
					// set item as selected to persist highlight
					//	menuItem.setChecked(true); no need , no fragments
					// close drawer when item is tapped
					mDrawerLayout.closeDrawers();
					switch (menuItem.getItemId()) {
						case R.id.nav_alerts:
							utils.showEditAlertsDialog();
							break;
						case R.id.nav_settings:
							//   startActivity(new Intent(MainActivity.this,SettingsActivity.class));
							if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
								ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this);
								Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
								startActivity(intent, options.toBundle());
								
							} else {
								Intent intent = new Intent(MainActivity.this, ArticleViewActivity.class);
								
								startActivity(intent);
							}
							break;
						case R.id.nav_subs:
							if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
								ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this);
								Intent intent = new Intent(MainActivity.this, SubscriptionsActivity.class);
								startActivity(intent, options.toBundle());
								
							} else {
								Intent intent = new Intent(MainActivity.this, SubscriptionsActivity.class);
								
								startActivity(intent);
							}
							break;
						case R.id.nav_search:
							Intent intent = new Intent(MainActivity.this, AdvancedSearchActivity.class);
							
							startActivity(intent);
							break;
						case R.id.nav_about:
							utils.showAboutUsDialog();
							break;
						default:
							break;
						
					}
					// Add code here to update the UI based on the item selected
					// For example, swap UI fragments here
					
					return true;
				}
			});
		tabLayout = findViewById(R.id.tabs);
		categoriesView = findViewById(R.id.horizontalscrollview);
		
		categoryHealth = findViewById(R.id.health);
		categoryTechnology = findViewById(R.id.technology);
		categorySports = findViewById(R.id.sports);
		categoryScience = findViewById(R.id.science);
		categoryGeneral = findViewById(R.id.general);
		categoryBusiness = findViewById(R.id.business);
		categoryEntertainment = findViewById(R.id.entertainment);
		
		categoryBusiness.setOnClickListener(this);
		categoryEntertainment.setOnClickListener(this);
		
		categoryGeneral.setOnClickListener(this);
		categoryGeneral.setBackgroundResource(R.drawable.bg_round_corner);
		lastClicked = categoryGeneral;
		
		categoryScience.setOnClickListener(this);
		
		categorySports.setOnClickListener(this);
		
		categoryTechnology.setOnClickListener(this);
		
		categoryHealth.setOnClickListener(this);
		
		tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
			@Override
			public void onTabSelected(TabLayout.Tab tab) {
				viewPager.setCurrentItem(tab.getPosition());
				CharSequence title = viewPager.getAdapter().getPageTitle(tab.getPosition());
				setTitle(title);
				if (tab.getPosition() == 0) {
					getSupportActionBar().setSubtitle(title);
					
					categoriesView.setVisibility(View.VISIBLE);
				} else {
					categoriesView.setVisibility(View.GONE);
					getSupportActionBar().setSubtitle(title);
				}
				
				switch (tab.getPosition()) {
					case 1:
						if(Preferences.getInstance(MainActivity.this).getKey("saved_sources").equals(""))
							Toast.makeText(MainActivity.this, R.string.no_subs, Toast.LENGTH_SHORT).show();
						// TODO
						break;
				}
			}
			
			@Override
			public void onTabUnselected(TabLayout.Tab tab) {
			}
			
			@Override
			public void onTabReselected(TabLayout.Tab tab) {
			}
		});
		appBarLayout = findViewById(R.id.appbar);
		appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
			@Override
			public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
				
				if (Math.abs(verticalOffset) - appBarLayout.getTotalScrollRange() == 0) {
					//  Collapsed
					appBarLayout.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
					tabLayout.setSelectedTabIndicatorColor(Color.WHITE);
					
				} else {
					//Expanded
					appBarLayout.setBackgroundColor(Color.TRANSPARENT);
					tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.colorPrimary));
					
				}
			}
		});
		
		AdView mAdView = (AdView) findViewById(R.id.adView);
		AdRequest adRequest = new AdRequest.Builder().build();
		mAdView.loadAd(adRequest);
		mAdView.setAdListener(new AdListener() {
			@Override
			public void onAdLoaded() {
				// Code to be executed when an ad finishes loading.
			}
			
			@Override
			public void onAdFailedToLoad(int errorCode) {
				// Code to be executed when an ad request fails.
				Log.e(" onAdFailedToLoad: ",String.valueOf(errorCode) );
			}
			
			@Override
			public void onAdOpened() {
				// Code to be executed when an ad opens an overlay that
				// covers the screen.
			}
			
			@Override
			public void onAdLeftApplication() {
				// Code to be executed when the user has left the app.
			}
			
			@Override
			public void onAdClosed() {
				// Code to be executed when when the user is about to return
				// to the app after tapping on an ad.
			}
		});
		//weatherView=findViewById(R.id.weather_view);
		
	}
	
	private void sartAlertService() {
		dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));
		Job myJob = dispatcher.newJobBuilder()
			.setService(AlertService.class) // the JobService that will be called
			.setTag("alert_service")        // uniquely identifies the job
			.setRecurring(true)
			// don't persist past a device reboot
			.setLifetime(Lifetime.FOREVER)
			// start between 0 and 60 seconds from now
			.setTrigger(Trigger.executionWindow(0, 60))
			// don't overwrite an existing job with the same tag
			.setReplaceCurrent(false)
			// retry with exponential backoff
			.setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
			// constraints that need to be satisfied for the job to run
			.setConstraints(
				// only run on an unmetered network
				Constraint.ON_ANY_NETWORK
			
			)
			.build();
		
		dispatcher.mustSchedule(myJob);
	}
	
	public class WeatherConfig {
		
		public int[] conditionsCodes = {Weather.CONDITION_UNKNOWN, Weather.CONDITION_CLEAR, Weather.CONDITION_CLOUDY,
			Weather.CONDITION_FOGGY, Weather.CONDITION_HAZY, Weather.CONDITION_ICY,
			Weather.CONDITION_RAINY, Weather.CONDITION_SNOWY
			, Weather.CONDITION_STORMY, Weather.CONDITION_WINDY};
		public String[] conditionsDescription = {getString(R.string.no_weather_conditions), getString(R.string.clear), getString(R.string.cloudy),
			getString(R.string.foggy), getString(R.string.hazy), getString(R.string.icy), getString(R.string.rainy), getString(R.string.snowy), getString(R.string.stormy), getString(R.string.windy)};
		
		public int[] iconsDay = {R.drawable.foggy, R.drawable.clear_day, R.drawable.cloudy_day,
			R.drawable.foggy, R.drawable.hazy, R.drawable.icy, R.drawable.rain,
			R.drawable.snowy, R.drawable.storm, R.drawable.wind};
		
		public int[] iconsNight = {R.drawable.foggy, R.drawable.clear_night, R.drawable.cloudy_night,
			R.drawable.foggy, R.drawable.hazy, R.drawable.icy, R.drawable.rain,
			R.drawable.snowy, R.drawable.storm, R.drawable.wind};
	}
	
	@Override
	public void onClick(View v) {
		lastClicked.setBackgroundColor(Color.WHITE);
		switch (v.getId()) {
			case R.id.general:
				setTopFeedsTo("general");
				categoryGeneral.setBackgroundResource(R.drawable.bg_round_corner);
				categoryGeneral.setPadding(2, 5, 2, 5);
				//	categoryGeneral.setBackgroundColor(Color.LTGRAY);
				lastClicked = categoryGeneral;
				getSupportActionBar().setSubtitle(R.string.general_category);
				break;
			
			case R.id.business:
				setTopFeedsTo("business");
				categoryBusiness.setBackgroundResource(R.drawable.bg_round_corner);
				categoryBusiness.setPadding(2, 5, 2, 5);
				//	categoryBusiness.setBackgroundColor(Color.LTGRAY);
				lastClicked = categoryBusiness;
				getSupportActionBar().setSubtitle(getString(R.string.category_business));
				break;
			
			case R.id.sports:
				setTopFeedsTo("sports");
				categorySports.setBackgroundResource(R.drawable.bg_round_corner);
				categorySports.setPadding(2, 5, 2, 5);
				//categorySports.setBackgroundColor(Color.LTGRAY);
				lastClicked = categorySports;
				getSupportActionBar().setSubtitle(getString(R.string.category_sports));
				break;
			
			case R.id.health:
				setTopFeedsTo("health");
				categoryHealth.setBackgroundResource(R.drawable.bg_round_corner);
				categoryHealth.setPadding(2, 5, 2, 5);
				
				//categoryHealth.setBackgroundColor(Color.LTGRAY);
				lastClicked = categoryHealth;
				getSupportActionBar().setSubtitle(getString(R.string.category_health));
				break;
			
			case R.id.science:
				setTopFeedsTo("science");
				categoryScience.setBackgroundResource(R.drawable.bg_round_corner);
				categoryScience.setPadding(2, 5, 2, 5);
				
				//	categoryScience.setBackgroundColor(Color.LTGRAY);
				lastClicked = categoryScience;
				getSupportActionBar().setSubtitle(getString(R.string.category_science));
				break;
			
			case R.id.technology:
				setTopFeedsTo("technology");
				categoryTechnology.setBackgroundResource(R.drawable.bg_round_corner);
				categoryTechnology.setPadding(2, 5, 2, 5);
				
				//categoryTechnology.setBackgroundColor(Color.LTGRAY);
				lastClicked = categoryTechnology;
				getSupportActionBar().setSubtitle(getString(R.string.category_technology));
				break;
			
			case R.id.entertainment:
				setTopFeedsTo("entertainment");
				categoryEntertainment.setBackgroundResource(R.drawable.bg_round_corner);
				categoryEntertainment.setPadding(2, 5, 2, 5);
				
				//	categoryEntertainment.setBackgroundColor(Color.LTGRAY);
				lastClicked = categoryEntertainment;
				getSupportActionBar().setSubtitle(getString(R.string.category_entertainment));
				break;
			default:
				break;
		}
	}
	
}
