package com.example.mqttdemo;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;

public class MainActivity extends Activity {

	TextView tv = null;
	private StatusUpdateReceiver statusUpdateIntentReceiver;  
	private MQTTMessageReceiver  messageIntentReceiver; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		tv = (TextView) findViewById(R.id.text1);
		
		SharedPreferences settings = getSharedPreferences(MQTTService.APP_ID, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString("broker", "172.16.34.22"); // CHANGE ME to your broker address
		editor.putString("topic", "message.school.*"); // CHANGE ME to your topic
		editor.commit();
		
		statusUpdateIntentReceiver = new StatusUpdateReceiver();
		IntentFilter intentSFilter = new IntentFilter(
				MQTTService.MQTT_STATUS_INTENT);
		registerReceiver(statusUpdateIntentReceiver, intentSFilter);

		messageIntentReceiver = new MQTTMessageReceiver();
		IntentFilter intentCFilter = new IntentFilter(
				MQTTService.MQTT_MSG_RECEIVED_INTENT);
		registerReceiver(messageIntentReceiver, intentCFilter);
		Intent svc = new Intent(this, MQTTService.class);
		System.out.println("开始activity");
//		startService(svc);
		Intent i = new Intent(this, MQTTService.class);
		this.startService(i);
		System.out.println("already start service");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	public class StatusUpdateReceiver extends BroadcastReceiver  
	{  
	    @Override   
	    public void onReceive(Context context, Intent intent)  
	    {  
	        Bundle notificationData = intent.getExtras();  
	        String newStatus = notificationData.getString(MQTTService.MQTT_STATUS_MSG);	    	  
	    }  
	} 
	
	public class MQTTMessageReceiver extends BroadcastReceiver  
	{  
	    @Override   
	    public void onReceive(Context context, Intent intent)  
	    {  
	        Bundle notificationData = intent.getExtras();  
	        String newTopic = notificationData.getString(MQTTService.MQTT_MSG_RECEIVED_TOPIC);  
	        String newData  = notificationData.getString(MQTTService.MQTT_MSG_RECEIVED_MSG);	    	  
	        System.out.println("topic    : "+newTopic);
	        System.out.println("data     : "+newData);
	        tv.setText(newData);
	        
	    }  
	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(statusUpdateIntentReceiver);  
	    unregisterReceiver(messageIntentReceiver); 
		super.onDestroy();
	}
	
	


}
