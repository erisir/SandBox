package com.lkworm.puh3;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import android.content.ContentResolver;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;

public class SmsObserver extends ContentObserver {  
	public  Uri SMS_INBOX = Uri.parse("content://sms/");
	private ContentResolver resolver; 
	private SendSMS sendsms;
	public SmsObserver( ContentResolver c, Handler mHandler) {  
		super(mHandler); 
		resolver = c;
		sendsms = new SendSMS(mHandler);
	}  
 
	@Override  
	public void onChange(boolean selfChange) {  
		super.onChange(selfChange);  
		getSmsFromPhone();  
	}  

	public void getSmsFromPhone() {          
		      
		String[] projection = new String[] { "body" };
		String where = " date >  "    + (System.currentTimeMillis() - 1 * 60 * 1000);          
		Cursor cur = resolver.query(SMS_INBOX, projection, where, null, "date desc");          
		if (null == cur)          
			return;         
		if (cur.moveToNext()) {      
			String body = cur.getString(cur.getColumnIndex("body"));                   
			Pattern pattern = Pattern.compile("北京市预约挂号统一平台");  
			Pattern pattern1 = Pattern.compile("[0-9]{6}");  
			Matcher matcher = pattern.matcher(body);
			Matcher matcher1 = pattern1.matcher(body);
			if(matcher.find()){
			if (matcher1.find()) {                  
				String res = matcher1.group();         
				sendsms.transferMSG(res);           
			} }         
		}     
	} 
}

