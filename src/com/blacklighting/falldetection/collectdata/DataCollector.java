/**
 * 
 */
package com.blacklighting.falldetection.collectdata;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;

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
	private float[] orientation=new float[9]; // 手机相对于绝对坐标系的实时角度
	private float[] gValues;
	private Display display;
	private SensorManager mSensorManager;
	private Sensor mLinearSensor;
	private Sensor mASensor;
	private Sensor mMagneticFieldSensor;
	private Sensor mGsensor;
	private DataArray datas; // 存储一顿时间之内的绝对坐标系内的加速度和旋转角度

	public static final int DATALENGTH = 300; // 数据结构链表的长度
	float zThreshold = -20.0f; // z轴加速的的阈值
	int counter = 0; // 发现超出阈值之后计数的计数器
	boolean isCounting = false; // 是否开始计数
	Context context;
	public DataCollector(Context context) {
		this.context=context;
		mSensorManager = (SensorManager) context
				.getSystemService(Context.SENSOR_SERVICE);
		mLinearSensor = mSensorManager
				.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
		mASensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mMagneticFieldSensor = mSensorManager
				.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		mGsensor=mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
		datas = new DataArray(DATALENGTH);
		display=((WindowManager)(context.getSystemService(Context.WINDOW_SERVICE))).getDefaultDisplay();
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
		// TODO Auto-generated method stub

	}

	public void onFirstWarming(){
		
	}
	
	@Override
	public void onSensorChanged(SensorEvent arg0) {

		float tempx, tempy, tempz, zw, xw, yw;
//		Log.v("DataCollector",""+arg0.values[0]+"\t"+arg0.values[1]+"\t"+arg0.values[2]+"\t");
		if (arg0.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
			linearAccelerometerValues = arg0.values;

			if (accelerometerValues == null || magneticFieldValues == null
					|| linearAccelerometerValues == null) {
				return;
			}

			// 坐标变换

			zw = orientation[0];
			xw = orientation[1];
			yw = orientation[2];

			// z轴变换
			tempx = linearAccelerometerValues[0];
			tempy = linearAccelerometerValues[1];
			tempz = linearAccelerometerValues[2];
			linearAccelerometerValues[0] = (float) (tempx * Math.cos(zw) - tempy
					* Math.sin(zw));
			linearAccelerometerValues[1] = (float) (tempx * Math.sin(zw) + tempy
					* Math.cos(zw));

			tempx = linearAccelerometerValues[0];
			tempy = linearAccelerometerValues[1];
			tempz = linearAccelerometerValues[2];
			// x轴变换
			linearAccelerometerValues[1] = (float) (tempy * Math.cos(xw) - tempz
					* Math.sin(xw));
			linearAccelerometerValues[2] = (float) (tempy * Math.sin(xw) + tempz
					* Math.cos(xw));

			tempx = linearAccelerometerValues[0];
			tempy = linearAccelerometerValues[1];
			tempz = linearAccelerometerValues[2];
			// y轴变换
			linearAccelerometerValues[2] = (float) (tempz * Math.cos(yw) - tempx
					* Math.sin(yw));
			linearAccelerometerValues[0] = (float) (tempz * Math.sin(yw) + tempx
					* Math.cos(yw));
			
			//将数据添加到数据链表中
			datas.addData(new DataStruct(arg0.timestamp,
					linearAccelerometerValues,gValues));

			if (linearAccelerometerValues[2] < zThreshold
					&& !isCounting) {
				isCounting = true;		//当发现有数据超过阈值时开始计数
			}
			if (isCounting && ++counter == DATALENGTH/2) {
				isCounting = false;
				
				onFirstWarming();
				
//				Log.v("warmingliuyajun", "一次警告");
//				Toast.makeText(context.getApplicationContext(), "警告！！！！！！！！！！", Toast.LENGTH_LONG).show();
//				
//				Intent i=new Intent(context,WarmingActivity.class);
//				context.startActivity(i);
				
//				Builder b=new Builder(context.getApplicationContext());
//				b.setTitle("警告");
//				b.setMessage(""+linearAccelerometerValues[2]);
//				b.setPositiveButton("确定", null);
//				b.create().show();
				
				
				//数据收集满了之后，将数据提交到数据分析模块
				datas=new DataArray(DATALENGTH);		//清空数据
				counter=0;
			}

		} else {
			if (arg0.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
				magneticFieldValues = arg0.values;
			} else if (arg0.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
				accelerometerValues = arg0.values;
			} else if(arg0.sensor.getType()==Sensor.TYPE_GRAVITY){
				switch(display.getRotation()){
				case Surface.ROTATION_0:
					gValues=arg0.values;
					break;
				case Surface.ROTATION_90:
					gValues[0]=arg0.values[1];
					gValues[1]=-arg0.values[0];
					gValues[2]=arg0.values[2];
					break;
				case Surface.ROTATION_180:
					gValues[0]=-arg0.values[0];
					gValues[1]=-arg0.values[1];
					gValues[2]=arg0.values[2];
					break;
				case Surface.ROTATION_270:
					gValues[0]=-arg0.values[1];
					gValues[1]=arg0.values[0];
					gValues[2]=arg0.values[2];
					break;
				}
			}

			if (accelerometerValues == null || magneticFieldValues == null
					|| linearAccelerometerValues == null) {
				return;
			}

			float[] R = new float[9];
			SensorManager.getRotationMatrix(R, null, accelerometerValues,
					magneticFieldValues);
			SensorManager.getOrientation(R, orientation);

			// if (accelerometerValues[2] < 0) {
			// rawOra[1] = (float) (Math.PI - rawOra[1]);
			// }

		}

	}

}
