package com.example.feelsafe;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.MenuItem.OnMenuItemClickListener;
import android.widget.TextView;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class Wait extends Activity{
	ProgressWheel pw;
	Thread t;
	boolean doit=true;
	@Override
	protected void onCreate(Bundle savedInstanceState){
		if(!MainMenu.toggle)
		{
			Intent intent = new Intent(getBaseContext(), MainMenu.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
	    	intent.putExtra("launch",true);
            startActivity(intent);
		    finish();	
		}
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.wait);
		overridePendingTransition(R.anim.abc_fade_in,R.anim.abc_fade_out);
		getActionBar().setSplitBackgroundDrawable(new ColorDrawable(Color.parseColor("#000000")));
		ActionBar mActionBar = getActionBar();
		mActionBar.setDisplayShowHomeEnabled(false);
		mActionBar.setDisplayShowTitleEnabled(false);
		LayoutInflater mInflater = LayoutInflater.from(this);
        View mCustomView = mInflater.inflate(R.layout.custom_actionbar, null);
		TextView mTitleTextView = (TextView) mCustomView.findViewById(R.id.title_text);
		Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/Terminal Dosis-Light.ttf");
		mTitleTextView.setTypeface(tf);
		mTitleTextView.setText("Protect ME");
        mActionBar.setCustomView(mCustomView);
		mActionBar.setDisplayShowCustomEnabled(true);
		pw=(ProgressWheel) findViewById(R.id.pwtwo);
        pw.setTextSize(50);
		pw.setTextColor(Color.parseColor("#ffffff"));
		pw.setAlpha((float) 0.8);
		pw.setText("Launching");
		pw.spin();
		t=new Thread()
		{
			public void run()
			{
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(doit)
				{Intent intent = new Intent(getBaseContext(),SlideMenuWithActivityGroup.class);
	            intent.putExtra("launch",true);
	            startActivity(intent);
			    finish();
				}
			}};
		t.start();
		
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		menu.findItem(R.id.addcon).setVisible(false);
		menu.findItem(R.id.helpComing).setVisible(false);
		menu.findItem(R.id.helpRequired).setVisible(false);
		menu.findItem(R.id.alertLanched).setVisible(false);
		menu.findItem(R.id.google).setVisible(false);
		menu.findItem(R.id.nc).setTitle("Cancel Alert").setOnMenuItemClickListener(new OnMenuItemClickListener(){
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				Intent intent = new Intent(getBaseContext(),SlideMenuWithActivityGroup.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		    	startActivity(intent);
	            doit=false;
	            finish();
				return false;
			}});
		return super.onCreateOptionsMenu(menu);
	}	
}
