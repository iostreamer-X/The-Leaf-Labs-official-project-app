package com.example.feelsafe;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class save extends Activity{
	@Override
	  protected void onCreate(Bundle o) {
		super.onCreate(o);
		Log.d("dfg","working");
		Toast.makeText(getApplicationContext(),"working", Toast.LENGTH_SHORT).show();
        finish();
	}
	
	

}
