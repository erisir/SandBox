package com.lkworm.LifeTimeService;

import com.lkworm.LifeTimeService.gps.GPSTrackManager;
import com.lkworm.LifeTimeService.gps.TrackFileManager;
import com.lkworm.LifeTimeService.map.DemoLocationSource;
import com.lkworm.LifeTimeService.map.MapControl;
import com.lkworm.LifeTimeService.puh3.SmsObserver;
import com.tencent.tencentmap.mapsdk.maps.SupportMapFragment;
import com.tencent.tencentmap.mapsdk.maps.TencentMap;
import com.tencent.tencentmap.mapsdk.maps.UiSettings;

import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

public class MainActivity extends FragmentActivity{
	private  static final String TAG = "MainActivity";
	private Handler mHandler;
	private Intent startIntent;
	private  SmsObserver smsObserver;
	@SuppressWarnings("unused")
	private  GPSTrackManager gpsTrackManager;
	FragmentManager fragmentManager;
	private Message  message;
	private TencentMap tencentMap;
	private int MSGCODE[] = new int[]{1111,9999};
	private DemoLocationSource locationSource;
	@SuppressWarnings("unused")
	private MapControl mapControl;
	private UiSettings mapUiSettings;

	private CheckBox cbAllGesture;
	private CheckBox cbCompass;
	private CheckBox cbZoomWidget;
	private CheckBox cbLocationWidget;
	private CheckBox cbRotateGesture;
	private CheckBox cbScrollGesture;
	private CheckBox cbTiltGesture;
	private CheckBox cbZoomGesture;
	private CheckBox checkboxPuh3;
	private CheckBox checkboxTrack;
	private TextView msgTextUp;
	private TextView msgTextDown;
	private CheckBox overWriteTrack;
	private CheckBox cbSatellite;
	private CheckBox cbTraffic;

