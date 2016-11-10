package com.epgis.packmanage;

import java.io.BufferedReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import com.tencent.map.geolocation.TencentLocation;
import com.tencent.map.geolocation.TencentLocationListener;
import com.tencent.map.geolocation.TencentLocationManager;
import com.tencent.map.geolocation.TencentLocationRequest;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.location.Location;
import android.location.LocationProvider;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

 
@SuppressLint("SimpleDateFormat")
public class GPSTrackManager extends Service {
	protected static final String TAG = "GPSTrackManager";
 
	/**
	 * ָ��SDcard�е��ļ�λ��,����ʱ����Ϻ�׺��TXT��
	 * */
	/**
	 * �Ƿ�����λ����
	 * */
	public boolean isOpenTrack = true;
	public RandomAccessFile randFileWriter = null;
	/**
	 * �洢�Ͷ�ȡ�켣������Sdcard�е��ļ�λ��
	 * */
	private String gpsTrackFolder = "mnt/sdcard/myTrackLog/" ;
	
	/**
	 * �Ƿ�ʼ¼�ƹ켣
	 * */
	private boolean isStartTrack;
	/**
	 * �켣¼��ʱ��ȡ�ĵ�����Ϣ
	 * */
	private ArrayList<TencentLocation> locations;
	/**
	 * android.location.LocationManager;
	 * */
	private TencentLocationManager locationManager;
	private TencentLocationRequest request;

	private static Context context;
	private   TencentLocationListener  locationListener= new TencentLocationListener () {
		@Override
		public void onLocationChanged(TencentLocation location, int error, String reason)
		{	
			if (!isStartTrack) {
				locationManager.removeUpdates(locationListener);
				Log.i(TAG, "GPS��ֹͣ��¼");  
			}else 
			{
				if(location != null){
					locations.add(location);
					if(locations.size()>100){
						saveLocations();
					}
					Log.i(TAG, "GPS����ȡ��ַ--->"+location.getName());  
				}
			}
		}                
		/**         
		 * * GPS״̬�仯ʱ����         
		 * */   
		@Override
		public void onStatusUpdate(String name, int status, String desc) {            
			switch (status) {            
			//GPS״̬Ϊ�ɼ�ʱ            
			case LocationProvider.AVAILABLE:                
				Log.i(TAG, "��ǰGPS״̬Ϊ�ɼ�״̬");                
				break;            
				//GPS״̬Ϊ��������ʱ            
			case LocationProvider.OUT_OF_SERVICE:                
				Log.i(TAG, "��ǰGPS״̬Ϊ��������״̬");                
				break;            
				//GPS״̬Ϊ��ͣ����ʱ            
			case LocationProvider.TEMPORARILY_UNAVAILABLE:                
				Log.i(TAG, "��ǰGPS״̬Ϊ��ͣ����״̬");                
				break;            
			}        
		}                  
	};

	public GPSTrackManager(Context context) {
		this.context = context;
	}
	public GPSTrackManager() {
		
	}

	/**
	 * �켣¼��
	 * */
	public boolean tracklocations(boolean flag) {
		
		if(!flag){
			locationManager.removeUpdates(locationListener);
			isOpenTrack = false;
			return false;
		}
		//�жϿ����Ƿ���
		if (!isOpenTrack) {          
			return false;			
		}
		//�ж�GPS�Ƿ���������        
		int error = locationManager.requestLocationUpdates(request, locationListener);
		String errorStr = "";
		if(error !=0){
			switch(error)
			{
			case 0:  errorStr = "ע��λ�ü������ɹ�";
			break;
			case 1:  errorStr = "�豸ȱ��ʹ����Ѷ��λSDK��Ҫ�Ļ�������";
			break;
			case 2:  errorStr = "���õ� key ����ȷ";
			break;
			case 3:  errorStr = "�Զ�����libtencentloc.soʧ��";
			break;
			}
			Toast.makeText(context, errorStr, Toast.LENGTH_SHORT).show();           
			return false;        
		}
		isStartTrack = true;	
		return true;		
	}

