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

import com.tencent.map.geolocation.TencentLocation;
import com.tencent.map.geolocation.TencentLocationListener;
import com.tencent.map.geolocation.TencentLocationManager;
import com.tencent.map.geolocation.TencentLocationRequest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationProvider;
import android.util.Log;
import android.widget.Toast;

 
@SuppressLint("SimpleDateFormat")
public class GPSTrackManager  {
	protected static final String TAG = "GPSTrackManager";
	/**
	 * ָ��SDcard�е��ļ�λ��,����ʱ����Ϻ�׺��TXT��
	 * */
	public String trackFileName = defineFileName()+".gpx";;;
	/**
	 * �Ƿ�����λ����
	 * */
	public boolean isOpenTrack = true;
	/**
	 * �洢�Ͷ�ȡ�켣������Sdcard�е��ļ�λ��
	 * */
	private String gpsTrackPath = "mnt/sdcard/myTrackLog/" + trackFileName;
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

	private Context context;
	private   TencentLocationListener  locationListener= new TencentLocationListener () {
		@Override
		public void onLocationChanged(TencentLocation location, int error, String reason)
		{	
			if (!isStartTrack) {
				locationManager.removeUpdates(locationListener);
				Toast.makeText(context, "�켣��λ�Ѿ�ȡ��...", Toast.LENGTH_SHORT).show();
			}else 
			{
				if(location != null){
					locations.add(location);
					Toast.makeText(context, "�켣��λ��...", Toast.LENGTH_SHORT).show();
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

		locations = new ArrayList<TencentLocation>();


		request = TencentLocationRequest.create();
		request.setInterval(1000);
		locationManager = TencentLocationManager.getInstance(context);
		locationManager.requestLocationUpdates(request,locationListener);;
	}

	/**
	 * �켣¼��
	 * */
	public boolean tracklocations() {
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
	public boolean saveLocations() {
		//�жϿ����Ƿ���
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
				for (int i = 0; i < locations.size(); i++) {
					String str = String.format("\r\n<trkpt lat=\"%f\" lon=\"%f\">\r\n<ele>\"%f\"</ele>\r\n<time>\"%s\"</time>\r\n</trkpt>\r\n", locations.get(i).getLongitude(),locations.get(i).getLatitude(),locations.get(i).getAltitude(),getGPSTime());
					positions += str;
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
	 * ���ع켣��¼�б�
	 * @return ArrayList<HashMap<String,Object>>
	 * ArrayList ÿ����Ӧ�ļ��е�һ������
	 * HashMap{name:String,position:ArrayList<Location>}
	 * */
	public ArrayList<HashMap<String, Object>> getTrackList() {
		//�жϿ����Ƿ���
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
	 * ���洢�ļ���sdCard���Ƿ���ڲ�����
	 * */
	private  boolean createFile(){
		File file = new File(gpsTrackPath);
		if(file.exists() == false){
			try {
				file.createNewFile();
				FileWriter writer;//д���ļ�ͷ
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
	 * �洢ʱ����洢�ļ���
	 * */
	private String  defineFileName() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		return sdf.format(new Date());
	}
	private String getGPSTime() {
		// TODO Auto-generated method stub
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdf1 = new SimpleDateFormat("hh:mm:ss");
		return sdf.format(new Date())+"T"+sdf1.format(new Date())+"Z";
	} 
	
	public String getFileHander(){
		String str = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\" ?>\r\n<gpx xmlns=\"http://www.topografix.com/GPX/1/1\" xmlns:gpxtpx=\"http://www.garmin.com/xmlschemas/TrackPointExtension/v1\" creator=\"OruxMaps v.6.5.9\" version=\"1.1\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd\">\r\n<metadata>\r\n<name><![CDATA[��ɽ̫������2016-11-05 14:35]]></name>\r\n<desc><![CDATA[]]></desc>\r\n<link href=\"http://www.oruxmaps.com\">\r\n<text>OruxMaps</text>\r\n</link>\r\n<time>2016-11-05T06:35:35Z</time><bounds maxlat=\"40.0382312\" maxlon=\"116.2590316\" minlat=\"40.0046640\" minlon=\"116.1865904\"/>\r\n</metadata>\r\n<trk>\r\n<name><![CDATA[�켣��¼]]></name>\r\n<desc><![CDATA[<p>��ʼʱ��: 11/05/2016 14:35</p><p>����ʱ��: 11/05/2016 18:25</p><p>����: 6.2 km (03:50)</p><p>�ƶ�ʱ��: 03:21</p><p>ƽ���ٶ�: 1.62 km/h</p><p>ƽ���ƶ��ٶ�: 1.85 km/h</p><p>����ٶ�: 9.94km/h</p><p>��С�߳�: 42 m</p><p>���߳�: 390 m</p><p>�����ٶ�: 282.2 m/h</p><p>�½��ٶ�: -252.9 m/h</p><p>�߳�����: 379 m</p><p>�߳��½�: -343 m</p><p>����ʱ��: 01:20</p><p>�½�ʱ��: 01:21</p><hr align=\"center\" width=\"480\" style=\"height: 2px; width: 517px\"/>]]></desc>\r\n<type>������</type>\r\n<extensions>\r\n<om:oruxmapsextensions xmlns:om=\"http://www.oruxmaps.com/oruxmapsextensions/1/0\">\r\n<om:ext type=\"TYPE\" subtype=\"0\">28</om:ext>\r\n<om:ext type=\"DIFFICULTY\">0</om:ext>\r\n</om:oruxmapsextensions>\r\n</extensions>\r\n<trkseg>\r\n";			
		return str;
	}
	public String getFileEnd(){
		String str = "</trkseg>\r\n</trk>\r\n</gpx>";
		return str;
	}


}
