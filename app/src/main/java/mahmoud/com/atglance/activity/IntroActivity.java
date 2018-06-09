package mahmoud.com.atglance.activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import mahmoud.com.atglance.Preferences;
import mahmoud.com.atglance.R;
import mahmoud.com.atglance.Utils;
import mahmoud.com.atglance.adapter.IntroImagesAdapter;

public class IntroActivity extends AppCompatActivity {
	ViewPager viewPager;
	Button skip;
	Preferences preferences;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		preferences = Preferences.getInstance(this);
		preferences.saveKey(Preferences.KEY_DEFAULT_COUNTRY_CODE, "");
		
		setContentView(R.layout.activity_entro);
		
		preferences.saveKey(Preferences.KEY_FIRST_TIME_RUN, "yes");
		
		preferences.saveKey(Preferences.KEY_KEYWORDS, "");
		preferences.saveKey(Preferences.KEY_SAVED_SOURCES, "");
		
		preferences.saveKey(Preferences.KEY_DEFAULT_UNIT, "celsius");
		preferences.saveKey(Preferences.KEY_MUTE_ALERTS, "no");
		preferences.saveKey(Preferences.KEY_SERVICE_LAST_TIME_RUN, "0");
		skip = findViewById(R.id.skipbtn);
		skip.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//show countries dialog
				new Utils(IntroActivity.this).showCountriesDialog();
				
			}
		});
		viewPager = (ViewPager) findViewById(R.id.viewPager);
		
		IntroImagesAdapter viewPagerAdapter = new IntroImagesAdapter(this);
		
		viewPager.setAdapter(viewPagerAdapter);
		
	}
}
