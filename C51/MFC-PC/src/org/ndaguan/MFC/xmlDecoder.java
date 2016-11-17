package org.ndaguan.MFC;

import java.io.FileInputStream;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;


public class xmlDecoder {

	public xmlDecoder() {
		// TODO Auto-generated constructor stub
	}
	private void parseXML(String path){
		SAXBuilder builder = new SAXBuilder();
		try {
			Document document = builder.build(path);
			List trkpt =    document.getRootElement().getChildren().get(1).getChildren().get(4).getChildren();
			for (int j = 0; j < 2; j++) {
				double lat = Double.valueOf((((Element) trkpt.get(j)).getAttributeValue("lat"))).doubleValue();
				double lon = Double.valueOf((((Element) trkpt.get(j)).getAttributeValue("lon"))).doubleValue();
				double att = Double.valueOf(((Element) trkpt.get(0)).getChildren().get(0).getValue());
				String time =((Element) trkpt.get(0)).getChildren().get(1).getValue();
				System.out.print(String.format("lat:\t%f\tlon:\t%f\t%f\ttime:\t%s\t\n", lat,lon,att,time));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void main(String[] args) {
		xmlDecoder xmld = new xmlDecoder();
		xmld.parseXML("D:\\tracklog\\20161117.gpx");

	}

}
