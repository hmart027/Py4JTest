package com.martindynamics.py4j.test;

import java.io.Serializable;

public class ImageObject implements Serializable{

	private static final long serialVersionUID = 7462399897004342831L;

	public int[]       coordinates; // y0,x0,y1,x1
	public float       score;
	public boolean[][] mask;  //y,x
	public String      label;
	
	public ImageObject(int imgW, int imgH, float score, String label, byte[] maskBytes, byte[] objBytes) {
		coordinates = new int[4];
		mask        = new boolean[imgH][imgW];
		this.score  = score;
		this.label  = label;
		
		coordinates[0] = (objBytes[3]&0xFF)<<24  | (objBytes[2]&0xFF)<<16  | (objBytes[1]&0xFF)<<8  | (objBytes[0]&0xFF);
		coordinates[1] = (objBytes[7]&0xFF)<<24  | (objBytes[6]&0xFF)<<16  | (objBytes[5]&0xFF)<<8  | (objBytes[4]&0xFF);
		coordinates[2] = (objBytes[11]&0xFF)<<24 | (objBytes[10]&0xFF)<<16 | (objBytes[9]&0xFF)<<8  | (objBytes[8]&0xFF);
		coordinates[3] = (objBytes[15]&0xFF)<<24 | (objBytes[14]&0xFF)<<16 | (objBytes[13]&0xFF)<<8 | (objBytes[12]&0xFF);
		
		if(maskBytes!=null){
			for(int h=0, index=0; h<imgH; h++) {
				for(int w=0; w<imgW; w++) {
					mask[h][w] = maskBytes[index++]>0;
				}
			}
		}
	}
}
