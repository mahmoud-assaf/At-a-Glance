package mahmoud.com.atglance.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import mahmoud.com.atglance.Preferences;
import mahmoud.com.atglance.R;

public class SplashActivity extends AppCompatActivity {
	private static int SPLASH_TIME_OUT = 1000;
	Preferences preferences;
	Boolean firstTime = true;
	Intent i;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		preferences = Preferences.getInstance(this);
		firstTime = preferences.getKey(Preferences.KEY_FIRST_TIME_RUN).equals("null");
		setContentView(R.layout.activity_splash);
		
		new Handler().postDelayed(new Runnable() {
			
			/*
			 * Showing splash screen with a timer. This will be useful when you
			 * want to show case your app logo / company
			 */
			
			@Override
			public void run() {
				// This method will be executed once the timer is over
				// Start your app main activity
				if (firstTime) {
					i = new Intent(SplashActivity.this, IntroActivity.class);
				} else {
					i = new Intent(SplashActivity.this, MainActivity.class);
				}
				
				startActivity(i);
				
				// close this activity
				finish();
			}
		}, SPLASH_TIME_OUT);
	}
}
