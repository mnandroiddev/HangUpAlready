package com.dan.hanson.hangupalready;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.TextView;

public class GraphActivity extends Activity {

	private boolean capturing = false;

	private Button btn;
	private TextView average;
	private TextView max;
	private TextView shakeWeightText;
	private GraphView graph;
	protected int width;
	protected int height;
	private SensorManager manager;
	private Sensor sensor;
	private ShakeListener shakeListener;
	private HorizontalScrollView scroll;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// hide title bar for maximum graph space
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.new_layout);

		lockDisplay();
		getMembersFromXml();

		manager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		sensor = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		shakeListener = new ShakeListener(this, true, graph.getShakeWeight());
	}

	/**
	 * instantiate the member views and set up any data associated with them
	 */
	private void getMembersFromXml() {
		btn = (Button) findViewById(R.id.btn);
		btn.setOnClickListener(new myClickListener());

		graph = (GraphView) findViewById(R.id.graph);
		scroll = (HorizontalScrollView) findViewById(R.id.scroll);

		average = (TextView) findViewById(R.id.average);
		max = (TextView) findViewById(R.id.max);
		shakeWeightText = (TextView) findViewById(R.id.shake);
		shakeWeightText.setTextColor(graph.getShakeWeightColor());
		shakeWeightText.setText("Sensitivity: " + graph.getShakeWeight());

		this.updateRawData();
		graph.setMinimumWidth(this.width);
	}

	/**
	 * gets the display's width and height, though height is probably never used
	 */
	@SuppressWarnings("deprecation")
	private void updateRawData() {

		Display display = getWindowManager().getDefaultDisplay();

		if (Build.VERSION.SDK_INT >= 13) {
			Point size = new Point();
			display.getSize(size);
			width = size.x;
			height = size.y;

		} else {
			width = display.getWidth();
			height = display.getHeight();

		}

	}

	/**
	 * lock to current orientation, preventing orientation based activity
	 * restarts which would clear acceleration data/cause view redrawing does
	 * not prevent equal-ratio orientation changes (e.g. landscape to opposite facing
	 * landscape)
	 */

	private void lockDisplay() {
		int orientation = getResources().getConfiguration().orientation;
		if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
		} else {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
		}

	}

	/**
	 * reset everything when user navigates away
	 */
	@Override
	protected void onPause() {
		super.onPause();
		manager.unregisterListener(shakeListener);
		graph.isDrawn = false;
		capturing = false;
		btn.setText(R.string.start_graph);
	}
	
	

	private class myClickListener implements OnClickListener {
		/**
		 * 
		 * switches between capturing acceleration data and graphing said data;
		 * 
		 * updates the data of graph;
		 * 
		 * updates text fields with new data;
		 * 
		 * clears all views from scroll and a newly created graphView is added
		 * to scroll with the same minWidth and data;
		 * 
		 */
		@Override
		public void onClick(View view) {

			// stop capturing, make a new graph and redraw everything
			if (capturing) {
				capturing = false;
				manager.unregisterListener(shakeListener);
				graph.isDrawn = false;
				btn.setText(R.string.start_graph);

				// 6=5+1 to show the end of data was reached and give a buffer
				int minWidth = 6 * shakeListener.getData().size();

				if (minWidth < width) {
					minWidth = (int) Math.round(width);
				}

				// create new graph with new dimensions and data
				graph = new GraphView(view.getContext(), minWidth, shakeListener.getData(),
						graph.getHeight());
				graph.setId(R.id.graph);

				// add new graph to scroll view
				scroll.setMinimumWidth(minWidth);
				scroll.removeAllViews();
				scroll.addView(graph);

				// prepare graph for drawing
				graph.updateStats();
				graph.updateDisplayData();

				average.setText("Average: " + graph.getAvg());
				max.setText("Maximum: " + graph.getMax());

				graph.invalidate();

			} else {

				capturing = true;
				shakeListener.clearData();

				manager.registerListener(shakeListener, sensor, SensorManager.SENSOR_DELAY_GAME);
				btn.setText(R.string.stop_graph);
			}

		}
	}

}
