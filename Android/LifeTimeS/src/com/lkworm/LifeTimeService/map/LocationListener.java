package com.lkworm.LifeTimeService.map;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.tencent.map.geolocation.TencentLocation;
import com.tencent.map.geolocation.TencentLocationListener;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class LocationListener  implements  TencentLocationListener{
	private  static final String TAG = "DemoLocationSource";
	private final String gpsTrackFileEnd = "</trkseg>\r\n</trk>\r\n</gpx>";	
	private String gpsTrackFolder = "mnt/sdcard/myTrackLog/" ;
	private RandomAccessFile randFileWriter = null;	
	private ArrayList<TencentLocation> locations;	 

	private int MSGCODE[] = new int[]{1111,9999};

	private static Handler handler;
	private static Message message;
	public LocationListener() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onLocationChanged(TencentLocation arg0, int arg1, String arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusUpdate(String arg0, int arg1, String arg2) {
		// TODO Auto-generated method stub
		
	}
	
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
	

}
