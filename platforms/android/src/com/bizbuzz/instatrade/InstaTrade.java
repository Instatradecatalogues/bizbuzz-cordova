/*
       Licensed to the Apache Software Foundation (ASF) under one
       or more contributor license agreements.  See the NOTICE file
       distributed with this work for additional information
       regarding copyright ownership.  The ASF licenses this file
       to you under the Apache License, Version 2.0 (the
       "License"); you may not use this file except in compliance
       with the License.  You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

       Unless required by applicable law or agreed to in writing,
       software distributed under the License is distributed on an
       "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
       KIND, either express or implied.  See the License for the
       specific language governing permissions and limitations
       under the License.
 */

package com.bizbuzz.instatrade;

import com.google.android.gcm.GCMRegistrar;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.ConnectionResult;

import  java.io.OutputStream;
import  java.io.IOException;
import java.io.FileNotFoundException;
import  java.io.File;
import  java.io.FileReader;
import  java.io.BufferedReader;
import  java.net.HttpURLConnection;
import  java.net.MalformedURLException;
import  java.net.URL;
import  java.util.Map;
import  java.util.HashMap;
import  java.util.Map.Entry;
import  java.util.Iterator;
import  java.util.concurrent.atomic.AtomicInteger;

import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.DialogInterface;
import android.widget.Toast;
import android.widget.TextView;
import android.net.Uri;
import android.app.Activity;
import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.util.Log;
import android.text.TextUtils;
import org.json.JSONObject;
import org.json.JSONException;
import org.apache.cordova.*;

public class InstaTrade extends CordovaActivity 
{

    public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
                                           
	String SENDER_ID = "419996752549";
	static final String TAG = "InstaTrade";

    TextView mDisplay;
    GoogleCloudMessaging gcm;
    AtomicInteger msgId = new AtomicInteger();
    SharedPreferences prefs;
    Context context;

	String regid;
	String instaUrl;
	String username;
     
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
       super.onCreate(savedInstanceState);
        // super.init();                                                    	
       	setContentView(R.xml.main);
        mDisplay = (TextView) findViewById(R.id.display);
		super.setIntegerProperty("splashscreen", R.drawable.splash);
		 super.loadUrl(Config.getStartUrl(),10000);
	   
		try {
			JSONObject jsonObject = new JSONObject(getProperties());
			instaUrl = jsonObject.optString("url");
			username = jsonObject.optString("username");		
	    }catch(JSONException jex) {
          jex.printStackTrace();
	   }
	   
		 context = getApplicationContext();
			if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(this);
            regid = getRegistrationId(context);
            if(regid.isEmpty() && !instaUrl.isEmpty() && !username.isEmpty()) {
                registerInBackground();
            }		
        } else {
            Log.i(TAG, "No valid Google Play Services APK found.");
        }
    }
    
    	
	@Override
    protected void onResume() {
     super.onResume();
     checkPlayServices();
    }
    
    
    private String getRegistrationId(Context context) {
      final SharedPreferences prefs = getGCMPreferences(context);
      String registrationId = prefs.getString(PROPERTY_REG_ID, "");
      if (registrationId.isEmpty()) {
         Log.i(TAG, "Registration not found.");
         return "";
      }
     // Check if app was updated; if so, it must clear the registration ID
     // since the existing regID is not guaranteed to work with the new
     // app version.
     int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
     int currentVersion = getAppVersion(context);
     if (registeredVersion != currentVersion) {
         Log.i(TAG, "App version changed.");
         return "";
     }
     return registrationId;
}


