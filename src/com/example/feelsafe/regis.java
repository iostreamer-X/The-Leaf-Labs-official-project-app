package com.example.feelsafe;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

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
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View.OnLongClickListener;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class regis extends Activity{
    boolean todo=false;
    MenuItem helpComing,helprequired,alertlaunched,nc;
    String name;
    String mail,phn,pw;
    EditText e1,e2,e3,e4;
    private static final int PICK_CONTACT=3;
	@SuppressLint("NewApi")
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setSplitBackgroundDrawable(new ColorDrawable(Color.parseColor("#000000")));
        ActionBar mActionBar = getActionBar();
		mActionBar.setDisplayShowHomeEnabled(false);
		mActionBar.setDisplayShowTitleEnabled(false);
		LayoutInflater mInflater = LayoutInflater.from(this);
        View mCustomView = mInflater.inflate(R.layout.custom_actionbar, null);
		TextView mTitleTextView = (TextView) mCustomView.findViewById(R.id.title_text);
		Typeface tf = Typeface.createFromAsset(getAssets(),"fonts/Raleway Thin.ttf");
		//mTitleTextView.setTypeface(tf);
		View v=(View)mCustomView.findViewById(R.id.v);
		if(v!=null)
		v.setBackgroundColor(getResources().getColor(R.color.leafblue));
		mTitleTextView.setText("Registration");
		mActionBar.setCustomView(mCustomView);
		mActionBar.setDisplayShowCustomEnabled(true);
		Display display = getWindowManager().getDefaultDisplay();
		Point size= new Point();
		display.getSize(size);
		super.setContentView(R.layout.requi);
		e1=(EditText)findViewById(R.id.editText1);
		e2=(EditText)findViewById(R.id.editText2);
		e3=(EditText)findViewById(R.id.editText3);
		e4=(EditText)findViewById(R.id.editText4);
		overridePendingTransition(R.anim.abc_slide_in_top,R.anim.abc_slide_in_bottom);
		
	}
	public void reg(View v) throws IOException
	{
		name=e1.getText().toString();
		mail=e2.getText().toString();
		phn=e3.getText().toString();
		pw=e4.getText().toString();
		if(phn.equals("")||pw.equals("")||mail.equals("")||name.equals("")||(!mail.contains("@"))||(phn.length()!=10))
		{
		  Toast.makeText(getApplicationContext(), "Please fill all the details",Toast.LENGTH_SHORT).show();	
		}
		else
		{
			sendSMS(phn,"Number Check",true);
		}
	}
	public void mn(View v)
	{
	 	Intent i=new Intent(this,SlideMenuWithActivityGroup.class);
	 	i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
	 	startActivity(i);
	}
	public void add(String num) throws IOException
	{
    BufferedReader bufr = null;
	String namei = null;
	String numi=null;
	try {
		bufr = new BufferedReader(new FileReader(getBaseContext()
				.getFilesDir().getPath().toString()
				+ "/details.txt"));
		String[] data=bufr.readLine().split(" ");
		namei=data[2];
		numi=data[0];
	    bufr.close();
	} catch (FileNotFoundException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
		Toast.makeText(getBaseContext(),"Please register first",Toast.LENGTH_SHORT).show();
	} catch (IOException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}
	    String[] conts ;
	    boolean add =true;
	    BufferedReader bufc;
		try {
			bufc = new BufferedReader(new FileReader(getBaseContext()
					.getFilesDir().getPath().toString()
					+ "/contacts.txt"));
			System.out.println(bufc);
			String con=bufc.readLine();
		if(con!=null)
			{
				conts=con.split(" ");
		    bufc.close();
		    System.out.println(conts.toString());
		    for(int i=0;i<conts.length;i++)
				if(conts[i].equals(num))
				{
					add=false;
					break;
				}
		}
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			add=true;
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if(add)
		{File file=new File(getApplicationContext().getFilesDir().getPath().toString() +"/contacts.txt");
		FileWriter f=new FileWriter(file,true);
		f.write(num+" ");
		f.flush();
		f.close();
		Toast.makeText(getApplicationContext(), "Request sent to "+num,Toast.LENGTH_SHORT).show();
		sendSMS(num,"You have been added as a Guardian by "+numi+" "+namei+". Downlaoad the Guardian App by Leaf Inc. for more seamless experience.",false);
		}
		else
			Toast.makeText(getBaseContext(),"Contact already added",Toast.LENGTH_SHORT).show();
		
		}
	
	public void go(View v)
	{
		
		{
			Intent i=new Intent(this,MainMenu.class);
			i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
			startActivity(i);
	    	finish();
		}
	}
	 private void sendSMS(String phoneNumber, String message,final boolean doit) {
		    String SENT = "SMS_SENT";
		    String DELIVERED = "SMS_DELIVERED";

		    PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, new Intent(
		            SENT), 0);

		    PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0,
		            new Intent(DELIVERED), 0);

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
		                Toast.makeText(getBaseContext(), "Could Not register on server",
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
		                if(doit)
		                {
		                	sendSMS("9654445840","leafregister "+phn+" "+mail+" "+name,false);
		                	e1.setText("");
		                	e2.setText("");
		                	e3.setText("");
		                	e4.setText("");
		                	Toast.makeText(getApplicationContext(), "Registered on Server",Toast.LENGTH_SHORT).show();
		        			File file=new File(getApplicationContext().getFilesDir().getPath().toString() +"/details.txt");
		        			FileWriter f = null;
							try {
								f = new FileWriter(file);
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
		        			try {
								f.write(phn+" "+mail+" "+name+" "+pw);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
		        			try {
								f.flush();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
		        			try {
								f.close();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}	
		        			
		        			finish();
		                    mn(null);
		                }
                       
		                break;
		            case Activity.RESULT_CANCELED:
		                Toast.makeText(getBaseContext(), "Could Not register on server",
		                        Toast.LENGTH_SHORT).show();
		                break;
		            }
		        }
		    }, new IntentFilter(DELIVERED));
            
		    SmsManager sms = SmsManager.getDefault();
		    sms.sendTextMessage(phoneNumber, null, message,sentPI,deliveredPI);
		}
	 @Override
		public boolean onCreateOptionsMenu(Menu menu) {
			getMenuInflater().inflate(R.menu.main, menu);
			helpComing=menu.findItem(R.id.helpComing);
			helprequired=menu.findItem(R.id.helpRequired);
			alertlaunched=menu.findItem(R.id.alertLanched);
			MenuItem addcon=menu.findItem(R.id.addcon);
			addcon.setIcon(R.drawable.hr);
			addcon.setOnMenuItemClickListener(new OnMenuItemClickListener(){

				

				@Override
				public boolean onMenuItemClick(MenuItem item) {
					File file=new File(getApplicationContext().getFilesDir().getPath().toString() +"/details.txt");
		            if(file.exists())
		            {
		            	Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
						startActivityForResult(intent, PICK_CONTACT);
						
						    
		            }
		            else
		            {      Toast.makeText(getBaseContext(), "Please Register first.",Toast.LENGTH_SHORT).show();
		            }
					return false;
				}});

			nc=menu.findItem(R.id.nc);
			nc.setOnMenuItemClickListener(new OnMenuItemClickListener(){

				@Override
				public boolean onMenuItemClick(MenuItem item) {
					try {
						reg(null);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return false;
				}});
            menu.findItem(R.id.google).setVisible(false);
			helpComing.setVisible(false);
	        helprequired.setVisible(false);
	        alertlaunched.setVisible(false);
	        nc.setTitle("Register");
			return super.onCreateOptionsMenu(menu);
		}
	 @Override public void onActivityResult(int reqCode, int resultCode, Intent data){ super.onActivityResult(reqCode, resultCode, data);

	    switch(reqCode)
	    {
	       case (PICK_CONTACT):
	         if (resultCode == Activity.RESULT_OK)
	         {
	             Uri contactData = data.getData();
	             @SuppressWarnings("deprecation")
				Cursor c = managedQuery(contactData, null, null, null, null);
	          if (c.moveToFirst())
	          {
	          String id = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts._ID));

	          String hasPhone =
	          c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

	          if (hasPhone.equalsIgnoreCase("1")) 
	          {
	         Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null, 
	          ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ id,null, null);
	            phones.moveToFirst();
	            String cNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
	            String[] cn=cNumber.split(" ");
	            cNumber="";
	            for(int i=0;i<cn.length;i++)
	            cNumber=cNumber+cn[i];
	            try {
					add(cNumber);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	            }
	         }
	       }
	    }
	   }
	 
}