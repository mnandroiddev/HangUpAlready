package com.dan.hanson.hangupalready;

import java.util.ArrayList;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

/*
 * Class that senses shake events
 * 
 * either returns data for graphing
 * or ends phone call 
 */

class ShakeListener implements SensorEventListener {

	private ArrayList<Float> sensorValues = new ArrayList<Float>();
	private Context context;
	private boolean isGraph;
	private Float shakeLimit;
	private float oldX = 0;

	private float oldY = 0;
	private float oldZ = 0;
	private long oldTime = 0;

	public ShakeListener(Context context, Boolean inGraphMode, Float limit) {
		isGraph = inGraphMode;
		shakeLimit = limit;
		this.context = context;
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		float[] values = event.values;
		float x = Math.abs(values[0]);
		float y = Math.abs(values[1]);
		float z = Math.abs(values[2]);

		long theTime = System.currentTimeMillis();

		if ((theTime - oldTime) > 100) {

			long difference = (theTime - oldTime);
			oldTime = theTime;
			float toCompare = Math.abs((x + y + z) - (oldX + oldY + oldZ)) / difference * 10000;
			// float toCompare = Math.abs(x + y + z - oldX - oldY - oldZ)
			// / difference * 10000;
			sensorValues.add(toCompare);

			oldX = x;
			oldY = y;
			oldZ = z;
			if (!isGraph) {
				if (toCompare > shakeLimit) {

					accelService.disconnectPhoneItelephony(context);

					android.os.Process.killProcess(android.os.Process.myPid());
				}
			}
		}

	}

	protected Float getShakeLimit() {
		return shakeLimit;
	}

	protected ArrayList<Float> getData() {
		return sensorValues;
	}

	protected void clearData() {
		this.sensorValues.clear();
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}
}