	private TrackFileManager trackFileManager;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);


		FragmentManager fm = getSupportFragmentManager();
		SupportMapFragment mapFragment = 
				(SupportMapFragment) fm.findFragmentById(R.id.frag_map);

		mHandler = new Handler(Looper.getMainLooper()) {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				String val = ((Bundle)msg.obj).getString("value") ;
				switch (msg.what) {
				case 1111:
					msgTextDown.setText(val);
					break;
				case 9999:
					msgTextUp.setText(val);
					break;
				}
			}
		};
		tencentMap = mapFragment.getMap();
		//定位开始
		//设置是否显示我的位置，地图SDK不负责获取位置，由外界提供 设计的地图和定位是分开的，
		//所以需要setLocationSource(locationSource);提供接口
		locationSource = new DemoLocationSource(this);
		tencentMap.setLocationSource(locationSource);
		tencentMap.setMyLocationEnabled(true);

		trackFileManager = new TrackFileManager(this,this,tencentMap);
		mapControl = new MapControl(tencentMap);
		//puh3 smsSever

		smsObserver = new SmsObserver(getContentResolver(),mHandler);		
		gpsTrackManager  = new GPSTrackManager(this,mHandler);

		initMapUI();
		bindMapUIListener();
		cbAllGesture.setChecked(true);
	}
 
	@Override
	public void onStart() {//可视开始
		// TODO Auto-generated method stub
		super.onStart();
		Log.d(TAG, "onStart");

	}

	@Override
	public void onResume() {//可操作？开始
		// TODO Auto-generated method stub
		super.onResume();
		Log.d(TAG, "onResume");
	}

	@Override
	public void onPause() {//可操作？结束
		// TODO Auto-generated method stub
		super.onPause();
		Log.d(TAG, "onPause");

	}

	@Override
	public void onStop() {//可视结束
		// TODO Auto-generated method stub
		super.onStop();
		Log.d(TAG, "onStop");

	}

	@Override
	public void onDestroy() {//线程结束
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.d(TAG, "onDestroy");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void sendMSG(int i, String msg){//0,下方，1上方
		Bundle data = new Bundle();  
		data.putString("value", msg);
		message = mHandler.obtainMessage(MSGCODE[i], data);
		((Message) message).sendToTarget();
	}

	public void onLoadTrackFile(View view){
		trackFileManager.showTrack(overWriteTrack.isChecked());
	}
	public void onRemoveTrackFile(View view){
		trackFileManager.removeTrack();
	}
	protected void initMapUI() {
		mapUiSettings = tencentMap.getUiSettings();
		overWriteTrack = (CheckBox)findViewById(R.id.overWriteTrack);
		checkboxPuh3 = (CheckBox)findViewById(R.id.checkBoxPuh3);
		checkboxTrack = (CheckBox)findViewById(R.id.checkBoxTrack);
		msgTextUp = (TextView)findViewById(R.id.MsgUp);
		msgTextDown = (TextView)findViewById(R.id.MsgDown);

		cbAllGesture = (CheckBox)findViewById(R.id.cb_all_gesture);
		cbCompass = (CheckBox)findViewById(R.id.cb_compass);
		cbZoomWidget = (CheckBox)findViewById(R.id.cb_zoom_widget);
		cbLocationWidget = (CheckBox)findViewById(R.id.cb_location_button);
		cbRotateGesture = (CheckBox)findViewById(R.id.cb_rotate_gesture);
		cbScrollGesture = (CheckBox)findViewById(R.id.cb_scroll_gesture);
		cbTiltGesture = (CheckBox)findViewById(R.id.cb_tilt_gesture);
		cbZoomGesture = (CheckBox)findViewById(R.id.cb_zoom_gesture);
		cbSatellite = (CheckBox)findViewById(R.id.cb_satellite);
		cbTraffic = (CheckBox)findViewById(R.id.cb_traffic);


		if (tencentMap.getMapType() == TencentMap.MAP_TYPE_SATELLITE) {
			cbSatellite.setChecked(true);
		} else {
			cbSatellite.setChecked(false);
		}
		cbTraffic.setChecked(tencentMap.isTrafficEnabled());

		cbAllGesture.setChecked(true);
		cbCompass.setChecked(mapUiSettings.isCompassEnabled());
		cbZoomWidget.setChecked(mapUiSettings.isZoomControlsEnabled());
		cbLocationWidget.setChecked(mapUiSettings.isMyLocationButtonEnabled());
		cbRotateGesture.setChecked(mapUiSettings.isRotateGesturesEnabled());
		cbScrollGesture.setChecked(mapUiSettings.isScrollGesturesEnabled());
		cbTiltGesture.setChecked(mapUiSettings.isTiltGesturesEnabled());
		cbZoomGesture.setChecked(mapUiSettings.isZoomGesturesEnabled());
	}	

	private void bindService(){
		startIntent= new Intent(this, GPSTrackManager.class);
		ComponentName ret =  startService(startIntent);
		if(ret == null){
			Log.i(TAG,"开启服务失败");
			msgTextUp.setText("开启服务失败");
		}
		else{
			Log.i(TAG,"开启服务成功");
			msgTextUp.setText("开启服务成功");
		}
	} 
	protected void bindMapUIListener() {

		OnCheckedChangeListener onCheckedChangeListener = new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				switch (buttonView.getId()) {
				case R.id.checkBoxPuh3:
					if(isChecked)
					{
						getContentResolver().registerContentObserver(smsObserver.SMS_INBOX, true,  
								smsObserver); 
						msgTextUp.setText("开始监控短信验证码");
					}else{
						getContentResolver().unregisterContentObserver(smsObserver); 
						msgTextUp.setText("停止监控短信验证码");
					}
					break;
				case R.id.checkBoxTrack:
					if(isChecked){
						//						//startServiceThread.start();
						//						ComponentName ret =  startService(startIntent);
						//						if(ret == null){
						//							Log.i(TAG,"开启服务失败");
						//							msgTextUp.setText("开启服务失败");
						//						}
						//						else{
						//							Log.i(TAG,"开启服务成功");
						//							msgTextUp.setText("开启服务成功");
						//						}
						bindService();
					}
					else
					{
						stopService(startIntent);  				 
						Log.i(TAG, "位置服务关闭");    
					}
					break;
				case R.id.cb_all_gesture:
					mapUiSettings.setAllGesturesEnabled(isChecked);
					cbRotateGesture.setChecked(isChecked);
					cbScrollGesture.setChecked(isChecked);
					cbTiltGesture.setChecked(isChecked);	
					cbZoomGesture.setChecked(isChecked);

					mapUiSettings.setCompassEnabled(isChecked);
					mapUiSettings.setZoomControlsEnabled(isChecked);
					mapUiSettings.setMyLocationButtonEnabled(isChecked);
					break;
				case R.id.cb_compass:
					mapUiSettings.setCompassEnabled(isChecked);
					break;
				case R.id.cb_zoom_widget:
					mapUiSettings.setZoomControlsEnabled(isChecked);
					break;
				case R.id.cb_location_button:
					mapUiSettings.setMyLocationButtonEnabled(isChecked);
					break;
				case R.id.cb_rotate_gesture://旋转屏幕手势
					mapUiSettings.setRotateGesturesEnabled(isChecked);
					break;
				case R.id.cb_scroll_gesture:
					mapUiSettings.setScrollGesturesEnabled(isChecked);
					break;
				case R.id.cb_tilt_gesture:
					mapUiSettings.setTiltGesturesEnabled(isChecked);
					break;
				case R.id.cb_zoom_gesture:
					mapUiSettings.setZoomGesturesEnabled(isChecked);
					break;
				case R.id.cb_satellite:
					if (isChecked) {
						tencentMap.setMapType(TencentMap.MAP_TYPE_SATELLITE);
					} else {
						tencentMap.setMapType(TencentMap.MAP_TYPE_NORMAL);
					}
					break;
				case R.id.cb_traffic:
					tencentMap.setTrafficEnabled(isChecked);
					break;

				default:
					break;
				}
			}
		};
		checkboxPuh3.setOnCheckedChangeListener(onCheckedChangeListener);
		checkboxTrack.setOnCheckedChangeListener(onCheckedChangeListener);
		cbAllGesture.setOnCheckedChangeListener(onCheckedChangeListener);
		cbCompass.setOnCheckedChangeListener(onCheckedChangeListener);
		cbZoomWidget.setOnCheckedChangeListener(onCheckedChangeListener);
		cbLocationWidget.setOnCheckedChangeListener(onCheckedChangeListener);
		cbRotateGesture.setOnCheckedChangeListener(onCheckedChangeListener);
		cbScrollGesture.setOnCheckedChangeListener(onCheckedChangeListener);
		cbTiltGesture.setOnCheckedChangeListener(onCheckedChangeListener);
		cbZoomGesture.setOnCheckedChangeListener(onCheckedChangeListener);
		cbSatellite.setOnCheckedChangeListener(onCheckedChangeListener);
		cbTraffic.setOnCheckedChangeListener(onCheckedChangeListener);
	}

}
