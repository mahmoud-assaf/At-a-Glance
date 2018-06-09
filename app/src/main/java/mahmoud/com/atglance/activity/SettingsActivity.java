package mahmoud.com.atglance.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.transition.Transition;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import mahmoud.com.atglance.Preferences;
import mahmoud.com.atglance.R;
import mahmoud.com.atglance.Utils;

public class SettingsActivity extends AppCompatActivity {
	Preferences preferences;
	Utils utils;
	RadioButton radioButtonCelsius, radioButtonFahr;
	RadioGroup radioGroupUnit;
	TextView countrytxtview;
	CheckBox muteAlerts;
	Toolbar toolbar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
			
			getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
			Transition enterTrans = new Slide();
			getWindow().setEnterTransition(enterTrans);
			
			Transition returnTrans = new Slide();
			getWindow().setReturnTransition(returnTrans);
			
		}
		setContentView(R.layout.activity_settings);
		toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		toolbar.setNavigationIcon(R.drawable.ic_back);
		
		toolbar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
		
		preferences = Preferences.getInstance(this);
		utils = new Utils(this);
		countrytxtview = findViewById(R.id.txtCountry);
		countrytxtview.setText(preferences.getKey(Preferences.KEY_DEFAULT_COUNTRY_NAME));
		countrytxtview.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showCountriesDialog();
			}
		});
		
		radioButtonCelsius = findViewById(R.id.radio_celsius);
		radioButtonFahr = findViewById(R.id.radio_fahr);
		if (preferences.getKey(Preferences.KEY_DEFAULT_UNIT).equals("celsius"))
			radioButtonCelsius.setChecked(true);
		else if (preferences.getKey(Preferences.KEY_DEFAULT_UNIT).equals("fahr"))
			radioButtonFahr.setChecked(true);
		radioGroupUnit = findViewById(R.id.radioGroupunits);
		radioGroupUnit.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				
				// checkedId is the RadioButton selected
				if (checkedId == R.id.radio_celsius)
					preferences.saveKey(Preferences.KEY_DEFAULT_UNIT, "celsius");
				
				else if (checkedId == R.id.radio_fahr)
					preferences.saveKey(Preferences.KEY_DEFAULT_UNIT, "fahr");
				Toast.makeText(SettingsActivity.this, R.string.unit_saved, Toast.LENGTH_SHORT).show();
				
			}
		});
		
		muteAlerts = findViewById(R.id.check_get_alerts);
		if (preferences.getKey(Preferences.KEY_MUTE_ALERTS).equals("yes"))
			muteAlerts.setChecked(true);
		else muteAlerts.setChecked(false);
		muteAlerts.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// update your model (or other business logic) based on isChecked
				if (isChecked) {
					preferences.saveKey(Preferences.KEY_MUTE_ALERTS, "yes");
					Toast.makeText(SettingsActivity.this, R.string.alerts_muted, Toast.LENGTH_SHORT).show();
					
				} else {
					preferences.saveKey(Preferences.KEY_MUTE_ALERTS, "no");
					Toast.makeText(SettingsActivity.this, R.string.alerts_unmuted, Toast.LENGTH_SHORT).show();
				}
				
			}
		});
		
	}
	
	public void showCountriesDialog() {
		
		// setup the alert builder
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.choose_country);
		
		// add a list
		final String[] countries = getResources().getStringArray(R.array.countries_names);
		final String[] countriesCodes = getResources().getStringArray(R.array.countries_codes);
		builder.setItems(countries, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
				preferences.saveKey(Preferences.KEY_DEFAULT_COUNTRY_CODE, countriesCodes[which]);
				preferences.saveKey(Preferences.KEY_DEFAULT_COUNTRY_NAME, countries[which]);
				countrytxtview.setText(countries[which]);
				Toast.makeText(SettingsActivity.this, R.string.country_saved, Toast.LENGTH_SHORT).show();
				
			}
		});
		
		// create and show the alert dialog
		AlertDialog dialog = builder.create();
		dialog.show();
	}
}
