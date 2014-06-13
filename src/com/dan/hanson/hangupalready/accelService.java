package com.dan.hanson.hangupalready;

import java.lang.reflect.Method;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;

import com.android.internal.telephony.ITelephony;

public class accelService extends Service {

	private SensorManager manager;
	private Sensor sensor;
	private ShakeListener shakeListener;
	private float shakeWeight;

	/**
	 * instantiate member objects and register the shakeListener with
	 * user's stored shake limit(or default value)
	 */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		shakeWeight = Float.valueOf(sharedPreferences.getString("sensitivity", "3000"));
		manager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		sensor = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		shakeListener = new ShakeListener(this, false, shakeWeight);
		manager.registerListener(shakeListener, sensor, SensorManager.SENSOR_DELAY_GAME);

		return START_NOT_STICKY;
	}

	/**
	 * invoke hidden endCall method from telephony service
	 */
	public static void disconnectPhoneItelephony(Context context) {
		// this is where the magic happens :D

		ITelephony telephonyService;
		TelephonyManager telephony = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		try {
			Class c = Class.forName(telephony.getClass().getName());
			Method m = c.getDeclaredMethod("getITelephony");
			m.setAccessible(true);
			telephonyService = (ITelephony) m.invoke(telephony);
			telephonyService.endCall();

			// dirty
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

}
