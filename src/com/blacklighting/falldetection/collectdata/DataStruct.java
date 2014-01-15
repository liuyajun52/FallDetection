/**
 * 
 */
package com.blacklighting.falldetection.collectdata;

/**
 * @author liuyajun
 * 
 */
public class DataStruct {
	long timestamp;
	float[] accValue;
	float[] gValue;

	public DataStruct(long timestamp, float[] accValue, float[] gValue) {
		this.timestamp = timestamp;
		this.accValue = accValue;
		this.gValue = gValue;
	}
}
