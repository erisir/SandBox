package com.lkworm.LifeTimeService.map;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.lkworm.LifeTimeService.gps.GPSTrackManager;
import com.tencent.map.geolocation.TencentLocation;
import com.tencent.map.geolocation.TencentLocationListener;
import com.tencent.map.geolocation.TencentLocationManager;
import com.tencent.map.geolocation.TencentLocationRequest;
import com.tencent.tencentmap.mapsdk.maps.CameraUpdate;
import com.tencent.tencentmap.mapsdk.maps.CameraUpdateFactory;
import com.tencent.tencentmap.mapsdk.maps.LocationSource;
import com.tencent.tencentmap.mapsdk.maps.TencentMap;
import com.tencent.tencentmap.mapsdk.maps.TencentMap.CancelableCallback;
import com.tencent.tencentmap.mapsdk.maps.model.CameraPosition;
import com.tencent.tencentmap.mapsdk.maps.model.LatLng;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;

public class DemoLocationSource    implements LocationSource, TencentLocationListener {
	private  static final String TAG = "DemoLocationSource";
	private Context mContext;
	private OnLocationChangedListener mChangedListener;
	private TencentLocationManager locationManager;
	private TencentLocationRequest locationRequest;
	private TencentMap tencentMap;
	private MapControl mapControl;
	private final String gpsTrackFileEnd = "</trkseg>\r\n</trk>\r\n</gpx>";	
	private boolean isStartTrack;
	private String gpsTrackFolder = "mnt/sdcard/myTrackLog/" ;
	private RandomAccessFile randFileWriter = null;	
	private ArrayList<TencentLocation> locations;	 

	private final int GPSAccuracy = 200; 
	private final long GPSInterval = 1000;//sec
	private int locationBufferSize = 6;
	private int MSGCODE[] = new int[]{1111,9999};

	private static Handler handler;
	private static Message message;
	private int runningCounter = 0;
	private String runningStr = "";
	private Intent startIntent;
	
	public DemoLocationSource(Context context, MapControl mapC,Handler mHandler, Intent intent) {
		// TODO Auto-generated constructor stub
		mContext = context;
		mapControl = mapC;
		startIntent = intent;
		
		handler = mHandler;
 
		message = new Message();  
	}
	//LocationSource的回调  当我的位置有变化时，调用的接口。
	@Override
	public void onLocationChanged(TencentLocation arg0, int arg1,
			String arg2) {
		// TODO Auto-generated method stub
		Log.i(TAG, "onLocationChanged");  
		if (arg1 == TencentLocation.ERROR_OK && mChangedListener != null) {
			Log.e("maplocation", "location: " + arg0.getCity() + " " + arg0.getProvider());
			Location location = new Location(arg0.getProvider());
			location.setLatitude(arg0.getLatitude());
			location.setLongitude(arg0.getLongitude());
			location.setAccuracy(arg0.getAccuracy());
			//LocationSource..onLocationChanged(location) 新的位置信息设置函数，由调用者把新的位置信息传给地图SDK
			mChangedListener.onLocationChanged(location);
			//	\mapControl.MoveCameraPosition(null, arg0.getLatitude(), arg0.getLongitude());
			if (!isStartTrack) {
				Log.i(TAG, "GPS，停止记录");  
				sendMSG(1,"GPS，停止记录");
//				deactivate() ;
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
					locations.add(arg0);
					if(locations.size()>locationBufferSize){
						saveLocations();
					}
					Log.i(TAG, "GPS，获取地址--->"+arg0.getName());  

					sendMSG(0,String.format("当前位置:[%s]精度：%.0fm\t%s",arg0.getName(), arg0.getAccuracy(),runningStr));
				}
			}
			
			////画图
			
		}


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
	private String getGPSTrackPath() {
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
	public void sendMSG(int i, String msg){
		Bundle data = new Bundle();  
		data.putString("value", msg);
		message = handler.obtainMessage(MSGCODE[i], data);
		((Message) message).sendToTarget();
	}
	
	@Override
	public void onStatusUpdate(String arg0, int arg1, String arg2) {
		Log.i(TAG, "onStatusUpdate"); 
		switch (arg1) {            
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
	//LocationSource的回调  设置位置变化回调接口
	@Override
	public void activate(OnLocationChangedListener arg0) {
		
		Log.i(TAG,"activate");	
		mChangedListener = arg0;
		locations = new ArrayList<TencentLocation>();		 
		 this.mContext.stopService(startIntent);  				 
		Log.i(TAG, "关闭位置服务");    
		locationManager = TencentLocationManager.getInstance(mContext);
		locationRequest = TencentLocationRequest.create();
		locationRequest.setInterval(GPSInterval);
		int err = locationManager.requestLocationUpdates(locationRequest, this);
		switch (err) {
		case 0:
			Log.i(TAG,"监听成功");
			isStartTrack = true;	
			break;

		case 1:
			Log.i(TAG,"设备缺少使用腾讯定位服务需要的基本条件");
			break;
		case 2:
			Log.i(TAG,"manifest 中配置的 key 不正确");
			break;
		case 3:
			Log.i(TAG,"自动加载libtencentloc.so失败");
			break;

		default:
			break;
		}
	}
	//LocationSource的回调  取消位置变化回调
	@Override
	public void deactivate() {
		 
		Log.i(TAG,"deactivate");
		saveLocations();
		ComponentName ret = this.mContext.startService(startIntent);
		if(ret == null){
			Log.i(TAG,"开启服务失败");
		}
		else{
			Log.i(TAG,"开启服务成功");
		}
		locationManager.removeUpdates(this);
		//mContext = null;
		locationManager = null;
		locationRequest = null;
		mChangedListener = null;

	}

	public void onPause() {
		Log.i(TAG,"onPause");
		locationManager.removeUpdates(this);
	}

	public void onResume() {
		Log.i(TAG,"onResume");
		locationManager.requestLocationUpdates(locationRequest, this);
	}

	public void onAnimatToSigemaClicked(View view) {
		//		LatLng sigma = new LatLng(39.977290,116.337000);
		CameraUpdate cameraSigma = 
				CameraUpdateFactory.newCameraPosition(new CameraPosition(
						new LatLng(39.977290,116.337000), 19, 45f, 45f));
		tencentMap.animateCamera(cameraSigma, (CancelableCallback) this);
	}

}

