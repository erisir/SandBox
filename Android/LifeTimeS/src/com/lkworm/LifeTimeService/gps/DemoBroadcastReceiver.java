package com.lkworm.LifeTimeService.gps;  

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.util.Log;  

public class DemoBroadcastReceiver  extends BroadcastReceiver {  

	@Override  
	public void onReceive(Context context, Intent intent) {  
		State wifiState = null;  
		State mobileState = null;  
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);  
		wifiState = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();  
		mobileState = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();  
		if (wifiState != null && mobileState != null  
				&& State.CONNECTED != wifiState  
				&& State.CONNECTED == mobileState) {  
			Intent service = new Intent(context,GPSTrackManager.class);  
			service.setAction("com.lkworm.LifeTimeService.gps.GPSTrackManager");  
			// 在广播中启动服务必须加上startService(intent)的修饰语。Context是对象  
			context.startService(service);  
			// 手机网络连接成功  
			startServer( context);
		} else if (wifiState != null && mobileState != null  
				&& State.CONNECTED != wifiState  
				&& State.CONNECTED != mobileState) {  
			// 手机没有任何的网络  
		} else if (wifiState != null && State.CONNECTED == wifiState) {  
			// 无线网络连接成功  
			startServer( context);
		}  

	}
	private void startServer(Context context){
		Log.i("DemoBroadcastReceiver","定位服务已经被广播唤醒");  
		Intent service = new Intent(context,GPSTrackManager.class);  
		service.setAction("com.lkworm.LifeTimeService.gps.GPSTrackManager");  
		// 在广播中启动服务必须加上startService(intent)的修饰语。Context是对象  
		context.startService(service);  
	}
}  
