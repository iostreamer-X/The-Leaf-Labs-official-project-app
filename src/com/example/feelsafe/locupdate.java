package com.example.feelsafe;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;

public class locupdate extends Service {
static String lt="",ln="";
	  @Override
	  public int onStartCommand(Intent intent, int flags, int startId) {
		  Cursor cursor = getContentResolver().query(Uri.parse("content://sms/inbox"), null, null, null, null);
	        cursor.moveToFirst();
	        String msgData = "",time;
	        do{
	          
	           for(int idx=0;idx<cursor.getColumnCount();idx++)
	           {
	               msgData=cursor.getString(idx);
	               if(msgData!=null)
		               if(msgData.contains("leafup"))
		               {
		            	   
		            	   String []params=msgData.split(" ");
		            	   time=params[1];
		            	   
		            	   Long ptime=Long.parseLong(time);
		            	   ptime=ptime/1000;
		            	   if((System.currentTimeMillis()/1000)-ptime<=3600)
		            	   {
		            		   lt=params[2];
			            	   ln=params[3];
		            	   }
		            	   //deleteSms(cursor.getString(cursor.getColumnIndexOrThrow("_id")));   
		            	   break;
		               }
	           }
	        }while(cursor.moveToNext());return Service.START_STICKY;
	  }

	  @Override
	  public IBinder onBind(Intent intent) {
	  //TODO for communication return IBinder implementation
	    return null;
	  }
	} 
