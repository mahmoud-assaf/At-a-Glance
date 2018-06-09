package mahmoud.com.atglance.rest;

import java.util.Map;

import mahmoud.com.atglance.model.NewsResponse;
import mahmoud.com.atglance.model.SourcesResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface ApiService {
	
	//category in country ,all countries if country=""
	@GET("top-headlines?apiKey=xxxxxxxxxxxxxxxxxxxxxxx&pageSize=100")
	Call<NewsResponse> getCountryCategoryNews(@Query("category") String category, @Query("country") String country);
	
	//get sources
	@GET("sources?apiKey=fd69bafdbd7d43ae84d0dc4472a604f7")
	Call<SourcesResponse> getSources();
	
	//get subscriptions
	@GET("top-headlines?apiKey=xxxxxxxxxxxxxxxxxxxxxxx&pageSize=100")
	Call<NewsResponse> getSubscriptions(@Query("sources") String sources);
	
	//advanced search
	@GET("everything?apiKey=xxxxxxxxxxxxxxxxxxxxxxx&pageSize=100")
	Call<NewsResponse> searchNews(@QueryMap(encoded = true) Map<String, String> params);
	
}
