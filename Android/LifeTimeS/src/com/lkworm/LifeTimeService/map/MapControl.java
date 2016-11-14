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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class MapControl  extends FragmentActivity implements 
OnCameraChangeListener, OnCompassClickedListener, OnMapClickListener, 
OnMapLongClickListener, CancelableCallback {
	private  static final String TAG = "MainActivity";
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
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
//		bindListener();
	}

	protected void bindListener() {
		tencentMap.setOnCameraChangeListener(this);
		tencentMap.setOnCompassClickedListener(this);
		tencentMap.setOnMapClickListener(this);
		tencentMap.setOnMapLongClickListener(this);

		OnCheckedChangeListener onCheckedChangeListener = 
				new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				switch (buttonView.getId()) {

				default:
					break;
				}
			}
		};
		//		cbCustZoom.setOnCheckedChangeListener(onCheckedChangeListener);
		//		cbScrollBy.setOnCheckedChangeListener(onCheckedChangeListener);
		//		cbLog.setOnCheckedChangeListener(onCheckedChangeListener);
	}

	public void onUpClicked(View view) {
		tencentMap.moveCamera(CameraUpdateFactory.scrollBy(0, -100));
	}

	public void onDownClicked(View view) {
		tencentMap.moveCamera(CameraUpdateFactory.scrollBy(0, 100));
	}

	public void onRightClicked(View view) {
		tencentMap.moveCamera(CameraUpdateFactory.scrollBy(100, 0));
	}

	public void onLeftClicked(View view) {
		tencentMap.moveCamera(CameraUpdateFactory.scrollBy(-100, 0));
	}

	public void onZoomOutClicked(View view) {
		tencentMap.moveCamera(CameraUpdateFactory.zoomOut());
	}

	public void onZoomInClicked(View view) {
		tencentMap.moveCamera(CameraUpdateFactory.zoomIn());
	}


	public void MoveCameraPosition(View view,double lat,double lng) {
		CameraUpdate cameraSigma = 
				CameraUpdateFactory.newCameraPosition(new CameraPosition(
						new LatLng(lat,lng), 19, 0f, 0f));
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
