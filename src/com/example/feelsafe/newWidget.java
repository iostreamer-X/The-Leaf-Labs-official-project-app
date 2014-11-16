package com.example.feelsafe;

import android.app.Activity;
import android.os.Bundle;

public class newWidget extends Activity{

	MainMenu mm=new MainMenu();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.menu);
		mm.ale(null);
		
	}
}