private String getProperties(){
	    String baseDir = Environment.getExternalStorageDirectory().getAbsolutePath();
           String fileName = "InstaTrade"+File.separator+"config"+File.separator+"properties.txt";
		   String allProperties="{}";
           // Not sure if the / is on the path or not
           File propertyFile = new File(baseDir + File.separator + fileName);
		   if(!propertyFile.exists()){
					Log.i(TAG,"properties.txt is not exist.");
		           return allProperties;
		   }
		   BufferedReader br=null;
		   	try {
		     br  = new BufferedReader(new FileReader(propertyFile));	
			  StringBuilder sb = new StringBuilder();
			 String line = br.readLine();
			 while (line != null) {
				sb.append(line);
				sb.append(System.lineSeparator());
				line = br.readLine();
			 }
			 allProperties = sb.toString();
			//alertBox(allProperties);
		 	 } catch (IOException   ioex) {
			 		ioex.printStackTrace();
			 }finally {
			 	try {
					if (br != null) 	br.close();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
			return allProperties;
}

private SharedPreferences getGCMPreferences(Context context) {
    // This sample app persists the registration ID in shared preferences, but
    // how you store the regID in your app is up to you.
    return getSharedPreferences(InstaTrade.class.getSimpleName(),Context.MODE_PRIVATE);
}

private boolean checkPlayServices() {
    int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
    if (resultCode != ConnectionResult.SUCCESS) {
        if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
            GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                    PLAY_SERVICES_RESOLUTION_REQUEST).show();
        } else {
            Log.i(TAG, "This device is not supported.");
            finish();
        }
        return false;
    }
    return true;
}

private static int getAppVersion(Context context) {
    try {
        PackageInfo packageInfo = context.getPackageManager()
                .getPackageInfo(context.getPackageName(), 0);
        return packageInfo.versionCode;
    } catch (NameNotFoundException e) {
        // should never happen
        throw new RuntimeException("Could not get package name: " + e);
    }
}

private void registerInBackground() {
    new AsyncTask<Void, Void, String>() {
        @Override
        protected String doInBackground(Void... params) {
            String msg = "";
            try {
                if (gcm == null) {
                    gcm = GoogleCloudMessaging.getInstance(context);
                }
                regid = gcm.register(SENDER_ID);
                msg = "Device registered, registration ID=" + regid;
                
				  // Persist the regID - no need to register again.
                storeRegistrationId(context, regid);
                // You should send the registration ID to your server over HTTP,
                // so it can use GCM/HTTP or CCS to send messages to your app.
                // The request to your server should be authenticated if your app
                // is using accounts.   To the InstaTrade server..
					 Log.i(TAG, "################### Registration Id " +regid);
	              String serverUrl = instaUrl+"/register/deviceregistration";  
                  Map<String, String> regparams = new HashMap<String, String>();
                  regparams.put("regId", regid);
				  regparams.put("username",username);
                  sendRegistrationIdToBackend(serverUrl, regparams);
    
	//	sendEmail(regid);
                // For this demo: we don't need to send it because the device
                // will send upstream messages to a server that echo back the
                // message using the 'from' address in the message.
				
            } catch (IOException ex) {
                msg = "Error :" + ex.getMessage();
                // If there is an error, don't just keep trying to register.
                // Require the user to click a button again, or perform
                // exponential back-off.
            }
            return msg;
        }
		
		 @Override
        protected void onPostExecute(String msg) {
            mDisplay.append(msg + "\n");
        }
    }.execute(null, null, null);

}

private void storeRegistrationId(Context context, String regId) {
    final SharedPreferences prefs = getGCMPreferences(context);
    int appVersion = getAppVersion(context);
    Log.i(TAG, "Saving regId on app version " + appVersion);
    SharedPreferences.Editor editor = prefs.edit();
    editor.putString(PROPERTY_REG_ID, regId);
    editor.putInt(PROPERTY_APP_VERSION, appVersion);
    editor.commit();
}


private void sendRegistrationIdToBackend(String endpoint, Map<String, String> regparams)    throws IOException{
    // Your implementation here.
	 URL url;
        try {
            url = new URL(endpoint);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("invalid url: " + endpoint);
        }
        StringBuilder bodyBuilder = new StringBuilder();
        Iterator<Entry<String, String>> iterator = regparams.entrySet().iterator();
        // constructs the POST body using the parameters
        while (iterator.hasNext()) {
            Entry<String, String> param = iterator.next();
            bodyBuilder.append(param.getKey()).append('=')
                    .append(param.getValue());
					
            if (iterator.hasNext()) {
                bodyBuilder.append('&');
            }
        }
        String body = bodyBuilder.toString();
        Log.v(TAG, "Posting '" + body + "' to " + url);
        byte[] bytes = body.getBytes();
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setFixedLengthStreamingMode(bytes.length);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded;charset=UTF-8");
            // post the request
            OutputStream out = conn.getOutputStream();
            out.write(bytes);
            out.close();
            // handle the response
            int status = conn.getResponseCode();
            if (status != 200) {
              throw new IOException("Post failed with error code " + status);
            }
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
} 
}

