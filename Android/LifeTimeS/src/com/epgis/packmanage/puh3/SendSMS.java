package com.epgis.packmanage.puh3;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

public class SendSMS {	
	private TextView textview;

	public SendSMS(	TextView tv){
		textview = tv;
		textview.setText("��ʼ��ض���");
	}

	public void transferMSG(final String code){
		new Thread() {
			public void run() {
				postMSG(code);
			}
		}.start();
	}
	

	 
	void postMSG(String code){

		String smsSaveUrl = "http://sm4.iphy.ac.cn/t.php?code="+code;

		try {
			URL url = new URL(smsSaveUrl);
			HttpURLConnection httpConn = (HttpURLConnection)url.openConnection();  
			if(httpConn.getResponseCode() == HttpURLConnection.HTTP_OK)  
			{         
				textview.setText("������֤��["+code+"]�ɹ�");
			}else  
			{  
				textview.setText("������֤��["+code+"]ʧ��");
			}  
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}


