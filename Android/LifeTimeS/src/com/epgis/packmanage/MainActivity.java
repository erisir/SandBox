package com.epgis.packmanage;

import com.epgis.packmanage.gps.DownloadUtil;
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
import android.widget.TextView;


public class MainActivity extends Activity {
	DownloadUtil downloadUtil;
	Dialog alertDialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//puh3 smsSever
		ContentResolver cr = getContentResolver();  
		final SmsObserver smsObserver = new SmsObserver(this,cr);

		//puh3
		//		downloadUtil = new DownloadUtil(this);
		//		if (downloadUtil.isUpdate()) {
		//			alertDialog = new AlertDialog.Builder(this). 
		//	                 setTitle("更新提示！"). 
		//	                 setMessage("发现新的版本，是否更新？"). 
		//	                 setIcon(R.drawable.ic_launcher). 
		//	                 setPositiveButton("确定", new DialogInterface.OnClickListener() { 
		//	                      
		//	                     @Override 
		//	                     public void onClick(DialogInterface dialog, int which) { 
		//	                         // TODO Auto-generated method stub  
		//	                    	 downloadUtil.update();
		//	                     } 
		//	                 }). 
		//	                 setNegativeButton("取消", new DialogInterface.OnClickListener() { 
		//	                      
		//	                     @Override 
		//	                     public void onClick(DialogInterface dialog, int which) { 
		//	                         // TODO Auto-generated method stub  
		//	                    	 alertDialog.dismiss();
		//	                     } 
		//	                 }). 
		//	                 create(); 
		//	         alertDialog.show(); 
		//		}
		final GPSTrackManager GM  = new GPSTrackManager(this);

		final Button startTrack = (Button)findViewById(R.id.startTrack);

		final Button stopTrack = (Button)findViewById(R.id.stopTrack);
		/*final Button startPuh3Server = (Button)findViewById(R.id.startTrack);
		final Button stopPuh3Server = (Button)findViewById(R.id.stopTrack);
		*/

		final TextView textView = (TextView)findViewById(R.id.textView1);
		startTrack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				GM.tracklocations();
				startTrack.setVisibility(View.GONE);
				stopTrack.setVisibility(View.VISIBLE);
			}
		});

		stopTrack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				GM.saveLocations();
				textView.setText(GM.getTrackList().toString());

			}
		});
		/*startPuh3Server.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				getContentResolver().registerContentObserver(smsObserver.SMS_INBOX, true,  
						smsObserver); 

			}
		});
		stopPuh3Server.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				getContentResolver().unregisterContentObserver(smsObserver); 
				textView.setText("已经停止监控");
			}
		});*/
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
