package com.lkworm.LifeTimeService;

import com.lkworm.LifeTimeService.gps.GPSTrackManager;
import com.lkworm.LifeTimeService.puh3.SmsObserver;
import com.tencent.tencentmap.mapsdk.maps.MapView;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.TextView;

public class MainActivity extends Activity{
	private  static final String TAG = "MainActivity";
	private Handler mHandler;
	private Intent startIntent;
	private  SmsObserver smsObserver;
	@SuppressWarnings("unused")
	private  GPSTrackManager gpsTrackManager;
	//private MapView mMapView;

	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//UI
		final CheckBox checkboxPuh3 = (CheckBox)findViewById(R.id.checkBoxPuh3);
		final CheckBox checkboxTrack = (CheckBox)findViewById(R.id.checkBoxTrack);
		final TextView msgTextUp = (TextView)findViewById(R.id.MsgUp);
		final TextView msgTextDown = (TextView)findViewById(R.id.MsgDown);
		//mMapView = (MapView) findViewById(R.id.map);
		startIntent = new Intent(this, GPSTrackManager.class);

		//puh3 smsSever
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
		
		
		smsObserver = new SmsObserver(getContentResolver(),mHandler);		
		gpsTrackManager  = new GPSTrackManager(this,mHandler);
		 
		checkboxTrack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if(checkboxTrack.isChecked()){
					ComponentName ret = startService(startIntent);
					if(ret == null){
						Log.i(TAG,"开启服务失败");
						msgTextUp.setText("开启服务失败");
					}
					else{
						Log.i(TAG,"开启服务成功");
						msgTextUp.setText("开启服务成功");
					}
				}
				else
				{
					stopService(startIntent);  				 
					Log.i(TAG, "关闭位置服务");    
					msgTextUp.setText("关闭位置服务");
				}

			}
		});
		
		
		checkboxPuh3.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if(checkboxPuh3.isChecked())
				{
					getContentResolver().registerContentObserver(smsObserver.SMS_INBOX, true,  
						smsObserver); 
					msgTextUp.setText("开始监控短信验证码");
				}else{
					getContentResolver().unregisterContentObserver(smsObserver); 
					msgTextUp.setText("停止监控短信验证码");
				}

			}
		});		 
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	
}
