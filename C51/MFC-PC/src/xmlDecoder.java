
import java.io.FileNotFoundException;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;

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
			Element users = document.getRootElement();
			List trk = users.getChildren("trkseg");

			Element trkseg =  (Element) trk.get(0);
			List trkpt =     trkseg.getChildren();
 
			Element listElement1 =   (Element) trkpt.get(0);
			List listElement =     listElement1.getChildren();
			for (int j = 0; j < listElement.size(); j++) {
				
				System.out.println(((Element) listElement.get(j)).getName()
						+ ":" + ((Element) listElement.get(j)).getValue());
			}
			System.out.println();

		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public  static void main(String[] args) {
		xmlDecoder xmld = new xmlDecoder();
		xmld.parserXml("D:\\test.gpx");
	}
}