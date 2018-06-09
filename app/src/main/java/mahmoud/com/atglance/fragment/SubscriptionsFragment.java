package mahmoud.com.atglance.fragment;

import android.content.Context;
import android.net.Uri;
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

public class SubscriptionsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

	public ArrayList<Article> articles;
	ArticlesAdapter adapter;
	ApiService newsSevice;
	RecyclerView recyclerView;
	SwipeRefreshLayout mSwipeRefreshLayout;
	Preferences preferences;
	boolean fragmentInitialized = false;
	boolean isvisible = false;

	public SubscriptionsFragment() {
		// Required empty public constructor
	}

	public static SubscriptionsFragment newInstance() {
		SubscriptionsFragment fragment = new SubscriptionsFragment();

		return fragment;
	}

	private void getSubscriptions(String saved_sources) {

		// bar.setVisibility(View.VISIBLE);
		mSwipeRefreshLayout.setRefreshing(true);
		Call<NewsResponse> call;

		call = newsSevice.getSubscriptions(saved_sources);

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

	@Override
	public void onRefresh() {

		// Fetching data from server
		getSubscriptions(preferences.getKey("saved_sources"));

	}

	public ArrayList<Article> getArticles() {
		return articles;
	}

	public ArticlesAdapter getAdapter() {
		return adapter;
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		isvisible = isVisibleToUser;
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
		
		return v;
	}

	@Override
	public void onResume() {
		super.onResume();

		
		
		//  Log.e(TAG ," onResume: " );
		if (articles == null) {
			//  Log.e(TAG ," onResume: getting news" );

			

			// Fetching data from server
			if (!preferences.getKey("saved_sources").equals("")){
				mSwipeRefreshLayout.setRefreshing(true);
				getSubscriptions(preferences.getKey("saved_sources"));
			}

		}
		fragmentInitialized = true;

	}

	/**
	 * This interface must be implemented by activities that contain this
	 * fragment to allow an interaction in this fragment to be communicated
	 * to the activity and potentially other fragments contained in that
	 * activity.
	 * <p>
	 * See the Android Training lesson <a href=
	 * "http://developer.android.com/training/basics/fragments/communicating.html"
	 * >Communicating with Other Fragments</a> for more information.
	 */
	public interface OnFragmentInteractionListener {
		// TODO: Update argument type and name
		void onFragmentInteraction(Uri uri);
	}

}
