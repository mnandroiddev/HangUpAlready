package com.dan.hanson.hangupalready;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;

public class EasyPreferenceActivity extends PreferenceActivity {

	Context context;

	/**
	 * create preference activity based on API level
	 */
	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		if (Build.VERSION.SDK_INT >= 11) {
			getFragmentManager().beginTransaction()
					.replace(android.R.id.content, new easyPreferenceFragment()).commit();

		} else {
			addPreferencesFromResource(R.layout.preferences);

		}
	}

	@SuppressLint("NewApi")
	public static class easyPreferenceFragment extends PreferenceFragment {
		@Override
		public void onCreate(final Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.layout.preferences);
		}
	}
}