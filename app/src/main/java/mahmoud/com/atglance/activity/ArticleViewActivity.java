package mahmoud.com.atglance.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.transition.Transition;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import mahmoud.com.atglance.R;
import mahmoud.com.atglance.Utils;
import mahmoud.com.atglance.model.Article;
import mahmoud.com.atglance.provider.DBContract;

public class ArticleViewActivity extends AppCompatActivity {
	public WebView webView;
	Article article;
	boolean loadingFinished = true;
	boolean redirect = false;
	ProgressBar bar;
	ImageView backButton;
	private ShareActionProvider mShareActionProvider;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		if (intent == null) {
			return;
			
		}
		article = getIntent().getExtras().getParcelable("article");
		if (article == null)
			return;
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
			
			getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
			Transition enterTrans = new Slide();
			getWindow().setEnterTransition(enterTrans);
			
			Transition returnTrans = new Slide();
			getWindow().setReturnTransition(returnTrans);
			
		}
		setContentView(R.layout.activity_article_view);
		
		Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
		
		setSupportActionBar(myToolbar);
		setTitle(article.getTitle());
		getSupportActionBar().setSubtitle(article.getSource().getName());
		myToolbar.setNavigationIcon(R.drawable.ic_back);
		
		myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
		
		bar = findViewById(R.id.progressBar);
		
		webView = (WebView) findViewById(R.id.webview);
		webView.getSettings().setAppCacheMaxSize(5 * 1024 * 1024); // 5MB
		webView.getSettings().setAppCachePath(this.getCacheDir().getAbsolutePath());
		webView.getSettings().setAllowFileAccess(true);
		webView.getSettings().setAppCacheEnabled(true);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT); // load online by default
		webView.setWebViewClient(new WebViewClient() {
			
			@Override
			public boolean shouldOverrideUrlLoading(
				WebView view, WebResourceRequest request) {
				if (!loadingFinished) {
					redirect = true;
				}
				
				loadingFinished = false;
				webView.loadUrl(article.getUrl());
				return true;
			}
			
			@Override
			public void onPageStarted(
				WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
				loadingFinished = false;
				//SHOW LOADING IF IT ISNT ALREADY VISIBLE
				//  Log.e("started","started");
				bar.setVisibility(View.VISIBLE);
			}
			
			@Override
			public void onPageFinished(WebView view, String url) {
				if (!redirect) {
					loadingFinished = true;
				}
				
				if (loadingFinished && !redirect) {
					//HIDE LOADING IT HAS FINISHED
					//    Log.e("finished","finished");
					bar.setVisibility(View.INVISIBLE);
				} else {
					redirect = false;
				}
			}
		});
		Utils utils = new Utils(this);
		if (!utils.isOnline()) { // loading offline
			webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
			
		}
		webView.loadUrl(article.getUrl());
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_article_view, menu);
		// Locate MenuItem with ShareActionProvider
		MenuItem item = menu.findItem(R.id.menu_item_share);
		
		// Fetch and store ShareActionProvider
		mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
		Intent sendIntent = new Intent();
		sendIntent.setAction(Intent.ACTION_SEND);
		sendIntent.putExtra(Intent.EXTRA_TEXT, article.getTitle() + "\n" + article.getUrl());
		sendIntent.setType("text/plain");
		// startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.send_to)));
		if (mShareActionProvider != null)
			mShareActionProvider.setShareIntent(sendIntent);
		return true;
		
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.save:
				Uri CONTENT_URI = DBContract.CONTENT_URI;
				ContentValues values = new ContentValues();
				values.put("title", article.getTitle());
				values.put("description", article.getDescription());
				values.put("source", article.getSource().getName());
				values.put("url", article.getUrl());
				values.put("imageurl", article.getUrlToImage());
				values.put("publishedat", article.getPublishedAt());
				
				Uri mUri = getContentResolver().insert(CONTENT_URI, values);
				if (mUri != null) {
					Toast.makeText(ArticleViewActivity.this, R.string.articel_saved_success, Toast.LENGTH_LONG).show();
				}
				
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
}