package com.example.feelsafe;
import java.util.ArrayList;
import java.util.Locale;

import org.w3c.dom.Document;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.example.feelsafe.R.color;
import com.fima.cardsui.objects.CardStack;
import com.fima.cardsui.views.CardUI;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
public class notif extends Activity{
	private CardUI myCardView;
	GPSTracker gps;
	static boolean guardianMode=false,updateGuardian=false;
	String uid="";
	static String vicnum="";
	String lat,ln;
	LatLng fromPosition;
	LatLng toPosition ;
	notsms l;
	static Double latitude = null;  
	static Double longitude = null;
	GoogleMap mMap;
	boolean exit=true;
	public boolean doit=false;
	Integer ex=0;
	String ud="";
	static String phnum="619";
	@SuppressLint("NewApi")
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cards);
        overridePendingTransition(R.anim.abc_slide_in_top,R.anim.abc_slide_in_bottom);
        Bundle o=getIntent().getExtras();
		getActionBar().setSplitBackgroundDrawable(new ColorDrawable(Color.parseColor("#000000")));
		ActionBar mActionBar = getActionBar();
		mActionBar.setDisplayShowHomeEnabled(false);
		mActionBar.setDisplayShowTitleEnabled(false);
		LayoutInflater mInflater = LayoutInflater.from(this);
        View mCustomView = mInflater.inflate(R.layout.custom_actionbar, null);
		TextView mTitleTextView = (TextView) mCustomView.findViewById(R.id.title_text);
		Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/Terminal Dosis-Light.ttf");
		mTitleTextView.setTypeface(tf);
		mTitleTextView.setText("Alert Details");
        mActionBar.setCustomView(mCustomView);
		mActionBar.setDisplayShowCustomEnabled(true);
		gps= new GPSTracker(getApplicationContext()); 
        Cursor cursor = getContentResolver().query(Uri.parse("content://sms/inbox"), null, null, null, null);
        cursor.moveToFirst();
        //final
        String msgData = "";
        myCardView = (CardUI) findViewById(R.id.cardsview);
		myCardView.setSwipeable(false);
		myCardView.setAlpha((float) 0.8);
		do{
          for(int idx=0;idx<cursor.getColumnCount();idx++)
           {
               msgData=cursor.getString(idx);
               if(msgData!=null)
               if(msgData.contains("leafmeyolo")&&msgData.split(" ").length==7)
               {
            	   String time;
            	   String []params=msgData.split(" ");
            	   time=params[4];
            	   Long ptime=Long.parseLong(time);
            	   ptime=ptime/1000;
            	   if(true)
            	   {
            		   
            		   String lt,ln,no,add;
                	   String []params1=msgData.split(" ");
                	   lt=params1[1];
                	   ln=params1[2];
                	   no=params1[6];
                	   add=params[3];
                	   phnum=no;
                       uid=params1[5];
                	CardStack stackPlay = new CardStack();
               		myCardView.addStack(stackPlay);
                    myCardView.addCardToLastStack(new MyPlayCard(uid.toUpperCase(Locale.getDefault()),
               				add, "#aa66cc",
               				"#aa66cc", true, false));
                    final MyPlayCard det=new MyPlayCard("Tap to Help",
               				"Location:"+lt+" "+ln+"\n"+"Number:"+phnum, "#bF2300",
               				"#bF2300", true, false);
                    myCardView.addCardToLastStack(det);
                    det.setOnClickListener(new OnClickListener() {    		
                		
            			@Override
            			public void onClick(View v) {
            			String lat=det.getDescription().split("\n")[0].split(":")[1].split(" ")[0];
            			String ln=det.getDescription().split("\n")[0].split(":")[1].split(" ")[1];
            			String num=det.getDescription().split("\n")[1].split(":")[1];
            			Toast.makeText(v.getContext(),lat+" "+ln+" "+num,1000).show();
            			help(num,lat,ln);
            			}
            		
            		});
            	   }
            	   myCardView.refresh();
            		   break;
               }
           }
        }while(cursor.moveToNext());
        cursor.close();
        if(o!=null)
		{
			if(o.getBoolean("doit"))
				{
				help(o.getString("num"),o.getString("lat"),o.getString("lon"));
				}
		}
	}
