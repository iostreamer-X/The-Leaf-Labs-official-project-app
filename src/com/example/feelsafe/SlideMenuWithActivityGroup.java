package com.example.feelsafe;

import java.util.ArrayList;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ActivityGroup;
import android.app.LocalActivityManager;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.aretha.slidemenu.SlideMenu;
import com.aretha.slidemenu.SlideMenu.LayoutParams;

@SuppressWarnings("deprecation")
public class SlideMenuWithActivityGroup extends ActivityGroup  implements TextToSpeech.OnInitListener{
	private SlideMenu mSlideMenu;
	MenuItem helpComing,helprequired,alertlaunched,nc;
	private TextToSpeech tts;
	static Point size ;
	private final int REQ_CODE_SPEECH_INPUT = 100;
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_slidemenu);
		overridePendingTransition(R.anim.abc_slide_in_top,R.anim.abc_slide_in_bottom);
		getActionBar().setSplitBackgroundDrawable(new ColorDrawable(Color.parseColor("#000000")));
		ActionBar mActionBar = getActionBar();
		mActionBar.setDisplayShowHomeEnabled(false);
		mActionBar.setDisplayShowTitleEnabled(false);
		LayoutInflater mInflater = LayoutInflater.from(this);
        View mCustomView = mInflater.inflate(R.layout.custom_actionbar, null);
		TextView mTitleTextView = (TextView) mCustomView.findViewById(R.id.title_text);
		Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/Raleway Thin.ttf");
		//mTitleTextView.setTypeface(tf);
		mTitleTextView.setText("Protect me");
        mActionBar.setCustomView(mCustomView);
		mActionBar.setDisplayShowCustomEnabled(true);
		this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
	}

	@SuppressLint("NewApi")
	@Override
	public void onContentChanged() {
		super.onContentChanged();
		mSlideMenu = (SlideMenu) findViewById(R.id.slideMenu);
		Display display = getWindowManager().getDefaultDisplay();
		size= new Point();
		display.getSize(size);
		final LocalActivityManager activityManager = getLocalActivityManager();
		View primary = activityManager.startActivity("PrimaryActivity",
				new Intent(this, PrimaryActivity.class)).getDecorView();
		mSlideMenu.addView(primary, new LayoutParams(
				(int) (size.x*0.9),
				android.view.ViewGroup.LayoutParams.MATCH_PARENT,
				LayoutParams.ROLE_PRIMARY_MENU));

		View secondary = activityManager.startActivity("SecondaryActivity",
				new Intent(this, SecondaryActivity.class)).getDecorView();
		mSlideMenu.addView(secondary, new LayoutParams(
				(int) (size.x*0.9),
				android.view.ViewGroup.LayoutParams.MATCH_PARENT,
				LayoutParams.ROLE_SECONDARY_MENU));
        Intent mainMenu=new Intent(this,MainMenu.class);
        Bundle o=getIntent().getExtras();
		if(o!=null)
		{
			if(o.getBoolean("launch"))
				{
				mainMenu=new Intent(this,MainMenu.class).putExtra("launch",true);
				}
			
		}
		View content = activityManager.startActivity("MainMenu",
				mainMenu).getDecorView();
		mSlideMenu.addView(content, new LayoutParams(
				android.view.ViewGroup.LayoutParams.MATCH_PARENT,
				android.view.ViewGroup.LayoutParams.MATCH_PARENT,
				LayoutParams.ROLE_CONTENT));
	}
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
        if(MainMenu.hc)
        	{
        	helpComing.setVisible(true);
        	nc.setTitle("Help Coming");
        	helprequired.setVisible(false);
        	}
        if(MainMenu.hr)
        	{
        	helprequired.setVisible(true);
        	helprequired.setOnMenuItemClickListener(new OnMenuItemClickListener(){

				@Override
				public boolean onMenuItemClick(MenuItem item) {
					startActivity(new Intent(getBaseContext(),notif.class));
					return false;
				}});
        	}
        if(MainMenu.al)
        	{
        	alertlaunched.setVisible(true);
        	nc.setTitle("Alert has been launched");
        	helprequired.setVisible(false);
        	}
        if(MainMenu.al==false)
        {
        	alertlaunched.setVisible(false);
        	if(notsms.voice)
			nc.setTitle("Stop Listening");
			else
			nc.setTitle("Listen in Background");
		}
       return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		MenuItem addcon=menu.findItem(R.id.addcon);
		final MenuItem gog=menu.findItem(R.id.google);
        addcon.setVisible(false);
		helpComing=menu.findItem(R.id.helpComing);
		helprequired=menu.findItem(R.id.helpRequired);
		alertlaunched=menu.findItem(R.id.alertLanched);
		nc=menu.findItem(R.id.nc);
		nc.setOnMenuItemClickListener(new OnMenuItemClickListener(){

			@Override
			public boolean onMenuItemClick(MenuItem item) {
				notsms.voice=!notsms.voice;
				if(notsms.voice)
					{
					nc.setTitle("Stop Listening");
					gog.setIcon(R.drawable.ic_action_mic_muted);
					}
					else
					{nc.setTitle("Listen in Background");
					 gog.setIcon(R.drawable.ic_action_mic);
					}
				return false;
			}});;
		helpComing.setVisible(false);
        helprequired.setVisible(false);
        alertlaunched.setVisible(false);
        gog.setVisible(true)
        .setOnMenuItemClickListener(new OnMenuItemClickListener(){

			@Override
			public boolean onMenuItemClick(MenuItem item) {
				if(!notsms.voice)
					{
					 promptSpeechInput();
				    }
				return false;
			}});;
		return super.onCreateOptionsMenu(menu);
	}
	private void promptSpeechInput() {
	    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
	    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
	            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
	    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
	    intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
	            "I am listening...");
	    try {
	        startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
	    } catch (ActivityNotFoundException a) {
	        Toast.makeText(getApplicationContext(),
	                "Tough Luck",
	                Toast.LENGTH_SHORT).show();
	    }
	}

	/**
	 * Receiving speech input
	 * */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);

	    switch (requestCode) {
	    case REQ_CODE_SPEECH_INPUT: {
	        if (resultCode == RESULT_OK && null != data) {

	            ArrayList<String> result = data
	                    .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
	            Toast.makeText(getBaseContext(), result.get(0),Toast.LENGTH_LONG).show();
	            String str=result.get(0);
	            if(str.contains("kutte")||str.contains("kutiya")||str.contains("f***"))
	            	{
	            	tts = new TextToSpeech(this, this);
	            	tts.setPitch(3);
	            	speakOut("Go fuck Yourself asshole.");
	            	}
	            if(str.equals("alert"))
	            {
	            	startActivity(new Intent(getBaseContext(),Wait.class));
	            }
	            if(str.equals("help centre"))
	            {
	            	startActivity(new Intent(getBaseContext(),notif.class));
	            }
	            if(str.contains("add")&&str.split(" ").length>=3)
				  {   String name="";
		              int i=2;
		              for(;i<str.split(" ").length-1;i++)
		              {
		            	  name=name+str.split(" ")[i]+" ";
		              }
		              name=name+str.split(" ")[i];
		              Toast.makeText(getBaseContext(),name+"<-Name",Toast.LENGTH_SHORT).show();
			          Cursor cursor = null;
					    try {
					        cursor = getBaseContext().getContentResolver().query(Phone.CONTENT_URI, null, null, null, null);
					        int contactIdIdx = cursor.getColumnIndex(Phone._ID);
					        int nameIdx = cursor.getColumnIndex(Phone.DISPLAY_NAME_PRIMARY);
					        int phoneNumberIdx = cursor.getColumnIndex(Phone.NUMBER);
					        int photoIdIdx = cursor.getColumnIndex(Phone.PHOTO_ID);
			 		        cursor.moveToFirst();
					        do {
					            String idContact = cursor.getString(contactIdIdx);
					            String contactName = cursor.getString(nameIdx).toLowerCase();
					            String phoneNumber = cursor.getString(phoneNumberIdx);
					            if(str.contains(contactName))
					            {
					            	Toast.makeText(getBaseContext(),"Matchfound",Toast.LENGTH_SHORT).show();
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
	        break;
	    }

	    }
	}

	@Override
	public void onInit(int status) {

	    if (status == TextToSpeech.SUCCESS) {

	        int result = tts.setLanguage(Locale.US);

	        if (result == TextToSpeech.LANG_MISSING_DATA
	                || result == TextToSpeech.LANG_NOT_SUPPORTED) {
	            Log.e("TTS", "This Language is not supported");
	        } 
	        else {
	            speakOut("Go fuck yourself asshole.");
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
	@Override
	public void onBackPressed() {
	if(!MainMenu.toggle)
		Toast.makeText(getBaseContext(),"Please tap on 'Cancel' and then exit.",Toast.LENGTH_SHORT).show();
	else
		{
		finish();
		}
		
	}
}
