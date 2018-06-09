package mahmoud.com.atglance.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.transition.Transition;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import mahmoud.com.atglance.Preferences;
import mahmoud.com.atglance.R;
import mahmoud.com.atglance.adapter.SourcesAdapter;
import mahmoud.com.atglance.model.SourcesResponse;
import mahmoud.com.atglance.model.SubSource;
import mahmoud.com.atglance.rest.ApiService;
import mahmoud.com.atglance.rest.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SubscriptionsActivity extends AppCompatActivity implements SourcesRecyclerViewClickInterface {
	Toolbar toolbar;
	List<String> savedSources = new ArrayList<>();
	Preferences preferences;
	List<SubSource> sources = new ArrayList<>();
	ApiService apiService;
	RecyclerView sourcesrv;
	SourcesAdapter adapter;
	ProgressBar bar;
	int counter = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		apiService = RetrofitClient.getClient().create(ApiService.class);
		preferences = Preferences.getInstance(this);
		savedSources = new ArrayList(Arrays.asList(preferences.getKey(Preferences.KEY_SAVED_SOURCES).split(",")));
		counter = savedSources.size();
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
			
			getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
			Transition enterTrans = new Slide();
			getWindow().setEnterTransition(enterTrans);
			
			Transition returnTrans = new Slide();
			getWindow().setReturnTransition(returnTrans);
			
		}
		setContentView(R.layout.activity_subscriptions);
		toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		toolbar.setNavigationIcon(R.drawable.ic_back);
		
		toolbar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
		
		bar = findViewById(R.id.progressBar);
		sourcesrv = findViewById(R.id.sources_rv);
		LinearLayoutManager layoutManager = new LinearLayoutManager(this);
		sourcesrv.setLayoutManager(layoutManager);
		DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(sourcesrv.getContext(),
			layoutManager.getOrientation());
		sourcesrv.addItemDecoration(dividerItemDecoration);
		adapter = new SourcesAdapter(this, sources);
		sourcesrv.setAdapter(adapter);
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		getSources();
	}
	
	private void getSources() {
		bar.setVisibility(View.VISIBLE);
		
		Call<SourcesResponse> call;
		
		call = apiService.getSources();
		
		call.enqueue(new Callback<SourcesResponse>() {
			@Override
			public void onResponse(Call<SourcesResponse> call, Response<SourcesResponse> response) {
				
				//Log.e("code",String.valueOf(response.code()));
				
				if (response.code() > 200) {
					Toast.makeText(SubscriptionsActivity.this, R.string.something_wrong, Toast.LENGTH_SHORT).show();
					
					return;
				}
				sources = response.body().getSources();
				for (int i = 0; i < sources.size(); i++) {
					if (savedSources.contains(sources.get(i).getId())) {
						sources.get(i).setSelected(true);
						Log.e(" saved source: ", sources.get(i).getId());
					}
					
				}
				adapter = new SourcesAdapter(SubscriptionsActivity.this, sources);
				adapter.setListner(SubscriptionsActivity.this);
				sourcesrv.setAdapter(adapter);
				bar.setVisibility(View.INVISIBLE);
				
			}
			
			@Override
			public void onFailure(Call<SourcesResponse> call, Throwable t) {
				// Log error here since request failed
				Log.e("ERROR", t.toString());
				
				Toast.makeText(SubscriptionsActivity.this, R.string.something_wrong, Toast.LENGTH_SHORT).show();
				bar.setVisibility(View.INVISIBLE);
			}
		});
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.subscriptions_menu, menu);
		
		return true;
		
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.save:
				StringBuilder sb = new StringBuilder();
				for (int i = 0; i < savedSources.size(); i++) {
					sb.append(savedSources.get(i)).append(",");
				}
				preferences.saveKey(Preferences.KEY_SAVED_SOURCES, sb.toString());
				Log.e(" sources saved: ", sb.toString());
				Toast.makeText(SubscriptionsActivity.this, R.string.saved_success, Toast.LENGTH_SHORT).show();
				onBackPressed();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	public void onSourceChecked(int position) {
		Log.e(" activity: ", "checked  " + position + "  counter =  " + counter);
		if (counter >= 20) {
			Toast.makeText(this, R.string.maximum_20, Toast.LENGTH_SHORT).show();
			return;
			
		}
		savedSources.add(sources.get(position).getId());
		
		counter += 1;
	}
	
	@Override
	public void onSourceUnChecked(int position) {
		Log.e(" activity : ", "un checked " + position);
		
		savedSources.remove(sources.get(position).getId());
		counter -= 1;
	}
}
