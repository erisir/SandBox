package com.epgis.packmanage.puh3;

import java.io.IOException;


import java.net.HttpURLConnection;
import java.net.URL;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class SendSMS {	
	 
	private Handler handler;
	private Message  message;



	public SendSMS(	Handler mHandler){
		this.handler = mHandler;
		message = new Message();  
		
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
		Bundle data = new Bundle();  
		try {
			URL url = new URL(smsSaveUrl);
			HttpURLConnection httpConn = (HttpURLConnection)url.openConnection();  
			if(httpConn.getResponseCode() == HttpURLConnection.HTTP_OK)  
			{         
				data.putString("value", "发送验证码【"+code+"】成功");
			}else  
			{  
				data.putString("value", "发送验证码【"+code+"】失败"); 
			}  
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		message = handler.obtainMessage(8899, data);
	    ((Message) message).sendToTarget();
	}
}


