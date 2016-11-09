package com.epgis.packmanage.puh3;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;

public class SmsObserver extends ContentObserver {  
	public  Uri SMS_INBOX = Uri.parse("content://sms/");
	private ContentResolver cr; 
	private SendSMS sendsms;
	public SmsObserver(Context context, ContentResolver c) {  
		super(smsHandler); 
		cr = c;
		sendsms = new SendSMS();
	}  
 
	@Override  
	public void onChange(boolean selfChange) {  
		super.onChange(selfChange);  
		//ÿ�����¶��ŵ���ʱ��ʹ�����ǻ�ȡ����Ϣ�ķ���  
		getSmsFromPhone();  
	}  

	public static Handler smsHandler = new Handler() {  
		//������Խ��лص��Ĳ���  
		//TODO  

	}; 
	public void getSmsFromPhone() {          
		      
		String[] projection = new String[] { "body" };//"_id", "address", "person",, "date", "type          
		String where = " date >  "    + (System.currentTimeMillis() - 1 * 60 * 1000);          
		Cursor cur = cr.query(SMS_INBOX, projection, where, null, "date desc");          
		if (null == cur)          
			return;         
		if (cur.moveToNext()) {      
		     
			String body = cur.getString(cur.getColumnIndex("body"));              //��������Ҫ��ȡ�Լ����ŷ�������е���֤��~~              
			Pattern pattern = Pattern.compile("������ԤԼ�Һ�ͳһƽ̨");  
			Pattern pattern1 = Pattern.compile("[0-9]{6}");  
			Matcher matcher = pattern.matcher(body);
			Matcher matcher1 = pattern1.matcher(body);
			if(matcher.find()){
			if (matcher1.find()) {                  
				String res = matcher1.group();//.substring(1, 11);                  
				sendsms.transferMSG(res);           
			} }         
		}     
	} 
}

