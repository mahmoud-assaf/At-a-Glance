package mahmoud.com.atglance.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import mahmoud.com.atglance.R;
import mahmoud.com.atglance.adapter.ArticlesAdapter;
import mahmoud.com.atglance.model.Article;
import mahmoud.com.atglance.model.NewsResponse;
import mahmoud.com.atglance.rest.ApiService;
import mahmoud.com.atglance.rest.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchResultsFragment extends Fragment {
	HashMap<String, String> searchParams = new HashMap<>();
	ApiService apiService;
	ProgressBar bar;
	RecyclerView results_recyclerv;
	View noResults;
	ArrayList<Article> articles;
	ArticlesAdapter adapter;
	TextView tryAgain;

	public SearchResultsFragment() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		
		if (getArguments() == null)
			return;
		Bundle b = this.getArguments();
		if (b.getSerializable("search_params") != null) {
			searchParams = (HashMap<String, String>) b.getSerializable("search_params");

		}
		apiService = RetrofitClient.getClient().create(ApiService.class);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View v = inflater.inflate(R.layout.fragment_search_results, container, false);
		getActivity().setTitle("Search results");

		bar = v.findViewById(R.id.progressBar);
		tryAgain = v.findViewById(R.id.tryagainTxtView);
		tryAgain.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				getActivity().onBackPressed();
			}
		});
		results_recyclerv = v.findViewById(R.id.results_rv);
		LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
		results_recyclerv.setLayoutManager(layoutManager);
		DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(results_recyclerv.getContext(),
			layoutManager.getOrientation());
		results_recyclerv.addItemDecoration(dividerItemDecoration);
		adapter = new ArticlesAdapter(getActivity(), new ArrayList<Article>());
		noResults = v.findViewById(R.id.no_results_view);
		adapter = new ArticlesAdapter(getActivity(), new ArrayList<Article>());
		return v;
	}

	@Override
	public void onResume() {
		super.onResume();
		
			// Fetching data from server
			getSearchResults(searchParams);
	

	}

	private void getSearchResults(HashMap<String, String> searchParams) {
		bar.setVisibility(View.VISIBLE);
		Call<NewsResponse> call;

		call = apiService.searchNews(searchParams);

		call.enqueue(new Callback<NewsResponse>() {
			@Override
			public void onResponse(Call<NewsResponse> call, Response<NewsResponse> response) {

				Log.e("url", call.request().toString());

				if (response.code() > 200) {
					Log.e("ERROR code", response.toString());
					Toast.makeText(getActivity(), R.string.something_wrong, Toast.LENGTH_SHORT).show();

					return;
				}
				articles = response.body().getArticles();
				if (articles.size() == 0) {
					noResults.setVisibility(View.VISIBLE);
					results_recyclerv.setVisibility(View.INVISIBLE);
					bar.setVisibility(View.INVISIBLE);

				} else {
					adapter = new ArticlesAdapter(getActivity(), articles);

					results_recyclerv.setAdapter(adapter);

					bar.setVisibility(View.INVISIBLE);
				}
			}

			@Override
			public void onFailure(Call<NewsResponse> call, Throwable t) {
				// Log error here since request failed
				Log.e("ERROR", t.toString());
				Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
				bar.setVisibility(View.INVISIBLE);
			}
		});
	}

}
