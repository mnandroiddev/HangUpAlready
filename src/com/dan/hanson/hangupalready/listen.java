package com.dan.hanson.hangupalready;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.widget.Toast;

public class listen extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context
				.getApplicationContext());

		boolean checkBoxValue = sharedPreferences.getBoolean("check", false);
		boolean warning = sharedPreferences.getBoolean("warning", true);

		TelephonyManager manager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);

		switch (manager.getCallState()) {

		case TelephonyManager.CALL_STATE_OFFHOOK:
			// if application is activated launch when phone is off-hook and
			// warn if appropriate

			if (checkBoxValue) {
				if (warning) {
					Toast.makeText(context, context.getString(R.string.warning), Toast.LENGTH_SHORT)
							.show();
				}
				context.startService(new Intent(context,
						com.dan.hanson.hangupalready.accelService.class)
						.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
			}
			break;

		case TelephonyManager.CALL_STATE_IDLE:
			// if user hangs up kill the process

			android.os.Process.killProcess(android.os.Process.myPid());
			break;
		}
	}

}
