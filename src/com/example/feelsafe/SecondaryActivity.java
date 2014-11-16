package com.example.feelsafe;

import java.io.InputStream;
import java.net.URL;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import com.fima.cardsui.objects.CardStack;
import com.fima.cardsui.views.CardUI;

public class SecondaryActivity extends Activity {
	CardUI mCardView;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();
		StrictMode.setThreadPolicy(policy);
		setContentView(R.layout.activity_secondary);

		mCardView = (CardUI) findViewById(R.id.cardsview);
		mCardView.setSwipeable(false);
		mCardView.setAlpha((float) 0.7);
		CardStack stack1 = new CardStack();
		mCardView.addStack(stack1);
		MyImageCard myic1 = new MyImageCard(
				"About Us-Our Journey, Our Aim, Our Vision",
				R.drawable.abundance1);
		myic1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(getBaseContext(), About.class));
			}
		});
		mCardView.addCard(myic1);
		MyPlayCard website = new MyPlayCard("Visit Our Website",

		"www.theleaflabs.com", "#4682B4", "#4682B4", true, false);
		website.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Uri uriUrl = Uri.parse("http://www.theleaflabs.com");
				Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
				startActivity(launchBrowser);

			}

		});
		mCardView.addCard(website);
		MyPlayCard fbPage = new MyPlayCard("Our FaceBook Page",

		"Click to open", "#4682B4", "#4682B4", true, false);
		fbPage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				try {

					getPackageManager()
							.getPackageInfo("com.facebook.katana", 0);
					Intent i = new Intent(Intent.ACTION_VIEW, Uri
							.parse("fb://profile/430301230443558"));
					startActivity(i);

				} catch (Exception e) {

					startActivity(new Intent(Intent.ACTION_VIEW, Uri
							.parse("https://www.facebook.com/LeafCorporations")));
				}

			}

		});
		mCardView.addCard(fbPage);
		MyPlayCard twitterPage = new MyPlayCard("Our Twitter Page",
		"Click to open", "#4682B4", "#4682B4", true, false);
		twitterPage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Uri uriUrl = Uri.parse("http://www.twitter.com/leaflaboratory");
				Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
				startActivity(launchBrowser);

			}

		});
		mCardView.addCard(twitterPage);
		mCardView.refresh();

	}

	public static BitmapDrawable getRoundedRectBitmap(Bitmap bitmap, int pixels) {
		int color;
		Paint paint;
		Rect rect;
		RectF rectF;
		Bitmap result;
		Canvas canvas;
		float roundPx;

		result = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(),
				Bitmap.Config.ARGB_8888);
		canvas = new Canvas(result);

		color = 0xff424242;
		paint = new Paint();
		rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		rectF = new RectF(rect);
		roundPx = pixels;

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		@SuppressWarnings("deprecation")
		BitmapDrawable bitmapDrawable = new BitmapDrawable(result);

		return bitmapDrawable;
	}

	public Bitmap getUserPic(String userID) {
		String imageURL;
		Bitmap bitmap = null;
		Log.d("TAG", "Loading Picture");
		imageURL = "https://graph.facebook.com/" + userID
				+ "/picture?type=large";
		try {
			bitmap = BitmapFactory.decodeStream((InputStream) new URL(imageURL)
					.getContent());
		} catch (Exception e) {
			Log.d("TAG", "Loading Picture FAILED");
			e.printStackTrace();
		}
		return bitmap;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}
}
