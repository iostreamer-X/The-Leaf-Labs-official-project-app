package com.example.feelsafe;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ScreenReceiver extends BroadcastReceiver{

    public static boolean wasScreenOn = true;
    public static boolean state=false;
    public static boolean lastState=false;
    public static long time=0;
    public static long lastTime=0;
    public static int pressed=0;
    boolean ft=true;
    @Override
    public void onReceive(final Context context, final Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            // do whatever you need to do here
            wasScreenOn = false;
            state=false;
            if(ft)
            {
            	lastState=state;
            	ft=false;
            }
        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            // and do whatever you need to do here
            wasScreenOn = true;
            state=true;
            if(ft)
            {
            	lastState=state;
            	ft=false;
            }
        }
        if(lastState!=state)
        {
        	time=System.currentTimeMillis();
        }
        if(time-lastTime<=1000&&time-lastTime>=150)
        {

	        Intent i = new Intent(context,Wait.class);
			i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
            	
	        
        }
        lastState=state;
        lastTime=time;
    }

}
