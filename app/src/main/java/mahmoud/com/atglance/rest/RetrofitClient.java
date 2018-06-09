package mahmoud.com.atglance.rest;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
	public static final String API_KEY = "xxxxxxxxxxxxxxxxxxxxxxx";
	private static final String BASE_URL = "https://newsapi.org/v2/";
	/**
	 * Get Retrofit Instance
	 */
	private static Retrofit retrofit = null;
	
	public static Retrofit getClient() {
		if (retrofit == null) {
			retrofit = new Retrofit.Builder()
				.baseUrl(BASE_URL)
				.addConverterFactory(GsonConverterFactory.create())
				.build();
		}
		return retrofit;
	}
	
}
