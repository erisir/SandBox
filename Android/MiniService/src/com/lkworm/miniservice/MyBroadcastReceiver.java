package com.lkworm.miniservice;
import java.util.Date;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

public class MyBroadcastReceiver extends BroadcastReceiver {
	final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
	final String SYS_TIME_CHANGE = "android.intent.action.ACTION_TIME_CHANGED";
	final String USER_PRESENT = "android.intent.action.USER_PRESENT";
	final String  TAG = "MyBroadcastReceiver";
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		//如果收到短信
		Log.i(TAG,intent.getAction());
		switch(intent.getAction()){
		case SMS_RECEIVED:
			smsDecoder( context,  intent);
			break;
		case SYS_TIME_CHANGE:
			Log.i("MyBroadcastReceiver",SYS_TIME_CHANGE);
			break;
		case USER_PRESENT:
			Log.i("MyBroadcastReceiver",SYS_TIME_CHANGE);
			bindService( context);
			break;
		}
	}
	private void bindService(Context context){
		Intent service = new Intent(context,GPSTrackManager.class);  
		service.setAction("com.lkworm.LifeTimeService.gps.GPSTrackManager");  
		// 在广播中启动服务必须加上startService(intent)的修饰语。Context是对象  
		ComponentName ret = context.startService(service);  
		boolean started = AlipayService.StartService(context);
		Log.i(TAG,"AlipayService.StartService:"+ (started ? "Start" : "Stop"));
		if(ret == null){
			Log.i(TAG,"开启服务失败");
		}
		else{
			Log.i(TAG,"开启服务成功");
		}
	} 
	private void smsDecoder(Context context, Intent intent){

		//取消这条有序广播（取消后会让其它应用收不到短信）
		//abortBroadcast();
		Bundle bundle=intent.getExtras();
		if (bundle!=null) {//如果数据不为空
			//获得收到的短信数据
			Object[] objArray=(Object[]) bundle.get("pdus");
			//根据objArray的大小创建一个SmsMessage数组，用于封装短信内容
			SmsMessage []smsMessage=new SmsMessage[objArray.length];
			StringBuffer sb=new StringBuffer();
			new DateFormat();
			sb.append("时间："+DateFormat.format("yyyy-MM-dd hh.mm.ss", new Date()));
			//遍历smsMessage数组取出所有短信
			for (int i = 0; i < smsMessage.length; i++) {
				//将每条字节类型的短信数据转换成SmsMessage对象
				smsMessage[i]=SmsMessage.createFromPdu((byte[])objArray[i]);
				//获取短信发送地址
				sb.append("发送者："+smsMessage[i].getOriginatingAddress());
				//获取短信内容
				sb.append("短信内容："+smsMessage[i].getDisplayMessageBody()+"\n");
			}
			Toast.makeText(context, sb.toString(), Toast.LENGTH_LONG).show();
		}
	}

}