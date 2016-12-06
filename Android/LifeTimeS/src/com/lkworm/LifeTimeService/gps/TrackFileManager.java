package com.lkworm.LifeTimeService.gps;

import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import com.lkworm.LifeTimeService.R;
import com.lkworm.LifeTimeService.file.CallbackBundle;
import com.lkworm.LifeTimeService.file.OpenFileDialog;
import com.tencent.map.geolocation.TencentLocation;
import com.tencent.tencentmap.mapsdk.maps.TencentMap;
import com.tencent.tencentmap.mapsdk.maps.model.LatLng;
import com.tencent.tencentmap.mapsdk.maps.model.Polyline;
import com.tencent.tencentmap.mapsdk.maps.model.PolylineOptions;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.net.ParseException;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class TrackFileManager{
	private  static final String TAG = "TrackManager";
	private final int MaxVelocity = 20;//m/s
	private ArrayList<LatLng> latLngs;
	private Dialog dialog;
	private ArrayList<Polyline> polylines;
	private Context mContent;
	private  double EARTH_RADIUS = 6378.137;//地球半径
	private Location lastPosition =new Location("last");
	private Location nextPosition =new Location("last");



	TencentMap tencentMap;

	private SeekBar sbHue;
	private SeekBar sb_velocity;

	public TrackFileManager(Activity activity,Context context,TencentMap map){
		mContent = context;
		tencentMap = map;
		polylines = new ArrayList<Polyline>();

		sbHue = (SeekBar)activity.findViewById(R.id.sb_hue);
		sb_velocity = (SeekBar)activity.findViewById(R.id.sb_velocity);
		bindListener();
	}
	public void removeTrack() {
		for (Polyline j : polylines) {
			j.remove();
		}
	}

	public void showTrack(boolean isOverWriteTrack) {
		latLngs = new ArrayList<LatLng>();
		if(!isOverWriteTrack)
			removeTrack();
		OpenDialog(GPSTrackManager.getTrackFolder());
	}
	private CallbackBundle fileDialogOpenCallback = new CallbackBundle() {  
		@Override  
		public void callback(Bundle bundle) {  
			String filepath = bundle.getString("path");  
			parseXMLWithPull(filepath);
			dialog.dismiss();
			addPolyLine();
		}  
	};
	public void openTrackFile(String filepath,boolean isOverWriteTrack){
		latLngs = new ArrayList<LatLng>();
		if(!isOverWriteTrack)
			removeTrack();
		parseXMLWithPull(filepath);
		addPolyLine();
	}
	private void OpenDialog(String Startpath){
		Map<String, Integer> images = new HashMap<String, Integer>();  
		// 下面几句设置各文件类型的图标， 需要你先把图标添加到资源文件夹  
		images.put(OpenFileDialog.sRoot, R.drawable.death_star);   // 根目录图标  
		images.put(OpenFileDialog.sParent, R.drawable.death_star);    //返回上一层的图标  
		images.put(OpenFileDialog.sFolder, R.drawable.death_star);   //文件夹图标  
		images.put("gpx", R.drawable.death_star);   //wav文件图标  
		images.put(OpenFileDialog.sEmpty, R.drawable.death_star);  
		dialog = OpenFileDialog.createDialog(Startpath,0, mContent, "打开文件",fileDialogOpenCallback ,   
				".gpx;",  
				images);
		dialog.show();
	}

	private  double rad(double d)
	{
		return d * Math.PI / 180.0;
	}

	public  double GetDistance(double lat1, double lng1, double lat2, double lng2)
	{
		double radLat1 = rad(lat1);
		double radLat2 = rad(lat2);
		double a = radLat1 - radLat2;
		double b = rad(lng1) - rad(lng2);

		double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a/2),2) +
				Math.cos(radLat1)*Math.cos(radLat2)*Math.pow(Math.sin(b/2),2)));
		s = s * EARTH_RADIUS;
		s = Math.round(s * 10000) / 10000;
		return s;
	}
	private void parseXMLWithPull(String path){
		try {
			XmlPullParserFactory  factory = XmlPullParserFactory.newInstance();
			XmlPullParser xmlPullParser = factory.newPullParser();
			xmlPullParser.setInput(new FileInputStream(path),"UTF-8");
			int eventType = xmlPullParser.getEventType();
			double lat = 0;
			double lon = 0;
			long time = 0;
			while (eventType != (XmlPullParser.END_DOCUMENT)){
				String nodeName = xmlPullParser.getName();
				switch (eventType){
				//开始解析XML
				case XmlPullParser.START_TAG:{
					//通过nextText()获取文本节点值<tag>val<tag>，或通过getAttributeValue(i)获取属性节点值<tag "name1"=val1 "name2"=val2>
					if("trkpt".equals(nodeName)){
						lat = Double.valueOf(xmlPullParser.getAttributeValue(0)).doubleValue();
						lon =Double.valueOf(xmlPullParser.getAttributeValue(1)).doubleValue();
						/*xmlPullParser.next();
						xmlPullParser.next();		
						xmlPullParser.next();
						//xmlPullParser.getName() == ele
						//ele = xmlPullParser.
						xmlPullParser.next();
						xmlPullParser.next();
						xmlPullParser.next();
						//xmlPullParser.getName() == time
						//ele = xmlPullParser.
						String strTime = xmlPullParser.nextText();//"2016-12-06T13:52:08X"
						strTime = strTime.replace('T', ' ' );
						strTime = strTime.replace('X', ' ' );
						strTime = strTime.replace('Z', ' ' );
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ");
						Date date;
						try {
							date = sdf.parse(strTime);	
							time = date.getTime();
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						 */
						if(lastPosition.getLatitude() != 0){
							double filter = (double)sb_velocity.getProgress()/1000;//0~1000
							if( Math.abs(lastPosition.getLatitude() - lat)<filter && Math.abs(lastPosition.getLongitude() - lon)<filter){
								latLngs.add(new LatLng(lat,lon));
							}
						}

						lastPosition.setLatitude(lat);
						lastPosition.setLongitude(lon);
					}

				} break;
				//结束解析
				case XmlPullParser.END_TAG:{
					if("app".equals(nodeName)){
						Log.i(TAG, "parseXMLWithPull: meet end ");
					}
				} break;
				default: 
					break;
				}
				//下一个
				eventType = xmlPullParser.next();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void addPolyLine() {//透明度0-100，色调0-1，饱和度0-1，亮度0-1
		Polyline polyline = tencentMap.addPolyline(new PolylineOptions().
				addAll(latLngs).
				color(Color.HSVToColor(
						100,
						new float[]{sbHue.getProgress(), 1f, 1f})).
				//是否在线上显示箭头，默认不显示。多用于导航线绘制
				arrow(true).
				width(9f));
		polylines.add(polyline);
	}
	public void bindListener() {
		OnSeekBarChangeListener onSeekBarChangeListener = new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				if (polylines.size() <=0l) {
					return;
				}
				switch (seekBar.getId()) {
				case R.id.sb_hue:
					for (Polyline polyline : polylines)
						polyline.setColor(Color.HSVToColor(
								100,
								new float[]{progress, 1f, 1f}));
					break;

				default:
					break;
				}
			}
		};
		sbHue.setOnSeekBarChangeListener(onSeekBarChangeListener);
	}

}
