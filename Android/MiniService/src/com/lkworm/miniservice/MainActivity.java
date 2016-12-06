package com.lkworm.miniservice;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ScrollView;
import android.widget.TextView;

public class MainActivity extends Activity {
	private  static final String TAG = "MainActivity";
	private static TextView msgText;
	public static Handler mHandler = new Handler(Looper.getMainLooper()) {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			String val = ((Bundle)msg.obj).getString("value") ;
			switch (msg.what) {
			case 1111:
				if(showLogCheck.isChecked()){
					msgText.setText(msgText.getText()+"\r\n"+val);
					if(autoScroll.isChecked()){
						mHandler.post(new Runnable() {  
							@Override  
							public void run() {  
								scrollView.fullScroll(ScrollView.FOCUS_DOWN);   
							}  
						});
					}
				}
				break;
			case 9999:
				 
				break;

			}
		}
	};
	private static CheckBox showLogCheck;
	private static ScrollView scrollView;
	private static CheckBox autoScroll;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		msgText = (TextView)findViewById(R.id.msg);
		autoScroll = (CheckBox)findViewById(R.id.AutoScroll);
		showLogCheck = (CheckBox)findViewById(R.id.showLogCheck);
		scrollView = (ScrollView)findViewById(R.id.scrollView1);
		
		showLogCheck.setChecked(true);
		autoScroll.setChecked(true);
		
		
		IntentFilter mTime = new IntentFilter(Intent.ACTION_TIME_TICK);
		MyBroadcastReceiver receiver = new MyBroadcastReceiver();
		registerReceiver(receiver, mTime);
	}
	public void onShowLogCheck(View view){
		msgText.setText("");		
	}

	@Override
	public void onStart() {//可视开始
		// TODO Auto-generated method stub
		super.onStart();
		GPSTrackService.StartService(this);
//		GPSTrackService.LogError(TAG+"onStart");
		Log.d(TAG, "onStart");
	}

	@Override
	public void onResume() {//可操作？开始
		// TODO Auto-generated method stub
		super.onResume();
		GPSTrackService.StartService(this);
//		GPSTrackService.LogError(TAG+"onResume");
		Log.d(TAG, "onResume");
	}

	@Override
	public void onPause() {//可操作？结束
		// TODO Auto-generated method stub
		super.onPause();
		Log.d(TAG, "onPause");
//		GPSTrackService.LogError(TAG+"onPause");

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

}
