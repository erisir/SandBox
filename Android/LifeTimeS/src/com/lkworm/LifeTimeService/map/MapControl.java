package com.lkworm.LifeTimeService.map;

import com.tencent.tencentmap.mapsdk.maps.CameraUpdate;
import com.tencent.tencentmap.mapsdk.maps.CameraUpdateFactory;
import com.tencent.tencentmap.mapsdk.maps.TencentMap;
import com.tencent.tencentmap.mapsdk.maps.TencentMap.CancelableCallback;
import com.tencent.tencentmap.mapsdk.maps.TencentMap.OnCameraChangeListener;
import com.tencent.tencentmap.mapsdk.maps.TencentMap.OnCompassClickedListener;
import com.tencent.tencentmap.mapsdk.maps.TencentMap.OnMapClickListener;
import com.tencent.tencentmap.mapsdk.maps.TencentMap.OnMapLongClickListener;
import com.tencent.tencentmap.mapsdk.maps.model.CameraPosition;
import com.tencent.tencentmap.mapsdk.maps.model.LatLng;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;

public class MapControl  extends FragmentActivity implements 
OnCameraChangeListener, OnCompassClickedListener, OnMapClickListener, 
OnMapLongClickListener, CancelableCallback {
	private  static final String TAG = "MapControl";
	private TencentMap tencentMap;
	//	private SlidingDrawer slidingDrawer;
	//	private CheckBox cbScrollBy;
	//	private CheckBox cbCustZoom;
	//	private CheckBox cbLog;
	//	private CheckBox cbMapAnimation;
	public MapControl(TencentMap map){
		tencentMap = map;
		bindListener();
	}
	protected void bindListener() {
		tencentMap.setOnCameraChangeListener(this);
		tencentMap.setOnCompassClickedListener(this);
		tencentMap.setOnMapClickListener(this);
		tencentMap.setOnMapLongClickListener(this);
	}

	public void MoveCameraPosition(double lat,double lng) {
		CameraUpdate cameraSigma = 
				CameraUpdateFactory.newCameraPosition(new CameraPosition(
						new LatLng(lat,lng), tencentMap.getCameraPosition().zoom , 0f, 0f));
		tencentMap.animateCamera(cameraSigma, this);
	}

	public void onStopAnimationClicked(View view) {
		tencentMap.stopAnimation();
	}

	@Override
	public void onCameraChange(CameraPosition arg0) {
		// TODO Auto-generated method stub
		Log.i(TAG, "On Camera Change!");
	}

	@Override
	public void onCompassClicked() {
		// TODO Auto-generated method stub
		Log.i(TAG, "On Compass Clicked!");
	}

	@Override
	public void onMapLongClick(LatLng arg0) {
		// TODO Auto-generated method stub
		Log.i(TAG,"On Map Long Click!");
	}

	@Override
	public void onMapClick(LatLng arg0) {
		// TODO Auto-generated method stub
		Log.i(TAG, "On Map Click!");
	}

	@Override
	public void onCancel() {
		// TODO Auto-generated method stub
		Log.i(TAG, "动画取消");
	}

	@Override
	public void onFinish() {
		// TODO Auto-generated method stub
		Log.i(TAG, "动画结束");
	}

	@Override
	public void onCameraChangeFinished(CameraPosition arg0) {
		// TODO Auto-generated method stub
		Log.i(TAG, "camera change finished");
	}
}
