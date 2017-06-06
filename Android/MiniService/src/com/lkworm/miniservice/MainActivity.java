package com.lkworm.miniservice;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.baidu.speech.EventListener;
import com.baidu.speech.EventManager;
import com.baidu.speech.EventManagerFactory;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AndroidRuntimeException;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ScrollView;
import android.widget.TextView;

public class MainActivity extends Activity {
	private static TextView msgText;
    private EventManager mWpEventManager;
    private String TAG = "MainActivity";
	private BaiduTts speaker = null;
	public static Handler mHandler = new Handler(Looper.getMainLooper()) {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			String val = ((Bundle)msg.obj).getString("value") ;
			switch (msg.what) {
			case 1111:
				if(showLogCheck.isChecked()){
					msgText.setText(msgText.getText()+"\r\n"+val);
					if(autoScroll.isChecked()){
						mHandler.post(new Runnable() {  
							@Override  
							public void run() {  
								scrollView.fullScroll(ScrollView.FOCUS_DOWN);   
							}  
						});
					}
				}
				break;
			case 9999:
				 
				break;

			}
		}
	};
	private static CheckBox showLogCheck;
	private static ScrollView scrollView;
	private static CheckBox autoScroll;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		msgText = (TextView)findViewById(R.id.msg);
		autoScroll = (CheckBox)findViewById(R.id.AutoScroll);
		showLogCheck = (CheckBox)findViewById(R.id.showLogCheck);
		scrollView = (ScrollView)findViewById(R.id.scrollView1);
		
		showLogCheck.setChecked(true);
		autoScroll.setChecked(true);
		//IntentFilter mTime = new IntentFilter(Intent.ACTION_TIME_TICK);
		//MyBroadcastReceiver receiver = new MyBroadcastReceiver();
		//registerReceiver(receiver, mTime);
		wakeupInit();
		speaker  = new BaiduTts(getResources(),this.getBaseContext());
	}
	public void onShowLogCheck(View view){
		msgText.setText("");		
	}
    private void wakeupInit() {
      
        // 唤醒功能打开步骤
        // 1) 创建唤醒事件管理器
        mWpEventManager = EventManagerFactory.create(MainActivity.this, "wp");

        // 2) 注册唤醒事件监听器
        mWpEventManager.registerListener(new EventListener() {
           
			@Override
            public void onEvent(String name, String params, byte[] data, int offset, int length) {
                Log.d(TAG, String.format("event: name=%s, params=%s", name, params));
                try {
                    JSONObject json = new JSONObject(params);
                    if ("wp.data".equals(name)) { // 每次唤醒成功, 将会回调name=wp.data的时间, 被激活的唤醒词在params的word字段
                        String word = json.getString("word");
                        msgText.append("唤醒成功, 唤醒词: " + word + "\r\n");
                        if(word.equals("小度你好")){
                    		speaker.speak("你好");
                        }
                        if(word.equals("百度一下")){
                    		speaker.speak("你好，想查什么呢");
                        }
                    } else if ("wp.exit".equals(name)) {
                    	msgText.append("唤醒已经停止: " + params + "\r\n");
                    }
                } catch (JSONException e) {
                	 Log.d(TAG, "ERROR");
                    throw new AndroidRuntimeException(e);
                }
            }
        });

        // 3) 通知唤醒管理器, 启动唤醒功能
        HashMap params = new HashMap();
        params.put("kws-file", "assets:///WakeUp.bin"); // 设置唤醒资源, 唤醒资源请到 http://yuyin.baidu.com/wake#m4 来评估和导出
        mWpEventManager.send("wp.start", new JSONObject(params).toString(), null, 0, 0);
    }

}
