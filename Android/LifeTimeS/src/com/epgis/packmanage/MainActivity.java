package com.epgis.packmanage;

import com.epgis.packmanage.gps.GPSTrackManager;
import com.epgis.packmanage.puh3.SmsObserver;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;


public class MainActivity extends Activity {
 
	Dialog alertDialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//puh3 smsSever
		ContentResolver cr = getContentResolver();  
		
 
	
		final CheckBox checkboxPuh3 = (CheckBox)findViewById(R.id.checkBoxPuh3);
		final CheckBox checkboxTrack = (CheckBox)findViewById(R.id.checkBoxTrack);

		final TextView textView = (TextView)findViewById(R.id.textView1);
		
		final SmsObserver smsObserver = new SmsObserver(this,cr,textView);
		
		final GPSTrackManager GM  = new GPSTrackManager(this);
	 	

		checkboxTrack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(checkboxTrack.isChecked()){
					textView.setText("正在记录轨迹");
					GM.tracklocations();
				}
				else
				{
				GM.saveLocations();				 
				textView.setText("停止记录轨迹");
				textView.setText(GM.getTrackList().toString());
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
					textView.setText("已经开始监控");
				}else{
					getContentResolver().unregisterContentObserver(smsObserver); 
					textView.setText("已经停止监控");
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
