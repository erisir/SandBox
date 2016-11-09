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
		textview.setText("开始监控短信");
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
				textview.setText("发送验证码["+code+"]成功");
			}else  
			{  
				textview.setText("发送验证码["+code+"]失败");
			}  
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}


