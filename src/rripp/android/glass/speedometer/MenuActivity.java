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

import rripp.android.glass.speedometer.R;
import rripp.android.glass.speedometer.ui.SpeedometerView;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class MenuActivity extends Activity {
	
	private SpeedometerView mView;
	private SpeedometerService mService;
	
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override 
        public void onServiceConnected(ComponentName name, IBinder service) {
            if (service instanceof SpeedometerService.SpeedometerBinder) {
                mView = ((SpeedometerService.SpeedometerBinder) service).getView();
                mService = ((SpeedometerService.SpeedometerBinder) service).getService();
                openOptionsMenu();
            }
            // No need to keep the service bound.
            unbindService(this);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            // Nothing to do here.
        }
    };
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		bindService(new Intent(this, SpeedometerService.class), mConnection, 0);
	}

    @Override
    public void openOptionsMenu() {
    	super.openOptionsMenu();
    }

	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.spedo, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
    	boolean visible = true;
    	menu.findItem(R.id.units).setVisible(visible);
        menu.findItem(R.id.kmh).setVisible(visible);
        menu.findItem(R.id.mph).setVisible(visible);
        menu.findItem(R.id.exit).setVisible(visible);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection.
		//TODO reset max speed than changing units, implement reset option 
        switch (item.getItemId()) {
            case R.id.kmh:
            	mService.resetSpeed();
            	mView.useSiMetrics(true, this);
            	return true;
            case R.id.mph:
            	mService.resetSpeed();
            	mView.useSiMetrics(false, this);
            	return true;
            case R.id.exit:
                stopService(new Intent(this, SpeedometerService.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onOptionsMenuClosed(Menu menu) {
        // Nothing else to do, closing the Activity.
    	finish();
    }
}
