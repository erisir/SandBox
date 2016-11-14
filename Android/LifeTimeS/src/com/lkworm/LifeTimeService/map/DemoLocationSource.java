package com.lkworm.LifeTimeService.map;

import com.tencent.map.geolocation.TencentLocation;
import com.tencent.map.geolocation.TencentLocationListener;
import com.tencent.map.geolocation.TencentLocationManager;
import com.tencent.map.geolocation.TencentLocationRequest;
import com.tencent.tencentmap.mapsdk.maps.LocationSource;

import android.content.Context;
import android.location.Location;
import android.util.Log;

public class DemoLocationSource implements LocationSource, TencentLocationListener {
	private  static final String TAG = "MainActivity";
	private Context mContext;
	private OnLocationChangedListener mChangedListener;
	private TencentLocationManager locationManager;
	private TencentLocationRequest locationRequest;
	
	public DemoLocationSource(Context context) {
		// TODO Auto-generated constructor stub
		mContext = context;
		locationManager = TencentLocationManager.getInstance(mContext);
		locationRequest = TencentLocationRequest.create();
		locationRequest.setInterval(2000);
	}
	
	@Override
	public void onLocationChanged(TencentLocation arg0, int arg1,
			String arg2) {
		// TODO Auto-generated method stub
		if (arg1 == TencentLocation.ERROR_OK && mChangedListener != null) {
			Log.e("maplocation", "location: " + arg0.getCity() + " " + arg0.getProvider());
			Location location = new Location(arg0.getProvider());
			location.setLatitude(arg0.getLatitude());
			location.setLongitude(arg0.getLongitude());
			location.setAccuracy(arg0.getAccuracy());
			mChangedListener.onLocationChanged(location);
		}
	}

	@Override
	public void onStatusUpdate(String arg0, int arg1, String arg2) {
		// TODO Auto-generated method stub
	}

	@Override
	public void activate(OnLocationChangedListener arg0) {
		// TODO Auto-generated method stub
		mChangedListener = arg0;
		int err = locationManager.requestLocationUpdates(locationRequest, this);
		switch (err) {
		case 1:
			Log.i(TAG,"设备缺少使用腾讯定位服务需要的基本条件");
			break;
		case 2:
			Log.i(TAG,"manifest 中配置的 key 不正确");
			break;
		case 3:
			Log.i(TAG,"自动加载libtencentloc.so失败");
			break;

		default:
			break;
		}
	}

	@Override
	public void deactivate() {
		// TODO Auto-generated method stub
		locationManager.removeUpdates(this);
		mContext = null;
		locationManager = null;
		locationRequest = null;
		mChangedListener = null;
	}
	
	public void onPause() {
		locationManager.removeUpdates(this);
	}
	
	public void onResume() {
		locationManager.requestLocationUpdates(locationRequest, this);
	}
	
}
