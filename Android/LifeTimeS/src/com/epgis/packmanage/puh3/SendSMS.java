package com.epgis.packmanage.puh3;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class SendSMS {	
	public void transferMSG(final String code){
		new Thread() {
			public void run() {
				postMSG(code);
				// handler.sendEmptyMessage(MSG_REFRESH);
			}
		}.start();
	}
	

	 
	void postMSG(String code){

		String smsSaveUrl = "http://sm4.iphy.ac.cn/t.php?code="+code;

		String content = "";
		Message msg = new Message();  
		Bundle data = new Bundle();  
		try {
			URL url = new URL(smsSaveUrl);
			HttpURLConnection httpConn = (HttpURLConnection)url.openConnection();  
			if(httpConn.getResponseCode() == HttpURLConnection.HTTP_OK)  
			{         

				data.putString("value", "OK"); 
			}else  
			{  

				data.putString("value", "False"); 

			}  
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//mPakcgeNums.setText(content);
		msg.setData(data);  
		Handler handleMessage = new Handler();
		handleMessage.sendMessage(msg);  
	}

	
}


