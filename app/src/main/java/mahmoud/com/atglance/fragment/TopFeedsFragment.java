package mahmoud.com.atglance.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;

import mahmoud.com.atglance.Preferences;
import mahmoud.com.atglance.R;
import mahmoud.com.atglance.adapter.ArticlesAdapter;
import mahmoud.com.atglance.model.Article;
import mahmoud.com.atglance.model.NewsResponse;
import mahmoud.com.atglance.rest.ApiService;
import mahmoud.com.atglance.rest.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TopFeedsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
	public final String TAG = TopFeedsFragment.this.getClass().getSimpleName();
	public ArrayList<Article> articles;
	public String currentCategory;
	ApiService newsSevice;
	RecyclerView recyclerView;
	ArticlesAdapter adapter;
	ProgressBar bar;
	boolean fragmentInitialized = false;
	SwipeRefreshLayout mSwipeRefreshLayout;
	Preferences preferences;
	
	public TopFeedsFragment() {
		// Required empty public constructor
	}
	
	// TODO: Rename and change types and number of parameters
	public static TopFeedsFragment newInstance() {
		TopFeedsFragment fragment = new TopFeedsFragment();
		
		return fragment;
	}
	
	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		
		if (isVisibleToUser) { // fragment is visible and have created
			//  Log.e(TAG , " setUserVisibleHint:  visible");
                     /*  if(articles==null) {
                                Log.e(TAG , " setUserVisibleHint: articles null" );
                              //  getCategoryHeadlines();
        
                        }else {
                               adapter = new ArticlesAdapter(getActivity(), articles);
                               recyclerView.setAdapter(adapter);
                               bar.setVisibility(View.INVISIBLE);
                       }*/
		} else {
			//    Log.e(TAG , " setUserVisibleHint:  not visible");
		}
	}
	
	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		preferences = Preferences.getInstance(getActivity());
		newsSevice = RetrofitClient.getClient().create(ApiService.class);
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		//    Log.e(TAG , " onCreateView: ");
		View v = inflater.inflate(R.layout.fragment_head_lines, container, false);
		recyclerView = v.findViewById(R.id.articles_rv);
		LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
		recyclerView.setLayoutManager(layoutManager);
		DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
			layoutManager.getOrientation());
		recyclerView.addItemDecoration(dividerItemDecoration);
		adapter = new ArticlesAdapter(getActivity(), new ArrayList<Article>());
		recyclerView.setAdapter(adapter);
		
		// SwipeRefreshLayout
		mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_container);
		mSwipeRefreshLayout.setOnRefreshListener(this);
		mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
			android.R.color.holo_green_dark,
			android.R.color.holo_orange_dark,
			android.R.color.holo_blue_dark);
		
		/**
		 * Showing Swipe Refresh animation on activity create
		 * As animation won't start on onCreate, post runnable is used
		 */
		
		//   bar=v.findViewById(R.id.progressBar);
                /*if(getUserVisibleHint()){ // fragment is visible
                        if(articles==null)
                        getCategoryHeadlines();
                }*/
               /* if(articles==null)
                        getCategoryHeadlines();*/
		
		return v;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		//  Log.e(TAG ," onResume: " );
		if (articles == null) {
			//  Log.e(TAG ," onResume: getting news" );
			
			mSwipeRefreshLayout.setRefreshing(true);
			
			// Fetching data from server
			getCategoryHeadlines(currentCategory);
			
		}
		fragmentInitialized = true;
	}
	
	@Override
	public void onDetach() {
		super.onDetach();
		
	}
	
	@Override
	public void onRefresh() {
		
		// Fetching data from server
		getCategoryHeadlines(currentCategory);
	}
	
	private void getCategoryHeadlines(String category) {
		// bar.setVisibility(View.VISIBLE);
		mSwipeRefreshLayout.setRefreshing(true);
		Call<NewsResponse> call;
		
		call = newsSevice.getCountryCategoryNews(category, preferences.getKey("default_country_code"));
		
		call.enqueue(new Callback<NewsResponse>() {
			@Override
			public void onResponse(Call<NewsResponse> call, Response<NewsResponse> response) {
				
				//Log.e("code",String.valueOf(response.code()));
				
				if (response.code() > 200) {
					Toast.makeText(getActivity(), R.string.something_wrong, Toast.LENGTH_SHORT).show();
					mSwipeRefreshLayout.setRefreshing(false);
					return;
				}
				articles = response.body().getArticles();
				
				adapter = new ArticlesAdapter(getActivity(), articles);
				
				recyclerView.setAdapter(adapter);
				mSwipeRefreshLayout.setRefreshing(false);
				//  bar.setVisibility(View.INVISIBLE);
				//	saveCallTime(call, articles.get(0).getPublishedAt());
				
			}
			
			@Override
			public void onFailure(Call<NewsResponse> call, Throwable t) {
				// Log error here since request failed
				Log.e("ERROR", t.toString());
				mSwipeRefreshLayout.setRefreshing(false);
				Toast.makeText(getActivity(), R.string.something_wrong, Toast.LENGTH_SHORT).show();
				//  bar.setVisibility(View.INVISIBLE);
			}
		});
	}
	
	public ArrayList<Article> getArticles() {
		return articles;
	}
	
	public ArticlesAdapter getAdapter() {
		return adapter;
	}
	
	public void setCurrentCategory(String category) {
		currentCategory = category;
		if (fragmentInitialized)
			getCategoryHeadlines(currentCategory);
	}
	
}
