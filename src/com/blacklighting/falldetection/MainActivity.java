package com.blacklighting.falldetection;

import java.lang.ref.WeakReference;

import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.view.Menu;
import android.widget.Toast;

public class MainActivity extends PreferenceActivity {
	static final int GUIDE_WHAT = 0;
	MHandler mHandler = new MHandler(MainActivity.this);

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.setting);

		SharedPreferences mPreferences = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		if (mPreferences.getBoolean("serviceSwitch", true)) {
			Intent i = new Intent(MainActivity.this, DetectionServer.class);
			startService(i);
		}

		if (mPreferences.getBoolean("firstStart", true)) {
			Editor ed = mPreferences.edit();
			ed.putBoolean("firstStart", false);
			ed.commit();
			mHandler.sendEmptyMessage(GUIDE_WHAT);

		}

		mPreferences
				.registerOnSharedPreferenceChangeListener(new OnSharedPreferenceChangeListener() {

					@Override
					public void onSharedPreferenceChanged(
							SharedPreferences arg0, String arg1) {
						// TODO Auto-generated method stub
						if (arg1.equals("serviceSwitch")) {
							boolean commond = arg0.getBoolean("serviceSwitch",
									true);
							Intent i = new Intent(MainActivity.this,
									DetectionServer.class);
							if (commond) {
								startService(i);
								Toast.makeText(MainActivity.this, "监测服务已经开启",
										Toast.LENGTH_SHORT).show();
							} else {
								stopService(i);
								Toast.makeText(MainActivity.this, "监测服务已经关闭",
										Toast.LENGTH_SHORT).show();
							}
						}
					}
				});
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
			Preference preference) {
		// TODO Auto-generated method stub
		String key = preference.getKey();
		if (key.equals("about")) {
			Builder builder = new Builder(MainActivity.this);
			builder.setTitle("关于")
					.setMessage(
							"电子科技大学\n黑色之光创意工作室\n刘亚军\n付敏\n谢弘宸\nliuyajun52@gmail.com")
					.setPositiveButton("确定", null);
			builder.create().show();

		}
		return super.onPreferenceTreeClick(preferenceScreen, preference);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	static class MHandler extends Handler {
		WeakReference<MainActivity> act;

		public MHandler(MainActivity act) {
			this.act = new WeakReference<MainActivity>(act);
		}

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case GUIDE_WHAT:
				Intent guide = new Intent(act.get(), GuideActivity.class);
				act.get().startActivity(guide);
				break;
			}
		}

	}
}
