package com.martindynamics.py4j.test;

import java.util.ArrayList;
import java.util.List;

import py4j.ClientServer;

public class ImageProcessor {
	
	ClientServer server;
	ImageProcessorListener processor;
	
	public ImageProcessor(){
		 server = new ClientServer(null);
	     // We get an entry point from the Python side
	     processor = (ImageProcessorListener) server.getPythonServerEntryPoint(new Class[] { ImageProcessorListener.class });
	}
	
	public void close() {
		if(server!=null)
			server.shutdown();
	}
	
	public List<ImageObject> processImage(byte[][][] img) {
		int w = img[0].length;
		int h = img.length;
		ArrayList<ImageObject> objects = new ArrayList<>();
		ImageObject object;
		try {
			int objectCount = processor.processImage(w, h, 3, flatten(img));
			for (int o = 0; o < objectCount; o++) {
				object = new ImageObject(w, h, processor.getScore(o), processor.getLabel(o), 
						processor.getMaskBytes(o), processor.getObjectBytes(o));
				objects.add(object);
			}

			return objects;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
		
	//image needs to be flattened into a 1d array such that the color is the fastest changing index, followed by x, and finally y.
	//That is, a nested array would be indexed as [y][x][c]
	public static byte[] flatten(byte[][][] img) {
		int[] shape = new int[] {img.length, img[0].length, img[0][0].length};
		byte[] out = new byte[shape[0]*shape[1]*3];
		for (int y = 0, index = 0; y < shape[0]; y++) {
			for (int x = 0; x < shape[1]; x++) {
				out[index] = img[y][x][0];
				out[index+1] = img[y][x][1];
				out[index+2] = img[y][x][2];
				index += 3;
			}
		}
		return out;
	}

}
