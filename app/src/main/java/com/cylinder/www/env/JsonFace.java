package com.cylinder.www.env;


import java.util.Date;

public class JsonFace {
	

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public int getLength() {
		return length;
	}
	public void setLength(int length) {
		this.length = length;
	}
	public byte[] getData() {
		return data;
	}
	public void setData(byte[] data) {
		this.data = data;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public String getDonorId() {
		return donorId;
	}
	public void setDonorId(String donorId) {
		this.donorId = donorId;
	}
	public String getMachineId() {
		return machineId;
	}
	public void setMachineId(String machineId) {
		this.machineId = machineId;
	}
	public boolean isAutomaticRecognition() {
		return automaticRecognition;
	}
	public void setAutomaticRecognition(boolean automaticRecognition) {
		this.automaticRecognition = automaticRecognition;
	}
	public int getRcognitionResult() {
		return rcognitionResult;
	}
	public void setRcognitionResult(int rcognitionResult) {
		this.rcognitionResult = rcognitionResult;
	}
	private String id;
    private int length;
    private byte[] data;
    private Date createTime;
    private String donorId;
    private String machineId;
    private boolean automaticRecognition;
    private int rcognitionResult;

}
