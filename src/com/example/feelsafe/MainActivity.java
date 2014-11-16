package com.example.feelsafe;

import java.io.File;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
public class MainActivity extends Activity {
	 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_main);
        overridePendingTransition(R.anim.abc_slide_in_top,R.anim.abc_slide_in_bottom);
        System.out.println(getBaseContext());
                        
new Thread()
        {
        	public void run()
        	{
        		try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        		
                Intent intent = new Intent(getBaseContext(),notsms.class);
        	    PendingIntent pintent = PendingIntent.getService(getBaseContext(), 0, intent, 0);                
        	    finish();
        	    AlarmManager alarm = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
	            //for 1 second
	            alarm.setRepeating(AlarmManager.RTC_WAKEUP,System.currentTimeMillis(),
	                         1*1000, pintent);
	            File file=new File(getApplicationContext().getFilesDir().getPath().toString() +"/details.txt");
	            if(file.exists())
	            {
	            	   Intent i=new Intent(getApplicationContext(),SlideMenuWithActivityGroup.class);
					   startActivity(i);
					    
	            }
	            else
	            {       Intent i=new Intent(getApplicationContext(),regis.class);
					    startActivity(i);
					    
	            }
					
        	}
        }.start();
       
    }
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    }
