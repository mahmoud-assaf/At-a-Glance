package mahmoud.com.atglance;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by mahmoud on 25/01/2018.
 */

public class Preferences {
	public static final String KEY_DEFAULT_COUNTRY_CODE = "default_country_code";
	public static final String KEY_DEFAULT_COUNTRY_NAME = "default_country_name";
	public static final String KEY_FIRST_TIME_RUN = "first_time";
	public static final String KEY_SAVED_SOURCES = "saved_sources";
	public static final String KEY_DEFAULT_UNIT = "default_unit";
	public static final String KEY_MUTE_ALERTS = "mute_alerts";
	public static final String KEY_SERVICE_LAST_TIME_RUN = "service_last_time_run";
	public static final String KEY_KEYWORDS = "keywords";
	private static Preferences Prefs;
	String myPrefs = "prefs";
	private SharedPreferences prefs;

	private Preferences(Context context) {
		prefs = context.getSharedPreferences(myPrefs, Context.MODE_PRIVATE);
	}

	public static Preferences getInstance(Context context) {
		if (Prefs == null) {
			Prefs = new Preferences(context);
		}
		return Prefs;
	}

	public void saveKey(String key, Object value) {
		SharedPreferences.Editor prefsEditor = prefs.edit();
		if (value instanceof String)
			prefsEditor.putString(key, (String) value);
		else if (value instanceof Integer)
			prefsEditor.putInt(key, (Integer) value);
		else if (value instanceof Float)
			prefsEditor.putFloat(key, (Float) value);
		else if (value instanceof Boolean)
			prefsEditor.putBoolean(key, (Boolean) value);

		prefsEditor.commit();
	}



    /*public void saveKey(String key,String value) {
        SharedPreferences.Editor prefsEditor = prefs.edit();
        prefsEditor .putString(key, value);
        prefsEditor.commit();
    }*/

	public String getKey(String key) {
		if (prefs != null) {
			return prefs.getString(key, "null");
		}
		return "null";
	}

}
