/**
 * 
 */
package com.blacklighting.falldetection;

import com.blacklighting.falldetection.collectdata.DataCollector;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**行为识别服务
 * @author liuyajun
 *
 */
public class DetectionServer extends Service {
	DataCollector collector;

	/* (non-Javadoc)
	 * @see android.app.Service#onBind(android.content.Intent)
	 */
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		collector=new DataCollector(getApplicationContext());
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		collector.beginCollectData();
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		collector.stopCollectData();
		super.onDestroy();
	}

	
	
}