	/**
	 * �洢 ��ȡ�Ĺ켣������Ϣ,�켣ֹͣ¼��ʱ����
	 * */
	@SuppressLint("DefaultLocale")
	public boolean saveLocations() {
		//�жϿ����Ƿ���
		if (!isOpenTrack) {          
			return false;			
		}

		if (createFile()) {
			if(locations == null || locations.size() == 0){
				return false;
			}			 
			try {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				SimpleDateFormat sdf1 = new SimpleDateFormat("hh:mm:ss");
				 
				randFileWriter = new RandomAccessFile(getGPSTrackPath(),"rw");
				randFileWriter.read();
				randFileWriter.seek(randFileWriter.length()-this.getFileEnd().length());
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
				 
				String content = positions + "\n"+getFileEnd();
				randFileWriter.writeBytes(content);
				randFileWriter.close();
				locations.clear();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}	
		return true;
	}

	private String getGPSTrackPath() {
		// TODO Auto-generated method stub
		return gpsTrackFolder+defineFileName()+".gpx";
	}

	public boolean addFileTail() {
		
		FileWriter writer;
		 
		try {
			writer = new FileWriter(getGPSTrackPath(),true);
			writer.write(getFileEnd());
			writer.close();
			locations.clear();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	return true;
}
 
	public ArrayList<HashMap<String, Object>> getTrackList() {
		if (!isOpenTrack) {          
			return null;			
		}
		ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
		File file = new File(getGPSTrackPath());
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
	 
	private  boolean createFile(){
		File file = new File(getGPSTrackPath());
		if(file.exists() == false){
			try {
				file.createNewFile();
				FileWriter writer;//д���ļ�ͷ
				try {
					writer = new FileWriter(getGPSTrackPath(),true);
					String content = getFileHander()+getFileEnd();
					writer.write(content);
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}

			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}
	/**
	 * �洢ʱ����洢�ļ���
	 * */
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
				 + "  \r\n<name><![CDATA["+this.defineFileName()+"�켣]]></name>"
				 + "  \r\n<desc><![CDATA[]]></desc>"
				 + "  \r\n<link href=\"http://www.oruxmaps.com\">"
				 + "  \r\n<text>OruxMaps</text>"
				 + "  \r\n</link>"
				 + "  \r\n<time>"+this.getGPSTime()+"</time><bounds maxlat=\"40.0382312\" maxlon=\"116.2590316\" minlat=\"40.0046640\" minlon=\"116.1865904\"/>"
				 + "  \r\n</metadata>"
				 + "  \r\n<trk>"
				 + "  \r\n<name><![CDATA[�켣��¼]]></name>"
				//+ " <desc><![CDATA[<p>��ʼʱ��: null</p><p>����ʱ��: "+this.getGPSTime()+"</p><p>����: null km (03:50)</p><p>�ƶ�ʱ��: 03:21</p><p>ƽ���ٶ�: 1.62 km/h</p><p>ƽ���ƶ��ٶ�: 1.85 km/h</p><p>����ٶ�: 9.94km/h</p><p>��С�߳�: 42 m</p><p>���߳�: 390 m</p><p>�����ٶ�: 282.2 m/h</p><p>�½��ٶ�: -252.9 m/h</p><p>�߳�����: 379 m</p><p>�߳��½�: -343 m</p><p>����ʱ��: 01:20</p><p>�½�ʱ��: 01:21</p><hr align=\"center\" width=\"480\" style=\"height: 2px; width: 517px\"/>]]></desc>"
				 + "  \r\n<desc><![CDATA[<p>"+this.getdesc()+"</p><hr align=\"center\" width=\"480\" style=\"height: 2px; width: 517px\"/>]]></desc>"
				 + "  \r\n<type>�ճ�</type>"
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
		// TODO Auto-generated method stub
		return "desc";
	}

	public String getFileEnd(){
		String str = "</trkseg>\r\n</trk>\r\n</gpx>";		
		return str;
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		tracklocations(false);
		return null;
	}
	@Override 
    //Serviceʱ������  
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
		request.setInterval(1000);
		locationManager = TencentLocationManager.getInstance(context);
		locationManager.requestLocationUpdates(request,locationListener);
		
        tracklocations(true);
        return super.onStartCommand(intent, flags, startId);  
    }  
    @Override 
    //��Service����ʹ��ʱ����  
    public void onDestroy()  
    {  
        Log.i(TAG, "Service onDestroy--->"); 
        tracklocations(false);
        saveLocations();
        super.onDestroy();  
        
    }  
 
    

}
