package mahmoud.com.atglance.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.transition.Transition;
import android.util.Log;
import android.view.View;
import android.view.Window;

import java.util.HashMap;

import mahmoud.com.atglance.R;
import mahmoud.com.atglance.fragment.SearchFragment;
import mahmoud.com.atglance.fragment.SearchResultsFragment;

public class AdvancedSearchActivity extends AppCompatActivity implements SearchFragment.OnFragmentSearchListener {
	Toolbar toolbar;
	public  final int SEARCH_FRAGMENT=1;
	public  final int SEARCH_RESULTS_FRAGMENT=2;
	public int  currentFragment=SEARCH_FRAGMENT;
	HashMap<String, String> searchParams;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(savedInstanceState!=null){
			currentFragment=savedInstanceState.getInt("current_fragment");
			if(savedInstanceState.containsKey("search_params"))
			searchParams=(HashMap<String, String>)savedInstanceState.getSerializable("search_params");
		}
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
			
			getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
			Transition enterTrans = new Slide();
			getWindow().setEnterTransition(enterTrans);
			
			Transition returnTrans = new Slide();
			getWindow().setReturnTransition(returnTrans);
			
		}
		setContentView(R.layout.activity_advanced_search);
		toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		toolbar.setNavigationIcon(R.drawable.ic_back);
		
		toolbar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
		
		
		
		if (currentFragment==SEARCH_FRAGMENT) {
			SearchFragment searchFragment=(SearchFragment)getSupportFragmentManager().findFragmentByTag("search_fragment");
			if (searchFragment==null){
				Log.e( " fragment search: ","is  null new one" );
				
				searchFragment = new SearchFragment();
				searchFragment.setRetainInstance(true);
			}
			
			
			
			FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
			fragmentTransaction.replace(R.id.container, searchFragment, "search_fragment");
			fragmentTransaction.commit();
		}else{
			SearchResultsFragment fr=(SearchResultsFragment)getSupportFragmentManager().findFragmentByTag("search_results_fragment");
			if (fr==null) {
				Log.e( " fragment results: ","is  null new one" );
				fr = new SearchResultsFragment();
				Bundle bundle = new Bundle();
				bundle.putSerializable("search_params", searchParams);
				fr.setArguments(bundle);
				fr.setRetainInstance(true);
				
			}
				android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
				fragmentTransaction.replace(R.id.container, fr,"search_results_fragment");


// Commit the transaction
				fragmentTransaction.commit();
				currentFragment=SEARCH_RESULTS_FRAGMENT;
			}
			
		
	}
	
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("current_fragment",currentFragment);
		outState.putSerializable("search_params",searchParams);
	}
	
	@Override
	public void onFragmentSearch(HashMap<String, String> searchParams) {
		this.searchParams=searchParams;
		SearchResultsFragment fr = new SearchResultsFragment(); // Replace with your Fragment class
		Bundle bundle = new Bundle();
		bundle.putSerializable("search_params", searchParams);
		fr.setArguments(bundle);
		fr.setRetainInstance(true);
		android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
		fragmentTransaction.replace(R.id.container, fr,"search_results_fragment");
		fragmentTransaction.addToBackStack(null);

// Commit the transaction
		fragmentTransaction.commit();
		currentFragment=SEARCH_RESULTS_FRAGMENT;
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		if (currentFragment==SEARCH_RESULTS_FRAGMENT)
			currentFragment=SEARCH_FRAGMENT;
	}
}
