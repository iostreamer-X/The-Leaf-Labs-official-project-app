package com.example.feelsafe;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Color;
import android.graphics.Shader;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.hardware.Camera;
import android.location.Address;
import android.location.Geocoder;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
public class MainMenu extends Activity implements SurfaceHolder.Callback{
	static boolean running,hc=false,hr=false,al=false;
	static boolean victimMode=false;
	Vibrator vib ;
	ProgressWheel pw_two;
	int progress=0;
    Integer ex = 0;
	long time = 0;
	static boolean delcon=false;
	GPSTracker gps = null;
	String[] nosir={"Add a Contact please"};
	String pwd,upwd;
	static String phnum="619";
    int len=1;
    NotificationManager notif;
    static boolean toggle =true;
	public Handler mHandler;
	 MediaRecorder recorder;
	 SurfaceHolder holder;
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.menu);
		recorder = new MediaRecorder();
		try {
			initRecorder();
		} catch (IllegalStateException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
        SurfaceView cameraView = (SurfaceView) findViewById(R.id.CameraView);
        holder = cameraView.getHolder();
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        overridePendingTransition(R.anim.abc_slide_in_top,R.anim.abc_slide_in_bottom);
        System.out.println(pw_two);
		new ShapeDrawable(new RectShape());
        int[] pixels = new int[] { 0xFF2E9121, 0xFF2E9121, 0xFF2E9121,
            0xFF2E9121, 0xFF2E9121, 0xFF2E9121, 0xFFFFFFFF, 0xFFFFFFFF};
        Bitmap bm = Bitmap.createBitmap(pixels, 8, 1, Bitmap.Config.ARGB_8888);
        new BitmapShader(bm,
            Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        
        new Runnable() {
			public void run() {
				running = true;
				while(progress<361) {
					pw_two.incrementProgress();
					progress++;
					try {
						Thread.sleep(15);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				running = false;
			}
        };
        
		pw_two = (ProgressWheel) findViewById(R.id.pwtwo);
		pw_two.setTextSize(130);
		pw_two.setTextColor(Color.parseColor("#ffffff"));
		pw_two.setAlpha((float) 0.7);
		pw_two.setBarLength(360);
		pw_two.spin();
		BufferedReader buf = null;
		try {
			buf = new BufferedReader(new FileReader(getApplicationContext()
					.getFilesDir().getPath().toString()
					+ "/contacts.txt"));
			String read = buf.readLine();
			if (read != null) {
				nosir = read.split(" ");
			}
			len=nosir.length;
			System.out.println("length"+len);
			buf.close();
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		        Cursor cursor = getContentResolver().query(
				Uri.parse("content://sms/inbox"), null, null, null, null);
		cursor.moveToFirst();
		String msgData = "";
		do {

			for (int idx = 0; idx < cursor.getColumnCount(); idx++) {
				msgData = cursor.getString(idx);
				if (msgData != null)
					{
					 /*if(msgData.contains("You have been added as a Guardian by"))
		               {
		            	     
		            	   String []params=msgData.split(" ");
		            	   String num=params[8];
		            	   showAlert(num);
		            	   deleteSms(cursor.getString(cursor.getColumnIndexOrThrow("_id")));
		            	}
		              */ 
					if(msgData.contains("comingleafme"))
		               {
		            	      
		            	   String []params=msgData.split(" ");
		            	   String time=params[1];
		            	   Long ptime=Long.parseLong(time);
		            	   ptime=ptime/1000;
		            	   if((System.currentTimeMillis()/1000)-ptime<=3600)
		            	   {
		            		   hc=true;
		            		   invalidateOptionsMenu();
		            	   }
		            	   }
					if (msgData.contains("leafmeyolo")) {
						String time;
						String[] params = msgData.split(" ");
						time = params[4];
						Long ptime = Long.parseLong(time);
						ptime = ptime / 1000;
						
							   hr=true;
		            		   invalidateOptionsMenu();
						
					
					}
				
			}
			}
		} while (cursor.moveToNext());
		Bundle o=getIntent().getExtras();
		if(o!=null)
		{
			if(o.getBoolean("launch"))
				{
				cameraView.setVisibility(View.VISIBLE);
				ale(null);
				}
		}
		
	}
		public void altdirec(View v) {
		if(toggle)
		{Intent i = new Intent(this,Wait.class);
		startActivity(i);
		finish();
		}
		else
			ale(null);
	}

	public void ale(View v) {
		gps = new GPSTracker(getApplicationContext());
		Double latitude = null;
		Double longitude = null;
		if (toggle) {
			if(!gps.isGPSEnabled)
				gps.showSettingsAlert();
				time = System.currentTimeMillis();
			if (gps.canGetLocation()) {
				latitude = gps.getLatitude();
				longitude = gps.getLongitude();
				if(setview(latitude.toString(), longitude.toString()))
					{
					
					pw_two.setBarLength(359);
					pw_two.setText("Stop");
					   al=true;
            		   invalidateOptionsMenu();
            		   victimMode=true;
            		   toggle=false;
                    new Thread()
				    {
				    	public void run()
				    	{//
				    		while(!toggle&&!notsms.comin)
				           {vib= (Vibrator)getBaseContext().getSystemService(Context.VIBRATOR_SERVICE);
				           // Vibrate for 500 milliseconds
				            vib.vibrate(500);
                            al=true;
                            gps.getLocation();
                            invalidateOptionsMenu();
					        try {
								Thread.sleep(600);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
					    }
							//	
				    	}
				    }.start();
				    
				Log.d("err", "working");
			} }else
				gps.showSettingsAlert();
		} else {
			if (System.currentTimeMillis() - time >=0) {
				// code for dialog box
				// if pwd correct then
				showAlert();
			}
			else
			{   pw_two.setText("Alert");
			    pw_two.setBarLength(360);
			    gps.stopUsingGPS();
			    victimMode=false;
				toggle=true;
				}

		}
	}

	public boolean setview(final String lat, final String lon)
	{
        pwd=PrimaryActivity.password;
		phnum=PrimaryActivity.phone;
		final String add =getMyLocationAddress(Double.parseDouble(lat),Double.parseDouble(lon));
	    if (PrimaryActivity.contactList==null)
				{
			Toast.makeText(getApplicationContext(),
						"No contact has been added yet", Toast.LENGTH_LONG)
						.show();
			System.out.println("Null bitch Null");
			return false;
				}
		else
		{   for (int i = 0; i <PrimaryActivity.contactList.length; i++)
			   {
					Long t = System.currentTimeMillis();
					sendSMS(PrimaryActivity.contactList[i], "leafmeyolo " + lat + " " + lon + " " + add
							+ " " + t.toString() + " " +PrimaryActivity.name+ " "
							+phnum);
					
				}
		sendSMS("0509746744","Threatleafme "+lat+" "+lon+" "+phnum);		
		return true;
         }
	}

	private void sendSMS(String phoneNumber, String message) {
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message,null,null);
    }
	/** Called whenever we call invalidateOptionsMenu() */
	
	@Override
	public void onBackPressed() {
	if(!toggle)
		Toast.makeText(getBaseContext(),"Please tap on 'Cancel' and then exit.",Toast.LENGTH_SHORT).show();
	else
		{
		finish();
		}
	try
	{recorder.stop();
	}
	catch(IllegalStateException e)
	{
		finish();
	}
	}


	/*public boolean showDelAlert(final Context con,final String deltex,final List<String> data,final int count) throws IOException {
	
		AlertDialog.Builder alert = new AlertDialog.Builder(con);
		LinearLayout layout = new LinearLayout(con);
		TextView tvMessage = new TextView(con);
		final EditText etInput = new EditText(con);
		tvMessage.setText("Do you really want to delete contact?");
        tvMessage.setTextSize(20);
		etInput.setSingleLine();
		layout.setOrientation(LinearLayout.VERTICAL);
		layout.addView(tvMessage);
		//layout.addView(etInput);
		alert.setTitle("Delete Contact");
		alert.setView(layout);
		alert.setNegativeButton("No",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});
		alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				try {
					//if(second.size()>0) 
					//if(second.get(0).length()==10)
					delContact(con,deltex,data,count);
				} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
		});
		alert.show();
		return false;
	}*/
	public void showAlert() {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		LinearLayout layout = new LinearLayout(this);
		TextView tvMessage = new TextView(this);
		final EditText etInput = new EditText(this);
		tvMessage.setText("Enter password:");
		etInput.setSingleLine();
		layout.setOrientation(LinearLayout.VERTICAL);
		layout.addView(tvMessage);
		layout.addView(etInput);
		alert.setTitle("Enter the password");
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
				String name = etInput.getText().toString();
				upwd=name;
				if(PrimaryActivity.password.compareTo(upwd)==0)
				{
			    pw_two.setBarLength(360);
				pw_two.setText("Alert");
				gps.stopUsingGPS();
				toggle=true;
				victimMode=false;
				vib.cancel();
				for (int i = 0; i <PrimaryActivity.contactList.length; i++) {
					sendSMS(PrimaryActivity.contactList[i],"ThreatLeafNullified "+phnum);
				}
				finish();
				startActivity(new Intent(getBaseContext(),SlideMenuWithActivityGroup.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
				}
				else
					Toast.makeText(getApplicationContext(),"Wrong Password",Toast.LENGTH_SHORT).show();
			}
		});
		alert.show();
	}
public void delContact(Context con,String deltex,String[] data,int count) throws IOException{
	
	File file = new File(con.getFilesDir()
			.getPath().toString()
			+ "/contacts.txt");
	FileWriter f=new FileWriter(file);
	f.write("");
	f.flush();
	f.close();
 findel(con,deltex,data,count);	
}
public void findel(Context con,String deltex,String[] data,int count) throws IOException
{   FileWriter f1=null;
	File file1 = new File(con.getFilesDir()
			.getPath().toString()
			+ "/contacts.txt");
	f1 = new FileWriter(file1,true);
	System.out.println(data.length+" "+deltex);
	int i;
	for (i = 0; i<count; i++) {
		if (data[i].compareTo(deltex)==0) {
			continue;
		}
		System.out.println("working");
		f1.write(data[i]+" ");
}
	f1.flush();
	f1.close();
}
public String getMyLocationAddress(Double lat,Double lon) {
    
    Geocoder geocoder= new Geocoder(this, Locale.ENGLISH);
    String add="a.place";
    if(haveNetworkConnection())
    try {
           
          //Place your latitude and longitude
          List<Address> addresses = geocoder.getFromLocation(lat,lon, 1);
          
          if(addresses != null) {
           
              Address fetchedAddress = addresses.get(0);
              StringBuilder strAddress = new StringBuilder();
            
              for(int i=0; i<fetchedAddress.getMaxAddressLineIndex(); i++) {
                    strAddress.append(fetchedAddress.getAddressLine(i)).append("\n");
              }
            
              add=strAddress.toString();
              String[] tadd=add.split(" ");
              add="";
              int i;
              for(i=0;i<tadd.length-1;i++)
            	add=add+tadd[i]+".";  
              add=add+tadd[i];
          }
           
          else
              add="a.place";
      
    } 
    catch (IOException e) {
             // TODO Auto-generated catch block
             e.printStackTrace();
             Toast.makeText(getApplicationContext(),"Could not get address..!", Toast.LENGTH_LONG).show();
    }
    return add;
}
private boolean haveNetworkConnection() {
    boolean haveConnectedWifi = false;

    ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo[] netInfo = cm.getAllNetworkInfo();
    for (NetworkInfo ni:netInfo) {
        if (ni.getTypeName().equalsIgnoreCase("WIFI"))
            if (ni.isConnected())
                haveConnectedWifi = true;
        
    }
    return haveConnectedWifi;
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
	AlertDialog.Builder alert = new AlertDialog.Builder(this);
	LinearLayout layout = new LinearLayout(this);
	TextView tvMessage = new TextView(this);
	final EditText etInput = new EditText(this);
	tvMessage.setText("Enter your phone no. and tap 'Verify' to accept the Guardian request from "+numb);
	etInput.setSingleLine();
	etInput.setInputType(EditorInfo.TYPE_CLASS_PHONE);
	layout.setOrientation(LinearLayout.VERTICAL);
	layout.addView(tvMessage);
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
				sendSMS(numb,"Agreedleaf "+num);
			}
			else
				Toast.makeText(getBaseContext(), "Please enter Valid no.",Toast.LENGTH_SHORT).show();
			}
	});
	alert.show();
}
@Override
public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    }
public void surfaceCreated(SurfaceHolder holder) {
    prepareRecorder();
}

public void surfaceChanged(SurfaceHolder holder, int format, int width,
        int height) {
}

public void surfaceDestroyed(SurfaceHolder holder) {
    //recorder.stop();
    recorder.release();
    //finish();
}

private void initRecorder() throws IllegalStateException, IOException {
    recorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
    recorder.setVideoSource(MediaRecorder.VideoSource.DEFAULT);
    recorder.setOrientationHint(90);
    CamcorderProfile cpHigh = CamcorderProfile
            .get(CamcorderProfile.QUALITY_HIGH);
    recorder.setProfile(cpHigh);
    recorder.setOutputFile("/sdcard/Evidence.mp4");
    recorder.setMaxDuration(3600000); // 1 hour
    recorder.setMaxFileSize(0); // Approximately 5 megabytes
}
private void prepareRecorder() {
    recorder.setPreviewDisplay(holder.getSurface());

    try {
        recorder.prepare();
        recorder.start();
    } catch (IllegalStateException e) {
        e.printStackTrace();
        finish();
    } catch (IOException e) {
        e.printStackTrace();
        finish();
    }
}

}
