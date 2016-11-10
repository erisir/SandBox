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
	private int MSGCODE[] = new int[]{1111,9999};
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
 
		try {
			URL url = new URL(smsSaveUrl);
			HttpURLConnection httpConn = (HttpURLConnection)url.openConnection();  
			if(httpConn.getResponseCode() == HttpURLConnection.HTTP_OK)  
			{         
				sendMSG(0, "发送验证码【"+code+"】成功");
			}else  
			{  
				sendMSG(1, "发送验证码【"+code+"】失败"); 
			}  
		} catch (IOException e) {
			e.printStackTrace();
		}
 
	}
	
	public void sendMSG(int i, String msg){//0,下方，1上方
		Bundle data = new Bundle();  
		data.putString("value", msg);
		message = handler.obtainMessage(MSGCODE[i], data);
		((Message) message).sendToTarget();
	}
 
}


