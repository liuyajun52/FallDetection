package com.blacklighting.falldetection;

import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;

public class SettingActivity extends PreferenceActivity {

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.activity_setting);
		addPreferencesFromResource(R.xml.setting);
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
			Preference preference) {
		String key = preference.getKey();
		if (key.equals("about")) {
			Builder builder = new Builder(SettingActivity.this);
			builder.setTitle("关于")
					.setMessage(
							"电子科技大学\n黑色之光创意工作室\n刘亚军\n付敏\n谢弘宸\nliuyajun52@gmail.com")
					.setPositiveButton("确定", null);
			builder.create().show();

		}
		SharedPreferences mPreferences = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());

		mPreferences
				.registerOnSharedPreferenceChangeListener(new OnSharedPreferenceChangeListener() {

					@Override
					public void onSharedPreferenceChanged(
							SharedPreferences arg0, String arg1) {
						if (arg1.equals("serviceSwitch")) {
							boolean commond = arg0.getBoolean("serviceSwitch",
									true);
							Intent i = new Intent(SettingActivity.this,
									DetectionServer.class);
							if (commond) {
								startService(i);
							} else {
								stopService(i);
							}
						}
					}
				});
		return super.onPreferenceTreeClick(preferenceScreen, preference);
	}

}
