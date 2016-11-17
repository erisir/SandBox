package com.lkworm.miniservice;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ScrollView;
import android.widget.TextView;

public class MainActivity extends Activity {

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
					mHandler.post(new Runnable() {  
					    @Override  
					    public void run() {  
					    	scrollView.fullScroll(ScrollView.FOCUS_DOWN);   
					    }  
					});
				}
				break;
			case 9999:
				msgText.setText("\r\n"+val);
				break;

			}
		}
	};
	private static CheckBox showLogCheck;
	private static ScrollView scrollView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		msgText = (TextView)findViewById(R.id.msg);
		showLogCheck = (CheckBox)findViewById(R.id.showLogCheck);
		scrollView = (ScrollView)findViewById(R.id.scrollView1);
		showLogCheck.setChecked(true);
		IntentFilter mTime = new IntentFilter(Intent.ACTION_TIME_TICK);
		MyBroadcastReceiver receiver = new MyBroadcastReceiver();
		registerReceiver(receiver, mTime);
	}
	public void onShowLogCheck(View view){
		msgText.setText("");		
	}

}
