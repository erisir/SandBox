package com.epgis.packmanage;

import com.epgis.packmanage.gps.GPSTrackManager;

import com.epgis.packmanage.puh3.SmsObserver;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.TextView;

public class MainActivity extends Activity{
 
	Dialog alertDialog;
	private Handler mHandler;
	 GPSTrackManager gpsTrackManager = null;
	 SmsObserver smsObserver = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//puh3 smsSever
		ContentResolver cr = getContentResolver();  
		
 
	
		final CheckBox checkboxPuh3 = (CheckBox)findViewById(R.id.checkBoxPuh3);
		final CheckBox checkboxTrack = (CheckBox)findViewById(R.id.checkBoxTrack);

		final TextView textView = (TextView)findViewById(R.id.textView1);
		
		
		mHandler = new Handler(Looper.getMainLooper()) {
		    @Override
		    public void handleMessage(Message msg) {
		    	super.handleMessage(msg);
				String val = ((Bundle)msg.obj).getString("value") ;
	 	        
				switch (msg.what) {
				case 8899:
					textView.setText(val);
					break;
				 
				}
		    }
		};
		
		smsObserver = new SmsObserver(this,cr,mHandler);		
		gpsTrackManager  = new GPSTrackManager(this);
		 

		checkboxTrack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(checkboxTrack.isChecked()){
					textView.setText("���ڼ�¼�켣");
					gpsTrackManager.tracklocations(true);
				}
				else
				{
				gpsTrackManager.saveLocations();
				gpsTrackManager.addFileTail();
				gpsTrackManager.tracklocations(false);
				textView.setText("ֹͣ��¼�켣");
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
					textView.setText("�Ѿ���ʼ���");
				}else{
					getContentResolver().unregisterContentObserver(smsObserver); 
					textView.setText("�Ѿ�ֹͣ���");
				}

			}
		});		 
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	
}
