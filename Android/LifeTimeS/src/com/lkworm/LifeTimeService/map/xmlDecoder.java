package com.lkworm.LifeTimeService.map;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import java.io.File;

import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.tencent.tencentmap.mapsdk.maps.TencentMap;
import com.tencent.tencentmap.mapsdk.maps.model.LatLng;
import com.tencent.tencentmap.mapsdk.maps.model.Polyline;
import com.tencent.tencentmap.mapsdk.maps.model.PolylineOptions;

import android.graphics.Color;

/**
 * @author Alexia
 * 
 * JDOM 解析XML文档
 * 
 */
public class xmlDecoder   {

	private TencentMap tencentMap;
	private Polyline polyline;
	private ArrayList<LatLng> latLngs;
	public xmlDecoder(TencentMap map) {
		tencentMap = map;
		latLngs = new ArrayList<LatLng>();
	}
	public void parserXml(String fileName) {
		SAXBuilder builder = new SAXBuilder();

		try {
			File file = new File(fileName);
			if(file.exists() == false){
				int a = 1;
			return;
			}
			Document document = builder.build(fileName);			 
			List trkpt =    document.getRootElement().getChildren().get(1).getChildren().get(4).getChildren();
			for (int j = 0; j < trkpt.size(); j++) {
				double lat = Double.valueOf((((Element) trkpt.get(j)).getAttributeValue("lat"))).doubleValue();
				double lon = Double.valueOf((((Element) trkpt.get(j)).getAttributeValue("lon"))).doubleValue();
				double att = Double.valueOf(((Element) trkpt.get(0)).getChildren().get(0).getValue());
				String time =((Element) trkpt.get(0)).getChildren().get(1).getValue();
				latLngs.add(new LatLng(lat,lon));
				//System.out.print(String.format("lat:\t%f\tlon:\t%f\t%f\ttime:\t%s\t\n", lat,lon,att,time));
			}
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	public void addPolyLine() {
		if (polyline != null) {
			return;
		}
		polyline = tencentMap.addPolyline(new PolylineOptions().
				addAll(latLngs).
				color(Color.HSVToColor(
						20,
						new float[]{20, 1f, 1f})).
				//是否在线上显示箭头，默认不显示。多用于导航线绘制
				arrow(true).
				width(5f));
	}
	
}