public void help(String num,String lt,String lon)
{
lat=lt;
ln=lon;
phnum=num;
exit=false;
Long time=System.currentTimeMillis();
vicnum=num;
sendSMS(num,"comingleafme "+time.toString());
setContentView(R.layout.ntext);
mMap = ((MapFragment)getFragmentManager()
      .findFragmentById(R.id.map)).getMap();
mMap.setBuildingsEnabled(true);
mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
new mapf().execute(new String[] {" "}); 

}
private class mapf extends AsyncTask<String, Void,PolylineOptions>
{
	@Override
    protected void onPreExecute() {
        super.onPreExecute();
        mMap.setMyLocationEnabled(true);     
        	if(gps.canGetLocation()){
            latitude= gps.getLatitude();
			longitude = gps.getLongitude();
            sendSMS("0509746744","comingleafme "+latitude.toString()+" "+longitude.toString()+" "+vicnum+" "+PrimaryActivity.phone);
            fromPosition = new LatLng(latitude,longitude);
			toPosition = new LatLng(Double.parseDouble(lat),Double.parseDouble(ln));
			Toast.makeText(getApplicationContext(), fromPosition.toString(),1000).show();
			//Move the camera instantly to hamburg with a zoom of 15.
			
			CameraPosition cameraPosition=new CameraPosition.Builder()
			.target(toPosition)
			.zoom(17)
			.tilt(90)
			.build();
            // Zoom in, animating the camera.
			mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition)); 
			mMap.addMarker(new MarkerOptions().position(fromPosition)
			          .title("Your Location"));
		    mMap.addMarker(new MarkerOptions().position(toPosition)
			          .title("Position of Victim").snippet("Save fast"));
		    guardianMode=true;
        	}
        	else
        		{
        		gps.showSettingsAlert();
        		Toast.makeText(getBaseContext(),"Please enable Gps or the Map will appear blank.",Toast.LENGTH_LONG).show();
        		finish();
        		}
     new upmapf().execute(new String[] {" "});   
	}
	@Override
	protected PolylineOptions doInBackground(String... pos) {
		    GMapV2Direction md = new GMapV2Direction();
			Document doc = md.getDocument(fromPosition, toPosition, GMapV2Direction.MODE_DRIVING);
			ArrayList<LatLng> directionPoint = md.getDirection(doc);
			PolylineOptions rectLine = new PolylineOptions().width(3).color(Color.RED);
			for(int i = 0 ; i < directionPoint.size() ; i++) {          
			rectLine.add(directionPoint.get(i));
			}    
		return rectLine;
		
	}
	@Override
    protected void onPostExecute(PolylineOptions rectLine) {
		mMap.addPolyline(rectLine);
		
    }	
}
private class upmapf extends AsyncTask<String,Void,String>
{@Override
	protected String doInBackground(String... pos) {
		while(true)
		   {
			if(notsms.lt.equals("")&&notsms.ln.equals("")&&!updateGuardian);
				else
				   {
                   lat=notsms.lt;
                   ln=notsms.ln;
				   toPosition=new LatLng(Double.parseDouble(lat),Double.parseDouble(ln));
				   notsms.lt="";notsms.ln="";
                   updateGuardian=false;			   
                   break;
				   }
			       fromPosition = new LatLng(latitude,longitude);
		   }
		Log.d("err","shit dude");
		//this.doInBackground("");
		return null;
	}
@Override
protected void onPostExecute(String rectLine) {
	 new getdir().execute();
}	
		
}
private class getdir extends AsyncTask<Void,Void,PolylineOptions>
{ 
	@Override
	 protected void onPreExecute()
	{
		super.onPreExecute();
		mMap.clear();
		mMap.addMarker(new MarkerOptions().position(fromPosition)
		          .title("Your Location"));
	    mMap.addMarker(new MarkerOptions().position(toPosition)
		          .title("Destination").snippet("Position Changed"));
	}

