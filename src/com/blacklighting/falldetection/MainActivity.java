package com.blacklighting.falldetection;

import java.lang.ref.WeakReference;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {
	SharedPreferences mPreferences;
	Button switchButton;
	final static int FIRST_RUN_WHAT = 0;
	MHandler mHandler = new MHandler(MainActivity.this);

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

		if (mPreferences.getBoolean("first_run", true)) {
			Editor ed = mPreferences.edit();
			ed.putBoolean("first_run", false);
			ed.commit();
			mHandler.sendEmptyMessage(FIRST_RUN_WHAT);
		}
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
		Intent i = new Intent(MainActivity.this, DetectionServer.class);
		if (mPreferences.getBoolean("serviceSwitch", true)) {
			startService(i);
			switchButton.setText("监测服务已经开启");
			switchButton.setBackgroundColor(Color.RED);
		} else {
			stopService(i);
			switchButton.setText("监测服务已经关闭");
			switchButton.setBackgroundColor(Color.YELLOW);
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
							"电子科技大学\n黑色之光创意工作室\n刘亚军(设计、算法、编码）\n付敏（设计、美工、文档）\n谢弘宸（测试、文档）\n liuyajun52@gmail.com")
					.setPositiveButton("确定", null);
			builder.create().show();
			break;
		case R.id.switchButton:
			Editor ed = mPreferences.edit();
			boolean serviceState = mPreferences.getBoolean("serviceSwitch",
					true);
			ed.putBoolean("serviceSwitch", !serviceState);
			ed.commit();
			Intent i = new Intent(MainActivity.this, DetectionServer.class);
			if (!serviceState) {
				startService(i);
				switchButton.setText("监测服务已经开启");
				switchButton.setBackgroundColor(Color.RED);
			} else {
				stopService(i);
				switchButton.setText("监测服务已经关闭");
				switchButton.setBackgroundColor(Color.YELLOW);
			}
			Toast.makeText(MainActivity.this, serviceState ? "监测关闭" : "监测开启",
					Toast.LENGTH_SHORT).show();
			break;
		}
	}

	static class MHandler extends Handler {
		private WeakReference<MainActivity> act;

		public MHandler(MainActivity act) {
			this.act = new WeakReference<MainActivity>(act);
		}

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case FIRST_RUN_WHAT:
				Intent first_run = new Intent(act.get(), FirstRunActivity.class);
				act.get().startActivity(first_run);
				break;
			}
		}

	}
}
