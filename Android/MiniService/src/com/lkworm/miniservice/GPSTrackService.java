package com.lkworm.miniservice;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Date;

import com.tencent.map.geolocation.TencentLocation;
import com.tencent.map.geolocation.TencentLocationListener;
import com.tencent.map.geolocation.TencentLocationManager;
import com.tencent.map.geolocation.TencentLocationRequest;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.LocationProvider;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.RemoteViews;


public class GPSTrackService extends Service {
	private static final int NOTIFICATION_ID = 1017;
	private static final String TAG = "GPSTrackManager";
	private Context context;

	private RandomAccessFile randFileWriter = null;	
	private ArrayList<TencentLocation> locations;	 
	private TencentLocationManager locationManager;
	private TencentLocationRequest request;

	private  String gpsTrackFolder = "mnt/sdcard/myTrackLog/" ;
	private  String gpsTrackFileEnd = "</trkseg>\r\n</trk>\r\n</gpx>";	

	private  int GPSAccuracy = 100; 
	private  long GPSInterval = 5*1000;//sec
	private int locationBufferSize = 10;
	private int runningCounter = 0;
	private String runningStr = "";

	private static int MSGCODE[] = new int[]{1111,9999};
	private static Message  message = new Message();


	public static void sendMSG(int i, String msg){//0,下方，1上方
		Bundle data = new Bundle();  
		data.putString("value", msg);
		message = MainActivity.mHandler.obtainMessage(MSGCODE[i], data);
		((Message) message).sendToTarget();
	}
	@Override
	public void onCreate() {
		super.onCreate();

	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {  
		startForegroundCompat();//设置成前台进程

		context = getApplicationContext();
		if(locations == null)
			locations = new ArrayList<TencentLocation>();	
		if(request == null)
			request = TencentLocationRequest.create();
		request.setInterval(GPSInterval);
		if(locationManager == null)
			locationManager = TencentLocationManager.getInstance(context);
		//判断GPS是否正常启动        
		int error = locationManager.requestLocationUpdates(request, locationListener);
		String errorStr = "注册监听器：";
		if(error !=0){
			switch(error)
			{
			case 0:  errorStr += "注册位置监听器成功";
			case 1:  errorStr += "设备缺少使用腾讯定位SDK需要的基本条件";
			break;
			case 2:  errorStr += "配置的 key 不正确";
			break;
			case 3:  errorStr += "自动加载libtencentloc.so失败";
			break;
			}
			LogMessage(true, errorStr);           
		}
		return super.onStartCommand(intent, flags, startId);  
	}
	@Override
	public void onDestroy() {
		// 退出前台服务，同时清掉状态栏通知。
		// 在api 18的版本上，这时候状态栏通知没了，但InnerService还在，且仍旧是前台服务状态，目的达到。
		stopForeground(true);
		locationManager.removeUpdates(locationListener);
		locations = null;
		request = null;
		locationManager = null;
		saveLocations();
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	public static boolean StartService(Context context) {
		context = context.getApplicationContext();
		context.startService(new Intent(context, GPSTrackService.class));
		return true;
	}
	public static boolean StopService(Context context) {
		context = context.getApplicationContext();
		if (Build.VERSION.SDK_INT < 18) {
			context.stopService(new Intent(context, GPSTrackService.class));
		} else {
			context.stopService(new Intent(context, InnerService.class));
		}

		return true;
	}


	private void startForegroundCompat() {

		if (Build.VERSION.SDK_INT < 18) {
			// api 18（4.3）以下，随便玩
			startForeground(NOTIFICATION_ID, new Notification());
			LogMessage(true,"startForeground<18");
		} else {
			// api 18的时候，google管严了，得绕着玩
			// 先把自己做成一个前台服务，提供合法的参数
			startForeground(NOTIFICATION_ID, fadeNotification(this));
			LogMessage(true,"startForeground>18");
			// 再起一个服务，也是前台的
			startService(new Intent(this, InnerService.class));
		}
	}

	public static class InnerService extends Service {
		final String TAG = "AlipayService";
		@Override
		public void onCreate() {
			super.onCreate();
			// 先把自己也搞成前台的，提供合法参数
			startForeground(NOTIFICATION_ID, fadeNotification(this));
			LogMessage(true,"InnerService+startForeground");
			// 关键步骤来了：自行推掉，或者把AlipayService退掉。
			// duang！系统sb了，说好的人与人的信任呢？
			stopSelf();
			LogMessage(true,"InnerService+stopSelf");
		}

		@Override
		public void onDestroy() {
			stopForeground(true);
			super.onDestroy();
		}

		@Override
		public IBinder onBind(Intent intent) {
			return null;
		}
	}

	private static Notification fadeNotification(Context context) {
		Notification notification = new Notification();
		// 随便给一个icon，反正不会显示，只是假装自己是合法的Notification而已
		notification.icon = R.drawable.ic_launcher;
		notification.contentView = new RemoteViews(context.getPackageName(), R.layout.notification_view);
		return notification;
	}


	//以下 GPS manager
	private   TencentLocationListener  locationListener= new TencentLocationListener () {

		@SuppressLint("DefaultLocale")
		@Override
		public void onLocationChanged(TencentLocation location, int error, String reason)
		{	
			runningCounter ++;
			runningStr +=".";
			if(runningCounter>=6){
				runningCounter = 0;
				runningStr = "";
			}
			if(location != null && location.getAccuracy()<GPSAccuracy && location.getAccuracy()>0.1){
				locations.add(location);
				if(locations.size()>locationBufferSize){
					saveLocations();
				}
				String str = String.format("当前位置:[%s]\r\n"
						+ "                  定位方式:[%s]精度：%.0fm\t%s",location.getName(), location.getProvider(),location.getAccuracy(),runningStr);
				LogMessage(true, str);  
			}
		}

		@Override
		public void onStatusUpdate(String name, int status, String desc) {   
			LogMessage(false, "onStatusUpdate:  name:" +name +"  desc    "+desc+String.format("   status   :%d", status));                
			switch (status) {            
			//GPS状态为可见时            
			case LocationProvider.AVAILABLE:                
				LogMessage(false, "当前GPS状态为可见状态");                
				break;            
				//GPS状态为服务区外时            
			case LocationProvider.OUT_OF_SERVICE:                
				LogMessage(false, "当前GPS状态为服务区外状态");                
				break;            
				//GPS状态为暂停服务时            
			case LocationProvider.TEMPORARILY_UNAVAILABLE:                
				LogMessage(false, "当前GPS状态为暂停服务状态");                
				break;            
			}        
		}                  
	};

	@SuppressLint("DefaultLocale")
	public boolean saveLocations() {
		if (createFile()) {
			if(locations == null || locations.size() == 0){
				return false;
			}			 
			try {
				randFileWriter = new RandomAccessFile(getGPSTrackPath(),"rw");
				randFileWriter.read();
				randFileWriter.seek(randFileWriter.length()-gpsTrackFileEnd.length());
				String positions = "";	
				for (int i = 0; i < locations.size(); i++) {
					String str = String.format("\r\n<trkpt lat=\"%f\" lon=\"%f\">\r\n"
							+ "<ele>%f</ele>\r\n"
							+ "<time>%s</time>\r\n"
							+ "</trkpt>",
							locations.get(i).getLatitude(), locations.get(i).getLongitude(),
							locations.get(i).getAltitude(),
							DateFormat.format("yyyy-MM-ddThh:mm:ssX", locations.get(i).getTime())
							);
					positions += str;
				}

				String content = positions + "\n"+gpsTrackFileEnd;
				randFileWriter.writeBytes(content);
				randFileWriter.close();
				randFileWriter = null;
				locations.clear();
			} catch (IOException e) {
				LogMessage(true,"saveLocations"+e.toString());
			}
		}	
		return true;
	}

	public String getGPSTrackPath() {
		return gpsTrackFolder+DateFormat.format("yymmdd", new Date())+".gpx";
	}

	private  boolean createFile(){
		File file = new File(getGPSTrackPath());
		if(file.exists() == false){
			try {
				file.createNewFile();
				FileWriter writer;//写入文件头
				try {
					writer = new FileWriter(getGPSTrackPath(),true);
					String content = getFileHander()+gpsTrackFileEnd;
					writer.write(content);
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}

			} catch (IOException e) {
				LogMessage(true,"createFile"+e.toString());
				return false;
			}
		}
		file = null;
		return true;
	}

	private static void LogMessage(boolean flag, String string) {
		Log.i(TAG,string);
		if(flag)sendMSG(0,DateFormat.format("hh.mm.ss", new Date())+"\t"+string);

	}

	public String getFileHander(){
		String str = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\" ?>"
				+ " \r\n<gpx xmlns=\"http://www.topografix.com/GPX/1/1\" xmlns:gpxtpx=\"http://www.garmin.com/xmlschemas/TrackPointExtension/v1\" creator=\"OruxMaps v.6.5.9\" version=\"1.1\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd\">"
				+ "  \r\n<metadata>"
				+ "  \r\n<name><![CDATA["+DateFormat.format("yymmdd", new Date())+"轨迹]]></name>"
				+ "  \r\n<desc><![CDATA[]]></desc>"
				+ "  \r\n<link href=\"http://www.oruxmaps.com\">"
				+ "  \r\n<text>OruxMaps</text>"
				+ "  \r\n</link>"
				+ "  \r\n<time>"+DateFormat.format("yyyy-MM-ddThh:mm:ssX", new Date())+"</time><bounds maxlat=\"40.0382312\" maxlon=\"116.2590316\" minlat=\"40.0046640\" minlon=\"116.1865904\"/>"
				+ "  \r\n</metadata>"
				+ "  \r\n<trk>"
				+ "  \r\n<name><![CDATA[轨迹记录]]></name>"
				+ "  \r\n<desc><![CDATA[<p>"+this.getdesc()+"</p><hr align=\"center\" width=\"480\" style=\"height: 2px; width: 517px\"/>]]></desc>"
				+ "  \r\n<type>日常</type>"
				+ "  \r\n<extensions>"
				+ "  \r\n<om:oruxmapsextensions xmlns:om=\"http://www.oruxmaps.com/oruxmapsextensions/1/0\">"
				+ "  \r\n<om:ext type=\"TYPE\" subtype=\"0\">28</om:ext>"
				+ "  \r\n<om:ext type=\"DIFFICULTY\">0</om:ext>"
				+ "  \r\n</om:oruxmapsextensions>"
				+ "  \r\n</extensions>"
				+ "  \r\n<trkseg> \r\n";			
		return str;
	}
	private String getdesc() {
		return "desc";
	}

}
