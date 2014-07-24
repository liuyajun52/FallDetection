/**
 * 
 */
package com.blacklighting.falldetection.dataanalysis;

import com.blacklighting.falldetection.collectdata.DataArray;

/**
 * 数据分析模块
 * 
 * @author liuyajun
 * 
 */
public class DataAnalysiser {
	final int AVERAGESIZE = 10;
	final int STILLSIZE = 20;
	final Double TRANSFORM_TO_SECOND = Double.valueOf(1000000000);
	final double ROTATION_THRESHHOLE = Math.PI / 4; // 旋转角度的阈值
	final double VARIANCE_THRESHHOLE = 0.8; // 静止方差阈值
	final double ZD_THRESHHOLE = 2; // 竖直位移最小量的阈值
	private DataArray dataArray;

	public DataAnalysiser(DataArray dataArray) {
		this.dataArray = dataArray;
	}

	/**
	 * @return 分析结果
	 */
	public boolean analysis() {
		return firstAnalysis() && secondAnalysis() &&thirdAnaylsis();
	}

	/**
	 * 一层检验，计算嫌疑事件开始到结束设备在竖直方向的夹角
	 * 
	 * @return
	 */
	private boolean firstAnalysis() {
		float[] beginGValues = { 0.0f, 0.0f, 0.0f };
		float[] endGValues = { 0.0f, 0.0f, 0.0f };

		// 计算嫌疑跌倒时间重力数据前若干组和后若干组的平均值

		for (int i = 0; i < AVERAGESIZE; i++) {
			beginGValues[0] += dataArray.get(i).getGValueComponent(0);
			beginGValues[1] += dataArray.get(i).getGValueComponent(1);
			beginGValues[2] += dataArray.get(i).getGValueComponent(2);
		}

		for (int i = dataArray.size() - 1; i >= dataArray.size() - AVERAGESIZE; i--) {
			endGValues[0] += dataArray.get(i).getGValueComponent(0);
			endGValues[1] += dataArray.get(i).getGValueComponent(1);
			endGValues[2] += dataArray.get(i).getGValueComponent(2);
		}

		// 计算平均向量的夹角的余弦

		double cosine = (beginGValues[0] * endGValues[0] + beginGValues[1]
				* endGValues[1] + beginGValues[2] * endGValues[2])
				/ Math.sqrt((beginGValues[0] * beginGValues[0]
						+ beginGValues[1] * beginGValues[1] + beginGValues[2]
						* beginGValues[2])
						* (endGValues[0] * endGValues[0] + endGValues[1]
								* endGValues[1] + endGValues[2] * endGValues[2]));

		double angleOfRotation = Math.acos(cosine);

		return  angleOfRotation> ROTATION_THRESHHOLE;
	}

	/**
	 * 二层检测，测试嫌疑时间结束时各方向加速的的若干个数据的方差
	 * 
	 * @return
	 */
	private boolean secondAnalysis() {

		float[] endAccValuesRrv = { 0.0f, 0.0f, 0.0f };
		float[] endAccValuesVariance = { 0.0f, 0.0f, 0.0f };

		for (int i = dataArray.size() - 1; i >= dataArray.size() - STILLSIZE; i--) {
			endAccValuesRrv[0] += dataArray.get(i).getAccValueComponent(0);
			endAccValuesRrv[1] += dataArray.get(i).getAccValueComponent(1);
			endAccValuesRrv[2] += dataArray.get(i).getAccValueComponent(2);
		}

		endAccValuesRrv[0] /= STILLSIZE;
		endAccValuesRrv[1] /= STILLSIZE;
		endAccValuesRrv[2] /= STILLSIZE;

		for (int i = dataArray.size() - 1; i >= dataArray.size() - STILLSIZE; i--) {
			endAccValuesVariance[0] += (dataArray.get(i)
					.getAccValueComponent(0) - endAccValuesRrv[0])
					* (dataArray.get(i).getAccValueComponent(0) - endAccValuesRrv[0]);
			endAccValuesVariance[1] += (dataArray.get(i)
					.getAccValueComponent(1) - endAccValuesRrv[1])
					* (dataArray.get(i).getAccValueComponent(1) - endAccValuesRrv[1]);
			endAccValuesVariance[2] += (dataArray.get(i)
					.getAccValueComponent(2) - endAccValuesRrv[2])
					* (dataArray.get(i).getAccValueComponent(2) - endAccValuesRrv[2]);
		}

		endAccValuesVariance[0] /= STILLSIZE;
		endAccValuesVariance[1] /= STILLSIZE;
		endAccValuesVariance[2] /= STILLSIZE;

		 return (endAccValuesVariance[0] < VARIANCE_THRESHHOLE)
		 && (endAccValuesVariance[1] < VARIANCE_THRESHHOLE)
		 && (endAccValuesVariance[2] < VARIANCE_THRESHHOLE);
//		return endAccValuesVariance[2] < VARIANCE_THRESHHOLE;

	}

	/**
	 * 三层判断，计算嫌疑事件的竖直位移最小值
	 * 
	 * @return
	 */
	private boolean thirdAnaylsis() {
//		Double minZDisplacement = 0.0;
//		Double tempvPre = 0.0, tempvNow = 0.0;
//		for (int i = 0; i < dataArray.size() - 1; i++) {
//			Double timeNow = Double.valueOf( dataArray.get(i + 1).getTime());
//			Double timePre = Double.valueOf(dataArray.get(i).getTime());
//			Double timeSt=timeNow-timePre;
//			Double time = timeSt/ TRANSFORM_TO_SECOND;
//			tempvNow = tempvPre
//					+ 0.5
//					* (dataArray.get(i).getAccValueComponent(2) + dataArray
//							.get(i + 1).getAccValueComponent(2)) * time;
//			minZDisplacement += (0.5 * (tempvPre + tempvNow) * time);
//			tempvPre = tempvNow;
//		}
//
//		return minZDisplacement > ZD_THRESHHOLE;
		return true;
	}

}
