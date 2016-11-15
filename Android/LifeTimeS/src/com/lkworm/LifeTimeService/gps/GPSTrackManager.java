package com.lkworm.LifeTimeService.gps;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.tencent.map.geolocation.TencentLocation;
import com.tencent.map.geolocation.TencentLocationListener;
import com.tencent.map.geolocation.TencentLocationManager;
import com.tencent.map.geolocation.TencentLocationRequest;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;


@SuppressLint("SimpleDateFormat")
public class GPSTrackManager extends Service {

	private  static final String TAG = "GPSTrackManager";
	private final String gpsTrackFileEnd = "</trkseg>\r\n</trk>\r\n</gpx>";	
	private static Context context;
	private boolean isStartTrack;
	private String gpsTrackFolder = "mnt/sdcard/myTrackLog/" ;
	private RandomAccessFile randFileWriter = null;	
	private ArrayList<TencentLocation> locations;	 
	private TencentLocationManager locationManager;
	private TencentLocationRequest request;

	private final int GPSAccuracy = 200; 
	private final long GPSInterval = 1*1000;//sec
	private int locationBufferSize = 6;
	private int MSGCODE[] = new int[]{1111,9999};
	
	private static Handler handler;
	private static Message message;
	private int runningCounter = 0;
	private String runningStr = "";

	private   TencentLocationListener  locationListener= new TencentLocationListener () {
		

		@Override
		public void onLocationChanged(TencentLocation location, int error, String reason)
		{	
			if (!isStartTrack) {
				locationManager.removeUpdates(locationListener);
				Log.i(TAG, "GPS，停止记录");  
				sendMSG(1,"GPS，停止记录");
			}else 
			{
				runningCounter ++;
				runningStr +=".";
				if(runningCounter>=6){
					runningCounter = 0;
					runningStr = "";
				}
				if(location.getAccuracy()>GPSAccuracy || location.getAccuracy()<1){
					Log.i("acrc", "GPS，精度--->"+String.format("%f", location.getAccuracy()));  
				}else{
					Log.i(TAG, "GPS，精度--->"+String.format("%f", location.getAccuracy()));  
				}
				if(location != null && location.getAccuracy()<GPSAccuracy && location.getAccuracy()>0.1){
					locations.add(location);
					if(locations.size()>locationBufferSize){
						saveLocations();
					}
					Log.i(TAG, "GPS，获取地址--->"+location.getName());  
				
					sendMSG(0,String.format("当前位置:[%s]精度：%.0fm\t%s",location.getName(), location.getAccuracy(),runningStr));
				}
			}
		}                

		@Override
		public void onStatusUpdate(String name, int status, String desc) {   
			Log.i(TAG, "onStatusUpdate");                
			switch (status) {            
			//GPS状态为可见时            
			case LocationProvider.AVAILABLE:                
				Log.i(TAG, "当前GPS状态为可见状态");                
				break;            
				//GPS状态为服务区外时            
			case LocationProvider.OUT_OF_SERVICE:                
				Log.i(TAG, "当前GPS状态为服务区外状态");                
				break;            
				//GPS状态为暂停服务时            
			case LocationProvider.TEMPORARILY_UNAVAILABLE:                
				Log.i(TAG, "当前GPS状态为暂停服务状态");                
				break;            
			}        
		}                  
	};


	public   GPSTrackManager(Context ct,Handler mHandler) {
		context = ct;
		handler = mHandler;
		message = new Message();  
	}
	public GPSTrackManager() {

	}

	public boolean tracklocations(boolean flag) {

		if(!flag){
			locationManager.removeUpdates(locationListener);
			return false;
		}
		//判断GPS是否正常启动        
		int error = locationManager.requestLocationUpdates(request, locationListener);
		String errorStr = "注册监听器：";
		if(error !=0){
			switch(error)
			{
			case 0:  errorStr += "注册位置监听器成功";
			break;
			case 1:  errorStr += "设备缺少使用腾讯定位SDK需要的基本条件";
			break;
			case 2:  errorStr += "配置的 key 不正确";
			break;
			case 3:  errorStr += "自动加载libtencentloc.so失败";
			break;
			}
			Log.i(TAG, errorStr);           
			return false;        
		}
		isStartTrack = true;	
		return true;		
	}


	@SuppressLint("DefaultLocale")
	public boolean saveLocations() {
		if (createFile()) {
			if(locations == null || locations.size() == 0){
				return false;
			}			 
			try {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				SimpleDateFormat sdf1 = new SimpleDateFormat("hh:mm:ss");

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
							sdf.format(locations.get(i).getTime())+"T"+sdf1.format(locations.get(i).getTime())+"Z");
					positions += str;
				}

				String content = positions + "\n"+gpsTrackFileEnd;
				randFileWriter.writeBytes(content);
				randFileWriter.close();
				locations.clear();
			} catch (IOException e) {
				Log.i(TAG,"saveLocations"+e.toString());
			}
		}	
		return true;
	}

	public String getGPSTrackPath() {
		return gpsTrackFolder+defineFileName()+".gpx";
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
				Log.i(TAG,"createFile"+e.toString());
				return false;
			}
		}
		return true;
	}

	private String  defineFileName() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		return sdf.format(new Date());
	}
	private String getGPSTime() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdf1 = new SimpleDateFormat("hh:mm:ss");
		return sdf.format(new Date())+"T"+sdf1.format(new Date())+"Z";
	} 

	public String getFileHander(){
		String str = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\" ?>"
				+ " \r\n<gpx xmlns=\"http://www.topografix.com/GPX/1/1\" xmlns:gpxtpx=\"http://www.garmin.com/xmlschemas/TrackPointExtension/v1\" creator=\"OruxMaps v.6.5.9\" version=\"1.1\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd\">"
				+ "  \r\n<metadata>"
				+ "  \r\n<name><![CDATA["+this.defineFileName()+"轨迹]]></name>"
				+ "  \r\n<desc><![CDATA[]]></desc>"
				+ "  \r\n<link href=\"http://www.oruxmaps.com\">"
				+ "  \r\n<text>OruxMaps</text>"
				+ "  \r\n</link>"
				+ "  \r\n<time>"+this.getGPSTime()+"</time><bounds maxlat=\"40.0382312\" maxlon=\"116.2590316\" minlat=\"40.0046640\" minlon=\"116.1865904\"/>"
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

	@Override
	public IBinder onBind(Intent intent) {
		Log.i(TAG, "Service onBind--->");  
		tracklocations(false);
		return null;
	}
	@Override 
	//Service时被调用  
	public void onCreate()  
	{  
		Log.i(TAG, "Service onCreate--->");  
		super.onCreate();  
	}  

	@Override  
	public int onStartCommand(Intent intent, int flags, int startId) {  
		Log.d(TAG, "onStartCommand() executed");  

		locations = new ArrayList<TencentLocation>();		  
		request = TencentLocationRequest.create();
		request.setInterval(GPSInterval);
		locationManager = TencentLocationManager.getInstance(context);
		locationManager.requestLocationUpdates(request,locationListener);

		tracklocations(true);
		return super.onStartCommand(intent, flags, startId);  
	}  
	@Override 
	//当Service不在使用时调用  
	public void onDestroy()  
	{  
		Log.i(TAG, "Service onDestroy--->"); 
		tracklocations(false);
		saveLocations();
		super.onDestroy();  

	}  
	public void sendMSG(int i, String msg){
		Bundle data = new Bundle();  
		data.putString("value", msg);
		message = handler.obtainMessage(MSGCODE[i], data);
		((Message) message).sendToTarget();
	}
 
}
