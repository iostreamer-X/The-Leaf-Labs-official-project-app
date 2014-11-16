package com.example.feelsafe;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class About extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);
		overridePendingTransition(R.anim.abc_slide_in_top,
				R.anim.abc_slide_in_bottom);
		getActionBar().setSplitBackgroundDrawable(
				new ColorDrawable(Color.parseColor("#000000")));
		ActionBar mActionBar = getActionBar();
		mActionBar.setDisplayShowHomeEnabled(false);
		mActionBar.setDisplayShowTitleEnabled(false);
		LayoutInflater mInflater = LayoutInflater.from(this);
		View mCustomView = mInflater.inflate(R.layout.custom_actionbar, null);
		TextView mTitleTextView = (TextView) mCustomView
				.findViewById(R.id.title_text);
		mTitleTextView.setText("Leaf Laboratories");
		mActionBar.setCustomView(mCustomView);
		mActionBar.setDisplayShowCustomEnabled(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		menu.findItem(R.id.addcon).setVisible(false);
		menu.findItem(R.id.helpComing).setVisible(false);
		menu.findItem(R.id.helpRequired).setVisible(false);
		menu.findItem(R.id.alertLanched).setVisible(false);
		menu.findItem(R.id.google).setVisible(false);
		menu.findItem(R.id.nc).setTitle("Contact Us");

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.nc) {
			try {

				this.getPackageManager().getPackageInfo("com.facebook.katana",
						0);
				Intent i = new Intent(Intent.ACTION_VIEW,
						Uri.parse("fb://profile/430301230443558"));
				startActivity(i);

			} catch (Exception e) {

				startActivity(new Intent(Intent.ACTION_VIEW,
						Uri.parse("https://www.facebook.com/LeafCorporations")));
			}
		}
		return super.onOptionsItemSelected(item);
	}

}
