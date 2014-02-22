package com.blacklighting.falldetection;

import java.lang.ref.WeakReference;
import java.util.List;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.Context;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class WarmingActivity extends Activity implements OnClickListener,
		LocationListener {
	private LocationManager locationManager;
	private Location currentLocation;
	private MHandler mHandler = new MHandler(this);;
	private String phoneNumber;
	private TextView counterView;
	private RingtoneManager mRingtoneManager;
	private Ringtone ringtone;
	CountThread countThread;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_warming);
		counterView = (TextView) findViewById(R.id.counter);
		findViewById(R.id.stopButton).setOnClickListener(this);

		// 获取设置的电话号码
		phoneNumber = PreferenceManager.getDefaultSharedPreferences(
				getApplicationContext()).getString("phone", null);

		// 播放提示音
		String ringtoneName = PreferenceManager.getDefaultSharedPreferences(
				getApplicationContext()).getString("ringtone", null);
		Uri ringtoneUri = ringtoneName == null ? RingtoneManager
				.getDefaultUri(RingtoneManager.TYPE_RINGTONE) : Uri
				.parse(ringtoneName);
		mRingtoneManager = new RingtoneManager(this);
		mRingtoneManager.setType(RingtoneManager.TYPE_ALARM); // 设置铃声类型
		ringtone = RingtoneManager.getRingtone(this, ringtoneUri);
		ringtone.play();

		// 注册位置监听器，开始收集地理位置信息
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				1000, 0, WarmingActivity.this);

		// 开始倒计时
		countThread = new CountThread();
		countThread.start();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 活动结束时结束收集地理位置信息
		locationManager.removeUpdates(WarmingActivity.this);
		if (countThread != null) {
			countThread.interrupt(); // 停止计时
			countThread = null;
		}
		if (ringtone.isPlaying()) {
			ringtone.stop(); // 停止播放提示音
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.warming, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.stopButton:
			finish();
			break;
		}
	}

	class CountThread extends Thread {
		private boolean isStoped=false;
		@Override
		public void run() {
			super.run();

			// 倒数一分钟
			for (int i = 0; i < 60; i++) {
				if (isStoped) {
					return;
				}
				try {
					sleep(1000);
					mHandler.sendEmptyMessage(60 - i);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			mHandler.sendEmptyMessage(-2);
		}

		@Override
		public void interrupt() {
			super.interrupt();
			isStoped=true;
		}

	}

	static class MHandler extends Handler {
		private WeakReference<WarmingActivity> act;
		private String smsContent = "软件检测到您的家属发生了跌倒，请尽快采取措施，位置：";
		private Location location;

		public MHandler(WarmingActivity act) {
			this.act = new WeakReference<WarmingActivity>(act);
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case -1:
				act.get().finish();
				break;
			case -2:
				// 发送求助短信
				if (act.get().phoneNumber != null) {
					// 直接调用短信接口发短信
					SmsManager smsManager = SmsManager.getDefault();
					location = act.get().currentLocation;
					List<String> divideContents = smsManager
							.divideMessage(smsContent
									+ " "
									+ (location == null ? "网络原因未知"
											: ("" + location.getAccuracy()
													+ " " + location
													.getAltitude())));
					for (String text : divideContents) {
						smsManager
								.sendTextMessage("+86" + act.get().phoneNumber,
										null, text, null, null);
					}
				}
				act.get().finish();
				break;
			default:
				act.get().counterView.setText("" + msg.what);
				break;
			}
		}

	}

	@Override
	public void onLocationChanged(Location location) {
		currentLocation = location;
	}

	@Override
	public void onProviderDisabled(String provider) {

	}

	@Override
	public void onProviderEnabled(String provider) {

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {

	}

}
