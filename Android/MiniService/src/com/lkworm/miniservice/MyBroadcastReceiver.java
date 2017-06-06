package com.lkworm.miniservice;
import java.util.Date;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.format.DateFormat;
import android.util.Log;

public class MyBroadcastReceiver extends BroadcastReceiver {
	final String USER_PRESENT = "android.intent.action.USER_PRESENT";
	final String ACTION_TIME_TICK = "android.intent.action.TIME_TICK";
	//一分钟调用一次
	final String  TAG = "MyBroadcastReceiver";
	private int MSGCODE[] = new int[]{1111,9999};
	private Message  message = new Message();
	@Override
	public void onReceive(Context context, Intent intent) {
		//如果收到短信
		Log.i(TAG,intent.getAction());
		sendMSG(0,DateFormat.format("HH.mm.ss", new Date())+"\t"+intent.getAction());
		switch(intent.getAction()){
	 
		case ACTION_TIME_TICK:
			Log.i("MyBroadcastReceiver",ACTION_TIME_TICK);
			GPSTrackService.StartService(context);
			break;
		case USER_PRESENT:
			sendMSG(1, "\t\t\t\t\t\tWelcome!");
			break;
		}
	}
	 
	public void sendMSG(int i, String msg){//0,下方，1上方
		Bundle data = new Bundle();  
		data.putString("value", msg);
		message = MainActivity.mHandler.obtainMessage(MSGCODE[i], data);
		((Message) message).sendToTarget();
	}
	 
}