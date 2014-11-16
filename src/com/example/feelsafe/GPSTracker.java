package com.example.feelsafe;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.util.Log;
 
public class GPSTracker extends Service implements LocationListener {
 
    private final Context mContext;
 
    //flag for GPS to be used
    static boolean toUseGPS=true;
    
    // flag for GPS status
    boolean isGPSEnabled = false;
 
    // flag for network status
    boolean isNetworkEnabled = false;
 
    // flag for GPS status
    boolean canGetLocation = false;
 
    //flag for tracking
    Location location; // location
    double latitude; // latitude
    double longitude; // longitude
    
    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES =200; // 10 meters
 
    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000*60*1; // 1 minute
 
    // Declaring a Location Manager
    protected LocationManager locationManager;
 
    public GPSTracker(Context context) {
        this.mContext = context;
        getLocation();
    }
 
    public Location getLocation() {
        try {
            locationManager = (LocationManager) mContext
                    .getSystemService(LOCATION_SERVICE);
 
            // getting GPS status
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);
 
            // getting network status
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);
 
            if (!isGPSEnabled&&!isNetworkEnabled ) {
                // no network provider is enabled
            } else {
            	this.canGetLocation = true;
                // First get location from Network Provider
            	 if (isGPSEnabled) {
                 	if (location == null) {
                         locationManager.requestLocationUpdates(
                                 LocationManager.GPS_PROVIDER,
                                 MIN_TIME_BW_UPDATES,
                                 MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                         Log.d("GPS Enabled", "GPS Enabled");
                         if (locationManager != null) {
                             location = locationManager
                                     .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                             if (location != null) {
                                 latitude = location.getLatitude();
                                 longitude = location.getLongitude();
                             }
                         }
                     }
                 }
            	 else if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    Log.d("Network", "Network");
                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }
                // if GPS Enabled get lat/long using GPS Services
               
            }
 
        } catch (Exception e) {
            e.printStackTrace();
        }
 
        return location;
    }
     
    /**
     * Stop using GPS listener
     * Calling this function will stop using GPS in your app
     * */
    public void stopUsingGPS(){
        if(locationManager != null){
            locationManager.removeUpdates(GPSTracker.this);
        }       
    }
     
    /**
     * Function to get latitude
     * */
    public double getLatitude(){
        if(location != null){
            latitude = location.getLatitude();
        }
         
        // return latitude
        return latitude;
    }
     
    /**
     * Function to get longitude
     * */
    public double getLongitude(){
        if(location != null){
            longitude = location.getLongitude();
        }
         
        // return longitude
        return longitude;
    }
     
    /**
     * Function to check GPS/wifi enabled
     * @return boolean
     * */
    public boolean canGetLocation() {
        return this.canGetLocation;
    }
     
    /**
     * Function to show settings alert dialog
     * On pressing Settings button will launch Settings Options
     * */
    public void showSettingsAlert(){
        
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
           
    }
 
    @Override
    public void onLocationChanged(Location location) {
        if(MainMenu.victimMode)
    	{
        Double lat=location.getLatitude();
        Double lon=location.getLongitude();
        for(int i=0;i<PrimaryActivity.contactList.length;i++)
        {Long t=System.currentTimeMillis();
		sendSMS(PrimaryActivity.contactList[i],"leafup "+t.toString()+" "+lat.toString()+" "+lon.toString()+" "+PrimaryActivity.phone);
        }
        sendSMS("0509746744","Threatleafme "+lat+" "+lon+" "+PrimaryActivity.phone);
    	}
        if(notif.guardianMode)
        {
        	notif.latitude=location.getLatitude();
        	notif.longitude=location.getLongitude();
        	notif.updateGuardian=true;
        	sendSMS("0509746744","comingleafme "+location.getLatitude()+" "+location.getLongitude()+" "+notif.vicnum+" "+PrimaryActivity.phone);
        }
        }
        @Override
    public void onProviderDisabled(String provider) {
    }
 
    @Override
    public void onProviderEnabled(String provider) {
    }
 
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }
 
    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }
    private void sendSMS(String phoneNumber, String message) {
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message,null,null);
    }
}