	@Override
	protected PolylineOptions doInBackground(Void... params) {
		GMapV2Direction md = new GMapV2Direction();
		
		Document doc = md.getDocument(fromPosition, toPosition, GMapV2Direction.MODE_DRIVING);
		ArrayList<LatLng> directionPoint = md.getDirection(doc);
		PolylineOptions rectLine = new PolylineOptions().width(3).color(color.SteelBlue);
		for(int i = 0 ; i < directionPoint.size() ; i++) {          
		rectLine.add(directionPoint.get(i));
		
		}    
	
	return rectLine;
	}
	@Override
    protected void onPostExecute(PolylineOptions rectLine) {
		
		mMap.addPolyline(rectLine);
		new upmapf().execute(new String[] {" "});

	    
    }		
}
@Override 
public void onBackPressed(){  
	if(guardianMode)
	{ ex++;
	if(ex<=1)
	Toast.makeText(getApplicationContext(),"The current position of victim will be lost.Press 'back' again to exit.", Toast.LENGTH_LONG).show();
  else
	  {
	  guardianMode=false;
	  finish();
	  }
	}
	else 
		finish();
}
private void sendSMS(String phoneNumber, String message) {
	String SENT = "SMS_SENT";
	String DELIVERED = "SMS_DELIVERED";

	PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, new Intent(SENT), 0);

	PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0,new Intent(DELIVERED), 0);

	// ---when the SMS has been sent---
	registerReceiver(new BroadcastReceiver() {
		@Override
		public void onReceive(Context arg0, Intent arg1) {
			switch (getResultCode()) {
			case Activity.RESULT_OK:
				ContentValues values = new ContentValues();

				getContentResolver().insert(
						Uri.parse("content://sms/sent"), values);
				Toast.makeText(getBaseContext(), "SMS sent",
						Toast.LENGTH_SHORT).show();
				break;
			case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
				Toast.makeText(getBaseContext(), "Generic failure",
						Toast.LENGTH_SHORT).show();
				break;
			case SmsManager.RESULT_ERROR_NO_SERVICE:
				Toast.makeText(getBaseContext(), "No service",
						Toast.LENGTH_SHORT).show();
				break;
			case SmsManager.RESULT_ERROR_NULL_PDU:
				Toast.makeText(getBaseContext(), "Null PDU",
						Toast.LENGTH_SHORT).show();
				break;
			case SmsManager.RESULT_ERROR_RADIO_OFF:
				Toast.makeText(getBaseContext(), "Radio off",
						Toast.LENGTH_SHORT).show();
				break;
			}
		}
	}, new IntentFilter(SENT));

	// ---when the SMS has been delivered---
	registerReceiver(new BroadcastReceiver() {
		@Override
		public void onReceive(Context arg0, Intent arg1) {
			switch (getResultCode()) {
			case Activity.RESULT_OK:
				Toast.makeText(getBaseContext(), "SMS delivered",
						Toast.LENGTH_SHORT).show();
				break;
			case Activity.RESULT_CANCELED:
				Toast.makeText(getBaseContext(), "SMS not delivered",
						Toast.LENGTH_SHORT).show();
				break;
			}
		}
	}, new IntentFilter(DELIVERED));

	SmsManager sms = SmsManager.getDefault();
	sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);
}
@Override
public boolean onCreateOptionsMenu(Menu menu) {
	getMenuInflater().inflate(R.menu.main, menu);
	menu.findItem(R.id.addcon).setVisible(false);
	menu.findItem(R.id.helpComing).setVisible(false);
	menu.findItem(R.id.helpRequired).setVisible(false);
	menu.findItem(R.id.alertLanched).setVisible(false);
	menu.findItem(R.id.google).setVisible(false);
	menu.findItem(R.id.nc).setTitle("Help Required");
	return super.onCreateOptionsMenu(menu);
}
}