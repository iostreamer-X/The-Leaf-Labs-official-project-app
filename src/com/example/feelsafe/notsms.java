package com.example.feelsafe;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.media.AudioManager;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class notsms extends Service implements TextToSpeech.OnInitListener{
static String lt="",ln="",name="Your kin";
public static SpeechRecognizer sr;
public static boolean voice=false;
boolean no=true;
static public boolean comin=false;
private TextToSpeech tts;
private AudioManager mAudioManager;
int n=5;	  
Cursor cursor;
@Override
	public int onStartCommand(Intent intent, int flags, int startId) {	  
	mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
	BroadcastReceiver mReceiver;
	final IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
    filter.addAction(Intent.ACTION_SCREEN_OFF);
    mReceiver = new ScreenReceiver();
    registerReceiver(mReceiver, filter);
	cursor= getContentResolver().query(Uri.parse("content://sms/inbox"), null, null, null, null);
	        cursor.moveToFirst();
	        String msgData = "";
	        if(voice)
	        {
	        sr = SpeechRecognizer.createSpeechRecognizer(this);       
	        sr.setRecognitionListener(new listener());
	        Intent lintent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);        
	        String languagePref = "en";//or, whatever iso code...
	        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, languagePref);
	        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, languagePref); 
	        intent.putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, languagePref);
	        lintent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,"voice.recognition.test");
            lintent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS,Long.valueOf(5000));
            sr.startListening(lintent);
            mAudioManager.setStreamSolo(AudioManager.STREAM_VOICE_CALL, true);
            Log.d("voice recog","started");
            }
	        else
	        	mAudioManager.setStreamSolo(AudioManager.STREAM_VOICE_CALL,false);
	        n++;
	        registerReceiver(onBatteryChanged,                     
					new IntentFilter(Intent.ACTION_BATTERY_CHANGED));  
		    do{
	          
	           for(int idx=0;idx<cursor.getColumnCount();idx++)
	           {
	               msgData=cursor.getString(idx);
	               if(msgData!=null)
	               {if(msgData.contains("leafmeyolo")&&msgData.split(" ").length==7)
	               {
	            	   String time;
	            	   String []params=msgData.split(" ");
	            	   time=params[4];
	            	   if(params.length>=6)
	            	   if(no)
	            	   {name=params[5]+" is in danger at "+params[3]+" Please save.";
	            	    no=false;
	            	   }
	            	   Long ptime=Long.parseLong(time);
	            	   ptime=ptime/1000;
	            	   if((System.currentTimeMillis()/1000)-ptime<=300)
	            	   {
	            		   Intent i=new Intent(this,notif.class);
	            		   i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
	            	    	PendingIntent pIntent = PendingIntent.getActivity(this.getBaseContext(),0,i,0);
	            	        Notification n  = new NotificationCompat.Builder(this)
	            	        .setContentTitle(name)
	            	        .setContentText("Tap to help.")
	            	        .setSmallIcon(R.drawable.ic_launcher)
	            	        .setContentIntent(pIntent)
	            	        .setAutoCancel(true)
	            	        .build();
	            	        n.flags |= Notification.FLAG_NO_CLEAR;
	            	        long[] vibrate = { 0, 100, 200, 300,400,500,600 };
	            	        n.vibrate = vibrate;
	            	        n.flags |=Notification.DEFAULT_LIGHTS;
	            	        NotificationManager notif = (NotificationManager) 
	            	    		    getSystemService(NOTIFICATION_SERVICE); 
	            	        
	            	        notif.notify(0, n);
	            	       tts = new TextToSpeech(this, this);
	            	       speakOut(name);
	            	        
	            	    }
	            	   else if((System.currentTimeMillis()/1000)-ptime>=3600)
	            		   deleteSms(cursor.getString(cursor.getColumnIndexOrThrow("_id")));
	               		   
	               }
	               if(msgData.contains("leafup"))
	               {
	            	   
	            	   String []params=msgData.split(" ");
	            	   String time=params[1];
	            	   String phnum=params[4];
	            	   Long ptime=Long.parseLong(time);
	            	   ptime=ptime/1000;
	            	   if((System.currentTimeMillis()/1000)-ptime<=3600&&(phnum.equals(notif.phnum)))
	            	   {
	            		   Toast.makeText(getBaseContext(),"Location Changed",Toast.LENGTH_SHORT).show();
	            		   lt=params[2];
		            	   ln=params[3];
	            		   
	            	   }
	            	   deleteSms(cursor.getString(cursor.getColumnIndexOrThrow("_id")));   
	            	   
	               }
	               if(msgData.contains("Threatleafme"))
	               {
	            	   
	            	   String []params=msgData.split(" ");
	            	   String phnum=params[1];
	            	   String lat=params[2];
	            	   String lon=params[3];
	            	   Intent i=new Intent(this,notif.class);
            		   i.putExtra("doit",true);
            		   i.putExtra("lat",lat);
            		   i.putExtra("lon",lon);
            		   i.putExtra("num",phnum);
            		   i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            	    	PendingIntent pIntent = PendingIntent.getActivity(this.getBaseContext(),0,i,0);
            	        Notification n  = new NotificationCompat.Builder(this)
            	        .setContentTitle(name)
            	        .setContentText("Tap to help.")
            	        .setSmallIcon(R.drawable.ic_launcher)
            	        .setContentIntent(pIntent)
            	        .setAutoCancel(true)
            	        .build();
            	        n.flags |= Notification.FLAG_NO_CLEAR;
            	        long[] vibrate = { 0, 100, 200, 300,400,500,600 };
            	        n.vibrate = vibrate;
            	        n.flags |=Notification.DEFAULT_LIGHTS;
            	        NotificationManager notif = (NotificationManager) 
            	    		    getSystemService(NOTIFICATION_SERVICE); 
            	        
            	        notif.notify(1, n);

	            	   deleteSms(cursor.getString(cursor.getColumnIndexOrThrow("_id")));   
	            	   
	               }
	               if(msgData.contains("comingleafme"))
	               {
	            	   comin=true;   
	            	   String []params=msgData.split(" ");
	            	   String time=params[1];
	            	   Long ptime=Long.parseLong(time);
	            	   ptime=ptime/1000;
	            	   if((System.currentTimeMillis()/1000)-ptime<=60)
	            	   {
	            		   Intent i=new Intent(this,MainMenu.class);
	            	    	PendingIntent pIntent = PendingIntent.getActivity(this.getBaseContext(),0,i,0);
	            	        Notification n  = new NotificationCompat.Builder(this)
	            	        .setContentTitle("Help is coming")
	            	        .setContentText("Someone is coming to help you.")
	            	        .setSmallIcon(R.drawable.ic_launcher)
	            	        .setContentIntent(pIntent)
	            	        .setAutoCancel(true)
	            	        .build();
	            	        long[] vibrate={0,500};
	            	        n.vibrate = vibrate;
	            	        NotificationManager notif = (NotificationManager) 
	            	    		    getSystemService(NOTIFICATION_SERVICE); 
	            	        notif.notify(1,n);
	            	   }
	            	   else
	            		   deleteSms(cursor.getString(cursor.getColumnIndexOrThrow("_id")));
	            	}
	               if(msgData.contains("ThreatLeafNullified"))
	               {
	            	deleteSms(cursor.getString(cursor.getColumnIndexOrThrow("_id")));
		            String num=msgData.split(" ")[1];
	            	Cursor c1 = getContentResolver().query(Uri.parse("content://sms/inbox"), null, null, null, null);
	       	        c1.moveToFirst();
	       	        String m1 = "";
	       	        do{
	       	          for(int idx1=0;idx1<c1.getColumnCount();idx1++)
	       	           {
	       	        	m1=c1.getString(idx1);
	       	        	if(m1!=null)
	       	        	if(m1.contains("leafmeyolo")&&m1.contains(num))
	       	        	{
	       	        		deleteSms(c1.getString(c1.getColumnIndexOrThrow("_id")));
	    	            }
	       	           }
	       	     }while(c1.moveToNext());
	               }
	               /*if(msgData.contains("Agreedleaf"))
	               {
	            	   String []params=msgData.split(" ");
	            	   if(params.length==2)
	            	   {
	            		try {
							String []contsw=contw();
							String num=params[1];
							for(int i=0;i<contsw.length;i++)
							{
								if(num.contains(contsw[i]))
								{
									File file=new File(getApplicationContext().getFilesDir().getPath().toString() +"/contacts.txt");
									FileWriter f=new FileWriter(file,true);
									f.write(num+" ");
									f.flush();
									f.close();        
									Toast.makeText(getApplicationContext(),num+" successfully added as Guardian.",Toast.LENGTH_SHORT).show();
									clrcontw(num, contsw);
									deleteSms(cursor.getString(cursor.getColumnIndexOrThrow("_id")));
									break;
								}
							}
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}  
	            	   }
	               }
	               if(msgData.contains("You have been added as a Guardian by"))
	               {
	            	     
	            	   String []params=msgData.split(" ");
	            	   String num=params[8];
	            	Intent i=new Intent(this,MainMenu.class);
           	    	PendingIntent pIntent = PendingIntent.getActivity(this.getBaseContext(),0,i,0);
           	        Notification n  = new NotificationCompat.Builder(this)
           	        .setContentTitle("Guardian Request")
           	        .setContentText(num+" added you as a Guardian. Should you choose to accept.")
           	        .setSmallIcon(R.drawable.ic_launcher)
           	        .setContentIntent(pIntent)
           	        .setAutoCancel(true)
           	        .build();
           	        NotificationManager notif = (NotificationManager) 
           	    		    getSystemService(NOTIFICATION_SERVICE); 
           	        notif.notify(1,n);
	            	}*/
	                 }
	               }
	        }while(cursor.moveToNext());
	        return Service.START_NOT_STICKY;
	  }
      public String[] contw() throws IOException
      {
    	  BufferedReader bufr = new BufferedReader(new FileReader(getBaseContext()
    				.getFilesDir().getPath().toString()
    				+ "/contactsw.txt"));
    		    String read=bufr.readLine();
    		    String[] contsw=read.split(" ");
    		    bufr.close();
    		    return contsw;
      }
      public void clrcontw(String num,String[] contsw)throws IOException
      {
    	  File file=new File(getApplicationContext().getFilesDir().getPath().toString() +"/contactsw.txt");
    	  FileWriter f=new FileWriter(file);
    	  f.write("");
    	  f.flush();
    	  f.close();
    	  f=new FileWriter(file,true);
    	  for(int i=0;i<contsw.length;i++)
    		  {
    		  if(contsw[i].contains(num))
    			  continue;
    		  else
    			  f.write(contsw[i]+" ");
    		  }
    	  f.flush();
    	  f.close();
      }
	  @Override
	  public IBinder onBind(Intent intent) {
	  //TODO for communication return IBinder implementation
	    return null;
	  }
	  @Override
	    public void onInit(int status) {
	 
	        if (status == TextToSpeech.SUCCESS) {
	 
	            int result = tts.setLanguage(Locale.US);
	 
	            if (result == TextToSpeech.LANG_MISSING_DATA
	                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
	                Log.e("TTS", "This Language is not supported");
	            } else {
	                speakOut(name);
	            }
	 
	        } else {
	            Log.e("TTS", "Initilization Failed!");
	        }
	 
	    }
	 
	    private void speakOut(String name) {
	 
	    	Log.e("name",name);
	    	String text =name;
	 
	        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
	    }
	    public boolean deleteSms(String smsId) {
	        boolean isSmsDeleted = false;
	        try {
	            this.getContentResolver().delete(
	                    Uri.parse("content://sms/" + smsId), null, null);
	            isSmsDeleted = true;

	        } catch (Exception ex) {
	            isSmsDeleted = false;
	        }
	        return isSmsDeleted;
	    }
	    public void showAlert(final String numb) {
			AlertDialog.Builder alert = new AlertDialog.Builder(getApplicationContext());
			LinearLayout layout = new LinearLayout(getApplicationContext());
			//TextView tvMessage = new TextView(this);
			final EditText etInput = new EditText(getApplicationContext());
			//tvMessage.setText("Enter password:");
			etInput.setSingleLine();
			layout.setOrientation(LinearLayout.VERTICAL);
			//layout.addView(tvMessage);
			layout.addView(etInput);
			alert.setTitle("Please Enter your phone number");
			alert.setView(layout);
			alert.setNegativeButton("Cancel",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
						}
					});
			alert.setPositiveButton("Verify", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					String num = etInput.getText().toString();
					if(num.length()==10)
					{
						sendSMS(numb,"Agreedleaf +91"+num);
					}
					else
						Toast.makeText(getBaseContext(), "Please enter Valid no.",Toast.LENGTH_SHORT).show();
					}
			});
			alert.show();
		}	    
	    private void sendSMS(String phoneNumber, String message) {
	        SmsManager sms = SmsManager.getDefault();
	        sms.sendTextMessage(phoneNumber, null, message,null,null);
	    }
	    @Override
	    	public void onDestroy() {
	    		// TODO Auto-generated method stub
	    	sr.destroy();
	    	cursor.close();
	    	super.onDestroy();
	    	}
	    class listener implements RecognitionListener          
	    {
	             public void onResults(Bundle results)                   
	             {
	                      String str = new String();
	                      ArrayList<String> data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
	                      str=data.get(0);
	                      System.out.println(str); 
	                      if(str.equals("alert"))
                          startActivity(new Intent(getBaseContext(),Wait.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
	                      if(str.equals("open app"))
	                           startActivity(new Intent(getBaseContext(),MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
	                      if (str.equals("help centre"))
	                    	  startActivity(new Intent(getBaseContext(),notif.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
						  if(str.contains("add")&&str.split(" ").length==3)
						  {
							  String name=str.split(" ")[2];
							  Cursor cursor = null;
							    try {
							        cursor = getBaseContext().getContentResolver().query(Phone.CONTENT_URI, null, null, null, null);
							        int contactIdIdx = cursor.getColumnIndex(Phone._ID);
							        int nameIdx = cursor.getColumnIndex(Phone.DISPLAY_NAME);
							        int phoneNumberIdx = cursor.getColumnIndex(Phone.NUMBER);
							        int photoIdIdx = cursor.getColumnIndex(Phone.PHOTO_ID);
					 		        cursor.moveToFirst();
							        do {
							            String idContact = cursor.getString(contactIdIdx);
							            String contactName = cursor.getString(nameIdx).toLowerCase();
							            String phoneNumber = cursor.getString(phoneNumberIdx);
							            if(name.equals(contactName))
							            {
							            	regis r=new regis();
							            	r.add(phoneNumber);
							            }
							        } while (cursor.moveToNext()); 
							        
							        
							    } catch (Exception e) {
							        e.printStackTrace();
							    } finally {
							        if (cursor != null) {
							            cursor.close();
							        }
							    }
						  }
	             }

	    			@Override
	    			public void onReadyForSpeech(Bundle params) {
	    				// TODO Auto-generated method stub
	    				System.out.println("ready"); 
	    			}

	    			@Override
	    			public void onBeginningOfSpeech() {
	    				// TODO Auto-generated method stub
	    				System.out.println("starting");
	    			}

	    			@Override
	    			public void onRmsChanged(float rmsdB) {
	    				// TODO Auto-generated method stub
	    				//System.out.println("rms changed "+10*Math.pow(10, ((double)rmsdB/(double)10)));
	    			}

	    			@Override
	    			public void onBufferReceived(byte[] buffer) {
	    				// TODO Auto-generated method stub
	    				//System.out.println("buffer "+buffer);
	    			}

	    			@Override
	    			public void onEndOfSpeech() {
	    				// TODO Auto-generated method stub
	    				System.out.println("ending");
	    			}

	    		 	@Override
	    			public void onError(int error) {
	    				// TODO Auto-generated method stub
	    				System.out.println("error "+error);
	    			}

	    			@Override
	    			public void onPartialResults(Bundle partialResults) {
	    				// TODO Auto-generated method stub
	    				System.out.println("partial res");
	    			}

	    			@Override
	    			public void onEvent(int eventType, Bundle params) {
	    				// TODO Auto-generated method stub
	    				System.out.println("shit");
	    			}

	    }
	    BroadcastReceiver onBatteryChanged=new BroadcastReceiver() {   
	    	public void onReceive(Context context, Intent intent) {      
	    	int pct=100*intent.getIntExtra("level", 1)/intent.getIntExtra("scale", 1); 
	    	if(pct<=9)
	    	    {
	    		GPSTracker.toUseGPS=false;
	    		Intent i=new Intent(getBaseContext(),SlideMenuWithActivityGroup.class);
     		    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
     	    	PendingIntent pIntent = PendingIntent.getActivity(getBaseContext(),0,i,0);
     	        Notification n  = new NotificationCompat.Builder(getBaseContext())
     	        .setContentTitle("Power Saver Mode On")
     	        .setContentText("Battery level low")
     	        .setSmallIcon(R.drawable.al)
     	        .setContentIntent(pIntent)
     	        .setAutoCancel(true)
     	        .build();
     	        n.flags |=Notification.DEFAULT_LIGHTS;
     	        NotificationManager notif = (NotificationManager) 
     	    		    getSystemService(NOTIFICATION_SERVICE); 
     	        
     	        //notif.notify(0, n);
	    	    }
	    	else
	    		{
	    		GPSTracker.toUseGPS=true;
	    		Intent i=new Intent(getBaseContext(),SlideMenuWithActivityGroup.class);
     		    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
     	    	PendingIntent pIntent = PendingIntent.getActivity(getBaseContext(),0,i,0);
     	        Notification n  = new NotificationCompat.Builder(getBaseContext())
     	        .setContentTitle("Power Saver Mode Off")
     	        .setContentText("Battery level Normal")
     	        .setSmallIcon(R.drawable.hc)
     	        .setContentIntent(pIntent)
     	        .setAutoCancel(true)
     	        .build();
     	        n.flags |=Notification.DEFAULT_LIGHTS;
     	        n.flags |= Notification.FLAG_NO_CLEAR;
   	            NotificationManager notif = (NotificationManager) 
     	    		    getSystemService(NOTIFICATION_SERVICE); 
     	        
     	        //notif.notify(3,n);
	    		}
	    	switch(intent.getIntExtra("status", -1)) {        
	    	  case BatteryManager.BATTERY_STATUS_CHARGING:          
	    	  break;                
	    	  case BatteryManager.BATTERY_STATUS_FULL:          
	    	  int plugged=intent.getIntExtra("plugged", -1);                    
	    	  if (plugged==BatteryManager.BATTERY_PLUGGED_AC ||             
	    	  plugged==BatteryManager.BATTERY_PLUGGED_USB) {            
	    	  }          
	    	else {            

	    	}          
	    	break;                
	    	default:          
	    	break;     
	    	}    
	    	}  
	    	};

} 
