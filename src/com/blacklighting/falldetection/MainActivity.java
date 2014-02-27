package com.blacklighting.falldetection;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {
	SharedPreferences mPreferences;
	Button switchButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// addPreferencesFromResource(R.xml.setting);
		setContentView(R.layout.activity_main);
		mPreferences = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());

		switchButton = (Button) findViewById(R.id.switchButton);

		findViewById(R.id.guideButton).setOnClickListener(this);
		findViewById(R.id.settingButton).setOnClickListener(this);
		findViewById(R.id.aboutButton).setOnClickListener(this);
		switchButton.setOnClickListener(this);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Builder builder = new Builder(MainActivity.this);
			builder.setTitle("退出提示")
					.setMessage(
							mPreferences.getBoolean("serviceSwitch", true) ? "监测服务已经运行，您退出助手后，我们仍然时刻关注您的安全。"
									: "监测服务已经关闭，您确认要离开软件？")
					.setPositiveButton("确认",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									finish();
								}
							}).setNegativeButton("取消", null);
			builder.create().show();
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (mPreferences.getBoolean("serviceSwitch", true)) {
			Intent i = new Intent(MainActivity.this, DetectionServer.class);
			startService(i);
			switchButton.setText("监测服务已经开启");
		} else {
			switchButton.setText("监测服务已经关闭");
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.guideButton:
			Intent guide = new Intent(MainActivity.this, GuideActivity.class);
			startActivity(guide);
			break;
		case R.id.settingButton:
			Intent setting = new Intent(MainActivity.this,
					SettingActivity.class);
			startActivity(setting);
			break;
		case R.id.aboutButton:
			Builder builder = new Builder(MainActivity.this);
			builder.setTitle("关于")
					.setMessage(
							"电子科技大学\n黑色之光创意工作室\n刘亚军\n付敏\n谢弘宸\nliuyajun52@gmail.com")
					.setPositiveButton("确定", null);
			builder.create().show();
			break;
		case R.id.switchButton:
			Editor ed = mPreferences.edit();
			boolean serviceState = mPreferences.getBoolean("serviceSwitch",
					true);
			ed.putBoolean("serviceSwitch", !serviceState);
			ed.commit();
			switchButton.setText(!serviceState ? "监测服务已经开启" : "监测服务已经关闭");
			Toast.makeText(MainActivity.this, serviceState ? "监测关闭" : "监测开启",
					Toast.LENGTH_SHORT).show();
			break;
		}
	}
}
