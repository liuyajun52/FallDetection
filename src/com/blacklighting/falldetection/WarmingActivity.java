package com.blacklighting.falldetection;

import java.lang.ref.WeakReference;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
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
	private SmsManager smsManager;
	private MHandler mHandler = new MHandler(this);;
	private String phoneNumber;
	private String smsContent = "软件检测到您的家属发生了跌倒，请尽快采取措施，位置：";
	private TextView counterView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_warming);
		counterView = (TextView) findViewById(R.id.counter);

		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		smsManager = SmsManager.getDefault();
		phoneNumber = PreferenceManager.getDefaultSharedPreferences(
				getApplicationContext()).getString("phone", null);
		findViewById(R.id.concleButton).setOnClickListener(this);

		// 注册位置监听器，开始收集地理位置信息
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				1000, 0, WarmingActivity.this);

		new CountThread().start();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		locationManager.removeUpdates(WarmingActivity.this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.warming, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

	}

	class CountThread extends Thread {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();

			// 倒数一分钟
			for (int i = 0; i < 60; i++) {
				if (isInterrupted()) {
					return;
				}
				try {
					sleep(1000);
					mHandler.sendEmptyMessage(60 - i);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			// 发送求助短信
			if (phoneNumber != null) {
				smsManager.sendTextMessage(phoneNumber, null,
						smsContent
								+ (currentLocation == null ? "未知"
										: currentLocation.toString()), null,
						null);
			}
			mHandler.sendEmptyMessage(-1);
		}

		@Override
		public void interrupt() {
			// TODO Auto-generated method stub
			super.interrupt();
			mHandler.sendEmptyMessage(-1);
		}

	}

	static class MHandler extends Handler {
		private WeakReference<WarmingActivity> act;

		public MHandler(WarmingActivity act) {
			this.act = new WeakReference<WarmingActivity>(act);
		}

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if (msg.what == -1) {
				act.get().finish();
			} else {
				act.get().counterView.setText("" + msg.what);
			}
		}

	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		currentLocation = location;
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

}
