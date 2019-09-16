package com.martindynamics.py4j.test;

public interface ImageProcessorListener {
	
	public int processImageTest();
	
	public int processImage();
	
	public int processImage2(int w, int h, int c, byte[] data);
	
	public String getLabel(int id);

	public float getScore(int id);
	
	public byte[] getObjectBytes(int id);
	
	public byte[] getMaskBytes(int id);
	
	public byte[] getData();
	
}
