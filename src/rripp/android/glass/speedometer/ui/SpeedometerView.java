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

package rripp.android.glass.speedometer.ui;

import rripp.android.glass.speedometer.ConstantValues;
import rripp.android.glass.speedometer.R;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.View;

/**
 * View used to draw the level line.
 */
public class SpeedometerView extends View {

    private final Paint mTextPaint;
    private final Paint mDrawPaint;
    private final Path mPath;
    private int[] mSpeed;
    private boolean mUseSiMetrics;
    private String mUnits;
    private String mSpeedText;
    private String mMaxSpeedText;
    private String mMeanSpeedText;

    /**
     * Constructor
     * @param context
     */
    public SpeedometerView(Context context) {
        this(context, null, 0);
    }

    public SpeedometerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SpeedometerView(Context context, AttributeSet attrs, int style) {
        super(context, attrs, style);
        
        mSpeed = new int[3];
        mSpeedText = "";
        mMaxSpeedText = "";
        mMeanSpeedText = context.getString(R.string.noGPS);
        mUseSiMetrics = true;
        mUnits = context.getString(R.string.unit_kmh);
        
        //Set up the paint for text labels
        mTextPaint = new Paint();
        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setStrokeWidth(5);

        //Set up the paint for drawings
        int[] colors = {Color.GREEN, Color.YELLOW, Color.RED};
        float[] positions = {0.5f, 0.90f, 1.0f};
        SweepGradient gradient = new SweepGradient(200, 330, colors, positions);
        mDrawPaint = new Paint();
        mDrawPaint.setShader(gradient);
        mDrawPaint.setStyle(Paint.Style.STROKE);
        mDrawPaint.setStrokeWidth(50);
        mPath = new Path();
    }

    /**
     * Set the acceleration values.
     *
     * @param array of acceleration values X,Y,Z.
     */
    public void setSpeed(int[] speed) {
    	mSpeed = speed;
    	mSpeedText = convert(mSpeed[0]) + " " + mUnits;
    	mMaxSpeedText = "Max: "+convert(mSpeed[1]) + " " + mUnits;
    	mMeanSpeedText = "";//"Average: "+convert(mSpeed[2]) + " " + mUnits;
        // Redraw the line.
        invalidate();
    }

    public int[] getSpeed() {
        return mSpeed;
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = canvas.getWidth();
        int height = canvas.getHeight();
        
        drawArc(canvas, height, width);
        drawLabels(canvas, height, width);
    }
    
    /**
     * Draw labels, such as right, left, break, accelerations
     * @param canvas
     * @param height
     * @param width
     */
    private void drawLabels(Canvas canvas, int height, int width){
    	
    	int margin = ConstantValues.TEXT_SIZE;
    	mTextPaint.setColor(Color.WHITE);
    	mTextPaint.setTextSize(2 * ConstantValues.TEXT_SIZE);
        canvas.drawText(mSpeedText,
        		width / 2 - mTextPaint.measureText(mSpeedText) / 2,
        		height - margin,
        		mTextPaint);
    	mTextPaint.setTextSize(ConstantValues.TEXT_SIZE);
        canvas.drawText(mMeanSpeedText,
        		width - mTextPaint.measureText(mMeanSpeedText) - margin,
        		margin,
        		mTextPaint);
        mTextPaint.setColor(Color.parseColor("#cc3333"));
        canvas.drawText(mMaxSpeedText,
        		margin,
        		margin,
        		mTextPaint);
    }

    /**
     * Draw moving red point that represents acceleration in X-Y plane  
     * @param canvas
     * @param height
     * @param width
     */
    private void drawArc(Canvas canvas, int height, int width){
    	
        int screenSize = Math.max(height, width);
        //Draw the speedometer arc
        int margin = 2 * ConstantValues.TEXT_SIZE;
        mPath.arcTo(new RectF(margin, 
        		margin, 
        		screenSize - margin, 
        		screenSize - margin), 
        		-180, 
        		scale(mSpeed[0]), 
        		true);
        canvas.drawPath(mPath, mDrawPaint);
        mPath.reset();
        //Log.d(TAG,width+"X"+height);
    }

    /**
     * Round to 1 decimal point value
     * @param value the initial value
     * @return resulting string
     */
    private String convert(int value){
    	return String.valueOf(value);
    }
    
    /**
     * scale the values so they correspond to drawing 
     * @param acc input 
     * @return scaled output
     */
    private int scale(int speed){
    	return Math.min((int) (1.8 * speed), 180);
    }
    
    /**
     * Comes from menu and defines the measurement 
     * units - km/h or mph
     * @param units
     */
    public void useSiMetrics(boolean si, Context context){
    	mUseSiMetrics = si;
    	if (si){
    		mUnits = context.getString(R.string.unit_kmh);
    	}else{
    		mUnits = context.getString(R.string.unit_mph);
    	}
    }
    public boolean siMetrics(){
    	return mUseSiMetrics;
    }
}