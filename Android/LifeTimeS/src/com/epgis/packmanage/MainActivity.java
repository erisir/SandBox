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
					textView.setText("���ڼ�¼�켣");
					GM.tracklocations();
				}
				else
				{
				GM.saveLocations();				 
				textView.setText("ֹͣ��¼�켣");
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
