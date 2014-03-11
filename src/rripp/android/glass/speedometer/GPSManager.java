/**
 * Copyright (C) 2014 Roman Ripp
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package rripp.android.glass.speedometer;

import java.util.List;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

public class GPSManager {
	
	private final String TAG = GPSManager.class.getSimpleName();
	private static final long gpsMinTime = ConstantValues.GPS_MIN_UPDATE_TIME;
    private static final int gpsMinDistance = 0;
     
    private static LocationManager locationManager = null;
    private static LocationListener locationListener = null;
    private static GPSCallback gpsCallback = null;
     
    public GPSManager(){ 
    	GPSManager.locationListener = new LocationListener(){
    			@Override
                public void onLocationChanged(final Location location){
    				if (GPSManager.gpsCallback != null){
    					GPSManager.gpsCallback.onGPSUpdate(location);
    				}
    			}
                     
    			@Override
    			public void onProviderDisabled(final String provider){
    			}
                     
    			@Override
    			public void onProviderEnabled(final String provider){
    				Log.d(TAG, "Location enabled");
    			}
                     
    			@Override
    			public void onStatusChanged(final String provider, final int status, final Bundle extras){
    				Log.d(TAG, "Location disabled");
    			}
            };
    }
     
    public GPSCallback getGPSCallback(){
    	return GPSManager.gpsCallback;
    }
     
    public void setGPSCallback(final GPSCallback gpsCallback){
    	GPSManager.gpsCallback = gpsCallback;
    }
     
    public void startListening(Context context){
    	if (GPSManager.locationManager == null){
    			GPSManager.locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    	}
             
    	final Criteria criteria = new Criteria();
             
    	criteria.setAccuracy(Criteria.ACCURACY_FINE);
    	criteria.setSpeedRequired(true);
            //criteria.setAltitudeRequired(false);
            //criteria.setBearingRequired(false);
            //criteria.setCostAllowed(true);
            //criteria.setPowerRequirement(Criteria.POWER_LOW);
             
    	final List<String> providers = GPSManager.locationManager.getProviders(criteria, true);
    	Log.d(TAG, "Providers: " + providers);

    	for (final String provider : providers){
    		GPSManager.locationManager.requestLocationUpdates(provider, GPSManager.gpsMinTime,
    				GPSManager.gpsMinDistance, GPSManager.locationListener);
    	}
    }
     
    public void stopListening(){
    	try{
    			if (GPSManager.locationManager != null && GPSManager.locationListener != null){
                	GPSManager.locationManager.removeUpdates(GPSManager.locationListener);
    			}
                     
    			GPSManager.locationManager = null;
            }
            catch (final Exception ex){
                     
            }
    }
}
