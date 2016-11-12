package com.lkworm.LifeTimeService;

import com.lkworm.LifeTimeService.gps.GPSTrackManager;
import com.lkworm.LifeTimeService.puh3.SmsObserver;

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


	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//UI
		final CheckBox checkboxPuh3 = (CheckBox)findViewById(R.id.checkBoxPuh3);
		final CheckBox checkboxTrack = (CheckBox)findViewById(R.id.checkBoxTrack);
		final TextView msgTextUp = (TextView)findViewById(R.id.MsgUp);
		final TextView msgTextDown = (TextView)findViewById(R.id.MsgDown);
		
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
						Log.i(TAG,"��������ʧ��");
						msgTextUp.setText("��������ʧ��");
					}
					else{
						Log.i(TAG,"��������ɹ�");
						msgTextUp.setText("��������ɹ�");
					}
				}
				else
				{
					stopService(startIntent);  				 
					Log.i(TAG, "�ر�λ�÷���");    
					msgTextUp.setText("�ر�λ�÷���");
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
					msgTextUp.setText("��ʼ��ض�����֤��");
				}else{
					getContentResolver().unregisterContentObserver(smsObserver); 
					msgTextUp.setText("ֹͣ��ض�����֤��");
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
