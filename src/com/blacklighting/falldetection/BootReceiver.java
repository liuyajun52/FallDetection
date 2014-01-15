/**
 * 
 */
package com.blacklighting.falldetection;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**开机启动监听器，用于在开机启动完成后启动行为识别服务
 * @author liuyajun
 *
 */
public class BootReceiver extends BroadcastReceiver {

	/* (non-Javadoc)
	 * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
	 */
	@Override
	public void onReceive(Context arg0, Intent arg1) {
		// TODO Auto-generated method stub
		arg0.startService(new Intent(arg0,DetectionServer.class));
	}

}
