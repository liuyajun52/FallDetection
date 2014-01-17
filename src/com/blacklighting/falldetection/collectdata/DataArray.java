/**
 * 
 */
package com.blacklighting.falldetection.collectdata;

import java.util.ArrayList;
import java.util.List;

/**数据链表
 * @author liuyajun
 *
 */
public class DataArray {
	
	private int maxSize=600;
	private List<DataStruct> dataArray;
	
	public DataArray(){
		dataArray=new ArrayList<DataStruct>();
	}
	
	public DataArray(int maxSize){
		this();
		this.maxSize=maxSize;
	}
	
	public void addData(DataStruct  data){
		if(dataArray.size()==maxSize){
			dataArray.remove(0);
		}
		dataArray.add(data);
	}


	
	public int getMaxSize() {
		return maxSize;
	}

	public void setMaxSize(int maxSize) {
		this.maxSize = maxSize;
	}

	public List<DataStruct> getDataArray() {
		return dataArray;
	}

	public DataStruct get(int index){
		return dataArray.get(index);
	}
	
	public int size(){
		return dataArray.size();
	}
	

}
