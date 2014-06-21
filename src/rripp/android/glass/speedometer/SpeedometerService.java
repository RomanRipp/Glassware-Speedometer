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

import rripp.android.glass.speedometer.ui.SpeedometerRenderer;
import rripp.android.glass.speedometer.ui.SpeedometerView;

import com.google.android.glass.timeline.LiveCard;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class SpeedometerService extends Service implements GPSCallback{

	private final String TAG = SpeedometerService.class.getSimpleName();
    private SpeedometerRenderer mRenderer;
    private static final String LIVE_CARD_TAG = "Spedometer";
    //private TimelineManager mTimelineManager;
    private GPSManager mGPSManager; 
    private LiveCard mLiveCard;

    /**this passes speed values to the view drawer,
     * here {currentSpeed, maxSpeed, averageSpeed}
    **/
    private int[] mSpeed = {0, 0, 0};
    
    /**
     * Binder giving access to the underlying {@code Timer}.
     */
    public class SpeedometerBinder extends Binder {
        public SpeedometerView getView() {
            return mRenderer.getSpeedometerView();
        }
        public SpeedometerService getService(){
        	return SpeedometerService.this;
        }
    }

    private final SpeedometerBinder mBinder = new SpeedometerBinder();
    
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG,"Service created");
        //mTimelineManager = TimelineManager.from(this);
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
    	Log.d(TAG,"Setvice started"); 
    	//Set up the location manager
    	if (mGPSManager == null){
    		mGPSManager = new GPSManager();
    	}
    	mGPSManager.startListening(this);
    	mGPSManager.setGPSCallback(this);
    	//Start graphics renderer
    	if (mRenderer == null){
    		mRenderer = new SpeedometerRenderer(this);
    	}
    	//Publish the Speedometer gauge on a time line
    	if (mLiveCard == null) {
    		Context context = getApplicationContext();
    		//mTimelineManager = TimelineManager.from(context);
            //mLiveCard = mTimelineManager.createLiveCard(LIVE_CARD_TAG);
    		mLiveCard = new LiveCard(context, LIVE_CARD_TAG);
            mLiveCard.setDirectRenderingEnabled(true)
            		.getSurfaceHolder().addCallback(mRenderer);
            //On click open menu 
            intent = new Intent(context, MenuActivity.class);
            mLiveCard.setAction(PendingIntent.getActivity(context, 0,
                    intent, 0));
            mLiveCard.publish(LiveCard.PublishMode.REVEAL);
        }
    	//Create imersion
        return START_STICKY;
    }

    /**
     * Listen to location services and pass the value to renderer
     */
	@Override
	public void onGPSUpdate(Location location) {
		computeSpeed(location.getSpeed());
	}
    
	/**
	 * pass the speed value from available location sensor
	 * @param currentSpeed
	 */
	private void computeSpeed(float currentSpeed) {
		//Test
		//currentSpeed = conunt++;
		// km/h mph conversion
		if (mRenderer.getSpeedometerView().siMetrics()){
			mSpeed[0] = (int) (3.6f * currentSpeed);
		}else{
			mSpeed[0] = (int) (2.23f * currentSpeed);
		}
		
		Log.d(TAG, "Speed: "+currentSpeed);
   		if (mSpeed[0] > mSpeed[1]){
   			mSpeed[1] = mSpeed[0];
   		}
   		//mSpeed[2] = (mSpeed[2] + mSpeed[0]) / conunt;
		mRenderer.getSpeedometerView().setSpeed(mSpeed);
	}
	
	public void resetSpeed(){
		mSpeed[1] = 0;
		mSpeed[2] = 0;
	}
	
    @Override
    public void onDestroy() {
        if (mLiveCard != null && mLiveCard.isPublished()) {
            mLiveCard.getSurfaceHolder().removeCallback(mRenderer);
            mLiveCard.unpublish();
            mLiveCard = null;
        }
        if (mGPSManager != null){
        	mGPSManager.stopListening();
        	mGPSManager.setGPSCallback(null);
        	mGPSManager = null;
        }
        Log.d(TAG,"Service destroyed");
    	super.onDestroy();
    }
	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}
}