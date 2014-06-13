package com.dan.hanson.hangupalready;

import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.View;

public class GraphView extends View {
	// TODO:graph styling as well as labeling
	private int height;
	private int width;
	private Float max = Float.valueOf(0);
	private Float avg = Float.valueOf(0);

	private ArrayList<Float> theData = new ArrayList<Float>();
	private ArrayList<Float> displayData = new ArrayList<Float>();
	private Context context;
	private Float shakeWeight = Float.valueOf(0);
	private Float shakeWeightDisplay = Float.valueOf(0);
	private Float displayMaximum;
	protected boolean isDrawn = false;
	private Path path;
	private Float maximum;

	private Paint shakeWeightPaint = new Paint();
	private Paint gridLinePaint = new Paint();
	private Paint dataPaint = new Paint();
	private Paint gridEdgePaint = new Paint();
	private Paint backgroundPaint = new Paint();
	private Paint textPaint = new Paint();

	// need to play around here
	public GraphView(Context context, AttributeSet attrs) {
		super(context, attrs);

		initPaint();
		setPaintStyle(Style.STROKE);
		this.setMinimumHeight(200);
		this.setMinimumWidth(200);
		this.setBackgroundColor(Color.BLACK);
		this.context = context;
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		shakeWeight = Float.valueOf(sharedPreferences.getString("sensitivity", "3000"));

	}

	protected GraphView(Context context2, int minWidth, ArrayList<Float> data, int height) {
		super(context2);
		this.height = height;
		this.theData = data;

		initPaint();
		setPaintStyle(Style.STROKE);
		this.setMinimumHeight(200);
		this.setMinimumWidth(minWidth);
		this.setBackgroundColor(backgroundPaint.getColor());
		this.context = context2;
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		shakeWeight = Float.valueOf(sharedPreferences.getString("sensitivity", "3000"));
		isDrawn = false;

	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		height = this.getHeight();
		width = this.getWidth();
		canvas.drawColor(backgroundPaint.getColor());

		if (!isDrawn) {
			updateStats();
			updateDisplayData();
		}

		graph(canvas);

	}

	private void graph(Canvas canvas) {

		if (!isDrawn) {
			path = new Path();
			path.moveTo(0, height);
			// build path of data points
			for (int i = 0; i < displayData.size(); i++) {
				path.lineTo(i * 5, height - displayData.get(i));
			}
		}

		canvas.drawPath(path, dataPaint);

		// scale the horizontal grid lines depending on the maximum value
		int divisor = calcDivisor();

		// draw horizontal grid lines
		for (int i = 0; i <= displayMaximum / divisor; i++) {

			Float scaledHeight = height * ((i * divisor) / displayMaximum);
			canvas.drawLine(0, height - scaledHeight, width, height - scaledHeight, gridLinePaint);

			canvas.drawText("" + i * divisor, 5, height - scaledHeight - 5, textPaint);
		}

		// draw vertical grid lines
		// int time = 30;
		// for (int i = 0; i < time; i++) {

		// canvas.drawLine(i * width / time, 0, i * width / time, height - 1,
		// gridLinePaint);
		// }
		// draw edge lines
		canvas.drawLine(0, 0, 0, height, gridEdgePaint);
		canvas.drawLine(0, height - 1, width, height - 1, gridEdgePaint);
		// draw shakeWeight
		canvas.drawLine(0, height - shakeWeightDisplay, width, height - shakeWeightDisplay,
				shakeWeightPaint);

		isDrawn = true;
	}

	private int calcDivisor() {
		int toReturn = 50;
		if (displayMaximum <= 500)
			toReturn = 50;
		if (displayMaximum > 500)
			toReturn = 100;
		if (displayMaximum >= 1000)
			toReturn = 250;
		if (displayMaximum >= 2500)
			toReturn = 500;
		if (displayMaximum >= 5000)
			toReturn = 1000;
		return toReturn;
	}

	protected void updateStats() {
		max = Float.valueOf(0);
		avg = Float.valueOf(0);
		Float temp = Float.valueOf(0);
		Float total = Float.valueOf(0);
		for (int i = 0; i < theData.size(); i++) {
			temp = theData.get(i);

			max = (temp > max) ? temp : max;
			total += temp;
		}
		avg = (theData.size() > 0) ? (total / theData.size()) : 0;

	}

	protected void updateDisplayData() {
		maximum = (shakeWeight > max) ? shakeWeight : max;
		displayMaximum = maximum;
		displayMaximum += displayMaximum / 6;

		displayMaximum += 100 - (displayMaximum % 100);// round up to nearest
														// hundred

		shakeWeightDisplay = (shakeWeight / displayMaximum) * height;

		displayData.clear();

		for (Float temp : theData) {
			temp = (temp / displayMaximum) * height;
			displayData.add(temp);
		}

	}

	protected void setColors(int backgroundColor, int shakeWeightColor, int gridLineColor,
			int dataColor, int gridEdgeColor) {
		shakeWeightPaint.setColor(shakeWeightColor);
		gridLinePaint.setColor(gridLineColor);
		dataPaint.setColor(dataColor);
		gridEdgePaint.setColor(gridEdgeColor);
		backgroundPaint.setColor(backgroundColor);
	}

	private void initPaint() {
		shakeWeightPaint.setColor(0xffff4444);
		gridLinePaint.setColor(Color.LTGRAY);
		dataPaint.setColor(Color.CYAN);
		dataPaint.setStyle(Style.STROKE);
		gridEdgePaint.setColor(Color.WHITE);
		backgroundPaint.setColor(Color.BLACK);
		textPaint.setColor(Color.WHITE);
		textPaint.setTextAlign(Align.LEFT);
	}

	protected Float getShakeWeight() {
		return shakeWeight;
	}

	public Float getMax() {
		return max;
	}

	public Float getAvg() {
		return avg;
	}

	public ArrayList<Float> getTheData() {
		return this.theData;
	}

	void setPaintStyle(Paint.Style style) {
		dataPaint.setStyle(style);
	}

	public void setTheData(ArrayList<Float> data) {

		this.theData = data;

	}

	protected int getShakeWeightColor() {
		return this.shakeWeightPaint.getColor();
	}

}
