package test;

import java.io.IOException;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

/**
 * @author Alexia
 * 
 * JDOM 解析XML文档
 * 
 */
public class xmlDecoder   {

	public void parserXml(String fileName) {
		SAXBuilder builder = new SAXBuilder();

		try {
			Document document = builder.build(fileName);			 
			List trkpt =    document.getRootElement().getChildren().get(1).getChildren().get(4).getChildren();
			for (int j = 0; j < 2; j++) {
				double lat = Double.valueOf((((Element) trkpt.get(j)).getAttributeValue("lat"))).doubleValue();
				double lon = Double.valueOf((((Element) trkpt.get(j)).getAttributeValue("lon"))).doubleValue();
				double att = Double.valueOf(((Element) trkpt.get(0)).getChildren().get(0).getValue());
				String time =((Element) trkpt.get(0)).getChildren().get(1).getValue();
				System.out.print(String.format("lat:\t%f\tlon:\t%f\t%f\ttime:\t%s\t\n", lat,lon,att,time));
			}
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public  static void main(String[] args) {
		xmlDecoder xmld = new xmlDecoder();
		xmld.parserXml("H:\\20161114.gpx");
	}
}