package com.blacklighting.falldetection;

import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.Menu;
import android.widget.Toast;

public class MainActivity extends PreferenceActivity {
	boolean hasHeightSeting, hasPositionSeting, hasPhoneSeting,
			hasRingtoneSeting;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.setting);
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		Intent i=new Intent(MainActivity.this,DetectionServer.class);
		startService(i);

		sp.registerOnSharedPreferenceChangeListener(new OnSharedPreferenceChangeListener() {

			@Override
			public void onSharedPreferenceChanged(SharedPreferences arg0,
					String arg1) {
				if (arg1.equals("height")) {
					hasHeightSeting = true;
				} else if (arg1.equals("position")) {
					hasPositionSeting = true;
				} else if (arg1.equals("phone")) {
					hasPhoneSeting = true;
				} else if (arg1.equals("ringtone")) {
					hasRingtoneSeting = true;
				} 

				if (hasHeightSeting && hasPositionSeting && hasPhoneSeting
						&& hasRingtoneSeting) {
					Toast.makeText(getApplicationContext(), "设置完成，检测服务启动",
							Toast.LENGTH_SHORT).show();
					startService(new Intent(MainActivity.this,
							DetectionServer.class));
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (!(hasHeightSeting && hasPositionSeting && hasPhoneSeting
					&& hasRingtoneSeting )) {
				Builder builder = new Builder(MainActivity.this);
				builder.setTitle("提示")
						.setMessage("还未完成设置，退出后检测服务将不会启动，确定要退出吗？")
						.setNegativeButton("取消", null)
						.setPositiveButton("确认",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface arg0,
											int arg1) {
										finish();
									}
								});
				builder.create().show();
			}else{
				finish();
			}
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}

}
