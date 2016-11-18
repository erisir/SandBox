package org.ndaguan.MFC;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import org.json.JSONObject;



public class test {
	private long[] readFile(String path){
		String line = null;
		long[] val = new long[3];
		try {
			InputStream fis = new FileInputStream(path);
			InputStreamReader isr = new InputStreamReader(fis, Charset.forName("UTF-8"));
			BufferedReader br = new BufferedReader(isr);
			br.readLine();
			line = br.readLine();
			String[] sV = line.split(",");
			for(int i = 0;i<3;i++){
				val[i]= Long.valueOf(sV[i]).longValue();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
		return val;
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		test t = new test();
		System.out.print((t.readFile("H:config.txt"))[1]);
	}

}
