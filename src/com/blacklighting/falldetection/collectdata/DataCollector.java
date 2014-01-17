/**
 * 
 */
package com.blacklighting.falldetection.collectdata;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.blacklighting.falldetection.dataanalysis.DataAnalysiser;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Environment;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;
import android.widget.Toast;

/**
 * 传感器数据收集器
 * 
 * @author liuyajun
 * 
 */
public class DataCollector implements SensorEventListener {

	private float[] linearAccelerometerValues; // 线性加速度实时数值
	private float[] accelerometerValues; // 加速度实时数值
	private float[] magneticFieldValues; // 磁力实时数值
	private float[] gValues;
	private float[] RM; // 旋转矩阵
	private Display display;
	private SensorManager mSensorManager;
	private Sensor mLinearSensor;
	private Sensor mASensor;
	private Sensor mMagneticFieldSensor;
	private Sensor mGsensor;
	private DataArray datas; // 存储一段时间之内的绝对坐标系内的加速度和旋转角度

	File tempFile;
	int ij = 0;

	public static final int DATALENGTH = 200; // 数据结构链表的长度
	float zThreshold = 12.0f; // z轴加速的的阈值
	int counter = 0; // 发现超出阈值之后计数的计数器
	boolean isCounting = false; // 是否开始计数
	Context context;

	public DataCollector(Context context) {
		this.context = context;
		mSensorManager = (SensorManager) context
				.getSystemService(Context.SENSOR_SERVICE);
		mLinearSensor = mSensorManager
				.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
		mASensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mMagneticFieldSensor = mSensorManager
				.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		mGsensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
		datas = new DataArray(DATALENGTH);
		display = ((WindowManager) (context
				.getSystemService(Context.WINDOW_SERVICE))).getDefaultDisplay();
	}

	public DataCollector(Context context, float zThreshold) {
		this(context);
		this.zThreshold = zThreshold;
	}

	public void beginCollectData() {
		mSensorManager.registerListener(this, mLinearSensor,
				SensorManager.SENSOR_DELAY_NORMAL);
		mSensorManager.registerListener(this, mMagneticFieldSensor,
				SensorManager.SENSOR_DELAY_NORMAL);
		mSensorManager.registerListener(this, mASensor,
				SensorManager.SENSOR_DELAY_NORMAL);
		mSensorManager.registerListener(this, mGsensor,
				SensorManager.SENSOR_DELAY_NORMAL);

	}

	public void stopCollectData() {
		mSensorManager.unregisterListener(this);
	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {

	}

	public void onFirstWarming() {

	}

	@Override
	public void onSensorChanged(SensorEvent arg0) {

		if(arg0.accuracy==SensorManager.SENSOR_STATUS_UNRELIABLE){
			return;
		}
		
		float tempx, tempy, tempz;
		if (arg0.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
			linearAccelerometerValues = arg0.values.clone();

			if (RM==null|| linearAccelerometerValues == null) {
				return;
			}

			// 用旋转矩阵做坐标变换

			tempx = linearAccelerometerValues[0] * RM[0]
					+ linearAccelerometerValues[1] * RM[3]
					+ linearAccelerometerValues[2] * RM[6];
			tempy = linearAccelerometerValues[0] * RM[1]
					+ linearAccelerometerValues[1] * RM[4]
					+ linearAccelerometerValues[2] * RM[7];
			tempz = linearAccelerometerValues[0] * RM[2]
					+ linearAccelerometerValues[1] * RM[5]
					+ linearAccelerometerValues[2] * RM[8];

			// 将数据添加到数据链表中
			datas.addData(new DataStruct(arg0.timestamp, new float[] { tempx,
					tempy, tempz }, gValues.clone()));

			if (tempz > zThreshold && !isCounting) {
				isCounting = true; // 当发现有数据超过阈值时开始计数
			}
			if (isCounting && ++counter == DATALENGTH / 2) {
				isCounting = false;
				Toast.makeText(context, " 一级触发", Toast.LENGTH_SHORT).show();
//				tempFile = new File(Environment.getExternalStorageDirectory()
//						+ "/" + "temp" +(ij++) + ".csv");
//				FileOutputStream out=null;
//				try {
//					 out = new FileOutputStream(tempFile);
//					for (int i = 0; i < datas.size(); i++) {
//						out.write(("" + datas.get(i).getAccValuei(0) + ","
//								+ datas.get(i).getAccValuei(1) + ","
//								+ datas.get(i).getAccValuei(2) + ","
//								+ datas.get(i).getGValuei(0) + ","
//								+ datas.get(i).getGValuei(1) + ","
//								+ datas.get(i).getGValuei(2) + "\n").getBytes());
//					}
//				} catch (FileNotFoundException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}finally{
//					try {
//						out.flush();
//						out.close();
//					} catch (IOException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//				}

				DataAnalysiser analysiser = new DataAnalysiser(datas);
				boolean analysisResult = analysiser.analysis();
				if (analysisResult) {
					onFirstWarming();
				}
				// 数据收集满了之后，将数据提交到数据分析模块
				datas = new DataArray(DATALENGTH); // 清空数据
				counter = 0;
			}

		} else {
			if (arg0.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
				magneticFieldValues = arg0.values.clone();
			} else if (arg0.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
				accelerometerValues = arg0.values.clone();
			} else if (arg0.sensor.getType() == Sensor.TYPE_GRAVITY) {
				switch (display.getRotation()) {
				case Surface.ROTATION_0:
					gValues = arg0.values.clone();
					break;
				case Surface.ROTATION_90:
					gValues[0] = arg0.values[1];
					gValues[1] = -arg0.values[0];
					gValues[2] = arg0.values[2];
					break;
				case Surface.ROTATION_180:
					gValues[0] = -arg0.values[0];
					gValues[1] = -arg0.values[1];
					gValues[2] = arg0.values[2];
					break;
				case Surface.ROTATION_270:
					gValues[0] = -arg0.values[1];
					gValues[1] = arg0.values[0];
					gValues[2] = arg0.values[2];
					break;
				}
				return;
			}

			if (accelerometerValues == null || magneticFieldValues == null) {
				return;
			}

			float[] R = new float[9];

			if (SensorManager.getRotationMatrix(R, null, accelerometerValues,
					magneticFieldValues)) {
				RM = R;
			}
		}

	}

}
