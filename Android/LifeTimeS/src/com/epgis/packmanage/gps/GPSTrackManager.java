package com.epgis.packmanage.gps;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

/**
 * @author bai GPS轨迹管理，处理所有轨迹管理逻辑业务
 * */
public class GPSTrackManager {
	protected static final String TAG = "GPSTrackManager";
	/**
	 * 指定SDcard中的文件位置,输入时请带上后缀名TXT。
	 * */
	public String trackFileName = defineFileName()+".gpx";;;
	/**
	 * 是否开启定位开关
	 * */
	public boolean isOpenTrack = true;
	/**
	 * 存储和读取轨迹管理在Sdcard中的文件位置
	 * */
	private String gpsTrackPath = "mnt/sdcard/myTrackLog/" + trackFileName;
	/**
	 * 是否开始录制轨迹
	 * */
	private boolean isStartTrack;
	/**
	 * 轨迹录制时获取的地理信息
	 * */
	private ArrayList<Location> locations;
	/**
	 * android.location.LocationManager;
	 * */
	private LocationManager locationManager;
	
	private Context context;
	/**
	 * 获取GPS信息
	 * */
	public GPSTrackManager(Context context) {
		this.context = context;
		locationManager = (LocationManager)this.context.getSystemService(Context.LOCATION_SERVICE);
		locations = new ArrayList<Location>();
	}
	/**
	 * 轨迹录制
	 * */
	public boolean tracklocations() {
		//判断开关是否开启
		if (!isOpenTrack) {          
			return false;			
		}
		 //判断GPS是否正常启动        
		if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
			Toast.makeText(context, "请开启GPS导航...", Toast.LENGTH_SHORT).show();           
			return false;        
			}
		isStartTrack = true;
		// 设置监听器，自动更新的最小时间为间隔N秒或最小位移变化超过N米
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1 * 1000, 0,locationListener);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1 * 1000, 0,locationListener);	
		return true;		
	}

	/**
	 * 存储 获取的轨迹地理信息,轨迹停止录制时调用
	 * */
	public boolean saveLocations() {
		//判断开关是否开启
		if (!isOpenTrack) {          
			return false;			
		}
		isStartTrack = false;
		if (createFile()) {
			if(locations == null || locations.size() == 0){
				return false;
			}
			FileWriter writer;
			try {
				writer = new FileWriter(gpsTrackPath,true);
				String positions = "";
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-ddThh:mm:ssZ");
				for (int i = 0; i < locations.size(); i++) {
					positions += "\r\n<trkpt lat=\""+locations.get(i).getLongitude()+"\" lon=\""+locations.get(i).getLatitude()+"\">\r\n<ele>"+locations.get(i).getAltitude()+"</ele>\r\n<time>"+sdf.format(locations.get(i).getTime())+"</time>\r\n</trkpt>\r\n";
				}
				if(positions.length() > 0){
					positions = positions.substring(0, positions.length()-1);
				}
				String content = positions + "\n"+getFileEnd();
				writer.write(content);
				writer.close();
				locations.clear();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}	
		return true;
	}
	/**
	 * 返回轨迹记录列表
	 * @return ArrayList<HashMap<String,Object>>
	 * ArrayList 每条对应文件中的一条数据
	 * HashMap{name:String,position:ArrayList<Location>}
	 * */
	public ArrayList<HashMap<String, Object>> getTrackList() {
		//判断开关是否开启
		if (!isOpenTrack) {          
			return null;			
		}
		ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
		File file = new File(gpsTrackPath);
		if(file.isFile() && file.exists()){
			try {
				String encoding = "GBK";
				InputStreamReader read = new InputStreamReader(new FileInputStream(file),encoding);
				BufferedReader bufferedReader = new BufferedReader(read);
				String lineText = "";
				while ((lineText = bufferedReader.readLine())!=null) {
					int pos = lineText.indexOf("position");
					String name = lineText.substring(5,pos-1);
					String position = lineText.substring(pos+9, lineText.length());
					String[] positions = position.split(",");
					ArrayList<Location> templist = new ArrayList<Location>();
					for (String str : positions) {
						String[] temp = str.split(" ");
						Location loc = new Location("");
						loc.setLongitude(Double.valueOf(temp[0]));
						loc.setLatitude(Double.valueOf(temp[1]));
						templist.add(loc);
					}
					HashMap<String, Object> hMap = new HashMap<String, Object>();
					hMap.put("name", name);
					hMap.put("position", templist);
					list.add(hMap);
				}
				read.close();
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
		return list;
	}
	/**
	 * 检查存储文件在sdCard中是否存在并创建
	 * */
	private  boolean createFile(){
		File file = new File(gpsTrackPath);
		if(file.exists() == false){
			try {
				file.createNewFile();
				FileWriter writer;//写入文件头
				try {
					writer = new FileWriter(gpsTrackPath,true);
					String content = getFileHander();
					writer.write(content);
					writer.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}
	/**
	 * 存储时定义存储文件名
	 * */
	private String  defineFileName() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		return sdf.format(new Date());
	}
	/**
	 * 位置监听
	 * */
	private LocationListener locationListener=new LocationListener() {                
		/**         
		 * * 位置信息变化时触发         
		 * */
		@Override
		public void onLocationChanged(Location location) {	
			if (!isStartTrack) {
				locationManager.removeUpdates(this);
				Toast.makeText(context, "轨迹定位已经取消...", Toast.LENGTH_SHORT).show();
			}else {
				if(location != null){
					locations.add(location);
					Toast.makeText(context, "轨迹定位中...", Toast.LENGTH_SHORT).show();
				}
			}
		}                
		/**         
		 * * GPS状态变化时触发         
		 * */   
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {            
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
		/**         
		 * * GPS开启时触发         
		 * */   
		@Override
		public void onProviderEnabled(String provider) {     
			}            
		/**         
		 * * GPS禁用时触发         
		 * */ 
		@Override
		public void onProviderDisabled(String provider) {       
			}        
		};
		public String getFileHander(){
			String str = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\" ?>\r\n<gpx xmlns=\"http://www.topografix.com/GPX/1/1\" xmlns:gpxtpx=\"http://www.garmin.com/xmlschemas/TrackPointExtension/v1\" creator=\"OruxMaps v.6.5.9\" version=\"1.1\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd\">\r\n<metadata>\r\n<name><![CDATA[西山太舟坞上2016-11-05 14:35]]></name>\r\n<desc><![CDATA[]]></desc>\r\n<link href=\"http://www.oruxmaps.com\">\r\n<text>OruxMaps</text>\r\n</link>\r\n<time>2016-11-05T06:35:35Z</time><bounds maxlat=\"40.0382312\" maxlon=\"116.2590316\" minlat=\"40.0046640\" minlon=\"116.1865904\"/>\r\n</metadata>\r\n<trk>\r\n<name><![CDATA[轨迹记录]]></name>\r\n<desc><![CDATA[<p>起始时间: 11/05/2016 14:35</p><p>结束时间: 11/05/2016 18:25</p><p>距离: 6.2 km (03:50)</p><p>移动时间: 03:21</p><p>平均速度: 1.62 km/h</p><p>平均移动速度: 1.85 km/h</p><p>最大速度: 9.94km/h</p><p>最小高程: 42 m</p><p>最大高程: 390 m</p><p>上升速度: 282.2 m/h</p><p>下降速度: -252.9 m/h</p><p>高程上升: 379 m</p><p>高程下降: -343 m</p><p>上升时间: 01:20</p><p>下降时间: 01:21</p><hr align=\"center\" width=\"480\" style=\"height: 2px; width: 517px\"/>]]></desc>\r\n<type>背包客</type>\r\n<extensions>\r\n<om:oruxmapsextensions xmlns:om=\"http://www.oruxmaps.com/oruxmapsextensions/1/0\">\r\n<om:ext type=\"TYPE\" subtype=\"0\">28</om:ext>\r\n<om:ext type=\"DIFFICULTY\">0</om:ext>\r\n</om:oruxmapsextensions>\r\n</extensions>\r\n<trkseg>\r\n";			
			return str;
		}
		public String getFileEnd(){
			String str = "</trkseg>\r\n</trk>\r\n</gpx>";
			return str;
		}
		 
		
}
