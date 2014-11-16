package com.example.feelsafe;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fima.cardsui.objects.CardStack;
import com.fima.cardsui.views.CardUI;

public class PrimaryActivity extends FragmentActivity {
	private CardUI details,contacts;	
	public static String[] contactList=null;
	public static String phone,mail,name,password;
	Dialog dialogbox;
	static ArrayList<String> contactsList=new ArrayList<String>();
	String read;
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_primary);
		details = (CardUI) findViewById(R.id.detailsCard);
		details.setSwipeable(false);
		BufferedReader bufr = null;
		CardStack stackPlay = new CardStack();
		stackPlay.setTitle("Options");
		details.addStack(stackPlay);
		
		try {
			bufr = new BufferedReader(new FileReader(getBaseContext()
					.getFilesDir().getPath().toString()
					+ "/details.txt"));
			String[] nosr = null;
			nosr = bufr.readLine().split(" ");
			phone=nosr[0];
			mail=nosr[1];
			name=nosr[2];
			password=nosr[3];
			MyPlayCard ChangeDetails=new MyPlayCard("Change Details",
					
	   				"& Add Contacts", "#4682B4",
	   				"#4682B4", true, false);
	        ChangeDetails.setOnClickListener(new OnClickListener() {    		
	    		
				@Override
				public void onClick(View v) {
					showRegAlert();
					}
			
			});
			details.addCard(ChangeDetails);
			CardStack detstackPlay = new CardStack();
			detstackPlay.setTitle("Details");
			details.addStack(detstackPlay);
		    details.addCardToLastStack(new MyPlayCard("Phone",
	   				phone, "#4682B4",
	   				"#4682B4", true, false));
			details.addCardToLastStack(new MyPlayCard("E-Mail Address",
	   				mail, "#4682B4",
	   				"#4682B4", true, false));
			details.addCardToLastStack(new MyPlayCard("Name",
	   				name, "#4682B4",
	   				"#4682B4", true, false));
		    
			
		} catch (FileNotFoundException e1) {
          MyPlayCard ChangeDetails=new MyPlayCard("Please Register",
					
	   				"& Add Contacts", "#4682B4",
	   				"#4682B4", true, false);
	        ChangeDetails.setOnClickListener(new OnClickListener() {    		
	    		
				@Override
				public void onClick(View v) {
					Intent i=new Intent(getBaseContext(),regis.class);
				 	i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
				 	startActivity(i);
				 	}
			
			});
			details.addCard(ChangeDetails);
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		contacts = (CardUI) findViewById(R.id.contactsCard);
		contacts.setSwipeable(false);
		final CardStack CstackPlay = new CardStack();
		CstackPlay.setTitle("Contacts");
		contacts.addStack(CstackPlay);
		BufferedReader buf = null;
		try {
			buf = new BufferedReader(new FileReader(getApplicationContext()
					.getFilesDir().getPath().toString()
					+ "/contacts.txt"));
			read= buf.readLine();
			if (read != null) {
				contactList = read.split(" ");
				Log.e("cont",contactList.toString());
				for(int i=0;i<contactList.length;i++)
					{
					contactsList.add(contactList[i]);
					final MyPlayCard con=new MyPlayCard(contactList[i],
					
			   				"Tap to Delete.", "#4682B4",
			   				"#4682B4", true, false);
					con.setOnClickListener(new OnClickListener() {    		
                		
            			@Override
            			public void onClick(View v) {
            		    //MainMenu mm;
                        dialogbox = new Dialog(PrimaryActivity.this);
                        dialogbox.setContentView(R.layout.contactdeletedialog);
                        dialogbox.setTitle("Deleting Contact");
                        dialogbox.setCancelable(true);
                        Button buttonYes = (Button) dialogbox.findViewById(R.id.btnYes);
                        buttonYes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            MainMenu mm=new MainMenu();
                        	try {
								mm.delContact(getApplicationContext(),con.getTitlePlay(),contactList,contactList.length);
							    dialogbox.cancel();    
                        	} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
                        }
                        });
                        dialogbox.show();
                        Button buttonNo = (Button) dialogbox.findViewById(R.id.btnNo);
                        buttonNo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialogbox.dismiss();
                        }
                        });
                        dialogbox.show();
        				}
            		
            		});
					contacts.addCardToLastStack(con);
					}			
			}
			else
			{MyPlayCard con=new MyPlayCard("No Contact Added",
	   				"Please Add a Contact", "#4682B4",
	   				"#4682B4", true, false);
			 contacts.addCardToLastStack(con);
	       }
			buf.close();
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			MyPlayCard con=new MyPlayCard("No Contact Added",
	   				"Please Add a Contact", "#4682B4",
	   				"#4682B4", true, false);
			contacts.addCardToLastStack(con);
			Log.e("cont",e1.toString());
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			MyPlayCard con=new MyPlayCard("IOException",
	   				"An error occured while accessing storage.", "#4682B4",
	   				"#4682B4", true, false);
			contacts.addCardToLastStack(con);
			Log.e("cont",e.toString());
		}
	    if(contactList==null&&read!=null)
		{MyPlayCard con=new MyPlayCard("No Contact Added",
   				"Please Add a Contact", "#4682B4",
   				"#4682B4", true, false);
		contacts.addCardToLastStack(con);
       }
	    details.setAlpha((float) 0.7);
	    contacts.setAlpha((float) 0.7);
	    details.refresh();
		contacts.refresh();
	}
	public void go(View v)
	{
		Intent i=new Intent(getApplicationContext(),regis.class);
		startActivity(i);
	}
	public void showRegAlert(){
		
	AlertDialog.Builder alert = new AlertDialog.Builder(this);
	LinearLayout layout = new LinearLayout(this);
	TextView tvMessage = new TextView(this);
	final EditText etInput = new EditText(this);
	tvMessage.setText("Change Details");
	etInput.setSingleLine();
	layout.setOrientation(LinearLayout.VERTICAL);
	layout.addView(tvMessage);
	layout.addView(etInput);
	alert.setTitle("Please Enter Password to continue...");
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
			String pwd=name;
			if(password.compareTo(pwd)==0)
			{
				Intent i=new Intent(getBaseContext(),regis.class);
				i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
				startActivity(i);
			}
			else
				Toast.makeText(getApplicationContext(),"Wrong Password",Toast.LENGTH_SHORT).show();
		}
	});
	alert.show();
}
}
